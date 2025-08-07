package com.wenge.model.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 映射组件表
 */
@Data
@Table(value = "component_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
public class Component {
    @Id(value = "id", keyType = KeyType.Auto)
    private Long id;

    @Column("component_id")
    @ApiModelProperty(name = "componentId", value = "组件id 有业务作用", dataType = "String")
    private String componentId;

    @Column("component_name")
    @ApiModelProperty(name = "componentName", value = "组件名称", dataType = "String")
    private String componentName;

    @Column("component_desc")
    @ApiModelProperty(name = "componentDesc", value = "组件描述", dataType = "String")
    private String componentDesc;

    @Column("status")
    @ApiModelProperty(name = "status", value = "组件状态 0未发布 1已发布", dataType = "Integer")
    private Integer status;

    /**
     * 组件图标
     */
    @Column("icon")
    @ApiModelProperty(name = "icon", value = "组件图标", dataType = "String")
    private String icon;

    /**
     * 画布数据
     */
    @Column(value = "canvas", typeHandler = Fastjson2TypeHandler.class)
    @ApiModelProperty(name = "canvas", value = "画布数据", dataType = "String")
    private String canvas;

    /**
     * 类型 1-插件(基于 api),2-普通工作流，3-普通对话流,4-应用的工作流,5-应用的对话流，, 6-插件(基于代码)
     *
     * @see com.wenge.model.enums.ComponentEnum
     */
    @Column("type")
    @ApiModelProperty(name = "type", value = "类型 1插件", dataType = "String")
    private Integer type;

    /**
     * 创建人
     */
    @Column(value = "create_user")
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String createUser;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 更新人，OnFieldFill注解可以自动填充，fill为触发机制，mdcKey为MDC中key，提取其中的值
     */
    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String updateUser;

    /**
     * 租户id
     */
    @Column(value = "tenant_id")
    @ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
    private String tenantId;

    /**
     * 是否发布应用商店 0-未发布（默认） 1-已发布 2-待审核
     * 单独建一张表记录 发布记录或者下架记录
     */
    @Column("publish_app_store")
    @ApiModelProperty(name = "publishAppStore", value = "0-未发布（默认） 1-已发布 2-待审核", dataType = "Integer")
    private Integer publishAppStore;

    @Column("publish_desc")
    @ApiModelProperty(name = "publishDesc",value = "发布描述", dataType = "String")
    private String publishDesc;

    @Column("publish_time")
    @ApiModelProperty(name = "publishTime", value = "发布时间", dataType = "Date")
    private Date publishTime;

    @Column("all_visible")
    @ApiModelProperty(name = "allVisible",value = "是否所有人可见", dataType = "Integer", notes = "1:所有人可见,0:只有自己可见")
    private Integer allVisible;

    /**
     * 标签，多个逗号分隔
     */
    @Column("labels")
    private String labels;

    /**
     * 内置官方标签：0-否，1-是
     */
    @Column("system_plugin")
    private Integer systemPlugin;

    /**
     * 模型创建归属,是官方创建还是个人创建
     */
    @Column("owner_type")
    @ApiModelProperty(name = "ownerType",value = "模型创建归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
    private String ownerType;

    @Column(ignore = true)
    private ApplicationInfo applicationInfo;

    @Column(ignore = true)
    private List<ComponentNode> nodes;


    @Column(ignore = true)
    @ApiModelProperty(name = "componentNewId", value = "指定组件id", dataType = "String")
    private String componentNewId;

    /**
     * fromApp-从应用删除，其他默认删除
     */
    @Column(ignore = true)
    @ApiModelProperty(name = "deleteFrom", value = "删除的来源", dataType = "String")
    private String deleteFrom;
}
