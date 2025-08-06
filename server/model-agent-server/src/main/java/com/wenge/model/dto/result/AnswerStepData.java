package com.wenge.model.dto.result;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.AnswerRefParam;
import com.wenge.model.enums.StepStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AnswerStepData implements Serializable {

    private static final long serialVersionUID = -826248558154588966L;

    /**
     * 当前回答步骤
     */
    private String step;

    /**
     * 当前回答
     */
    private String answer;

    /**
     * 推理
     */
    private String reasoningContent;

    /**
     * 当前回答的大纲
     */
    private String outline;


    ///**
    // * 领域
    // */
    //private String type;
    //
    ///**
    // * 机构
    // */
    //private String org;

    /**
     * 状态，1：正在检索答案，2：正在回答(有答案的情况下)，3：回答完成(有答案的情况下)，4：无法回答, 5:最终输出答案
     */
    private StepStatusEnum status;

    /**
     * 当前步骤索引
     */
    private Integer index;

    /**
     * 当前场景自定义的数据
     */
    private Object customize;

    /**
     * 当前场景推流状态，0：未推流，1：推流中，2：推流完成
     */
    private Integer streamStatus;

    /**
     * 当前推流的进度
     */
    private JSONObject currentData;

    /**
     * 当前回答
     */
    private String errorMessage;

    /**
     * 知识库策略 不传默认全部
     */
    private List<String> strategy;

    /**
     *
     */
    private List<AnswerRefParam> refList;
}
