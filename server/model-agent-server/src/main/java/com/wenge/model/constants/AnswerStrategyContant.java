package com.wenge.model.constants;

public interface AnswerStrategyContant {

    /**
     * 异常情况
     */
    String ERROR_STRATEGY = "errorStrategy";


    /**
     * 问题明确推荐返回
     */
    String RECOMMENDED_STRATEGY = "recommendedStrategy";

    /**
     * 问题明确，根据知识库QA的推荐返回
     */
    String VAGUE_LLM_STRATEGY = "vague_llm_strategy";

    /**
     * 问答【答案】
     */
    String FIND_QA_CONTENT = "findQaContent";

    /**
     * 问答【问题】
     */
    String FIND_QA_TITLE = "findQaTitle";

    /**
     * 文档库
     */
    String DOCUMENT_LIBRARY = "documentLibrary";

    /**
     * 工作流策略
     */
    String WORKFLOW_STRATEGY = "workflowStrategy";
    String WORKFLOW_STEP = "工作流-原始数据";


    /**
     * 附件策略
     */
    String ATTACHMENT_STRATEGY = "attachmentStrategy";

    String ATTACHMENT_STEP = "附件解析-原始数据";

    /**
     * 附件
     */
    String ATTACHMENT_LIBRARY = "attachmentLibrary";

    /**
     * 附件策略
     */
    String API_STRATEGY = "apiStrategy";

    String API_STEP = "API解析-原始数据";

    /**
     * 附件
     */
    String API_LIBRARY = "apiLibrary";

    /**
     * 多媒体库
     */
    String MULTI_MEDIA_LIBRARY = "multiMediaLibrary";

    /**
     * 网络策略
     */
    String NETWORK_STRATEGY = "networkStrategy";

    /**
     * 模型发散
     */
    String FIND_ANSWER_BY_MODEL = "findAnswerByModel";

    /**
     * 知识库
     */
    String KNOWLEDGE_CONTENT = "knowledgeContent";

    /**
     * 拦截敏感词
     */
    String INTERCEPT_SENSITIVE = "interceptSensitive";

    /**
     * 主题对话
     */
    String SUBJECT_TALK = "subjectTalk";

    /**
     * 业务场景 市监局
     */
    String BUSINESS_SCENARIO = "businessScenario";


    /**
     * 问数来源
     */
    String WENSHU_CONTENT = "wenshuContent";

    /**
     * 机构建议
     */
    String SUGGEST_ORG = "suggestOrg";

    /**
     * 汇总收集
     */
    String FINAL_COLLECT = "finalCollectStrategy";

    /**
     * 内置问题
     */
    String BUILT_IN =  "builtIn";

    /**
     * 雅意知识库
     */
    String YAYI_KNOWLEGE = "yayiKnowldegeStrategy";

    /**
     * 网页数据
     */
    String WEBSITE_STRATEGY = "WebsiteStrategy";

    /**
     * 结构化数据
     */
    String STRUCTURED_STRATEGY  = "structuredStrategy";

    /**
     * mcp策略
     */
    String MCP_STRATEGY  = "mcpStrategy";

    String MCP_STEP = "mcp 服务-原始数据";
    String MCP_ROUTE = "mcp";

    /**
     * 组件策略
     */
    String COMPONENT = "component";

    String REF_PREFIX = "引用知识库来源-";

    String NO_ANSWER_TEXT = "无法回答";
    String DIALOGUE_ID = "会话id";

    String FINAL_COLLECT_STEP = "汇总所有知识库-原始数据";
    String FINAL_COLLECT_REF = REF_PREFIX + "汇总";
    String FINAL_COLLECT_ANSWER = "汇总-大模型回答";
    String FINAL_COLLECT_RANGE = "汇总-重排";
    String FINAL_COLLECT_FILTER = "汇总-过滤";

    String ERROR_STEP = "异常";

    String FIND_QA_TITLE_STEP = "政策问答库【问题】";
    String FIND_QA_TITLE_REF = REF_PREFIX + "问答库【问题】";
    String FIND_QA_TITLE_ANSWER = "问答库-重排【问题】";
    String FIND_QA_TITLE_ROUTE = "QA对问答【问题】";
    String FIND_QA_FILTER = "QA-过滤";

