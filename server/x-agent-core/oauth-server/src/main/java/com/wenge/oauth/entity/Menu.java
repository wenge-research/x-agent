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
 * Description: 菜单表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:03:35
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "oauth_menu", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Menu extends FlexModel<Menu> implements Serializable {

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
	 * 自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
	private Long id;

	/**
	 * 是否隐藏:1.是 0.否
	 */
	@Column("is_hidden")
	@ApiModelProperty(name = "isHidden", value = "是否隐藏:1.是 0.否", dataType = "Integer")
	private Integer isHidden;

	/**
	 * 菜单编码
	 */
	@Column("menu_code")
	@ApiModelProperty(name = "menuCode", value = "菜单编码", dataType = "String")
	private String menuCode;

	/**
	 * 菜单图标地址
	 */
	@Column("menu_icon")
	@ApiModelProperty(name = "menuIcon", value = "菜单图标地址", dataType = "String")
	private String menuIcon;

	/**
	 * 激活菜单图标地址
	 */
	@Column("activate_icon")
	@ApiModelProperty(name = "activateIcon", value = "激活菜单图标地址", dataType = "String")
	private String activateIcon;

	/**
	 * 菜单id
	 */
	@Column("menu_id")
	@ApiModelProperty(name = "menuId", value = "菜单id", dataType = "String")
	private String menuId;

	/**
	 * 菜单名称
	 */
	@Column("menu_name")
	@ApiModelProperty(name = "menuName", value = "菜单名称", dataType = "String")
	private String menuName;

	/**
	 * 类型:1.菜单 2.功能，3.页签
	 */
	@Column("menu_type")
	@ApiModelProperty(name = "menuType", value = "类型:1.菜单 2.功能", dataType = "Integer")
	private Integer menuType;

	/**
	 * 菜单路由
	 */
	@Column("menu_url")
	@ApiModelProperty(name = "menuUrl", value = "菜单路由", dataType = "String")
	private String menuUrl;

	/**
	 * 排序
	 */
	@Column("order_no")
	@ApiModelProperty(name = "orderNo", value = "排序", dataType = "Integer")
	private Integer orderNo;

	/**
	 * 上级菜单id
	 */
	@Column("pid")
	@ApiModelProperty(name = "pid", value = "上级菜单id", dataType = "String")
	private String pid;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 菜单状态(1.启用 0.停用)
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "菜单状态(1.启用 0.停用)", dataType = "Integer")
	private Integer status;

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
	 * 客户端类型,默认为空，则所有端都可以使用，多个用逗号隔开[PC,H5]
	 */
	@Column("client_type")
	@ApiModelProperty(name = "clientType", value = "客户端类型,默认为空，则所有端都可以使用，多个用逗号隔开[PC,H5]", dataType = "String")
	private String clientType;

	/**
	 * 智巡主键id
	 */
	@Column("zx_id")
	@ApiModelProperty(name = "zxId", value = "智巡id", dataType = "String")
	private String zxId;

	/**
	 * 子菜单
	 */
	@Column(ignore = true)
	private List<Menu> children;

	// 重写equals方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Menu menu = (Menu) obj;
		return menuCode.equals(menu.menuCode);
	}

	// 重写hashCode方法
	@Override
	public int hashCode() {
		return menuCode.hashCode();
	}
}