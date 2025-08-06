package com.wenge.model.dto.param.wenhai;

/**
 * SignInArea, 签到地域信息,详见《字典接口模块》，各个字段取值详见字段说明
 */
@lombok.Data
public class SignInArea {
    /**
     * 签到市,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=省份id,取返回结果id字段值】
     */
    private String signInCity;
    /**
     * 签到国家,详见《字典接口模块》国家三字英文码值对照列表【取返回结果pek字段值】
     */
    private String signInCountry;
    /**
     * 签到区|县,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=市id,取返回结果id字段值】
     */
    private String signInCounty;
    /**
     * 签到省份,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=0,取返回结果id字段值】
     */
    private String signInProvince;
}
