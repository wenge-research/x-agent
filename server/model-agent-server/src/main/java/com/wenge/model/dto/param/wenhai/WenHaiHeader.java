package com.wenge.model.dto.param.wenhai;

/**
 * 闻海接口请求头
 */
@lombok.Data
public class WenHaiHeader {
    /**
     * AppKey
     */
    private String appKey;
    /**
     * 随机数
     */
    private String nonce;
    /**
     * 签名，详见附件客户签名生成工具类
     */
    private String signature;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 密钥
     */
    private String secretKey;
}