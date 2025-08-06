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
 * Description: 应用-知识库关联表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 19:57:11
 *
 */
@ApiModel
@Data
@Table("application_knowledge")
@EqualsAndHashCode(callSuper = false)
public class ApplicationKnowledge implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 知识库id
	 */
	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	/**
	 * 类型 knowledge workflow
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型 集成了知识库和工作流", dataType = "String")
	private String type;

	/**
	 * 应用名称
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationName;

	/**
	 * 知识库名称
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "knowledgeId", value = "知识库名称", dataType = "String")
	private String knowledgeName;

	/**
	 * 关联的应用数
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "associatedApp", value = "关联的应用数", dataType = "Integer")
	private Integer associatedApp;

}