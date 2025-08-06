package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 实体类表示在售房地产项目的统计信息。
 */
@ApiModel
@Data
@Table(value = "lhmz_estate_sales", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class RealEstateSale {

    /**
     * 序号，主键ID，自增。
     */
    @Column("id")
    private Long id;

    @Column("index")
    private Long index;

    /**
     * 在售项目名称，不能为空。
     */
    @Column("project_name")
    private String projectName;

    /**
     * 单位名称，不能为空。
     */
    @Column("unit_name")
    private String unitName;

    /**
     * 销售时间，不能为空。
     */
    @Column("sales_date")
    private String salesDate;

    /**
     * 销售套数，不能为空。
     */
    @Column("number_of_units_sold")
    private String numberOfUnitsSold;

    /**
     * 总建筑面积（平方米），不能为空。
     */
    @Column("total_floor_area")
    private String totalFloorArea;
}
