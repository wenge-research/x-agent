package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * Description: 拦截词实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table("intercept_word")
@EqualsAndHashCode(callSuper = false)
public class InterceptWord implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 关键词
	 */
	@Column("content")
	@ApiModelProperty(name = "content", value = "关键词", dataType = "String")
	private String content;

	/**
	 * 
	 */
	@Column("id")
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 处理方式
	 */
	@Column("processing")
	@ApiModelProperty(name = "processing", value = "处理方式", dataType = "String")
	private String processing;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 状态：1 启用；2 停用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态：1 启用；2 停用", dataType = "String")
	private String status;

	/**
	 * 类型:[无法回答,禁用词,替换词,学区判断]
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型:[无法回答,禁用词,替换词,学区判断]", dataType = "String")
	private String type;

	/**
	 * 租户id -作废
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	private String tenantId;

	/**
	 * 应用id(如果为空，则是通用的，如果不为空，则为指定应用的)-作废
	 */
	@Column(value = "application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id(如果为空，则是通用的，如果不为空，则为指定应用的)", dataType = "String")
	private String applicationId;


	/**
	 * 敏感词所属库ID
	 */
	@Column(value = "intercept_word_house_id")
	@ApiModelProperty(name = "interceptWordHouseId", value = "敏感词所属库ID", dataType = "Long")
	private Long interceptWordHouseId;


	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "更新时间", dataType = "String")
	private String createTime;


	@Column(value = "update_user_id")
	@ApiModelProperty(name = "updateUserId", value = "修改人ID", dataType = "String")
	private String updateUserId;


	@Column(value = "create_user_id")
	@ApiModelProperty(name = "createUserId", value = "创建人ID", dataType = "String")
	private String createUserId;

	@Column(value = "create_user_name")
	@ApiModelProperty(name = "createUserName", value = "创建人", dataType = "String")
	private String createUserName;

	/**
	 * 删除标记：0-未删除 1-删除
	 */
	@Column("deleted_flag")
	@ApiModelProperty(name = "deletedFlag", value = "删除标记：0-未删除 1-删除", dataType = "Integer")
	private Integer deletedFlag;

	/**
	 * 数据来源 0-添加  1-导入
	 */
	@Column("source")
	@ApiModelProperty(name = "source", value = "数据来源 0-添加  1-导入", dataType = "Integer")
	private Integer source;

	/**
	 * 导入文件id
	 */
	@Column("export_file_id")
	@ApiModelProperty(name = "exportFileId", value = "删除标记：0-未删除 1-删除", dataType = "String")
	private String exportFileId;

	/**
	 * 关联事项id
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "matterGuideId", value = "关联事项id", dataType = "String")
	private String matterGuideId;

	/**
	 * 关联事项的系统提示词
	 */
	@Column(ignore = true)
	@ApiModelProperty(value = "系统提示词")
	private String extraSystemPrompt;

	/**
	 * 关联事项的大模型事项提示词
	 */
	@Column(ignore = true)
	@ApiModelProperty(value = "大模型事项提示词")
	private String prompt;

}