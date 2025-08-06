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
@Table(value = "url_parser_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class UrlParserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@Column("url_id")
	@ApiModelProperty(name = "urlId", value = "文件id，有业务作用", dataType = "String")
	private String urlId;

	@Column("page_url")
	@ApiModelProperty(name = "pageUrl", value = "url名称", dataType = "String")
	private String pageUrl;

	@Column("title")
	@ApiModelProperty(name = "title", value = "文件名", dataType = "String")
	private String title;

	@Column("status")
	@ApiModelProperty(name = "status", value = "状态：0 上传中；1 解析中；2 解析成功；3 解析失败，4 上传失败,5 上传成功", dataType = "Integer")
	private Integer status;

	@Column("word_count")
	@ApiModelProperty(name = "wordCount", value = "字数", dataType = "Integer")
	private Integer wordCount;

	@Column("paragraphs_num")
	@ApiModelProperty(name = "paragraphsNum", value = "段落数", dataType = "Integer")
	private Integer paragraphsNum;

	@Column("period_end")
	@ApiModelProperty(name = "periodEnd", value = "有效期结束", dataType = "String")
	private String periodEnd;

	@Column("period_start")
	@ApiModelProperty(name = "periodStart", value = "有效期开始时间", dataType = "String")
	private String periodStart;

	@Column("delete_flag")
	@ApiModelProperty(name = "deleteFlag", value = "0未删除 1删除", dataType = "Integer")
	private Integer deleteFlag;

	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
	private String createUser;

	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
	private String updateUser;

	@Column("subsection_rule")
	@ApiModelProperty(name = "subsectionRule", value = "切分规则 1yayi 2自定义", dataType = "Integer")
	private Integer subsectionRule;

	@Column("avg_paragraphs_count")
	@ApiModelProperty(name = "avgParagraphsCount", value = "平均段落字数", dataType = "Integer")
	private Integer avgParagraphsCount;

	@Column("error_msg")
	@ApiModelProperty(name = "errorMsg", value = "错误消息", dataType = "String")
	private String errorMsg;

	@Column("error_num")
	@ApiModelProperty(name = "errorNum", value = "解析错误次数", dataType = "Integer")
	private Integer errorNum;

	@Column("source")
	@ApiModelProperty(name = "source", value = "来源 0-上传(默认) 1-同步拉取", dataType = "Integer")
	private Integer source;

	@Column("collect_platform_task_id")
	@ApiModelProperty(name = "collectPlatformTaskId", value = "采集平台任务id", dataType = "Integer")
	private Integer collectPlatformTaskId;

	/**
	 * 是否启用
	 */
	@Column("enable")
	private String enable;

	@Column("folders_id")
	@ApiModelProperty(name = "foldersId", value = "文件夹 ID", dataType = "String")
	private String foldersId;


}