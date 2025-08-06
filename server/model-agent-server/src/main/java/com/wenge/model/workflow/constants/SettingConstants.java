package com.wenge.model.workflow.constants;

import java.util.Arrays;
import java.util.List;

public interface SettingConstants {
    /* 开始节点配置 */
    String START = "start";
    String START_DIALOGUE_FLAG = "multi_dialogue_flag";
    String START_DIALOGUE_NUM = "multi_dialogue_num";
    String START_SYSTEM_PROMT = "system_promt";
    String START_USER_INPUT = "USER_INPUT";
    String START_CONVERSATION_NAME = "CONVERSATION_NAME";
    String START_CONVERSATION_ID = "conversationId";

    /* api节点配置参数 */
    String URL = "url";
    String METHOD = "method";
    String CONTENT_TYPE = "contentType";
    String HEADERS = "headers";
    String RESPONSE_TYPE = "responseType";
    String REQUEST_BODY = "requestBody";
    String RESPONSE_BODY = "responseBody";
    String JSON_PATH = "jsonPath";
    String API_SOURCE = "api_source";
    String API_SOURCE_HTTP = "http";
    String API_SOURCE_PLUGINS = "plugins";

    /* llm节点参数配置 */
    String MODEL_ID = "modelId";
    String USER_PROMPT = "userPrompt";
    String LLM_SYSTEM_PROMPT = "systemPrompt";
    String LLM_TOOLS = "tools";
    String LLM_HISTORY_FLAG = "historyFlag";
    String LLM_RESPONSE_FORMAT = "responseFormat";

    /* 回答策略节点配置 */
    String ANSWER_STRATEGY = "answerStrategy";

    /* 知识库节点配置 */
    String KNOWLEDGE_ID = "knowledgeId";
    String PARAM = "param";
    String TYPE = "type";
    String PROMPT_TEMPLATE = "promptTemplate";
    String FILTER_NUM = "filterNum";
    String PREPARE_NUM = "prepareNum";
    String CONTENT_SCORE = "contentScore";

    String STRATEGY = "strategy";

    /* 结束节点配置 */
    String RESPONSE_TEMPLATE = "responseTemplate";
    String RESPONSE_MODEL = "responseModel";

    /* 排他节点配置 */
    String CASES = "cases";
    String INDEX = "index";

    /* 迭代节点配置 */
    String INNER_COMPONENT_ID = "componentId";
    String INNER_COMPONENT_ID_LIST = "componentldList";

    /* 回复节点配置 */
    String TEMPLATE = "template";
    String IS_STREAM = "isStream";


    /**
     * 结束节点
     */
    String END_CONTENT = "content";
    String END_STREAM_FLAG = "streamFlag";
    String END_STREAM_VAR = "streamVar";
    String END_INTERVAL = "interval";
    String END_MATTER_GUIDE_VAR = "matterGuideVar";
    String FORMAT = "format";
    // 当前推流的位置
    String CURRENT_KEY = "current";
    String REASON_CURRENT_KEY = "reasonCurrent";
    // 变量更新
    String UPDATE_VARIABLE_KEY = "updateVariable";
    // 停止更新标记
    String EXIT_UPDATE_FLAG_KEY = "exitUpdateFlag";
    // 停止推流标记
    String END_STREAM_KEY = "endStream";
    // 完整答案
    String FULL_CONTENT_KEY = "fullContent";
    String FULL_REASON_CONTENT_KEY = "fullReasonContent";
    // 推理字段
    String REASON_VAR = "reasonVar";
    // 自定义推理
    String REASON_FORMATTER = "reasonFormatter";
    String ARRAY_TO_STR_FLAG = "arrayToStrFlag";


    /**
     * 文本分割节点的配置
     */
    String MODEL = "model";
    String TEXT = "text";
    String DELIMITER = "delimiter";
    String PARAM_NAME = "paramName";

    /**
     * 场景节点
     */
    String SCENE_ID = "sceneId";
    String SCENE_OUTPUT_INTERCEPT_FLAG = "interceptFlag";
    String SCENE_OUTPUT_NEW_QUESTION = "newQuestion";
    String SCENE_OUTPUT_MATTER_GUIDE = "matterGuide";
    String SCENE_OUTPUT_SYSTEM_PROMPT = "systemPrompt";
    String SCENE_OUTPUT_ANSWER = "answer";

    /**
     * 代码节点
     */
    String CODE = "code";
    String LANGUAGE = "language";
    String FUNC_NAME = "funcName";
    /**
     * 文档解析节点
     */
    String SLICE = "slice";

    /**
     * 全局变量
     */
    // 全局对话id
    String GLOBAL_DIALOGUE_ID = "globalDialogueId";
    String GLOBAL_TOKEN = "globalToken";
    // 全局会话id
    String GLOBAL_CONVERSATION_ID = "globalConversationId";
    // 全局变量
    String GLOBAL_START = "global";
    // 全局问题
    String GLOBAL_QUESTION = "globalQuestion";
    // 全局uuid
    String GLOBAL_UUID = "globalUuid";
    String GLOBAL_ANSWER = "globalAnswer";
    String GLOBAL_REASON_CONTENT = "globalReasonContent";
    // 问题
    String QUESTION = "question";
    // 回答
    String GLOBAL_FINISHREASON = "concurrentHashMap_finishReason";

