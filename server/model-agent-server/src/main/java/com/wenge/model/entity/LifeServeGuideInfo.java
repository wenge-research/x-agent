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

/**
 * @Description: life_serve_guide_info
 * @Author: zs
 * @Date: 2024/10/11 14:12
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "life_serve_guide_info")
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public class LifeServeGuideInfo extends FlexModel<LifeServeGuideInfo> {

    public LifeServeGuideInfo() {
    }
    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    @Column("name")
    @ApiModelProperty(value = "服务名称")
    private String name;

    @Column("brief_introduce")
    @ApiModelProperty(value = "服务简介")
    private String briefIntroduce;

    @Column("detail_url")
    @ApiModelProperty(value = "服务详情url")
    private String detailUrl;

    @Column("intercept_word_id")
    @ApiModelProperty(value = "拦截词主键id")
    private Long interceptWordId;

    @Column("handle_method_id")
    @ApiModelProperty(value = "敏感词处理方式id")
    private Long handleMethodId;

    @Column("matter_guide_id")
    @ApiModelProperty(value = "事项指南id")
    private String matterGuideId;

    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "更新时间", dataType = "String")
    private String createTime;

    @Column(value = "update_user_id")
    @ApiModelProperty(name = "updateUserId", value = "修改人ID", dataType = "String")
    private String updateUserId;

    @Column(value = "create_user_id")
    @ApiModelProperty(name = "createUserId", value = "创建人ID", dataType = "String")
    private String createUserId;

    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deleteFlag", value = "deleteFlag", dataType = "Integer")
    private Integer deleteFlag;

}