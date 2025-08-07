package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeductionV2Param extends WGParam {

    private static final long serialVersionUID = 6002977228297887139L;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 应用 id
     */
    private String apiKey;
}
