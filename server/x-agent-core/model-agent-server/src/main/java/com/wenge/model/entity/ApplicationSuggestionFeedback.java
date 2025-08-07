package com.wenge.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.dto.result.ComponentDto;
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
@Table(value = "application_suggestion_feedback")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApplicationSuggestionFeedback extends FlexModel<ApplicationSuggestionFeedback> {
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    @Column("application_name")
    @ApiModelProperty(value = "应用名称")
    private String applicationName;

    @Column("application_code")
    @ApiModelProperty(value = "应用Code")
    private String applicationCode;

    @Column("type")
    @ApiModelProperty(value = "类型-使用建议,BUG反馈,操作体验,其他反馈")
    private String type;

    @Column("create_user_id")
    @ApiModelProperty(value = "创建人ID")
    private String createUserId;

    @Column("create_user_name")
    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @Column("create_user_phone")
    @ApiModelProperty(value = "创建人手机号码")
    private String createUserPhone;

    @Column("content")
    @ApiModelProperty(value = "'反馈内容'")
    private String content;

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

    @Column("imgs_url")
    @ApiModelProperty(value = "图片附件s")
    private String imgsUrl;


    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Column(ignore = true)
    @ApiModelProperty(value = "删除list")
    private List<Long> delIds;

}