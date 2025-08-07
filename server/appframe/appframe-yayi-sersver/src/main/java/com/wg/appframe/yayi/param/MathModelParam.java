package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MathModelParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = -3545360385052811318L;

    private List<YayiMessage> messages;

    private String uri;
    private Float temperature;
    private Boolean stream;
    private Integer max_try_steps;
    private Integer setMax_new_tokens;
    private Boolean do_sample;
    private Float presence_penalty;
    private Float frequency_penalty;
    private Float top_p;
    private Float top_k;
    private Integer n;

}
