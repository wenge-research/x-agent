package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 字典条目实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:04:39
 *
 */
@ApiModel
@Data
@Table("dict_item")
@EqualsAndHashCode(callSuper = false)
public class DictItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键ID", dataType = "Long")
	private Long id;

	/**
	 * 字典项id
	 */
	@Column("item_id")
	@ApiModelProperty(name = "itemId", value = "字典项id", dataType = "String")
	private String itemId;

	/**
	 * 字典类型id
	 */
	@Column("dict_type_id")
	@ApiModelProperty(name = "dictTypeId", value = "字典类型id", dataType = "String")
	private String dictTypeId;

	/**
	 * 父id，条目-类型关系使用type，条目-条目关系使用该字段
	 */
	@Column("pid")
	@ApiModelProperty(name = "pid", value = "父id，条目-类型关系使用type，条目-条目关系使用该字段", dataType = "String")
	private String pid;

	/**
	 * 字典类型
	 */
	@Column("dict_type")
	@ApiModelProperty(name = "dictType", value = "字典类型", dataType = "String")
	private String dictType;

	/**
	 * 条目标签
	 */
	@Column("item_label")
	@ApiModelProperty(name = "itemLabel", value = "条目标签", dataType = "String")
	private String itemLabel;

	/**
	 * 条目名称
	 */
	@Column("item_name")
	@ApiModelProperty(name = "itemName", value = "条目名称", dataType = "String")
	private String itemName;

	/**
	 * 排序
	 */
	@Column("item_order")
	@ApiModelProperty(name = "itemOrder", value = "排序", dataType = "Integer")
	private Integer itemOrder;

	/**
	 * 是否默认（1是 0否）
	 */
	@Column("default_item")
	@ApiModelProperty(name = "defaultItem", value = "是否默认（1是 0否）", dataType = "String")
	private String defaultItem;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 1.正常，0.停用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "1.正常，0.停用", dataType = "String")
	private String status;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 创建人
	 */
	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	private String createUser;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	private String updateUser;

	/**
	 * 子级
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "children", value = "更新人", dataType = "String")
	private List<DictItem> children;
}