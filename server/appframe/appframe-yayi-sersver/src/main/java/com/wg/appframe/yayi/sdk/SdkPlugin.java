package com.wg.appframe.yayi.sdk;


/**
 * 创建一个接口，供第三方 SDK
 */
public interface SdkPlugin {

    /** 插件唯一标识 */
    String getPluginId(String params);

    /** 执行核心逻辑, 我们可以定义入参，由我们系统传参给第三方用户执行灵活判断，目前先不给入参 */
    Result execute();

}
