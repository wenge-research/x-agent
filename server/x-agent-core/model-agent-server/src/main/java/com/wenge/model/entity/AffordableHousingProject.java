package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类表示保障性住房项目的详细信息。
 */
@ApiModel
@Data
@Table(value = "lhmz_affordable_housing", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class AffordableHousingProject {

    /**
     * 序号，主键ID，自增。
     */
    @Column("id")
    private Long id;

    /**
     * 项目名称，不能为空。
     */
    @Column("project_name")
    private String projectName;

    /**
     * 竣工时间，不能为空。
     */
    @Column("completion_date")
    private String completionDate;

    /**
     * 地址，不能为空。
     */
    @Column("address")
    private String address;

    /**
     * 住房类型，不能为空。
     */
    @Column("housing_type")
    private String housingType;

    /**
     * 套数，不能为空。
     */
    @Column("number_of_units")
    private String numberOfUnits;

    /**
     * 总建筑面积（万平方米），不能为空。
     */
    @Column("total_floor_area")
    private String totalFloorArea;
}
