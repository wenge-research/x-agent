package com.wenge.model.config.stt.message;

import lombok.Data;

import java.util.List;

@Data
public class AliyunMessage {

    private Header header;
    private Payload payload;

    @Data
    public static class Header {
        // 通用
        private String namespace;
        private String name;
        private String task_id;
        private String message_id;
        // 请求
        private String appkey;
        // 响应
        private Integer status;
        private String status_text;
    }

    @Data
    public static class Payload {
        // 请求参数
        private String format;
        private Integer sample_rate;
        private Boolean enable_intermediate_result;
        private Boolean enable_punctuation_prediction;
        private Boolean enable_inverse_text_normalization;

        // 响应参数
        private Integer index;
        private Integer time;
        private String result;
        private Double confidence;
        private List words;
        private Integer status;
        private String fixed_result;
        private String unfixed_result;

    }
}
