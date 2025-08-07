package com.wenge.model.entity;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Description: 应用信息对话日志
 *
 * @Author: yijiazheng
 * Version: 1.0
 * Create Date Time: 2025-04-08 11:02:50
 */
@ApiModel
@Data
@Table(value = "application_dialogue", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ApplicationInfoDialogue implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键，自增id，没有业务作用
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键，自增id，没有业务作用", dataType = "Long")
	private Long id;
	/**
	 * 应用id，有业务作用
	 */
	@Column("application_info_id")
	@ApiModelProperty(name = "applicationInfoId", value = "关联application_info表id字段，没有业务作用", dataType = "String")
	private String applicationInfoId;
	/**
	 * 应用id，有业务作用
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id，有业务作用", dataType = "String")
	private String applicationId;
	/**
	 * 是否删除[1-删除,0-未删除]
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
	private String deleteFlag;
	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 创建人
	 */
	@Column(value = "create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;
	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 更新人，OnFieldFill注解可以自动填充，fill为触发机制，mdcKey为MDC中key，提取其中的值
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	/**
	 * 租户id
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;
	/**
	 * 提问问题
	 */
	@Column("component_id")
	@ApiModelProperty(name = "componentId", value = "提问问题", dataType = "String")
	private String componentId;
	/**
	 * 提问问题
	 */
	@Column("question")
	@ApiModelProperty(name = "question", value = "提问问题", dataType = "String")
	private String question;
	/**
	 * 回答问题
	 */
	@Column("answer")
	@ApiModelProperty(name = "answer", value = "回答问题", dataType = "String")
	private String answer;
	/**
	 * ai出题应答id
	 */
	@Column("dialogue_id")
	@ApiModelProperty(name = "dialogueId", value = "ai出题应答id", dataType = "String")
	private String dialogueId;
	/**
	 * 问答标识
	 */
	@Column("client_id")
	@ApiModelProperty(name = "clientId", value = "问答标识", dataType = "String")
	private String clientId;
	/**
	 * 问答标识
	 */
	@Column("show_recommended_questions")
	@ApiModelProperty(name = "showRecommendedQuestions", value = "问答标识", dataType = "String")
	private Integer showRecommendedQuestions;
	/**
	 * 题目类型，1单选，2多选
	 */
	@Column("answer_flag")
	@ApiModelProperty(name = "answerFlag", value = "题目类型，1单选，2多选", dataType = "String")
	private Integer answerFlag;
	/**
	 * 其他
	 */
	@Column("output")
	@ApiModelProperty(name = "output", value = "其他", dataType = "String")
	private String output;
	/**
	 * 其他
	 */
	@Column("answer_type")
	@ApiModelProperty(name = "answerType", value = "其他", dataType = "Integer")
	private Integer answerType;
	/**
	 * 部门id//用来数据隔离
	 */
	@Column("dept_id")
	@ApiModelProperty(name = "deptId", value = "部门id//用来数据隔离", dataType = "String")
	private String deptId;
	/**
	 * 用户id//用来数据隔离
	 */
	@Column("user_id")
	@ApiModelProperty(name = "userId", value = "用户id//用来数据隔离", dataType = "String")
	private String userId;
}