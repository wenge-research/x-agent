package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 关芯智巡参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProviderGuanxinParam extends WGParam {

    private static final long serialVersionUID = -269851621221459922L;

    /**
     * 请求id
     */
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 数据id
     */
    private String dataId;

    /**
     * 模型
     */
    private String module;

    /**
     * 场景描述
     */
    private String sceneDescription;

    /**
     * 图片地址
     */
    private List<String> imageUrl;

    /**
     * 事项指标集
     */
    private List<Content> contentList;

    /**
     * 回调地址
     */
    private String callBackUrl;

    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = -9195984799308390133L;

        /**
         * 事项名称
         */
        private String matter;

        /**
         * 索引号
         */
        private Integer index;

        /**
         * 检查指标
         */
        private List<String> checkItemList;
    }
}
