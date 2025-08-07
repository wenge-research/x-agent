package com.wenge.model.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 记录用户的聊天记忆，以便更精准地进行推荐
 */
@Data
@Table("component_node_memory")
public class Memory {
    /**
     * 主键id
     */
    @Id(value = "id", keyType = KeyType.Auto)
    private Integer id;

    /**
     * 用户id
     */
    @Column("user_id")
    private String userId;

    /**
     * 工作流应用id
     */
    @Column("application_id")
    private String applicationId;

    /**
     * 主题
     */
    @Column("subject")
    private String subject;

    /**
     * 内容
     */
    @Column("content")
    private String content;

    /**
     * 对话id 该条记忆是从哪个对话中总结的
     */
    @Column("dialogue_id")
    private String dialogueId;

    /**
     * 记忆创建时间
     */
    @Column("create_time")
    private String createTime;
}
