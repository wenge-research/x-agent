package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.StructureConstant;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.KnowledgeInoDataNumResult;
import com.wenge.model.entity.*;
import com.wenge.model.mapper.DataSourceParserInfoMapper;
import com.wenge.model.mapper.ExcelParserInfoMapper;
import com.wenge.model.mapper.TableDirectoryInfoMapper;
import com.wenge.model.mapper.es.KnowledgeStructuredDataMapper;
import com.wenge.model.service.DataSourceParserInfoService;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.strategy.dataSourceParser.DataSource;
import com.wenge.model.strategy.dataSourceParser.DataSourceFactory;
import com.wenge.model.strategy.dataSourceParser.MyDatabaseOperations;
import com.wenge.model.task.DataSourceStructureRunTask;
import com.wenge.model.utils.JdbcUtils;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.utils.WenShuExeclPost;
import com.wenge.model.vo.SqlSourceVo;
import com.wenge.model.vo.StructureParserVo;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.exception.GlobalException;
import com.wg.appframe.mybatisflex.core.Wrappers;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.DataSourceParserInfoTableDef.DATA_SOURCE_PARSER_INFO;
import static com.wenge.model.entity.table.ExcelParserInfoTableDef.EXCEL_PARSER_INFO;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.TableDirectoryInfoTableDef.TABLE_DIRECTORY_INFO;


@Service
@Slf4j
public class DataSourceParserInfoServiceImpl extends ServiceImpl<DataSourceParserInfoMapper, DataSourceParserInfo> implements DataSourceParserInfoService {

    @Autowired
    private DataSourceParserInfoMapper dataSourceParserInfoMapper;
    @Autowired
    private TableDirectoryInfoMapper tableDirectoryInfoMapper;
    @Autowired
    private KnowledgeStructuredDataMapper knowledgeStructuredDataMapper;
    @Autowired
    private ExcelParserInfoMapper excelParserInfoMapper;
    @Autowired
    private DenseVectorService denseVectorService;

    @Resource(name = "dataSourceStructurePool")
    private ThreadPoolExecutor structurePool;

    @Value("${mybatis-flex.datasource.myds.driver-class-name}")
    private String driver;

    @Value("${mybatis-flex.datasource.myds.url}")
    private String url;

    @Value("${mybatis-flex.datasource.myds.username}")
    private String username;

    @Value("${mybatis-flex.datasource.myds.password}")
    private String password;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FoldersService foldersService;
    @Autowired
    private MyDatabaseOperations myDatabaseOperations;
    @Override
    public Result<PageInfo<StructureParserVo>> unionData(UnionParam param) {
        String knowledgeId = param.getKnowledgeId();
        List<Folders> allFolders = new ArrayList<>();
        if (StringUtils.isNotBlank(knowledgeId)) {
            String foldersId = param.getFoldersId();
            if (StringUtils.isNotBlank(foldersId)) {
                allFolders = Folders.creat()
                        .where(FOLDERS.KNOWLEDGE_ID.eq(knowledgeId))
                        .lists();
                allFolders = foldersService.getTreeByFolderId(foldersId, allFolders);
            }
        }

        List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());

