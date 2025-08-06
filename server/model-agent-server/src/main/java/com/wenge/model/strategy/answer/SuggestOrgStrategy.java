package com.wenge.model.strategy.answer;

import com.alibaba.fastjson.JSONObject;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.InterceptTypeEnum;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.LlmGenerateUtil;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Vector;

@Service(AnswerStrategyContant.SUGGEST_ORG)
@Slf4j
public class SuggestOrgStrategy implements AnswerStrategy{

    @Autowired
    private LlmGenerateUtil llmGenerateUtil;

    @Autowired
    private KnowledgeDataMapper knowledgeDataMapper;

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        StepEntity step = new StepEntity();
        contextList.add(step);
        step.setStep("无法回答时推荐机构");

        // 获取推荐机构字典
        //List<String> suggestOrgList = DialogueServiceImpl.INTERCEPT_WORD.get("推荐机构");
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        List<String> suggestOrgList = answerUtils.getInterceptWord(applicationInfo.getApplicationId(), InterceptTypeEnum.SUGGEST_ORG.getName());

        if (CollectionUtils.isNotEmpty(suggestOrgList)) {
            String suggestOrgMap = suggestOrgList.get(0);
            String[] split = suggestOrgMap.split(",");
            JSONObject suggestOrgData = new JSONObject();
            String[] split1;
            StringBuilder sb = new StringBuilder();
            for (String sug : split) {
                split1 = sug.split(":");
                suggestOrgData.put(split1[0], split1[1]);
                sb.append(split1[0]).append("，");
            }
            sb.delete(sb.length() - 1, sb.length());

            List<YayiMessage> messagesList = Lists.newArrayList();
            YayiMessage messages = new YayiMessage();
            messages.setRole("system");
            messages.setContent("Provide helpful and harmless responses.");
            messagesList.add(messages);

            messages = new YayiMessage();
            messages.setRole("user");
            messages.setContent("我将提供一个提问和几个分类，请根据问题的实际含义选定一个分类，如果问题都不属于我所提供的分类，就归属于其他分类，只需要回答分类即可，能做到吗？");
            messagesList.add(messages);

            messages = new YayiMessage();
            messages.setRole("yayi");
            messages.setContent("当然可以，请告诉我你的提问和提供的分类选项。我会根据问题的实际含义来选择最合适的分类。如果所有提供的分类都不适用，我会将其归类为“其他”。");
            messagesList.add(messages);
            String question = dto.getContentTemp();
            question = AnswerUtils.questionMark(question);
            //String typeFromModel = llmGenerateUtil.getFrom30b("问题：" + question + "\r\n分类：" + sb, messagesList, step, false);
            String typeFromModel = llmGenerateUtil.generate("问题：" + question + "\r\n分类：" + sb, messagesList, step, false);
            if (StringUtils.isNotBlank(typeFromModel)) {
                StepEntity notAnswerType = new StepEntity();
                String orgInfo = suggestOrgData.getString(typeFromModel);
                if (StringUtils.isNotBlank(orgInfo)) {
                    String getFromPolicyTerm = getGetFromPolicyTerm(orgInfo, notAnswerType);
                    answerData.setAnswer(getFromPolicyTerm);
                    //answerData.setOrg(orgInfo);
                    //answerData.setType(typeFromModel);
                }
            }
        }
        return answerData;

    }

    /**
     * 查询完整匹配知识库问答
     *
     * @param question
     * @return
     */
    private String getGetFromPolicyTerm(String question, StepEntity step) {
        List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();

        LambdaEsQueryWrapper<KnowledgeData> wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                .select(KnowledgeData::getTitle, KnowledgeData::getContent)
                //.eq(KnowledgeData::getSuggest, "推荐")
                .eq(KnowledgeData::getTitle, question)
                .in(KnowledgeData::getKnowledgeId, knowldegeIdList)
                .size(1);
        List<KnowledgeData> dataList = knowledgeDataMapper.selectList(wrapper);
        step.setResult(dataList);
        String resultFromEs = "";
        if (CollectionUtils.isNotEmpty(dataList)) {
            KnowledgeData data = dataList.get(0);
            resultFromEs = data.obtainAnswer();
        }
        return resultFromEs;
    }
}
