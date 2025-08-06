package com.wenge.model.dto.param;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.rely.FieldType;

import java.util.List;

/**
 * @BelongsProject: yayi-model-application
 * @BelongsPackage:com.wenge.model.dto.param
 * @Author:caohaifeng
 * @createTime:2024-08-12
 * @Description:TODO
 * @Version:1.0
 */

@Data
public class ApplicationOverviewIndicatorsParam {

    private String question;

    private String applicationName;
    private String applicationId;

    private List<String> applicationIds;
    private List<String> knowledgeIds;

    private String userId;

    private String sourceType; //网页端-PC  移动端-APP

    private String type;

    private String startTime;

    private String endTime;

    /**
     * 次数最多-count，时间最新-time
     */
    private String top50Type = "count";

    /**
     * topN，默认50
     */
    private Integer count = 50;

    /**
     * 类型：1 赞；0 踩
     */
    private String feedbackType;

    private String deptId;
    private String [] fileImages;














}
