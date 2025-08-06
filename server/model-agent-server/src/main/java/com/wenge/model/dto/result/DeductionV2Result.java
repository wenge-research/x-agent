package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeductionV2Result extends WGResult {

    private static final long serialVersionUID = 1342575799455536767L;

    /**
     * 组件id
     */
    private String componentId;

    /**
     * apiKey
     */
    private String apiKey;

    /**
     * apiSecret
     */
    private String apiSecret;

    /**
     * 应用名称
     */
    private String applicationName;
}
