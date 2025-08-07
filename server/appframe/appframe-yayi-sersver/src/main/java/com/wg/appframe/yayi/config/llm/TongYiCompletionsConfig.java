package com.wg.appframe.yayi.config.llm;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.TranslationOptions;
import com.wg.appframe.yayi.entity.YayiMessage;

import java.util.List;
import java.util.Optional;

/**
 * 通义千问请求参数
 */
public class TongYiCompletionsConfig extends GeneralConfig {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 历史对话组成的消息列表
     */
    private List<YayiMessage> messages;

    /**
     * 是否流式返回
     */
    private Boolean stream;

    /**
     * 当启用流式输出时，可通过将本参数设置为{"include_usage": true}，在输出的最后一行显示所使用的Token数。
     * 如果设置为false，则最后一行不显示使用的Token数。
     * 本参数仅在设置stream为true下生效。
     */
    private JSONObject stream_options;

    /**
     * 采样温度
     */
    private float temperature;

    /**
     * 核采样的概率阈值，控制模型生成文本的多样性。
     * top_p越高，生成的文本更多样。反之，生成的文本更确定。
     * 取值范围：（0,1.0]
     */
    private float top_p;

    /**
     * 控制模型生成文本时的内容重复度。
     * 取值范围：[-2.0, 2.0]。正数会减少重复度，负数会增加重复度。
     */
    private float presence_penalty;


    /**
     * 返回内容的格式。可选值：{"type": "text"}或{"type": "json_object"}。设置为{"type": "json_object"}时会输出标准格式的JSON字符串。
     */
    private JSONObject response_format;

    /**
     * 本次请求返回的最大 Token 数
     */
    private Integer max_tokens;

    /**
     * 设置seed参数会使文本生成过程更具有确定性，通常用于使模型每次运行的结果一致。
     * 在每次模型调用时传入相同的seed值（由您指定），并保持其他参数不变，模型将尽可能返回相同的结果。
     * 取值范围：无符号64位整数，即0到264−1。
     */
    private Integer seed;

    /**
     * 使用stop参数后，当模型生成的文本即将包含指定的字符串或token_id时，将自动停止生成。
     * 您可以在stop参数中传入敏感词来控制模型的输出。
     */
    private String stop;

    /**
     * 可供模型调用的工具数组，可以包含一个或多个工具对象。一次function call流程模型会从中选择一个工具
     */
    private List<LlmTool> tools;

    /**
     * 当您使用翻译模型时需要配置的翻译参数。
     */
    private TranslationOptions translation_options;

    /**
     * 模型在生成文本时是否使用互联网搜索结果进行参考。
     */
    private Boolean enable_search;

    public TongYiCompletionsConfig(GeneralConfig generalConfig) {
        super();
        this.model = generalConfig.getModel();
        this.messages = generalConfig.getMessages();
        this.stream = generalConfig.getStream();
        Optional.ofNullable(generalConfig.getTemperature())
                .ifPresent((temperature) -> this.temperature = temperature.floatValue());
        Optional.ofNullable(generalConfig.getTopP())
                .ifPresent((topP) -> this.top_p = topP.floatValue());
        Optional.ofNullable(generalConfig.getPresencePenalty())
                .ifPresent((pp) -> this.presence_penalty = pp.floatValue());
        Optional.ofNullable(generalConfig.getResponseFormat())
                .ifPresent((format) -> this.response_format = JSONUtil.parseObj(format));
        this.max_tokens = generalConfig.getMaxTokens();
        this.seed = generalConfig.getSeed();
        this.stop = generalConfig.getStop();
        this.tools = generalConfig.getTools();
        Optional.ofNullable(generalConfig.getDisableSearch())
                .ifPresent((disable) -> this.enable_search = !disable);
    }

}
