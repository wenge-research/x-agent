package com.wenge.model.strategy.answer;

import com.google.common.collect.Lists;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.ExcelParserInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.ExcelParserInfoService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ExcelParserInfoTableDef.EXCEL_PARSER_INFO;

@Service(AnswerStrategyContant.WENSHU_CONTENT)
@Slf4j
public class WenshuContentStrategy implements AnswerStrategy{

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private ExcelParserInfoService excelParserService;

    /**
     * 获取答案
     *
     * @param dto
     * @param contextList
     * @return
     */
    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        // 获取日志id
        MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
        String answer = StringConstant.BLANK;
        return answerData;
    }


    private List<String> getTableNameList(List<String> knowledgeIdList){
        QueryWrapper queryWrapper = Wrappers.create();
        queryWrapper.and(EXCEL_PARSER_INFO.DELETE_FLAG.eq("0"));
        queryWrapper.and(EXCEL_PARSER_INFO.KNOWLEDGE_ID.in(knowledgeIdList));
        queryWrapper.and(EXCEL_PARSER_INFO.ENABLE_FLAG.eq("1"));
        queryWrapper.and(EXCEL_PARSER_INFO.SYNCH_STATUS.eq("3"));
        final List<ExcelParserInfo> excelParserInfos = excelParserService.getMapper().selectListByQuery(queryWrapper);
        return excelParserInfos.stream().map(ExcelParserInfo::getExcelId).collect(Collectors.toList());
    }

    /**
     * 获取知识库数据，以供大模型回答，这里是最原始的数据，没有经过重排过滤
     * @param dto
     * @param contextList
     * @return
     */
    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {

        MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
        StepEntity step = new StepEntity();
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.WENSHU_CONTENT);
        //
        // // 获取应用
        // ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // // 判断是否开启问答库
        // if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
        //     log.warn("当前应用没有开启问答库");
        //     return content;
        // }
        //
        // // 判断是否开启问数搜索
        // if (!YesNoEnum.YES.getName().equals(applicationInfo.getWenshuFlag())) {
        //     log.warn("当前应用没有开启问数搜索");
        //     return content;
        // }
        //
        // List<String> collect = new ArrayList<>();
        // contextList.add(step);
        // step.setStep(AnswerStrategyContant.WENSHU_CONTENT_STEP);
        // List<String> tableNameList = getTableNameList(dto.getKnowledgeIdList());
        // if (CollectionUtils.isEmpty(tableNameList)) {
        //     log.warn("当前没有结构化数据表 execl ");
        //     return content;
        // }
        // try {
        //     JSONObject jsonObject = WenShuExeclPost.getAnswerByExecl(dto.getQuestion(),tableNameList,dto.getKnowledgeIdList());
        //     if (jsonObject != null && "success".equals(jsonObject.get("status"))) {
        //         JSONArray results = jsonObject.getJSONArray("data");
        //         for (Object result : results) {
        //             try {
        //                 Map<String, Object> maps = (Map<String, Object>) result;
        //                 String value = dto.getQuestion() + "?  ";
        //                 for (Map.Entry<String, Object> entry : maps.entrySet()) {
        //                     value += entry.getKey() + ":" + entry.getValue() + ";";
        //                 }
        //                 collect.add(value + "\n null");
        //             }catch(Exception e){
        //                 e.printStackTrace();
        //             }
        //
        //         }
        //         content.setContentList(collect);
        //     }else {
        //         log.error("调用【问数搜索】接口errer ：{}", jsonObject);
        //     }
        // }catch (Exception e){
        //     e.printStackTrace();
        //     log.error("调用【问数搜索】接口异常 ：{}");
        // }
        return content;
    }


    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        return Lists.newArrayList();
    }


    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        return com.google.common.collect.Lists.newArrayList();
    }
}
