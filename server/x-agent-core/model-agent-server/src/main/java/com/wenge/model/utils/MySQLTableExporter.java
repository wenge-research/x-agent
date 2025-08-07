package com.wenge.model.utils;

import com.alibaba.excel.EasyExcel;
import com.wenge.model.utils.pojo.TableInfoExport;

import java.io.File;
import java.sql.*;
import java.util.*;

public class MySQLTableExporter {

    private static final String URL = "jdbc:mysql://xxxxx:xxx/xxxxxxx";
    private static final String USER = "xxxxxx";
    private static final String PASSWORD = "xxxx";
    private static final String your_database_name = "xxxxx";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        List<TableInfoExport> tableInfoExportList = new ArrayList<>();
        try {
            // 1. 注册 JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 打开连接
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            List<String> tablesNeed =  Arrays.asList("application_info","application_configuration","application_knowledge","application_plugin","application_plugin_data","application_user","application_user_configuration","auth_channel","business_scenario_list","component_info","component_node","component_node_2025_01_03","component_node_memory","component_node_meta_param","component_node_rel","conversation","data_source_parser_info","dense_vector","dialog_template","dict_item","dict_type","excel_parser_file","file","folders","government_affairs_component_info","grant_data","guang_xin_matter","intelligent_translation_file","intercept_word","intercept_word_file","intercept_word_handling_method","intercept_word_house","intercept_word_house_application_rel","intercept_word_type","ip_blacklist","knowledge_data_type","knowledge_info","legal_and_regulatory_data","lh_online","life_serve_guide_info","llm_info","matter_guide","matter_guide_data","matter_guide_filed","matter_guide_group","matter_guide_info","matter_guide_option","matter_guide_type","model_plugin_api_auth_user","model_plugin_api_manage","oauth_dept","oauth_login_log","oauth_menu","oauth_role","oauth_role_menu","oauth_tenant","oauth_user","oauth_user_role","opera_log","parse_website","preset_question","prompt_config","review_results","scene_application_ref","scene_management","scene_matter_group_ref","service_menu","smart_prompt_config","structure_table_config","url_parser_info","virtual_human_component_info","virtual_human_sdk_list","voice_component_info","website_config","wenhai_config","workflow_node_executions");

            // 3. 执行元数据查询
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, "smart_customer_agent_demo", "%", new String[]{"TABLE"});
            System.out.println("总计需要遍历的表数量:" + tables);
            Set<String> tableCommentSet = new HashSet<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if(!tablesNeed.contains(tableName)){
                    continue;
                }
                System.out.println("Table: " + tableName);
                    if (tableCommentSet.contains(tableName)) {
                        continue;
                    }
                    tableCommentSet.add(tableName);
                    String columnCount = "";
                    // 获取表注释（描述）
                    String tableComment = getTableComment(connection, tableName);
                    System.out.println("Description: " + tableComment);
                    try {
                        //表数据量
                        columnCount = getColumnCount(connection, tableName, null);
                    } catch (SQLException e) {
                        e.getMessage();
                        continue;
                    }


                    // 获取列信息
                    ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                    int i = 0;
                    Set<String> feildCommentSet = new HashSet<>();
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        if (feildCommentSet.contains(columnName)) {
                            continue;
                        }
                        feildCommentSet.add(columnName);
                        String columnType = columns.getString("TYPE_NAME");
                        int columnSize = columns.getInt("COLUMN_SIZE");
                        String columnDef = columns.getString("COLUMN_DEF");
                        String isNullable = columns.getString("NULLABLE");
                        String isPrimaryKey = getPrimaryKeyStatus(connection, tableName, columnName);
                        String columnComment = getColumnComment(connection, tableName, columnName);

                        System.out.println("  Column: " + columnName);
                        System.out.println("    Type: " + columnType);
                        System.out.println("    Size: " + columnSize);
                        System.out.println("    Description: " + columnComment);
                        System.out.println("    Nullable: " + isNullable);
                        System.out.println("    Primary Key: " + isPrimaryKey);
                        System.out.println("    Default Value: " + (columnDef != null ? columnDef : "NULL"));
                        System.out.println();
                        TableInfoExport tableInfo = new TableInfoExport();
                        tableInfo.setTableEnglishName(tableName);
                        tableInfo.setTableChineseName(tableComment);
                        tableInfo.setCurrentDataTableNumber(columnCount); //当前数据表条数
                        tableInfo.setIncrementalDataNumber("0"); //增量数据条数
                        tableInfo.setFieldNumber((i + 1) + ""); //字段序号
                        tableInfo.setFieldEnglishName(columnName); //字段英文名
                        tableInfo.setFieldChineseName(columnComment);//字段中文名
                        tableInfo.setFieldType(columnType);//字段类型
                        tableInfo.setFieldLength(columnSize + "");//字段长度
                        tableInfo.setIsPrimaryKey(isPrimaryKey);
                        tableInfo.setIsNull(isNullable.equals("0") ? "否" : "是");
                        tableInfo.setReferenceStandardNumber(columnComment);
                        tableInfoExportList.add(tableInfo);
                        i++;

                    }

                    System.out.println("------------------结束----------------" + tableName);
                    System.out.println("------------------结束----------------" + tableName);
                    System.out.println("------------------结束----------------" + tableName);
                }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 4. 最后块用于关闭资源
            try {
                if (statement != null) statement.close();
            } catch (SQLException se2) {
            } // 什么都不做
            try {
                if (connection != null) connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        //导出
        simpleWrite(tableInfoExportList);
    }

    private static String getTableComment(Connection connection, String tableName) throws SQLException {
        String query = "SHOW FULL TABLES WHERE Table_type = 'BASE TABLE' AND Tables_in_your_database_name = '" + tableName + "'";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query.replace("your_database_name", your_database_name));
            if (rs.next()) {
                String tableWithCommentQuery = "SHOW TABLE STATUS LIKE '" + tableName + "'";
                ResultSet tableStatusRs = stmt.executeQuery(tableWithCommentQuery);
                if (tableStatusRs.next()) {
                    return tableStatusRs.getString("Comment");
                }
            }
        }
        return "";
    }

    private static String getColumnComment(Connection connection, String tableName, String columnName) throws SQLException {
        String query = "SHOW FULL COLUMNS FROM " + tableName + " LIKE '" + columnName + "'";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("Comment");
            }
        }
        return "";
    }

    private static String getColumnCount(Connection connection, String tableName, String columnName) throws SQLException {
        String query = "SELECT  count(1) count FROM " + tableName + "";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("count");
            }
        }
        return "";
    }

    private static String getPrimaryKeyStatus(Connection connection, String tableName, String columnName) throws SQLException {
        String query = "SHOW INDEX FROM " + tableName + " WHERE Key_name = 'PRIMARY' AND Column_name = '"+columnName+"'";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            return rs.next() ? "是" : "否";
        }
    }


    public static void simpleWrite(List<TableInfoExport> data) {
        String fileName = null;
        // 写法2
        fileName = System.getProperty("user.dir") + File.separator + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        System.out.println("文件路径：" + fileName);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, TableInfoExport.class).sheet("sheet1").doWrite(data);
    }

}