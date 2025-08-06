package com.wenge.model.task;

import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.StructureConstant;
import com.wenge.model.entity.DataSourceParserInfo;
import com.wenge.model.entity.KnowledgeStructuredData;
import com.wenge.model.mapper.DataSourceParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeStructuredDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.vo.TableInfoVo;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceStructureRunTask implements Callable<Boolean> {

    private String driverClassName;
    private String jdbcUrl;
    private String jdbcName;
    private String jdbcPassword;
    private String tableName;
    private String dataSourceId;
    private String knowledgeId;
    private List<TableInfoVo> parsedTableList;
    private DenseVectorService denseVectorService;
    private DataSourceParserInfoMapper dataSourceParserInfoMapper;
    private KnowledgeStructuredDataMapper knowledgeStructuredDataMapper;

    @Override
    public Boolean call() throws ClassNotFoundException {
        // 标记为开始解析
        updateStatus(dataSourceId, StructureConstant.PARSER_ING);

        Class.forName(driverClassName);
        List<KnowledgeStructuredData> knowledgeStructuredDataList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);
             Statement stmt = conn.createStatement()) {
            // 分页查询的起始页和每页数量
            int pageNumber = 1;
            int pageSize = 100;
            // 循环遍历查询
            while (true) {
                // 构造分页查询语句
                String selectDataSql = String.format("SELECT * FROM %s LIMIT  %d,%d", tableName, (pageNumber - 1) * pageSize, pageSize);
                // 执行查询
                try(ResultSet rs = stmt.executeQuery(selectDataSql)) {
                    // 遍历结果集
                    while (rs.next()) {
                        // 处理每一行数据，获取他的列
                        StringBuilder columnBuilder = new StringBuilder();
                        for (TableInfoVo tableInfoVo : parsedTableList) {
                            String columnData = rs.getString(tableInfoVo.getColumnName());
                            String remarks = tableInfoVo.getRemarks();
                            columnBuilder.append("[").append(remarks).append("]").append(":").append(columnData).append("\n");
                        }
                        // 对columnBuilder进行向量化写入ES
                        knowledgeStructuredDataList.add(buildKnowledgeStructuredData(dataSourceId, knowledgeId, tableName,
                                columnBuilder));
                    }
                    // 如果当前页结果少于pageSize条记录，说明已经是最后一页
                    if (!rs.isLast()) break;
                    // 准备查询下一页
                    pageNumber++;
                }
            }
            // 删除原来的老数据
            delEsOldData(dataSourceId, knowledgeId, tableName);
            // 写入新数据
            knowledgeStructuredDataMapper.insertBatch(knowledgeStructuredDataList);
            // 更新状态为解析成功
            updateStatus(dataSourceId,StructureConstant.PARSER_SUCCESS);
            return true;
        } catch (SQLException e) {
            log.error("解析数据源失败", e);
            // 更新状态为解析失败
            updateStatus(dataSourceId, StructureConstant.PARSER_FAIL);
            return false;
        }
    }

    private void updateStatus(String dataSourceId,Integer status){

        QueryWrapper dataSourceParserInfoWrappers = Wrappers.init()
                .and(new QueryColumn("data_source_id").eq(dataSourceId));
        DataSourceParserInfo dataSourceParserInfo = dataSourceParserInfoMapper.selectOneByQuery(dataSourceParserInfoWrappers);
        dataSourceParserInfo.setParserFlag(status);
        dataSourceParserInfo.setUpdateTime(new Date());
        dataSourceParserInfoMapper.update(dataSourceParserInfo);
    }

    private KnowledgeStructuredData buildKnowledgeStructuredData(String dataSourceId, String knowledgeId, String tableName,
                                                                 StringBuilder columnBuilder) {
        String accountName = AppContextHolder.getAccountName();
        KnowledgeStructuredData structuredData = KnowledgeStructuredData.builder()
                .knowledgeId(knowledgeId)
                .businessId(dataSourceId)
                .tableName(tableName)
                .createTime(DateUtil.format(new Date(), DateUtil.PATTERN_1))
                .updateTime(DateUtil.format(new Date(), DateUtil.PATTERN_1))
                .createUser(accountName)
                .updateUser(accountName)
                .content(columnBuilder.toString())
                .id(IdUtil.randomUUID())
                .type(1)
                .build();
        List<Double> doubles = denseVectorService.modelEncode(columnBuilder.toString(), knowledgeId);
        if (doubles.size() == 1024) {
            structuredData.setContentDense1024(doubles);
        } else {
            structuredData.setContentDense(doubles);
        }
        return structuredData;
    }

    private void delEsOldData(String dataSourceId, String knowledgeId, String tableName) {
        LambdaEsQueryWrapper<KnowledgeStructuredData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeStructuredData.class)
                .eq(KnowledgeStructuredData::getKnowledgeId, knowledgeId)
                .eq(KnowledgeStructuredData::getBusinessId, dataSourceId)
                .eq(KnowledgeStructuredData::getTableName, tableName);
        knowledgeStructuredDataMapper.delete(esQueryWrapper);
    }
}
