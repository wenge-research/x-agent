package com.wenge.model.strategy.documentAnalysis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.File;
import com.wenge.model.service.impl.FileServiceImpl;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.constants.DocumentAnalysisConstant.DOCUMENT_ANALYSIS_SERVER_LOCAL_DEPLOY;

/**
 * 本地化部署解析文档
 */
@Service(DOCUMENT_ANALYSIS_SERVER_LOCAL_DEPLOY)
@Slf4j
public class LocalDeployAnalysisStrategy implements DocumentAnalysisStrategy{

    @Value("${third.pdf.qsAliyun}")
    private String qsAliyun;


    @Override
    public List<DocumentResult.FileContent> documentAnalysis(File file) {
        List<DocumentResult.FileContent> fileContents = Lists.newArrayList();
        JSONObject param = new JSONObject();
        param.put("url", file.getFileUrl());
        log.info("阿里云文档解析url:{},请求参数：{}", qsAliyun, param.toJSONString());
        String pdfResponse = HttpUtil.post(qsAliyun, param.toJSONString());
        log.info("阿里云文档解析返回结果：{}", pdfResponse);
        if (StringUtils.isBlank(pdfResponse)) {
            // 更新状态，失败
            FileServiceImpl.updateStatusFail(file, "文档解析失败");
        } else {
            DocumentResult.DocumentData documentResult = JSONObject.parseObject(pdfResponse, DocumentResult.DocumentData.class);
            fileContents = documentResult.getFile_content();
            if (CollectionUtil.isEmpty(fileContents)) {
                // 更新状态，失败
                FileServiceImpl.updateStatusFail(file, "文档解析失败");
            }
        }

        return fileContents;
    }

}
