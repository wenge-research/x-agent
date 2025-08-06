package com.wenge.model.dto.param;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSONArray;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.DialogueAnswerOutline;
import com.wenge.model.dto.result.DialogueProgress;
import com.wenge.model.entity.*;
import com.wenge.model.enums.SearchWayEnum;
import com.wenge.model.enums.StepStatusEnum;
import com.wg.appframe.core.constant.StringConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * @author zwc
 * @since 2023/5/18
 */
@Data
public class DialogueByStreamParam implements Serializable {
    private static final long serialVersionUID = -639756000534908744L;

    @ApiModelProperty(value = "客户端 id", required = true)
    @NotBlank(message = "客户端 id 不能为空")
    private String clientId;

    private List<DialogueMessage> listMsg;

    private Vector<StepEntity> contextList;

    private List<DialogueMessage> recommendedListMsg;

    @NotBlank(message = "会话 id 不能为空")
    private String conversationId;

    /**
     * 回答的文本
     */
    private String answer;

    private List<DialogueAnswerOutline> outline;

    /**
     * 问题，前端对话显示使用，后台逻辑没有使用到
     */
    private String question;

    /**
     * 应用id
     */
    @NotBlank(message = "应用 id 不能为空")
    private String applicationId;

    /**
     * 用户的原问题
     */
    @NotBlank(message = "问题不能为空 不能为空")
    private String content;

    /**
     * 保存原问题处理后的临时问题，大部分场景都是用这个临时问题，而不是原问题
     */
    private String contentTemp;

    /**
     * 改写后的问题，有包括地点，（大模型给出的新问题后，有可能会添加地点数据）
     */
    private String rewriteContent;

    /**
     * 改写后的新问题（大模型给的原始的新问题）
     */
    private String newQuestion;

    /**
     * 推荐问题 模糊的问题调用大模型获取三个推荐问题
     */
    private JSONArray recommendedQuestions;

    /**
     * 是否展示推荐问题 recommendedQuestions数组大于1展示
     */
    private String showRecommendedQuestions = "true";

    /**
     * 历史对话id
     */
    private List<String> dialogueIdList;

    /**
     * 知识库id
     */
    private List<String> knowledgeIdList;

    /**
     * 回答的id
     */
    private String dialogueId;

    /**
     * 回答的纯文本
     */
    private String plainText;

    /**
     * 是否正确回答
     */
    private String answerFlag;

    /**
     * 访问ip
     */
    private String ipAddress;

    /**
     * 推荐机构
     */
    private String suggestOrg;
    /**
     * 是否推荐
     */
    private String suggest;

    /**
     * 用户提问的分类
     */
    private String contentQaType;

    /**
     * 是否需要流式输出
     */
    private String stream;

    /**
     * 网站，按钮，超链接展示配置
     */
    private List<WebsiteConfig> websiteConfigs;

    /**
     * 推流给前端，调用此方法，将会逻辑推流给前端（如果靠前的步骤正在推流或则还没结束，则将会进入等待，如果靠前的步骤没有足够的答案，那么这里就会直接给前端推数据）
     */
    private Consumer<AnswerStepData> answerConsumer;

    /**
     * 自定义数据，应对各种特殊个性化情况
     */
    private Object matterGuide;

    /**
     * 自定义数据，市监局触发的业务场景列表
     */
    private List<BusinessScenarioList> businessScenarioLists;

    /**
     * 推流进度
     */
    private StepStatusEnum finishReason;

    /**
     * 推流步骤
     */
    private String finalStepId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户名
     */
    private String userId;

    /**
     * 检索方式,MIXED -混合检索(默认)，FULL_TEXT-全文检索，SEMANTIC-语义检索
     */
    private SearchWayEnum searchWay;

    /**
     *  客户端类型来源 PC APP
     **/
    private String clientType;


    /**
     * 预设业务场景话术 例如：您是否想要办理以下业务
     **/
    private String businessScenarioScript;

    /**
     * 预设业务场景是否需要展示上传按钮 是 否
     **/
    private String businessScenarioUploadPicStatus;

    /**
     * 预设业务场景 对象
     **/
    private BusinessScenarioList businessScenario;

    /**
     *  智能搜索模块 搜索答案类型来源： -1或不传-全部（默认值）、0-文件、1-音频、2-图片、3-视频
     *  注意：该字段请勿传null
     **/
    private Integer searchType = -1;


    /**
     * 工作流的输出结果
     */
    private Map<String, Object> output;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 账号名称，针对一些用户可以免登录问答
     */
    private String accountName;

    /**
     * 推流的字段
     */
    private String streamVar;

    /**
     * 推理字段
     */
    private String reasonVar;

    /**
     * 是否联网兜底[是/否]
     */
    private String finalNetworkFlag;

    /**
     * 临时开启互联网,是/否
     */
    private String networkFlag;

    /**
     * 本次对话消耗的token数 各个步骤调用大模型消耗token值累加
     */
    private volatile Integer consumeTokensTotal = 0;


    /**
     * 是否开启调试模式
     */
    private String debuggerFlag;

    /**
     * 推理过程
     */
    private String reasoningContent;

    /**
     * 临时模型 id，前端控制
     */
    private String modelId;

    /**
     * 附件时传参
     */
    private List<AttachmentListParam> attachmentList;

    /**
     * 存储查询结果的列表。
     */
    private List<String> queryList;

    /**
     * 引用答案
     */
    private List<AnswerRefParam> refList;

    /**
     * 实时进度
     */
    private List<DialogueProgress> progressList;

    /**
     * 内置问题结果搜索结果
     */
    private KnowledgeData builtInData;

    /**
     * 流水号
     */
    private String traceId;

    /**
     * 问题拆解
     * @return
     */
    public List<String> getQueryList() {
        if (CollectionUtils.isNotEmpty(this.queryList)) {
            return this.queryList;
        } else {
            String question = StringConstant.BLANK;
            if (StringUtils.isNotBlank(this.contentTemp)) {
                question = this.contentTemp;
            }
            if (StringUtils.isBlank(question) && StringUtils.isNotBlank(this.content)) {
                question = this.content;
            }
            if (StringUtils.isBlank(question)) {
                return Lists.newArrayList();
            }
            return ListUtil.toList(question);
        }
    }
}
