package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wenge.model.dto.result.ContactInfoResult;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@ApiModel
@Data
@Table(value = "lhmz_supermarket_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class SupermarketInfo {

    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("address")
    private String address;

    @Column("contact")
    private String contact;

    @Column("type")
    private String type;

    /**
     * 纬度（保留6位小数）
     */
    @Column("latitude")
    private String latitude;

    /**
     * 经度（保留6位小数）
     */
    @Column("longitude")
    private String longitude;

    @Column("serial_number")
    private String serialNumber;

    @Column(ignore = true)
    private List<ContactInfoResult> contactInfoResults;
}