        List<StructureParserVo> unionData = dataSourceParserInfoMapper.unionData(param.getPageNo() - 1, param.getPageSize(), param, foldersIdList);
        if (!CollectionUtils.isEmpty(unionData)) {
            unionData.forEach(item -> item.setDesc(item.getDescription()));
        }
        Integer count = dataSourceParserInfoMapper.unionDataCount(param.getDesc(), param, foldersIdList);
        PageInfo<StructureParserVo> pageInfo = new PageInfo<>(param.getPageNo(), param.getPageSize(), count, unionData);
        return Result.success(pageInfo);
    }

    @Override
    public Result updateSynchStatusUnionData(UnionParam param) {
        if (StringUtils.isBlank(param.getBusinessId())) {
            return Result.fail("业务ID不能为空");
        }
        if (StringUtils.isBlank(param.getKnowledgeId())) {
            return Result.fail("知识库ID不能为空");
        }
        QueryWrapper unionDataWrapper = Wrappers.init().and(EXCEL_PARSER_INFO.EXCEL_ID.eq(param.getBusinessId())).and(EXCEL_PARSER_INFO.KNOWLEDGE_ID.eq(param.getKnowledgeId())).limit(1);
        final List<ExcelParserInfo> excelParserInfos = excelParserInfoMapper.selectListByQuery(unionDataWrapper);
        if (CollectionUtils.isEmpty(excelParserInfos)) {
            return Result.fail("不存在当前文件记录");
        }
//        问数文件同步大模型分析处理 0-未同步 2-同步中 3-同步完成 4-同步异常（重新同步）
        ExcelParserInfo excelParserInfo = excelParserInfos.get(0);
        if ("2".equals(excelParserInfo.getSynchStatus())) {
            return Result.fail("同步中，请稍等");
        }
        if ("3".equals(excelParserInfo.getSynchStatus())) {
            return Result.fail("请勿重复同步");
        }
        excelParserInfo.setSynchStatus("2");
        excelParserInfoMapper.update(excelParserInfo);

        //这里调用 异步调用杨齐提供的接口
//        new Thread() {
//            @Override
//            public void run() {
        ExcelParserInfo excelParserInfoThread = excelParserInfo;
        try {
            excelParserInfoThread.setSynchStatus("2");
            excelParserInfoMapper.update(excelParserInfo);

            JSONObject jsonObject = WenShuExeclPost.addExcel(excelParserInfoThread.getFileUrl(), excelParserInfoThread.getExcelId(), excelParserInfoThread.getKnowledgeId());
//                    log.info("问数execl文件同步 状态：{}", jsonObject);
            String status = (String) jsonObject.get("status");
            if ("success".equals(status)) {
                excelParserInfoThread.setSynchStatus("3");
                return Result.success("同步中，刷新列表可查看同步结果");
            } else {
                log.warn("#### 请核实一下 ### {}", jsonObject);
                excelParserInfoThread.setSynchStatus("3");
                return Result.fail("表名称重复，请核对一下");
            }
        } catch (Exception e) {
            e.printStackTrace();
            excelParserInfoThread.setSynchStatus("4");
        } finally {
            excelParserInfoMapper.update(excelParserInfoThread);
        }
//            }
//        }.start();
        return Result.success("同步中，刷新列表可查看同步结果");
    }


    @Override
    public Result getTableList(String businessId) {
        QueryWrapper tableDirectoryWrapper = Wrappers.init().and(TABLE_DIRECTORY_INFO.BUSINESS_ID.eq(businessId));
        List<TableDirectoryInfo> infoList = tableDirectoryInfoMapper.selectListByQuery(tableDirectoryWrapper);
        List<String> tableList = infoList.stream().map(TableDirectoryInfo::getTableName).collect(Collectors.toList());
        return Result.success(tableList);
    }

    @Override
    public Result getDataSourceDataList(DataSourceTableDataPageParam param) throws SQLException {
        final String dataSourceId = param.getDataSourceId();
        final String tableName = param.getTableName();
        Assert.notNull(dataSourceId, "dataSourceId不能为空");

        // 获取数据源信息
        QueryWrapper dataSourceWrapper = Wrappers.init().and(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.eq(dataSourceId));
        DataSourceParserInfo dataSourceParserInfo = dataSourceParserInfoMapper.selectOneByQuery(dataSourceWrapper);
        Assert.notNull(dataSourceParserInfo, "数据源不存在");

        Integer pageNo = param.getPageNo();
        Integer pageSize = param.getPageSize();
        final Integer dsType = dataSourceParserInfo.getDsType();
        StringBuilder jdbc=null;
        jdbc= new StringBuilder(jdbc(dsType));
        final String jdbcUrl =jdbc.append(dataSourceParserInfo.getJdbcUrl()).append(":").append(dataSourceParserInfo.getPort()).append("/").append(dataSourceParserInfo.getDesc()).toString();
        final String jdbcName = dataSourceParserInfo.getJdbcName();
        final String jdbcPassword = dataSourceParserInfo.getJdbcPassword();

        DataSource dataSource = dataSourceFactory.getDataSource(dsType);
        Map<String, Object> result = dataSource.getAllDataFromTable(jdbcUrl, jdbcName, jdbcPassword, tableName, pageNo, pageSize,param.getBussiness(),myDatabaseOperations);

        return Result.success(result);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result addDataSourceParserInfo(AddDataSourceParserInfoParam param) {
        // 写入解析task表，这里是库层面的，一个库对应一个数据
        DataSourceParserInfo dataSourceParserInfo = buildDataSourceParserInfo(param);
        dataSourceParserInfo.setDsType(param.getDsType());
        dataSourceParserInfoMapper.insert(dataSourceParserInfo);
        String driverType = jdbcdriverType(param.getDsType());
        SqlSourceVo sqlSource = parseJdbcUrl(param);
        String knowledgeId = param.getKnowledgeId();
        StringBuilder jdbcurl=new StringBuilder();
        jdbcurl.append(sqlSource.getPrefix()).append(sqlSource.getHost()).append(":").append(sqlSource.getPort()).append("/").append(sqlSource.getDatabase());
        // 读取源端数据，写入本地数据库，建表写数据
        //final String sourceJdbcUrl = param.getJdbcUrl();
        final String sourceJdbcUrl = String.valueOf(jdbcurl);
        final String sourceJdbcName = param.getJdbcName();
        final String sourceJdbcPassword = param.getJdbcPassword();
        // 获取源端数据库下面的所有表
        DataSource dataSource = dataSourceFactory.getDataSource(dataSourceParserInfo.getDsType());
        List<String> sourceTableList = Lists.newArrayList();
        try {
            sourceTableList = dataSource.getAllTables(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword);
        } catch (GlobalException e) {
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(e.getMessage());
        }

        // 写配置表，每一个配置其实就是一个任务
        List<TableDirectoryInfo> tableDirectoryInfos = new ArrayList<>();
        List<ExcelInfoData> excelInfoDataList = new ArrayList<>();
        // 遍历每张表
        for (String sourceTableName : sourceTableList) {
            // 获取该表的所有字段信息封装为map
            Map<String, TableInfoVo> columnsInfoMap = JdbcUtils.getTableColumnsInfo(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword, sourceTableName,driverType);
            List<TableInfoVo> allFieldList = new ArrayList<>();
            // 取出每一列对应的信息，名字，类型等
            columnsInfoMap.forEach((columnName, tableInfo) -> allFieldList.add(tableInfo));
            // 每个表是一个配置，以后同步不同步，开启不开启解析都是这里的层次处理的，这里是表层面的
            TableDirectoryInfo directoryInfo = buildTableDirectoryInfo(sourceTableName, allFieldList, dataSourceParserInfo);
            tableDirectoryInfos.add(directoryInfo);

            // 获取该表的一条记录
            Map<String, Object> singleRecord = JdbcUtils.getSingleRecord(sourceJdbcUrl, sourceJdbcName, sourceJdbcPassword, sourceTableName,driverType);
            // 构建表结构信息
            List<Map<String, Object>> fieldList = buildFieldList(allFieldList);
            // 构建每个表是的excelInfo信息
            Map<String, Object> excelInfo = buildExcelInfo(sourceTableName, fieldList, singleRecord);
            // 构建excelInfoData
            ExcelInfoData excelInfoData = buildExcelInfoData(sqlSource, sourceTableName, knowledgeId, excelInfo);
            excelInfoDataList.add(excelInfoData);

        }

        tableDirectoryInfoMapper.insertBatch(tableDirectoryInfos);
        // 批量插入数据到excel_info_data_sz表
        int result = JdbcUtils.batchInsertExcelData(excelInfoDataList,driverType,myDatabaseOperations);
        log.info("成功插入{}条数据到excel_info_data_sz表", result);
        return Result.success();

    }


    @Override
    public Result runTask(RunTaskDataSourceParserInfoParam param) throws ClassNotFoundException {
        String dataSourceId = param.getDataSourceId();
        Assert.notNull(dataSourceId, "dataSourceId can not empty!");

        String knowledgeId = param.getKnowledgeId();
        Assert.notNull(knowledgeId, "knowledgeId can not empty!");
        String tableName = param.getTableName();
        Assert.notNull(tableName, "tableName can not empty!");
        Map<String, Object> parserInfoWrapper = new HashMap<>();
        parserInfoWrapper.put("data_source_id", dataSourceId);
        DataSourceParserInfo parserInfo = dataSourceParserInfoMapper.selectOneByMap(parserInfoWrapper);
        Integer enableFlag = parserInfo.getEnableFlag();
        Integer parserFlag = parserInfo.getParserFlag();
        if (StructureConstant.NOT_ENABLED.equals(enableFlag)) {
            return Result.success("任务未启用,无法进行结构化。请启用后再次进行结构化...");
        }
        if (StructureConstant.PARSER_ING.equals(parserFlag)) {
            return Result.success("任务正在结构化,请稍等...");
        }

        Map<String, Object> tableWrapper = new HashMap<>();
        tableWrapper.put("business_id", dataSourceId);
        tableWrapper.put("table_name", tableName);
        TableDirectoryInfo directoryInfo = tableDirectoryInfoMapper.selectOneByMap(tableWrapper);

        // 取出所有字段
        List<TableInfoVo> tableInfoVos = JSON.parseArray(directoryInfo.getAllField(), TableInfoVo.class);
        if (CollectionUtils.isEmpty(tableInfoVos)) {
            return Result.success("未查询到可结构化的表字段");
        }
        // 取出解析字段
        List<TableInfoVo> parsedTableList = tableInfoVos.stream().filter(tableInfoVo ->
                StructureConstant.FIELD_CHECK.equals(tableInfoVo.getFlag())).collect(Collectors.toList());
        // 未选任何结构化字段时，默认全部字段进行结构化
        if (CollectionUtils.isEmpty(parsedTableList)) {
//            return Result.success("结构化字段为空,请勾选要结构化的字段。");
            parsedTableList = tableInfoVos;
        }
        String driverType=jdbcdriverType(parserInfo.getDsType());
        String jdbc= jdbc(parserInfo.getDsType());
        StringBuilder jdbcUrl = new StringBuilder(jdbc).append(parserInfo.getJdbcUrl()).append(":").append(parserInfo.getPort()).append("/").append(parserInfo.getDesc());
        // 异步处理，提交线程池任务
        DataSourceStructureRunTask task = DataSourceStructureRunTask.builder()
                .dataSourceId(dataSourceId)
                .knowledgeId(knowledgeId)
                .driverClassName(driverType)
                .jdbcUrl(String.valueOf(jdbcUrl))
                .jdbcName(parserInfo.getJdbcName())
                .jdbcPassword(parserInfo.getJdbcPassword())
                .tableName(directoryInfo.getTableName())
                .parsedTableList(parsedTableList)
                .denseVectorService(denseVectorService)
                .dataSourceParserInfoMapper(dataSourceParserInfoMapper)
                .knowledgeStructuredDataMapper(knowledgeStructuredDataMapper)
                .build();
        structurePool.submit(task);
        return Result.success();
    }

    @Override
    public Result getDetail(String id) {
        QueryWrapper queryWrapper = Wrappers.init().and(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.eq(id));
        DataSourceParserInfo dataSourceParserInfo = mapper.selectOneByQuery(queryWrapper);
        return Result.success(dataSourceParserInfo);
    }

    @Override
    public Result del(StrutsDelParam param) {
        List<StrutsDelItem> strutsDelItems = param.getStrutsDelItems();
        if (CollectionUtils.isEmpty(strutsDelItems)) {
            return Result.fail();
        }
        Map<Integer, List<StrutsDelItem>> delItemMap = strutsDelItems.stream().collect(Collectors.groupingBy(StrutsDelItem::getType));

        List<StrutsDelItem> dataSourceList = delItemMap.get(StructureConstant.DATA_SOURCE);
        List<StrutsDelItem> excelList = delItemMap.get(StructureConstant.EXCEL);
        List<String> businessIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dataSourceList)) {
            List<String> dataSourceIds = dataSourceList.stream().map(StrutsDelItem::getBusinessId).collect(Collectors.toList());
            businessIds.addAll(dataSourceIds);
            QueryWrapper queryWrapper = Wrappers.init().and(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.in(dataSourceIds));
            // 删除任务表
            dataSourceParserInfoMapper.deleteByQuery(queryWrapper);
        }
        if (!CollectionUtils.isEmpty(excelList)) {
            List<String> excelIds = excelList.stream().map(StrutsDelItem::getBusinessId).collect(Collectors.toList());
            businessIds.addAll(excelIds);
            QueryWrapper queryWrapper = Wrappers.init().and(EXCEL_PARSER_INFO.EXCEL_ID.in(excelIds));
            // 删除任务表
            excelParserInfoMapper.deleteByQuery(queryWrapper);

            // 删除excel同步的表
            deleteExcelTable(businessIds);
        }
        if (CollectionUtils.isEmpty(businessIds)) {
            return Result.success();
        }

        QueryWrapper queryWrapper = Wrappers.init().and(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.in(businessIds));
        // 删除任务表
        dataSourceParserInfoMapper.deleteByQuery(queryWrapper);

        // 删除配置表
        Wrappers<Object> configWrapper = Wrappers.init().and(TABLE_DIRECTORY_INFO.BUSINESS_ID.in(businessIds));
        tableDirectoryInfoMapper.deleteByQuery(configWrapper);

        // 删除ES数据
        LambdaEsQueryWrapper<KnowledgeStructuredData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeStructuredData.class)
                .in(KnowledgeStructuredData::getBusinessId, businessIds);
        knowledgeStructuredDataMapper.delete(esQueryWrapper);

        return Result.success();
    }

    @Override
    public Result enable(EnableParam param) {
        String businessId = param.getBusinessId();
        Assert.notNull(businessId, "业务ID不能为空");
        QueryWrapper queryWrapper = Wrappers.init().and(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.eq(businessId));
        DataSourceParserInfo dataSourceParserInfo = mapper.selectOneByQuery(queryWrapper);
        if (Objects.isNull(dataSourceParserInfo)) {
            return Result.fail("未找到该数据");
        }
        dataSourceParserInfo.setEnableFlag(param.getEnableFlag());
        mapper.update(dataSourceParserInfo);
        return Result.success();
    }

    @Override
    public Result updateValidDate(UpdateStructDataValidDateParam param) {
        String businessId = param.getBusinessId();
        Assert.notNull(businessId, "业务ID不能为空");
        QueryWrapper queryWrapper = Wrappers.init().and(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.eq(businessId));
        DataSourceParserInfo dataSourceParserInfo = mapper.selectOneByQuery(queryWrapper);
        if (Objects.isNull(dataSourceParserInfo)) {
            return Result.fail("未找到该数据");
        }
        String validDate = StringUtils.isBlank(param.getValidDate()) ? StructureConstant.PERMANENT_VALID_DATE : param.getValidDate();
        dataSourceParserInfo.setValidDate(validDate);
        mapper.update(dataSourceParserInfo);
        return Result.success();
    }

    @Override
    public DataSourceParserInfo getDatasourceFroNode(String datasourceId) {
        if (StringUtils.isBlank(datasourceId)) {
            return null;
        }
        return Wrappers.of(mapper)
                .where(DATA_SOURCE_PARSER_INFO.DATA_SOURCE_ID.eq(datasourceId))
                .limit(1)
                .one();
    }

    @Override
    public Result strutsList(StrutsResultPageParam param) throws IOException {

        LambdaEsQueryWrapper<KnowledgeStructuredData> wrapper = EsWrappers.lambdaQuery(KnowledgeStructuredData.class)
                .eq(StrUtil.isNotBlank(param.getBusinessId()), KnowledgeStructuredData::getBusinessId, param.getBusinessId())
                .matchPhrasePrefixQuery(StrUtil.isNotBlank(param.getTableName()), KnowledgeStructuredData::getTableName, param.getTableName());

        EsPageInfo<KnowledgeStructuredData> structuredDataEsPageInfo = knowledgeStructuredDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        return Result.success(structuredDataEsPageInfo);
    }

    private void deleteExcelTable(List<String> businessIds) {
        Wrappers<Object> configWrapper = Wrappers.init().and(TABLE_DIRECTORY_INFO.BUSINESS_ID.in(businessIds));
        List<TableDirectoryInfo> infos = tableDirectoryInfoMapper.selectListByQuery(configWrapper);
        for (TableDirectoryInfo info : infos) {
            String tableName = info.getTableName();
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 Statement stmt = conn.createStatement()) {
                // 注册JDBC驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                String sql = "DROP TABLE IF EXISTS " + tableName;
                stmt.executeUpdate(sql);
                System.out.println("表已删除");
            } catch (ClassNotFoundException e) {
                System.out.println("JDBC驱动未找到");
            } catch (SQLException e) {
                System.out.println("SQL错误: " + e.getMessage());
            }
        }
    }

    public static DataSourceParserInfo buildDataSourceParserInfo(AddDataSourceParserInfoParam param) {
        DataSourceParserInfo dataSourceParserInfo = new DataSourceParserInfo();
        BeanUtils.copyProperties(param, dataSourceParserInfo);
        dataSourceParserInfo.setParserFlag(StructureConstant.PARSER_BEGIN);
        dataSourceParserInfo.setEnableFlag(StructureConstant.ENABLED);
        dataSourceParserInfo.setCreateTime(new Date());
        dataSourceParserInfo.setUpdateTime(new Date());
        dataSourceParserInfo.setDataSourceId(IdUtil.randomUUID());
        // 设置有效时间
        String validDate = StringUtils.isBlank(param.getValidDate()) ? StructureConstant.PERMANENT_VALID_DATE : param.getValidDate();
        dataSourceParserInfo.setValidDate(validDate);
        return dataSourceParserInfo;
    }

    public static TableDirectoryInfo buildTableDirectoryInfo(String sourceTableName, List<TableInfoVo> allFieldList, DataSourceParserInfo dataSourceParserInfo) {
        TableDirectoryInfo directoryInfo = TableDirectoryInfo.builder()
                .tableName(sourceTableName)
                .allField(JSONObject.toJSONString(allFieldList))
                .businessId(dataSourceParserInfo.getDataSourceId())
                .type(StructureConstant.DATA_SOURCE)
                .parserFlag(StructureConstant.PARSER_BEGIN)
                .build();
        return directoryInfo;
    }


    private static ExcelInfoData buildExcelInfoData(SqlSourceVo sqlSource, String sourceTableName, String knowledgeId, Map<String, Object> excelInfo) {

        ExcelInfoData excelInfoData = ExcelInfoData.builder()
                .SqlSource(JSONObject.toJSONString(sqlSource))
                .tableName(sourceTableName)
                .knowledgeId(knowledgeId)
                .excelInfo(JSONObject.toJSONString(excelInfo))
                .build();

        return excelInfoData;
    }

    private List<Map<String, Object>> buildFieldList(List<TableInfoVo> allFieldList) {
        List<Map<String, Object>> fieldList = new ArrayList<>();
        for (TableInfoVo tableInfoVo : allFieldList) {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("字段名称", tableInfoVo.getColumnName());
            fieldMap.put("字段类型", tableInfoVo.getColumnType());
            fieldMap.put("字段注释", tableInfoVo.getRemarks());
            fieldList.add(fieldMap);
        }

        return fieldList;
    }

    private Map<String, Object> buildExcelInfo(String sourceTableName, List<Map<String, Object>> allFieldListSz, Map<String, Object> singleRecord) {
        Map<String, Object> map = new HashMap<>();
        map.put("sql表名", sourceTableName);
        map.put("sql表字段名称、字段类型和字段注释信息", allFieldListSz);
        map.put("数据示例如下", singleRecord);
        return map;
    }

    public static SqlSourceVo parseJdbcUrl(AddDataSourceParserInfoParam param) {

        String host = param.getJdbcUrl();//ip
        String user = param.getJdbcName();//用户名
        String password = param.getJdbcPassword();//密码
        String database=param.getDesc();
        String port=param.getPort();
        String prefix=null;
        prefix=jdbc(param.getDsType());
        /*// 去除参数部分（如果有）
        String cleanUrl = jdbcUrl.split("\\?")[0];

        // 分割协议和剩余部分
        String[] parts = cleanUrl.split("//");
        if (parts.length < 2) {
            throw new IllegalArgumentException("无效的JDBC URL，格式应为：jdbc:mysql://host:port/database");
        }


        // 分割主机端口和数据库
        String[] hostPortDb = parts[1].split("/", 2);
        if (hostPortDb.length < 2) {
            throw new IllegalArgumentException("缺少数据库名称，正确格式示例：jdbc:mysql://localhost:3306/database");
        }


        // 分割主机和端口
        String[] hostPort = hostPortDb[0].split(":");
        String host = hostPort[0];
        String port = hostPort.length > 1 ? hostPort[1] : "3306"; // 默认端口
        String database = hostPortDb[1];
        */
        return SqlSourceVo.builder()
                .host(host)
                .port(port)
                .user(user)
                .password(password)
                .database(database)
                .prefix(prefix)
                .build();

    }
    public static String jdbc(Integer type){
        String prefix=null;
        if(type.equals(1)){
            prefix="jdbc:mysql://";
        }
        if(type.equals(2)){
            prefix="jdbc:dm://";
        }
        if(type.equals(3)){
            prefix="jdbc:yasdb://";
        }
        return prefix;
    }
    public static String jdbcdriverType(Integer type){
        String driverType=null;
        if(type.equals(1)){
            driverType="com.mysql.cj.jdbc.Driver";
        }
        if(type.equals(2)){
            driverType="dm.jdbc.driver.DmDriver";
        }
        if(type.equals(3)){
            driverType="com.yashandb.jdbc.Driver";
        }
        return driverType;
    }

    @Override
    public Map<String, Long> getDataSourceParserInfoNum() {

        List<KnowledgeInoDataNumResult> dataNumResults =  dataSourceParserInfoMapper.getDataSourceParserInfoNum();
        return  dataNumResults.stream().collect(Collectors.toMap(KnowledgeInoDataNumResult::getKnowledgeId, KnowledgeInoDataNumResult::getDataNum));
    }
}
