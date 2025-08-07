package com.wg.appframe.yayi.sdk;

import lombok.Data;

/**
 * 认证信息预留类
 **/
@Data
public class AccessData {
    private String appKey;
    private String appSecret;
    private String data;
}
