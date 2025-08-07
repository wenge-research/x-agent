package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecommendQuestionParam extends WGParam {

    private static final long serialVersionUID = 7408849922010567565L;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 问题
     */
    private String question;

    /**
     * 对话ID
     */
    private String dialogueId;
}
