package com.wenge.model.enums;

/**
 * Websocket消息推送类型
 */
public enum WebsocketTypeEnums {

    KNOWLEDGE_QA_PUSH("knowledge_qa_push","知识库QA导入进度推送"),
    KNOWLEDGE_URL_PUSH("knowledge_url_push","url数据导入进度推送"),
    EXCEL_PARSER_PUSH("excel_parser_push","Excel解析任务进度推送");

    private final String code;
    private final String desc;

    WebsocketTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
