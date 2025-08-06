package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.model.constants.MybatisFileConstant;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import lombok.Data;

@Data
@Table(value = "appointment_consultation", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
public class AppointmentConsultation {

    /**
     * 自增主键ID
     */
    @Id(value = "id", keyType = KeyType.Auto)
    private Long id;

    /**
     * 预约咨询单号ID(32位)
     */
    @Column("consult_id")
    private String consultId;

    /**
     * 咨询人姓名
     */
    @Column("name")
    private String name;

    /**
     * 咨询人电话
     */
    @Column("phone")
    private String phone;

    /**
     * 咨询人邮箱
     */
    @Column("email")
    private String email;

    /**
     * 公司名称
     */
    @Column("company_name")
    private String companyName;

    /**
     * 公司主页
     */
    @Column("company_website")
    private String companyWebsite;

    /**
     * 公司职务
     */
    @Column("position")
    private String position;

    /**
     * 公司类型（初创公司、小微公司、中型公司、大型公司、国有企业、政府部门）
     */
    @Column("company_type")
    private Integer companyType;

    /**
     * 业务场景
     */
    @Column("business_scenarios")
    private String businessScenarios;

    /**
     * 创建时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 完成状态（0-未完成，1-进行中，2-已完成）
     */
    @Column("status")
    private Integer status;

    /**
     * 处理人
     */
    @Column("handler")
    @OnFieldFill(fill = FieldFill.UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String handler;

    /**
     * 处理人id
     */
    @Column("handler_id")
    @OnFieldFill(fill = FieldFill.UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_ID)
    private String handlerId;

    /**
     * 更新时间
     */
    @Column("update_time")
    private String updateTime;
}
