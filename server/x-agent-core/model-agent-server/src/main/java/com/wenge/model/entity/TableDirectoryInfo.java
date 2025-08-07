package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "structure_table_config", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class TableDirectoryInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	@Column("business_id")
	@ApiModelProperty(name = "businessId", value = "data_source_parser_info表或者data_source_parser_info表业务主键 表示关联，" +
			"通过结构化业务类型type字段区分，1 数据源  2excel文件", dataType = "String")
	private String businessId;

	@Column("type")
	@ApiModelProperty(name = "type", value = "结构化业务类型，1 数据源  2excel文件", dataType = "Integer")
	private Integer type;


	@Column("table_name")
	@ApiModelProperty(name = "tableName", value = "表名", dataType = "String")
	private String tableName;

	@Column("parser_flag")
	@ApiModelProperty(name = "parserFlag", value = "文件是否被解析 0未解析 1已经解析", dataType = "Integer")
	private Integer parserFlag;

	@Column("parser_field")
	@ApiModelProperty(name = "parserField", value = "勾选的解析的字段，多个以逗号隔开", dataType = "String")
	private String parserField;

	@Column("all_field")
	@ApiModelProperty(name = "allField", value = "该表的所有字段，多个以逗号隔开", dataType = "String")
	private String allField;
}