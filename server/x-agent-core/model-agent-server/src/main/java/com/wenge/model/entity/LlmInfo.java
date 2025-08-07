package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description: 大模型信息表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 15:50:53
 *
 */
@ApiModel
@Data
@Table(value = "llm_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class LlmInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * appKey
	 */
	@Column("app_key")
	@ApiModelProperty(name = "appKey", value = "appKey", dataType = "String")
	private String appKey;

	/**
	 * 模型编码
	 */
	@Column("model_code")
	@ApiModelProperty(name = "modelCode", value = "模型编码", dataType = "String")
	private String modelCode;

	/**
	 * 秘钥
	 */
	@Column("app_secret")
	@ApiModelProperty(name = "appSecret", value = "秘钥", dataType = "String")
	private String appSecret;

	/**
	 * best_of
	 */
	@Column("best_of")
	@ApiModelProperty(name = "bestOf", value = "best_of", dataType = "BigDecimal")
	private BigDecimal bestOf;

	/**
	 * 1:true，0：false 
	 */
	@Column("disable_search")
	@ApiModelProperty(name = "disableSearch", value = "1:true，0：false ", dataType = "String")
	private Boolean disableSearch;

	/**
	 * 1:true，0：false
	 */
	@Column("do_sample")
	@ApiModelProperty(name = "doSample", value = "1:true，0：false ", dataType = "String")
	private Boolean doSample;

	/**
	 * 1:true，0：false 
	 */
	@Column("enable_citation")
	@ApiModelProperty(name = "enableCitation", value = "1:true，0：false ", dataType = "String")
	private Boolean enableCitation;

	/**
	 * 1:true，0：false 
	 */
	@Column("enable_system_memory")
	@ApiModelProperty(name = "enableSystemMemory", value = "1:true，0：false ", dataType = "String")
	private Boolean enableSystemMemory;

	/**
	 * 1:true，0：false 
	 */
	@Column("enable_trace")
	@ApiModelProperty(name = "enableTrace", value = "1:true，0：false ", dataType = "String")
	private Boolean enableTrace;

	/**
	 * frequency_penalty
	 */
	@Column("frequency_penalty")
	@ApiModelProperty(name = "frequencyPenalty", value = "frequency_penalty", dataType = "BigDecimal")
	private BigDecimal frequencyPenalty;

	/**
	 * 主键，自增id，没有业务作用
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键，自增id，没有业务作用", dataType = "Long")
	private Long id;

	/**
	 * 1:true，0：false 
	 */
	@Column("logprobs")
	@ApiModelProperty(name = "logprobs", value = "1:true，0：false ", dataType = "String")
	private Boolean logprobs;

	/**
	 * maxNewTokens
	 */
	@Column("max_new_tokens")
	@ApiModelProperty(name = "maxNewTokens", value = "maxNewTokens", dataType = "Integer")
	private Integer maxNewTokens;

	/**
	 * max_output_tokens
	 */
	@Column("max_output_tokens")
	@ApiModelProperty(name = "maxOutputTokens", value = "max_output_tokens", dataType = "Integer")
	private Integer maxOutputTokens;



	/**
	 * max_tokens
	 */
	@Column("max_tokens")
	@ApiModelProperty(name = "maxTokens", value = "max_tokens", dataType = "Integer")
	private Integer maxTokens;

	/**
	 * model
	 */
	@Column("model")
	@ApiModelProperty(name = "model", value = "model", dataType = "String")
	private String model;

	/**
	 * 模型id，有业务作用
	 */
	@Column("model_id")
	@ApiModelProperty(name = "modelId", value = "模型id，有业务作用", dataType = "String")
	private String modelId;

	/**
	 * 模型名称
	 */
	@Column("model_name")
	@ApiModelProperty(name = "modelName", value = "模型名称", dataType = "String")
	private String modelName;

	/**
	 * n
	 */
	@Column("n")
	@ApiModelProperty(name = "n", value = "n", dataType = "Integer")
	private Integer n;

	/**
	 * penalty_score
	 */
	@Column("penalty_score")
	@ApiModelProperty(name = "penaltyScore", value = "penalty_score", dataType = "BigDecimal")
	private BigDecimal penaltyScore;

	/**
	 * presence_penalty
	 */
	@Column("presence_penalty")
	@ApiModelProperty(name = "presencePenalty", value = "presence_penalty", dataType = "BigDecimal")
	private BigDecimal presencePenalty;

	/**
	 * repetition_penalty
	 */
	@Column("repetition_penalty")
	@ApiModelProperty(name = "repetitionPenalty", value = "repetition_penalty", dataType = "BigDecimal")
	private BigDecimal repetitionPenalty;

	/**
	 * response_format
	 */
	@Column("response_format")
	@ApiModelProperty(name = "responseFormat", value = "response_format", dataType = "String")
	private String responseFormat;

	/**
	 * 重试间隔，毫秒
	 */
	@Column("retry_interval")
	@ApiModelProperty(name = "retryInterval", value = "重试间隔，毫秒", dataType = "Integer")
	private Integer retryInterval;

	/**
	 * 重试次数
	 */
	@Column("retry_times")
	@ApiModelProperty(name = "retryTimes", value = "重试次数", dataType = "Integer")
	private Integer retryTimes;

	/**
	 * 状态[启用/停用]
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态[启用/停用]", dataType = "String")
	private String status;

	/**
	 * stop
	 */
	@Column("stop")
	@ApiModelProperty(name = "stop", value = "stop", dataType = "String")
	private String stop;

	/**
	 * 1:true，0：false 
	 */
	@Column("stream")
	@ApiModelProperty(name = "stream", value = "1:true，0：false ", dataType = "String")
	private Boolean stream;

	/**
	 * system
	 */
	@Column("system")
	@ApiModelProperty(name = "system", value = "system", dataType = "String")
	private String system;

	/**
	 * system_memory_id
	 */
	@Column("system_memory_id")
	@ApiModelProperty(name = "systemMemoryId", value = "system_memory_id", dataType = "String")
	private String systemMemoryId;

	/**
	 * temperature
	 */
	@Column("temperature")
	@ApiModelProperty(name = "temperature", value = "temperature", dataType = "BigDecimal")
	private BigDecimal temperature;

	/**
	 * top_k
	 */
	@Column("top_k")
	@ApiModelProperty(name = "topK", value = "top_k", dataType = "BigDecimal")
	private BigDecimal topK;

	/**
	 * topLogprobs
	 */
	@Column("top_logprobs")
	@ApiModelProperty(name = "topLogprobs", value = "topLogprobs", dataType = "BigDecimal")
	private BigDecimal topLogprobs;

	/**
	 * top_p
	 */
	@Column("top_p")
	@ApiModelProperty(name = "topP", value = "top_p", dataType = "BigDecimal")
	private BigDecimal topP;

	/**
	 * 大模型接口地址
	 */
	@Column("uri")
	@ApiModelProperty(name = "uri", value = "大模型接口地址", dataType = "String")
	private String uri;

	/**
	 * 厂商名称
	 */
	@Column("manufacturer")
	@ApiModelProperty(name = "manufacturer", value = "厂商名称", dataType = "String")
	private String manufacturer;

	/**
	 * 默认配置的数据禁止修改，需要作为其他数据的模板
	 */
	@Column("default_config")
	@ApiModelProperty(name = "default_config", value = "default_config", dataType = "Integer")
	private Integer defaultConfig;

	/**
	 * deleteFlag
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "deleteFlag", dataType = "Integer")
	private Integer deleteFlag;

	/**
	 * 策略
	 */
	@Column(ignore = true)
	private LlmStrategy llmStrategy;

	/**
	 * 租户id
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;

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
	 * tag
	 */
	@Column("tag")
	@ApiModelProperty(name = "tag", value = "标签", dataType = "String")
	private String tag;

	/**
	 * 大模型类型
	 */
	@Column("model_type")
	@ApiModelProperty(name = "model_type", value = "大模型类型", dataType = "String")
	private String modelType;

	@Column("desc")
	@ApiModelProperty(name = "desc", value = "模型描述", dataType = "String")
	private String desc;

	/**
	 * 模型创建归属,是官方创建还是个人创建
	 */
	@Column("owner_type")
	@ApiModelProperty(name = "ownerType",value = "模型创建归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
	private String ownerType;

	@Column("labels")
	@ApiModelProperty(name = "labels", value = "标签，多个标签之间用,")
	private String labels;

	@ApiModelProperty(name = "maxTokenL", value = "最大回复数最小取值")
	@Column(ignore = true)
	private Integer maxTokenL;

	@ApiModelProperty(name = "maxTokenR", value = "最大回复数最大取值")
	@Column(ignore = true)
	private Integer maxTokenR;

	@Column("manufacturer_model_id")
	@ApiModelProperty(name = "manufacturerModelId", value = "大模型厂商模型信息表的id", dataType = "String")
	private String manufacturerModelId;

	@Column(ignore = true)
	@ApiModelProperty(name = "manufacturerIcon", value = "厂商logo", dataType = "String")
	private String manufacturerIcon;

	@Column(ignore = true)
	@ApiModelProperty(name = "manufacturerIcon", value = "基础模型名称(对应厂商的模型名称)", dataType = "String")
	private String baseModelName;

}