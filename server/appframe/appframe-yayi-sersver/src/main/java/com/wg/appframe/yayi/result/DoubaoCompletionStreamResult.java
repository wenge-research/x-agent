//package com.wg.appframe.yayi.result;
//
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 豆包流式输出响应结果
// */
//@Data
//public class DoubaoCompletionStreamResult implements Serializable {
//    private static final long serialVersionUID = 214378837423332738L;
//
//    private String id; //	String	本次请求的唯一标识	021718049470528d92fcbe0865fdffdde********************
//    private String model; //	String	本次请求实际使用的模型名称和版本	doubao-pro-4k-240515
//    private String created; //	Integer	本次请求创建时间的 Unix 时间戳（秒）	1718049470
//    private String object; //	String	固定为 chat.completion	chat.completion
//    private List<StreamChoice> choices; //本次请求的模型输出内容
//
//    private DoubaoCompletionResult.Usage usage; //本次请求的 tokens 用量
//
//    @Data
//    public static class StreamChoice {
//        private Integer index;
//        private String finish_reason;
//        private ChoiceDelta delta;
//        private DoubaoCompletionResult.ChoiceLogprobs logprobs;
//    }
//
//    @Data
//    public static class ChoiceDelta {
//        private String content;
//        private String role;
//        private List<ChoiceDeltaToolCall> tool_calls;
//    }
//    @Data
//    public static class ChoiceDeltaToolCall {
//        private String id;
//        private String type;
//        private Integer index;
//        private DoubaoCompletionResult.Function function;
//    }
//}
