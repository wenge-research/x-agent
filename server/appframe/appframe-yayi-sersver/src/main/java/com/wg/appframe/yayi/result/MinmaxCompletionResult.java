package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class MinmaxCompletionResult implements Serializable {

    private static final long serialVersionUID = 8368066114849676627L;

    private String id; //	String	本次请求的唯一标识	021718049470528d92fcbe0865fdffdde********************
    private String model; //	String	本次请求实际使用的模型名称和版本	doubao-pro-4k-240515
    private String created; //	Integer	本次请求创建时间的 Unix 时间戳（秒）	1718049470
    private String object; //	String	固定为 chat.completion	chat.completion
    private Boolean input_sensitiv; //	String	固定为 chat.completion	chat.completion
    private Boolean output_sensitive; //	String	固定为 chat.completion	chat.completion
    private BaseResp base_resp; //	String	固定为 chat.completion	chat.completion
    private List<Choice> choices; //本次请求的模型输出内容

    private Usage usage; //本次请求的 tokens 用量

    @Data
    public class Choice {
        private Integer index;
        private String finish_reason;

        private Message message;
        private ChoiceDelta delta;

        private ChoiceLogprobs logprobs;
    }

    @Data
    public static class BaseResp implements Serializable {

        private static final long serialVersionUID = -5711103653457230445L;

        private Integer status_code;
        private String status_msg;
    }

    @Data
    public class Message {
        private String role;
        private String content;
        private List<MessageToolCall> tool_calls;

    }

    @Data
    public static class MessageToolCall {
        private String id;
        private String type;
        private Function function;
    }

    @Data
    public static class Function {
        private String name;
        private String arguments;
    }

    @Data
    public static class Usage {
        private Integer prompt_tokens;

        private Integer completion_tokens;

        private Integer total_tokens;
    }

    @Data
    public static class ChoiceLogprobs {
        private List<TokenLogprob> content;
    }

    @Data
    public static class TokenLogprob {
        private String token;

        private List<Integer> bytes;
        private Float logprob;
        private List<TopLogprob> top_logprob;
    }

    @Data
    public static class TopLogprob {
        private String token;
        private Float logprob;
        private List<Integer> bytes;
    }

    @Data
    public static class ChoiceDelta {
        // 思维链内容
        private String reasoning_content;
        // 输出内容
        private String content;
        private String role;
        private List<ChoiceDeltaToolCall> tool_calls;
    }
    @Data
    public static class ChoiceDeltaToolCall {
        private String id;
        private String type;
        private Integer index;
        private MinmaxCompletionResult.Function function;
    }
}
