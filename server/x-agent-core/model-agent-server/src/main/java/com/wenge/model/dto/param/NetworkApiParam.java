package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class NetworkApiParam implements Serializable {

    private static final long serialVersionUID = 5061071337196651264L;

    /**
     * 回调接口地址，默认空
     */
    private String callbackurl;

    /**
     * 返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容
     */
    private String data_type;

    /**
     * 搜索平台：toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)
     */
    private String engine;

    /**
     * 是否需要浏览器渲染，默认 0 否  ，1 是(速度慢)
     */
    private Integer is_render;

    /**
     * 采集第几页，默认1，小于等于20
     */
    private Long page;

    /**
     * 请求id，默认为空
     */
    private String requestid;

    /**
     * 返回top N条数据，大于1，可为空或不传
     */
    private Integer top;

    /**
     * 关键词
     */
    private String word;
}
