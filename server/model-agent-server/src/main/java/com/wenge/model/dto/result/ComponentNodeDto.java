package com.wenge.model.dto.result;

import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.entity.TriggerConfig;
import com.wenge.oauth.entity.TokenUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "组件节点")
public class ComponentNodeDto {
    private Long id;

    @ApiModelProperty(value = "节点id", required = true)
    private String nodeId;

    @ApiModelProperty(value = "节点名称", required = true)
    private String nodeName;

    @ApiModelProperty(value = "组件id", required = true)
    private String componentId;

    @ApiModelProperty(value = "节点类型", required = true)
    private Integer nodeType;

    @ApiModelProperty(value = "输入参数")
    private List<MetaParam> input;

    @ApiModelProperty(value = "输出参数")
    private List<MetaParam> output;

    /**
     * 节点配置，不是所有节点都有
     */
    @ApiModelProperty(value = "特殊节点配置")
    private String settings;

    private ComponentDto next;

    private String sseClientId;


    /**
     * clientType
     */
    private String clientType;

    /**
     * tokenUser
     */
    private TokenUser tokenUser;

    @ApiModelProperty(value = "触发器参数 如果开启了触发器,则必填")
    private TriggerConfig trigger;



}
