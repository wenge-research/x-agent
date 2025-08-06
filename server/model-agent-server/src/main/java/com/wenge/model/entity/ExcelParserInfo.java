package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.model.constants.MybatisFileConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 文件实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:35
 *
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "excel_parser_file", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ExcelParserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	@Column("excel_id")
	@ApiModelProperty(name = "excelId", value = "业务id", dataType = "String")
	private String excelId;

	@Column("description")
	@ApiModelProperty(name = "desc", value = "描述", dataType = "String")
	private String desc;

	@Column("synch_status")
	@ApiModelProperty(name = "synchStatus", value = "问数文件同步大模型分析处理 0-未同步 2-同步中 3-同步完成 4-同步异常（重新同步）", dataType = "String")
	private String synchStatus;

	@Column("file_url")
	@ApiModelProperty(name = "fileUrl", value = "文件存储路径", dataType = "String")
	private String fileUrl;

	@Column("file_size")
	@ApiModelProperty(name = "fileSize", value = "文件大小", dataType = "String")
	private String fileSize;

	@Column("file_post_fix")
	@ApiModelProperty(name = "filePostFix", value = "文件后缀", dataType = "String")
	private String filePostFix;

	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@Column("folders_id")
	@ApiModelProperty(name = "foldersId", value = "知识库id", dataType = "String")
	private String foldersId;

	@Column("parser_flag")
	@ApiModelProperty(name = "parserFlag", value = "文件是否被解析 0未解析 1已经解析", dataType = "Integer")
	private Integer parserFlag;

	@Column("enable_flag")
	@ApiModelProperty(name = "enableFlag", value = "启用状态 0 关闭 1启用", dataType = "Integer")
	private Integer enableFlag;

	@Column("valid_date")
	@ApiModelProperty(name = "validDate", value = "有效时间", dataType = "String")
	private String validDate;

	@Column("delete_flag")
	@ApiModelProperty(name = "deleteFlag", value = "0未删除 1删除", dataType = "Integer")
	private Integer deleteFlag;

	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private Date createTime;

	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
	private String createUser;

	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private Date updateTime;

	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
	private String updateUser;
}