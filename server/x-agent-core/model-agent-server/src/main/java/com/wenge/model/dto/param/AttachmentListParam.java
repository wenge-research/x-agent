package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttachmentListParam  implements Serializable {
    private static final long serialVersionUID = -639756000534908744L;

    // 附件解析内容
    private String text;

    // 后缀：mp4、wav
    private String extend;

    // 附件地址
    private String videoUrl;
}
