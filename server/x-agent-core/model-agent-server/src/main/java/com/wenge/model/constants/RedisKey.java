package com.wenge.model.constants;

import com.wenge.oauth.constants.RedisConstant;

public interface RedisKey extends RedisConstant {

    // QA库导入进度
    String IMPORTING = BASE + "importing:";

    // url导入进度
    String IMPORTING_URL = BASE + "importing-url:";

    // 保持全量词库
    //String POLICY_WORDS = BASE + "policy_words:";

    // 保存拦截的ip
    String SENSITIVE_IP = BASE + "sensitive:ip:";

    // 保存事项列表
    String SCENE = BASE + "scene:";

    // 保存讨论话题
    String MATTER_DATA = BASE + "matter_data:";
    String MATTER_DETAIL = BASE + "matter_detail:";

    // 保存业务场景
    String BUSINESS_SCENARIO = BASE + "businessScenario:";

    // 关芯客服定时器不重复执行
    String GUANXI_MASTTER= BASE + "guanxi-mastter:";

    // 文档解析定时器不重复执行
    String PARSE_DOC = BASE + "parse-doc:";
    String HK_PARSE_DOC = BASE + "hk-parse-doc:";

    // 雅意文档解析不重复执行
    String PARSE_YAYI_DOC = BASE + "parse-yaiy-doc:";

    // 保存互联网内容
    String NETWORK_CONTENT = BASE + "network_content:";

    // 对话功能划分
    String DIALOG_DIVISION = BASE + "dialog_division:";

    // 国科大的稿件定时器不重复执行
    String STORY = BASE + "story:";

    // 西部国传稿件定时器不重复执行
    String XBGC_STORY = BASE + "xbgc_story:";

    // 重庆大学城根据用户信息推荐问题
    String RECOMMENT_QUESTION = BASE + "recomment_question_by_user:";

    // 大学城活动
    String UNIVERSITY_ACTIVITY = BASE + "university_activity:";

    // 大学城实践
    String UNIVERSITY_PRACTICE = BASE + "university_practice:";

    // 大学城其他类型
    String UNIVERSITY_OTHER = BASE + "university_other:";

    // 大模型 token
    String LLM_API_TOKEN = BASE + "llm_api_token:";

    // 重庆大学城根据用户信息推荐问题
    String ALI_TTS_TOKEN = BASE + "ali_tts_token:";

    String STOP_TOKEN = BASE + "stop_word:";

    String KNOWLEDGE_API_DYNAMIC_TASK = BASE + "knowledge_api_dynamic_task:";

    String WORKFLOW_DYNAMIC_TASK = BASE + "workflow_dynamic_task:";

    String WORKFLOW_DYNAMIC_HANDLER_TASK = BASE + "workflow_dynamic_handler_task:";

    String KNOWLEDGE_API_HANDLER_TASK = BASE + "knowledge_api_handler_dynamic_task:";

    String HK_REQUEST_ID = BASE + "hk_request_id:";
}
