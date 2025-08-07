package com.wg.appframe.yayi.result;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommoneCompletionResult extends YayiResult{

    private static final long serialVersionUID = 7805169143176622489L;

    private JSONObject llmResult;
}
