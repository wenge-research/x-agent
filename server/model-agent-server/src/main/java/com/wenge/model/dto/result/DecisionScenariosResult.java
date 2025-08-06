package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionScenariosResult extends WGParam {

    private static final long serialVersionUID = -1701710866553104884L;

    private String filename;
    private String title;
    private String description;
    private String event;
    private Integer agents_count;
    private String created_date;
}
