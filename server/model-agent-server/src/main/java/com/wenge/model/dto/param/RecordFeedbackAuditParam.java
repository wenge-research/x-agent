package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.rely.FieldType;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordFeedbackAuditParam extends WGParam {

    private static final long serialVersionUID = 7668808923750612948L;

    /**
     * 对话id
     */
    private String applicationId;

    /**
     * 对话id
     */
    private String dialogueId;


    /**
     * 对话提交赞踩记录-客户端审核 审核状态：0-赞同 1-不赞同
     */
    private String feedbackAuditStatus;

    /**
     * 对话提交赞踩记录-客户端审核 审核意见
     */
    private String feedbackAuditComment;
}
