package com.wenge.model.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 组件节点类型
 * 注意，添加节点枚举时候，请勿重复code
 */
@Getter
@AllArgsConstructor
public enum ComponentNodeEnum {
    /**
     * 开始节点
     */
    START(1, "start"),
    /**
     * api节点
     */
    API(2, "api"),
    /**
     * 并行节点
     */
//    PARALLEL(3),
    /**
     * 大模型节点
     */
    LLM(4, "llm"),
    /**
     * 排他节点，条件判断节点
     */
    EXCLUSIVE(5, "if_else"),
    /**
     * 迭代节点
     */
    ITERATION(6, "iteration"),

    /**
     * 分类节点
     */
    CLASSIFICATION(7, "classification"),

    /**
     * 提取参数节点
     */
    EXTRACTION_PARAMETER(8, "extraction_parameter"),

    /**
     * 知识库节点
     */
    KNOWLEDGE(9, "knowledge_node"),

    /**
     * 结束节点
     */
    END(10, "end"),

    /**
     * 回复节点
     */
    REPLY(11, "reply"),

    /**
     * content
     */
    CONTENT(12, "content"),

    /**
     * 文档
     */
    DOCUMENT(13, "document"),

    /**
     * URL
     */
    URL(14, "url_node"),

    /**
     * 结构化数据
     */
    STRUCTURED_DATA(15, "structured_data"),

    /**
     * 多媒体
     */
    MEDIA(16, "media"),

    /**
     * 雅意文档库
     */
    YAYI_KNOWLEDGE(17, "yayi_knowledge"),

    /**
     * QA_Title
     */
    Q_A_TITLE(18, "q_a_title"),

    /**
     * QA_Content
     */
    Q_A_CONTENT(19, "q_a_content"),

    /**
     * 工具-拦截词
     */
    INTERCEPT_WORD(20, "intercept_word"),

    /**
     * 工具节点
     */
    TOOL(21, "tool"),

    /**
     * 长期记忆节点
     */
    MEMORY(22, "memory"),

    /**
     * 文档提取节点
     */
    DOCUMENT_EXTRACTOR(23, "document_extractor"),

    /**
     * 文本处理节点
     */
    TEXT_PROCESS(24, "text_process"),

    /**
     * 变量节点
     */
    VARIABLE(25, "variable"),

    /**
     * 场景节点
     */
    SCENE(26, "scene"),

    /**
     * 代码节点
     */
    CODE(27, "code"),

    /**
     * 退出循环节点
     */
    BREAK(28, "break"),

    /**
     * 继续循环节点
     */
    CONTINUE(29, "continue"),

    /**
     * 问答节点
     */
    CONVERSATION(30, "conversation"),

    /**
     * 数据库节点
     */
    DATABASE(31, "database"),

    /**
     * 工作流节点
     */
    WORKFLOW(32, "workflow"),

    /**
     * 自定义工作流
     */
    CUSTOM(33, "custom"),

    /**
     * mcp节点
     */
    MCP(34, "mcp"),

    /**
     * 知识库写入节点
     */
    KNOWLEDGE_WRITE(35, "knowledge_write"),

    /**
     * mysql插入节点
     */
    MYSQL_INSERT(36, "mysql-insert"),

    /**
     * 自定义 sql
     */
    MYSQL_SQL(37, "mysql-sql"),

    /**
     * agent节点
     */
    AGENT(38, "agent_body"),

    /**
     * 数据集节点
     */
    COLLECT(39, "collect_data"),


    /**
     * AI生成图片节点
     */
    AI_IMAGE(40, "ai_image"),

    /**
     * AI生成视频节点
     */
    AI_VIDEO(41, "ai_video"),


    ;
    @EnumValue
    @JsonValue
    private final Integer code;

    private final String serviceName;

    public static ComponentNodeEnum getByCode(int code) {
        for (ComponentNodeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
