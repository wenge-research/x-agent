package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Generate128KParam extends YayiParam implements Serializable {

    private static final long serialVersionUID = 7126409194489077271L;

    private List<YayiMessage> messages;
    private Integer max_new_tokens;
    private Boolean do_sample;
    private Float temperature;
    private Float presence_penalty;
    private Float frequency_penalty;
    private Float top_p;
    private Integer top_k;
    private Integer best_of;
    private Boolean stream;
    private Integer n;
    private String accept;
    private String model;
    private Float repetition_penalty;
    private Float encoder_repetition_penalty;
}
