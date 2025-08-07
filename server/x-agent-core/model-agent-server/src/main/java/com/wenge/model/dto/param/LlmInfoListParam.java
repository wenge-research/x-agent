package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LlmInfoListParam extends WGParam {

    private static final long serialVersionUID = -3782808040698210527L;

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
    private String tag;

    /**
     * 大模型类型
     */
    private String modelType;

    /**
     * 创建人归属类型
     */
    private String ownerType;

}
