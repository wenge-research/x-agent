package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.ApiDataPageParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.*;
import com.wenge.model.service.ApiDataService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;

/**
 * 附件策略
 */
@Service(API_STRATEGY)
@Slf4j
public class ApiStrategy implements AnswerStrategy {

    @Autowired
    private ApiDataService apiDataService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;


    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(step);
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.API_STRATEGY);
        step.setStep(API_STEP);
        List<ApiData> apiDataList = getApiDataList(step);
        // 内容[api请求地址]
        List<String> contentList = apiDataList.stream().map(p -> {
            //内容【知识库id|api地址】
            return p.getContent() + AnswerStrategyContant.PREFIX_SYMBOL +
                    p.getKnowledgeId() + AnswerStrategyContant.MID_ONE_SYMBOL
                    + p.getApiAddress() + AnswerStrategyContant.SUFFIX_SYMBOL;
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
        if (null == step || !step.getStep().equals(API_STEP)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        List<String> list = (List<String>) step.getResult();
        return api(list);
    }

    /**
     * 文档库 格式：内容[api来源]
     *
     * @param list
     * @return
     */
    private List<SourceAnswerResult> api(List<String> list) {
        return list.stream().map(p -> {
            // 解析： 内容【知识库id|api地址】
            String[] split1 = p.split(AnswerStrategyContant.PREFIX_SYMBOL);
            String text = split1[0];
            String knowledgeId = split1[1];
            SourceAnswerResult sourceAnswerResult = new SourceAnswerResult();
            String route = split1[2]
                    .replace(AnswerStrategyContant.PREFIX_SYMBOL, StringConstant.BLANK)
                    .replace(AnswerStrategyContant.SUFFIX_SYMBOL, StringConstant.BLANK);
            sourceAnswerResult.setRoute(ListUtil.toList(route));
            String[] split = text.split(REARRANGE_SPLIT_CHAR);
            sourceAnswerResult.setText(split[split.length - 1]);
            String knowledgeName = StringConstant.BLANK;
            if (StringUtils.isNotBlank(knowledgeId)) {
                KnowledgeInfo knowledgeInfo = knowledgeInfoService.getById(knowledgeId);
                if (null != knowledgeInfo) {
                    knowledgeName = knowledgeInfo.getKnowledgeName();
                }
            }
            sourceAnswerResult.setKnowledgeName(knowledgeName);
            return sourceAnswerResult;
        }).collect(Collectors.toList());
    }


    /**
     * 从es获取数据
     * @return
     */
    private List<ApiData> getApiDataList(StepEntity step) {
        Map<String, List<KnowledgeInfo>> knowledgeVectorMap = DialogueServiceImpl.KNOWLEDGE_VECTOR_MAP.get();
        if (null == knowledgeVectorMap || knowledgeVectorMap.isEmpty()) {
            return Lists.newArrayList();
        }
        List<KnowledgeInfo> knowledgeInfos = knowledgeVectorMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        List<String> knowledgeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
        ApiDataPageParam param = new ApiDataPageParam();
        param.setKnowledgeIds(knowledgeIdList);
        List<ApiData> apiDataList = apiDataService.getApiDataByKnowledgeId(param);
        step.setResult(apiDataList);
        return apiDataList;
    }

}
