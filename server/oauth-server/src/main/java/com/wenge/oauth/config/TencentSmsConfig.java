// package com.wenge.oauth.config;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Configuration
// public class TencentSmsConfig {
//
//     /**
//      * API相关
//      */
//     private static String URL ="sms.tencentcloudapi.com";
//     private static final String REGION = "ap-guangzhou";
//     /**
//      * 账号相关
//      */
//     @Value("${sms.tencent.secretId}")
//     private String SECRET_ID;
//     @Value("${sms.tencent.secretKey}")
//     private String SECRET_KEY;
//
//
//     @Bean
//     public SmsClient smsClient(){
//         Credential cred = new Credential(SECRET_ID, SECRET_KEY);
//         // 实例化一个http选项，可选的，没有特殊需求可以跳过
//         HttpProfile httpProfile = new HttpProfile();
//         httpProfile.setEndpoint(URL);
//         // 实例化一个client选项，可选的，没有特殊需求可以跳过
//         ClientProfile clientProfile = new ClientProfile();
//         clientProfile.setHttpProfile(httpProfile);
//         //实例化 SMS 的 client 对象
//         return new SmsClient(cred, REGION, clientProfile);
//     }
// }
