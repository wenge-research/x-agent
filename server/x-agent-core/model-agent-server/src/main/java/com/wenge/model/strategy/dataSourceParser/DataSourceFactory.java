package com.wenge.model.strategy.dataSourceParser;

import org.springframework.stereotype.Component;

@Component
public class DataSourceFactory {
    public DataSource getDataSource(Integer dataSourceType) {
        switch (dataSourceType) {
            case 1:
                return new MySQLDataSource();
            case 2:
                return new DMDataSource();
            case 3:
                return new YashanDataSource();
            default:
                throw new IllegalArgumentException("暂不支持该类数据源");
        }
    }
}
