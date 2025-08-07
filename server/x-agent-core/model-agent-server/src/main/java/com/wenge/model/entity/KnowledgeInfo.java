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
 * Description: 知识库管理实体类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 14:10:22
 */
@ApiModel
@Data
@Table(value = "knowledge_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class KnowledgeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自动清洗[是/否]
	 */
	@Column("clean_flag")
	@ApiModelProperty(name = "cleanFlag", value = "自动清洗[是/否]", dataType = "String")
	private String cleanFlag;

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
	 * 切割方式，为空时智能分割，保存分割符号(,，。)
	 */
	@Column("cutting_mode")
	@ApiModelProperty(name = "cuttingMode", value = "切割方式，为空时智能分割，保存分割符号(,，。)", dataType = "String")
	private String cuttingMode;

	/**
	 * 是否删除[1-删除,0-未删除]
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
	private Integer deleteFlag;

	/**
	 * 主键，自增id，没有业务作用
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键，自增id，没有业务作用", dataType = "Long")
	private Long id;

	/**
	 * 知识库描述
	 */
	@Column("introduce")
	@ApiModelProperty(name = "introduce", value = "知识库描述", dataType = "String")
	private String introduce;

	/**
	 * 知识库id，有业务作用
	 */
	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id，有业务作用", dataType = "String")
	private String knowledgeId;

	/**
	 * 知识库名称
	 */
	@Column("knowledge_name")
	@ApiModelProperty(name = "knowledgeName", value = "知识库名称", dataType = "String")
	private String knowledgeName;

	/**
	 * 知识库类型[1-知识库数据，2-文档，3-URL解析，4-API采集，5-结构化数据（excel或者数据库）]
	 */
	@Column("knowledge_type")
	@ApiModelProperty(name = "knowledgeType", value = "知识库类型[1-问答，2-文档，3-URL解析，4-API采集，5-结构化数据（excel或者数据库）]", dataType = "String")
	private String knowledgeType;

	/**
	 * 有效期结束
	 */
	@Column("period_end")
	@ApiModelProperty(name = "periodEnd", value = "有效期结束", dataType = "String")
	private String periodEnd;

	/**
	 * 有效期开始时间
	 */
	@Column("period_start")
	@ApiModelProperty(name = "periodStart", value = "有效期开始时间", dataType = "String")
	private String periodStart;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 切割长度
	 */
	@Column("segment_length")
	@ApiModelProperty(name = "segmentLength", value = "切割长度", dataType = "Integer")
	private Integer segmentLength;

	/**
	 * 是否启用【是/否】
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否启用【是/否】", dataType = "Integer")
	private String status;

	/**
	 * 标签，多个用，号隔开
	 */
	@Column("tag_name")
	@ApiModelProperty(name = "tagName", value = "标签，多个用，号隔开", dataType = "String")
	private String tagName;

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
	 * 租户id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;

	/**
	 * 向量化模型id
	 */
	@Column("dense_vector_id")
	@ApiModelProperty(name = "denseVectorId", value = "向量化模型id", dataType = "String")
	private String denseVectorId;

	/**
	 * 向量化模型名称
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "denseVectorName", value = "向量化模型名称", dataType = "String")
	private String denseVectorName;

	/**
	 * 关联应用数目
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "associatedApp", value = "关联应用数目", dataType = "Integer")
	private Integer associatedApp;

	/**
	 * 知识库文档解析服务[yayiAnalysis:雅意智能解析/policy-aliyun:政策阿里云解析/local-depoly:本地部署解析]
	 */
	@Column("document_analysis_server")
	@ApiModelProperty(name = "documentAnalysisServer", value = "知识库文档解析服务[yayiAnalysis:雅意智能解析/policy-aliyun:政策阿里云解析/local-depoly:本地部署解析]", dataType = "String")
	private String documentAnalysisServer;

	/**
	 * 知识库已上传的文件数
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "fileNum", value = "文件数量", dataType = "Integer")
	private Integer fileNum;

	/**
	 * 知识库绑定的结构化数据数量
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "dataSourceParserInfoNum", value = "结构化数据数量", dataType = "Integer")
	private Integer dataSourceParserInfoNum;

	/**
	 * 知识库归属,是官方创建还是个人创建
	 */
	@Column("owner_type")
	@ApiModelProperty(name = "ownerType",value = "知识库归属,是官方创建还是个人创建", dataType = "Integer", notes = "personal:个人创建, official:官方创建")
	private String ownerType;

	/**
	 * 查询个人时区分自己和他人
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "isMe",value = "查询个人时区分自己和他人", dataType = "Boolean", notes = "true:是自己, false:是他人")
	private Boolean isMe;

	/**
	 * 查询"我的"中区分是否授权
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "granted",value = "区分是否授权", dataType = "Boolean", notes = "true:授权, false:未授权")
	private Boolean granted;
}