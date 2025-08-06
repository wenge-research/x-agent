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

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/8/16 14:23
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "matter_guide_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MatterGuideType extends FlexModel<MatterGuideType> {
    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column(ignore = true)
    @ApiModelProperty(value = "主键，自增id转为字符串类型，没有业务作用")
    private String idx;

    @Column("name")
    @ApiModelProperty(value = "类型名称")
    private String name;


    @Column("remark")
    @ApiModelProperty(value = "备注说明")
    private String remark;

    @Column("status")
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

}