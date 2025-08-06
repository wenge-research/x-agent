package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LlmPageListParam extends WgPageInfo {

    private static final long serialVersionUID = 2414147571356252988L;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 厂商名称
     */
    private String manufacturer;

    /**
     * 状态[启用/停用]
     */
    private String status;

    /**
     * 标签
     */
    private String tags;

    /**
     * 大模型类型
     */
    private String modelType;

    /**
     * 是否来源于前台聊天，是/否
     */
    private String fromClientFlag;
}
