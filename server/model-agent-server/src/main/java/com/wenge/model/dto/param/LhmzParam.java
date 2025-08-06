package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import lombok.Data;


@Data
public class LhmzParam {


    /**
     * 区域级别
     */
    private String regionLevel;

    /**
     * 产业类型
     */
    private String industryType;

    /**
     * 查询的内容
     */
    private String queryContent;

    private String minTotalArea;         // 建筑面积最小范围
    private String maxTotalArea;         // 建筑面积最大范围

    private String minAreaPerFloor;      // 单层最大范围
    private String maxAreaPerFloor;      // 单层面积最大范围

    /**
     * 用途
     */
    private String purpose;

    private String purposeType;

    /**
     * 是否靠近地铁
     *  是/否
     */
    private String nearbySubway;

    /**
     * 纬度（保留6位小数）
     */
    private String latitude;

    /**
     * 经度（保留6位小数）
     */
    private String longitude;

    private String industryName;          // 产业名称

    private Integer pageNo;

    private Integer pageSize;

    private String order;

    private String sort;

    private String rent;

    /**
     * 应用id
     */
    private String applicationId;
}
