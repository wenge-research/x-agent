package com.wg.appframe.wos.dto.params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class UploadParam implements Serializable {

    private static final long serialVersionUID = -5305465616496696710L;
    private List<MultipartFile> fileList;

    private MultipartFile file;

    private String filePath;
    private Boolean rename = false;
}
