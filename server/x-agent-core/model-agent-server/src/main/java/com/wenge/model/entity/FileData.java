package com.wenge.model.entity;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.List;

@Data
public class FileData implements Serializable {

    private static final long serialVersionUID = 2140260158713971752L;

    @IndexId(type = IdType.UUID)
    private String id;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String knowledgeId;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String fileId;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String fileName;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private Integer paraNum;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private Integer pageNum;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String foldersId;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String content;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String type;

    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String createUser;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String updateTime;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String updateUser;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String status;

    /**
     * 生效开始时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss", strategy = FieldStrategy.IGNORED)
    private String effectiveStartTime;

    /**
     * 生效结束时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss", strategy = FieldStrategy.IGNORED)
    private String effectiveEndTime;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense1024;

    @Score
    private Float score;

}
