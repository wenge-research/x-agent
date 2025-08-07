package com.wenge.model.strategy.dataSourceParser;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.DataSourceParserInfo;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import org.apache.commons.compress.utils.Lists;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DataSource{
    List<String> getAllTables(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword);
    Map<String,Object> getAllDataFromTable(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword,
                                           String tableName,Integer pageNum, Integer pageSize);
    Map<String,Object> getAllDataFromTable(String sourceJdbcUrl, String sourceJdbcName, String sourceJdbcPassword,
                                           String tableName,Integer pageNum, Integer pageSize,String bussiness,MyDatabaseOperations myDatabaseOperations) throws SQLException;

    /**
     * 插入表数据
     * @param datasourceFroNode
     * @param tableName
     * @param columeList
     * @param dataList
     * @return
     */
    default String insertData(DataSourceParserInfo datasourceFroNode,
                              String tableName, List<String> columeList, List<Map<String, Object>> dataList) {
        return YesNoEnum.YES.getName();
    };

    /**
     * 执行 sql
     * @param datasourceFroNode
     * @param sql
     * @return
     */
    default List<JSONObject> runSql(DataSourceParserInfo datasourceFroNode, String sql) {
        return Lists.newArrayList();
    };

}
