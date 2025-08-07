package com.wg.appframe.yayi.sdk;

/**
 * 认证信息预留接口，第三方传参, 使用方（我们系统）做校验
 */
public interface CustomAccessInterface {

    Result customAccess(AccessData accessData);
}
