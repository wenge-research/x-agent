package com.wenge.model.utils;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/8 9:40
 */

import com.wg.appframe.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DataTransfer {

    // JDBC连接URL及数据库用户密码
    static final String SRC_DB_URL = "jdbc:mysql://172.16.100.11:3306/smart_customer_agent_demo";
    static final String SRC_DB_USER = "root";
    static final String SRC_DB_PASSWORD = "1qazXSW@";

    static final String DEST_DB_URL = "jdbc:mysql://172.16.100.11:3306/test";
    static final String DEST_DB_USER = "root";
    static final String DEST_DB_PASSWORD = "1qazXSW@";

    static final String GET_TABLES_SQL = "SHOW TABLES";

    public static List<String> getTableList(String DB_URL,String DB_USER,String DB_PASSWORD){
        List<String> tables = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet tbrs = stmt.executeQuery(GET_TABLES_SQL);
            while (tbrs.next()) {
                String tableName = tbrs.getString(1);
                tables.add(tableName);
            }
        } catch (Exception e){
            log.error(e.getMessage(),e);
            throw new GlobalException("000015", "数据库连接异常，请检查网络或者配置是否正常");
        }
        // 连接源数据库
        return tables;
    }

    public boolean tableTransfer(String SRC_DB_URL,String SRC_DB_USER,String SRC_DB_PASSWORD,
                                 String DEST_DB_URL,String DEST_DB_USER,String DEST_DB_PASSWORD){
        Connection srcConn = null;
        Connection destConn = null;
        Statement srcStmt = null;
        Statement destStmt = null;
        ResultSet rs = null;
        try {
            // 连接源数据库
            srcConn = DriverManager.getConnection(SRC_DB_URL, SRC_DB_USER, SRC_DB_PASSWORD);
            srcStmt = srcConn.createStatement();
            ResultSet tbrs = srcStmt.executeQuery("SHOW TABLES");
            List<String> tables = new ArrayList<>();
            while (tbrs.next()) {
                String tableName = tbrs.getString(1);
                tables.add(tableName);
            }
            for (String tableName : tables) {
                // 获取源端表的创建语句
                String showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
                rs = srcStmt.executeQuery(showCreateTableSQL);
                // 解析创建语句
                String createTableSQL = "";
                if (rs.next()) {
                    createTableSQL = rs.getString(2); // 第二列是创建表的语句
                }
                // 连接目标数据库
                destConn = DriverManager.getConnection(DEST_DB_URL, DEST_DB_USER, DEST_DB_PASSWORD);
                destStmt = destConn.createStatement();
                // 创建目标表
                destStmt.executeUpdate(createTableSQL);
                System.out.println("Table created in destination database.");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcStmt != null) srcStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destStmt != null) destStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcConn != null) srcConn.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destConn != null) destConn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static boolean tableDataTransfer(String SRC_DB_URL,String SRC_DB_USER,String SRC_DB_PASSWORD,
                                     String DEST_DB_URL,String DEST_DB_USER,String DEST_DB_PASSWORD,
                                     List<String> tables){
        Connection srcConn = null;
        Connection destConn = null;
        Statement srcStmt = null;
        Statement destStmt = null;
        ResultSet rs = null;
        try {
            // 连接源数据库
            srcConn = DriverManager.getConnection(SRC_DB_URL, SRC_DB_USER, SRC_DB_PASSWORD);
            srcStmt = srcConn.createStatement();
            for (String tableName : tables) {
                // 获取源端表的创建语句
                String showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
                rs = srcStmt.executeQuery(showCreateTableSQL);
                // 解析创建语句
                String createTableSQL = "";
                if (rs.next()) {
                    createTableSQL = rs.getString(2); // 第二列是创建表的语句
                }
                // 连接目标数据库
                destConn = DriverManager.getConnection(DEST_DB_URL, DEST_DB_USER, DEST_DB_PASSWORD);
                destStmt = destConn.createStatement();
                // 创建目标表
                destStmt.executeUpdate(createTableSQL);
                System.out.println("Table created in destination database.");
                // 读取源端数据并插入目标端数据库
                String queryDataSQL = "SELECT * FROM " + tableName;
                rs = srcStmt.executeQuery(queryDataSQL);
                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        Object value = rs.getObject(i);
                        if("BIT".equals(metaData.getColumnTypeName(i)) && value != null){
                            value = (boolean)value?1:0;
                        }
                        if (value == null) {
                            insertSQL.append("NULL");
                        } else {
                            if (value instanceof String){
                                String strVal = (String) value;
                                if(strVal.contains("NA")){
                                    value = strVal.replaceAll("'NA'","`NA`");
                                }
                            }
                            insertSQL.append("'").append(value).append("'");
                        }
                        if (i < metaData.getColumnCount()) {
                            insertSQL.append(",");
                        }
                    }
                    insertSQL.append(")");
                    System.out.println("正在插入的表为:" + tableName + "正在解析的插入sql为:" + insertSQL);
                    try {
                        destStmt.executeUpdate(insertSQL.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcStmt != null) srcStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destStmt != null) destStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcConn != null) srcConn.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destConn != null) destConn.close(); } catch (Exception e) { e.printStackTrace(); }
        }

    }

    public static boolean tableDataTransfer(String SRC_DB_URL,String SRC_DB_USER,String SRC_DB_PASSWORD,
                                 String DEST_DB_URL,String DEST_DB_USER,String DEST_DB_PASSWORD){
        Connection srcConn = null;
        Connection destConn = null;
        Statement srcStmt = null;
        Statement destStmt = null;
        ResultSet rs = null;
        try {
            // 连接源数据库
            srcConn = DriverManager.getConnection(SRC_DB_URL, SRC_DB_USER, SRC_DB_PASSWORD);
            srcStmt = srcConn.createStatement();

            ResultSet tbrs = srcStmt.executeQuery("SHOW TABLES");
            List<String> tables = new ArrayList<>();
            while (tbrs.next()) {
                String tableName = tbrs.getString(1);
                tables.add(tableName);
            }
            for (String tableName : tables) {

                // 获取源端表的创建语句
                String showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
                rs = srcStmt.executeQuery(showCreateTableSQL);

                // 解析创建语句
                String createTableSQL = "";
                if (rs.next()) {
                    createTableSQL = rs.getString(2); // 第二列是创建表的语句
                }

                // 连接目标数据库
                destConn = DriverManager.getConnection(DEST_DB_URL, DEST_DB_USER, DEST_DB_PASSWORD);
                destStmt = destConn.createStatement();

                // 创建目标表
                destStmt.executeUpdate(createTableSQL);
                System.out.println("Table created in destination database.");

                // 读取源端数据并插入目标端数据库
                String queryDataSQL = "SELECT * FROM " + tableName;
                rs = srcStmt.executeQuery(queryDataSQL);
                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        Object value = rs.getObject(i);
                        if("BIT".equals(metaData.getColumnTypeName(i)) && value != null){
                            value = (boolean)value?1:0;
                        }
                        if (value == null) {
                            insertSQL.append("NULL");
                        } else {
                            if (value instanceof String){
                                String strVal = (String) value;
                                if(strVal.contains("NA")){
                                    value = strVal.replaceAll("'NA'","`NA`");
                                }
                            }
                            insertSQL.append("'").append(value).append("'");
                        }
                        if (i < metaData.getColumnCount()) {
                            insertSQL.append(",");
                        }
                    }
                    insertSQL.append(")");
                    System.out.println("正在插入的表为:" + tableName + "正在解析的插入sql为:" + insertSQL);
                    try {
                        destStmt.executeUpdate(insertSQL.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcStmt != null) srcStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destStmt != null) destStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcConn != null) srcConn.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destConn != null) destConn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        Connection srcConn = null;
        Connection destConn = null;
        Statement srcStmt = null;
        Statement destStmt = null;
        ResultSet rs = null;
        try {
            // 连接源数据库
            srcConn = DriverManager.getConnection(SRC_DB_URL, SRC_DB_USER, SRC_DB_PASSWORD);
            srcStmt = srcConn.createStatement();

            ResultSet tbrs = srcStmt.executeQuery("SHOW TABLES");
            List<String> tables = new ArrayList<>();
            while (tbrs.next()) {
                String tableName = tbrs.getString(1);
                tables.add(tableName);
            }
            for (String tableName : tables) {

                // 获取源端表的创建语句
                String showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
                rs = srcStmt.executeQuery(showCreateTableSQL);

                // 解析创建语句
                String createTableSQL = "";
                if (rs.next()) {
                    createTableSQL = rs.getString(2); // 第二列是创建表的语句
                }

                // 连接目标数据库
                destConn = DriverManager.getConnection(DEST_DB_URL, DEST_DB_USER, DEST_DB_PASSWORD);
                destStmt = destConn.createStatement();

                // 创建目标表
                destStmt.executeUpdate(createTableSQL);
                System.out.println("Table created in destination database.");

                // 读取源端数据并插入目标端数据库
                String queryDataSQL = "SELECT * FROM " + tableName;
                rs = srcStmt.executeQuery(queryDataSQL);
                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        Object value = rs.getObject(i);
                        if("BIT".equals(metaData.getColumnTypeName(i)) && value != null){
                            value = (boolean)value?1:0;
                        }
                        if (value == null) {
                            insertSQL.append("NULL");
                        } else {
                            if (value instanceof String){
                                String strVal = (String) value;
                                if(strVal.contains("NA")){
                                    value = strVal.replaceAll("'NA'","`NA`");
                                }
                            }
                            insertSQL.append("'").append(value).append("'");
                        }
                        if (i < metaData.getColumnCount()) {
                            insertSQL.append(",");
                        }
                    }
                    insertSQL.append(")");
                    System.out.println("正在插入的表为:" + tableName + "正在解析的插入sql为:" + insertSQL);
                    try {
                        destStmt.executeUpdate(insertSQL.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcStmt != null) srcStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destStmt != null) destStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (srcConn != null) srcConn.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (destConn != null) destConn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
