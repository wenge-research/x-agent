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

/**
 * Description: 部门信息实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-25 10:27:27
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "oauth_dept", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthDept extends FlexModel<OauthDept> implements Serializable {

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
	 * 部门编码
	 */
	@Column("dept_code")
	@ApiModelProperty(name = "deptCode", value = "部门编码", dataType = "String")
	private String deptCode;

	/**
	 * 部门id
	 */
	@Column("dept_id")
	@ApiModelProperty(name = "deptId", value = "部门id", dataType = "String")
	private String deptId;

	/**
	 * 部门地址
	 */
	@Column("dept_location")
	@ApiModelProperty(name = "deptLocation", value = "部门地址", dataType = "String")
	private String deptLocation;

	/**
	 * 部门名称
	 */
	@Column("dept_name")
	@ApiModelProperty(name = "deptName", value = "部门名称", dataType = "String")
	private String deptName;

	/**
	 * 排序
	 */
	@Column("dept_order")
	@ApiModelProperty(name = "deptOrder", value = "排序", dataType = "Integer")
	private Integer deptOrder;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 父级部门id
	 */
	@Column("pid")
	@ApiModelProperty(name = "pid", value = "父级部门id", dataType = "String")
	private String pid;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 租户id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;

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
	 * 智巡主键id
	 */
	@Column("zx_id")
	@ApiModelProperty(name = "zxId", value = "智巡id", dataType = "String")
	private String zxId;
}