    String FIND_QA_CONTENT_STEP = "政策问答库【回答】";
    String FIND_QA_CONTENT_REF = REF_PREFIX + "问答库【回答】";
    String FIND_QA_CONTENT_ANSWER = "问答库-重排【回答】";
    String FIND_QA_CONTENT_ROUTE = "QA对问答【回答】";

    String KNOWLEDGE_CONTENT_STEP = "政策向量库-向量库数据";
    String KNOWLEDGE_CONTENT_ROUTE = "政策向量库";

    String WENSHU_CONTENT_STEP = "问数搜索-原始数据";

    String DOCUMENT_STEP = "文件库-原始文件数据";
    String DOCUMENT_ROUTE = "文件库";

    String LLM_ANSWER = "大模型常识发挥";

    String FIND_ANSWER_BY_MODEL_REF = REF_PREFIX + "大模型发散";

    String BUILT_IN_STEP =  "内置问题";
    String BUILT_IN_REF = REF_PREFIX + "内置问题";
    String BUILT_IN_ROUTE = "内置问题";

    String BUSINESS_SCENARIO_STEP = "业务场景";
    String BUSINESS_SCENARIO_NO_DATA = "没有配置业务场景策略";
    String BUSINESS_SCENARIO_NO_SCENE = "没有找到对应业务场景";
    String BUSINESS_SCENARIO_ANSWER = BUSINESS_SCENARIO_STEP + "指定回答：";
    String BUSINESS_SCENARIO_NO_WAY = "没有处理方式";
    String BUSINESS_SCENARIO_TALK_REF = REF_PREFIX + "业务场景";


    String SUBJECT_STEP = "讨论话题";
    String SUBJECT_NO_DATA = "没有配置主题策略";
    String SUBJECT_NO_SCENE = "没有找到对应场景";
    String SUBJECT_ANSWER = SUBJECT_STEP + "指定回答：";
    String SUBJECT_NO_WAY = "没有处理方式";
    String SUBJECT_TALK_REF = REF_PREFIX + "讨论话题";

    String NETWORK_STEP = "互联网原始数据";
    String NETWORK_REF = REF_PREFIX + "互联网";
    String NETWORK_ANSWER = "互联网-大模型回答";
    String NETWORK_ROUTE = "互联网";

    String YAYI_KNOWLEGE_STEP = "雅意知识库原始数据";
    String YAYI_KNOWLEGE_REF = REF_PREFIX + "雅意知识库";
    String YAYI_KNOWLEGE_REARANGE = "雅意知识库-重排";
    String YAYI_KNOWLEGE_LLM = "雅意知识库-大模型回答";
    String YAYI_KNOWLEGE_ROUTE = "雅意知识库";

    String WEBSITE_STEP = "网页库-原始数据";
    String WEBSITE_ROUTE = "网站网页库";

    /**
     * 问题理解
     */
    String UNDERSTANDING = "问题拆解";

    /**
     * 重排分隔符  策略===>索引===>内容
     */
    String REARRANGE_SPLIT_CHAR = "===>";
    String NO_KNOWLEDGE = "暂无知识库";

    String INTERCEPT = "拦截";
    String INTERCEPT_PASS = "通过" + INTERCEPT;
    String INTERCEPT_NOT_PASS = "不通过:";

    String INTERCEPT_IP_STEP = INTERCEPT + "IP";
    String INTERCEPT_IP_NOT = INTERCEPT + "IP" + INTERCEPT_NOT_PASS;

    String INTERCEPT_ALL_QUESTION = INTERCEPT + "问题全词";
    String INTERCEPT_ALL_QUESTION_NOT = INTERCEPT + "问题全词" + INTERCEPT_NOT_PASS;

    String INTERCEPT_QUESTION = INTERCEPT + "问题部分词";
    String INTERCEPT_QUESTION_NOT = INTERCEPT + "问题部分词" + INTERCEPT_NOT_PASS;

    String INTERCEPT_QUESTION_FORBIDDEN = INTERCEPT + "问题禁用词";
    String INTERCEPT_QUESTION_FORBIDDEN_NOT = INTERCEPT + "问题禁用词" + INTERCEPT_NOT_PASS;

