package com.wenge.model.dto.result.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.enums.CodeLanguageEnum;
import com.wenge.model.enums.EnableEnum;
import com.wg.appframe.core.dto.results.WGResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel(value = "节点管理-自定义节点-响应实体类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentCustomNodeResult extends WGResult {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点描述")
    private String nodeDesc;

    @ApiModelProperty("节点图标url")
    private String icon;

    @ApiModelProperty("启用状态")
    private Integer startStatus;

    @ApiModelProperty("代码")
    private String code;

    @ApiModelProperty("代码语言")
    private String codeLanguage;

    @ApiModelProperty("输入参数")
    private List<JSONObject> input;

    @ApiModelProperty("输出参数")
    private List<JSONObject> output;

    @ApiModelProperty(value = "特殊节点配置")
    private JSONObject settings;

}
