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
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Description: 文件实体类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:35
 */
@ApiModel
@Data
@Table(value = "file", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 创建人
     */
    @Column("create_user")
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String createUser;

    /**
     * 0未删除 1删除
     */
    @Column("delete_flag")
    @ApiModelProperty(name = "deleteFlag", value = "0未删除 1删除", dataType = "Integer")
    private String deleteFlag;

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
     * 文件大小
     */
    @Column("file_size")
    @ApiModelProperty(name = "fileSize", value = "文件大小", dataType = "Long")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Column("file_type")
    @ApiModelProperty(name = "fileType", value = "文件类型", dataType = "String")
    private String fileType;

    /**
     * 文件路径
     */
    @Column("file_url")
    @ApiModelProperty(name = "fileUrl", value = "文件路径", dataType = "String")
    private String fileUrl;

    /**
     * 原始文件路径
     */
    @Column("original_url")
    @ApiModelProperty(name = "originalUrl", value = "原始文件路径", dataType = "String")
    private String originalUrl;

    /**
     * 文件夹 ID
     */
    @Column("folders_id")
    @ApiModelProperty(name = "foldersId", value = "文件夹 ID", dataType = "String")
    private String foldersId;

    /**
     * 主键自增
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;

    /**
     * 段落数
     */
    @Column("paragraphs_num")
    @ApiModelProperty(name = "paragraphsNum", value = "段落数", dataType = "Integer")
    private Integer paragraphsNum;

    /**
     * 有效期结束
     */
    @Column("period_end")
    @ApiModelProperty(name = "periodEnd", value = "有效期结束", dataType = "String")
    private String periodEnd;

    /**
     * 有效期开始时间
     */
    @Column("period_start")
    @ApiModelProperty(name = "periodStart", value = "有效期开始时间", dataType = "String")
    private String periodStart;

    /**
     * 状态：0 上传中；1 解析中；2 解析成功；3 解析失败，4 上传失败,5 上传成功
     */
    @Column("status")
    @ApiModelProperty(name = "status", value = "状态：0 上传中；1 解析中；2 解析成功；3 解析失败，4 上传失败,5 上传成功", dataType = "Integer")
    private Integer status;

    /**
     * 类型：0 文件；1 图片；2 音频；
     */
    @Column("type")
    @ApiModelProperty(name = "type", value = "类型：0 文件；1 图片；2 音频；", dataType = "Integer")
    private Integer type;

    /**
     * 文件密码 可以为空
     */
    @Column("password")
    @ApiModelProperty(name = "password", value = "文件密码 可以为空", dataType = "String")
    private String password;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 更新人
     */
    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String updateUser;

    /**
     * 字数
     */
    @Column("word_count")
    @ApiModelProperty(name = "wordCount", value = "字数", dataType = "Integer")
    private Integer wordCount;

    /**
     * 错误消息
     */
    @Column("error_msg")
    @ApiModelProperty(name = "errorMsg", value = "错误消息", dataType = "String")
    private String errorMsg;

    @Column(ignore = true)
    private MultipartFile multipartFile;

    /**
     * 文件对应web链接
     */
    @Column("web_link")
    @ApiModelProperty(name = "webLink", value = "文件对应web链接", dataType = "String")
    private String webLink;

    /**
     * 解析错误次数
     */
    @Column("error_num")
    @ApiModelProperty(name = "errorNum", value = "解析错误次数", dataType = "Integer")
    private Integer errorNum;

    /**
     * 文件类型数量
     */
    @Column(ignore = true)
    private Long count;

    /**
     * 转换后的pdf文件路径
     */
    @Column("trans_pdf_url")
    @ApiModelProperty(name = "transPdfUrl", value = "转换后的PDF文件路径", dataType = "String")
    private String transPdfUrl;

    /**
     * 是否启用
     */
    @Column("enable")
    private String enable;

    /**
     * 知识库id
     */
    @Column(ignore = true)
    @ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
    private String knowledgeId;

    /**
     * 知识库文档解析服务[yayiAnalysis:雅意智能解析/policy-aliyun:政策阿里云解析/local-depoly:本地部署解析]
     */
    @Column(ignore = true)
    @ApiModelProperty(name = "documentAnalysisServer", value = "知识库文档解析服务[yayiAnalysis:雅意智能解析/policy-aliyun:政策阿里云解析/local-depoly:本地部署解析]", dataType = "String")
    private String documentAnalysisServer;

    /**
     * 平均段落长度
     */
    @Column(ignore = true)
    @ApiModelProperty(name = "averageParagraphLength", value = "平均段落长度", dataType = "Integer")
    private Integer averageParagraphLength;

    @Column("source")
    @ApiModelProperty(name = "source", value = "来源 0-上传(默认) 1-同步拉取", dataType = "Integer")
    private Integer source;

    @Column("collect_platform_task_id")
    @ApiModelProperty(name = "collectPlatformTaskId", value = "采集平台任务id", dataType = "Integer")
    private Integer collectPlatformTaskId;

}