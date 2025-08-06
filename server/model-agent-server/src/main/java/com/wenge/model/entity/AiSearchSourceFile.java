package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Description: Ai智能搜索溯源文件
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-02-13 18:06:35
 */
@ApiModel
@Data
@Table(value = "ai_search_source_file", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class AiSearchSourceFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;

    /**
     * 文件id，有业务作用
     */
    @Column("file_id")
    @ApiModelProperty(name = "fileId", value = "文件id，有业务作用", dataType = "String")
    private String fileId;

    /**
     * 文件名
     */
    @Column("file_name")
    @ApiModelProperty(name = "fileName", value = "文件名", dataType = "String")
    private String fileName;

    /**
     * 转换后的pdf文件路径
     */
    @Column("trans_pdf_url")
    @ApiModelProperty(name = "transPdfUrl", value = "转换后的PDF文件路径", dataType = "String")
    private String transPdfUrl;

    /**
     * 文件路径
     */
    @Column("file_url")
    @ApiModelProperty(name = "fileUrl", value = "文件路径", dataType = "String")
    private String fileUrl;

    /**
     * 文件类型
     */
    @Column("file_type")
    @ApiModelProperty(name = "fileType", value = "文件类型", dataType = "String")
    private String fileType;

    /**
     * 文件大小
     */
    @Column("file_size")
    @ApiModelProperty(name = "fileSize", value = "文件大小", dataType = "Long")
    private Long fileSize;

    /**
     * 文档所属分类
     */
    @Column("type")
    @ApiModelProperty(name = "type", value = "文档所属分类", dataType = "String")
    private String type;

    /**
     * 字数
     */
    @Column("word_count")
    @ApiModelProperty(name = "wordCount", value = "字数", dataType = "Integer")
    private Integer wordCount;

    /**
     * 段落数
     */
    @Column("paragraphs_num")
    @ApiModelProperty(name = "paragraphsNum", value = "段落数", dataType = "Integer")
    private Integer paragraphsNum;

    /**
     * 有效期开始时间
     */
    @Column("period_start")
    @ApiModelProperty(name = "periodStart", value = "有效期开始时间", dataType = "String")
    private String periodStart;

    /**
     * 有效期结束
     */
    @Column("period_end")
    @ApiModelProperty(name = "periodEnd", value = "有效期结束", dataType = "String")
    private String periodEnd;

    /**
     * 0未删除 1删除
     */
    @Column("delete_flag")
    @ApiModelProperty(name = "deleteFlag", value = "0未删除 1删除", dataType = "Integer")
    private String deleteFlag;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 原始文件路径
     */
    @Column("original_url")
    @ApiModelProperty(name = "originalUrl", value = "原始文件路径", dataType = "String")
    private String originalUrl;

    /**
     * 图片颜色 0-红色 1-白色 2-紫色 3-绿色
     */
    @Column("pic_color_type")
    @ApiModelProperty(name = "picColorType", value = "图片颜色 0-红色 1-白色 2-紫色 3-绿色", dataType = "Integer")
    private Integer picColorType;

    /**
     * 文件对应web链接
     */
    @Column("web_link")
    @ApiModelProperty(name = "webLink", value = "文件对应web链接", dataType = "String")
    private String webLink;

    /**
     * 标题
     */
    @Column("title")
    @ApiModelProperty(name = "title", value = "标题", dataType = "String")
    private String title;

    /**
     * 收录时间
     */
    @Column("record_time")
    @ApiModelProperty(name = "recordTime", value = "收录时间", dataType = "String")
    private String recordTime;

    /**
     * 名中段落
     */
    @Column("hit_paragraph_num")
    @ApiModelProperty(name = "hitParagraphNum", value = "命中的段落", dataType = "Integer")
    private Integer hitParagraphNum;

    /**
     * 命中内容
     */
    @Column("hit_content")
    @ApiModelProperty(name = "hitContent", value = "命中内容", dataType = "String")
    private String hitContent;

    /**
     * 文档主题图片
     */
    @Column("document_pic")
    @ApiModelProperty(name = "documentPic", value = "命中内容", dataType = "String")
    private String documentPic;

    /**
     * 创建人
     */
    @Column("create_user")
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String createUser;

    /**
     * 更新人
     */
    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String updateUser;


}