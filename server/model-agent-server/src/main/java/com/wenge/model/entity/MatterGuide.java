package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wg.appframe.mybatisflex.core.FlexModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "matter_guide")
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public class MatterGuide extends FlexModel<MatterGuide> {

    public MatterGuide() {
    }

    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    /**
     * 主键，自增id，没有业务作用
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    /**
     * 应用ID
     */
    @Column("application_id")
    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    /**
     * 事项ID
     */
    @Column("matter_id")
    @ApiModelProperty(value = "事项ID")
    private String matterId;

    /**
     * 事项名称
     */
    @Column("matter_name")
    @ApiModelProperty(value = "事项名称")
    private String matterName;

    /**
     * 事项描述
     */
    @Column("matter_desc")
    @ApiModelProperty(value = "事项描述")
    private String matterDesc;

    /**
     * 事项路由
     */
    @Column("matter_route")
    @ApiModelProperty(value = "事项路由")
    private String matterRoute;

    /**
     * 事项类型
     */
    @Column("matter_type")
    @ApiModelProperty(value = "事项类型")
    private String matterType;

    @Column("matter_type_id")
    @ApiModelProperty(value = "事项类型Id")
    private Long matterTypeId;


    /**
     * 开场白
     */
    @Column("prompt")
    @ApiModelProperty(value = "开场白")
    private String prompt;

    /**
     * 系统提示词
     */
    @Column("extra_system_prompt")
    @ApiModelProperty(value = "系统提示词")
    private String extraSystemPrompt;

    /**
     * 入口提示语
     */
    @Column("enter_tip")
    @ApiModelProperty(value = "入口提示语")
    private String enterTip;

    /**
     * 填写提示语
     */
    @Column("fill_tip")
    @ApiModelProperty(value = "填写提示语")
    private String fillTip;

    /**
     * 填完时的提示语
     */
    @Column("complete_fill_tip")
    @ApiModelProperty(value = "填完时的提示语")
    private String completeFillTip;

    /**
     * 图标地址
     */
    @Column("icon_url")
    @ApiModelProperty(value = "图标地址")
    private String iconUrl;

    /**
     * 声明
     */
    @Column("statement")
    @ApiModelProperty(value = "声明")
    private String statement;

    /**
     * 处理方式
     */
    @Column("processing")
    @ApiModelProperty(name = "processing", value = "处理方式", dataType = "String")
    private String processing;

    /**
     * 是否显示，是/否
     */
    @Column("show_flag")
    @ApiModelProperty(value = "是否显示，是/否")
    private String showFlag;


    /**
     * deleteFlag
     */
    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deleteFlag", value = "deleteFlag", dataType = "Integer")
    private Integer deleteFlag;

    /**
     * 讨论主题
     */
    @Column("subject")
    @ApiModelProperty(value = "讨论主题")
    private String subject;

    @Column("subject_flag")
    @ApiModelProperty(value = "是否讨论话题")
    private Integer subjectFlag;

    /**
     * 租户ID
     */
    @Column(value = "tenant_id")
    @ApiModelProperty(name = "tenantId", value = "租户ID", dataType = "String")
    private String tenantId;

    @Column(ignore = true)
    @ApiModelProperty(value = "字段")
    private List<MatterGuideFiled> matterGuideFiledList;


    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "更新时间", dataType = "String")
    private String createTime;


    @Column(value = "update_user_id")
    @ApiModelProperty(name = "updateUserId", value = "修改人ID", dataType = "String")
    private String updateUserId;


    @Column(value = "create_user_id")
    @ApiModelProperty(name = "createUserId", value = "创建人ID", dataType = "String")
    private String createUserId;

    @Column(value = "create_user_name")
    @ApiModelProperty(name = "createUserName", value = "创建人", dataType = "String")
    private String createUserName;

    @Column(value = "application_name")
    @ApiModelProperty(name = "applicationName", value = "应用名称", dataType = "String")
    private String applicationName;

    @Column(value = "display")
    @ApiModelProperty(name = "display", value = "展示形式，[1：侧拉流程，2：弹框表单，3：内嵌卡片，4.内嵌按钮]", dataType = "String")
    private String display;

    @Column(value = "alias_name")
    @ApiModelProperty(name = "aliasName", value = "事项别名", dataType = "String")
    private String aliasName;

    @Column(ignore = true)
    @ApiModelProperty(name = "matterTypeName", value = "事项类型名称", dataType = "String")
    private String matterTypeName;

    @Column(ignore = true)
    @ApiModelProperty(name = "matterTypeName", value = "事项类型名称", dataType = "String")
    private String sceneId;

    @Column(ignore = true)
    @ApiModelProperty(name = "sorted", value = "执行顺序", dataType = "String")
    private Integer sorted;

    @Column(ignore = true)
    @ApiModelProperty(name = "groupName", value = "分组名", dataType = "String")
    private String groupName;


}