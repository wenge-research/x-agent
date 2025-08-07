package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordVerifyParam extends WGParam {

    private static final long serialVersionUID = 7709385140595662192L;

    private String applicationId;
    /**
     * 对话id
     */
    private String dialogueId;

    /**
     * 核实状态，0-待核实；1-已核实修改，2-已核实正确，3-恶意问题，4-不处置，5-待重新核实
     */
    private String verifyStatus;

    /**
     * 核实后的答案
     */
    private String verifyAnswer;


    /**
     * 营业执照url
     */
    private String yyzhUrl;


}
