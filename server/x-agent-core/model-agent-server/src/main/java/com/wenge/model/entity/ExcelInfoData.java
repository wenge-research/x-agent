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
@Table(value = "excel_info_data_sz", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ExcelInfoData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	@Column("sql_source")
	@ApiModelProperty(name = "tableName", value = "数据库源数据", dataType = "String")
	private String SqlSource;

	@Column("table_name")
	@ApiModelProperty(name = "tableName", value = "表名", dataType = "String")
	private String tableName;

	@Column("knowledgeId")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@Column("excel_info")
	@ApiModelProperty(name = "excelInfo", value = "该表的所有字段，多个以逗号隔开", dataType = "String")
	private String excelInfo;
}