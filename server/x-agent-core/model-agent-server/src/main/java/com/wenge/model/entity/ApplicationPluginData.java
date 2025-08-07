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
 * Create Date Time: 2024-08-30 15:32:33
 *
 */
@ApiModel
@Data
@Table("application_plugin_data")
@EqualsAndHashCode(callSuper = false)
public class ApplicationPluginData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 插件id
	 */
	@Column("plugin_id")
	@ApiModelProperty(name = "configId", value = "配置项id", dataType = "String")
	private String pluginId;

	/**
	 * 配置项编码
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "pluginCode", value = "配置项编码", dataType = "String")
	private String pluginCode;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	/**
	 * 开关状态[1-开，0-关]
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "开关状态[1-开，0-关]", dataType = "String")
	private String status;

	@Column(ignore = true)
	private JSONObject configExtend;
}