package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionsReportResult extends WGResult {

    private static final long serialVersionUID = -7033586071236637277L;

    /**
     * 报告html内容
     */
    private String html;

    /**
     * markdown内容
     */
    private String markdown;

    /**
     * 标题
     */
    private String title;
}
