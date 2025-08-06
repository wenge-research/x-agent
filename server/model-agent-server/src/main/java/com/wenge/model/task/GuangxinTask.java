package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.ProviderGuanxinParam;
import com.wenge.model.dto.result.ProviderGuanxinResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.GuangXinMatter;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.GuangXinMatterEnum;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.GuangXinMatterService;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.service.impl.ImageMultimediaServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.result.VisualResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 关芯智巡定时器
 */
@RestController
@RequestMapping("/agentTask/guangxin")
@Slf4j
public class GuangxinTask {

    @Autowired
    private GuangXinMatterService guangXinMatterService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private LlmInfoService llmInfoService;

    @Value("${task.guanxin.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;
    @Autowired
    private ImageMultimediaServiceImpl image;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisKey.GUANXI_MASTTER, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.GUANXI_MASTTER);
            redisService.unlock(RedisConstant.LOCK_GUAXIN);
        }
    }

    @Autowired
    private YayiServer yayiServer;

    // 线程池
    private final ThreadPoolExecutor POOL = ExecutorBuilder.create().setCorePoolSize(8).setMaxPoolSize(8).setWorkQueue(new LinkedBlockingDeque<>()).build();

    /**
     * 解析json
     * @param generate
     * @return
     */
    private  List<JSONObject> parseJson(String generate) {
        if (StringUtils.isBlank(generate) || generate.contains(AnswerStrategyContant.NO_ANSWER_TEXT)) {
            return Lists.newArrayList();
        }
        generate = AnswerUtils.dealLLmJson(generate);
        if (JSONUtil.isTypeJSONArray(generate)) {
            JSONArray jsonArray = JSONUtil.parseArray(generate);
            return jsonArray.toList(JSONObject.class);
        }
        return Lists.newArrayList();
    }

    /**
     * 更新事项
     *
     * @param guangXinMatter
     */
    private synchronized void updateGuanxinMatter(GuangXinMatter guangXinMatter) {
        guangXinMatterService.updateById(guangXinMatter);
    }

    /**
     * 识别失败
     *
     * @param guangXinMatter
     */
    private void updateAntimatterFailed(GuangXinMatter guangXinMatter, String msg) {
        Integer errorCount = guangXinMatter.getErrorCount();
        guangXinMatter.setErrorCount(errorCount + 1);
        guangXinMatter.setErrorMsg(msg);
        guangXinMatter.setStatus(GuangXinMatterEnum.FAIL.getCode());
        guangXinMatterService.updateById(guangXinMatter);
    }

    /**
     * 通知失败
     *
     * @param guangXinMatter
     */
    private void notifyFailed(GuangXinMatter guangXinMatter, String msg) {
        Integer errorCount = guangXinMatter.getErrorCount();
        guangXinMatter.setErrorCount(errorCount + 1);
        guangXinMatter.setErrorMsg(msg);
        guangXinMatter.setStatus(GuangXinMatterEnum.NOTIFY_FAIL.getCode());
        guangXinMatterService.updateById(guangXinMatter);
    }

    /**
     * 回调通知
     * @param item
     * @param providerGuanxinResult
     */
    private void notify(GuangXinMatter item, ProviderGuanxinResult providerGuanxinResult) {
        try {
            // 通知中
            item.setStatus(GuangXinMatterEnum.NOTIFYING.getCode());
            updateGuanxinMatter(item);

            if (StringUtils.isBlank(item.getCallBackUrl())) {
                item.setErrorMsg("无回调地址");
                updateGuanxinMatter(item);
                return;
            }

            HttpRequest post = HttpUtil.createPost(item.getCallBackUrl());
            post.body(JSON.toJSONString(providerGuanxinResult));
            post.setConnectionTimeout(1000 * 30);
            post.setReadTimeout(1000 * 30);
            HttpResponse execute = post.execute();
            String body = execute.body();
            if (StringUtils.isBlank(body)) {
                // 没有通知成功
                notifyFailed(item, "未收到通知成功消息");
                return;
            }
            // 通知成功
            item.setStatus(GuangXinMatterEnum.NOTIFY_SUCCESS.getCode());
            updateGuanxinMatter(item);

        } catch (Exception e) {
            notifyFailed(item, e.getMessage());
        }
    }


    /**
     * 调用大模型获取结果
     *
     * @param applicationInfo
     * @param param
     * @return
     */
    private String generate(GuangXinMatter item, ApplicationInfo applicationInfo, ProviderGuanxinParam param) {
        // 封装大模型消息
        StepEntity step = new StepEntity();
        List<YayiMessage> messagesList = Lists.newArrayList();
        if (StringUtils.isNotBlank(applicationInfo.getSystemPromptResult())) {
            messagesList.add(new YayiMessage("system", applicationInfo.getSystemPromptResult()));
        }

        List<ProviderGuanxinParam.Content> contentList = param.getContentList();
        int index = 1;
        for (ProviderGuanxinParam.Content content : contentList) {
            content.setIndex(index++);
        }
        // 封装事项指令
        String matterStr = contentList.stream().map(p -> {
            // 「事项1：事项名称，事项指标」
            // 「事项1：事项名称是办公场所环境问题，事项一的检查指标名称包括：地面是否有污水污迹，是否存在办公环境杂乱，是否存在卫生间脏乱，是否存在垃圾桶满冒，是否存在堆物堆料，是否存在物资未及时发放」
            List<String> checkItemList = p.getCheckItemList();
            String items = String.join("，", checkItemList);
            String template = "「事项{index}：事项{index}的事项名称是{matter}，事项{index}的检查指标名称包括：{item}」";
            template = template
                    .replace("{index}", p.getIndex() + "")
                    .replace("{matter}", p.getMatter())
                    .replace("{item}", items);
            return template;
        }).collect(Collectors.joining("\n"));

        // 获取日期
        String currentDataCn = DateUtil.getCurrentDateCn();
        // 获取指令模板
        String prompt = applicationInfo.getPromptTemplate();
        // 设置指令内容
        prompt = prompt.replace("{date}", currentDataCn)
                .replace("{matter}", matterStr)
                .replace("{sceneDescription}", param.getSceneDescription());
        // 获取大模型
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationInfo.getModelId());
        // 调用大模型，判别事项类型和指标问题
        String generate = llmStrategy.generate(prompt, messagesList, step, false);
        item.setLlmPrompt(JSON.toJSONString(step.getPrompt()));
        return generate;
    }

    /**
     * 解析图片
     */
    public void parseImage(ProviderGuanxinParam param, Long matterId) {
        if (CollectionUtils.isEmpty(param.getImageUrl())) {
            return;
        }
        String configuration = AppConfigContant.getConfiguration(param.getApplicationId() + "@" + "visual_image_prompt");
        if (StringUtils.isBlank(configuration)) {
            return;
        }
        POOL.execute(() -> {
            try {
                List<String> imageUrl = param.getImageUrl();
                GuangXinMatter matter = new GuangXinMatter();
                matter.setId(matterId);
                List<JSONObject> imageParseList = imageUrl.stream().map(p -> {
                    JSONObject data = new JSONObject();
                    data.put("url", p);
                    data.put("content", StringConstans.BLANK);
                    VisualResult visual = yayiServer.visual(p, configuration);
                    if (null != visual) {
                        Long code = visual.getCode();
                        if (StringConstans.CODE.equals(code)) {
                            VisualResult.VisualData visualData = visual.getData();
                            if (null != visualData) {
                                data.put("content", visualData.getContent());
                            }
                        }
                    }
                    return data;
                }).collect(Collectors.toList());
                matter.setParseImage(JSON.toJSONString(imageParseList));
                matter.setCallBackUrl(param.getCallBackUrl());
                ProviderGuanxinResult providerGuanxinResult = new ProviderGuanxinResult();
                providerGuanxinResult.setDataId(param.getDataId());
                providerGuanxinResult.setParseImageList(imageParseList);
                providerGuanxinResult.setType("2");
                notify(matter, providerGuanxinResult);
                updateGuanxinMatter(matter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理单个事项
     */
    public void handleMatter(GuangXinMatter item) {
        try {
            String paramContent = item.getParamContent();
            if (StringUtils.isBlank(paramContent)) {
                item.setStatus(GuangXinMatterEnum.PARAM_NOT_FULL.getCode());
                updateGuanxinMatter(item);
                return;
            }
            // 设置状态正在识别
            item.setStatus(GuangXinMatterEnum.WAITING.getCode());
            updateGuanxinMatter(item);

            // 提取参数
            ProviderGuanxinParam param = JSON.parseObject(paramContent, ProviderGuanxinParam.class);
            ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, YesNoEnum.YES.getName());

            // 解析图片
            parseImage(param, item.getId());

            // 封装结果对象
            ProviderGuanxinResult providerGuanxinResult = new ProviderGuanxinResult();
            providerGuanxinResult.setId(param.getId());
            providerGuanxinResult.setDataId(param.getDataId());

            // 缓存应用信息
            DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);

            // 调用大模型，获取结果
            String generate = generate(item, applicationInfo, param);

            // 设置大模型prompt和结果
            item.setLlmResult(generate);

            // 解析大模型的结果
            List<JSONObject> dataList = parseJson(generate);

            // 空数据也通知一下
            if (CollectionUtil.isEmpty(dataList)) {
                item.setStatus(GuangXinMatterEnum.SUCCESS.getCode());
                updateGuanxinMatter(item);
                // 空数据也通知一下
                providerGuanxinResult.setResultList(Lists.newArrayList());
                providerGuanxinResult.setType("1");
                notify(item, providerGuanxinResult);
                return;
            }

            // 解析结果
            List<ProviderGuanxinResult.GxResult> mapperResults = dataList.stream().map(itemData -> {
                ProviderGuanxinResult.GxResult gxResult = new ProviderGuanxinResult.GxResult();
                gxResult.setMatterName(itemData.getString("事项名称"));
                gxResult.setItemName(itemData.getString("指标名称"));
                gxResult.setCheck(itemData.getString("结果"));
                return gxResult;
            }).collect(Collectors.toList());
            providerGuanxinResult.setResultList(mapperResults);

            // 更新结果
            item.setResultContent(JSON.toJSONString(providerGuanxinResult));
            item.setStatus(GuangXinMatterEnum.SUCCESS.getCode());
            updateGuanxinMatter(item);

            // 通知
            providerGuanxinResult.setType("1");
            notify(item, providerGuanxinResult);
        } catch (Exception e) {
            log.error("关芯智巡定时器执行异常", e);
            updateAntimatterFailed(item, e.getMessage());
        }

    }
}
