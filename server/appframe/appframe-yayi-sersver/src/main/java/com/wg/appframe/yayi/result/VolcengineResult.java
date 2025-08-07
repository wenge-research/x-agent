package com.wg.appframe.yayi.result;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class VolcengineResult implements Serializable {

    private static final long serialVersionUID = 3935941485924157954L;

    private Long code;
    private String message;
    private String request_id;
}
