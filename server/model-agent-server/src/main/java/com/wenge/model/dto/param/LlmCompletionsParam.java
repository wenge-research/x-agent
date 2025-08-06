package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LlmCompletionsParam extends WGParam {

    private static final long serialVersionUID = -1761048095505920362L;

    /**
     * 应用 id
     */
    private String applicationId;

    /**
     * 大模型 id
     */
    private String modelId;

    /**
     * 客户端 id
     */
    private String clientId;

    /**
     * 大模型参数
     */
    private JSONObject llmParam;
}
