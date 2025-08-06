package com.wenge.model.dto.param;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Description: 文件实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:35
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddExcelParserInfoParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(name = "文件上传")
	@JSONField(serialize = false)
	private MultipartFile file;

	@ApiModelProperty(name = "desc", value = "描述", dataType = "String")
	private String desc;

	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@ApiModelProperty(name = "foldersId", value = "文件夹id", dataType = "String")
	private String foldersId;

	@ApiModelProperty(name = "validDate", value = "有效时间（永久有效无效传值），格式如2099-12-31 00:00:00", dataType = "String")
	private String validDate;

}