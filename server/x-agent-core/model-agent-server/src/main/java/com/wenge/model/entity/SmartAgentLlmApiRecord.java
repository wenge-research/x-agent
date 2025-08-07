package com.wenge.model.entity;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;

@Data
@IndexName
public class SmartAgentLlmApiRecord implements Serializable {

    private static final long serialVersionUID = -106422947770924245L;

    /**
     * 数据id
     */
    @IndexId(type = IdType.UUID)
    private String id;

    /**
     * 应用id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String applicationId;

    /**
     * 模型id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String modelId;

    /**
     * 客户端id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String clientId;

    /**
     * 流水 id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String traceId;

    /**
     * 状态码
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String code;

    /**
     * 密钥 key
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String apiKey;

    /**
     * 总消耗 token
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long total_tokens;

    /**
     *  响应消息
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String msg;

    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 结果
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String result;

    /**
     * 请求参数
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String param;

    /**
     * 1. llmCompletions-大模型-llm
     * 2. gxClassifyMatter-关芯智巡-gxzx
     * 3. dialogueApiStream-api流式对话-dialogue
     * 4. dialogueRunApiStream-工作流流失对话-workflowStream
     * 5. dialogueRunApiString-工作流非流失对话-workflowString
     * 6. mcp_server MCP服务-mcp_server
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String type;

}