    // 大模型信息
    String GLOBAL_LLM_LIST = "globalLlmList";

    // 工作流全局参数列表
    List<String> GLOBAL_PARAMS = Arrays.asList(GLOBAL_DIALOGUE_ID, GLOBAL_CONVERSATION_ID, GLOBAL_START, GLOBAL_QUESTION, GLOBAL_UUID, QUESTION, GLOBAL_FINISHREASON);

    /**
     * 参数提取器
     */
    String EXTRACTION_USER_PROMPT = "userPrompt";
    String EXTRACTION_SYSTEM_PROMPT = "systemPrompt";

    /**
     * 问答节点
     */
    String CONVERSATION_QUESTION = "question";

    /**
     * mcp节点
     */
    // mcp服务 id，数组
    String MCP_SERVER_ID = "mcpServerId";

    // AI生成图片节点配置
    String AI_IMAGE = "aiImage";
    // 模型id
    String AI_IMAGE_MODEL_ID = "aiImageModelId";
    // 用户提示词
    String AI_IMAGE_USER_PROMPT = "aiImageUserPrompt";
    // 参考图
    String AI_IMAGE_URL = "aiImageUrl";
    String AI_IMAGE_WIDTH = "width";
    String AI_IMAGE_HEIGHT = "height";


    // AI生成视频节点配置
    String AI_VIDEO= "aiVideo";
    // 模型id
    String AI_VIDEO_MODEL_ID = "aiVideoModelId";
    // 用户提示词
    String AI_VIDEO_USER_PROMPT = "aiVideoUserPrompt";
    // 首帧参考图
    String AI_VIDEO_IMAGE_URL = "aiVideoImageUrl";
    // 主体参考图
    String AI_VIDEO_SUBJECT_IMAGE_URL = "aiVideoSubjectImageUrl";
    String AI_VIDEO_DURATION = "duration";
    String AI_VIDEO_RESOLUTION = "resolution";
    String AI_VIDEO_RATIO = "ratio";

    // 迭代节点异步执
    String ITERATION_ASYNC_FLAG = "asyncFlag";
    String ITERATION_PARALLEL = "parallel";
    String ITERATION_NEXT_FLAG = "nextFlag";
    String ITERATION_LAST_RESULT = "item_result";
    String ITERATION_PROCESS_NUM = "process_num";
    String ITERATION_ITERATION_NUM = "iterationNum";
    String ITERATION_CURRENT_RESULT = "currentResult";

    // 知识库写入节点配置
    String KNN_WRITE_KNLG_ID = "knowledgeId";
    String KNN_WRITE_FILE_URL = "fileUrl";
    String KNN_WRITE_FILE_NAME = "fileName";
    String KNN_WRITE_FILE_ID = "documentId";


    // mysql 数据库
    String MYSQL_DB_FROM = "dbFrom";
    // 数据库来源于知识库引入
    String MYSQL_DB_FROM_KNOWLEDGE = "knowledge";
    // 使用默认的数据库
    String MYSQL_DB_FROM_DEFAULT = "defaultDb";


    // mysql 写入节点
    String MYSQL_INSERT_KNN_ID = "knowledgeId";
    String MYSQL_INSERT_BIZ_ID = "businessId";
    String MYSQL_INSERT_TABLE_NAME = "tableName";
    String MYSQL_INSERT_ROW_NUM = "rowNum";
    String MYSQL_INSERT_OUTPUT_LIST = "outputList";

    // mysql 自定义sql节点
    String MYSQL_SQL_KNN_ID = "knowledgeId";
    String MYSQL_SQL_BIZ_ID = "businessId";
    String MYSQL_SQL_TABLE_NAME = "tableName";
    String MYSQL_SQL_FORMATTER = "sqlFormatter";
    String MYSQL_SQL_DEFAULT_DATA = "default-data|";
    String MYSQL_SQL_ROW_NUM = "rowNum";
    String MYSQL_SQL_OUTPUT_LIST = "outputList";

    String TRIGGER_CONFIG = "trigger";

    /* agent智能体陪配置 */
    String AGENT_MODEL_ID = "modelId";
    String AGENT_SCENE = "scene";
    String AGENT_SYSTEM_PROMPT = "systemPrompt";
    String AGENT_MCP = "mcpIdList";
    String AGENT_KNN = "knnIdList";
    String AGENT_TYPE = "knnType";
    String AGENT_FROM = "from";
    String AGENT_QUESTION = "fromQuestion";
    String AGENT_REASONING_INDEX = "reasoningContentIndex";
    String AGENT_CONTENT_INDEX = "contentIndex";
    String AGENT_CONDITION_LIST = "conditionList";
    String AGENT_COMPONENT_ID = "componentId";

    /**
     * 数据集节点
     */
    String COLLECT_COLLECT_ID_LIST = "collectIdList";
}
