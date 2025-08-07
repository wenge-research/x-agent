package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TenantUpdateStatusParam extends WGParam {

    private static final long serialVersionUID = 1850573752693958472L;
    /**
     * 业务id
     */
    @ApiModelProperty(name = "tenantId", value = "业务id", dataType = "String")
    private String tenantId;


    /**
     * 是否启用: 0 禁用, 1 启用
     */
    @ApiModelProperty(name = "status", value = "是否启用: 0 禁用, 1 启用", dataType = "Integer")
    private Integer status;
}
