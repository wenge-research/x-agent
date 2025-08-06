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
@Table(value = "application_publish_record", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ApplicationPublishRecord extends FlexModel<ApplicationPublishRecord> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id",value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId",value = "应用ID/插件ID", dataType = "String")
    private String applicationId;

    @Column("publish_desc")
    @ApiModelProperty(name = "publishDesc",value = "发布描述", dataType = "String")
    private String publishDesc;

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

    @Column("message_source")
    @ApiModelProperty(value = "来源 1-应用 2-插件 3-其他")
    private Integer messageSource;


    @Column(ignore = true)
    private QueryWrapper currentWrapper;
}
