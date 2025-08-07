package com.wenge.oauth.constants;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.wenge.oauth.dto.param.ApplicationConfigurationParam;
import com.wenge.oauth.dto.param.CacheClearParam;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: caohaifeng
 * @date: 2024/8/30 15:23
 * @Description:  应用配置常量缓存池
 *                项目启动加载应用配置缓存 配置变动的时候全局刷新一遍
 * @Version:1.0
 **/

@Component
@Slf4j
public class AppConfigContant {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;
    private static ApplicationConfigurationService applicationConfigurationServiceStatic;
    /**
     * 配置map
     **/
    private static final Map<String, String> CACHE_CONFIG_MAP = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        applicationConfigurationServiceStatic = applicationConfigurationService;
        reload();
    }

    public static String getConfiguration(String key) {
        if(StringUtils.isBlank(key)){
            log.error("您想要获取key在配置信息中不存在，请检查！！！  当前key值为：{}", key);
            new Throwable("您想要获取key在配置信息中不存在，请检查！！！");
            return null;
        }
        return CACHE_CONFIG_MAP.get(key.trim());
    }

    public static String getConfiguration(String appId, String key) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(appId)) {
            log.error("您想要获取key在配置信息中不存在，请检查！！！  当前key值为：{}", key);
            new Throwable("您想要获取key在配置信息中不存在，请检查！！！");
            return null;
        }
        return CACHE_CONFIG_MAP.get(appId.trim() + "@" + key.trim());
    }

    //value值存在多个 使用逗号隔开 转为集合
    public static List<String> getConfigurations(String key) {
        List<String> list = new ArrayList<>();
        if(StringUtils.isBlank(key)){
            log.error("您想要获取key在配置信息中不存在，请检查！！！  当前key值为：{}", key);
            new Throwable("您想要获取key在配置信息中不存在，请检查！！！");
            return null;
        }

        for (String s : CACHE_CONFIG_MAP.get(key.trim()).split(",")) {
            list.add(s);
        }
        return list;
    }

    public static String setConfiguration(String key, String value) {
        clear(new CacheClearParam());
        return null;
    }

    /**
     * 清除缓存
     */
    public static void clear(CacheClearParam param) {
        CACHE_CONFIG_MAP.clear();
        reload();

        Environment environment = CoreContextProvider.getContext().getEnvironment();
        String cluster = environment.getProperty("agentX.cluster");
        // 不仅要关闭当前节点，还要关闭其他节点
        if (StringUtils.isNotBlank(cluster) && !StringConstant.ONE.equals(param.getFromNode())) {
            String[] ipArray = cluster.split(",");
            for (String host : ipArray) {
                try {
                    log.info("关闭其他节点连接：{}", host);
                    if (param == null) {
                        param = new CacheClearParam();
                    }
                    param.setFromNode(StringConstant.ONE);
                    String post = HttpUtil.post("http://" + host + "/cacheClear/clearAppConfig", JSONUtil.toJsonStr(param));
                    log.info("关闭其他节点连接返回：{}", post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String remove(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return CACHE_CONFIG_MAP.remove(key.trim());
    }

    private static void reload() {
        log.info("重新加载应用配置信息...start");
        final ApplicationConfigurationParam applicationConfigurationParam = new ApplicationConfigurationParam();
        applicationConfigurationParam.setPageNo(1);
        applicationConfigurationParam.setPageSize(99999);
        // 获取所有配置
        final List<ApplicationConfiguration> records = applicationConfigurationServiceStatic.getApplicationConfigurationList(applicationConfigurationParam).getData().getRecords();
        records.forEach(item -> {
            CACHE_CONFIG_MAP.put(item.getKeyInfo() != null ? item.getKeyInfo().trim() : "--", item.getValueInfo() != null ? item.getValueInfo().trim() : "--");
            CACHE_CONFIG_MAP.put(item.getKeyInfo() != null ? item.getApplicationId() + "@" + item.getKeyInfo().trim() : "--", item.getValueInfo() != null ? item.getValueInfo().trim() : "--");
        });

        log.info("重新加载应用配置信息...end  配置数量：{}", records.size());
    }

    //自定义一些常量

    // # 默认应用的applicationId,该应用为默认的，可以在在这个应用快速复制一个新的应用
    public static final String APP_CONFIG_DEFAULT_APP_ID = "appConfig.default.appId";
    //  # 发布链接
    public static final String APP_CONFIG_DEFAULT_RELEASE_URL = "appConfig.default.releaseUrl";
    //  # 发布链接
    public static final String APP_CONFIG_DEFAULT_RELEASE_API = "appConfig.default.releaseApi";


    // # 中关村关芯应用配置 # 群众的默认角色编码
    public static final String APP_CONFIG_GUANXIN_H5_RESIDENT_ROLE_CODES = "appConfig.guanxin.h5.residentRoleCodes";
    // # 中关村关芯应用配置 # 工作人员默认的角色编码
    public static final String APP_CONFIG_GUANXIN_H5_STAFF_ROLE_CODES = "appConfig.guanxin.h5.staffRoleCodes";


    // # 中关村关芯应用配置  # 点赞点踩 公开知识库
    public static final String APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID = "appConfig.guanxin.publicKnowledgeId";
    // # 中关村关芯应用配置 # 点赞点踩 不公开/内部知识库
    public static final String APP_CONFIG_GUANXIN_PRIVATE_KNOWLEDGE_ID = "appConfig.guanxin.privateKnowledgeId";
    // # 点踩提交数据是否同步到公开或者私有知识库中 true-同步 false不同步
    public static final String APP_CONFIG_GUANXIN_KNOWLEDGE_ENABLE = "appConfig.guanxin.knowledge.enable";


    // # 中关村关芯应用配置 # 智能客服
    public static final String APP_CONFIG_GUANXIN_ZHIXUN_CHECK_PHONE_URI= "appConfig.guanxin.zhixun.checkPhoneUri";
    public static final String APP_CONFIG_GUANXIN_ZHIXUN_TODAY_NOLOGIN_URI= "appConfig.guanxin.zhixun.todayNoLoginUri";

    // # 龙华gpt # 解析在线政府的文档存放在这个知识库
    public static final String APP_CONFIG_LONGHUA_GPT_KNOWLEDGE_ID= "appConfig.longhuaGPT.knowledgeId";

    // # 龙华gpt # 解析政策的办事指南对应的应用id
    public static final String APP_CONFIG_LONGHUA_GPT_APP_ID = "appConfig.longhuaGPT.appId";

    // 关芯客服应用对话部门划分
    public static final String FUN_DIVISION_APPID = "fun_division_appid";

    // 关芯客服应用对话部门划分的提示语
    public static final String FUN_DIVISION_PROMPT = "fun_division_prompt";

    // 获取改写的指令
    public static final String FILTERING_REWRITING_PROMPT = "filtering_rewriting_prompt";

    // 获取多轮对话改写的指令
    public static final String MULTI_FILTERING_REWRITING_PROMPT = "multi_filtering_rewriting_prompt";

    // 改写策略，YAYI:使用北京的雅意改写服务(默认)，LOCAL:使用本地的改写服务
    public static final String FILTERING_REWRITING_STRATEGY = "filtering_rewriting_strategy";

    // 获取推荐问题指令
    public static final String RECOMMENDED_PROMPT = "recommended_prompt";

    // 推荐问题开关，默认开
    public static final String RECOMMENDED_ENABLE = "recommended_enable";
    // 联想问题开关，默认开
    public static final String ASSOCIATION_ENABLE = "association_enable";
    // 是否开启推荐qa的问题，是/否
    public static final String RECOMMENDED_Q_FLAG = "recommended_q_flag";
    // 是否开启联想qa的问题，是/否
    public static final String ASSOCIATION_Q_FLAG = "association_q_flag";
    // 是否开启推荐qa的答案，是/否
    public static final String RECOMMENDED_A_FLAG = "recommended_a_flag";
    // 是否开启联想qa的答案，是/否
    public static final String ASSOCIATION_A_FLAG = "association_a_flag";

    // 是否开启推荐知识库内容，是/否
    public static final String RECOMMENDED_KNN_FLAG = "recommended_knn_flag";
    // 是否开启联想知识库内容，是/否
    public static final String ASSOCIATION_KNN_FLAG = "association_knn_flag";
    // 是否开启推荐LLM内容，是/否
    public static final String RECOMMENDED_LLM_FLAG = "recommended_llm_flag";
    // 是否开启联想LLM内容，是/否
    public static final String ASSOCIATION_LLM_FLAG = "association_llm_flag";
    // 推荐问题的数量，默认 3
    public static final String RECOMMENDED_NUM = "recommended_num";
    // 联想问题的数量，默认 3
    public static final String ASSOCIATION_NUM = "association_num";

    // 获取联想问题指令

    public static final String ASSOCIATION_PROMPT = "association_prompt";
    // 推荐问题是否启用来源于模糊问题引导，默认否
    public static final String RECOMMENDED_SOURCE_VAGUE_ENABLE = "recommended_source_vague_enable";

    // 互联网检索次数，整数
    public static final String INTERNET_SEARCH_COUNT = "internet_search_count";

    // 互联网数据重排分数阈值,0-1之间
    public static final String INTERNET_REWRITING_SCORE = "internet_rewriting_score";

    // 互联网的redis保留记录数，整数
    public static final String INTERNET_REDIS_COUNT = "internet_redis_count";

    // 是否开启互联网检缓存，是/否，默认否
    public static final String INTERNET_REDIS_ENABLE = "internet_redis_enable";

    // 互联网检索平台，YAYI/metasearch/sc_meta
    public static final String INTERNET_SEARCH_PLATFORM = "internet_search_platform";

    // 默认地区
    public static final String DEFAULT_REGION = "default_region";

    // 微信公众号配置
    // 设置微信公众号的appid
    public static final String WECHAT_APP_ID = "wechat.official.app_id";

    // 设置微信公众号的app secret
    public static final String WECHAT_SECRET = "wechat.official.secret";

    // 设置微信公众号的token
    public static final String WECHAT_TOKEN = "wechat.official.token";

    // 设置微信公众号的EncodingAESKey
    public static final String WECHAT_AES_KEY = "wechat.official.aes_key";

    // RedirectUri
    public static final String WECHAT_REDIRECT_URI = "wechat.official.redirect_uri";

    // 同步用户到关芯智巡系统开关，是/否
    public static final String SYNC_GUANXIN_ZHIXUN_USER_ENABLE = "sync_guanxin_zhixun_user_enable";

    // 同步用户到关芯智巡系统的应用id
    public static final String SYNC_GUANXIN_ZHIXUN_USER_APPID = "sync_guanxin_zhixun_user_appid";

    // 关心智巡注册接口
    public static final String APP_CONFIG_GUANXIN_ZHIXUN_REGISTER_URI= "appConfig.guanxin.zhixun.register";

    // 关芯智巡aes密钥
    public static final String APP_CONFIG_GUANXIN_ZHIXUN_AES_KEY= "appConfig.guanxin.zhixun.aesKey";

    // 提取问题中地点
    public static final String LLM_PROMPT_SYSTEM_WEATHER_ADDRESS = "llm.prompt.system.weatherAddress";

    // 天气城市名称
    public static final String WEATHER_ADDRESS_CITY_NAME = "weather.address.city.name";

    // 天气城市名称拼音
    public static final String WEATHER_ADDRESS_CITY_PINYIN = "weather.address.city.pinyin";

    // 天气摘要
    public static final String LLM_PROMPT_SYSTEM_WEATHER_SUMMARIES = "llm.prompt.user.weather.summaries";

    // 改写问题的模型id
    public static final String REWRITE_LLM_MODEL_NAME = "rewrite.llm.model.name";

    // 是否推送模糊引导开关
    // public static final String PUSH_VAGUE_FLAG = "vague_push_flag";

    // 高危时模型切换，大模型名称
    public static final String RISK_SWITCH_LLM_MODEL_NAMES = "risk_switch.llm.model.names";

    // 知识库文章过滤的system提示语
    public static final String LLM_PROMPT_SYSTEM_KNOWLEDGE_ARTICLE_FILTER = "llm.prompt.system.knowledge.article.filter";

    // 知识库文章过滤是否开启
    public static final String LLM_PROMPT_SYSTEM_KNOWLEDGE_ARTICLE_ENABLE = "llm.prompt.system.knowledge.article.enable";

    // 大模型识别QA场景
    public static final String LLM_PROMPT_SYSTEM_QA_SCENES_FILTER = "llm.prompt.system.qa.scenes.filter";

    // 问答场景是否开启
    public static final String QA_SCENES_FILTER_ENABLE = "qa.scenes.filter.enable";

    // 默认未找到答案的提示语
    public static final String LLM_NO_ANSWER_DEFAULT_TEXT = "llm.no.answer.default.text";

    // 汇总的system提示语
    public static final String LLM_PROMPT_SYSTEM_FINAL_COLLECT_STRATEGY = "llm.prompt.system.finalCollectStrategy";

    // 汇总的user提示语
    public static final String LLM_PROMPT_USER_FINAL_COLLECT_STRATEGY = "llm.prompt.user.finalCollectStrategy";

    // 模型策略的模型名称
    public static final String LLM_LLM_MODEL_STRATEGY_MODEL_NAME = "llm.llmModelStrategy.modelName";

    // 汽车限行城市名称
    public static final String CAR_LIMIT_ADDRESS_CITY_NAME = "carLimit.address.city.name";

    // 汽车限行城市名称拼音
    public static final String CAR_LIMIT_ADDRESS_CITY_PINYIN = "carLimit.address.city.pinyin";

    // 汽车限行提示语
    public static final String LLM_PROMPT_USER_CAR_LIMIT = "llm.prompt.user.carLimit";

    // 系统审核通过后推送到QA开关，是/否
    public static final String SYSTEM_CHECK_PUSH_QA_ENABLE = "system.check.push.qa.enable";

    // 知识库QA推荐问题指令
    public static final String KNOWLEDGE_QA_RECOMMENDED_PROMPT = "knowledge_QA_recommended_prompt";

    // 模糊问题时，给出备选的精确问题来源-目前支持QA和大模型
    // public static final String VAGUE_EXACT_PROBLEM_SOURCE = "vague_exact_problem_source";
    // 模糊问题时，是否启用直接返回精确问题，无需要考虑是否多伦对话，是/否
    public static final String VAGUE_NEED_ENABLE = "vague_need_enable";

    // 设置模糊问题判断大模型指令
    public static final String VAGUE_PROMPT_LLM_USER_PROMPT = "vague_prompt_llm_user_prompt";

    // 设置模糊问题大模型的系统指令
    public static final String VAGUE_PROMPT_LLM_SYSTEM_PROMPT = "vague_prompt_llm_system_prompt";

    // 引导数据来源于知识库，是/否
    public static final String VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE = "vague_prompt_from_knowledge_enable";

    // 引导数据来源于知识库的es分数阈值
    public static final String VAGUE_PROMPT_FROM_KNOWLEDGE_ES_SCORE = "vague_prompt_from_knowledge_es_score";

    // 引导数据来源于知识库的重排分数阈值
    public static final String VAGUE_PROMPT_FROM_KNOWLEDGE_RERANK_SCORE = "vague_prompt_from_knowledge_rerank_score";

    // 用户问题检查的大模型user指令
    public static final  String DOORSILL_LLM_USR_PROMPT = "doorsill_llm_usr_prompt";

    // 模糊问题引导的只做推荐不做回答，默认否，是/否
    public static final String VAGUE_NOT_ANSWER_FLAG = "vague_not_answer_flag";

    // 风险判断开关，默认关，是/否
    public static final String DOORSILL_RISK_ENABLE = "doorsill_risk_enable";

    // 大模型发散：回答风险问题的系统prompt
    public static final String DOORSILL_RISK_LLM_SYSTEM_PROMPT = "doorsill_risk_llm_system_prompt";

    // 最大返回token数
    public static final String MAX_TOKENS = "max_tokens";

    // 模糊问题引导开关，默认关，开/关
    public static final String VAGUE_GUIDE_ENABLE = "vague_guide_enable";
    // 是否打开模糊和风险问题总开关，默认否，是/否
    public static final String VAGUE_RISK_ENABLE = "vague_risk_enable";

    // 识别模糊问题的大模型，给 modelId
    public static final String VAGUE_GUIDE_MODEL_ID = "vagueGuideModelId";
    // 模糊问题引导是否使用QA，是/否
    public static final String VAGUE_GUIDE_QA_ENABLE = "vague_guide_qa_enable";
    // 模糊问题引导是否使用LLM，是/否
    public static final String VAGUE_FROM_LLM_ENABLE = "vague_from_llm_enable";

    // 根据内容组织答案prompt
    public static final String ORGANIZATE_ANSWER_PROMPT = "organizate_answer_prompt";

    // 问答形式检索标题分数阈值配置key
    public static final String QA_TITLE_SCORE_CONFIG = "qa_title_score_config";

    // 问答形式重排正文（回答）分数阈值配置key
    public static final String QA_RANGE_TITLE_SCORE = "qa_range_title_score_config";

    // 引导问题个数配置
    public static final String GUIDE_QUESTION_NUMBER_CONFIG = "guide_question_number_config";

    // 溯源时筛选出符合条件的网页链接
    public static final String NETWORK_SOURCE_REGEX = "network_source_regex";

    // 工作流：分类节点的systemPrompt
    public static final String WORKFLOW_CLASS_SYSTEM_PROMPT = "workflow_ClassificationNode_systemPrompt";

    // 工作流：获取分类节点的historyMsgList
    public static final String WORKFLOW_CLASS_HISTORY_MSG_LIST = "workflow_ClassificationNode_history_msg_list";

    // 工作流：提取参数的systemPrompt
    public static final String WORKFLOW_PARAMETER_SYSTEM_PROMPT = "workflow_parameter_systemPrompt";

    // 工作流：提取参数的tool
    public static final String WORKFLOW_PARAMETER_TOOLS = "workflow_parameter_tools";

    // 工作流：记忆节点的systemPrompt
    public static final String WORKFLOW_MEMORY_SYSTEM_PROMPT = "workflow_memory_systemPrompt";

    // 工作流：记忆节点的historyMsgList
    public static final String WORKFLOW_MEMORY_HISTORY_MSG_LIST = "workflow_memory_history_msg_list";

    //
    public static final String FROM_NETWORK_CONTENT = "from_network_content";

    // 是否开启联网判断 0-否 1-是
    public static final String NETWORK_CHECK_FLAG = "network_check_flag";
    // 免登录账号
    public static final String FREE_LOGIN_USER_NAME = "free_login_user_name";

    // 是否联网回答兜底（是/否）
    public static final String NETWORK_ANSWER_FLAG = "network_answer_flag";

    // 网信办的网页抓取数据应用id
    public static final String APP_CONFIG_CZC_GPT_APP_ID = "app_config_czc_gpt_app_id";

    // 网信办的网页抓取数据知识库id
    public static final String APP_CONFIG_CAC_GPT_KNOWLEDGE_ID = "app_config_cac_gpt_knowledge_id";

    // 问答输入框内容长度限制标识（是-有限制（即保持已有功能） 否-无限制）
    public static final String CONTENT_LENGTH_LIMIT = "content_length_limit";
    public static final String GENERATE_PROMPT = "generate_prompt";

    // 话题讨论指令
    public static final String SUBJECT_TALK_PROMPT = "subject_talk_prompt";

    // 话题讨论场景数
    public static final String SUBJECT_TALK_SCENE_NUM = "subject_talk_scene_num";

    // 识别场景的大模型配置
    public static final String SUBJECT_TALK_LLM_NAME = "subject_talk_llm_name";

    // 大学城匹配场景限制数量
    public static final String SCENE_LIMIT_COUNT = "scene_limit_count";

    // # 西部国传应用 # 获取的稿件里面的url放到这个知识库
    public static final String APP_CONFIG_XBGC_GPT_KNOWLEDGE_ID = "appConfig.xbgcGPT.knowledgeId";

    // 推荐问题个数配置（大学城）
    public static final String RECOMMEDN_QUESTION_NUMBER_CONFIG = "recommend_question_number_config";

    // 问答形式检索标题分数阈值配置key（大学城）
    public static final String QA_TITLE_SCORE_DENSE_CONFIG = "qa_title_score_dense_config";

    // 问答形式检索标题分数阈值配置key（大学城）
    public static final String PREPARE_NUM_CONFIG = "prepare_num_config";

    // 是否开启问题理解模型选择，是/否
    public static final String PLUGIN_MODEL_FLAG = "plugin_model_flag";

    // 是否开启翻译成中文，是/否
    public static final String TRANSLATE_TO_ZH_FLAG = "translate_to_zh";

    // 大模型发散的用户前缀指令
    public static final String USER_MESSAGE_PRE = "user_message_pre";

    // 是否开启内容索引，是/否
    public static final String CONTENT_INDEX_FLAG = "content_index_flag";

    // 联网默认渠道
    public static final String NETWORK_DEFAULT_CHANNEL = "network_default_channel";

    // 获取echarts图系统指令
    public static final String ECHARTS_SYSTEM_PROMPT = "echarts_system_prompt";
    // 是否需要缩减内容
    public static final String TRIM_KNN_CONTENT_FLAG = "trim_knn_content_flag";


    // 以下是默认的内容配置，可在应用的配置页面进行修改
    public static final String ORGANIZATE_ANSWER_PROMPT_DEFAULT = "你是一个用于处理用户提问的AI助手。\n" +
            "###**要求**\n" +
            "1.根据用户发送的问题（可能1个，可能多个），但可能表达不清晰或存在语法等问题。你需要在不改变原意的前提下，将用户的提问整理成尽可能简洁清晰的规范提问，用于引导用户进一步提问。\n" +
            "2.用户如果发送多条问题，你也需要列出多条问题。\n" +
            "###**输出格式**\n" +
            "1.返回的内容应参考以下结构：[引言]+[整理后的问题]\n" +
            "2.用顿号(、)隔开你所输出的各条问题，不需要添加序号。\n" +
            "###**输出内容**\n" +
            "1.对于用户的原始问题中相似度比较高的情况，你在整理时需要将相似度高的问题合并为一个。\n" +
            "2.[引言]的要求参考以下：引言需以“您好”等尊称开头、以逗号结尾，含有对于用户提供的问题主题的分析（以类似“看来您对[问题主题]感兴趣”的形式呈现），要求在体现尊称的前提下引出整理后的问题，请勿出现冒号（：），输出示例：您好，看来您对[问题主题]感兴趣，您是想了解[整理后的问题]等问题吗\n" +
            "###**用户问题**\n" +
            "{question}";

    public static final String DOORSILL_LLM_SYS_PROMPT_DEFAULT = "现在是{date}，你是一个对用户输入问题进行处理的助手，用于对用户输入的原问题进行处理改写，并以通顺的语句对改写后的内容进行输出以引导用户进行提问。${knowledge}。用于请按照以下步骤进行。\n" +
            "##**步骤**\n" +
            "步骤一.针对原问题给出3到5个优化后的新问题。（**【注意】此处的“新问题”为对原问题的改写，而非对原问题进行回答。**）。确保新问题和原问题保持一定的关联性，不要有过大的跳跃，你需要记住这3-5个新问题，以完成步骤二的要求。\n" +
            "#步骤二.你需要将你所记住的新问题中的问题整理成一段引导内容，用来引导用户进行提问。引导内容应参考以下结构：[引言]+[整理后的问题]\n" +
            "##**要求**\n" +
            "1.用顿号(、)隔开你所输出的各条问题，不需要添加序号。\n" +
            "2.[引言]的要求参考以下：引言需包含感谢语和问候语以“您好”开头，含有对于用户提供的问题主题的分析（以类似“看来您对[问题主题]感兴趣，为了更好地帮助您，以下是一些相关的问题：”的形式呈现），要求在体现尊称的前提下引出你所记住的新问题。";

    public static final String DOORSILL_RISK_LLM_SYSTEM_PROMPT_DEFAULT = "现在{date} ，你是一个深圳市龙华区政务智能问答机器人，提供智能问答服务。如果用户问题涉及到政治上的风险，请引导用户往没有风险的方向上指引。";

    public static final String RECOMMENDED_PROMPT_DEFAULT = "{\n" +
            "\t\"role\":\"user\",\"content\":\"\"\"原问题:{question}\n############\n以上是一个民众的提问，请按照以下步骤进行\n" +
            "\t步骤一.首先判断用户输入的“原问题”的描述内容是否模糊。\n" +
            "\t步骤二.如果“原问题”意图不明，则在“新问题”字段中提供5个可能的问题，以列表形式存储；如果“原问题”中包含完整的问题描述，则在“新问题”字段中给出一个优化后的新问题，存在\"\"内。\n" +
            "\t步骤三.请根据历史对话内容揣摩我的问题的真实意图，然后优化我的问题文本，使得符合历史对话的语境。\n" +
            "\t步骤四.如果我的问题与历史对话的语境不相关则忽略历史对话，认真揣摩我的问题的实际意图，根据问题的实际意图优化一下我的问题，使得问题的实际意图更加明显。\n" +
            "\t要求.不要直接回答我的问题，只需要帮我改写提问。确保新问题和原问题保持一定的关联性，不要有过大的跳跃。\\n问题涉及地点:给出提问提及的城市、区县、街道、社区、村、单位机构等地点，如果没有则输出\"不涉及\"。\\n步骤六.请用json格式输出，字段包括;新问题，原问题，问题涉及地点。输出要求:用json{\\\"新问题\\\":\\\"\\\",\\\"原问题\\\":\\\"\\\",\\\"问题涉及地点\\\":\\\"\\\"}输出。\"\"\"\n" +
            "\t}";

    public static final String ASSOCIATION_PROMPT_DEFAULT = "请根据以下信息生成 {count} 个问题，并以 **JSON 数组格式** 返回，例如：\n" +
            "[\"问题1\", \"问题2\", \"问题3\"]\n" +
            "\n" +
            "- 关键词：{keywords}（必须完整、连续出现在每个问题中，不可拆分）\n" +
            "- 历史对话：{history}（数组格式，如 [\"用户：...\", \"助手：...\"]，若无历史对话则为 []）\n" +
            "- 应用名称：{appName}\n" +
            "- 应用简介：{appDesc}\n" +
            "\n" +
            "【规则】\n" +
            "1. 若历史对话非空，请结合上下文生成自然、连贯的问题。\n" +
            "2. 若历史对话为空，则基于应用名称和应用简介，从用户角度生成合理问题。\n" +
            "3. 每个问题必须语义完整、表达自然，且包含完整的关键词。\n" +
            "4. 严格生成 {count} 个问题。\n" +
            "5. 只输出 JSON 数组，不要任何解释、标题、说明或额外文本。\n" +
            "\n" +
            "现在请开始：";
}

