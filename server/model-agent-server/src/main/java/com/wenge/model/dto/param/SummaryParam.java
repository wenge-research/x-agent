package com.wenge.model.dto.param;

import com.wenge.model.entity.FileLanguage;
import lombok.Data;

@Data
public class SummaryParam {
    private Param param;

    private FileLanguage fileLanguage;

    @Data
    public static class Param {
        private String clientId;
        private String content;
        private String type;
    }
}
