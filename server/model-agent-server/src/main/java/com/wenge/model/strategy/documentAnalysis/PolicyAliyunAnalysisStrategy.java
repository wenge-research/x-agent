package com.wenge.model.strategy.documentAnalysis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.result.PolicyAliyunAnalysis;
import com.wenge.model.entity.File;
import com.wenge.model.service.impl.FileServiceImpl;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.constants.DocumentAnalysisConstant.DOCUMENT_ANALYSIS_SERVER_POLICY_ALIYUN;

/**
 * 政策解析，使用阿里云服务
 */
@Service(DOCUMENT_ANALYSIS_SERVER_POLICY_ALIYUN)
@Slf4j
public class PolicyAliyunAnalysisStrategy implements DocumentAnalysisStrategy {

    @Value("${third.pdf.policyAliyun}")
    private String policyAliyun;

    @Override
    public List<DocumentResult.FileContent> documentAnalysis(File file) {
        List<DocumentResult.FileContent> fileContents = Lists.newArrayList();
        JSONObject param = new JSONObject();
        param.put("url", file.getFileUrl());
        log.info("政策阿里云文档解析url:{},请求参数：{}", policyAliyun, param.toJSONString());
        String pdfResponse = HttpUtil.post(policyAliyun, param.toJSONString());
        log.info("政策阿里云文档解析返回结果：{}", pdfResponse);
        if (StringUtils.isBlank(pdfResponse)) {
            // 更新状态，失败
            FileServiceImpl.updateStatusFail(file, "文档解析失败");
        } else {
            JSONObject result = JSON.parseObject(pdfResponse);
            JSONArray data = result.getJSONArray("data");
            List<PolicyAliyunAnalysis> list = data.toList(PolicyAliyunAnalysis.class);
            if (CollectionUtil.isEmpty(list)) {
                // 更新状态，失败
                FileServiceImpl.updateStatusFail(file, "文档解析失败");
                return Lists.newArrayList();
            }
            fileContents = list.stream().map(p -> {
                DocumentResult.FileContent fileContent = new DocumentResult.FileContent();
                fileContent.setText(p.getMarkdownContent());
                return fileContent;
            }).collect(Collectors.toList());
        }

        return fileContents;
    }

}
