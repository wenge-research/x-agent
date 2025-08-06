package com.wenge.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用快捷指令表
 */
@ApiModel
@Data
@Table(value = "application_quick_command", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApplicationQuickCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键", dataType = "Long")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
    private String applicationId;

    @Column("command_id")
    @ApiModelProperty(name = "commandId", value = "指令id，有业务作用", dataType = "String")
    private String commandId;

    @Column("command_type")
    @ApiModelProperty(name = "commandType", value = "指令类型", dataType = "String")
    private String commandType;

    @Column("command_name")
    @ApiModelProperty(name = "commandName", value = "指令名称", dataType = "String")
    private String commandName;

    @Column("command_content")
    @ApiModelProperty(name = "commandContent", value = "指令内容", dataType = "String")
    private String commandContent;

    @Column("status")
    @ApiModelProperty(name = "status", value = "指令状态 0-未开启 1-开启", dataType = "Integer")
    private Integer status;

    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deleteFlag", value = "是否删除 0-否 1-是", dataType = "Integer")
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 创建时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

}
