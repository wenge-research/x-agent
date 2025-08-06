package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@Table(value = "file_language", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class FileLanguage {

    @Id(value = "id", keyType = KeyType.Auto)
    private Long id;

    @Column(value = "file_id")
    private String fileId;

    @Column(value = "language")
    private String language;

    @Column(value = "file_url")
    private String fileUrl;

    @Column(value = "website_name")
    private String websiteName;

    @Column(value = "source_url")
    private String sourceUrl;

    @Column(value = "tags")
    private String tags;

    @Column(value = "keywords")
    private String keywords;

    @Column(value = "pictures")
    private String pictures;

    /**
     * 全文概况
     */
    @Column("full_text_overview")
    private String fullTextOverview;

    /**
     * 大纲提取
     */
    @Column("outline")
    private String outline;

    /**
     * 知识提取
     */
    @Column("knowledge_point")
    private String knowledgePoint;

    @Column(value = "delete_flag", isLogicDelete = true)
    private Integer deleteFlag;
}
