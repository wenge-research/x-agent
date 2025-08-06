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
@Table(value = "ai_search_file_sub_history", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class AiSearchFileSubHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
    private String applicationId;

    @Column("file_id")
    @ApiModelProperty(name = "fileId", value = "文件id", dataType = "String")
    private String fileId;

    @Column("status")
    @ApiModelProperty(name = "status", value = "订阅状态 0 为订阅 1 订阅", dataType = "String")
    private String status;

    @Column("user_id")
    @ApiModelProperty(name = "userId", value = "用户id", dataType = "String")
    private Long userId;

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
