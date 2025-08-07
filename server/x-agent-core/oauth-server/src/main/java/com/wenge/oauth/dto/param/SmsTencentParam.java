package com.wenge.oauth.dto.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 腾讯短信平台,接口文档：https://cloud.tencent.com/document/api/382/52077
 */
@Data
public class SmsTencentParam implements Serializable {

    private static final long serialVersionUID = 222587192845697086L;

    /**
     * 下发手机号码，采用 E.164 标准，格式为+[国家或地区码][手机号]，单次请求最多支持200个手机号且要求全为境内手机号或全为境外手机号。
     * 例如：+8618501234444， 其中前面有一个+号 ，86为国家码，18501234444为手机号。
     * 注：发送国内短信格式还支持0086、86或无任何国家或地区码的11位手机号码，前缀默认为+86。
     * 示例值：["+8618501234444"]
     */
    private List<String> PhoneNumberSet;

    /**
     * 短信 SdkAppId，在 短信控制台 添加应用后生成的实际 SdkAppId，示例如1400006666。
     * 示例值：1400006666
     */
    private String SmsSdkAppId;

    /**
     * 模板 ID，必须填写已审核通过的模板 ID。模板 ID 可前往 国内短信 或 国际/港澳台短信 的正文模板管理查看，若向境外手机号发送短信，仅支持使用国际/港澳台短信模板。
     * 示例值：1110
     */
    private String TemplateId;

    /**
     * 	短信签名内容，使用 UTF-8 编码，必须填写已审核通过的签名，例如：腾讯云，签名信息可前往 国内短信 或 国际/港澳台短信 的签名管理查看。
     * 	注意
     * 发送国内短信该参数必填，且需填写签名内容而非签名ID。
     * 发送国际/港澳台短信该参数非必填。
     */
    private String SignName;

    /**
     * 模板参数，若无模板参数，则设置为空。
     * 注意
     * 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致。
     */
    private List<String> TemplateParamSet;

    /**
     * 用户的 session 内容，可以携带用户侧 ID 等上下文信息，server 会原样返回。注意长度需小于512字节。
     * 示例值：outsid_1729495320_1011
     */
    private String SessionContext;

    /**
     * 国内短信无需填写该项；国际/港澳台短信已申请独立 SenderId 需要填写该字段，默认使用公共 SenderId，无需填写该字段。
     * 注：月度使用量达到指定量级可申请独立 SenderId 使用，详情请联系 腾讯云短信小助手。
     * 示例值：Qsms
     */
    private String SenderId;

    private String secretId;
    private String secretKey;
    private String token;
    private String service;
    private String version;
    private String action;
    private String region;

}
