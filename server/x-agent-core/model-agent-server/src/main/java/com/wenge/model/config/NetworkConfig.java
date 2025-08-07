package com.wenge.model.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "network")
@ApiModel
@Data
public class NetworkConfig {

    /**
     * api
     */
    @ApiModelProperty(name = "api", value = "api地址", dataType = "String")
    private String api;

    /**
     * 联网检索时：返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容
     */
    @ApiModelProperty(name = "dataType", value = "联网检索时：返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容", dataType = "String")
    private String dataType;

    /**
     * 联网检索时：返回top N条数据，大于1，可为空或不传
     */
    @ApiModelProperty(name = "top", value = "联网检索时：返回top N条数据，大于1，可为空或不传", dataType = "Integer")
    private Integer top;

    /**
     * 联网检索时：是否需要浏览器渲染，默认 0 否  ，1 是(速度慢)
     */
    @ApiModelProperty(name = "isRender", value = "联网检索时：是否需要浏览器渲染，默认 0 否  ，1 是(速度慢)", dataType = "Integer")
    private Integer isRender;

    /**
     * 搜索引擎,toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)
     */
    @ApiModelProperty(name = "engine", value = "搜索引擎,toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)", dataType = "String")
    private String engine;

    /**
     * 前缀词语
     */
    @ApiModelProperty(name = "prefix", value = "前缀词语", dataType = "String")
    private String prefix;

    /**
     * 后缀词语
     */
    @ApiModelProperty(name = "suffix", value = "后缀词语", dataType = "String")
    private String suffix;

    /**
     * 联网的api提供者，YAYI,metasearch
     */
    @ApiModelProperty(name = "apiProvider", value = "联网的api提供者", dataType = "String")
    private String apiProvider;

    /**
     * 是否缓存
     */
    @ApiModelProperty(name = "cacheEnable", value = "是否缓存", dataType = "Boolean")
    private Boolean cacheEnable;

}
