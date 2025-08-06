package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;


@Data
public class SaveCollectPlatformDataParam {
    /**
     * 采集平台任务id
     */
    private Integer taskId;
    private String knowledgeId;
    private List<KnowledgeDataInfo> knowledgeDataInfos;

    @Data
    public static class KnowledgeDataInfo {
        private String fileName;
        private String fileUrl;
    }

}
