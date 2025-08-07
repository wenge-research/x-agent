package com.wenge.model.task;

import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.StructureConstant;
import com.wenge.model.entity.ExcelParserInfo;
import com.wenge.model.entity.KnowledgeStructuredData;
import com.wenge.model.entity.StructuredOriginalData;
import com.wenge.model.mapper.ExcelParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeStructuredDataMapper;
import com.wenge.model.mapper.es.StructuredOriginalDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 把同步到MYSQL中的EXCEL数据进行向量化，数据在MYSQL中无需多数据源适配
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelStructureRunTask implements Callable<Boolean> {

    private String tableName;
    private List<String> ids;
    private String excelId;
    private String knowledgeId;
    private List<TableInfoVo> parsedTableList;
    private DenseVectorService denseVectorService;
    private ExcelParserInfoMapper excelParserInfoMapper;
    private KnowledgeStructuredDataMapper knowledgeStructuredDataMapper;

    @Override
    public Boolean call(){

        ApplicationContext context = CoreContextProvider.getContext();
        StructuredOriginalDataMapper originalDataMapper = context.getBean(StructuredOriginalDataMapper.class);
        int pageNo = 1;
        int size = 100;
        EsPageInfo<StructuredOriginalData> page;
        delEsOldData(knowledgeId, excelId, tableName);
        List<StructuredOriginalData> structuredOriginalData;
        if (CollectionUtils.isNotEmpty(ids)) {
            structuredOriginalData = originalDataMapper.selectBatchIds(ids);
            saveData(structuredOriginalData);
        } else {
            while (true) {
                page = EsWrappers.lambdaChainQuery(originalDataMapper)
                        .eq(StructuredOriginalData::getTableId, excelId)
                        .page(pageNo, size);
                structuredOriginalData = page.getList();
                if (CollectionUtils.isEmpty(structuredOriginalData)) {
                    break;
                }
                saveData(structuredOriginalData);
                pageNo++;
            }
        }
        // 更新状态为成功
        updateStatus(excelId,StructureConstant.PARSER_SUCCESS);
        return true;
    }

    /**
     * 更新状态
     * @param excelId
     * @param status
     */
    private void updateStatus(String excelId,Integer status){

        QueryWrapper queryWrapper = Wrappers.init()
                .and(new QueryColumn("excel_id").eq(excelId));
        ExcelParserInfo excelParserInfo = excelParserInfoMapper.selectOneByQuery(queryWrapper);
        excelParserInfo.setParserFlag(status);
        excelParserInfo.setUpdateTime(new Date());
        excelParserInfoMapper.update(excelParserInfo);
    }

    private void delEsOldData(String knowledgeId, String excelId, String tableName) {
        LambdaEsQueryWrapper<KnowledgeStructuredData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeStructuredData.class)
                .eq(KnowledgeStructuredData::getKnowledgeId, knowledgeId)
                .eq(KnowledgeStructuredData::getBusinessId, excelId)
                .eq(KnowledgeStructuredData::getTableName, tableName);
        knowledgeStructuredDataMapper.delete(esQueryWrapper);
    }

    private KnowledgeStructuredData buildKnowledgeStructuredData(String knowledgeId, String excelId,String tableName,
                                                                 StringBuilder columnBuilder) {
        KnowledgeStructuredData structuredData = KnowledgeStructuredData.builder()
                .knowledgeId(knowledgeId)
                .content(columnBuilder.toString())
                .createTime(DateUtil.format(new Date(), DateUtil.PATTERN_1))
                .updateTime(DateUtil.format(new Date(), DateUtil.PATTERN_1))
                //.contentDense(knowledgeDataService.modelEncode(columnBuilder.toString()))
                .businessId(excelId)
                .tableName(tableName)
                .id(IdUtil.randomUUID())
                .type(2)
                .build();
        List<Double> doubles = denseVectorService.modelEncode(columnBuilder.toString(), knowledgeId);
        if (doubles.size() == 1024) {
            structuredData.setContentDense1024(doubles);
        } else {
            structuredData.setContentDense(doubles);
        }
        return structuredData;
    }

    /**
     * 保存数据
     *
     * @param structuredOriginalDataList
     */
    private void saveData(List<StructuredOriginalData> structuredOriginalDataList) {
        List<KnowledgeStructuredData> toSaveList = structuredOriginalDataList.stream().map(p -> {
            StringBuilder columnBuilder = new StringBuilder();
            for (TableInfoVo tableInfoVo : parsedTableList) {
                p.forEach((k, v) -> {
                    if (k.equals(tableInfoVo.getColumnName())) {
                        columnBuilder.append("[").append(tableInfoVo.getRemarks()).append("]").append(":").append(v).append("\n");
                    }
                });
            }
            return buildKnowledgeStructuredData(knowledgeId, excelId, tableName, columnBuilder);
        }).collect(Collectors.toList());
        knowledgeStructuredDataMapper.insertBatch(toSaveList);
    }
}
