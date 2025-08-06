package com.wenge.model.workflow.component.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClassificationSetting implements Serializable {

    private static final long serialVersionUID = 5376748282447114737L;

    /**
     * 模型id
     */
    private String modelId;

    /**
     * 详细分类
     */
    private List<Configuration> configurationList;

    /**
     * 分类说明
     */
    private String classificationInstructions;

    /**
     * 分类数据
     */
    @Data
    public static  class Configuration implements Serializable {

        private static final long serialVersionUID = -1226192083739817552L;

        /**
         * 类别名称，例如：分类一
         */
        private String className;

        /**
         * 分类实际内容，例如：查询天气
         */
        private String content;

        /**
         * 对应节点id
         * 前端这里获取不到对应的id，在运行时后端赋值
         */
        private String nodeId;

        /**
         * 对应边的关系，使用该值填充nodeId
         */
        private String caseId;

    }
}
