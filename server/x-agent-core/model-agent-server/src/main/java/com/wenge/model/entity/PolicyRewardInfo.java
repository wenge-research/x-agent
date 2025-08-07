package com.wenge.model.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@Table(value = "lhmz_policy_reward_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class PolicyRewardInfo {

    @Column("id")
    private Long id;
//    @Column("reward_name") // 奖项名称
//    private String rewardName;
    @Column("serial_number")
    private String serialNumber;          // 序号
    @Column("region")
    private String region;                // 区域
    @Column("region_level")
    private String regionLevel;             // 区域级别
    @Column("main_award")
    private String mainAward;             // 主要奖项
    @Column("reward_amount")
    private String rewardAmount;          // 奖励金额（万元）
    @Column("reward_details")
    private String rewardDetails;         // 奖励细则
    @Column("notes")
    private String notes;                 // 注意事项
    @Column("before_after")
    private String beforeAfter;           // 事前/事后
    @Column("specific_conditions")
    private String specificConditions;    // 具体条件
    @Column("receiving_unit")
    private String receivingUnit;         // 受理单位
    @Column("industry_type")
    private String industryType;          // 产业类型
}
