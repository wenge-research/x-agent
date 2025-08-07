package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.wenge.model.workflow.entity.MetaParam;
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
import java.util.List;

/**
 * Description: 提示词配置实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-12-18 11:02:36
 *
 */
@ApiModel
@Data
@Table(value = "prompt_config", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class PromptConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 提示词id
	 */
	@Column("prompt_id")
	@ApiModelProperty(name = "promptId", value = "提示词id", dataType = "String")
	private String promptId;

	/**
	 * 提示词标题
	 */
	@Column("prompt_title")
	@ApiModelProperty(name = "promptTitle", value = "提示词标题", dataType = "String")
	private String promptTitle;

	/**
	 * 类型，system:系统提示词,user:用户提示词,yayi:机器人提示词
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型，system:系统提示词,user:用户提示词,yayi:机器人提示词", dataType = "String")
	private String type;

	/**
	 * 提示词内容
	 */
	@Column("content")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	@ApiModelProperty(name = "content", value = "提示词内容", dataType = "String")
	private String content;

	/**
	 * 备注说明
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注说明", dataType = "String")
	private String remark;

	/**
	 * 0-有效  1-无效
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "0-有效  1-无效", dataType = "Integer")
	private Integer status;

	/**
	 * 租户id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	private String tenantId;

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
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	private String createUser;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	private String updateUser;

	/**
	 * 是否删除[1-删除,0-未删除]
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "String")
	private Integer deleteFlag;

	/**
	 * 提示词参数，存储当前提示词中所有的变量名称
	 */
	@Column(value = "params", typeHandler = Fastjson2TypeHandler.class)
	@ApiModelProperty(name = "params", value = "提示词参数，存储当前提示词中所有的变量名称", dataType = "List")
	private List<MetaParam> params;

	@Column("all_visible")
	@ApiModelProperty(name = "allVisible",value = "是否所有人可见", dataType = "Integer", notes = "1:所有人可见,0:只有自己可见")
	private Integer allVisible;

	/**
	 * 提示词归属,是官方创建还是个人创建
	 */
	@Column("owner_type")
	@ApiModelProperty(name = "ownerType",value = "提示词归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
	private String ownerType;
}