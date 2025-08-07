package com.wenge.model.dto.template;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.rely.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public class ApplicationInfoVersionDTO implements Serializable {
    @ExcelProperty("应用apikey")
    private String apiKey;

    @ExcelProperty("应用密钥")
    private String apiSecret;

    @ExcelProperty("关联application_info表id字段")
    private String applicationInfoId;

    @ExcelProperty("应用版本号")
    private String appVersionNumber;

    @ExcelProperty("应用版本号说明")
    private String versionRemark;

    @ExcelProperty("应用回退说明")
    private String backVersionRemark;

    @ExcelProperty("操作类型")
    private String makeType;

    @ExcelProperty("关联知识库关联关系备份")
    private String bindingKnowledgeIdListJson;//knowledgeIdList

    @ExcelProperty("关联知识库")
    private String bindingKnowledgeIdsJson;//knowledgeIds

    @ExcelProperty("工作流关联关系备份")
    private String bindingWorkflowIdsJson;//workflowIds
    @ExcelProperty("关联预设问题关联关系备份")
    private String bindingPresetQuestionJson;
    @ExcelProperty("关联快捷指令关联关系备份")
    private String bindingApplicationQuickCommandJson;


    @ExcelProperty("关联敏敢词关联关系备份")
    private String bindingInterceptWordhoseJson;

    @ExcelProperty("关联个性化关联关系备份")
    private String bindingConfigrationJson;

    @ExcelProperty("节点信息备份")
    private String nodesJson;

    @ExcelProperty("节点关系备份")
    private String nodeRelJson;

    @ExcelProperty("映射组件信息备份")
    private String componentJson;
    @ExcelProperty("节点参数信息备份")
    private String metaParamsJson;

    @ExcelProperty("关联插件关联关系备份")
    private String pluginDataJson;

    @ExcelProperty("应用id，有业务作用")
    private String applicationId;

    @ExcelProperty("应用名称")
    private String applicationName;

    @ExcelProperty("应用编码")
    private String applicationCode;

    @ExcelProperty("网页图标icon")
    private String webIcon;

    @ExcelProperty("无法回答话术")
    private String failureTalk;

    @ExcelProperty("欢迎语")
    private String greeting;

    @ExcelProperty("应用介绍")
    private String introduce;

    @ExcelProperty("知识库回答[是/否]")
    private String knowledgeFlag;

    @ExcelProperty("安全敏感词拦截1[是/否]")
    private String sensitiveFlag;

    @ExcelProperty("安全敏感词拦截[是/否]")
    private String subjectFlag;

    @ExcelProperty("模型回答[是/否]")
    private String modelAnswerFlag;

    @ExcelProperty("是否联网检索[是/否]")
    private String networkFlag;

    @ExcelProperty("是否模型兜底[是/否]")
    private String modelFallbackFlag;

    @ExcelProperty("是否开启问数检索[是/否]")
    private String wenshuFlag;

    @ExcelProperty("发布链接[移动端]")
    private String clientLink;

    @ExcelProperty("logo地址")
    private String logo;

    @ExcelProperty("大模型id")
    private String modelId;

    @ExcelProperty("大模型名称")
    private String modelName;

    @ExcelProperty("开启多轮对话[是/否]")
    private String multiDialogueFlag;

    @ExcelProperty("携带上下文对话轮数")
    private Integer multiDialogueNum;

    @ExcelProperty("开启图像识别[是/否]")
    private String ocrFlag;

    @ExcelProperty("有效期结束")
    private String periodEnd;

    @ExcelProperty("有效期开始时间")
    private String periodStart;

    @ExcelProperty("状态：状态：1-公开发布；2-私有发布，3-未发布，4-暂存，5-停用")
    private String publishStatus;

    @ExcelProperty("推荐问题策略")
    private String recommendation;

    @ExcelProperty("推荐问题数量")
    private Integer recommendationNum;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("问题改写[是/否]")
    private String rewritingFlag;

    @ExcelProperty("问题意图推荐[是/否]")
    private String recommendedFlag;

    @ExcelProperty("问题意图推荐-推荐问题选择话术")
    private String recommendedTalk;

    @ExcelProperty("机器人图标")
    private String robotIcon;

    @ExcelProperty("system prompt设置")
    private String systemPrompt;

    @ExcelProperty("学习成长中话术")
    private String tailTalk;

    @ExcelProperty("语音id")
    private String ttsId;

    @ExcelProperty("用户图标")
    private String userIcon;

    @ExcelProperty("开启视频识别[是/否]")
    private String videoFlag;

    @ExcelProperty("是否打开视频解析按钮，是/否")
    private String videoResolveFlag;

    @ExcelProperty("开启虚拟人[是/否]")
    private String virtualHumanFlag;

    @ExcelProperty("虚拟人id")
    private String virtualHumanId;

    @ExcelProperty("开启语音对话[是/否]")
    private String voiceDialogueFlag;

    @ExcelProperty("ai对话问题开关[是/否]")
    private String aiQuestionFlag;

    @ExcelProperty("api接口地址")
    private String api;

    @ExcelProperty("按钮样式1")
    private String buttonStyle;

    @ExcelProperty("按钮样式")
    private String buttonText;

    @ExcelProperty("问答形式检索标题分数阈值")
    private Float qaTitleScore;

    @ExcelProperty("问答形式检索正文分数阈值")
    private Float qaContentScore;

    @ExcelProperty("问答形式重排标题分数阈值")
    private Float qaRangeTitleScore;

    @ExcelProperty("问答形式重排正文（回答）分数阈值")
    private Float qaRangeContentScore;

    @ExcelProperty("检索内容分数阈值Score")
    private Float contentScore;

    @ExcelProperty("检索内容分数阈值")
    private Float rangeContentScore;

    @ExcelProperty("引用知识库文段数量(重排后的数量)filterNum")
    private Integer filterNum;

    @ExcelProperty("知识库文段预备数量(重排前的数量)")
    private Integer prepareNum;

    @ExcelProperty("回答超时，单位秒")
    private Integer answerTimeout;

    @ExcelProperty("prompt模板")
    private String promptTemplate;

    @ExcelProperty("无法回答的关键词")
    private String notAnswer;

    @ExcelProperty("免责声明")
    private String disclaimer;

    @ExcelProperty("聊天模板id")
    private String templateId;

    @ExcelProperty("移动端聊天模板id")
    private String mobileTemplateId;

    @ExcelProperty("应用身份设定")
    private String identityIcon;

    @ExcelProperty("租户id")
    private String tenantId;

    @ExcelProperty("搜索平台")
    private String networkChannel;

    @ExcelProperty("是否需要润色")
    private String polishFlag;

    @ExcelProperty("润色的prompt")
    private String polishPrompt;

    @ExcelProperty("顺序执行步骤")
    private String processStep;

    @ExcelProperty("网页页签标题")
    private String webTitle;

    @ExcelProperty("讨论话题的prompt")
    private String subjectPrompt;

    @ExcelProperty("是否禁用IP,是/否")
    private String ipFlag;

    @ExcelProperty("文档链接类型[1-原始文件链接，2-网页链接]")
    private String docLinkType;

    @ExcelProperty("是否预览溯源文件")
    private String previewDoc;

    @ExcelProperty("前台是否展示推荐问题开关[1-开，0-关]")
    private String recommendQuestionsShowFlag;

    @ExcelProperty("前台是否展示依据来源开关[1-开，0-关]")
    private String sourceShowFlag;

    @ExcelProperty("前台是否展示相关事项开关[1-开，0-关]")
    private String relatedMattersShowFlag;

    @ExcelProperty("语音识别id")
    private String sttId;

    @ExcelProperty("H5聊天认证渠道")
    private String clientAuthChannel;

    @ExcelProperty("PC聊天认证渠道")
    private String pcAuthChannel;

    @ExcelProperty("是否需要转人工，是/否")
    private String toHumanFlag;

    @ExcelProperty("输入框提示语")
    private String inputPlaceholder;

    @ExcelProperty("开场白")
    private String prologue;

    @ExcelProperty("背景图url地址")
    private String backgroundImageUrl;

    @ExcelProperty("助手形象图片地址")
    private String facadeImageUrl;

    @ExcelProperty("应用类型，null或空字符串-问答助手； search-智能搜索； workflow-工作流")
    private String type;

    @ExcelProperty("预设业务场景是否开启[是/否]")
    private String businessScenarioEnable;

    @ExcelProperty("预设业务场景话术 例如：您是否想要办理以下业务")
    private String businessScenarioScript;

    @ExcelProperty("返回旧版的网页链接")
    private String backOldVersionLink;

    @ExcelProperty("关于网站，html富文本")
    private String aboutWebsite;

    @ExcelProperty("是否打开便民服务，是/否")
    private String menuServiceFlag;

    @ExcelProperty("是否反馈，是/否")
    private String feedbackFlag;

    @ExcelProperty("是否打开政策列表，是/否")
    private String policyListFlag;

    @ExcelProperty("是否打开历史对话，是/否")
    private String historyFlag;

    @ExcelProperty("归属logo（例如：国徽图片）")
    private String attributionLogo;

    @ExcelProperty("虚拟人logo")
    private String virtualHumanLogo;

    @ExcelProperty("聊天模板路由")
    private String templateRoute;

    @ExcelProperty("移动端聊天模板路由")
    private String mobileTemplateRoute;

    @ExcelProperty("H5聊天认证渠道编码")
    private String clientAuthChannelCode;

    @ExcelProperty("PC聊天认证渠道编码")
    private String pcAuthChannelCode;

    @ExcelProperty("文档个数")
    private Long fileCount;

    @ExcelProperty("图片个数")
    private Long imageCount;

    @ExcelProperty("音频个数")
    private Long audioCount;

    @ExcelProperty("视频个数")
    private Long videoCount;

    @ExcelProperty("链接个数")
    private Long urlCount;

    @ExcelProperty("是否来自管理端")
    private String fromManage;

    @ExcelProperty("是否联网兜底[是/否]")
    private String finalNetworkFlag;

    @ExcelProperty("0-未发布（默认） 1-已发布 2-待审核")
    private Integer publishAppStore;

    @ExcelProperty("发布时间")
    private Date publishTime;

    @ExcelProperty("发布分类（聊天助手、AI翻译、AI搜索、文案写作、行业报告、图片创作、学习助手、合规审查）")
    private String publishType;

    @ExcelProperty("发布说明")
    private String publishDesc;

    @ExcelProperty("微信公众号认证绑定的appid")
    private String clientAuthChannelAppId;

    @ExcelProperty("私有发布开关 0-开启 1-关闭")
    private Integer privateSwitch;
    @ExcelProperty("公有发布开关 0-开启 1-关闭")
    private Integer publicSwitch;
    @ExcelProperty("嵌入模式开关")
    private String implantSwitch;

    @ExcelProperty("隐私政策")
    private String privacyPolicy;

    @ExcelProperty("跳转主网站链接")
    private String url;

    @ExcelProperty("跳转主网站图片")
    private String picture;
}
