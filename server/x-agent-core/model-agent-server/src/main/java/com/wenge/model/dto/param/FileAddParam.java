package com.wenge.model.dto.param;

import com.alibaba.fastjson2.annotation.JSONField;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileAddParam extends WGParam {

    private static final long serialVersionUID = -7391139066572564370L;

    private String foldersId;
    @JSONField(serialize = false)
    private List<MultipartFile> files;
}
