package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类表示工业空间的基本情况。
 */
@ApiModel
@Data
@Table(value = "lhmz_industrial_spaces", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class IndustrialSpace {

    /**
     * 主键ID，自增。
     */
    @Column("id")
    private Long id;

    /**
     * 园区（写字楼）名称，不能为空。
     */
    @Column("building_name")
    private String buildingName;

    /**
     * 地址，不能为空。
     */
    @Column("address")
    private String address;

    /**
     * 建筑面积（平方米），不能为空。
     */
    @Column("total_area")
    private String totalArea;

    /**
     * 空置面积（平方米），不能为空。
     */
    @Column("vacant_area")
    private String vacantArea;

    /**
     * 租金（元），不能为空。
     */
    @Column("rent")
    private String rent;

    /**
     * 联系人，不能为空。
     */
    @Column("contact_person")
    private String contactPerson;

    /**
     * 联系电话，不能为空。
     */
    @Column("contact_phone")
    private String contactPhone;

    /**
     * 类别，不能为空。
     */
    @Column("category")
    private String category;
}