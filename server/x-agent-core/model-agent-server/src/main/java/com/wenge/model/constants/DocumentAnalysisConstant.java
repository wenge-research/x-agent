package com.wenge.model.constants;

/**
 * 文档解析策略
 */
public interface DocumentAnalysisConstant {

    // 雅意智能文档解析
    String DOCUMENT_ANALYSIS_SERVER_YAYI = "yayiAnalysis";
    // 企商环境，本地化部署封装阿里云解析
    String DOCUMENT_ANALYSIS_SERVER_POLICY_ALIYUN = "policy-aliyun";
    // 使用阿里云解析格式
    String DOCUMENT_ANALYSIS_SERVER_LOCAL_DEPLOY = "local-depoly";
    // 雅意文档内容解析
    String DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI = "yayiContentAnalysis";

}
