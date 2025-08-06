package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.MybatisFileConstant;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Description: 聊天模板实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-25 17:16:26
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "dialog_template", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DialogTemplate extends FlexModel<DialogTemplate> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 模板id
	 */
	@Column("template_id")
	@ApiModelProperty(name = "templateId", value = "模板id", dataType = "String")
	private String templateId;

	/**
	 * 模板名称
	 */
	@Column("template_name")
	@ApiModelProperty(name = "templateName", value = "模板名称", dataType = "String")
	private String templateName;

	/**
	 * 模板路由
	 */
	@Column("template_route")
	@ApiModelProperty(name = "templateRoute", value = "模板路由", dataType = "String")
	private String templateRoute;

	/**
	 * 模板图片路径
	 */
	@Column("picture_path")
	@ApiModelProperty(name = "picturePath", value = "模板图片路径", dataType = "String")
	private String picturePath;

	/**
	 * 0默认模板 1非默认模板
	 */
	@Column("default_flag")
	@ApiModelProperty(name = "defaultFlag", value = "0默认模板 1非默认模板", dataType = "Integer")
	private Integer defaultFlag;

	/**
	 * 聊天模板形式[PC/H5]
	 */
	@Column("form")
	@ApiModelProperty(name = "form", value = "聊天模板形式[PC/H5]", dataType = "String")
	private String form;

	/**
	 * 0单入参模板 1多入参模板
	 */
	@Column("is_multiple")
	@ApiModelProperty(name = "isMultiple", value = "聊天模板入参选择", dataType = "integer")
	private Integer isMultiple;

	/**
	 * smart_qa[智能问答], smart_report[智能报告], smart_search[智能搜索], smart_translate[智能翻译], smart_review[智能审查]
	 */
	@Column("intelligence_type")
	@ApiModelProperty(name = "intelligenceType", value = "聊天模板类型", dataType = "String")
	private String intelligenceType;

	/**
	 * 是否启用: 0 禁用, 1 启用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否启用: 0 禁用, 1 启用", dataType = "Integer")
	private Integer status;

	/**
	 * 模型创建归属,是官方创建还是个人创建
	 */
	@Column("owner_type")
	@ApiModelProperty(name = "ownerType",value = "模型创建归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
	private String ownerType;

	/**
	 * 创建人id
	 */
	@Column("create_user_id")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_ID)
	private String createUserId;


	/**
	 * 创建人名称
	 */
	@Column("create_user_name")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
	private String createUserName;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	private String createTime;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	private String updateTime;

	/**
	 * 更新人ID
	 */
	@Column("update_user_id")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_ID)
	private String updateUserId;

	/**
	 * 更新人名称
	 */
	@Column("update_user_name")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
	private String updateUserName;

	/**
	 * 删除标志(0:未删除,1:删除)
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "deleteFlag", dataType = "Integer")
	private Integer deleteFlag;
}