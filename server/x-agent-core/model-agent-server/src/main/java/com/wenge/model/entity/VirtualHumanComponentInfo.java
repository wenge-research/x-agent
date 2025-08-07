package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.MybatisFileConstant;
import com.wenge.model.enums.BitrateEnum;
import com.wenge.model.handler.IProxyConfigTypeHandler;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@ApiModel
@Data
@Table(value = "virtual_human_component_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class VirtualHumanComponentInfo extends FlexModel<VoiceComponentInfo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id",value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    @Column("component_id")
    @ApiModelProperty(name = "componentId",value = "组件业务ID", dataType = "String")
    private String componentId;

    @Column("component_name")
    @ApiModelProperty(name = "componentName",value = "组件名称", dataType = "String")
    private String componentName;

    @Column("introduce")
    @ApiModelProperty(name = "introduce",value = "组件简介", dataType = "String")
    private String introduce;

    @Column("component_icon")
    @ApiModelProperty(name = "componentIcon", value = "组件图标", dataType = "String")
    private String componentIcon;

    @Column("category")
    @ApiModelProperty(name = "category", value = "组件分类", dataType = "String")
    private String category;

    @Column("delete_flag")
    @ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
    private Integer deleteFlag = 0;

    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String updateUser;

    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    @Column("create_user")
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String createUser;

    @Column("tag")
    @ApiModelProperty(name = "category", value = "组件标签，多个以逗号隔开", dataType = "String")
    private String tag;

    @Column("mount_class")
    @ApiModelProperty(name = "mountClass", value = "传⼊需要挂载位置的dom类名，类名全局唯⼀，非必填，不传⼊则默认挂载在屏幕中间", dataType = "String")
    private String mountClass;

    @Column("signature")
    @ApiModelProperty(name = "signature", value = "用户鉴权", dataType = "String")
    private String signature;

    @Column("project_id")
    @ApiModelProperty(name = "projectId", value = "任务ID", dataType = "String")
    private String projectId;

    @Column("exclusive_virtual_human_id")
    @ApiModelProperty(name = "exclusiveVirtualHumanId", value = "参考OPENAPI文档获取虚拟⼈ID,SDK会根据虚拟⼈ID 生成⼀个全新的任务，进行初始化", dataType = "String")
    private String exclusiveVirtualHumanId;

    @Column("includeUI")
    @ApiModelProperty(name = "includeUI", value = "默认false,是否需要展示在员⼯平台配置的ui，置false只展示虚拟⼈", dataType = "String")
    private boolean includeUI;

    @Column("show_default_static_image")
    @ApiModelProperty(name = "showDefaultStaticImage", value = "默认false,是否在开启交互前后，展示默认数字⼈形象 。初始化sdk后，拉流前显示静态数字⼈形象图片，拉流结束（endRTC后）显示静态数字⼈形象图片", dataType = "String")
    private boolean showDefaultStaticImage;

    @Column("timeout")
    @ApiModelProperty(name = "timeout", value = "默认300000,间隔多长时间不交互自动关闭交互的时长，单位为ms", dataType = "Integer")
    private Integer timeout;

    // 数据库字段为proxy，但实体类属性为proxyConfig
    @Column(typeHandler = IProxyConfigTypeHandler.class)
    private IProxyConfig proxy;

    @Column("bitrate_enum")
    @ApiModelProperty(name = "bitrateEnum", value = "默认R_720P,分辨率信息,填枚举值R_1080P、R_720P，默认720P&1M-2M码率，1080P支持3M-6M码率", dataType = "BitrateEnum")
    private BitrateEnum bitrateEnum;
}
