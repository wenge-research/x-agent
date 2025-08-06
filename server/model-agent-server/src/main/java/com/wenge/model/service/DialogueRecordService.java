package com.wenge.model.service;

import com.wenge.model.dto.param.*;
import com.wenge.model.entity.Dialogue;
import com.wenge.oauth.entity.TokenUser;
import com.wg.appframe.core.bean.Result;
import org.dromara.easyes.core.biz.EsPageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DialogueRecordService {

    Result<EsPageInfo<Dialogue>> getRecord(RecordPageParam param);

    void exportRecord(RecordPageParam param, HttpServletResponse response);


    /**
     * 反馈[点赞点踩]
     * @param FeedbackParam
     * @return
     */
    Result feedback(FeedbackParam FeedbackParam);

    Result<EsPageInfo<Dialogue>> getCheckRecord(RecordCheckParam param);

    Result verifyRecord(RecordVerifyParam param);

    Result checkRecord(RecordAuditParam param);

    //这个推送只能系统审核使用
    Result pushingKnowledgeData(Dialogue dialogue, String applicationId, String dataSource, TokenUser tokenUser);

    Result checkFeedbackAuditRecord(RecordFeedbackAuditParam param);


    Result logicDelete(RecordAuditParam param);

    Result updateYYZFPicUrl(RecordVerifyParam param);

    /**
     * 根据会话id查询对话记录
     * @param conversationId
     * @return
     */
    List<Dialogue> getByConversationId(String conversationId);

    Result<EsPageInfo<Dialogue>> getLastOneRecord(LastestRecordPageParam param);

    /**
     * 条件查询对话历史数量
     * @param param
     * @return
     */
    Result getCountByAppId(RecordPageParam param);

    /**
     * 根据应用id和问题查询对话记录
     * @param questions
     * @param applicationIds
     * @return
     */
    List<Dialogue> getRecorByApplicationIdsAndQuestions(List<String> questions, List<String> applicationIds);

    Result bindKnn(KnnBindParam param);

    /**
     * 获取绑定的知识库id
     * @param param
     * @return
     */
    Result<String> getBindKnn(KnnBindParam param);

}
