package com.wenge.model.entity;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Description: 应用信息实体类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:50
 */
@ApiModel
@Data
@Table(value = "application_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ApplicationInfo implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	/**
	 * 应用版本号
	 */
	@Column("app_version_number")
	@ApiModelProperty(name = "appVersionNumber", value = "应用版本号", dataType = "String")
	private String appVersionNumber;
	/**
	 * 操作类型
	 */
	@Column("make_type")
	@ApiModelProperty(name = "makeType", value = "操作类型", dataType = "String")
	private String makeType;
	/**
	 * 应用apikey
	 */
	@Column("api_key")
	@ApiModelProperty(name = "apiKey", value = "应用apikey", dataType = "String")
	private String apiKey;

	/**
	 * 应用密钥
	 */
	@Column("api_secret")
	@ApiModelProperty(name = "apiSecret", value = "应用密钥", dataType = "String")
	private String apiSecret;

	/**
	 * 应用id，有业务作用
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id，有业务作用", dataType = "String")
	private String applicationId;

	/**
	 * 应用名称
	 */
	@Column("application_name")
	@ApiModelProperty(name = "applicationName", value = "应用名称", dataType = "String")
	private String applicationName;

	/**
	 * 应用编码
	 */
	@Column("application_code")
	@ApiModelProperty(name = "applicationCode", value = "应用编码", dataType = "String")
	private String applicationCode;

	/**
	 * 网页图标icon
	 */
	@Column("web_icon")
	@ApiModelProperty(name = "webIcon", value = "网页图标icon", dataType = "String")
	private String webIcon;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 创建人
	 */
	@Column(value = "create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	/**
	 * 是否删除[1-删除,0-未删除]
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
	private String deleteFlag;

	/**
	 * 无法回答话术
	 */
	@Column("failure_talk")
	@ApiModelProperty(name = "failureTalk", value = "无法回答话术", dataType = "String")
	private String failureTalk;

	/**
	 * 欢迎语
	 */
	@Column("greeting")
	@ApiModelProperty(name = "greeting", value = "欢迎语", dataType = "String")
	private String greeting;

	/**
	 * 主键，自增id，没有业务作用
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键，自增id，没有业务作用", dataType = "Long")
	private Long id;

	/**
	 * 应用介绍
	 */
	@Column("introduce")
	@ApiModelProperty(name = "introduce", value = "应用介绍", dataType = "String")
	private String introduce;

	/**
	 * 知识库回答[是/否]
	 */
	@Column("knowledge_flag")
	@ApiModelProperty(name = "knowledgeFlag", value = "知识库回答[是/否]", dataType = "String")
	private String knowledgeFlag;

	/**
	 * 安全敏感词拦截开关[是/否]
	 */
	@Column("sensitive_flag")
	@ApiModelProperty(name = "sensitiveFlag", value = "安全敏感词拦截[是/否]", dataType = "String")
	private String sensitiveFlag;

	/**
	 * 讨论话题开关[是/否]
	 */
	@Column("subject_flag")
	@ApiModelProperty(name = "subjectFlag", value = "安全敏感词拦截[是/否]", dataType = "String")
	private String subjectFlag;

	/**
	 * 模型回答[是/否]
	 */
	@Column("model_answer_flag")
	@ApiModelProperty(name = "modelAnswerFlag", value = "模型回答[是/否]", dataType = "String")
	private String modelAnswerFlag;

	/**
	 * 是否联网检索[是/否]
	 */
	@Column("network_flag")
	@ApiModelProperty(name = "networkFlag", value = "是否联网检索[是/否]", dataType = "String")
	private String networkFlag;
	/**
	 * 是否联网搜索[是/否]
	 */
	@Column("network_web_searching_flag")
	@ApiModelProperty(name = "networkWebSearchingFlag", value = "是否联网搜索[是/否]", dataType = "String")
	private String networkWebSearchingFlag;

	/**
	 * 是否模型兜底  需要在编辑的时候更新字段processStep中的模型发散（最后）
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "modelFallbackFlag", value = "是否模型兜底[是/否]", dataType = "String")
	private String modelFallbackFlag;

	/**
	 * 是否开启问数检索[是/否]
	 */
	@Column("wenshu_flag")
	@ApiModelProperty(name = "wenshuFlag", value = "是否开启问数检索[是/否]", dataType = "String")
	private String wenshuFlag;


	/**
	 * 发布链接
	 */
	@Column("client_link")
	@ApiModelProperty(name = "clientLink", value = "发布链接[移动端]", dataType = "String")
	private String clientLink;

	/**
	 * logo地址
	 */
	@Column("logo")
	@ApiModelProperty(name = "logo", value = "logo地址", dataType = "String")
	private String logo;

	/**
	 * 大模型id
	 */
	@Column("model_id")
	@ApiModelProperty(name = "modelId", value = "大模型id", dataType = "String")
	private String modelId;

	/**
	 * 大模型名称
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "modelName", value = "大模型名称", dataType = "String")
	private String modelName;

	/**
	 * 开启多轮对话[是/否]
	 */
	@Column("multi_dialogue_flag")
	@ApiModelProperty(name = "multiDialogueFlag", value = "开启多轮对话[是/否]", dataType = "String")
	private String multiDialogueFlag;

	/**
	 * 携带上下文对话轮数
	 */
	@Column("multi_dialogue_num")
	@ApiModelProperty(name = "multiDialogueNum", value = "携带上下文对话轮数", dataType = "Integer")
	private Integer multiDialogueNum;

	/**
	 * 开启图像识别[是/否]
	 */
	@Column("ocr_flag")
	@ApiModelProperty(name = "ocrFlag", value = "开启图像识别[是/否]", dataType = "String")
	private String ocrFlag;

	/**
	 * 有效期结束
	 */
	@Column("period_end")
	@ApiModelProperty(name = "periodEnd", value = "有效期结束", dataType = "String")
	private String periodEnd;

	/**
	 * 有效期开始时间
	 */
	@Column("period_start")
	@ApiModelProperty(name = "periodStart", value = "有效期开始时间", dataType = "String")
	private String periodStart;

	/**
	 * 状态：状态：1-公开发布；2-私有发布，3-未发布，4-暂存，5-停用
	 */
	@Column("publish_status")
	@ApiModelProperty(name = "publishStatus", value = "状态：状态：1-公开发布；2-私有发布，3-未发布，4-暂存，5-停用", dataType = "String")
	private String publishStatus;

	/**
	 * 推荐问题策略
	 */
	@Column("recommendation")
	@ApiModelProperty(name = "recommendation", value = "推荐问题策略", dataType = "String")
	private String recommendation;


	/**
	 * 推荐问题数量
	 */
	@Column("recommendation_num")
	@ApiModelProperty(name = "recommendationNum", value = "推荐问题数量", dataType = "Integer")
	private Integer recommendationNum;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 问题改写[是/否]
	 */
	@Column("rewriting_flag")
	@ApiModelProperty(name = "rewritingFlag", value = "问题改写[是/否]", dataType = "String")
	private String rewritingFlag;


	/**
	 * 问题意图推荐[是/否]
	 */
	@Column("recommended_flag")
	@ApiModelProperty(name = "recommendedFlag", value = "问题意图推荐[是/否]", dataType = "String")
	private String recommendedFlag;

	/**
	 * 问题意图推荐-推荐问题选择话术
	 */
	@Column("recommended_talk")
	@ApiModelProperty(name = "recommendedTalk", value = "问题意图推荐-推荐问题选择话术", dataType = "String")
	private String recommendedTalk;

	/**
	 * 机器人图标
	 */
	@Column("robot_icon")
	@ApiModelProperty(name = "robotIcon", value = "机器人图标", dataType = "String")
	private String robotIcon;

	/**
	 * system prompt设置
	 */
	@Column("system_prompt")
	@ApiModelProperty(name = "systemPrompt", value = "system prompt设置", dataType = "String")
	private String systemPrompt;

	/**
	 * 学习成长中话术
	 */
	@Column("tail_talk")
	@ApiModelProperty(name = "tailTalk", value = "学习成长中话术", dataType = "String")
	private String tailTalk;

	/**
	 * 语音播报id
	 */
	@Column("tts_id")
	@ApiModelProperty(name = "ttsId", value = "语音播报id", dataType = "String")
	private String ttsId;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 更新人，OnFieldFill注解可以自动填充，fill为触发机制，mdcKey为MDC中key，提取其中的值
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	/**
	 * 用户图标
	 */
	@Column("user_icon")
	@ApiModelProperty(name = "userIcon", value = "用户图标", dataType = "String")
	private String userIcon;

	/**
	 * 开启视频识别[是/否]
	 */
	@Column("video_flag")
	@ApiModelProperty(name = "videoFlag", value = "开启视频识别[是/否]", dataType = "String")
	private String videoFlag;

	/**
	 * 开启虚拟人[是/否]
	 */
	@Column("virtual_human_flag")
	@ApiModelProperty(name = "virtualHumanFlag", value = "开启虚拟人[是/否]", dataType = "String")
	private String virtualHumanFlag;

	/**
	 * 虚拟人id
	 */
	@Column("virtual_human_id")
	@ApiModelProperty(name = "virtualHumanId", value = "虚拟人id", dataType = "String")
	private String virtualHumanId;

	/**
	 * 开启语音对话[是/否]
	 */
	@Column("voice_dialogue_flag")
	@ApiModelProperty(name = "voiceDialogueFlag", value = "开启语音对话[是/否]", dataType = "String")
	private String voiceDialogueFlag;
	/**
	 * ai对话问题开关[是/否]
	 */
	@Column("ai_question_flag")
	@ApiModelProperty(name = "aiQuestionFlag", value = "开启语音对话[是/否]", dataType = "String")
	private String aiQuestionFlag;
	/**
	 * api接口地址
	 */
	@Column("api")
	@ApiModelProperty(name = "api", value = "api接口地址", dataType = "String")
	private String api;

	/**
	 * api接口地址
	 */
	@Column("button_style")
	@ApiModelProperty(name = "buttonStyle", value = "按钮样式", dataType = "String")
	private String buttonStyle;

	/**
	 * api接口地址
	 */
	@Column("button_text")
	@ApiModelProperty(name = "defaultButtonText", value = "按钮样式", dataType = "String")
	private String buttonText;

	/**
	 * 问答形式检索标题分数阈值
	 */
	@Column("qa_title_score")
	@ApiModelProperty(name = "qaTitleScore", value = "问答形式检索标题分数阈值", dataType = "Float")
	private Float qaTitleScore;

	/**
	 * 问答形式检索正文分数阈值
	 */
	@Column("qa_content_score")
	@ApiModelProperty(name = "qaContentScore", value = "问答形式检索正文分数阈值", dataType = "Float")
	private Float qaContentScore;

	/**
	 * 问答形式重排标题分数阈值
	 */
	@Column("qa_range_title_score")
	@ApiModelProperty(name = "qaRangeTitleScore", value = "问答形式重排标题分数阈值", dataType = "Float")
	private Float qaRangeTitleScore;

	/**
	 * 问答形式重排正文（回答）分数阈值
	 */
	@Column("qa_range_content_score")
	@ApiModelProperty(name = "qaRangeContentScore", value = "问答形式重排正文（回答）分数阈值", dataType = "Float")
	private Float qaRangeContentScore;

	/**
	 * 检索内容分数阈值
	 */
	@Column("content_score")
	@ApiModelProperty(name = "contentScore", value = "检索内容分数阈值", dataType = "Float")
	private Float contentScore;

	/**
	 * 检索内容分数阈值
	 */
	@Column("range_content_score")
	@ApiModelProperty(name = "rangeContentScore", value = "检索内容分数阈值", dataType = "Float")
	private Float rangeContentScore;

	/**
	 * 引用知识库文段数量(重排后的数量)
	 */
	@Column("filter_num")
	@ApiModelProperty(name = "filterNum", value = "引用知识库文段数量(重排后的数量)", dataType = "Integer")
	private Integer filterNum;

	/**
	 * 知识库文段预备数量(重排前的数量)-雅意重排
	 */
	@Column("prepare_num")
	@ApiModelProperty(name = "prepareNum", value = "知识库文段预备数量(重排前的数量)-雅意重排", dataType = "Integer")
	private Integer prepareNum;

	/**
	 * 回答超时，单位秒
	 */
	@Column("answer_timeout")
	@ApiModelProperty(name = "answerTimeout", value = "回答超时，单位秒", dataType = "Integer")
	private Integer answerTimeout;

	/**
	 * prompt模板
	 */
	@Column("prompt_template")
	@ApiModelProperty(name = "promptTemplate", value = "prompt模板", dataType = "String")
	private String promptTemplate;

	/**
	 * 无法回答的关键词
	 */
	@Column("not_answer")
	@ApiModelProperty(name = "notAnswer", value = "无法回答的关键词", dataType = "String")
	private String notAnswer;

	/**
	 * 免责声明
	 */
	@Column("disclaimer")
	@ApiModelProperty(name = "disclaimer", value = "免责声明", dataType = "String")
	private String disclaimer;

	/**
	 * 聊天模板id
	 */
	@Column("template_id")
	@ApiModelProperty(name = "templateId", value = "聊天模板id", dataType = "String")
	private String templateId;

	/**
	 * 移动端聊天模板id
	 */
	@Column("mobile_template_id")
	@ApiModelProperty(name = "mobileTemplateId", value = "移动端聊天模板id", dataType = "String")
	private String mobileTemplateId;

	/**
	 * 应用身份设定
	 */
	@Column("identity_icon")
	@ApiModelProperty(name = "identityIcon", value = "应用身份设定", dataType = "String")
	private String identityIcon;

	/**
	 * 租户id
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;

	/**
	 * 搜索平台：toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)
	 */
	@Column(value = "network_channel")
	@ApiModelProperty(name = "networkChannel", value = "搜索平台：toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)", dataType = "String")
	private String networkChannel;

	/**
	 * 是否需要润色，默认否
	 */
	@Column(value = "polish_flag")
	@ApiModelProperty(name = "polishFlag", value = "是否需要润色", dataType = "String")
	private String polishFlag;

	/**
	 * 润色的prompt
	 */
	@Column(value = "polish_prompt")
	@ApiModelProperty(name = "polishPrompt", value = "润色的prompt", dataType = "String")
	private String polishPrompt;

	/**
	 * 顺序执行步骤
	 */
	@Column(value = "process_step")
	@ApiModelProperty(name = "processStep", value = "顺序执行步骤", dataType = "String")
	private String processStep;

	/**
	 * 网页页签标题
	 */
	@Column(value = "web_title")
	@ApiModelProperty(name = "webTitle", value = "网页页签标题", dataType = "String")
	private String webTitle;

	/**
	 * 讨论话题的prompt
	 */
	@Column(value = "subject_prompt")
	@ApiModelProperty(name = "subjectPrompt", value = "讨论话题的prompt", dataType = "String")
	private String subjectPrompt;

	/**
	 * 是否禁用IP,是/否
	 */
	@Column(value = "ip_flag")
	@ApiModelProperty(name = "ipFlag", value = "是否禁用IP,是/否", dataType = "String")
	private String ipFlag;

	/**
	 * 文档链接类型[1-原始文件链接，2-网页链接]
	 */
	@Column(value = "doc_link_type")
	@ApiModelProperty(name = "docLinkType", value = "文档链接类型[1-原始文件链接，2-网页链接]", dataType = "String")
	private String docLinkType;

	/**
	 * 是否预览溯源文件 是/否
	 */
	@Column(value = "preview_doc")
	@ApiModelProperty(name = "previewDoc", value = "是否预览溯源文件", dataType = "String")
	private String previewDoc;

	/**
	 * 前台是否展示推荐问题开关[1-开，0-关]
	 */
	@Column(value = "recommend_questions_show_flag")
	@ApiModelProperty(name = "recommendQuestionsShowFlag", value = "前台是否展示推荐问题开关[1-开，0-关]", dataType = "String")
	private String recommendQuestionsShowFlag;

	/**
	 * 前台是否展示依据来源开关[1-开，0-关]
	 */
	@Column(value = "source_show_flag")
	@ApiModelProperty(name = "sourceShowFlag", value = "前台是否展示依据来源开关[1-开，0-关]", dataType = "String")
	private String sourceShowFlag;

	/**
	 * 前台是否展示相关事项开关[1-开，0-关]
	 */
	@Column(value = "related_matters_show_flag")
	@ApiModelProperty(name = "relatedMattersShowFlag", value = "前台是否展示相关事项开关[1-开，0-关]", dataType = "String")
	private String relatedMattersShowFlag;

	/**
	 * 语音识别id
	 */
	@Column(value = "stt_id")
	@ApiModelProperty(name = "sttId", value = "语音识别id", dataType = "String")
	private String sttId;

	/**
	 * H5聊天认证渠道
	 */
	@Column(value = "client_auth_channel")
	@ApiModelProperty(name = "clientAuthChannel", value = "H5聊天认证渠道", dataType = "String")
	private String clientAuthChannel;

	/**
	 * PC聊天认证渠道
	 */
	@Column(value = "pc_auth_channel")
	@ApiModelProperty(name = "pcAuthChannel", value = "PC聊天认证渠道", dataType = "String")
	private String pcAuthChannel;

	/**
	 * 是否需要转人工，是/否
	 */
	@Column(value = "to_human_flag")
	@ApiModelProperty(name = "toHumanFlag", value = "是否需要转人工，是/否", dataType = "String")
	private String toHumanFlag;

	/**
	 * 输入框提示语
	 */
	@Column(value = "input_placeholder")
	@ApiModelProperty(name = "toHumanFlag", value = "输入框提示语", dataType = "String")
	private String inputPlaceholder;

	/**
	 * 开场白
	 */
	@Column(value = "prologue")
	@ApiModelProperty(name = "prologue", value = "开场白", dataType = "String")
	private String prologue;

	/**
	 * 背景图url地址
	 */
	@Column(value = "background_image_url")
	@ApiModelProperty(name = "backgroundImageUrl", value = "背景图url地址", dataType = "String")
	private String backgroundImageUrl;

	/**
	 * 助手形象图片地址
	 */
	@Column(value = "facade_image_url")
	@ApiModelProperty(name = "facadeImageUrl", value = "助手形象图片地址", dataType = "String")
	private String facadeImageUrl;

	/**
	 * 应用类型
	 */
	@Column(value = "type")
	@ApiModelProperty(name = "type", value = "应用类型，null或空字符串-问答助手； search-智能搜索； workflow-工作流", dataType = "String")
	private String type;

	@Column(value = "business_scenario_enable")
	@ApiModelProperty(name = "businessScenarioEnable", value = "预设业务场景是否开启[是/否]", dataType = "String")
	private String businessScenarioEnable;

	@Column(value = "business_scenario_script")
	@ApiModelProperty(name = "businessScenarioScript", value = "预设业务场景话术 例如：您是否想要办理以下业务", dataType = "String")
	private String businessScenarioScript;

	@Column(value = "back_old_version_link")
	@ApiModelProperty(name = "backOldVersionLink", value = "返回旧版的网页链接", dataType = "String")
	private String backOldVersionLink;

	@Column(value = "about_website")
	@ApiModelProperty(name = "aboutWebsite", value = "关于网站，html富文本", dataType = "String")
	private String aboutWebsite;

	@Column(value = "menu_service_flag")
	@ApiModelProperty(name = "menuServiceFlag", value = "是否打开便民服务，是/否", dataType = "String")
	private String menuServiceFlag;

	@Column(value = "feedback_flag")
	@ApiModelProperty(name = "feedbackFlag", value = "是否反馈，是/否", dataType = "String")
	private String feedbackFlag;

	@Column(value = "policy_list_flag")
	@ApiModelProperty(name = "policyListFlag", value = "是否打开政策列表，是/否", dataType = "String")
	private String policyListFlag;

	@Column(value = "history_flag")
	@ApiModelProperty(name = "historyFlag", value = "是否打开历史对话，是/否", dataType = "String")
	private String historyFlag;

	@Column(value = "video_resolve_flag")
	@ApiModelProperty(name = "videoResolveFlag", value = "是否打开视频解析按钮，是/否", dataType = "String")
	private String videoResolveFlag;


	@Column(value = "attribution_logo")
	@ApiModelProperty(name = "attributionLogo", value = "归属logo（例如：国徽图片）", dataType = "String")
	private String attributionLogo;

	@Column(value = "virtual_human_logo")
	@ApiModelProperty(name = "virtualHumanLogo", value = "虚拟人logo", dataType = "String")
	private String virtualHumanLogo;

	/**
	 * 语速
	 */
	@Column("voice_speed")
	@ApiModelProperty(name = "voiceSpeed", value = "语速", dataType = "String")
	private BigDecimal voiceSpeed;

	/**
	 * 语调
	 */
	@Column("pitch")
	@ApiModelProperty(name = "pitch", value = "语调", dataType = "String")
	private BigDecimal pitch;

	@Column(ignore = true)
	private LlmInfo llmInfo;

	/**
	 * 知识库列表
	 */
	@Column(ignore = true)
	private List<ApplicationKnowledge> knowledgeIds;

	/**
	 * 知识库id列表
	 */
	@Column(ignore = true)
	private List<String> knowledgeIdList;

	/**
	 * 工作流id列表
	 */
	@Column(ignore = true)
	private List<ApplicationKnowledge> workflowIds;

	/**
	 * 预设问题列表
	 */
	@Column(ignore = true)
	private List<String> presetQuestionList;

	/**
	 * 应用快捷指令列表
	 */
	@Column(ignore = true)
	private List<ApplicationQuickCommand> applicationQuickCommandList;

	/**
	 * 聊天模板路由
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "templateRoute", value = "聊天模板路由", dataType = "String")
	private String templateRoute;

	/**
	 * 移动端聊天模板路由
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "mobileTemplateRoute", value = "移动端聊天模板路由", dataType = "String")
	private String mobileTemplateRoute;

	/**
	 * H5聊天认证渠道编码
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "clientAuthChannelCode", value = "H5聊天认证渠道编码", dataType = "String")
	private String clientAuthChannelCode;

	/**
	 * PC聊天认证渠道编码
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "pcAuthChannelCode", value = "PC聊天认证渠道编码", dataType = "String")
	private String pcAuthChannelCode;

	@Column(ignore = true)
	@ApiModelProperty("文档个数")
	private Long fileCount;

	/**
	 * 图片个数
	 */
	@Column(ignore = true)
	@ApiModelProperty("图片个数")
	private Long imageCount;

	/**
	 * 音频个数
	 */
	@Column(ignore = true)
	@ApiModelProperty("音频个数")
	private Long audioCount;

	/**
	 * 视频个数
	 */
	@Column(ignore = true)
	@ApiModelProperty("视频个数")
	private Long videoCount;

	/**
	 * 链接个数（知识库url个数）
	 */
	@Column(ignore = true)
	@ApiModelProperty("链接个数")
	private Long urlCount;

	/**
	 * 是否来自管理端(借用给溯源功能传参),1-是
	 */
	@Column(ignore = true)
	@ApiModelProperty("是否来自管理端")
	private String fromManage;

	/**
	 * 类型归属
	 */
	@Column(ignore = true)
	@ApiModelProperty("类型归属")
	private String ownerType;

	/**
	 * 流式语音开关 是 | 否
	 */
	@Column(value = "stream_voice")
	@ApiModelProperty(name = "streamVoice", value = "流式语音开关 是 | 否", dataType = "String")
	private String streamVoice;


	/**
	 * 是否有“复制”按钮权限 0-是 1-否
	 */
	@Column(ignore = true)
	private Integer copyPermission;


	/**
	 * 是否联网兜底[是/否]
	 */
	@Column("final_network_flag")
	@ApiModelProperty(name = "finalNetworkFlag", value = "是否联网兜底[是/否]", dataType = "String")
	private String finalNetworkFlag;

	/**
	 * 是否发布应用商店 0-未发布（默认） 1-已发布 2-待审核
	 */
	@Column("publish_app_store")
	@ApiModelProperty(name = "publishAppStore", value = "0-未发布（默认） 1-已发布 2-待审核", dataType = "Integer")
	private Integer publishAppStore;

	@Column("publish_time")
	@ApiModelProperty(name = "publishTime", value = "发布时间", dataType = "Date")
	private Date publishTime;

	@Column("publish_type")
	@ApiModelProperty(name = "publishType", value = "发布分类（聊天助手、AI翻译、AI搜索、文案写作、行业报告、图片创作、学习助手、合规审查）", dataType = "String")
	private String publishType;

	@Column("publish_desc")
	@ApiModelProperty(name = "publishDesc", value = "发布说明", dataType = "String")
	private String publishDesc;

	@Column("client_auth_channel_appId")
	@ApiModelProperty(name = "clientAuthChannelAppId", value = "微信公众号认证绑定的appid", dataType = "String")
	private String clientAuthChannelAppId;

	@Column("private_switch")
	@ApiModelProperty(name = "privateSwitch", value = "私有发布开关 0-开启 1-关闭", dataType = "Integer")
	private Integer privateSwitch;
	@Column("public_switch")
	@ApiModelProperty(name = "publicSwitch", value = "公有发布开关 0-开启 1-关闭", dataType = "Integer")
	private Integer publicSwitch;
	@Column("implant_switch")
	@ApiModelProperty(name = "implantSwitch", value = "嵌入模式开关 是-开启 否-关闭", dataType = "String")
	private String implantSwitch;
	@Column("privacy_policy")
	@ApiModelProperty(name = "privacyPolicy", value = "隐私政策", dataType = "String")
	private String privacyPolicy;

	@Column("url")
	@ApiModelProperty(name = "url", value = "跳转主网站链接", dataType = "String")
	private String url;

	@Column("picture")
	@ApiModelProperty(name = "picture", value = "跳转主网站图片", dataType = "String")
	private String picture;

	@Column(value = "voice_broadcast_config", typeHandler = Fastjson2TypeHandler.class)
	@ApiModelProperty(name = "voiceBroadcastConfig", value = "语音播报配置", dataType = "String")
	private String voiceBroadcastConfig;

	@Column("rearrange_model")
	@ApiModelProperty(name = "rearrangeModel", value = "重排模型： yayi-雅意重排  volcengine-火山引擎重排", dataType = "String")
	private String rearrangeModel;

	/**
	 * 知识库文段预备数量(重排前的数量)-火山引擎重排
	 */
	@Column("volcengine_prepare_num")
	@ApiModelProperty(name = "volcenginePrepareNum", value = "知识库文段预备数量(重排前的数量)-火山引擎重排", dataType = "Integer")
	private Integer volcenginePrepareNum;

	/**
	 * 点击发布或者更新按钮的标志 用于记录发布日志的记录  只有在真正发布的按钮传true
	 **/
	@Column(ignore = true)
	private Boolean clickPublish = false;

	/**
	 * * 问答输入框内容长度限制标识（是-有限制（即保持已有功能） 否-无限制）
	 */
	@Column(ignore = true)
	private String contentLengthLimit;

	/**
	 * 创建者真实姓名
	 */
	@Column(ignore = true)
	private String accountName;

	/**
	 * 收藏标识 0：未收藏，1：已收藏
	 */
	@Column(ignore = true)
	private Integer favoriteFlag;

	/**
	 * 应用关联的敏感词库列表
	 */
	@Column(ignore = true)
	private List<Long> interceptWordHouses;

	/**
	 * 应用关联的mcp服务id列表
	 */
	@Column(ignore = true)
	private List<String> mcpServerIds;

	/**
	 * 联想问题策略
	 */
	@Column("association")
	@ApiModelProperty(name = "association", value = "联想问题策略", dataType = "String")
	private String association;


	/**
	 * 联想问题数量
	 */
	@Column("association_num")
	@ApiModelProperty(name = "associationNum", value = "联想问题数量", dataType = "Integer")
	private Integer associationNum;

	/**
	 * 前台是否展示联想问题开关[1-开，0-关]
	 */
	@Column("association_questions_show_flag")
	@ApiModelProperty(name = "associationQuestionsShowFlag", value = "前台是否展示联想问题开关[1-开，0-关]", dataType = "String")
	private String associationQuestionsShowFlag;

	/**
	 * 模糊引导自定义配置想开关
	 */
	@Column("vague_flag")
	private String vagueFlag;


	/**
	 * 模糊引导配置提示词
	 */
	@Column("vague_config")
	private String vagueConfig;

	/**
	 * 溯源是否需要索引
	 */
	@Column(ignore = true)
	private String refIndexFlag;

	/**
	 * 讨论话题使用的大模型名称
	 */
	@Column(ignore = true)
	private String subjectLlmName;

	/**
	 * 大模型信息
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "modelInfo", value = "大模型信息", dataType = "String")
	private LlmInfo modelInfo;

	@Column(ignore = true)
	@ApiModelProperty(name = "maxToken", value = "最大输出token数", dataType = "String")
	private Integer maxToken;

	@Column(ignore = true)
	@ApiModelProperty(name = "granted", value = "是否被授权应用", dataType = "Boolean")
	private Boolean granted = false;


	// INSERT INTO smart_customer_agent.application_info (id, application_id, application_name, logo, introduce, greeting, robot_icon, failure_talk, virtual_human_flag, virtual_human_id, voice_dialogue_flag, tts_id, multi_dialogue_flag, multi_dialogue_num, model_id, ocr_flag, video_flag, rewriting_flag, knowledge_flag, system_prompt, tail_talk, user_icon, recommendation, link, publish_status, content_score, range_content_score, qa_title_score, qa_range_title_score, qa_content_score, qa_range_content_score, filter_num, prepare_num, period_start, period_end, api_key, api_secret, delete_flag, remark, api, button_style, button_text, model_answer_flag, create_time, update_time, create_user, update_user, answer_timeout, prompt_template) VALUES (23, '00759c5a4ae74622971ee51142287e2d', '中关村智能问答2', 'http://localhost:8686/wos/file/download?fileKey=robot.png', '应用介绍', '您好，我是中关村智能问答助手，现在让我们开始交流吧！您可以在下方的输入框中输入您的问题，如果还没想好，您可以试着问我：', 'http://localhost:8686/wos/file/download?fileKey=robot.png', '抱歉，我还在成长中，具体信息可进一步通过“中关村委员会”<https://kw.beijing.gov.cn/>查询或咨询', '否', '', '否', '', '是', 3, 'kimi', '否', '否', '否', '是', '你是一个中关村开发提供的网站GPT智能客服助手，为市民提供贴心的服务，提供政策解读、办事指南等服务功能，不能和市民闲聊。', '<br> <hr style=\\"border: 1px solid #2692cf59;\\">  我还在成长中，具体信息可进一步通过“中关村委员会”  <https://kw.beijing.gov.cn/>查询或咨询。', 'http://localhost:8686/wos/file/download?fileKey=robot.png', '匹配问题', null, '4', 1.7600, 0.7230, 1.9100, 0.9100, 1.8800, 0.8800, 3, 60, '2024-06-10 15:35:04', null, 'fe5673e65354cdead44a2042cd4197217c97cf70', 'bc1af7b81cfacb7a0ed66c43a1536ded', 0, '备注', null, '<a href=\\"{}\\" target=\\"_blank\\" style=\\"color: #ffffff; text-decoration: none;background-color: #409EFF;border-radius: 5px;padding: 0 4px;\\">{}</a> ', '详情请见', '否', '2024-06-10 15:35:04', '2024-06-14 11:22:09', '1', '1', 180, '
	//#背景环境 #
	// 今天是{date}，为了提高政务服务质量，当市民咨询政务或者生活上相关政务事项时，你的做法是：根据市民的输入的问题从知识库找到相关有效信息，然后根据这些有效信息进行总结归纳，最终回答市民的问题。
	//
	//#############
	// 知识库：
	//{content}
	//
	//#############
	// 输入问题 ：
	//{question}
	//
	//#知识库范围#
	//「」标签中的内容是知识库内容
	//
	//#问题范围#
	//『』标签中的内容是问题内容
	//
	//# 任务目标 #
	// 请阅读并理解<<<知识库>>>中的每段知识库内容，请按照以下步骤进行推理，然后根据<<<知识库>>>的相关信息对<<<输入问题>>>进行作答：
	// 1.逐个阅读每一段「」标签中的知识库内容，并提取有效信息。
	// 2.请分析给出的『』标签中的问题内容，提取问题的真实意图。
	// 3.根据输入问题的真实意图，从已提取到的有效信息中推理、总结出最终答案。
	//
	//#护栏#
	// 如果<<<知识库>>>没有足够的信息来回答问题，不要编造信息，请回答：{"引用知识库":没有相关知识库，"答案":"我不知道"，"问题":输入的问题}
	//
	//#范围#
	// 只允许你回答有关<<<知识库>>>中的内容，不得回答任何敏感信息相关的问题
	//
	//# 回答风格 #
	// 遵循政府政务客服人员的态度和风格
	//# 回答语气 #
	// 正式的
	//# 受众群体 #
	// 政府政务客服的咨询群众通常是关心自身权益和办理事项的市民，定制你的答案，以针对这些群众想要了解事项的准确信息
	//# 回答输出格式 #
	// 用json格式输出，{\\引用知识库\\:知识库编号或则没有相关知识库，\\答案\\:最终答案，\\问题\\:输入的问题}
	// 输出示例：{\\问题\\:\\你是谁？\\,\\答案\\:\\我是中关村政府开发提供的网站GPT智能客服助手，为市民提供贴心的服务，提供政策解读、办事指南等服务\\,\\引用知识库\\:\\知识库1，知识库2\\}');

	public String getSystemPromptResult() {
		if (StringUtils.isBlank(systemPrompt)) {
			return StringConstant.BLANK;
		}
		// 获取当前星期几，比如“星期六”
		String dayOfWeek = com.wenge.model.utils.DateUtil.getCurrentWeekCn();
		String time = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy年MM月dd日 HH:mm:ss");
		return systemPrompt.replace("{date}", time + "," + dayOfWeek);
	}

    @Override
    public ApplicationInfo clone() {
        try {
            ApplicationInfo clone = (ApplicationInfo) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}