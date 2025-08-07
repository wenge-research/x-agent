///*
// * Copyright 2022- 2022 Wenge Group Holding Ltd.
// */
//
//package com.wg.appframe.elasticsearch.properties;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
///**
// * <p>
// * ElasticSearch配置
// * </p>
// *
// * @author yangyunjun
// * @since 2022-04-18
// */
//@Setter
//@Getter
//@ConditionalOnProperty(prefix = "es", name = "host")
//@ConfigurationProperties(prefix = "es", ignoreUnknownFields = true)
//public class ElasticSearchProperties {
//
//    /**
//     * 服务地址
//     */
//    private String host;
//
//    /**
//     * 服务端口
//     */
//    private Integer port;
//
//    /**
//     * 用户名
//     */
//    private String username;
//
//    /**
//     * 密码
//     */
//    private String password;
//
//    /**
//     * 协议
//     */
//    private String scheme;
//
//    /**
//     * 链接建立超时时间
//     */
//    private Integer connectTimeOut = 120000;
//
//    /**
//     * 等待数据超时时间
//     */
//    private Integer socketTimeOut = 120000;
//
//    /**
//     * 连接池获取连接的超时时间
//     */
//    private Integer connectionRequestTimeOut = 180000;
//
//    /**
//     * 存活时间
//     */
//    private Integer keepAliveStrategy = 60000;
//}
