package com.wenge.model.dto.param;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonApiParam extends WGParam {

    private static final long serialVersionUID = 1838463042267612032L;

    /**
     * 请求地址
     */
    private String uri;
    private String appSecret;
    private String appKey;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 请求参数
     */
    private cn.hutool.json.JSONObject param;

    /**
     * 请求头
     */
    private JSONObject header;

    /**
     * 配置信息
     */
    private JSONObject config;
}
