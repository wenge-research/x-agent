package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.enums.KnowledgeTypeEnum;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Description: 文件夹 实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 17:48:53
 *
 */
@ApiModel
@Data(staticConstructor = "creat")
@Table(value = "folders", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Folders extends FlexModel<Folders> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

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
	 * 文件夹id，有业务作用
	 */
	@Column("folders_id")
	@ApiModelProperty(name = "foldersId", value = "文件夹id，有业务作用", dataType = "String")
	private String foldersId;

	/**
	 * 文件夹顺序
	 */
	@Column("folders_order")
	@ApiModelProperty(name = "foldersOrder", value = "文件夹顺序", dataType = "Integer")
	private Integer foldersOrder;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 知识库 ID，knowledge_info.knowledge_id
	 */
	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库 ID，knowledge_info.knowledge_id", dataType = "String")
	private String knowledgeId;

	/**
	 * 文件夹类型 2文档数据 6多媒体数据 {@link KnowledgeTypeEnum}
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "文件夹类型 2文档数据 3url 5结构化 6多媒体数据", dataType = "Integer")
	private String type;

	/**
	 * 名称
	 */
	@Column("name")
	@ApiModelProperty(name = "name", value = "名称", dataType = "String")
	private String name;

	/**
	 * 父级 ID，0 表示根文件夹
	 */
	@Column("parent_id")
	@ApiModelProperty(name = "parentId", value = "父级 ID，0 表示根文件夹", dataType = "Long")
	private String parentId;

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
}