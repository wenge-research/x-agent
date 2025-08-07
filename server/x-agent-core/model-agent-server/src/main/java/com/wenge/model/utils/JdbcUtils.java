package com.wenge.model.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import com.wenge.model.dto.param.JdbcExcelFieldParam;
import com.wenge.model.entity.ExcelInfoData;
import com.wenge.model.mapper.ExcelInfoDataMapper;
import com.wenge.model.strategy.dataSourceParser.MyDatabaseOperations;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JdbcUtils {

    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";

    public static boolean copyTableData(String SRC_DB_URL, String SRC_DB_USER, String SRC_DB_PASSWORD, String SRC_TABLE,
                                        String DEST_DB_URL, String DEST_DB_USER, String DEST_DB_PASSWORD) {
        Connection srcConn = null;
        Connection destConn = null;
        Statement srcStmt = null;
        Statement destStmt = null;
        ResultSet rs = null;
        try {
            // 连接源数据库
            srcConn = DriverManager.getConnection(SRC_DB_URL, SRC_DB_USER, SRC_DB_PASSWORD);
            srcStmt = srcConn.createStatement();
            // 查询源端数据表结构
            String queryTableInfo = "SELECT * FROM " + SRC_TABLE + " WHERE 1 = 0"; // 仅查询结构，不返回实际数据
            rs = srcStmt.executeQuery(queryTableInfo);
            ResultSetMetaData metaData = rs.getMetaData();
            // 构建创建表的SQL语句
            StringBuilder createTableSQL = new StringBuilder("CREATE TABLE " + SRC_TABLE + " (");
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnType = metaData.getColumnTypeName(i);
                createTableSQL.append(columnName).append(" ").append(columnType);
                if (i < columnCount) {
                    createTableSQL.append(",");
                }
            }
            createTableSQL.append(")");
            // 连接目标数据库
            destConn = DriverManager.getConnection(DEST_DB_URL, DEST_DB_USER, DEST_DB_PASSWORD);
            destStmt = destConn.createStatement();
            // 创建目标端数据表
            destStmt.executeUpdate(createTableSQL.toString());
            System.out.println("Table created in destination database.");
            // 读取源端数据并插入目标端数据库
            String queryDataSQL = "SELECT * FROM " + SRC_TABLE;
            rs = srcStmt.executeQuery(queryDataSQL);
            ResultSetMetaData dataMetaData = rs.getMetaData();
            while (rs.next()) {
                StringBuilder insertSQL = new StringBuilder("INSERT INTO " + SRC_TABLE + " VALUES (");
                for (int i = 1; i <= dataMetaData.getColumnCount(); i++) {
                    Object value = rs.getObject(i);
                    if (value == null) {
                        insertSQL.append("NULL");
                    } else {
                        insertSQL.append("'").append(value).append("'");
                    }
                    if (i < dataMetaData.getColumnCount()) {
                        insertSQL.append(",");
                    }
                }
                insertSQL.append(")");
                destStmt.executeUpdate(insertSQL.toString());
            }
            System.out.println("Data copied successfully from source to destination.");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (srcStmt != null) srcStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (destStmt != null) destStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (srcConn != null) srcConn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (destConn != null) destConn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取表数据
     * @param jdbcUrl
     * @param jdbcName
     * @param jdbcPassword
     * @param tableName
     * @param pageSize
     * @param pageNumber
     * @return
     */
    public static List<JSONObject> getTableData(String jdbcUrl, String jdbcName, String jdbcPassword, String tableName, Integer pageSize, Integer pageNumber) {
        if (StringUtils.isBlank(jdbcUrl) || StringUtils.isBlank(jdbcName) || StringUtils.isBlank(jdbcPassword) || StringUtils.isBlank(tableName)) {
            return Lists.newArrayList();
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<JSONObject> list = new ArrayList<>();
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        try {
            // 1. 注册 JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 建立连接
            conn = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);
            // 3. 创建 Statement 对象
            stmt = conn.createStatement();
            // 计算 offset
            int offset = (pageNumber - 1) * pageSize;
            // 准备 SQL 查询语句（使用 LIMIT 和 OFFSET 分页）
            String sql = "SELECT * FROM " + tableName + " LIMIT " + pageSize + " OFFSET " + offset;
            log.info("getTableData.sql:{}", sql);
            // 执行查询，获取结果集
            rs = stmt.executeQuery(sql);
            // 获取结果集的元数据（列信息）
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 处理结果集
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                // 动态遍历所有列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    jsonObj.set(columnName, value);
                }
                list.add(jsonObj);
            }

            // 关闭结果集，准备下次查询
            rs.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 5. 关闭连接
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void createTableByJdbc(String jdbcUrl, String jdbcName, String jdbcPassword, String sql) throws ClassNotFoundException {
        log.info("createTable param is jdbcUrl {},jdbcName {},jdbcPassword {},sql {}", jdbcUrl, jdbcName, jdbcPassword, sql);
        Class.forName(JDBC_DRIVER);
        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);
             Statement stmt = conn.createStatement()) {
            int i = stmt.executeUpdate(sql);
            log.info("建表完成");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getTableCreateInfo(String jdbcUrl, String jdbcName, String jdbcPassword, String tableName) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 1. 注册 JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 建立连接
            conn = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);

            // 3. 创建 Statement 对象
            stmt = conn.createStatement();

            // 4. 执行查询获取建表语句
            String sql = "SHOW CREATE TABLE " + tableName;
            rs = stmt.executeQuery(sql);

            // 5. 解析结果获取建表语句
            if (rs.next()) {
                // 第二列是建表语句
                return rs.getString(2);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 6. 关闭连接
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取表结构信息
     *
     * @param jdbcUrl
     * @param jdbcName
     * @param jdbcPassword
     * @param tableName
     * @param driverType
     * @return
     */
    public static Map<String, TableInfoVo> getTableColumnsInfo(String jdbcUrl, String jdbcName, String jdbcPassword, String tableName, String driverType) {
        Map<String, TableInfoVo> result = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<JSONObject> list = new ArrayList<>();
        try {
            // 1. 注册 JDBC 驱动
            Class.forName(driverType);
            // 2. 建立连接
            conn = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);
            // 3. 创建 Statement 对象
            stmt = conn.createStatement();
            // 计算 offset
            // 准备 SQL 查询语句（使用 LIMIT 和 OFFSET 分页）

            String tableSchema = StringConstant.BLANK;
            if (tableName.contains(".")) {
                tableSchema = tableName.split("\\.")[0];
                tableName = tableName.split("\\.")[1];
            }
            String sql = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE,COLUMN_COMMENT,IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '{tableSchema}' and TABLE_NAME = '{tableName}' ;".replace("{tableSchema}", tableSchema).replace("{tableName}", tableName);
            log.info("getTableColumnsInfo.sql:{}", sql);
            // 执行查询，获取结果集
            rs = stmt.executeQuery(sql);
            // 获取结果集的元数据（列信息）
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 处理结果集
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                // 动态遍历所有列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    jsonObj.set(columnName, value);
                }
                list.add(jsonObj);
            }

            // 获取结果集的元数据（列信息）
            List<TableInfoVo> tableInfoVos = list.stream().map(p -> {
                TableInfoVo tableInfoVo = new TableInfoVo();
                tableInfoVo.setColumnName(p.getStr("COLUMN_NAME"));
                tableInfoVo.setColumnType(p.getStr("DATA_TYPE"));
                tableInfoVo.setNullable("YES".equals(p.getStr("IS_NULLABLE")));
                tableInfoVo.setRemarks(p.getStr("COLUMN_COMMENT"));
                return tableInfoVo;
            }).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(tableInfoVos)) {
                result = tableInfoVos.stream().collect(Collectors.toMap(
                        TableInfoVo::getColumnName,
                        p -> p,
                        (k1, k2) -> k1,
                        HashMap::new
                ));
            }
            // 关闭结果集，准备下次查询
            rs.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 5. 关闭连接
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String buildCreateTableSql(List<JdbcExcelFieldParam> columns, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE ").append(tableName).append(" (");
        for (int i = 0; i < columns.size(); i++) {
            JdbcExcelFieldParam column = columns.get(i);
            String columnName = column.getCellName();
            String columnType = getSQLTypeFromExcelType(column.getCellType());
            sqlBuilder.append(columnName).append(" ").append(columnType);
            // 如果不是最后一列，添加逗号分隔
            if (i < columns.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(");");
        return sqlBuilder.toString();
    }

    private static String getSQLTypeFromExcelType(String cellType) {
        // 根据 Excel 的单元格类型 cellType，映射到对应的数据库类型
        switch (cellType) {
            case "STRING":
                return "VARCHAR(255)"; // 适当调整长度
            case "NUMERIC":
                return "NUMERIC";
            case "BOOLEAN":
                return "BOOLEAN";
            case "DATE":
                return "DATE";
            default:
                return "VARCHAR(255)";
        }
    }

    public static Map<String, Object> getSingleRecord(String jdbcUrl, String jdbcName, String jdbcPassword, String tableName,String driverType) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Map<String, Object> result = new HashMap<>();

        try {
            Class.forName(driverType);
            conn = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM " + tableName + " LIMIT 1";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    result.put(columnName, value);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static int batchInsertExcelData(List<ExcelInfoData> excelInfoDataList, String driverType, MyDatabaseOperations myDatabaseOperations) {
        //String jdbcUrl = "jdbc:mysql://172.16.100.11:3306/smart_customer_agent_demo";
        //String jdbcName = "root";
        //String jdbcPassword = "1qazXSW@";
        // String sql = "INSERT IGNORE INTO excel_info_data_sz (sql_source, table_name, knowledgeId, excel_info) VALUES (?,?,?,?)";

        ApplicationContext context = CoreContextProvider.getContext();
        ExcelInfoDataMapper excelInfoDataMapper = context.getBean(ExcelInfoDataMapper.class);

        // DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // dataSource.setDriverClassName(driverType);
        // dataSource.setUrl(myDatabaseOperations.getUrl());
        // dataSource.setUsername(myDatabaseOperations.getUsername());
        // dataSource.setPassword(myDatabaseOperations.getPassword());
        // JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        try {
            excelInfoDataMapper.insertBatch(excelInfoDataList);
            // return jdbcTemplate.batchUpdate(sql, excelInfoDataList.stream()
            //         .map(data -> new Object[]{
            //                 data.getSqlSource(),
            //                 data.getTableName(),
            //                 data.getKnowledgeId(),
            //                 data.getExcelInfo()
            //         })
            //         .collect(Collectors.toList())).length;
        } catch (DataAccessException e) {
            log.error("插入外部数据库失败，URL: {}", myDatabaseOperations.getUrl(), e);
            throw new RuntimeException("跨数据库写入失败", e);
        }
        return excelInfoDataList.size();
    }
}
