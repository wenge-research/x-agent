package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;

@Data
public class AiSearchRequest {

    /**
     * 搜索关键词
     */
    private String keyWord;

    /**
     * 生成问题的数量
     */
    private String questionNum;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 知识库id
     */
    private String knowledgeId;

    /**
     * 问题
     */
    private String question;

    private Integer pageNo;

    private Integer pageSize;

    /**
     * 文件类型
     */
    private List<Integer> types;

    /**
     * 文件类型
     */
    private List<String> fileTypes;

    private String queryType;

    /**
     * 开始创建时间
     */
    private String createStartTime;

    /**
     * 结束创建时间
     */
    private String createEndTime;

    /**
     * 文件大小
     */
    private Long fileSizeMax;

    /**
     * 文件大小
     */
    private Long fileSizeMin;


    /**
     * 排序字段 1 创建时间
     *
     */
    private String orderType;

    /**
     * 是否倒叙 0 否 1 是
     */
    private String isDesc;

}
