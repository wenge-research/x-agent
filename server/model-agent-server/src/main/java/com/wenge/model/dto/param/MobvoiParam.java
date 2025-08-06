package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class MobvoiParam implements Serializable {

    private static final long serialVersionUID = 96103446414808957L;

    private String text;
    private String signature;
    private String timestamp;
    private String appkey;
    private String speaker;
    private Boolean ignore_limit;
    private Boolean gen_srt;
    private String audio_type;
    private String symbol_sil;
    private Boolean merge_symbol;
    private Float speed;
    private Boolean streaming;

}
