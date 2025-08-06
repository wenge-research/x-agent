package com.wenge.model.entity;

import cn.hutool.json.JSONObject;
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
import java.util.List;

/**
 * Description: mcp 服务实体类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:32:47
 */
@ApiModel
@Data
@Table(value = "mcp_server_user", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class McpServerUser implements Serializable {

	private static final long serialVersionUID = 1L;



	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * mcp服务的id
	 */
	@Column("mcp_id")
	@ApiModelProperty(name = "mcpId", value = "mcp服务的id", dataType = "String")
	private String mcpId;

	/**
	 * mcp服务的id
	 */
	@Column("user_id")
	@ApiModelProperty(name = "userId", value = "用户的id", dataType = "String")
	private String userId;

	/**
	 * 状态：0 关闭；1 开启
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态：0 关闭；1 开启", dataType = "String")
	private Integer status;


}