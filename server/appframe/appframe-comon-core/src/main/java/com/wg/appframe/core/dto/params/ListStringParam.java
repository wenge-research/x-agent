package com.wg.appframe.core.dto.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: 单参数类-字符串数组类
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-12-01 09:53:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ListStringParam extends SingleParam {

    private static final long serialVersionUID = 6619927271245867429L;

    /**
     * 参数值
     */
    @ApiModelProperty(value = "参数值")
    private List<String> param;
}
