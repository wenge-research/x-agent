package com.wg.appframe.yayi.param;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommoneCompletionParam extends YayiParam {

    private static final long serialVersionUID = 989113104549379558L;

    private JSONObject llmParam;
    private Boolean stream;

    /**
     * 策略,common,simply
     */
    private String strategy;
}
