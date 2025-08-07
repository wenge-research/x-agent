package com.wenge.model.dto.param.wenhai;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScApiParam extends WGParam {

    private static final long serialVersionUID = -4763927449684284027L;

    /**
     * 请求地址
     */
    private String uri;

    /**
     * 密钥
     */
    private String appSecret;

    /**
     * appkey
     */
    private String appKey;

    /**
     * 请求参数
     */
    private JSONObject param;

    /**
     * 请求头
     */
    private JSONObject header;

    /**
     * 配置信息
     */
    private JSONObject config;
}
