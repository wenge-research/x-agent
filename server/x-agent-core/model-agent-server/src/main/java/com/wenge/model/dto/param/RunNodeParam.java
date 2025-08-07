package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wenge.model.dto.result.ComponentNodeDto;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wg.appframe.core.dto.params.WGParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RunNodeParam extends WGParam {

    private static final long serialVersionUID = -8796193460843940782L;

    private JSONObject inputs;

    @ApiModelProperty(value = "节点id", required = true)
    private String nodeId;

    @ApiModelProperty(value = "节点名称", required = true)
    private String nodeName;

    @ApiModelProperty(value = "组件id", required = true)
    private String componentId;

    @ApiModelProperty(value = "节点类型", required = true)
    private ComponentNodeEnum nodeType;

    @ApiModelProperty(value = "输入参数")
    private List<MetaParam> input;

    @ApiModelProperty(value = "输出参数")
    private List<MetaParam> output;

    /**
     * 节点配置，不是所有节点都有
     */
    @ApiModelProperty(value = "特殊节点配置")
    private JSONObject settings;

    private List<ComponentNodeDto> nextNodeInfoList;
}
