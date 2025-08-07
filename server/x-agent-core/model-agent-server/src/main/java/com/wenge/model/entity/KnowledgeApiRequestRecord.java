package com.wenge.model.entity;

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

/**
 * Description: 知识库API采集记录实体类
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-05-10 18:06:35
 */
@ApiModel
@Data
@Table(value = "knowledge_api_request_record", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class KnowledgeApiRequestRecord implements Serializable {

	@Id(value = "id", keyType = KeyType.Auto)
	private Long id;

	@Column("request_record_id")
	@ApiModelProperty(name = "requestRecordId", value = "接口请求记录业务id", dataType = "String")
	private String requestRecordId;

	@Column("knowledge_api_id")
	@ApiModelProperty(name = "knowledgeApiId", value = "api业务id", dataType = "String")
	private String knowledgeApiId;

	@Column("execute_time")
	@ApiModelProperty(name = "executeTime", value = "执行时间 单位ms", dataType = "Integer")
	private Long executeTime;

	@Column("state")
	@ApiModelProperty(name = "state", value = "状态 0-失败 1-成功", dataType = "Integer")
	private Integer state;

	@Column("consume_token")
	@ApiModelProperty(name = "consumeToken", value = "消耗token数", dataType = "Integer")
	private Integer consumeToken;

	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间（请求时间）", dataType = "String")
	private String createTime;

	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除 0-否 1-是", dataType = "Integer")
	private Integer deleteFlag;

	@Column("execute_msg")
	@ApiModelProperty(name = "executeMsg", value = "失败原因", dataType = "String")
	private String executeMsg;

}