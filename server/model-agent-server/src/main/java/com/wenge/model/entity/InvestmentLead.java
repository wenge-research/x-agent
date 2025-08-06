package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@Table(value = "lhmz_investment_lead", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class InvestmentLead {

    @Column("id")
    private Long id;

    // 投资方名称
    @Column("investor_name")
    private String investorName;

    // 投资意向概况
    @Column("investment_intent_overview")
    private String investmentIntentOverview;

    // 计划总投资
    @Column("planned_total_investment")
    private Double plannedTotalInvestment;

    // 投资联系人
    @Column("investment_contact_person")
    private String investmentContactPerson;

    // 联系方式
    @Column("contact_information")
    private String contactInformation;

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

    @Column("application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
    private String applicationId;

    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deleteFlag", value = "是否删除 0-否 1-是", dataType = "Integer")
    private Integer deleteFlag;


}
