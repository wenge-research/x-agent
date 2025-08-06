package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssociateQuestionParam extends WGParam {

    private static final long serialVersionUID = 7408849922010567565L;

    private String applicationId;
    private String question;
    private Integer questionType;

    /**
     * 联想问题数量
     */
    private Integer associateQuestionNum;
}
