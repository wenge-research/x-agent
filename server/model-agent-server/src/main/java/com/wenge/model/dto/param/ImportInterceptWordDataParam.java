package com.wenge.model.dto.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class ImportInterceptWordDataParam implements Serializable {

    private static final long serialVersionUID = -6536474977857884195L;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 敏感词库id
     */
    private Long interceptWordHouseId;

    /**
     * 是否覆盖
     */
    private Boolean replaceFlag;
}
