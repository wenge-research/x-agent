package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.MybatisFileConstant;
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
//@Data
@Data(staticConstructor = "create")
@Table(value = "model_plugin_api_manage", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ModelPluginApiManage extends FlexModel<ModelPluginApiManage> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id",value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    @Column("model_plugin_api_id")
    @ApiModelProperty(name = "modelPluginApiId",value = "业务ID", dataType = "String")
    private String modelPluginApiId;

    @Column("model_id")
    @ApiModelProperty(name = "modelId",value = "模型ID", dataType = "String")
    private String modelId;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId",value = "应用ID", dataType = "String")
    private String applicationId;

    @Column("model_instruction")
    @ApiModelProperty(name = "modelInstruction", value = "模型指令", dataType = "String")
    private String modelInstruction;

    @Column("auth_method")
    @ApiModelProperty(name = "authMethod", value = "插件鉴权方式 0-无需鉴权(默认) 1-API_KEY", dataType = "String")
    private String authMethod;

    @Column("category")
    @ApiModelProperty(name = "category", value = "模型插件类型 1-地址省市区解析 2-投诉问题打标 3-问题归纳汇总 等等", dataType = "String")
    private String category;

    @Column("name")
    @ApiModelProperty(name = "name", value = "插件名称", dataType = "String")
    private String name;

    @Column("description")
    @ApiModelProperty(name = "description", value = "插件描述", dataType = "String")
    private String description;

    @Column("desc_param")
    @ApiModelProperty(name = "descParam", value = "插件输入字段json", dataType = "String")
    private String descParam;

    @Column("desc_result")
    @ApiModelProperty(name = "descResult", value = "插件输出字段json", dataType = "String")
    private String descResult;

    @Column("path_api")
    @ApiModelProperty(name = "pathApi", value = "api相对路径", dataType = "String")
    private String pathApi;

    @Column("icon")
    @ApiModelProperty(name = "icon", value = "图标", dataType = "String")
    private String icon;

    @Column("doc_file_url")
    @ApiModelProperty(name = "docFileUrl", value = "插件文档地址", dataType = "String")
    private String docFileUrl;

    @Column("enable_flag")
    @ApiModelProperty(name = "enableFlag", value = "是否开启[1-是/0-否]", dataType = "String")
    private String enableFlag;


    @Column(value = "delete_flag", isLogicDelete = true)
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

    @Column("type")
    @ApiModelProperty(name = "type", value = "类型【app-应用的类型，plugin-插件类型】", dataType = "String")
    private String type;


    @Column(ignore = true)
    @ApiModelProperty(name = "modelPluginApiId",value = "业务ID", dataType = "String")
    private String newModelPluginApiId;
}
