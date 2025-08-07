package com.wenge.model.strategy.documentAnalysis;

import com.wenge.model.entity.File;
import com.wg.appframe.yayi.result.DocumentResult;

import java.util.List;

/**
 * 文档解析策略
 */
public interface DocumentAnalysisStrategy {

    /**
     * 解析文档
     * @param file
     * @return
     */
    List<DocumentResult.FileContent> documentAnalysis(File file);
}
