package com.wenge.model.strategy.documentAnalysis;

import com.wenge.model.entity.File;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.constants.DocumentAnalysisConstant.DOCUMENT_ANALYSIS_SERVER_YAYI;

/**
 * 雅意智能解析
 */
@Service(DOCUMENT_ANALYSIS_SERVER_YAYI)
@Slf4j
public class YayiAnalysisStrategy implements DocumentAnalysisStrategy{

    @Autowired
    private YayiServer yayiServer;

    @Override
    public List<DocumentResult.FileContent> documentAnalysis(File file) {
        List<DocumentResult.FileContent> fileContents = Lists.newArrayList();
        DocumentResult documentResult = yayiServer.documentAnalysis(file.getFileUrl());
        DocumentResult.DocumentData data = documentResult.getData();
        fileContents.addAll(data.getFile_content());
        return fileContents;
    }

}
