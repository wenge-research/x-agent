package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileUpdateWebLinkParam extends WGParam {

    private static final long serialVersionUID = -7391139066572564370L;

    private String webLink;
    private Long id;
}
