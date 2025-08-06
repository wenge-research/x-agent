package com.wenge.model.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点参数元数据
 */
@Data
@Table("component_node_meta_param")
@ApiModel(value = "组件节点参数元数据")
public class MetaParam implements Cloneable, Serializable {
    private static final long serialVersionUID = 5335104616115502181L;
    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(value = "param_id")
    @ApiModelProperty(value = "参数id", required = true)
    private String paramId;

    /**
     * 标签
     */
    @Column(value = "label")
    @ApiModelProperty(value = "标签", required = true)
    private String label;

    /**
     * 变量名
     */
    @Column(value = "variable")
    @ApiModelProperty(value = "变量名", required = true)
    private String variable;

    /**
     * 参数类型 表示参数的实际类型
     */
    @Column(value = "type")
    @ApiModelProperty(value = "参数类型", allowableValues = "integer,string", required = true)
    private String type;

    /**
     * 参数描述
     */
    @Column(value = "desc")
    @ApiModelProperty(value = "参数描述", required = true)
    private String desc;

    /**
     * 节点id
     */
    @Column(value = "node_id")
    @ApiModelProperty(value = "节点id", required = true)
    private String nodeId;

    /**
     * 引用节点id
     * 注：reference_node_name字段实际存的是引用节点id
     */
    @Column(value = "reference_node_name")
    @ApiModelProperty(value = "引用节点id")
    private String referenceNodeId;

    /**
     * 参数值 可能为表达式，或者值
     * @see com.wenge.model.workflow.enums.MetaParamEnum#REFERENCE
     */
    @Column(value = "value")
    @ApiModelProperty(value = "参数值", required = true)
    private String value;

    /**
     * 参数类型 表示是否为引用类型
     */
    @Column(value = "value_type")
    @ApiModelProperty(value = "参数类型", allowableValues = "integer,string,reference", required = true)
    private String valueType;

    /**
     * 最大长度
     */
    @Column(value = "max_length")
    private Integer maxLength;

    /**
     * 是否必填
     */
    @Column(value = "required")
    @ApiModelProperty(value = "是否必填", required = true)
    private Boolean required;

    /**
     * 0入参 1出参
     */
    @ApiModelProperty(value = "0入参 1出参", allowableValues = "0,1", required = true)
    @Column(value = "direction")
    private Integer direction;

    /**
     * 变量状态
     */
    @Column(ignore = true)
    private String status;

    /**
     * 节点类型
     */
    @Column(ignore = true)
    private Integer nodeType;

    @Override
    public MetaParam clone() {
        try {
            MetaParam clone = (MetaParam) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