    String INTERCEPT_YAYI_SECRI = INTERCEPT + "yayi判别敏感词";
    String INTERCEPT_YAYI_SECRI_NOT = INTERCEPT + "yayi判别敏感词" + INTERCEPT_NOT_PASS;

    String MULTI_MEDIA_STEP = "多媒体库-原始多媒体数据";
    String MULTI_MEDIA_ROUTE = "多媒体库";

    String STRUCTURED_STEP = "结构化数据-原始数据";
    String STRUCTURED_ROUTE = "结构化数据";

    String SUBJECT_WAY_FIELD = "way";
    String SUBJECT_WAY_NONE = "不处理";
    String SUBJECT_WAY_INTERCEPT = "拦截";
    String SUBJECT_WAY_IN = "内置问题";
    String BUSINESS_SCENARIO_WAY_IN = "业务场景内置";
    String SUBJECT_WAY_QUE = "问答【问题】";
    String SUBJECT_WAY_ANS = "问答【回答】";
    String SUBJECT_WAY_KNOWLEDGE = "知识库";
    String SUBJECT_WAY_LLM = "大模型";
    String SUBJECT_WAY_MATTER = "事项";
    String SUBJECT_WAY_LIFE_SERVE = "生活服务";
    String SUBJECT_WAY_ACTIVITY = "活动";
    String SUBJECT_WAY_PRACTICE = "实践";
    String SUBJECT_WAY_WEATHER = "天气";
    String SUBJECT_WAY_CAR_LIMIT = "汽车限号";
    String SUBJECT_SCENE_ENTRANCE = "事项-场景入口";
    String MATTER_LLM_STEP = "事项大模型回答";
    String LIFE_SERVE_LLM_STEP = "生活服务大模型回答";
    String ACTIVITY_LLM_STEP = "活动大模型回答";
    String PRACTICE_LLM_STEP = "实践大模型回答";
    String WEATHER_LLM_ADDR = "天气大模型提取地点";
    String WEATHER_LLM_STEP = "天气大模型回答";
    String CAR_LIMIT_LLM_ADDR = "汽车限号提取地点";
    String CAR_LIMIT_LLM_STEP = "汽车限号大模型回答";
    String SUBJECT_ANSWER_FIELD = "answer";
    String SUBJECT_PRE_QUE_FIELD = "preQuestion";
    String SUBJECT_EXT_QUE_FIELD = "extendQuestion";
    String SUBJECT_REP_QUE_FIELD =  "replaceQuestion";

    String QUESTION_PLACEHOLDER = "{question}";
    String WORKFLOW_QUESTION_PLACEHOLDER = "$" + QUESTION_PLACEHOLDER;
    String ORIGIN_QUESTION_PLACEHOLDER = "{originQuestion}";
    String ANSWER_PLACEHOLDER = "{answer}";
    String NO_ANSWER_PLACEHOLDER = "{noAnswer}";
    String CONTENT_PLACEHOLDER = "{content}";
    String DATE_PLACEHOLDER = "{date}";
    String SIZE_PLACEHOLDER = "{size}";
    String NUM_PLACEHOLDER = "{num}";
    String SCENES_PLACEHOLDER = "{scenes}";

    String TITLE_DENSE_FILED = "titleDense";
    String CONTENT_DENSE_FILED = "contentDense";
    String CONTENT_DENSE_FILED_1024 = "contentDense1024";

    String CHANGE_CONTENT_RECOMMENDED = "问题意图推荐";
    String VAGUE_LLM_STEP = "模糊问题大模型环节";
    String VAGUE_LLM_RESULT = "模糊问题大模型引导";
    String CHANGE_CONTENT_RECOMMENDED_NO = "没有开启问题意图推荐";

    String KNOWLEDGE_QA_CHANGE_CONTENT_RECOMMENDED = "QA模糊问题引导推荐";
    String LLM_CHANGE_CONTENT_RECOMMENDED = "大模型模糊问题引导推荐";
    String CHANGE_CONTENT_QA_NO = "模糊问题QA引导";
    String DOORSILL_CHECK_STEP = "检查用户问题";
    String RISK_STEP = "用户问题有风险";

