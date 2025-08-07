package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wenge.model.dto.result.ContactInfoResult;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 实体类表示工业空间的基本情况。
 */
@ApiModel
@Data
@Table(value = "lhmz_industrial_spaces2", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class IndustrialSpace2 {
    @Column("id")
    private Long id;
    @Column("serial_number")
    private String serialNumber;          // 序号
    @Column("community_name")
    private String communityName;         // 所属社区
    @Column("collective_property")
    private String collectiveProperty;    // 集体物业
    @Column("industry_name")
    private String industryName;          // 产业名称
    @Column("address_detail")
    private String addressDetail;         // 地址详情
    @Column("contact_person")
    private String contactPerson;         // 联系人
    @Column("contact_information")
    private String contactInformation;    // 联系方式
    @Column("building_count")
    private Integer buildingCount;        // 楼栋总数
    @Column("total_area")
    private String totalArea;         // 建筑面积
    @Column("area_per_floor")
    private String areaPerFloor;      // 单层面积
    @Column("vacant_area")
    private String vacantArea;        // 园区空置总面积
    @Column("purpose")
    private String purpose;               // 用途
    @Column("rent")
    private String rent;              // 租金

    @Column("industry_pic")
    private String industryPic;

    /**
     * 纬度（保留6位小数）
     */
    @Column("latitude")
    private String latitude;

    /**
     * 是否靠近地铁
     *  是/否
     */
    @Column("nearby_subway")
    private String nearbySubway;

    /**
     * 经度（保留6位小数）
     */
    @Column("longitude")
    private String longitude;

    @Column(ignore = true)
    private List<ContactInfoResult> contactInfoResults;
}