package com.wenge.model.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.StepEntity;
import com.wg.appframe.core.bean.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public interface DialogueService {
    JSONObject dialogueByStreamEvaluation(DialogueByStreamParam param);
    SseEmitter dialogueByStream(DialogueByStreamParam param, HttpServletRequest request);

    SseEmitter documentDialogue(DocumentDialogueParam param, HttpServletRequest request) throws IOException;

    Map<String, String> matching(BiddingMatchingParam param);

    Result<String> translateText(TranslateParam stepParam);


    boolean recommendedContent(DialogueByStreamParam dto, Vector<StepEntity> contextList, Boolean bool);

    /**
     * 插入对话记录
     * @param dto
     * @param contextList
     */
    void completeAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList) ;

    Result<String> checkSensitiveWord(SensitiveWordCheck param);

    Result genEChartsDataFromModel(GenEchartsParam param);


    /**
     * 对话内容总结
     * @param param
     * @return
     */
    Result conversationSummary(BiddingMatchingParam param);

    /**
     * 根据“问题”获取指定对话日志里面的上下文里面的“汇总-重排”步骤信息
     * @param param
     * @return
     */
    Result queryCollectRearangeStepInfo(DialogueCollectRerrangeStepParam param);

    List<String> associationContent(AssociateQuestionParam associateQuestionParam);

    public boolean vagueContent(DialogueByStreamParam dto, Vector<StepEntity> contextList, Boolean bool);
    JSONObject dialogueRun(DialogueByStreamParam param, long start, boolean stopFlag);

}
