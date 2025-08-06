package com.wenge.model.dto.param;

import com.wenge.model.workflow.entity.MetaParam;
import lombok.Data;

import java.util.List;

@Data
public class CodeToolUpdateParam {
    /**
     * 代码工具id
     */
    private String componentId;

    /**
     * 代码工具入参
     */
    private List<MetaParam> inputs;

    /**
     * 代码工具出参
     */
    private List<MetaParam> outputs;

    /**
     * 代码内容
     */
    private String code;

    /**
     * 代码语言
     */
    private String language;

    /**
     * 代码执行url
     */
    private String url;

    /**
     * 启用插件状态
     */
    private Integer status;

    /**
     * 发布商店状态
     */
    private Integer publishAppStore;


}
