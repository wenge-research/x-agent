package com.wenge.model.dto.param.wenhai;

/**
 * MediaInfo, 媒体信息对象入参</br><span
 * style='color:red;'>注意</span>：,详见分页检索账号信息列表接口【如果是长文本的建议把接口返回的platform下所有字段对应传入，社交额外带上账号ID值】
 */
@lombok.Data
public class MediaInfo {
    /**
     * 频道名称,详见分页检索账号信息列表接口【取返回结果platform.channelName字段值】
     */
    private String channelName;
    /**
     * 渠道编码,详见分页检索账号信息列表接口【取返回结果platform.dataSourceType字段值】
     */
    private String dataSource;
    /**
     * 一级域名,详见分页检索账号信息列表接口【取返回结果platform.host字段值】
     */
    private String host;
    /**
     * 域名类型,1、主域名  2、子域名，详见分页检索账号信息列表接口【取返回结果platform.hostType字段值】
     */
    private String hostType;
    /**
     * 指定媒体true 包含/false 不包含（针对账号相关）默认 true
     */
    private Boolean include;
    /**
     * 媒体类型,1、站点 2、频道 3、账号 4、群组，详见分页检索账号信息列表接口【取返回结果platform.mediaType字段值】
     */
    private String mediaType;
    /**
     * 二级域名,详见分页检索账号信息列表接口【取返回结果platform.subHost字段值】
     */
    private String subHost;
    /**
     * 用户id,（针对账号相关），详见分页检索账号信息列表接口【取返回结果userId字段值】
     */
    private String userId;
    /**
     * 用户名称,（针对账号相关），详见分页检索账号信息列表接口【取返回结果userName字段值】
     */
    private String userName;
    /**
     * 站点名称,详见分页检索账号信息列表接口【取返回结果platform.websiteName字段值】
     */
    private String websiteName;
}
