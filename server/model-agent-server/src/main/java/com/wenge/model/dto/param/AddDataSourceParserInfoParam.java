package com.wenge.model.dto.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
public class AddDataSourceParserInfoParam implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotNull
	@ApiModelProperty(name = "jdbcUrl", value = "JDBC路径", dataType = "String")
	private String jdbcUrl;
	@NotNull
	@ApiModelProperty(name = "jdbcName", value = "JDBC用户", dataType = "String")
	private String jdbcName;
	@NotNull
	@ApiModelProperty(name = "jdbcPassword", value = "JDBC密码", dataType = "String")
	private String jdbcPassword;
	@NotNull
	@ApiModelProperty(name = "desc", value = "简介描述", dataType = "String")
	private String desc;
	@NotNull
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@ApiModelProperty(name = "foldersId", value = "文件夹id", dataType = "String")
	private String foldersId;
	@ApiModelProperty(name = "validDate", value = "有效时间（永久有效无效传值），格式如2099-12-31 00:00:00", dataType = "String")
	private String validDate;
	@NotNull
	@ApiModelProperty(name = "dsType", value = "数据源类型1 MYSQL 2 DM 3 Yashan", dataType = "Integer")
	private Integer dsType;
	@NotNull
	@ApiModelProperty(name = "port", value = "端口", dataType = "String")
	private String port;

	/**
	 * 模块：knn-知识库，collect-数据集
	 */
	private String module;
}
