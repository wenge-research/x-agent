// package com.wenge.model.config;
//
// import com.mybatisflex.spring.boot.ConfigurationCustomizer;
// import com.wenge.model.entity.IProxyConfig;
// import com.wenge.model.handler.IProxyConfigTypeHandler;
// import org.apache.ibatis.logging.stdout.StdOutImpl;
// import org.mybatis.spring.annotation.MapperScan;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Configuration
// @MapperScan("com.wenge.model.mapper")
// public class MyBatisPlusConfig {
//
//     @Bean
//     public ConfigurationCustomizer mybatisConfigurationCustomizer() {
//         return configuration -> {
//             configuration.getTypeHandlerRegistry().register(IProxyConfig.class, new IProxyConfigTypeHandler());
//             configuration.setLogImpl(StdOutImpl.class);
//         };
//     }
// }
//
