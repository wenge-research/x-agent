package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
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
import java.util.List;

/**
 * Description: 角色表实体类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-01 20:03:03
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "oauth_role", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Role extends FlexModel<Role> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

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
	 * 角色ID
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "角色ID", dataType = "Long")
	private Long id;

	/**
	 * 角色ID
	 */
	@Column(value = "role_id")
	@ApiModelProperty(name = "roleId", value = "角色ID", dataType = "String")
	private String roleId;

	/**
	 * 父id
	 */
	@Column("pid")
	@ApiModelProperty(name = "pid", value = "父id", dataType = "Long")
	private String pid;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 角色代码
	 */
	@Column("role_code")
	@ApiModelProperty(name = "roleCode", value = "角色代码", dataType = "String")
	private String roleCode;

	/**
	 * 角色名称
	 */
	@Column("role_name")
	@ApiModelProperty(name = "roleName", value = "角色名称", dataType = "String")
	private String roleName;

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

	/**
	 * 状态，[1-启用，0-禁用]
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "角色名称", dataType = "String")
	private String status;

	/**
	 * 菜单列表
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "menuIdList", value = "菜单列表", dataType = "String")
	private List<String> menuIdList;

	/**
	 * 租户id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;

	/**
	 * 智巡主键id
	 */
	@Column("zx_id")
	@ApiModelProperty(name = "zxId", value = "智巡id", dataType = "String")
	private String zxId;

}