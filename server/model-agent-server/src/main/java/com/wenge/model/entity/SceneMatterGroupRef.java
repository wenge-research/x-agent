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
 * Description: 业务场景与事项关联表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:22:26
 *
 */
@ApiModel
@Data
@Table("scene_matter_group_ref")
@EqualsAndHashCode(callSuper = false)
public class SceneMatterGroupRef implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 场景scene_id,scene_management.scene_id
	 */
	@Column("scene_id")
	@ApiModelProperty(name = "sceneId", value = "场景scene_id,scene_management.scene_id", dataType = "String")
	private String sceneId;

	/**
	 * 分组id
	 */
	@Column("group_id")
	@ApiModelProperty(name = "groupId", value = "分组id", dataType = "String")
	private String groupId;

	/**
	 * 事项表matter_id，matter_guide.matter_id
	 */
	@Column("matter_id")
	@ApiModelProperty(name = "matterId", value = "事项表matter_id，matter_guide.matter_id", dataType = "String")
	private String matterId;

	/**
	 * 执行顺序
	 */
	@Column("sorted")
	@ApiModelProperty(name = "sorted", value = "执行顺序", dataType = "Integer")
	private Integer sorted;

	/**
	 * 是否必要，1-必要，0-不必要
	 */
	@Column("required_flag")
	@ApiModelProperty(name = "requiredFlag", value = "是否必要，1-必要，0-不必要", dataType = "String")
	private String requiredFlag;

	/**
	 * 操作提示
	 */
	@Column("operation_tips")
	@ApiModelProperty(name = "operationTips", value = "操作提示", dataType = "String")
	private String operationTips;

	/**
	 * 额外说明
	 */
	@Column("extra_explanation")
	@ApiModelProperty(name = "extraExplanation", value = "额外说明", dataType = "String")
	private String extraExplanation;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 创建人
	 */
	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	private String createUser;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	private String updateUser;

	/**
	 * 状态，1-启用，0-停用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态，1-启用，0-停用", dataType = "String")
	private String status;
}