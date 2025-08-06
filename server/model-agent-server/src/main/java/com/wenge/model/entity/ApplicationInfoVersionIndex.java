package com.wenge.model.entity;
import lombok.*;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: 应用信息版本控制表
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2025-04-01 10:02:50
 */
@Data
@Builder
@IndexName
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationInfoVersionIndex implements Serializable {
	private static final long serialVersionUID = 8901334677509447839L;

	@IndexId(type = IdType.UUID)
	private String id;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String versionId;
	/**
	 * 应用apikey
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String apiKey;

	/**
	 * 应用密钥
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String apiSecret;
	/**
	 * 应用id，有业务作用
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String applicationInfoId;
	/**
	 * 应用版本号
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String appVersionNumber;
	/**
	 * 应用版本号说明
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String versionRemark;
	/**
	 * 应用回退说明
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String backVersionRemark;
	/**
	 * 操作类型
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String makeType;
	/**
	 * 关联知识库关联关系备份///知识库列表
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingKnowledgeIdListJson;//knowledgeIdList
	/**
	 * 关联知识库关联关系备份//知识库id列表
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingKnowledgeIdsJson;//knowledgeIds
	/**
	 * 工作流关联关系备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingWorkflowIdsJson;//workflowIds
	/**
	 * 关联预设问题关联关系备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingPresetQuestionJson;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingApplicationQuickCommandJson;
	/**
	 * 关联敏敢词关联关系备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingInterceptWordhoseJson;
	/**
	 * 关联个性化关联关系备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String bindingConfigrationJson;
	/**
	 * 节点信息备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String nodesJson;
	/**
	 * 节点关系备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String nodeRelJson;
	/**
	 * 映射组件信息备份
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String componentJson;
	/**
	 * 应用id，有业务作用
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String applicationId;

	/**
	 * 应用名称
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String applicationName;

	/**
	 * 应用编码
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String applicationCode;

	/**
	 * 网页图标icon
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String webIcon;

	/**
	 * 创建时间
	 */
	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private String createTime;

	/**
	 * 创建人
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String createUser;

	/**
	 * 是否删除[1-删除,0-未删除]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String deleteFlag;

	/**
	 * 无法回答话术
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String failureTalk;

	/**
	 * 欢迎语
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String greeting;


	/**
	 * 应用介绍
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String introduce;

	/**
	 * 知识库回答[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String knowledgeFlag;

	/**
	 * 安全敏感词拦截开关[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String sensitiveFlag;

	/**
	 * 讨论话题开关[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String subjectFlag;

	/**
	 * 模型回答[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String modelAnswerFlag;

	/**
	 * 是否联网检索[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String networkFlag;
	/**
	 * 是否联网搜索[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String networkWebSearchingFlag;

	/**
	 *  是否模型兜底  需要在编辑的时候更新字段processStep中的模型发散（最后）
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String modelFallbackFlag;

	/**
	 * 是否开启问数检索[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String wenshuFlag;


	/**
	 * 发布链接
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String clientLink;

	/**
	 * logo地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String logo;

	/**
	 * 大模型id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String modelId;

	/**
	 * 大模型名称
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String modelName;

	/**
	 * 开启多轮对话[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String multiDialogueFlag;

	/**
	 * 携带上下文对话轮数
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer multiDialogueNum;

	/**
	 * 开启图像识别[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String ocrFlag;

	/**
	 * 有效期结束
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String periodEnd;

	/**
	 * 有效期开始时间
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String periodStart;

	/**
	 * 状态：状态：1-公开发布；2-私有发布，3-未发布，4-暂存，5-停用
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String publishStatus;

	/**
	 * 推荐问题策略
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String recommendation;



	/**
	 * 推荐问题数量
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer recommendationNum;

	/**
	 * 联想问题策略
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String association;


	/**
	 * 联想问题数量
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer associationNum;

	/**
	 * 备注
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String remark;

	/**
	 * 问题改写[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String rewritingFlag;


	/**
	 * 问题意图推荐[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String recommendedFlag;

	/**
	 * 问题意图推荐-推荐问题选择话术
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String recommendedTalk;

	/**
	 * 机器人图标
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String robotIcon;

	/**
	 * system prompt设置
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String systemPrompt;

	/**
	 * 学习成长中话术
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String tailTalk;

	/**
	 * 语音id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String ttsId;

	/**
	 * 更新时间
	 */
	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private String updateTime;

	/**
	 * 更新人，OnFieldFill注解可以自动填充，fill为触发机制，mdcKey为MDC中key，提取其中的值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String updateUser;

	/**
	 * 用户图标
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String userIcon;

	/**
	 * 开启视频识别[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String videoFlag;
	/**
	 * 是否打开视频解析按钮，是/否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String videoResolveFlag;
	/**
	 * 开启虚拟人[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String virtualHumanFlag;

	/**
	 * 虚拟人id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String virtualHumanId;

	/**
	 * 开启语音对话[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String voiceDialogueFlag;
	/**
	 * ai对话问题开关[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String aiQuestionFlag;
	/**
	 * api接口地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String api;

	/**
	 * api接口地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String buttonStyle;

	/**
	 * api接口地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String buttonText;

	/**
	 * 问答形式检索标题分数阈值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Float qaTitleScore;

	/**
	 * 问答形式检索正文分数阈值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Float qaContentScore;

	/**
	 * 问答形式重排标题分数阈值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Float qaRangeTitleScore;

	/**
	 * 问答形式重排正文（回答）分数阈值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Float qaRangeContentScore;

	/**
	 * 检索内容分数阈值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Float contentScore;

	/**
	 * 检索内容分数阈值
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Float rangeContentScore;

	/**
	 * 引用知识库文段数量(重排后的数量)
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer filterNum;

	/**
	 * 知识库文段预备数量(重排前的数量)
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer prepareNum;

	/**
	 * 回答超时，单位秒
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer answerTimeout;

	/**
	 * prompt模板
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String promptTemplate;

	/**
	 * 无法回答的关键词
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String notAnswer;

	/**
	 * 免责声明
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String disclaimer;

	/**
	 * 聊天模板id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String templateId;

	/**
	 * 移动端聊天模板id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String mobileTemplateId;

	/**
	 * 应用身份设定
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String identityIcon;

	/**
	 * 租户id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String tenantId;

	/**
	 * 搜索平台：toutiao、baidu、bing、toutiaoImage(图片只采列表第一页)
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String networkChannel;

	/**
	 * 是否需要润色，默认否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String polishFlag;

	/**
	 * 润色的prompt
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String polishPrompt;

	/**
	 * 顺序执行步骤
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String processStep;

	/**
	 * 网页页签标题
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String webTitle;

	/**
	 * 讨论话题的prompt
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String subjectPrompt;

	/**
	 * 是否禁用IP,是/否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String ipFlag;

	/**
	 * 文档链接类型[1-原始文件链接，2-网页链接]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String docLinkType;

	/**
	 * 是否预览溯源文件 是/否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String previewDoc;

	/**
	 * 前台是否展示推荐问题开关[1-开，0-关]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String recommendQuestionsShowFlag;

	/**
	 * 前台是否展示依据来源开关[1-开，0-关]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String sourceShowFlag;

	/**
	 * 前台是否展示相关事项开关[1-开，0-关]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String relatedMattersShowFlag;

	/**
	 * 语音识别id
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String sttId;

	/**
	 * H5聊天认证渠道
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String clientAuthChannel;

	/**
	 * PC聊天认证渠道
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String pcAuthChannel;

	/**
	 * 是否需要转人工，是/否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String toHumanFlag;

	/**
	 * 输入框提示语
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String inputPlaceholder;

	/**
	 * 开场白
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String prologue;

	/**
	 * 背景图url地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String backgroundImageUrl;

	/**
	 * 助手形象图片地址
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String facadeImageUrl;

	/**
	 * 应用类型
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String type;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String businessScenarioEnable;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String businessScenarioScript;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String backOldVersionLink;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String aboutWebsite;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String menuServiceFlag;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String feedbackFlag;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String policyListFlag;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String historyFlag;


	@IndexField(fieldType = FieldType.KEYWORD)
	private String attributionLogo;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String virtualHumanLogo;

	@IndexField(fieldType = FieldType.KEYWORD)
	private LlmInfo llmInfo;

	/**
	 * 知识库列表
	 */
	//@IndexField(fieldType = FieldType.KEYWORD)
	//private List<ApplicationKnowledge> knowledgeIds;

	/**
	 * 知识库id列表
	 */
	//@IndexField(fieldType = FieldType.KEYWORD)
	//private List<String> knowledgeIdList;

	/**
	 * 工作流id列表
	 */
	//@IndexField(fieldType = FieldType.KEYWORD)
	//private List<ApplicationKnowledge> workflowIds;

	/**
	 * 预设问题列表
	 */
	//@IndexField(fieldType = FieldType.KEYWORD)
	//private List<String> presetQuestionList;

	/**
	 * 聊天模板路由
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String templateRoute;

	/**
	 * 移动端聊天模板路由
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String mobileTemplateRoute;

	/**
	 * H5聊天认证渠道编码
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String clientAuthChannelCode;

	/**
	 * PC聊天认证渠道编码
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String pcAuthChannelCode;

	@IndexField(fieldType = FieldType.KEYWORD)
	private Long fileCount;

	/**
	 * 图片个数
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Long imageCount;

	/**
	 * 音频个数
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Long audioCount;

	/**
	 * 视频个数
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Long videoCount;

	/**
	 * 链接个数（知识库url个数）
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Long urlCount;

	/**
	 * 是否来自管理端(借用给溯源功能传参),1-是
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String fromManage;

	/**
	 * 流式语音开关 是 | 否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String streamVoice;

	/**
	 * 是否有“复制”按钮权限 0-是 1-否
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer copyPermission;


	/**
	 * 是否联网兜底[是/否]
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String finalNetworkFlag;

	/**
	 * 是否发布应用商店 0-未发布（默认） 1-已发布
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer publishAppStore;

	@IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private Date publishTime;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String publishType;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String publishDesc;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String clientAuthChannelAppId;

	@IndexField(fieldType = FieldType.KEYWORD)
	private Integer privateSwitch;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String publicSwitch;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String implantSwitch;
	@IndexField(fieldType = FieldType.KEYWORD)
	private String privacyPolicy;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String url;

	@IndexField(fieldType = FieldType.KEYWORD)
	private String picture;

	/**
	 * 点击发布或者更新按钮的标志 用于记录发布日志的记录  只有在真正发布的按钮传true
	 **/
	@IndexField(fieldType = FieldType.KEYWORD)
	private Boolean clickPublish = false;

	/**
	 * 	 * 问答输入框内容长度限制标识（是-有限制（即保持已有功能） 否-无限制）
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String contentLengthLimit;

	/**
	 * 创建者真实姓名
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String accountName;

	/**
	 * 应用关联的敏感词库列表
	 */
	//@IndexField(fieldType = FieldType.KEYWORD)
	//private List<Long> interceptWordHouses;
	/**
	 * 用户id//用来数据隔离
	 */
	@IndexField(fieldType = FieldType.KEYWORD)
	private String userId;
}