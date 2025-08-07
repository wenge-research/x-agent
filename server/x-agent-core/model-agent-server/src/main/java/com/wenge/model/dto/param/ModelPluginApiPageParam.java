package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;

@Data
public class ModelPluginApiPageParam extends WgPageInfo {

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 模型id
     */
    private String modelPluginApiId;

    /**
     * 模型类型
     */
    private String category;


    /**
     * 0-未启用 1-启用
     */
    private String enableFlag;


    private String name;
    private String type;


}
