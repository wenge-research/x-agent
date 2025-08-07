package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author: caohaifeng
 * @date: 2024/8/22 15:03
 * @Description: 拦截词库实体类
 * @Version:1.0
 **/
@ApiModel
@Data(staticConstructor = "create")
@Table("intercept_word_house")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InterceptWordHouse implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	@Column("id")
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 库名称
	 */
	@Column("name")
	@ApiModelProperty(name = "processing", value = "库名称", dataType = "String")
	private String name;

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
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
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

	@Column(value = "create_user_account")
	@ApiModelProperty(name = "createUserAccount", value = "创建人账号", dataType = "String")
	private String createUserAccount;

	/**
	 * 删除标记：0-未删除 1-删除
	 */
	@Column(value = "deleted_flag" ,isLogicDelete = true)
	@ApiModelProperty(name = "deletedFlag", value = "删除标记：0-未删除 1-删除", dataType = "Integer")
	private Integer deletedFlag;

	/**
	 * 词库数量 关联数据变动的时候要更新
	 */
	@Column("word_count")
	@ApiModelProperty(name = "wordCount", value = "词库数量", dataType = "Integer")
	private Integer wordCount;

	/**
	 * 应用数量 关联数据变动的时候要更新
	 */
	@Column("application_count")
	@ApiModelProperty(name = "applicationCount", value = "词库数量", dataType = "Integer")
	private Long applicationCount;


	/**
	 * 租户ID
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户ID", dataType = "String")
	private String tenantId;

	/**
	 * 该敏感词库是否关联应用ID
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "applicationId", value = "该敏感词库是否关联应用ID", dataType = "String")
	private String applicationId;


	/**
	 * 敏感词库与应用关联的ID 用于删除记录使用
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "applicationId", value = "该敏感词库是否关联应用ID", dataType = "Long")
	private Long relId;

	/**
	 * 敏感词归属,是官方创建还是个人创建
	 */
	@Column("owner_type")
	@ApiModelProperty(name = "ownerType",value = "敏感词归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
	private String ownerType;
}