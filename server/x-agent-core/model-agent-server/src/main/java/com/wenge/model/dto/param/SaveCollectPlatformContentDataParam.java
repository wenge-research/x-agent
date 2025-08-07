package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;


@Data
public class SaveCollectPlatformContentDataParam {
    /**
     * 采集平台任务id
     */
    private Integer taskId;
    private String knowledgeId;
    private List<KnowledgeContentDataInfo> knowledgeDataInfos;

    @Data
    public static class KnowledgeContentDataInfo {
        private String url;
        private String title;
        private String content;
    }

}
