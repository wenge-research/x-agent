package com.wenge.model.strategy.documentAnalysis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.wenge.model.entity.File;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.constants.DocumentAnalysisConstant.DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI;

/**
 * 雅意内容解析
 */
@Service(DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI)
@Slf4j
public class YayiContentAnalysisStrategy implements DocumentAnalysisStrategy{

    @Autowired
    private YayiServer yayiServer;

    @Override
    public List<DocumentResult.FileContent> documentAnalysis(File file) {
        List<DocumentResult.FileContent> fileContents = Lists.newArrayList();
        ContentParsingNewVersionResult contentParsingNewVersionResult = yayiServer.contentParsingNewVersion(file.getFileUrl());
        ContentParsingNewVersionResult.ContentParsingNewVersionData data = contentParsingNewVersionResult.getData();
        if (null !=data && null != data.getFile_content()) {
            List<ContentParsingNewVersionResult.ContentValue> content = data.getFile_content().getContent();
            if (CollectionUtil.isNotEmpty(content)) {
                fileContents = BeanUtil.copyToList(content, DocumentResult.FileContent.class);
                DocumentResult.Info info = new DocumentResult.Info();
                info.setFile_name(data.getFile_content().getTitle());
                info.setPage_num(1);
                info.setFile_type(data.getFile_content().getType());
                fileContents.forEach(v->{
                    v.setInfo(info);
                });
            }
        }


        return fileContents;
    }

}
