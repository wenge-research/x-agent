package com.wenge.model.dto.param.wenhai;

/**
 * BusinessScreenDTO, 业务筛选信息
 */
@lombok.Data
public class BusinessScreenDTO {
    private FilterSearch filterSearch;
    /**
     * 是否排重【默认不排重,true.排重、false.不排重】
     */
    private Boolean isCollapse;
    /**
     * 是否高亮【默认高亮,true-高亮、false-不高亮】
     */
    private Boolean isHighLight;
    private SecondSearch secondSearch;
}
