package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordAuditParam extends WGParam {

    private static final long serialVersionUID = 7668808923750612949L;

    /**
     * 对话id
     */
    private String applicationId;

    /**
     * 对话id
     */
    private String dialogueId;

    /**
     * 审核状态：0-待审核；1-审核通过；2-审核不通过，3-不处理
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditComment;

    /**
     * 核实后的答案
     */
    private String verifyAnswer;
}
