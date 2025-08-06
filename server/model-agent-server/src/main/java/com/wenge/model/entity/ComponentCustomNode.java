package com.wenge.model.entity;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.wenge.model.enums.CodeLanguageEnum;
import com.wenge.model.enums.EnableEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("component_custom_node")
@ApiModel("自定义节点表")
public class ComponentCustomNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Column(value = "id")
    @ApiModelProperty("主键id")
    private Long id;

    @Column(value = "component_id")
    @ApiModelProperty("组件id")
    private String componentId;

    @Column(value = "node_name")
    @ApiModelProperty("节点名称")
    private String nodeName;

    @Column(value = "node_desc")
    @ApiModelProperty("节点描述")
    private String nodeDesc;

    @Column(value = "icon")
    @ApiModelProperty("节点图标url")
    private String icon;

    @Column(value = "code")
    @ApiModelProperty("代码")
    private String code;

    @Column(value = "code_language")
    @ApiModelProperty("代码语言")
    private String codeLanguage;

    @Column(value = "input", typeHandler = Fastjson2TypeHandler.class)
    @ApiModelProperty("输入参数")
    private List<JSONObject> input;

    @Column(value = "output", typeHandler = Fastjson2TypeHandler.class)
    @ApiModelProperty("输出参数")
    private List<JSONObject> output;

    @Column(value = "is_delete", isLogicDelete = true)
    @ApiModelProperty("数据启用与删除状态")
    private EnableEnum isDelete;

    @Column(value = "start_status")
    @ApiModelProperty("启用状态 0:启用 1:禁用")
    private Integer startStatus;

    @Column(value = "create_user")
    @ApiModelProperty("创建用户")
    private Long createUser;

    @Column(value = "update_user")
    @ApiModelProperty("更行用户")
    private Long updateUser;

    @Column(value = "create_time")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @Column(value = "update_time")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
