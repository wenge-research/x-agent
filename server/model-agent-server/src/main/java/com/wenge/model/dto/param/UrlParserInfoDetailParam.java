package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UrlParserInfoDetailParam extends WgPageInfo {

    private static final long serialVersionUID = -7391139066572564370L;

    private String urlId;
}
