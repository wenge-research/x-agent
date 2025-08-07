package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScoreDataResult extends WGResult {

    private static final long serialVersionUID = -7158116627953359527L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 评分
     */
    private Float esScore;

    /**
     * 重排评分
     */
    private BigDecimal rearrangeScore;
}
