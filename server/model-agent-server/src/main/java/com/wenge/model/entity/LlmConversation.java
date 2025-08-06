package com.wenge.model.entity;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.List;

@Data
@IndexName
public class LlmConversation implements Serializable {

    private static final long serialVersionUID = -3527756657883773575L;

    @IndexId(type = IdType.CUSTOMIZE)
    private String id;

    @IndexField(fieldType = FieldType.NESTED, nestedClass = DialogueMessage.class)
    private List<DialogueMessage> messageList;

    /**
     * 会话ID
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String conversationId;

    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String createTime;

    /**
     * 步骤
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String step;

    /**
     * 问题
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String question;
}
