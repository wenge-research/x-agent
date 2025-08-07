package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import static com.wenge.oauth.constants.MybatisFiledConstant.MDC_USER_ID;

/**
 * @author: caohaifeng
 * @date: 2024/8/30 11:16
 * @Description: 应用nacos配置实体类
 * @Version:1.0
 **/
@ApiModel
@Data
@Table(value = "application_configuration", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ApplicationConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键，自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键，自增id", dataType = "Long")
	private Long id;

	/**
	 * 应用id，有业务作用
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id，有业务作用", dataType = "String")
	private String applicationId;

	/**
	 * key值
	 */
	@Column("key_info")
	@ApiModelProperty(name = "keyInfo", value = "key值", dataType = "String")
	private String keyInfo;

	/**
	 * value值
	 */
	@Column("value_info")
	@ApiModelProperty(name = "valueInfo", value = "value值", dataType = "String")
	private String valueInfo;

	/**
	 *   备注说明
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注说明", dataType = "String")
	private String remark;

	/**
	 * 是否有效 0-有效 1-无效
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否有效 0-有效 1-无效", dataType = "Integer")
	private Integer status;

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
	 * 更新用户id
	 */
	@Column("update_user_id")
	@ApiModelProperty(name = "updateUserId", value = "更新用户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MDC_USER_ID)
	private String updateUserId;







}