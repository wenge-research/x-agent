package com.wenge.model.config.stt.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AudioMessage {
    private Data data;

    @Getter
    @Setter
    public static class Data {

        private String audio;
        private String encoding;
        private String format;
        private Integer status;
    }
}