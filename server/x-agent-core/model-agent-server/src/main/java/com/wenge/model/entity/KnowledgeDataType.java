package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 知识数据分类实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 18:21:02
 *
 */
@ApiModel
@Data
@Table("knowledge_data_type")
@EqualsAndHashCode(callSuper = false)
public class KnowledgeDataType implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否删除[1-删除,0-未删除]
	 */
	@Column("delete_flag")
	@ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
	private String deleteFlag;

	/**
	 * 主键
	 */
	@Column("id")
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 父级id
	 */
	@Column("pid")
	@ApiModelProperty(name = "pid", value = "父级id", dataType = "Long")
	private Long pid;

	/**
	 * 是否启用[1-是，0-否]
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否启用[1-是，0-否]", dataType = "Integer")
	private Integer status;

	/**
	 * 类型
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型", dataType = "String")
	private String type;

	/**
	 * 创建时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "创建时间", dataType = "String")
	private String updateTime;

	/**
	 * 知识库id
	 */
	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@Column(ignore = true)
	private List<KnowledgeDataType> children;
}