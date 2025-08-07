package com.wenge.model.dto.param;

import lombok.Data;

@Data
public class YouyaAddCatalogParam {

    // 视频url
    private String videoUrl;
    // 回调地址
    private String callbackUrl;
    //
    private String requestId;
    // 文件id
    private String fileId;
}
