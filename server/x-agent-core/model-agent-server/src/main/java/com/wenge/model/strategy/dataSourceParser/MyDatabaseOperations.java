package com.wenge.model.strategy.dataSourceParser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyDatabaseOperations{
    @Value("${mybatis-flex.datasource.myds.url}")
    private String url;
    @Value("${mybatis-flex.datasource.myds.username}")
    private String username;
    @Value("${mybatis-flex.datasource.myds.password}")
    private String password;
}

