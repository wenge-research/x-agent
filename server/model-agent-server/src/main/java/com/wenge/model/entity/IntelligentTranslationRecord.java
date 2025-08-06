package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@IndexName
public class IntelligentTranslationRecord implements Serializable {

    private static final long serialVersionUID = 8901334677509447839L;

    /**
     * 数据id es自动生成
     */
    @IndexId(type = IdType.UUID)
    private String id;

    /**
     * 唯一ID 记录ID 代码中生成
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String uniqueId;

    /**
     * 需要翻译的文本内容
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, ignoreAbove = 10000)
    private String text;

    /**
     * 翻译之后的文本内容
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, ignoreAbove = 10000)
    private String textTranslate;


    /**
     * 源语言，zh-中文，en-英文
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String srcLang;

    /**
     * 目标语言，zh-中文，en-英文
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String tgtLang;

    /**
     * 翻译类型  file-文件 text-文本
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String translateType = "text";


    /**
     * 翻译类型为文件时 记录文件上传源文件地址
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String fileUrl;

    /**
     * 翻译类型为文件时 记录文件翻译之后文件地址
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String fileUrlTranslate;


    /**
     * 文件 pdf doc docx txt xlsx xls 类型
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String fileType;

    /**
     * 应用id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String applicationId;

    /**
     * 记录来源 默认0-智能翻译页面
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String dataSource = "0";


    /**
     *  翻译耗时
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String time;


    /**
     *  YAyi响应返回值body
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String responseBody;



    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;


    /**
     * 用户姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userName;


    /**
     * 用户ID
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userId;

    /**
     * 部门ID
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String deptId;

    /**
     * 部门名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String deptName;


    @Score
    private Float score;

    /**
     * 文件 pdf word txt execl 类型
     */
    @Column(ignore = true)
    private MultipartFile file;

    @Column(ignore = true)
    private String clientId;
}
