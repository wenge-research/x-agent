package com.wenge.model.dto.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DialogueCollectRerrangeStepParam {

    private static final long serialVersionUID = 8423288429253667684L;


    /**
     * 文件
     */
    private MultipartFile file;


}
