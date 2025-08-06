package com.wenge.model.strategy.dataSourceParser;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractTemplate {

    // 抽象方法，由子类实现获取连接的具体逻辑
   // protected abstract Connection getConnection();

    protected String getCountSqlStr(String tableName) {
       return String.format("SELECT COUNT(*) AS total FROM %s", tableName);
    }

    protected String getPageSqlStr(String tableName) {
        return String.format("SELECT * FROM %s LIMIT ? OFFSET ?", tableName);
    }

    // 具体方法，关闭连接
    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
