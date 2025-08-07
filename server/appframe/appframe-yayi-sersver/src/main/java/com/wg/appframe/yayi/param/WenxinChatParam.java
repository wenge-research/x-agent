package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WenxinChatParam implements Serializable {

    private static final long serialVersionUID = 572372925479207306L;
    private List<YayiMessage> messages;
    private Float temperature;
    private Float top_p;
    private Float penalty_score;
    private Boolean stream;
    private Boolean enable_system_memory;
    private String system_memory_id;
    private String system;
    private List<String> stop;
    private Boolean disable_search;
    private Boolean enable_citation;
    private Boolean enable_trace;
    private Integer max_output_tokens;
    private String user_id;
    private String uri;
    private String appKey;
    private String appSecret;
    private String response_format;

}
