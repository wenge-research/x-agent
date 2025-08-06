package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@ApiModel
@Data
@Table(value = "ai_search_data_analysis", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class AiSearchDataAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键自增
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
    private String applicationId;

    @Column("file_id")
    @ApiModelProperty(name = "fileId", value = "文件id", dataType = "String")
    private String fileId;

    @Column("read_num")
    @ApiModelProperty(name = "readNum", value = "浏览次数", dataType = "Integer")
    private Integer readNum;

    @Column("like_num")
    @ApiModelProperty(name = "likeNum", value = "点赞次数", dataType = "Integer")
    private Integer likeNum;

    @Column("sub_num")
    @ApiModelProperty(name = "subNum", value = "订阅数", dataType = "Integer")
    private Integer subNum;

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
}
