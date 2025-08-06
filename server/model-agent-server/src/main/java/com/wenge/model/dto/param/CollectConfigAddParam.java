package com.wenge.model.dto.param;

import com.wenge.model.dto.result.CollectFileTable;
import com.wenge.model.entity.CollectBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollectConfigAddParam extends CollectBaseConfig {

    private static final long serialVersionUID = 6815736820210985852L;

    /**
     * 文件上传
     */
    private List<MultipartFile> files;

    /**
     * 配置需要和文件保持对应
     */
    private List<String> configNames;

    /**
     * 文件上传
     */
    private MultipartFile file;

    /**
     * 数据源基础信息
     */
    private CollectFileTable dbConfig;
}
