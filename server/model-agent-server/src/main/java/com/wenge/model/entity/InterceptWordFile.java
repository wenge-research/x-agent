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

@ApiModel
@Data(staticConstructor = "create")
@Table(value = "intercept_word_file", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class InterceptWordFile implements Serializable {

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
    @Column("create_user_id")
    @ApiModelProperty(name = "createUserId", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String createUserId;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 更新人
     */
    @Column("update_user_id")
    @ApiModelProperty(name = "updateUserId", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String updateUserId;

    /**
     * 0未删除 1删除
     */
    @Column("deleted_flag")
    @ApiModelProperty(name = "deletedFlag", value = "0未删除 1删除", dataType = "Integer")
    private String deletedFlag;

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
     * 主键自增
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;


    /**
     * 状态：0-上传失败-默认 1-上传成功（记录url）2-解析失败  3-解析成功 4-导入成功 5-导入失败  10-其他错误
     */
    @Column("status")
    @ApiModelProperty(name = "status", value = "状态：0-上传失败-默认 1-上传成功（记录url）2-解析失败  3-解析成功 4-导入成功 5-导入失败  10-其他错误", dataType = "Integer")
    private Integer status;



    @Column(ignore = true)
    private MultipartFile multipartFile;


}