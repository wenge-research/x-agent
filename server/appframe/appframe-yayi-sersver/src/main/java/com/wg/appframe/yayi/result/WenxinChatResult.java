package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WenxinChatResult implements Serializable {

    private String id;
    private String errorCode;
    private String errorMsg;
    private String object;
    private Long created;

    /**
     * 表示当前子句的序号。只有在流式接口模式下会返回该字段
     */
    private Long sentenceId;

    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     */
    private Boolean isEnd;

    /**
     * 当前生成的结果是否被截断
     */
    private Boolean isTruncated;

    /**
     * 	输出内容标识，说明：
     * · normal：输出内容完全由大模型生成，未触发截断、替换
     * · stop：输出结果命中入参stop中指定的字段后被截断
     * · length：达到了最大的token数，根据EB返回结果is_truncated来截断
     * · content_filter：输出内容被截断、兜底、替换为**等
     */
    private String finishReason;

    /**
     * 搜索数据，当请求参数enable_citation或enable_trace为true，并且触发搜索时，会返回该字段
     */
    private List<SearchResult> searchInfo;

    /**
     * 对话返回结果
     */
    private String result;

    /**
     * 表示用户输入是否存在安全风险，是否关闭当前会话，清理历史会话信息
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
     * false：否，表示用户输入无安全风险
     */
    private Boolean needClearHistory;

    /**
     * 说明：返回flag表示触发安全
     */
    private Integer flag;

    /**
     * 当need_clear_history为true时，此字段会告知第几轮对话有敏感信息，如果是当前问题，ban_round=-1
     */
    private Integer banRound;

    private DeepseekCompletionResult.GenerateUsage usage;

    @Data
    public static class SearchResult implements Serializable {

        private static final long serialVersionUID = -8627622933453511454L;
        private Integer index;

        /**
         * 搜索结果URL
         */
        private String url;

        /**
         * 搜索结果标题
         */
        private String title;

    }
    @Data
    public static class GenerateUsage implements Serializable {

        private static final long serialVersionUID = -4763741427018102704L;

        private String prompt_tokens;
        private String completion_tokens;
        private String total_tokens;
    }
    private static final long serialVersionUID = 6151577387249893461L;

}
