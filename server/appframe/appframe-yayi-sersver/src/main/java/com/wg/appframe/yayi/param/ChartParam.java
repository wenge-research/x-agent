package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChartParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 3056423466533288607L;

    private String url;
    private String model;
    private List<YayiMessage> messages;
    private Boolean stream;
    private Float top_p;
    private Float temperature;
    private Integer max_new_tokens;
    private Boolean do_sample;

}