    String CHANGE_CONTENT_STEP = "改写环节";
    String CHANGE_CONTENT_LOCAL_RESULT = "改写环节-本地";
    String YAYI_CHANGE_CONTENT_RESULT = "改写环节-雅意";
    String CHANGE_CONTENT_NO = "没有开启改写";
    String CHANGE_CONTENT_BUILT_IN = "内置问题，跳过改写";

    String RERANGE_TAG = "rerange:";
    String COLLECCT_REARRANGE_RESULT = "rearrangeResul";
    String COLLECCT_REARRANGE_FILTER_RESULT = "rearrangeFilterResul";
    String COLLECCT_REARRANGE_SCORE = "rearrangeScore";
    String POLISH_STEP = "润色";

    String SCENE_NUM_PLACEHOLDER = "{sceneNum}";

    // 前缀符号
    String PREFIX_SYMBOL = "@【";

    // 后最符号
    String SUFFIX_SYMBOL = "】@";
    String MID_ONE_SYMBOL = "_";

    // embedding向量化模型策略
    String LOCAL_M3E_768 = "local_m3e_768";
    String LOCAL_BGE_768 = "local_bge_768";
    String YAYI_EMBEDDING_2_1024 = "YAYI_Embedding_2_1024";
    String LOCAL_BGE_M3_1024 = "local_bge_m3_1024";

    // 字段分隔符
    String FIELD_SEPARATOR = "|";

    // LLM JSON标识
    String LLM_JSON_FLAG = "llmJson";

    // 大模型参数
    String LLM_PARAM_OBJECT = "llmParamObject";
    // 大模型id
    String LLM_PARAM_MODEL_ID = "llmParamModelId";

    // 原问题 字段
    String ORIGINAL_QUESTION_FIELD = "原问题";

    // 新问题 字段
    String NEW_QUESTION_FIELD = "新问题";

    // 问题涉及地点 字段
    String QUESTION_LOCATION_FIELD = "问题涉及地点";

    // 不涉及 字段
    String NO_RELATED = "不涉及";

    // 天气地址参数
    String WEATHERADDRESS_PARAM = "weatherAddressParam";

    // 天气地址结果
    String WEATHERADDRESS_RESULT = "weatherAddressResult";
    String WEATHERLLM_PROMPT = "weatherLlmPrompt";
    String WEATHERLLM_RESULT = "weatherLlmResult";
    String FUTURE_WEATHER_ID = "futureWeatherId";
    String REAL_TIME_WEATHER_ID = "realTimeWeatherId";
    String WEATHER_PLACEHOLDER = "${weather}";
    String CITY_PLACEHOLDER = "${city}";
    String QUESTIONS_PLACEHOLDER = "${question}";
    String HISTORY_QUESTION_PLACEHOLDER = "${history_question}";
    String DATES_PLACEHOLDER = "${date}";
    String WEEK_PLACEHOLDER = "${week}";
    String TIME_PLACEHOLDER = "${time}";
    String TIMESTAMP_PLACEHOLDER = "${timestamp}";
    String UUID_PLACEHOLDER = "${uuid}";

    String LLM_ERROR_RISK = " risk";

    // 场景号
    String SCENE_NO = "场景号";

    // 换行标识
    String BR = "</br>";

    // json标识
    String JSON_START = "```json";

    // json结束标识
    String JSON_END = "```";
    // json转义符
    String JSON_ESCAPE = "\\\"";
    // 双引号
    String JSON_QUOTE = "\"";
    // 换行符
    String NEW_LINE = "\n";

    // AnswerUtils的常量池

    // 英文问号
    String QUESTION_MARK = "?";

    // 中文问号
    String QUESTION_MARK_CN = "？";

    // 分隔符
    String SPLIT_CHAR = "==>";

    // 标题字段
    String TITLE_FIELD = "title";

    // 问题的开始符号
    String Q_START_SYMBOL = "『";

    // 问题的结束符号
    String Q_END_SYMBOL = "』";

    // 系统提示字段
    String SYSTEM_PROMPT_FIELD = "system";
    String USER_PROMPT_FIELD = "user";
    String ASSISTANT_ROLE = "assistant";
    String YAYI_ROLE = "yayi";

