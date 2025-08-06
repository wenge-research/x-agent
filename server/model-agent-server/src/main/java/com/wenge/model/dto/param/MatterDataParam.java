package com.wenge.model.dto.param;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MatterDataParam extends WgPageInfo {

    private static final long serialVersionUID = 1391334754863274256L;

    private String matterId;

    private String applicationId;

    private JSONObject condition;
}
