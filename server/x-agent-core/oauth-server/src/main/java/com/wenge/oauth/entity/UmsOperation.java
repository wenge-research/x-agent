package com.wenge.oauth.entity;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;

/**
 * @author: caohaifeng
 * @date: 2024/8/23 11:25
 * @Description: es日志需要封装的字段信息
 * @Version:1.0
 **/

@Data
@Builder
@IndexName
@AllArgsConstructor
@NoArgsConstructor
public class UmsOperation implements Serializable{

	private static final long serialVersionUID = 8901334677509447888L;

	@IndexId(type = IdType.CUSTOMIZE)
	@IndexField(fieldType = FieldType.KEYWORD)
	private String id;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String uuid;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String basePath;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String uri;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String url;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String method;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String ip;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String description;

	@IndexField(fieldType = FieldType.INTEGER)
	private Integer logType;

	@IndexField(ignoreCase = true)
	private String logTypeName;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String belongModule;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String belongModuleName;


	@IndexField(fieldType = FieldType.KEYWORD)
	private String objectType;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String objectName;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String objectUuid;


	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private String accessTime;//必须为yyyy-MM-dd HH:mm:ss形式的时间
	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private String insertTime;//必须为yyyy-MM-dd HH:mm:ss形式的时间

	@IndexField(fieldType = FieldType.TEXT)
	private String parameter;

	@IndexField(fieldType = FieldType.TEXT)
	private String result;

	@IndexField(fieldType = FieldType.INTEGER)
	private Integer spend;//访问接口消耗时间

	@IndexField(fieldType = FieldType.KEYWORD)
	private String userAgent;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String systemName;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String browserName;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String operatorTagUserName; // 操作人（用户名）
	@IndexField(fieldType = FieldType.KEYWORD)
	private String operatorTagAccountName; // 操作人（用户名-登录使用）
	@IndexField(fieldType = FieldType.LONG)
	private Long operatorTagUserId; // 操作人（ID）
	@IndexField(fieldType = FieldType.KEYWORD)
	private String operatorTagTenantId; // 操作人租户（ID）

	@IndexField(fieldType = FieldType.KEYWORD)
	private String operatorSource; //操作来源 annotate-UmsOperationLog 通过这个注解拦截的数据   annotate-no 没有注解（不展示给用户）
	@Score
	private Float score;
}
