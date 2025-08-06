package com.wenge.model.entity;

import cn.hutool.json.JSONObject;
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
 * Description: 应用配置实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-28 10:38:12
 *
 */
@ApiModel
@Data
@Table("application_plugin")
@EqualsAndHashCode(callSuper = false)
public class ApplicationPlugin implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 配置分组
	 */
	@Column("plugin_group")
	@ApiModelProperty(name = "pluginGroup", value = "配置分组", dataType = "String")
	private String pluginGroup;

	/**
	 * 配置项编码
	 */
	@Column("plugin_code")
	@ApiModelProperty(name = "pluginCode", value = "配置项编码", dataType = "String")
	private String pluginCode;

	/**
	 * 配置名称
	 */
	@Column("plugin_name")
	@ApiModelProperty(name = "pluginName", value = "配置名称", dataType = "String")
	private String pluginName;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 配置项id
	 */
	@Column("plugin_id")
	@ApiModelProperty(name = "id", value = "配置项id", dataType = "Long")
	private String pluginId;

	/**
	 * 描述
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "描述", dataType = "String")
	private String remark;

	/**
	 * 插件图标
	 */
	@Column("plugin_icon")
	@ApiModelProperty(name = "pluginIcon", value = "插件图标", dataType = "String")
	private String pluginIcon;

	/**
	 * 开关状态[1-开，0-关]
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "status", value = "开关状态[1-开，0-关]", dataType = "String")
	private String status;

	@Column(ignore = true)
	private JSONObject configExtend;

}