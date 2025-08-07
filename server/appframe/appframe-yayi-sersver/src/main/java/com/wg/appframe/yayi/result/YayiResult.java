package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class YayiResult implements Serializable {

    private static final long serialVersionUID = 3935941485924157954L;

    private String id;
    private Long code;
    private String msg;
    private String time;
    private Boolean retryFlag;
}
