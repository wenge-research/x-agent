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
 * 纠错词实体类
 */
@ApiModel
@Data
@Table(value = "correct_word", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class CorrectWord extends FlexModel<SynonymWord> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 纠错词
	 */
	@Column("content")
	@ApiModelProperty(name = "content", value = "纠错词", dataType = "String")
	private String content;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 纠错词所属关键字ID
	 */
	@Column(value = "correct_word_key_id")
	@ApiModelProperty(name = "correctWordKeyId", value = "纠错词所属关键字ID", dataType = "Long")
	private String correctWordKeyId;

}