package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
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
@Table(value = "model_plugin_api_auth_user", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ModelPluginApiAuthUser extends FlexModel<ModelPluginApiAuthUser> implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    // @Column(ignore = true)
    // private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id",value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    @Column("model_plugin_api_id")
    @ApiModelProperty(name = "modelPluginApiId",value = "模型插件业务ID", dataType = "String")
    private String modelPluginApiId;

    @Column("remark")
    @ApiModelProperty(name = "remark",value = "密钥备注", dataType = "String")
    private String remark;

    @Column("mobile")
    @ApiModelProperty(name = "mobile",value = "用户手机号", dataType = "String")
    private String mobile;

    @Column("name")
    @ApiModelProperty(name = "name", value = "用户姓名", dataType = "String")
    private String name;

    @Column("expire_time")
    @ApiModelProperty(name = "expireTime", value = "过期时间", dataType = "String")
    private String expireTime;

    @Column("secret_key")
    @ApiModelProperty(name = "secretKey", value = "系统生成的密钥值", dataType = "String")
    private String secretKey;


    @Column("enable_flag")
    @ApiModelProperty(name = "enableFlag", value = "是否开启[是/否]", dataType = "String")
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

    @Column("qps")
    @ApiModelProperty(name = "qps", value = "qps", dataType = "Integer")
    private Integer qps;

    @Column("type")
    @ApiModelProperty(name = "type", value = "类型(plugin:插件,api-接口)", dataType = "String")
    private String type;

    @Column("orgName")
    @ApiModelProperty(name = "orgName", value = "单位名称", dataType = "String")
    private String orgName;
}
