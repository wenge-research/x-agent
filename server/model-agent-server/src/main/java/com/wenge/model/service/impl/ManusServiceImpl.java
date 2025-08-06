package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.param.ManusParam;
import com.wenge.model.dto.result.ManusResult;
import com.wenge.model.dto.result.ManusTask;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.ManusService;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.SseEmitterUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.PromptWebParam;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.param.YayiPluginSelectionModelParam;
import com.wg.appframe.yayi.result.PromptWebResult;
import com.wg.appframe.yayi.result.RearrangeResult;
import com.wg.appframe.yayi.result.YayiPluginSelectionModelResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service
@Slf4j
public class ManusServiceImpl implements ManusService {


    @Autowired
    @Qualifier("manusPool")
    private ThreadPoolExecutor executor;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private LlmInfoService llmInfoService;

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public Object dialogueByStream(ManusParam param) {
        String clientId = param.getClientId();
        String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        if (StringUtils.isBlank(param.getClientId())) {
            return Result.fail("clientId不能为空");
        }

        // 获取 sse
        SseEmitter emitter = SseEmitterUtils.getConnection(clientId);
        if (StringUtils.isBlank(param.getQuestion())) {
            try {
                ManusResult manusResult = new ManusResult();
                manusResult.setStep("checkParam");
                manusResult.setMsg("请输入问题");
                SseEmitterUtils.send(clientId, JSONUtil.toJsonStr(manusResult));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return emitter;
        }

        // 异步执行
        executor.execute(() -> {
            // 设置当前线程名称
            Thread.currentThread().setName(traceId);
            MDC.put(MDCTraceUtils.KEY_TRACE_ID, traceId);
            try {
                chat(param);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                DialogueServiceImpl.APPLICATION_INFO.remove();
                SseEmitterUtils.completeDelay(param.getClientId());
                MDC.clear();
            }
        });
        return emitter;
    }

    private void chat(ManusParam param) throws IOException {
        List<ManusTask> manusTaskList = problemUnderstanding(param);
        ManusResult result = new ManusResult();
        result.setStep("任务拆解");
        result.setManusTaskList(manusTaskList);
        SseEmitterUtils.send(param.getClientId(), JSONUtil.toJsonStr(result));

        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, YesNoEnum.NO.getName());
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationInfo.getModelId());
        List<YayiMessage> messagesList = Lists.newArrayList();
        if (StringUtils.isNotBlank(applicationInfo.getSystemPrompt())) {
            YayiMessage messages = new YayiMessage();
            messages.setContent(applicationInfo.getSystemPrompt());
            messages.setRole(SYSTEM_PROMPT_FIELD);
            messagesList.add(messages);
        }
        if (CollectionUtil.isNotEmpty(manusTaskList)) {
            String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);

            Vector<PromptWebResult.ResInfoDetail> resInfoAll = new Vector<>();

