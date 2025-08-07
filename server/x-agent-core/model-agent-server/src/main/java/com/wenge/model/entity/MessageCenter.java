package com.wenge.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@ApiModel
@Data(staticConstructor = "create")
@Table(value = "message_center")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MessageCenter extends FlexModel<MessageCenter> {
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    @Column("application_name")
    @ApiModelProperty(value = "应用名称")
    private String applicationName;

    @Column("plugin_id")
    @ApiModelProperty(value = "插件ID")
    private String pluginId;

    @Column("plugin_name")
    @ApiModelProperty(value = "插件名称")
    private String pluginName;


    @Column("message_source")
    @ApiModelProperty(value = "消息来源 1-应用审核 2-插件审核 3-其他")
    private Integer messageSource;

    @Column("message_type")
    @ApiModelProperty(value = "消息类型 0-审核通知  2-其他消息")
    private Integer messageType;


    @Column("message_content")
    @ApiModelProperty(value = "消息内容")
    private String messageContent;

    @Column("remark")
    @ApiModelProperty(value = "备注说明")
    private String remark;

    @Column(value = "status", isLogicDelete = true)
    @ApiModelProperty(value = "0-有效  1-删除")
    private Integer status;

    @Column("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private Date createTime;

    @Column("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private Date updateTime;

    @Column("message_user_id")
    @ApiModelProperty(value = "消息推送用户ID")
    private String messageUserId;

    @Column("message_viewer_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "已读时间")
    private Date messageViewerTime;

    @Column("message_status")
    @ApiModelProperty(value = "消息状态 0-未读(默认) 1-已读")
    private Integer messageStatus;




    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Column(ignore = true)
    @ApiModelProperty(value = "删除list")
    private List<Long> delIds;


}