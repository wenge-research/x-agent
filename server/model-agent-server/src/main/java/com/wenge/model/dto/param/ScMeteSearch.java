package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScMeteSearch extends WGParam {

    private static final long serialVersionUID = 7550703708700886555L;

    /**
     * 关键词（必需）
     * 示例值："string"
     */
    private String word;

    /**
     * 搜索平台（必需）
     * 境内支持：今日头条-toutiao、百度网页-baidu、Bing国内版-bing；
     * 境外支持：Google网页-google、Google新闻-google_news
     * 示例值："string"
     */
    private String engine;

    /**
     * 分页页码（可选）
     * 范围：1~20
     * 示例值：1
     */
    private Integer page;

    /**
     * 请求ID（可选）
     * 示例值："string"
     */
    private String requestId;

    /**
     * 回调接口地址（可选）
     * 示例值："string"
     */
    private String callbackURL;

    /**
     * 返回数据类型（可选）
     * 可选值：
     * - snapshot：快照（不采详情页）
     * - snapshot_and_context：快照和内容
     * 示例值："string"
     */
    private String dataType;

    /**
     * 返回top N条数据（可选）
     * 必须大于1，可为空或不传
     * 范围：2~20
     * 示例值：2
     */
    private Integer top;

    /**
     * 是否需要浏览器渲染（可选）
     * 0-否，1-是（速度慢）
     * 示例值：0
     */
    private Integer isRender;

    /**
     * 列表页发布时间提取失败时是否需要赋值当前时间（可选）
     * 0-否，1-是
     * 示例值：0
     */
    private Integer complete_time;

    /**
     * 是否境内外媒体（必需）
     * 0-境内，1-境外
     * 示例值：0
     */
    private Integer isForeignMedia;

}
