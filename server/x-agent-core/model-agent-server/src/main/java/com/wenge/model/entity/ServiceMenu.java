package com.wenge.model.entity;

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
 * Description: 服务菜单实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-05 11:34:23
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "service_menu", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ServiceMenu extends FlexModel<ServiceMenu> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
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
	 * 主键自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增id", dataType = "Long")
	private Long id;

	/**
	 * 菜单图标
	 */
	@Column("menu_icon")
	@ApiModelProperty(name = "menuIcon", value = "菜单图标", dataType = "String")
	private String menuIcon;

	/**
	 * 菜单名称
	 */
	@Column("menu_name")
	@ApiModelProperty(name = "menuName", value = "菜单名称", dataType = "String")
	private String menuName;

	/**
	 * 菜单排序
	 */
	@Column("menu_sorted")
	@ApiModelProperty(name = "menuSorted", value = "菜单排序", dataType = "Integer")
	private Integer menuSorted;

	/**
	 * 菜单状态[1-启用，0-停用]
	 */
	@Column("menu_status")
	@ApiModelProperty(name = "menuStatus", value = "菜单状态[1-启用，0-停用]", dataType = "String")
	private String menuStatus;

	/**
	 * 菜单链接
	 */
	@Column("menu_url")
	@ApiModelProperty(name = "menuUrl", value = "菜单链接", dataType = "String")
	private String menuUrl;

	/**
	 * 服务编码
	 */
	@Column("service_code")
	@ApiModelProperty(name = "serviceCode", value = "服务编码", dataType = "String")
	private String serviceCode;

	/**
	 * 服务类型
	 */
	@Column("service_type")
	@ApiModelProperty(name = "serviceType", value = "服务类型", dataType = "String")
	private String serviceType;

	/**
	 * 标签，多个用,隔开
	 */
	@Column("tag_name")
	@ApiModelProperty(name = "tagName", value = "标签，多个用,隔开", dataType = "String")
	private String tagName;

	/**
	 * 租户id
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
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
	@OnFieldFill(fill = FieldFill.UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;
}