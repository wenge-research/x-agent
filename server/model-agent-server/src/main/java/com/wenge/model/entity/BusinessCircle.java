package com.wenge.model.entity;

import lombok.Data;

/**
 * 商圈实体类
 */
@Data
public class BusinessCircle {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 区/县名称
     */
    private String district;

    /**
     * 街道名称
     */
    private String street;

    /**
     * 商圈名称
     */
    private String circleName;

    /**
     * 纬度（保留6位小数）
     */
    private String latitude;

    /**
     * 经度（保留6位小数）
     */
    private String longitude;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}
