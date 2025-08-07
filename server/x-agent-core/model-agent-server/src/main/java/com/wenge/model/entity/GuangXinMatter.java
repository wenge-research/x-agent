package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Description: 关芯智巡的事项判别表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-19 10:44:18
 *
 */
@ApiModel
@Data
@Table("guang_xin_matter")
@EqualsAndHashCode(callSuper = false)
public class GuangXinMatter implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
	private Long id;

	/**
	 * 数据id
	 */
	@Column("data_id")
	@ApiModelProperty(name = "dataId", value = "数据id", dataType = "String")
	private String dataId;

	/**
	 * 请求id
	 */
	@Column("trace_id")
	@ApiModelProperty(name = "traceId", value = "请求id", dataType = "String")
	private String traceId;

	/**
	 * 请求参数内容
	 */
	@Column("param_content")
	@ApiModelProperty(name = "paramContent", value = "请求参数内容", dataType = "String")
	private String paramContent;

	/**
	 * 请求参数内容
	 */
	@Column("result_content")
	@ApiModelProperty(name = "resultContent", value = "请求参数内容", dataType = "String")
	private String resultContent;

	/**
	 * 错误次数
	 */
	@Column("error_count")
	@ApiModelProperty(name = "errorCount", value = "错误次数", dataType = "Integer")
	private Integer errorCount;

	/**
	 * 错误信息
	 */
	@Column("error_msg")
	@ApiModelProperty(name = "errorMsg", value = "错误信息", dataType = "String")
	private String errorMsg;

	/**
	 * 回调地址
	 */
	@Column("call_back_url")
	@ApiModelProperty(name = "callBackUrl", value = "回调地址", dataType = "String")
	private String callBackUrl;

	/**
	 * 大模型指令
	 */
	@Column("llm_prompt")
	@ApiModelProperty(name = "llmPrompt", value = "大模型指令", dataType = "String")
	private String llmPrompt;

	/**
	 * 大模型结果
	 */
	@Column("llm_result")
	@ApiModelProperty(name = "llmResult", value = "大模型结果", dataType = "String")
	private String llmResult;

	/**
	 * 状态
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态", dataType = "String")
	private String status;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 描述图片，是一个json数组，每个元素是一个图片的url
	 */
	@Column("parse_image")
	@ApiModelProperty(name = "parseImage", value = "描述图片，是一个json数组，每个元素是一个图片的url", dataType = "String")
	private String parseImage;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

}