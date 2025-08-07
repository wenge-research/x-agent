package com.wenge.model.dto.param.wenhai;

/**
 * UserRegisterArea, 账号注册地域信息,具体详见《字典接口模块》，各个字段取值详见字段说明
 */
@lombok.Data
public class UserRegisterArea {
    /**
     * 账号注册地市,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=省份id,取返回结果id字段值】
     */
    private String userCity;
    /**
     * 账号注册地国家,详见《字典接口模块》国家三字英文码值对照列表【取返回结果pek字段值】
     */
    private String userCountry;
    /**
     * 账号注册地区|县,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=市id,取返回结果id字段值】
     */
    private String userCounty;
    /**
     * 账号注册地省份,详见《字典接口模块》指定区域父级节点查询子集(中国)【pid=0,取返回结果id字段值】
     */
    private String userProvince;
}
