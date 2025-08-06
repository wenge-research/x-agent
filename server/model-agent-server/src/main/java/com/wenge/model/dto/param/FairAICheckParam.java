package com.wenge.model.dto.param;

import com.wenge.model.entity.File;
import lombok.Data;

@Data
public class FairAICheckParam {
    /**
     * 上传后的文件
     */
    private File file;

    /**
     * ai审查工作流id
     */
    private String componentId;

    private String clientId;

    private String clientType;
}
