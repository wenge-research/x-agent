package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 法律法规库
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
//@ApiModel
//@Data(staticConstructor = "create")
//@Table("legal_and_regulatory_data")
//@EqualsAndHashCode(callSuper = false)

@Data
@IndexName
public class LegalAndRegulatoryData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@IndexId(type = IdType.UUID)
	private String id;


	/**
	 * 公司名称-wxm-网信办
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String companyName;

	/**
	 * 状态：0-有效 1-无效
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String status;


	/**
	 * 标题
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String title;

	/**
	 * 采集地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String url;


	/**
	 * 正文
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String content;


	/**
	 * 发布时间-字符串 例如：2021年06月11日 08:31
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String pushTimeStr;

	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private Date pushTime;

	/**
	 * 来源
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String source;

	/**
	 * 更新时间
	 */
	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private String updateTime;

	/**
	 * 创建时间
	 */
	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private String createTime;

	/**
	 * 1-网信法律法规 2-网信案例库 3-网信动态 4-普法动画
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer type;

	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String tab;

	/**
	 * 文件或者视频链接数组
	 */
	@IndexField(fieldType = FieldType.KEYWORD_TEXT)
	private String files;


}