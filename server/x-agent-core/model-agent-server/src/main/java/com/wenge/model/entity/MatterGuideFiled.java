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
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "matter_guide_filed")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MatterGuideFiled extends FlexModel<MatterGuideFiled> {
    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("matter_id")
    @ApiModelProperty(value = "事项ID")
    private String matterId;

    @Column("filed_id")
    @ApiModelProperty(value = "字段ID")
    private String filedId;

    @Column("filed_Name")
    @ApiModelProperty(value = "字段中文名")
    private String filedName;

    @Column("filed_code")
    @ApiModelProperty(value = "字段名")
    private String filedCode;

    @Column("filed_code_group")
    @ApiModelProperty(value = "字段名分组名称")
    private String filedCodeGroup;

    @Column("filed_code_group_id")
    @ApiModelProperty(value = "字段名分组ID")
    private String filedCodeGroupId;

    @Column("required_flag")
    @ApiModelProperty(value = "是否必填，是/否")
    private String requiredFlag;

    @Column("required_tips")
    @ApiModelProperty(value = "必填但是没有填写时的提示语")
    private String requiredTips;

    @Column("form_type")
    @ApiModelProperty(value = "表单类型，input/textarea/select/radio/checkbox/date/time/datetime/file/image/video/audio/number/email/url/tel/password/hidden/button/submit/reset/text")
    private String formType;

    @Column("word_limit")
    @ApiModelProperty(value = "填写字数限制")
    private Integer wordLimit;

    @Column("tip")
    @ApiModelProperty(value = "提示语")
    private String tip;

    @Column("form_show_flag")
    @ApiModelProperty(value = "表单是否显示，是/否")
    private String formShowFlag;

    @Column("sorted")
    @ApiModelProperty(value = "排序")
    private Integer sorted;

    @Column("placeholder")
    @ApiModelProperty(value = "占位符提示")
    private String placeholder;

    @Column("check_rule_code")
    @ApiModelProperty(value = "规则校验码")
    private String checkRuleCode;

    @Column("table_show_flag")
    @ApiModelProperty(value = "列表是否显示(是/否)")
    private String tableShowFlag;

    @Column("export_flag")
    @ApiModelProperty(value = "是否导出(是/否)")
    private String exportFlag;

    @Column("search_flag")
    @ApiModelProperty(value = "是否检索(是/否)")
    private String searchFlag;

    @Column("search_type")
    @ApiModelProperty(value = "检索类型(1-精确匹配，2-模糊匹配，3-多选精确匹配)")
    private String searchType;

    @Column("unique_flag")
    @ApiModelProperty(value = "是否加入唯一(是/否)")
    private String uniqueFlag;

    @Column("web_form_status")
    @ApiModelProperty(value = "后台配置的状态(1-可编辑，0-不可编辑)")
    private String webFormStatus;

    @Column("client_form_status")
    @ApiModelProperty(value = "客户端的状态(1-可编辑，0-不可编辑)")
    private String clientFormStatus;

    @Column(value = "deleted_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deletedFlag", value = "deletedFlag", dataType = "Integer")
    private Integer deletedFlag;


    @Column(ignore = true)
    @ApiModelProperty(value = "选项")
    private List<MatterGuideOption> optionList;


    @Column(ignore = true)
    @ApiModelProperty(value = "检索条件中的value值-前端传入")
    private String searchValue;
    @Column(ignore = true)
    @ApiModelProperty(value = "检索条件中的value值-前端传入")
    private String searchValue2;

}