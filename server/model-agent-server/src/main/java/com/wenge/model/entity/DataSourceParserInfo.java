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
@Table(value = "data_source_parser_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class DataSourceParserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	@Column("data_source_id")
	@ApiModelProperty(name = "dataSourceId", value = "业务id", dataType = "String")
	private String dataSourceId;


	@ApiModelProperty("数据源类型 1 MYSQL 2 DM 3 Yashan")
	@Column("ds_type")
	private Integer dsType;


	@Column("jdbc_url")
	@ApiModelProperty(name = "jdbcUrl", value = "JDBC路径", dataType = "String")
	private String jdbcUrl;

	@Column("jdbc_name")
	@ApiModelProperty(name = "jdbcName", value = "JDBC用户", dataType = "String")
	private String jdbcName;

	@Column("jdbc_password")
	@ApiModelProperty(name = "jdbcPassword", value = "JDBC密码", dataType = "String")
	private String jdbcPassword;

	@Column("description")
	@ApiModelProperty(name = "description", value = "简介描述", dataType = "String")
	private String desc;

	@Column("enable_flag")
	@ApiModelProperty(name = "enableFlag", value = "启用状态 0 关闭 1启用", dataType = "Integer")
	private Integer enableFlag;

	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	/**
	 * 模块：knn-知识库，collect-数据集
	 */
	@Column("module")
	@ApiModelProperty(name = "module", value = "模块：knn-知识库，collect-数据集", dataType = "String")
	private String module;

	@Column("folders_id")
	@ApiModelProperty(name = "foldersId", value = "文件夹id", dataType = "String")
	private String foldersId;

	@Column("parser_flag")
	@ApiModelProperty(name = "parserFlag", value = "文件是否被解析 0未解析 1解析中 2已经解析 3解析失败", dataType = "Integer")
	private Integer parserFlag;

	@Column("valid_date")
	@ApiModelProperty(name = "validDate", value = "有效时间", dataType = "String")
	private String validDate;

	@Column(value = "delete_flag", isLogicDelete = true)
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
	@Column("port")
	@ApiModelProperty(name = "port", value = "端口", dataType = "String")
	private String port;
}
