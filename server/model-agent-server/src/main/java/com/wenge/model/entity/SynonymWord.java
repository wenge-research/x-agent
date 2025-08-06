package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 同义词实体类
 */
@ApiModel
@Data
@Table(value = "synonym_word", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class SynonymWord extends FlexModel<SynonymWord> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 同义词
	 */
	@Column("content")
	@ApiModelProperty(name = "content", value = "同义词", dataType = "String")
	private String content;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 同义词所属库ID
	 */
	@Column(value = "synonym_word_key_id")
	@ApiModelProperty(name = "synonymWordKeyId", value = "同义词所属库ID", dataType = "Long")
	private String synonymWordKeyId;

}