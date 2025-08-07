package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ZhipuParam implements Serializable {

    private static final long serialVersionUID = 3277190508306818410L;
    private List<YayiMessage> messages;
    private String requestId;
    private String model;
    private Boolean do_sample;
    private Boolean stream;
    private Float temperature;
    private Float top_p;
    private Integer max_tokens;
    private String stop;
    private Object tool_choice;
    private String uri;
    private String appKey;
}
