package com.wenge.model.strategy.dataSourceParser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.DataSourceParserInfo;
import com.wenge.model.utils.DataTransfer;
import com.wenge.model.utils.PageInfo;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import io.micrometer.core.instrument.util.StringUtils;
import com.wenge.model.vo.TableInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.workflow.constants.SettingConstants.MYSQL_SQL_DEFAULT_DATA;

@Slf4j
public class MySQLDataSource extends AbstractTemplate implements DataSource {
    @Override
    public List<String> getAllTables(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword) {
        return DataTransfer.getTableList(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword);
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
                        row.put(allTableInfo.get(i - 1), value); // 使用已收集的列名
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
    public Map<String, Object> getAllDataFromTable(String sourceJdbcUrl, String sourceJdbcName,
                                                   String sourceJdbcPassword, String tableName,
                                                   Integer pageNum, Integer pageSize, String bussiness, MyDatabaseOperations myDatabaseOperations) throws SQLException {
        List<JSONObject> rowList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<String> allTableInfo = new ArrayList<>();
        List<TableInfoVo> tableInfoVos = new ArrayList<>(1);
        Connection connection = DriverManager.getConnection(myDatabaseOperations.getUrl(), myDatabaseOperations.getUsername(), myDatabaseOperations.getPassword());
        Statement myStatement = connection.createStatement();
        try (Connection conn = DriverManager.getConnection(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword);
             PreparedStatement countStmt = conn.prepareStatement(getCountSqlStr(tableName));
             PreparedStatement dataStmt = conn.prepareStatement(getPageSqlStr(tableName));
             ResultSet resultSet = myStatement.executeQuery("SELECT * FROM structure_table_config where table_name='" + tableName + "' and" + " business_id='" + bussiness + "'");
             ResultSet countRs = countStmt.executeQuery()) {
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
            log.debug("dataStmt.SQL: {}", dataStmt);
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
                        row.put(allTableInfo.get(i - 1), value); // 使用已收集的列名
                    }
                    // System.out.println(row.toString());
                    rowList.add(row);
                }
            }
            PageInfo<JSONObject> pageInfo = new PageInfo<>(pageNum, pageSize, totalRecords, rowList);
            result.put("data", pageInfo);
            result.put("allTableInfo", tableInfoVos);
        } catch (SQLException e) {
            log.error("获取数据失败", e);
        }
        return result;
    }

    @Override
    public String insertData(DataSourceParserInfo datasourceFroNode,
                             String tableName, List<String> columeList, List<Map<String, Object>> dataList) {
        if (CollectionUtils.isEmpty(dataList) || StringUtils.isBlank(tableName) || null == datasourceFroNode || StringUtils.isBlank(datasourceFroNode.getJdbcUrl()) || StringUtils.isBlank(datasourceFroNode.getJdbcName()) || StringUtils.isBlank(datasourceFroNode.getJdbcPassword())) {
            return StringConstant.BLANK;
        }

        Connection conn = null;
        PreparedStatement updateStatement = null;
        String msg = StringConstant.BLANK;

        // 构建INSERT SQL语句
        String sql = buildInsertSql(tableName, columeList);
        try {
            conn = DriverManager.getConnection(datasourceFroNode.getJdbcUrl(), datasourceFroNode.getJdbcName(), datasourceFroNode.getJdbcPassword());
            updateStatement = conn.prepareStatement(sql);
            conn.setAutoCommit(false);


            // 添加数据
            int count = 0;
            // 批处理大小
            int batchSize = 200;
            for (Map<String, Object> row : dataList) {
                int paramIndex = 1;
                for (String column : columeList) {
                    Object value = row.get(column);
                    updateStatement.setObject(paramIndex++, value);
                }

                // 添加到批处理
                updateStatement.addBatch();
                count++;

                // 定期执行批处理
                if (count % batchSize == 0) {
                    updateStatement.executeBatch();
                    updateStatement.clearBatch();
                }
            }
            log.info("insertData.sql:{}", updateStatement);
            updateStatement.executeUpdate();
            conn.commit();
            msg = YesNoEnum.YES.getName();
        } catch (Exception e) {
            msg = e.getMessage();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (updateStatement != null) {
                    updateStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }


    // 构建INSERT SQL语句
    public static String buildInsertSql(String tableName, List<String> columeList) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");

        // 添加列名,关键词用``包裹，不然会有语法错误
        sql.append(
                columeList.stream()
                        .map(col -> "`" + col + "`")
                        .collect(Collectors.joining(", "))
        );

        sql.append(") VALUES (");

        // 添加占位符
        String placeholders = Collections.nCopies(columeList.size(), "?")
                .stream()
                .collect(Collectors.joining(", "));
        sql.append(placeholders).append(")");

        return sql.toString();
    }

    @Override
    public List<JSONObject> runSql(DataSourceParserInfo datasourceFroNode, String sql) {
        if (StringUtils.isBlank(sql) || null == datasourceFroNode || StringUtils.isBlank(datasourceFroNode.getJdbcUrl()) || StringUtils.isBlank(datasourceFroNode.getJdbcName()) || StringUtils.isBlank(datasourceFroNode.getJdbcPassword())) {
            return Lists.newArrayList();
        }

        List<JSONObject> rowList = new ArrayList<>();
        List<String> columnList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement dataStmt = null;
        try {
            conn = DriverManager.getConnection(datasourceFroNode.getJdbcUrl(), datasourceFroNode.getJdbcName(), datasourceFroNode.getJdbcPassword());
            dataStmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            log.debug("runSql.SQL: {}", sql);
            boolean isResultSet = dataStmt.execute();
            if (isResultSet) {
                // 是查询操作
                try (ResultSet rs = dataStmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        columnList.add(metaData.getColumnName(i));
                    }

                    // 处理分页数据
                    while (rs.next()) {
                        JSONObject row = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            row.put(columnList.get(i - 1), value); // 使用已收集的列名
                        }
                        rowList.add(row);
                    }
                }
            } else {
                // 是更新操作，默认插入静态数据，用于标识操作更新影响的数量
                int updateCount = dataStmt.getUpdateCount();
                for (int i = 0; i < updateCount; i++) {
                    JSONObject data = new JSONObject();
                    data.put(MYSQL_SQL_DEFAULT_DATA, MYSQL_SQL_DEFAULT_DATA);
                    rowList.add(data);
                }
            }

        } catch (SQLException e) {
            log.error("获取数据失败", e);
        } finally {
            // 关闭资源
            try {
                if (dataStmt != null) {
                    dataStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rowList;
    }
}
