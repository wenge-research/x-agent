package com.wenge.model.dto.param.wenhai;

/**
 * ContentArea, 媒体地域信息,详见《字典接口模块》,各个字段取值详见字段说明
 */
@lombok.Data
public class ContentArea {
    /**
     * 内容地域提及市,详见《字典接口模块》内容提及-指定区域父级节点查询子集接口【level=3、pid=省份id,取返回结果id字段值】
     */
    private String contentAreaCity;
    /**
     * 内容地域提及国家,详见《字典接口模块》内容提及-指定区域父级节点查询子集接口【level=1、pid=1,取返回结果id字段值】
     */
    private String contentAreaCountry;
    /**
     * 内容地域提及区|县,详见《字典接口模块》内容提及-指定区域父级节点查询子集接口【level=4、pid=市id,取返回结果id字段值】
     */
    private String contentAreaCounty;
    /**
     * 内容地域提及省份,详见《字典接口模块》内容提及-指定区域父级节点查询子集接口【level=2、pid=国家id,取返回结果id字段值】
     */
    private String contentAreaProvince;
}
