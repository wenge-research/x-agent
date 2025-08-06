package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * ArticleAnalysis, 发文分析相关信息
 */
@lombok.Data
public class ArticleAnalysis {
    /**
     * 分类,详见《字典接口模块》分类列表查询【取返回结果categoryName字段值】,最多支持20个(包含20，总长度100)
     */
    private List<String> categories;
    /**
     * 内容提及地域信息,各个字段取值详见字段说明
     */
    private List<ContentArea> contentAreas;
    /**
     * 语义指纹【用于对相似文章进行排重】
     */
    private String fingerprint;
    /**
     * 语义指纹标记是否簇头【为空默认不构建,1-是、0-否】
     */
    private Long isFingerprintHead;
    /**
     * 是否敏感【为空默认全部，0-否、1-是】
     */
    private Long isSensitive;
    /**
     * 语种,详见《字典接口模块》ISO639语言编码信息列表【取返回结果code字段值】
     */
    private List<String> languages;
    /**
     * 标签信息分类,各个字段取值详见字段说明
     */
    private List<MediaGradeTag> mediaGradeTags;
    /**
     * 内容情感【0-中立、1-正面、2-负面】
     */
    private List<Long> polarities;
    /**
     * 提及人,最多支持20个(包含20，总长度100)
     */
    private List<String> subjectExtract;
    /**
     * 主体情感【格式：汉字-数字，数字只支持0-中立、1-正面、2-负面、3-无关、4-未知】
     */
    private List<String> subjectSentiments;
}
