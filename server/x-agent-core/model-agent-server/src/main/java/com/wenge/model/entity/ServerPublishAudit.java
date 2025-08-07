package com.wenge.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.workflow.entity.Component;
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
@Table(value = "server_publish_audit")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ServerPublishAudit extends FlexModel<ServerPublishAudit> {
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

    @Column("publish_type")
    @ApiModelProperty(value = "发布类型(分类)")
    private String publishType;

    @Column("application_type")
    @ApiModelProperty(value = "应用类型 空-问答助手 workflow-工作流 search-智能搜索")
    private String applicationType;

    @Column("create_user_id")
    @ApiModelProperty(value = "(发布人、应用所属人)创建人ID")
    private String createUserId;

    @Column("create_user_name")
    @ApiModelProperty(value = "(发布人、应用所属人)创建人名称")
    private String createUserName;

    @Column("publish_desc")
    @ApiModelProperty(value = "'发布描述'")
    private String publishDesc;

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

    @Column("audit_status")
    @ApiModelProperty(value = "审核状态 2-待审核 1-审核通过（已审核） 0-驳回（已审核）")
    private Integer auditStatus;
    @Column("audit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @Column("audit_user_id")
    @ApiModelProperty(value = "'审核人'")
    private String auditUserId;

    @Column("audit_user_name")
    @ApiModelProperty(value = "'审核人'")
    private String auditUserName;

    @Column("audit_success_content")
    @ApiModelProperty(value = "审核通过内容")
    private String auditSuccessContent;

    @Column("audit_fail_content")
    @ApiModelProperty(value = "审核驳回内容 两个下拉框内容使用【@】进行分割")
    private String auditFailContent;

    @Column("audit_fail_lable_one")
    @ApiModelProperty(value = "审核驳回标签一")
    private String auditFailLableOne;

    @Column("audit_fail_lable_two")
    @ApiModelProperty(value = "审核驳回标签二")
    private String auditFailLableTwo;

    @Column("introduce")
    @ApiModelProperty(value = "应用介绍")
    private String introduce;

    @Column("facade_image_url")
    @ApiModelProperty(value = "logo")
    private String facadeImageUrl;


    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Column(ignore = true)
    @ApiModelProperty(value = "删除list")
    private List<Long> delIds;


    @Column(ignore = true)
    ApplicationInfo applicationInfo;

    @Column(ignore = true)
    ComponentDto component; //插件信息

    @Column(ignore = true)
    List<LlmInfo> llmInfoList;

    @Column(ignore = true)
    List<ApplicationPlugin> applicationPluginList;

    @Column(ignore = true)
    List<KnowledgeInfo> knowledgeInfoList;

    @Column(ignore = true)
    List<ComponentDto> componentInfoList;


    @Column(ignore = true)
    @ApiModelProperty(value = "对话数")
    Long dialogueCount = 0L;

    @Column(ignore = true)
    @ApiModelProperty(value = "收藏数")
    Long collectCount = 0L;









}