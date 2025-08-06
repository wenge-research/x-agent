package com.wenge.model.constants;

public interface TaskConstant {

    // url解析提取内容标题的配置key_info值
    String EXTRACT_TITLE_PROMPT = "extract_title_prompt";

    // url解析清洗内容配置key_info值
    String CLEAN_CONTENT_PROMPT = "url_parse_prompt";

    // url解析清洗开始部分内容配置key_info值
    String CLEAN_PREFIX_CONTENT_PROMPT = "clean_prefix_content_prompt";

    // url解析清洗结尾部分内容配置key_info值
    String CLEAN_SUFFIX_CONTENT_PROMPT = "clean_suffix_content_prompt";

    String CLEAN_PREFIX_CONTENT_RESULT = "clean_prefix_content_result";
    String CLEAN_SUFFIX_CONTENT_RESULT = "clean_suffix_content_result";


}
