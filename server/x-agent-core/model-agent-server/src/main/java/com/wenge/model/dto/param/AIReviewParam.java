package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class AIReviewParam {
    private String apiKey;

    private JSONObject file;

    private String clientId;

    private String applicationId;

}