            manusTaskList.parallelStream().forEach(manusTask -> {
                MDC.put(MDCTraceUtils.KEY_TRACE_ID, traceId);
                Thread.currentThread().setName(manusTask.getTaskId());

                PromptWebParam promptWebParam = new PromptWebParam();
                PromptWebParam.Content content = new PromptWebParam.Content();
                promptWebParam.setContent(content);
                promptWebParam.setId(IdUtil.simpleUUID());
                String networkChannel = applicationInfo.getNetworkChannel();
                if (StringUtils.isBlank(networkChannel)) {
                    networkChannel = "quark";
                }

                content.setWeb_source_list(ListUtil.toList(networkChannel));
                content.setGet_news_num(50);
                content.setTop_k(50);
                content.setData_type("snapshot_and_context");

                PromptWebResult promptWebResult = yayiServer.promptWeb(manusTask.getContent(), promptWebParam);
                PromptWebResult.Data data = promptWebResult.getData();
                // List<NetworkApiResult.Datum> datumList = Lists.newArrayList();
                if (null != data) {
                    List<PromptWebResult.ResInfoDetail> resInfo = data.getResInfo();

                    if (CollectionUtil.isNotEmpty(resInfo)) {
                        RearrangeParam rearrangeParam = new RearrangeParam();
                        // 调用雅意重排能力
                        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
                        rangeContent.setQuery(manusTask.getContent());
                        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
                        for (PromptWebResult.ResInfoDetail resInfoDetail : resInfo) {
                            RearrangeParam.Articles articles = new RearrangeParam.Articles();
                            // 向量库没有title，这里固定给一个值
                            articles.setTitle(StringUtils.isNotBlank(resInfoDetail.getTitle()) ? resInfoDetail.getTitle() : "1");
                            articles.setPara(resInfoDetail.getTitle() + "\n" + resInfoDetail.getContext());
                            articlesList.add(articles);
                        }
                        rangeContent.setArticles(articlesList);
                        rearrangeParam.setContent(rangeContent);
                        RearrangeResult rearrange = answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
                        if (null == rearrange) {
                            return;
                        }
                        RearrangeResult.RearrangeData rearrangeData = rearrange.getData();
                        if (rearrangeData == null) {
                            return;
                        }

                        // 获取重排后的索引
                        List<Integer> indexList = rearrangeData.getIndex_list();
                        List<BigDecimal> resScoresList = rearrangeData.getRes_scores_list();
                        List<PromptWebResult.ResInfoDetail> toYayiList = Lists.newArrayList();
                        if (CollectionUtils.isNotEmpty(indexList)) {
                            int size = Math.min(10, applicationInfo.getFilterNum());
                            size = Math.min(resScoresList.size(), size);
                            Float rangeContentScore = applicationInfo.getRangeContentScore();
                            if (null == rangeContentScore) {
                                rangeContentScore = 0F;
                            }
                            for (int i = 0; i < size; i++) {
                                BigDecimal score = resScoresList.get(i);
                                if (score.compareTo(BigDecimal.valueOf(rangeContentScore)) >= 0) {
                                    toYayiList.add(resInfo.get(indexList.get(i)));
                                }
                            }

                            if (CollectionUtil.isNotEmpty(toYayiList)) {
                                try {
                                    ManusResult manusResult = new ManusResult();
                                    manusResult.setStep("网页探索");
                                    manusResult.setTaskId(manusTask.getTaskId());
                                    manusResult.setResInfo(toYayiList);
                                    resInfoAll.addAll(toYayiList);
                                    SseEmitterUtils.send(param.getClientId(), JSONUtil.toJsonStr(manusResult));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String prompt = toYayiList.stream()
                                        .map(p -> p.getTitle() + "\n" + p.getContext())
                                        .collect(Collectors.joining("==============="));

                                ManusResult showAnswer = new ManusResult();
                                showAnswer.setTaskId(manusTask.getTaskId());
                                showAnswer.setStep("总结");

                                // 内容：${content}
                                // 联网检索结果：${result}
                                String summary = "内容：" + manusTask.getContent() + "联网检索结果：" + prompt;
                                // 总结
                                llmStrategy.generate(summary, messagesList, new StepEntity(), false, llmResult -> {
                                    try {
                                        // log.info("FinalCollectStrategy大模型耗时:{}", System.currentTimeMillis() - start);
                                        // 如果是高危，则跳过
                                        if (StringUtils.isNotBlank(llmResult.getErrorMessage())) {
                                            if (llmResult.getErrorMessage().toLowerCase().contains(LLM_ERROR_RISK)) {
                                                throw new RuntimeException(llmResult.getErrorMessage());
                                            }
                                        }
                                        // String answer = result.getAnswer();
                                        String originalAnswer = llmResult.getAnswer();
                                        String reasoningContent = llmResult.getReasoningContent();
                                        if (StringUtils.isBlank(originalAnswer) && StringUtils.isBlank(reasoningContent)) {
                                            return;
                                        }
                                        // answer = answerUtils.analyticAnswer(answer);
                                        // String answer = AnswerUtils.analyticAnswer(originalAnswer);
                                        if (StringUtils.isBlank(originalAnswer) && StringUtils.isBlank(reasoningContent)) {
                                            return;
                                        }
                                        showAnswer.setStatus(result.getStatus());
                                        showAnswer.setAnswer(originalAnswer);
                                        showAnswer.setReasoningContent(reasoningContent);

                                        boolean flag = SseEmitterUtils.sendMsg(param.getClientId(), JSONUtil.toJsonStr(showAnswer));
                                        if (flag) {
                                            SseEmitterUtils.complete(param.getClientId());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                            }

                        }
                    }

                }
            });

            if (CollectionUtils.isNotEmpty(resInfoAll)) {
                String prompt = resInfoAll.stream()
                        .map(p -> p.getTitle() + "\n" + p.getContext())
                        .collect(Collectors.joining("==============="));

                List<YayiMessage> messagesLists = Lists.newArrayList();
                if (StringUtils.isNotBlank(applicationInfo.getSystemPrompt())) {
                    YayiMessage messages = new YayiMessage();
                    messages.setContent("##角色\n" +
                            "你是一个专业的数据可视化网页生成助手。根据提供的包含数据和描述的【内容】和【联网检索结果】，生成一段完整且专业的 HTML 文件代码。\n" +
                            "\n" +
                            "##任务要求\n" +
                            "1.深度理解用户提供的【内容】和【联网检索结果】，准确提炼和总结关键信息，生成有针对性、具体且有洞察力的文字说明，避免空泛描述。\n" +
                            "2.根据文本中的具体数据和业务背景，自动生成合适的 ECharts 图表（如柱状图、折线图、饼图等），图表标题、坐标轴、数据标签等内容要与输入内容高度相关，避免使用“类别1/2/3”“数据图表”等泛化词。\n" +
                            "3.页面内容以“文字总结+图表+文字总结+图表”这样的组合方式组织，每个图表前都要有对应的文字总结。\n" +
                            "4.文字说明和图表要紧密结合输入数据，确保内容准确、逻辑清晰，能反映数据的趋势、变化、异常等。\n" +
                            "5.输出内容仅为 HTML 文件代码，不要有其他多余说明。\n" +
                            "6.HTML 文件需包含必要的 ECharts CDN 引用，代码结构规范、美观。\n" +
                            "\n" +
                            "##示例\n" +
                            "###输入：\n" +
                            "“腾讯的收入在2019年为3772亿元，2020年为4820亿元，2021年为5601亿元，2022年为5546亿元，2023年为6090亿元。”\n" +
                            "###输出：\n" +
                            " <!DOCTYPE html>\n" +
                            "   <html>\n" +
                            "   <head>\n" +
                            "     <meta charset=\"utf-8\">\n" +
                            "     <title>腾讯近五年年收入可视化</title>\n" +
                            "     <script src=\"https://cdn.jsdelivr.net/npm/echarts/dist/echarts.min.js\"></script>\n" +
                            "   </head>\n" +
                            "   <body>\n" +
                            "     <h2>腾讯近五年年收入概览</h2>\n" +
                            "     <p>根据公开数据，腾讯在2019-2023年间年收入持续增长，2022年略有回落，2023年再创新高。</p>\n" +
                            "     <div id=\"main\" style=\"width: 600px;height:400px;\"></div>\n" +
                            "     <script>\n" +
                            "       var chartDom = document.getElementById('main');\n" +
                            "       var myChart = echarts.init(chartDom);\n" +
                            "       var option = {\n" +
                            "         title: { text: '腾讯近五年年收入' },\n" +
                            "         tooltip: {},\n" +
                            "         xAxis: { data: ['2019', '2020', '2021', '2022', '2023'] },\n" +
                            "         yAxis: {},\n" +
                            "         series: [{\n" +
                            "           name: '年收入（亿元）',\n" +
                            "           type: 'bar',\n" +
                            "           data: [3772, 4820, 5601, 5546, 6090]\n" +
                            "         }]\n" +
                            "       };\n" +
                            "       myChart.setOption(option);\n" +
                            "     </script>\n" +
                            "     <p>可以看出，2022年收入略有下降，但2023年恢复增长，显示出公司强劲的市场竞争力。</p>\n" +
                            "   </body>\n" +
                            "   </html>\n" +
                            "\n" +
                            "##注意\n" +
                            "注意事项：\n" +
                            "1.图表类型请根据数据自动选择（如时间序列用折线/柱状图，结构占比用饼图等）。\n" +
                            "2.保证 HTML 结构规范。\n" +
                            "3.不要输出与 HTML 无关的内容。\n" +
                            "4.文字说明要结合数据，体现分析和洞察，避免模板化、泛泛而谈。\n" +
                            "\n" +
                            "##限制\n" +
                            "echarts的url用下面这个\n" +
                            "https://cdn.staticfile.org/echarts/4.3.0/echarts.min.js");
                    messages.setRole(SYSTEM_PROMPT_FIELD);
                    messagesLists.add(messages);
                }
                String summary = prompt + "------------------\\n请根据以上资料，生成一个网页源代码，页面需要分块展示，并优化页面排版，另外还需要用https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js的 chart 库生成图表，包括柱形图，饼图，折线图等，生成网页代码即可，不要解释";
                // 生成图表
                String resuts = llmStrategy.generate(summary, messagesLists, new StepEntity(), false);
                ManusResult chats = new ManusResult();
                chats.setStep("图文总结");
                resuts = resuts
                        .replace("```html", StringConstant.BLANK)
                        .replace("```", StringConstant.BLANK);
                chats.setAnswer(resuts);
                SseEmitterUtils.sendMsg(param.getClientId(), JSONUtil.toJsonStr(chats));
            }
        }
    }


    /**
     * 问题理解
     *
     * @param param
     */
    private List<ManusTask> problemUnderstanding(ManusParam param) {
        if (StringUtils.isBlank(param.getQuestion())) {
            return Lists.newArrayList();
        }
        try {
            YayiPluginSelectionModelParam modelParam = new YayiPluginSelectionModelParam();
            modelParam.setAvaliable_plugins(ListUtil.toList());
            modelParam.setUse_only_custom_plugins(false);
            modelParam.setMessages(ListUtil.toList(new YayiMessage(USER_PROMPT_FIELD, param.getQuestion())));
            YayiPluginSelectionModelResult modelResult = yayiServer.pluginModel(modelParam);
            if (null != modelResult) {
                YayiPluginSelectionModelResult.PluginData data = modelResult.getData();
                if (null != data) {
                    YayiPluginSelectionModelResult.Message message = data.getMessage();
                    if (null != message) {
                        String pluginArgs = message.getPluginArgs();
                        if (StringUtils.isNotBlank(pluginArgs) && JSONUtil.isTypeJSONObject(pluginArgs)) {
                            JSONObject entries = JSONUtil.parseObj(pluginArgs);
                            if (entries.containsKey("query")) {
                                JSONArray query = entries.getJSONArray("query");
                                if (CollectionUtils.isNotEmpty(query)) {
                                    return query.stream().map(p -> {
                                        ManusTask manusTask = new ManusTask();
                                        manusTask.setTaskId(IdUtil.simpleUUID());
                                        manusTask.setContent(p.toString());
                                        return manusTask;
                                    }).collect(Collectors.toList());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ManusTask manusTask = new ManusTask();
        manusTask.setTaskId(IdUtil.simpleUUID());
        manusTask.setContent(param.getQuestion());
        return ListUtil.toList(manusTask);
    }
}
