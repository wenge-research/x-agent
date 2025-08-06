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
public class KnowledgeUrlData implements Serializable {

    private static final long serialVersionUID = 7010729784945401865L;

    @IndexId(type = IdType.UUID)
    private String id;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String knowledgeId;

    /**
     * 所属URL
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String url;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String parentId;

    /**
     * 所属URL
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String urlId;

    /**
     * 标题
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String title;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> titleDense;

    /**
     * 内容
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String content;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense1024;

    /**
     * 更新时间
     */
    @IndexField(fieldType = FieldType.DATE,dateFormat = "yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
    private String updateTime;

    /**
     * 更新人
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String updateUser;

    /**
     * 创建人
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String createUser;

    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.DATE,dateFormat = "yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
    private String createTime;

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

    /**
     * 数据分类
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String category;

    // 编码类型 1：初始切片 2：二次切片
    @IndexField(fieldType = FieldType.LONG)
    private Integer encodeType;

    // 是否开启 1开启 2关闭
    @IndexField(fieldType = FieldType.LONG)
    private Integer enable;

    @Score
    private Float score;
}
