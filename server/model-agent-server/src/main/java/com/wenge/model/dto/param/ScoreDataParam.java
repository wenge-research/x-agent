package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScoreDataParam extends WGParam {

    private static final long serialVersionUID = 3030995714595522542L;

    /**
     * 问题
     */
    private String question;

    /**
     * 策略
     */
    private String strategy;

    /**
     * 指定子策略
     */
    private String strategyDetail;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 客户端ID
     */
    private String clientId;
}
