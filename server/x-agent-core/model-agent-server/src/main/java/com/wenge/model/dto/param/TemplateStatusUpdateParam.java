package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateStatusUpdateParam extends WGParam {

    private static final long serialVersionUID = -314687128332375170L;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 主键id
     */
    private Long id;
}
