package com.wenge.model.dto.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class RunTaskDataSourceParserInfoParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private String knowledgeId;

	@ApiModelProperty(name = "dataSourceId", value = "业务id", dataType = "String")
	private String dataSourceId;
	private String tableName;

}
