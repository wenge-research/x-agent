package com.wenge.model.dto.param.wenhai;

/**
 * MediaArea, 媒体地域信息,详见《字典接口模块》，各个字段取值详见字段说明
 */
@lombok.Data
public class MediaArea {
    /**
     * 媒体所在市,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=省份id,取返回结果id字段值】
     */
    private String mediaAreaCity;
    /**
     * 媒体所在区|县,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=市id,取返回结果id字段值】
     */
    private String mediaAreaCounty;
    /**
     * 媒体所在省份,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=0,取返回结果id字段值】
     */
    private String mediaAreaProvince;
}
