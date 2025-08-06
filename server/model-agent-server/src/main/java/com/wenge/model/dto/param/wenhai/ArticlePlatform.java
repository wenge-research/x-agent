package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * ArticlePlatform, 发文站点相关信息
 */
@lombok.Data
public class ArticlePlatform {
    /**
     * 媒体国家,详见《字典接口模块》国家三字英文码值对照列表【取返回结果pek字段值】,最多支持20个(包含20，总长度100)
     */
    private List<String> country;
    /**
     * 排除媒体标签,详见《字典接口模块》媒体信源标签信息列表【取返回结果tagName字段值】,最多支持20个(包含20，总长度100)</br><span
     * style='color:red;'>注意</span>：articlePlatform.mediaTags与articlePlatform.excludeMediaTags字段同时有值，articlePlatform.excludeMediaTags不会生效不能同时存在，其中一个可不传
     */
    private List<String> excludeMediaTags;
    /**
     * 是否境内外媒体【0-境内、1-境外(外国媒体+港澳台媒体)、-1-境内外(全部)】
     */
    private Long isForeignMedia;
    /**
     * 媒体地域信息,详见《字典接口模块》,各个字段取值详见字段说明
     */
    private List<MediaArea> mediaAreas;
    /**
     * 指定媒体组,</br><span
     * style='color:red;'>注意</span>：详见分页检索账号信息列表接口【如果是长文本的建议把接口返回的platform下所有字段对应传入，社交额外带上账号ID值，若单独指定渠道检索不生效(查询全部渠道)，若想生效请使用articleInfo.dataSources字段】
     */
    private List<MediaInfo> mediaInfos;
    /**
     * 媒体标签,详见《字典接口模块》媒体信源标签信息列表【取返回结果tagName字段值】,最多支持20个(包含20，总长度100)</br><span
     * style='color:red;'>注意</span>：articlePlatform.mediaTags与articlePlatform.excludeMediaTags字段同时有值，articlePlatform.excludeMediaTags不会生效不能同时存在，其中一个可不传
     */
    private List<String> mediaTags;
}
