package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * ArticleInfo, 发文相关信息
 */
@lombok.Data
public class ArticleInfo {
    /**
     * 数据类型【0-对话、1-原创、2-转发、3-评论】
     */
    private List<Long> articleTypes;
    /**
     * 作者,最多支持20个(包含20，总长度300)
     */
    private List<String> author;
    /**
     * 渠道/媒体类型,详见《字典接口模块》指定境内外类型查询媒体类型接口【取返回结果code字段值】
     */
    private List<String> dataSources;
    private EsInsertTime esInsertTime;
    private InsertTime insertTime;
    /**
     * IP归属地所在国家,详见《字典接口模块》国家三字英文码值对照列表【取返回结果pek字段值】
     */
    private List<String> ipAreaCountry;
    /**
     * IP归属地所在省,详见《字典接口模块》指定区域父级节点查询信息【取返回结果id字段值】
     */
    private List<String> ipAreaProvinces;
    /**
     * 是否为广告数据【0-否、1-是】
     */
    private Long isAdvertisement;
    /**
     * 是否付费,【空/-1默认全部、-1-全部、0-免费、1-付费】</br><span
     * style='color:red;'>注意</span>：付费数据是需开通权限，如果未开权限数据会返回空，免费数据默认都有。
     */
    private Long isPaid;
    /**
     * 匹配方式【默认文本匹配,1-文本(正文|标题)、2-OCR、3-语音、4-场景、5-人脸、6-标题、7-正文(内容)】
     */
    private List<Long> matchMethods;
    /**
     * 发文类型匹配关系【0-任意包含，1-必须包含】，默认为任意包含
     */
    private Long mediaTypeMatch;
    /**
     * 发文类型【1-文本、2-图文、3-视频】
     */
    private List<Long> mediaTypes;
    /**
     * 根博文id集合【最多支持10个root_article_id】
     */
    private List<String> rootArticleIds;
    private SearchMode searchMode;
    private SearchTime searchTime;
    /**
     * 签到地域信息,详见《字典接口模块》，各个字段取值详见字段说明
     */
    private List<SignInArea> signInAreas;
    /**
     * 话题标签,最多支持20个(包含20，总长度300)
     */
    private List<String> topicTags;
    /**
     * 发文链接【最多支持5个url】
     */
    private List<String> urls;
}
