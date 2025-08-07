package com.wenge.model.dto.result;

import cn.hutool.json.JSONObject;
import com.wenge.model.entity.LlmInfo;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LlmCompletionsResult extends WGResult {

    private static final long serialVersionUID = 3844917475969939620L;

    private LlmInfo llmInfo;

    private JSONObject result;

    private String code;

    private String msg;
    private String traceId;
}
