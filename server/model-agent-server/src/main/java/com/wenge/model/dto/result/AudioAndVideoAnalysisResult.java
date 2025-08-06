package com.wenge.model.dto.result;

import lombok.Data;

import java.util.List;

/**
 * 语音、视频分析结果
 */
@Data
public class AudioAndVideoAnalysisResult {
    /**
     * 分片
     */
    private List<Chunk> timestep_transcription;


    /**
     * 转换结果
     */
    private String transcription;

    @Data
    public static class Chunk {
        private String text;
        private Double start;
        private Double end;
    }
}
