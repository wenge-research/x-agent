package com.wenge.model.workflow.component.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class WorkflowRunStatus {
    /**
     * 执行过程中会推送日志
     */
    public static final String DEBUG = "debug";
    /**
     * 只输出结束节点运行结果
     */
    public static final String RUN = "run";
    public static final String TOOL = "tool";
    /**
     * 迭代模式运行节点
     */
    public static final String ITERATION = "iteration";


    private NodeRunStatus data;


    // public static String question = StringConstant.BLANK;
}
