package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wg.appframe.mybatisflex.core.FlexModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Description: 网址置表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 15:33:13
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table("website_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WebsiteConfig extends FlexModel<WebsiteConfig> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	/**
	 * 自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
	private Long id;

	/**
	 * 类型，1-渲染按钮，2-检索网站
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型，1-渲染按钮，2-检索网站", dataType = "String")
	private String type;

	/**
	 * 网页地址
	 */
	@Column("web_url")
	@ApiModelProperty(name = "webUrl", value = "网页地址", dataType = "String")
	private String webUrl;

	/**
	 * 配置id
	 */
	@Column("website_id")
	@ApiModelProperty(name = "websiteId", value = "配置id", dataType = "String")
	private String websiteId;

	/**
	 * 按钮时：展示文本
	 */
	@Column("show_text")
	@ApiModelProperty(name = "showText", value = "按钮时：展示文本", dataType = "String")
	private String showText;

	/**
	 * 联网检索时：返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容
	 */
	@Column("data_type")
	@ApiModelProperty(name = "dataType", value = "联网检索时：返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容", dataType = "String")
	private String dataType;

	/**
	 * 联网检索时：返回top N条数据，大于1，可为空或不传
	 */
	@Column("top")
	@ApiModelProperty(name = "top", value = "联网检索时：返回top N条数据，大于1，可为空或不传", dataType = "Integer")
	private Integer top;

	/**
	 * 联网检索时：是否需要浏览器渲染，默认 0 否  ，1 是(速度慢)
	 */
	@Column("is_render")
	@ApiModelProperty(name = "isRender", value = "联网检索时：是否需要浏览器渲染，默认 0 否  ，1 是(速度慢)", dataType = "Integer")
	private Integer isRender;

	/**
	 * 是否有效，是/否
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否有效，是/否", dataType = "String")
	private String status;

	/**
	 * 搜索引擎,toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "engine", value = "搜索引擎,toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)", dataType = "String")
	private String engine;

	/**
	 * 前缀词语
	 */
	@Column(value = "prefix")
	@ApiModelProperty(name = "prefix", value = "前缀词语", dataType = "String")
	private String prefix;

	/**
	 * 后缀词语
	 */
	@Column(value = "suffix")
	@ApiModelProperty(name = "suffix", value = "后缀词语", dataType = "String")
	private String suffix;
}