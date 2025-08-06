package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionJsonSaveParam extends WGParam {

    private static final long serialVersionUID = 6812993445406083469L;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * json详情
     */
    private JSONObject detail;
}
