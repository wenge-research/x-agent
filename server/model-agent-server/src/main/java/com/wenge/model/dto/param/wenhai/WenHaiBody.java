// WenHaiBody.java

package com.wenge.model.dto.param.wenhai;

/**
 * SearchDTO, 发文检索入参
 */
@lombok.Data
public class WenHaiBody {
    private AccountMatch accountMatchDTO;
    /**
     * IP归属地所在国家+IP归属地所在省+内容提及地域信息+媒体国家+媒体地域信息+账号注册地域信息+签到地域信息构建关系【默认AND关系,1-AND、2-OR】
     */
    private String areaType;
    private ArticleAnalysis articleAnalysis;
    private ArticleInfo articleInfo;
    private ArticlePlatform articlePlatform;
    private ArticleUser articleUser;
    private BusinessScreenDTO businessDTO;
    /**
     * 全局有序ID【<span style='color:red;'>注意</span>：配合拉取数据使用防止数据遗漏,只有Gid排序该字段才会生效】
     */
    private Long gid;
    private HiddenMark hiddenMark;
    private BasePage pageInfo;
    private BaseSort sortInfo;
}
