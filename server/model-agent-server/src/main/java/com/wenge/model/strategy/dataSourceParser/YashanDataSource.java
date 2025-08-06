package com.wenge.model.strategy.dataSourceParser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.utils.DataTransfer;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class YashanDataSource extends AbstractTemplate implements DataSource{
    @Override
    public List<String> getAllTables(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword) {
        List<String> tables = new ArrayList<>();
        try{
            Class.forName("com.yashandb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword);
            Statement stmt = conn.createStatement();
            ResultSet tbrs = stmt.executeQuery("SELECT * FROM ALL_TABLES WHERE owner = '"+sourceJdbcName+"'");
            while (tbrs.next()) {
                String tableName = tbrs.getString(2);
                tables.add(tableName);
            }
        }catch (Exception e){
            throw new GlobalException("000015", "数据库连接异常，请检查网络或者配置是否正常");
        }
        // 连接源数据库
        return tables;
    }

    @Override
    public Map<String, Object> getAllDataFromTable(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword, String tableName, Integer pageNum, Integer pageSize) {
        List<JSONObject> rowList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<String> allTableInfo = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword);
             PreparedStatement countStmt = conn.prepareStatement(getCountSqlStr(tableName));
             PreparedStatement dataStmt = conn.prepareStatement(getPageSqlStr(tableName));
             ResultSet countRs = countStmt.executeQuery()) {

            // 获取总数
            countRs.next();
            int totalRecords = countRs.getInt("total");

            // 分页查询数据
            int offset = (pageNum - 1) * pageSize;
            dataStmt.setInt(1, pageSize);
            dataStmt.setInt(2, offset);
            try (ResultSet dataRs = dataStmt.executeQuery()) {
                ResultSetMetaData metaData = dataRs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    allTableInfo.add(metaData.getColumnName(i));
                }


                // 处理分页数据
                while (dataRs.next()) {
                    JSONObject row = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = dataRs.getObject(i);
                        row.put(allTableInfo.get(i-1), value); // 使用已收集的列名
                    }
                    System.out.println(row.toString());
                    rowList.add(row);
                }
            }
            PageInfo<JSONObject> pageInfo = new PageInfo<>(pageNum, pageSize, totalRecords, rowList);
            result.put("data", pageInfo);
            result.put("allTableInfo", allTableInfo);
        } catch (SQLException e) {
            log.error("获取数据失败", e);
        }
        return result;
    }

    @Override
    public Map<String, Object> getAllDataFromTable(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword, String tableName, Integer pageNum, Integer pageSize, String bussiness,MyDatabaseOperations myDatabaseOperations) throws SQLException {
        List<JSONObject> rowList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<String> allTableInfo = new ArrayList<>();
        Connection connection = DriverManager.getConnection(myDatabaseOperations.getUrl(), myDatabaseOperations.getUsername(), myDatabaseOperations.getPassword());
        Statement myStatement = connection.createStatement();
        List<TableInfoVo> tableInfoVos =new ArrayList<>(1);
        try{
            Class.forName("com.yashandb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword);
            PreparedStatement countStmt = conn.prepareStatement(getCountSqlStr(tableName));
            PreparedStatement dataStmt = conn.prepareStatement(getPageSqlStr(tableName));
            ResultSet resultSet = myStatement.executeQuery("SELECT * FROM structure_table_config where table_name='" + tableName + "' and" + " business_id='" + bussiness + "'");
            ResultSet countRs = countStmt.executeQuery();
            // 处理分页数据
            while (resultSet.next()) {
                String string = resultSet.getString("all_field");
                tableInfoVos = JSON.parseArray(string, TableInfoVo.class);
                System.out.println(tableInfoVos);
            }
            // 获取总数
            countRs.next();
            int totalRecords = countRs.getInt("total");

            // 分页查询数据
            int offset = (pageNum - 1) * pageSize;
            dataStmt.setInt(1, pageSize);
            dataStmt.setInt(2, offset);
            try (ResultSet dataRs = dataStmt.executeQuery()) {
                ResultSetMetaData metaData = dataRs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    allTableInfo.add(metaData.getColumnName(i));
                }


                // 处理分页数据
                while (dataRs.next()) {
                    JSONObject row = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = dataRs.getObject(i);
                        row.put(allTableInfo.get(i-1), value); // 使用已收集的列名
                    }
                    System.out.println(row.toString());
                    rowList.add(row);
                }
            }
            PageInfo<JSONObject> pageInfo = new PageInfo<>(pageNum, pageSize, totalRecords, rowList);
            result.put("data", pageInfo);
            result.put("allTableInfo", tableInfoVos);
        } catch (SQLException e) {
            log.error("获取数据失败", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
