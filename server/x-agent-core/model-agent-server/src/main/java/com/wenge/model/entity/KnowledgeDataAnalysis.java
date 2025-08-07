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

/**
 * 知识库数据汇总表实体类
 */
@ApiModel
@Data
@Table(value = "knowledge_data_analysis", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class KnowledgeDataAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("create_date")
    private String createDate; // 日期

    @Column("create_user")
    private String createUser; // 创建人

    @Column("update_user")
    private String updateUser; // 更新人

    @Column("create_time")
    private String createTime; // 创建时间

    @Column("update_time")
    private String updateTime; // 更新时间

    @Column("dept_id")
    private String deptId; // 部门id

    @Column("dept_name")
    private String deptName; // 部门名称

    @Column("data_type")
    private String dataType; // 数据类型：wj文件、wdd问答对、url、jgh 结构化

    @Column("knowledge_id")
    private String knowledgeId; // 知识库id

    @Column("relation_id")
    private String relationId; // 关联id 关联的数据唯一标识

    @Column("knowledge_name")
    private String knowledgeName; // 知识库名称

    @Column("delete_flag")
    private Integer deleteFlag;// 是否删除了 0未删除 1删除

    @Column("extended_type")
    private String extendedType; // 扩展字段 ：结构化：  datasource 表示数据库  表示excel 表格  文件/多媒体： 文件类型后缀
}