package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Description: 预置问题实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 14:01:20
 *
 */
@ApiModel
@Data
@Table(value = "preset_question", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class PresetQuestion implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 答案
	 */
	@Column("answer")
	@ApiModelProperty(name = "answer", value = "答案", dataType = "String")
	private String answer;

	/**
	 * 应用id，有业务作用
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id，有业务作用", dataType = "String")
	private String applicationId;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 创建人
	 */
	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 预设id，有业务作用
	 */
	@Column("preset_id")
	@ApiModelProperty(name = "presetId", value = "预设id，有业务作用", dataType = "String")
	private String presetId;

	/**
	 * 问题文本
	 */
	@Column("question")
	@ApiModelProperty(name = "question", value = "问题文本", dataType = "String")
	private String question;

	/**
	 * 是否启用[1-是，0-否]
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否启用[1-是，0-否]", dataType = "Integer")
	private Integer status;

	/**
	 * 类型[推荐问题,主题]
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型[推荐问题,主题]", dataType = "String")
	private String type;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;
}