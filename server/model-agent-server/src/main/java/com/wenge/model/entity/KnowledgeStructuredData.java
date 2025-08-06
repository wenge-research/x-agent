package com.wenge.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.List;

@IndexName
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeStructuredData implements Serializable {

    private static final long serialVersionUID = 7010729784945401865L;

    @IndexId(type = IdType.UUID)
    private String id;
    // 同步过来的业务id
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String businessId;
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String knowledgeId;
    // 同步过来的表名
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String tableName;
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String content;
    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense;
    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense1024;
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer type;
    @IndexField(fieldType = FieldType.DATE,dateFormat = "yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
    private String updateTime;
    @IndexField(fieldType = FieldType.DATE,dateFormat = "yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
    private String createTime;
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String createUser;
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

    @Score
    private Float score;
}