    // 问题字段
    String QUESTION_FIELD = "\n\n问题：";

    // 双引号替代符号
    String QUOTE_REPLACE_SYMBOL = "-|-";

    // 答案字段
    String ANSWER_FIELD = "答案";

    // 引用字段
    String REFERENCE_FIELD = "引用知识库";

    // 没有引用字段
    String NO_REFERENCE_FIELD = "没有相关知识库";


    // ES的属性
    // 删除状态字段
    String DEL_STATUS_FIELD = "delStatus.keyword";
    // 知识库id字段
    String KNOWLEDGE_ID_FIELD = "knowledgeId.keyword";
    // 精准匹配字段
    String ACCURATE_FIELD = "accurate.keyword";
    // 内容字段
    String CONTENT_FIELD = "content";
    // 向量检索字段
    String QUERY_VECTOR_FIELD = "queryVector";
    // 脚本类型字段
    String SCRIPT_TYPE = "painless";

    // QA策略名称
    String QA_STRATEGY_NAME ="qa-question";

    // 大模型输出“是否模糊”的json的key
    String IS_BLURRY = "是否模糊";

    String IS_RISK = "是否存在风险";

    // 空问题
    String EMPTY_QUESTION = "空问题";

    // 根据知识库QA推荐问题执行步骤
    String RECOMMENDED_KNOWLEDGE_QA_MESSAGE = "执行QA问题意图推荐步骤";

    // LLM 大模型的标记
    String LLM = "LLM";

    // QA  知识库问答库的标记
    String QA = "QA";

    // 对话答案大纲分隔符
    String ANSWER_OUTLINE_SPLIT_CHAR = "&&";
    String ANSWER_OUTLINE_SPLIT_CHAR_ONE = "==>";
    String ANSWER_OUTLINE_SPLIT_CHAR_TWO = "&";

    // 工作流应用类型
    String APP_TYPE_WORKFLOW = "workflow";
    // 智能搜索应用类型
    String APP_TYPE_SEARCH = "search";
    // 智能搜索和智能问答应用类型
    String APP_TYPE_DIALOGUE_SEARCH = "dialogue-search";
    // 智能问答应用类型
    String APP_TYPE_QA = "qa";


    // 大模型插件参数
    String LLM_PARAM_TOOL = "llmParamTool";

    // 大模型prompt模板参数占位符
    String LLM_PARAM_PROMPT_TEMPLATE = "${description}";

    // 大模型prompt模板json的键占位符
    String LLM_JSON_KEY_PROMPT_TEMPLATE = "${key}";

    // 错误消息
    String ERROR_APP_NOT_EXIST = "应用不存在或异常";
    String ERROR_KNOWLEDGE_NOT_EXIST = "未绑定知识库";

    // 多轮对话指令所需
    String MULTI_QUESTION = "问题：";
    String MULTI_ANSWER = "回答：";


    // 提取的关键词
    String EXTRACT_KEYWORD = "keyword";

    // 提取的关键词
    String EXTRACT_TYPE = "type";

    // 推理过程
    // 推理开始标记
    String INFERENCE_START_TAG = "<span style=\"color: gray;\">";

    // 推理结束标记
    String INFERENCE_END_TAG = " </br> 以上为推理过程 </span> <hr style=\"background-color: grey;\"> </br>";
    String THINK_START = "<think>";
    String THINK_END= "</think>";

    // 重庆大学城公网
    String UNIVERSITY_ORIGIN_ADDRESS = "http://220.194.188.77:9299/cqApi/infrastructure-app";
    String UNIVERSITY_ORIGIN_ADDRESS_IP = "http://220.194.188.77:9299/cqH5";

    // 智川平台域名
    String PLATFORM_DOMAIN = "https://city.wengegroup.com/cqApi";
    String PLATFORM_DOMAIN_TWO = "https://city.wengegroup.com/cqH5";

    /**
     * qa-question 问题策略
     */
    String QA_QUESTION_STRATEGY = "qa-question";

    String QA_CONTENT_STRATEGY = "qa-content";

    String QA_LARGEMODEL_STRATEGY = "qa-largemodel";
    String QA_KNOWLEDGE_STRATEGY = "knowledge";
}
