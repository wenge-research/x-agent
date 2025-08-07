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
 * Description: 文档提示词配置表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-16 17:26:09
 *
 */
@ApiModel
@Data
@Table("smart_prompt_config")
@EqualsAndHashCode(callSuper = false)
public class SmartPromptConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
	private Long id;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	/**
	 * 提示词id
	 */
	@Column("prompt_id")
	@ApiModelProperty(name = "promptId", value = "提示词id", dataType = "String")
	private String promptId;

	/**
	 * 提示词内容
	 */
	@Column("prompt_content")
	@ApiModelProperty(name = "promptContent", value = "提示词内容", dataType = "String")
	private String promptContent;

	/**
	 * 模块，bidding-招标，bid-投标，gx_matter-关芯智巡...(后续有更多待定)
	 */
	@Column("module")
	@ApiModelProperty(name = "module", value = "模块，bidding-招标，bid-投标，gx_matter-关芯智巡...(后续有更多待定)", dataType = "String")
	private String module;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;
}