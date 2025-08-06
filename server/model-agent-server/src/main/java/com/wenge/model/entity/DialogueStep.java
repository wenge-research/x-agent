package com.wenge.model.entity;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.mybatisflex.annotation.Column;
import com.wenge.model.utils.DateUtil;
import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;

@Data
public class DialogueStep implements Serializable {

    private static final long serialVersionUID = 6417066016163711990L;

    @IndexId(type = IdType.UUID)
    private String id;

    /**
     * 对话id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, fieldData = true)
    private String dialogueId;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String step;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private Object prompt;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private Object result;

    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.PATTERN_1);

    /**
     * 耗时,毫秒
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long costTime;

    /**
     * 大模型首次返回的时间
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long firstLlmTime;

    /**
     * 大模型结束的时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    /**
     * 大模型总消耗token
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer tokenTotal = 0;


    /**
     * 是否需要展示 0不需要 1需要
     */
    @Column(ignore = true)
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer isShow = 1;

}
