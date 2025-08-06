package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.StructureConstant;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.ExcelParserInfo;
import com.wenge.model.entity.StructuredOriginalData;
import com.wenge.model.entity.TableDirectoryInfo;
import com.wenge.model.mapper.ExcelParserInfoMapper;
import com.wenge.model.mapper.TableDirectoryInfoMapper;
import com.wenge.model.mapper.es.KnowledgeStructuredDataMapper;
import com.wenge.model.mapper.es.StructuredOriginalDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.ExcelParserInfoService;
import com.wenge.model.task.ExcelParserTask;
import com.wenge.model.task.ExcelStructureRunTask;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.constants.BaseEsConstants;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryChainWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.index.query.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ExcelParserInfoTableDef.EXCEL_PARSER_INFO;

/**
 * Description: 文件服务实现类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 */
@Service
@Slf4j
public class ExcelParserInfoServiceImpl extends ServiceImpl<ExcelParserInfoMapper, ExcelParserInfo> implements ExcelParserInfoService {

    /*@Autowired
    private JdbcConfigProperties jdbcConfig;*/
    @Autowired
    private ExcelParserInfoMapper excelParserInfoMapper;
    @Autowired
    private TableDirectoryInfoMapper tableDirectoryInfoMapper;
    @Autowired
    private KnowledgeStructuredDataMapper knowledgeStructuredDataMapper;
    @Autowired
    private DenseVectorService denseVectorService;
    @Resource(name = "excelParserPool")
    private ThreadPoolExecutor parserPool;

    @Resource(name = "excelStructurePool")
    private ThreadPoolExecutor structurePool;

    @Autowired
    private WosUtil wosUtil;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private StructuredOriginalDataMapper structuredOriginalDataMapper;

