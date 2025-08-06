package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.model.constants.MybatisFileConstant;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import lombok.Data;


/**
 * 图片信息实体类
 */
@Data
@Table(value = "image_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
public class ImageInfo {

    /**
     * 自增主键ID
     */
    @Id(value = "id", keyType = KeyType.Auto)
    private Long id;

    /**
     * 业务图片ID(32位)
     */
    @Column("image_id")
    private String ImageId;

    /**
     * 图片标题
     */
    @Column("topic")
    private String topic;

    /**
     * 图片链接
     */
    @Column("image_url")
    private String ImageUrl;


    /**
     * 图片大小(字节)
     */
    @Column("size")
    private Long size;

    /**
     * 图片宽度
     */
    @Column("width")
    private Integer width;

    /**
     * 图片高度
     */
    @Column("height")
    private Integer height;

    /**
     * 图片格式
     */
    @Column("format")
    private String format;

    /**
     * 状态(0:禁用,1:启用)
     */
    @Column("status")
    private Integer status = 1;


    /**
     * 创建人id
     */
    @Column("create_user_id")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_ID)
    private String createUserId;


    /**
     * 创建人名称
     */
    @Column("create_user_name")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String createUserName;

    /**
     * 创建时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    private String updateTime;


    /**
     * 更新人ID
     */
    @Column("update_user_id")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_ID)
    private String updateUserId;

    /**
     * 更新人名称
     */
    @Column("update_user_name")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String updateUserName;

    /**
     * 删除标志(0:未删除,1:删除)
     */
    @Column(value = "deleted_flag",isLogicDelete = true)
    private Integer deletedFlag;

    /**
     * 发布状态(0:未发布,1:已发布)
     */
    @Column("published_status")
    private Integer publishedStatus;

    /**
     * 审核状态(0:未审核,1:审核通过,2:审核不通过)
     */
    @Column("review_status")
    private Integer reviewStatus;

    /**
     * 描述
     */
    @Column("description")
    private String description;

    /**
     * 图片比例
     */
    @Column("ratio")
    private String ratio;

    /**
     * 媒体类型 1视频 2图片
     */
    @Column("media_type")
    private Integer mediaType;

    /**
     * 分辨率
     */
    @Column("resolution")
    private Integer resolution;

}