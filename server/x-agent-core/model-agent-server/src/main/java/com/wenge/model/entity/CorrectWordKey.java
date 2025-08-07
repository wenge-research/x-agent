package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 纠错词库实体类
 */
@ApiModel
@Data
@Table(value = "correct_word_key", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class CorrectWordKey implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *主键
	 */
	@Id(value = "主键", keyType = KeyType.None)
	@Column("id")
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private String id;

	/**
	 * 同义词关键字
	 */
	@Column("key_word")
	@ApiModelProperty(name = "keyWord", value = "同义词关键字", dataType = "String")
	private String keyWord;

	/**
	 * 类型
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型", dataType = "String")
	private String type;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

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
	@Column("deleted_flag")
	@ApiModelProperty(name = "deletedFlag", value = "删除标记：0-未删除 1-删除", dataType = "Integer")
	private Integer deletedFlag;

	/**
	 * 词库数量 关联数据变动的时候要更新
	 */
	@Column("word_count")
	@ApiModelProperty(name = "wordCount", value = "词库数量", dataType = "Integer")
	private Integer wordCount;

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
	@ApiModelProperty(name = "exportFileId", value = "导入文件id", dataType = "String")
	private String exportFileId;

	@Column(ignore = true)
	@ApiModelProperty(name = "type", value = "纠错词列表", dataType = "String")
	private List<CorrectWord> correctWordList;

}