package com.wenge.model.workflow.component.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExtractionParameterSetting implements Serializable {

    private static final long serialVersionUID = 5376748282447114737L;

    /**
     * 模型id
     */
    private String modelId;

    /**
     * 提取自然语言配置
     */
    private List<Configuration> extractParameters;

    /**
     * 提取结构化json配置
     */
    private List<Configuration> extractJsonParameters;

    /**
     * 提取参数
     */
    @Data
    public static  class Configuration implements Serializable {

        private static final long serialVersionUID = 8799250619319448177L;

        /**
         * 提取参数名称
         */
        private String fieldName;

        /**
         * 描述
         */
        private String description;

        /**
         * 类型
         */
        private String fieldType;

    }
}