    @Override
    public Result addExcelParserInfo(AddExcelParserInfoParam param) throws IOException, WosException {

        String knowledgeId = param.getKnowledgeId();
        String foldersId = param.getFoldersId();
        MultipartFile file = param.getFile();
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        long fileSize = file.getSize();
        // 不传值默认永久有效
        String validDate = StringUtils.isBlank(param.getValidDate()) ? StructureConstant.PERMANENT_VALID_DATE : param.getValidDate();

        //文件上传minio
        MinioInfoResult minioInfoResult = wosUtil.upload(bucket, "excelParser", file, true);
        if (minioInfoResult == null) {
            return Result.fail("文件上传失败，请重新导入");
        }

        // 写入解析task表
        ExcelParserInfo excelParserInfo = buildExcelParserInfo(foldersId, knowledgeId, fileName, fileSize, minioInfoResult.getUrlPath(), validDate);
        excelParserInfoMapper.insert(excelParserInfo);

        // 返回任务ID给前端
        Map<String, String> result = new HashMap<>();
        String taskId = excelParserInfo.getExcelId();
        result.put("taskId", taskId);

        ExcelParserTask task = null;
        InputStream inputStream = null;
        try {
            inputStream = wosUtil.getInput(bucket, minioInfoResult.getFileStoreKey() + "/" + minioInfoResult.getFileName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            task = ExcelParserTask.builder()
                    .knowledgeId(knowledgeId)
                    .inputStream(inputStream)
                    .excelParserInfo(excelParserInfo)
                    .excelParserInfoMapper(excelParserInfoMapper)
                    .tableDirectoryInfoMapper(tableDirectoryInfoMapper)
                    .websocket(new JSONObject())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        parserPool.submit(task);

        return Result.success(result);
    }

    @Override
    public Result runTask(RunTaskExcelParserInfoParam param) throws ClassNotFoundException {
        // check
        String knowledgeId = param.getKnowledgeId();
        Assert.notNull(knowledgeId, "knowledgeId is not empty!");
        String excelId = param.getExcelId();
        Assert.notNull(excelId, "excelId is not empty!");

        QueryWrapper infoWrapper = new QueryWrapper().and(new QueryColumn("excel_id").eq(excelId));
        ExcelParserInfo parserInfo = excelParserInfoMapper.selectOneByQuery(infoWrapper);
        Integer enableFlag = parserInfo.getEnableFlag();
        Integer parserFlag = parserInfo.getParserFlag();
        if(StructureConstant.NOT_ENABLED.equals(enableFlag)){
            return Result.success("任务未启用,无法进行结构化。请启用后再次进行结构化...");
        }
        if(StructureConstant.PARSER_ING.equals(parserFlag)){
            return Result.success("任务正在结构化,请稍等...");
        }

        QueryWrapper tableDirectoryInfoWrappers = new QueryWrapper().and(new QueryColumn("business_id").eq(excelId));
        TableDirectoryInfo tableDirectoryInfo = tableDirectoryInfoMapper.selectOneByQuery(tableDirectoryInfoWrappers);

        String tableName = tableDirectoryInfo.getTableName();
        String allField = tableDirectoryInfo.getAllField();
        List<TableInfoVo> tableInfoVos = JSON.parseArray(allField, TableInfoVo.class);
        // 取出解析字段
        List<TableInfoVo> parsedTableList = tableInfoVos.stream().filter(tableInfoVo ->
                StructureConstant.FIELD_CHECK.equals(tableInfoVo.getFlag())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(parsedTableList)){
            return Result.success("结构化字段为空,请勾选要结构化的字段。");
        }

        // execute async task
        ExcelStructureRunTask task = ExcelStructureRunTask.builder()
                .excelId(excelId)
                .ids(param.getIds())
                .knowledgeId(knowledgeId)
                .tableName(tableName)
                .parsedTableList(parsedTableList)
                .denseVectorService(denseVectorService)
                .excelParserInfoMapper(excelParserInfoMapper)
                .knowledgeStructuredDataMapper(knowledgeStructuredDataMapper)
                .build();
        structurePool.submit(task);
        return Result.success();
    }

    @Override
    public Result getDetail(String id) {
        ExcelParserInfo excelParserInfo = excelParserInfoMapper.selectOneById(id);
        return Result.success(excelParserInfo);
    }

    @Override
    public Result getExcelTableDataList(ExcelParserTablePageParam param) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper tableDirectoryInfoWrappers = new QueryWrapper()
                .and(new QueryColumn("business_id").eq(param.getExcelId()));
        TableDirectoryInfo tableInfo = tableDirectoryInfoMapper.selectOneByQuery(tableDirectoryInfoWrappers);

        if (null == tableInfo) {
            return Result.success();
        }
        String tableName = tableInfo.getTableName();

        List<TableInfoVo> allTableInfoVos = JSON.parseArray(tableInfo.getAllField(), TableInfoVo.class);

        LambdaEsQueryChainWrapper<StructuredOriginalData> lambdaEsQueryChainWrapper = EsWrappers.lambdaChainQuery(structuredOriginalDataMapper);
        lambdaEsQueryChainWrapper.eq(StructuredOriginalData::getTableId, param.getExcelId());
        EsPageInfo<StructuredOriginalData> page = null;
                lambdaEsQueryChainWrapper.eq(StructuredOriginalData::getTableId, param.getExcelId());
        if (CollectionUtil.isNotEmpty(allTableInfoVos) && StringUtils.isNotEmpty(param.getKeyword())) {
            String[] columnNameList = allTableInfoVos.stream().map(TableInfoVo::getColumnName).toArray(String[]::new);
            lambdaEsQueryChainWrapper.multiMatchQuery(param.getKeyword().trim(), columnNameList);
            lambdaEsQueryChainWrapper.multiMatchQuery(true, param.getKeyword().trim(), Operator.OR, 100, BaseEsConstants.DEFAULT_BOOST, columnNameList);
        }
        page = lambdaEsQueryChainWrapper.page(param.getPageNo(), param.getPageSize());
        PageInfo<StructuredOriginalData> pageInfo = new PageInfo<>(param.getPageNo(), param.getPageSize(), page.getTotal(), page.getList());
        result.put("allTableInfo", allTableInfoVos);
        result.put("tableName", tableName);
        result.put("data", pageInfo);
        return Result.success(result);
    }

    @Override
    public Result enable(EnableParam param) {
        QueryWrapper queryWrapper = Wrappers.init().and(new QueryColumn("excel_id").eq(param.getBusinessId()));
        ExcelParserInfo excelParserInfo = excelParserInfoMapper.selectOneByQuery(queryWrapper);
        excelParserInfo.setEnableFlag(param.getEnableFlag());
        excelParserInfoMapper.update(excelParserInfo);
        return Result.success();
    }

    @Override
    public ExcelParserInfo getExcelParserInfo(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        ExcelParserInfo one = QueryChain.of(mapper)
                .where(EXCEL_PARSER_INFO.EXCEL_ID.eq(id))
                .one();

        return one;
    }

    @Override
    public Result updateValidDate(UpdateStructDataValidDateParam param) {
        QueryWrapper queryWrapper = Wrappers.init().and(new QueryColumn("excel_id").eq(param.getBusinessId()));
        ExcelParserInfo excelParserInfo = excelParserInfoMapper.selectOneByQuery(queryWrapper);
        String validDate = StringUtils.isBlank(param.getValidDate()) ? StructureConstant.PERMANENT_VALID_DATE : param.getValidDate();
        excelParserInfo.setValidDate(validDate);
        excelParserInfoMapper.update(excelParserInfo);
        return Result.success();
    }


    private ExcelParserInfo buildExcelParserInfo(String foldersId, String knowledgeId, String fileName, long fileSize,String fileUrl, String validDate) {
        return ExcelParserInfo.builder()
                .excelId(IdUtil.randomUUID())
                .knowledgeId(knowledgeId)
                .foldersId(foldersId)
                .deleteFlag(0)
                .enableFlag(StructureConstant.ENABLED)
                .parserFlag(StructureConstant.PARSER_BEGIN)
                .createTime(new Date())
                .updateTime(new Date())
                .synchStatus("0")
                .desc(fileName)
                .fileUrl(fileUrl)
                .fileSize(String.valueOf(fileSize))
                .validDate(validDate)
                .build();
    }

}