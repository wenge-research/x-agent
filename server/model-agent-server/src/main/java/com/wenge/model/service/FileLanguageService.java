package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.FileLanguage;

import java.util.List;

public interface FileLanguageService extends IService<FileLanguage> {
    /**
     * 获取文件的各个语言版本url
     *
     * @param fileId
     * @return
     */
    List<FileLanguage> getLanguageFile(String fileId);
}
