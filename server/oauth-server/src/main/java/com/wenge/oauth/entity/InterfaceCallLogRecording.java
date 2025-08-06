package com.wenge.oauth.entity;

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
 * @date: 2024/11/06 15:25
 * @Description: es付费接口调用日志记录表对象
 * @Version:1.0
 **/

@Data
@Builder
@IndexName
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceCallLogRecording implements Serializable{

	private static final long serialVersionUID = 8901334677509447999L;

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
	private String authTokenAccount; //--插件接口自定义的token
	@IndexField(fieldType = FieldType.KEYWORD)
	private String authUserInfo; //--插件接口自定义的token 用户信息（业务员ID ID 姓名 电话 过期时间）
	@IndexField(fieldType = FieldType.KEYWORD)
	private String authUserInfoId; //操作人的token --插件接口自定义的token Id
	@IndexField(fieldType = FieldType.KEYWORD)
	private String authUserInfoType; //操作类型  1-来源插件用户 2-来源系统页面用户 3-无鉴权的用户
	@IndexField(fieldType = FieldType.KEYWORD)
	private String modelPluginApiId;
	@Score
	private Float score;
}
