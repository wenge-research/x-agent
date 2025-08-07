package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class VisualParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 1038354023613584706L;

    private String url;
    private String model;
    private List<YayiMessage> messages;
    private Boolean stream;
    private Float top_p;
    private Integer top_k;
    private Float temperature;
    private Integer max_new_tokens;
    private Boolean do_sample;

}
