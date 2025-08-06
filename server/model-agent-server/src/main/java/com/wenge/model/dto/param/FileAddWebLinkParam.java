package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileAddWebLinkParam extends WGParam {

    private static final long serialVersionUID = -7391139066572564370L;

    /**
     * 对应的网页地址
     */
    private String webLink;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 文件地址
     */
    private String fileUrl;
}
