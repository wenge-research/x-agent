package com.wenge.model.workflow.component.dto;

import lombok.Data;

/**
 * 知识库节点参数
 */
@Data
public class KnowledgeParam {

    /**
     * 检索内容分数阈值
     */
    private String contentScore;

    /**
     * 重排正文分数阈值
     */
    private String rangeContentScore;

    /**
     * 问答形式检索标题分数阈值
     */
    private String qaTitleScore;

    /**
     * 问答形式重排标题分数阈值
     */
    private String qaRangeTitleScore;

    /**
     * 问答形式检索正文分数阈值
     */
    private String qaContentScore;

    /**
     * 问答形式重排正文(回答)分数阈值
     */
    private String qaRangeContentScore;

    /**
     * 引用知识库文段数量(重排后的数量)
     */
    private String filterNum;

    /**
     * 知识库文段预备数量(重排前的数量)
     */
    private String prepareNum;

}
