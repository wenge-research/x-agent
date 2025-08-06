package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ScMeteSearchResult implements Serializable {

    private static final long serialVersionUID = -8932356140008476352L;

    /**
     * 响应码【200代表成功,其他参见错误码值表】)
     */
    private Long code;

    /**
     * 响应数据
     */
    private List<YayiMetaSearchVO> data;

    /**
     * 错误信息集合
     */
    private List<String> errors;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 是否成功【true代表成功,false代表失败】
     */
    private Boolean success;

    /**
     * 响应时间
     */
    private OffsetDateTime time;


    /**
     * YayiMetaSearchVO，雅意原搜索出参
     */
    @Data
    public static class YayiMetaSearchVO {
        /**
         * 正文内容
         */
        private String content;
        /**
         * 摘要
         */
        private String description;
        /**
         * 是否需要渲染
         */
        private Long isRender;
        /**
         * 发布时间
         */
        private String pubtime;
        /**
         * 标题
         */
        private String title;
        /**
         * 链接
         */
        private String url;
    }

}
