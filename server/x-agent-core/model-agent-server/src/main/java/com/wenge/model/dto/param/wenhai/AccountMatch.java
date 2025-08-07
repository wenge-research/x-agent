package com.wenge.model.dto.param.wenhai;

/**
 * AccountMatch, 自定义匹配方式入参</br><span
 * style='color:red;'>注意</span>：支持根据站点名称、账号名称等六个字段进行检索</br><span
 * style='color:red;'>明确需求，需要用到哪些字段，匹配的字段越少越好，否则浪费系统资源！</span></br>账号匹配方式matchType可以对输入的关键词进行精确匹配或模糊匹配</br>默认操作符defaultOperator可以决定不同关键词间的关系是and或or
 */
@lombok.Data
public class AccountMatch {
    /**
     * 作者【支持关键词长度最大100】
     */
    private String authors;
    /**
     * 站点渠道名称【支持关键词长度最大100】
     */
    private String channelNames;
    /**
     * 个性化账号id【支持关键词长度最大100】
     */
    private String customId;
    /**
     * 默认操作符【空/0默认AND、1-OR】
     */
    private Long defaultOperator;
    /**
     * 域名【支持关键词长度最大100】
     */
    private String host;
    /**
     * 账号匹配方式【0-精确匹配、1-模糊匹配】
     */
    private Long matchType;
    /**
     * 用户第二uid【支持关键词长度最大100】,账号检索可用，发文检索不可用
     */
    private String secUid;
    /**
     * 子站点名称【支持关键词长度最大100】
     */
    private String subWebsiteNames;
    /**
     * 账号ID【支持关键词长度最大100】
     */
    private String userIds;
    /**
     * 账号名称【支持关键词长度最大100】
     */
    private String userNames;
    /**
     * 站点名称【支持关键词长度最大100】
     */
    private String websiteNames;
}
