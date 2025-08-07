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
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "matter_guide_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class MatterGuideInfo {
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("matter_id")
    @ApiModelProperty(value = "事项id")
    private String matterId;

    @Column("info_id")
    @ApiModelProperty(value = "数据ID")
    private String infoId;

    @Column("user_id")
    @ApiModelProperty(value = "用户ID")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String userId;

    @Column("user_name")
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @Column("id_card")
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @Column("phone")
    @ApiModelProperty(value = "联系电话")
    private String phone;

    @Column("status")
    @ApiModelProperty(value = "0 未审批  1 已审批")
    private Integer status;

    @Column("remark")
    @ApiModelProperty(value = "审批描述信息")
    private String remark;

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