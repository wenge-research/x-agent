package com.wenge.model.dto.param;

import com.alibaba.fastjson2.annotation.JSONField;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollectPlatformFileSaveParam extends WGParam {

    private static final long serialVersionUID = -7391139066572564370L;

    private Integer taskId;

    private String knowledgeId;

    private String foldersId;

    @JSONField(serialize = false)
    private List<FileDataInfo> fileDataInfos;

    @Data
    public static class FileDataInfo {
        private String fileName;
        private String fileUrl;
    }

}
