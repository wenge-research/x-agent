package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.McpServer;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.ProcessPushEnum;
import com.wenge.model.service.ApplicationMcpRefService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.MCP_STRATEGY;
import static com.wenge.model.constants.AnswerStrategyContant.REARRANGE_SPLIT_CHAR;

@Service(AnswerStrategyContant.MCP_STRATEGY)
@Slf4j
public class McpStrategy implements AnswerStrategy {

    @Autowired
    private ApplicationMcpRefService applicationMcpRefService;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        String answer = StringConstant.BLANK;

        // answer = fromDocumentLibrary(dto, contextList);
        log.info("===>mcpStrategy:{}", answer);
        answerData.setAnswer(answer);
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.MCP_STRATEGY);
        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        // 判断是否开启问答库
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            return content;
        }
        step.setStep(AnswerStrategyContant.MCP_STEP);
        contextList.add(step);
        // 获取mcp结果
        DialogueServiceImpl.pushProcess(ProcessPushEnum.MCP, ListUtil.toList(), dto);

        List<McpServer> mcpServerByAppId = applicationMcpRefService.getMcpServerByAppId(applicationInfo.getApplicationId());
        List<McpResult> mcpResults = getMcpResult(dto.getContentTemp(), mcpServerByAppId, step);
        List<McpResult> mcpResultList = ListUtil.toList();
        // 提取奇数位
        if (CollectionUtil.isNotEmpty(mcpResults)) {
            int size = mcpResults.size();
            for (int i = 0; i < size; i++) {
                if (i % 2 != 0) {
                    mcpResultList.add(mcpResults.get(i));
                }
            }
        }

        step.setResult(mcpResultList);
        // 推送状态
        DialogueServiceImpl.pushProcess(ProcessPushEnum.MCP, mcpResults, dto);
        List<String> contentList = mcpResultList.stream().map(p -> {
            // 工具内容【工具名称】
            return p.getContent() + AnswerStrategyContant.PREFIX_SYMBOL + p.getName() + AnswerStrategyContant.SUFFIX_SYMBOL;
        }).collect(Collectors.toList());
        content.setContentList(contentList);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return content;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非文档库的结果
        if (null == step || !step.getStep().equals(MCP_STRATEGY)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        List<String> list = (List<String>) step.getResult();
        return mcpRef(list);
    }

    /**
     * 文档库 格式：内容[知识库id|文件id|评分]
     *
     * @param list
     * @return
     */
    private List<SourceAnswerResult> mcpRef(List<String> list) {
        return list.stream().map(p -> {
            // 解析： 工具内容【工具名称】
            String[] split1 = p.split(AnswerStrategyContant.PREFIX_SYMBOL);
            String text = split1[0];
            SourceAnswerResult sourceAnswerResult = new SourceAnswerResult();
            sourceAnswerResult.setKnowledgeName("mcp服务：");
            String route = split1[1]
                    .replace(AnswerStrategyContant.PREFIX_SYMBOL, StringConstant.BLANK)
                    .replace(AnswerStrategyContant.SUFFIX_SYMBOL, StringConstant.BLANK);
            sourceAnswerResult.setRoute(ListUtil.toList(route));
            String[] split = text.split(REARRANGE_SPLIT_CHAR);
            sourceAnswerResult.setText(split[split.length - 1]);
            return sourceAnswerResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        return Lists.newArrayList();
    }

    /**
     * mcp查询
     *
     * @param contentTemp
     * @param mcpServerByAppId
     * @return
     */
    public static List<McpResult> getMcpResult(String contentTemp, List<McpServer> mcpServerByAppId, StepEntity step) {
        ApplicationContext context = CoreContextProvider.getContext();
        Environment environment = context.getEnvironment();
        String queryApi = environment.getProperty("mcp.queryApi");
        if (CollectionUtil.isEmpty(mcpServerByAppId) || StringUtils.isBlank(queryApi)) {
            return Lists.newArrayList();
        }
        // 如果部署方式为npx或uvx，则将 url 置空（算法的要求）
        mcpServerByAppId.forEach(item -> {
            if ("npx".equals(item.getInstallWay()) || "uvx".equals(item.getInstallWay())) {
                item.setUrl(StringConstant.BLANK);
            }
            item.setType(item.getInstallWay());
            if ("npx".equals(item.getInstallWay()) || "uvx".equals(item.getInstallWay())) {
                if (StringUtils.isNotBlank(item.getNpxUvxService()) && JSONUtil.isTypeJSON(item.getNpxUvxService())) {
                    JSONObject npxJson = JSONUtil.parseObj(item.getNpxUvxService());
                    item.setArg(npxJson);
                }
            } else {
                JSONObject arg = new JSONObject();
                arg.set("apiKey", StringUtils.defaultString(item.getApiKey(), StringConstant.BLANK));
                item.setArg(arg);
                if ("custom".equals(item.getInstallWay())) {
                    item.setType("sse");
                }
            }
        });

        List<JSONObject> selectMcp = mcpServerByAppId.stream().map(p->{
            JSONObject entries = JSONUtil.parseObj(p);
            entries.set("id", IdUtil.simpleUUID());
            return entries;
        }).collect(Collectors.toList());
        JSONObject param = new JSONObject();
        param.set("query", contentTemp);
        param.set("select_mcp", selectMcp);
        List<String> knowledgeId = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();
        param.set("knowledge_id", knowledgeId);
        step.setPrompt("mcp查询url" + queryApi + "param:" + JSONUtil.toJsonStr(param));
        log.info("mcp查询url{} ,param:{}", queryApi, JSONUtil.toJsonStr(param));
        HttpRequest post = HttpUtil.createPost(queryApi);
        post.setConnectionTimeout(60 * 1000)
                .setReadTimeout(60 * 1000)
                .timeout(60 * 1000)
                .body(JSONUtil.toJsonStr(param));
        String body = post.execute().body();
        log.info("mcp查询返回结果：{}", body);
        if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            if (entries.containsKey("answer") && JSONUtil.isTypeJSONArray(entries.getStr("answer"))) {
                JSONArray answers = entries.getJSONArray("answer");
                if (CollectionUtil.isNotEmpty(answers)) {
                    return answers.toList(McpResult.class);
                }
            }
        }
        return Lists.newArrayList();
    }
}
