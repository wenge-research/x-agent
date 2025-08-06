package com.wenge.model.dto.result;

import lombok.Data;

import java.util.List;

/**
 * 语音、视频分析结果
 */
@Data
public class LegalAndRegulatoryDateCrawlResult {

    private String catalog;
    private String content;
    private String message;
    private String url;
    private Integer status_code;

    private List<ArticleInfo> subUrl;

    @Data
    public static class ArticleInfo {
        private String content;
        private String message;
        private String url;
        private String title;
        private Integer status_code;
    }
}
