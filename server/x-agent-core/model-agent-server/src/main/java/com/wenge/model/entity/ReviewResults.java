package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * 审查结果实体
 */
@ApiModel
@Data
@Table(value = "review_results", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ReviewResults implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Column("id")
    @ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
    private Long id;

    /**
     * 应用id(如果为空，则是通用的，如果不为空，则为指定应用的)
     */
    @Column(value = "application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id(如果为空，则是通用的，如果不为空，则为指定应用的)", dataType = "String")
    private String applicationId;

    /**
     * 项
     */
    @Column(value = "term")
    @ApiModelProperty(name = "term", value = "项", dataType = "String")
    private String term;

    /**
     * 结果
     */
    @Column(value = "result")
    @ApiModelProperty(name = "result", value = "审查结果， 0未通过 1通过", dataType = "String")
    private String result;


    /**
     * 招标文件Url
     */
    @Column(value = "bidding_file_url")
    @ApiModelProperty(name = "biddingFileUrl", value = "文件链接", dataType = "String")
    private String biddingFileUrl;

    /**
     * 投标文件Url
     */
    @Column(value = "bid_file_url")
    @ApiModelProperty(name = "bidFileUrl", value = "文件链接", dataType = "String")
    private String bidFileUrl;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;


}