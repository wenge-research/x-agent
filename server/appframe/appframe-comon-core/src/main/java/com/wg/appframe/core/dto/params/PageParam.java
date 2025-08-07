package com.wg.appframe.core.dto.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 分页参数
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-12-01 09:53:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageParam extends WGParam {

    private static final long serialVersionUID = -1192639412223245875L;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;

    /**
     * 条数
     */
    @ApiModelProperty(value = "条数", required = true)
    private Integer pageSize;

}
