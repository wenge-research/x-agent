package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.CollectField;
import com.wenge.model.dto.result.CollectFileTable;
import com.wenge.model.entity.CollectBaseConfig;
import com.wenge.model.entity.DataSourceParserInfo;
import com.wenge.model.entity.ExcelInfoData;
import com.wenge.model.entity.TableDirectoryInfo;
import com.wenge.model.mapper.CollectBaseConfigMapper;
import com.wenge.model.mapper.ExcelInfoDataMapper;
import com.wenge.model.mapper.TableDirectoryInfoMapper;
import com.wenge.model.service.CollectBaseConfigService;
import com.wenge.model.strategy.dataSourceParser.DataSource;
import com.wenge.model.strategy.dataSourceParser.DataSourceFactory;
import com.wenge.model.utils.JdbcUtils;
import com.wenge.model.vo.SqlSourceVo;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.CollectBaseConfigTableDef.COLLECT_BASE_CONFIG;
import static com.wenge.model.entity.table.ExcelInfoDataTableDef.EXCEL_INFO_DATA;
import static com.wenge.model.entity.table.TableDirectoryInfoTableDef.TABLE_DIRECTORY_INFO;


/**
 * Description: 数据集的表配置服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 20:14:11
 *
 */
@Service
@Slf4j
public class CollectBaseConfigServiceImpl extends ServiceImpl<CollectBaseConfigMapper, CollectBaseConfig> implements CollectBaseConfigService {

	private static final String AES_KEY = "yuijkoiu45ghnhyd";

	private static final String FILE_ID = "F_ID";
	private static final String COLLECT_DABABASE = "smart_customer_agent_collect";

	/**
	 * 	数据集的表配置数据库处理类
	 */
	@Autowired
	private CollectBaseConfigMapper collectBaseConfigMapper;

	@Autowired
	private WosUtil wosUtil;

	@Value("${appframe.minio.bucket}")
	private String bucket;

	@Value("${spring.profiles.active:active}")
	private String profiles;

	@Value("${mybatis-flex.datasource.myds.password}")
	private String mybatiskey;

	@Value("${mybatis-flex.datasource.myds.username}")
	private String username;
	@Value("${mybatis-flex.datasource.myds.url}")
	private String url;

	@Autowired
	private ExcelInfoDataMapper excelInfoDataMapper;

	@Autowired
	private DataSourceFactory dataSourceFactory;

	@Autowired
	private TableDirectoryInfoMapper tableDirectoryInfoMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result addCollectBaseConfig(CollectConfigAddParam param){
//		if (StringUtils.isBlank(param.getConfigName())) {
//			return Result.fail("数据库名称不能为空");
//		}
		if (CollectionUtil.isEmpty(param.getConfigNames())) {
			return Result.fail("数据库名称不能为空");
		}
		if (StringUtils.isBlank(param.getCollectId())) {
			return Result.fail("数据集ID不能为空");
		}

		List<CollectBaseConfig> collectBaseConfigs = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_NAME.in(param.getConfigNames()))
				.and(COLLECT_BASE_CONFIG.COLLECT_ID.eq(param.getCollectId()))
				.and(COLLECT_BASE_CONFIG.CONFIG_ID.ne(param.getConfigId()).when(StringUtils.isNotBlank(param.getConfigId())))
				.list();
		if (CollectionUtil.isNotEmpty(collectBaseConfigs)) {
			return Result.fail("名称: " + collectBaseConfigs.get(0).getConfigName() + "已存在");
		}

		// 验证参数
		List<MultipartFile> files = param.getFiles();
		List<String> fileNames = param.getConfigNames();
		if ("file".equals(param.getType()) && CollectionUtil.isEmpty(files)) {
			return Result.fail("请选择文件");
		} else if ("db".equals(param.getType()) && null == param.getDbConfig()) {
			return Result.fail("请填写数据库配置");
		}
		Map<String, String> resultMap = Maps.newHashMap();
		boolean isError = false;
		String errorMsg = StringUtils.EMPTY;
		if ("file".equals(param.getType())) {
			for (int i = 0; i < files.size(); i++) {
				// 收集问数
				List<CollectBaseConfig> wenshuList = Lists.newArrayList();
				// 收集数据配置
				List<CollectBaseConfig> dataList = Lists.newArrayList();
				MultipartFile multipartFile = files.get(i);
				String fileName = fileNames.get(i);

				param.setFile(multipartFile);
				param.setConfigName(fileName);
				// 保存文件
				Result<CollectBaseConfig> result = saveFile(param);
				if (!result.isSuccess()) {
					resultMap.put(fileName, result.getMsg());
					isError = true;
					errorMsg = fileName.concat(": ").concat(result.getMsg()) ;
					continue;
				}
				CollectBaseConfig data = result.getData();
				dataList.add(data);
				// 更新配置表信息，先更新数据，会产生自增 id
				if (CollectionUtil.isNotEmpty(dataList)) {
					saveBatch(dataList);
				}
				wenshuList.addAll(dataList);
				// 保存数据原始内容
				initTable(param, dataList);

				// 更新问数配置
				updateExcelInfoData(wenshuList);
			}
			if (isError) {
				return Result.fail(errorMsg);
			}
			return Result.success(resultMap);
		} else if ("db".equals(param.getType())) {
			String configName = fileNames.get(0);
			param.setConfigName(configName);
			// 收集问数
			List<CollectBaseConfig> wenshuList = Lists.newArrayList();
			// 收集数据配置
			List<CollectBaseConfig> dataList = Lists.newArrayList();
			// 保存数据源
			Result<CollectBaseConfig> result = saveDb(param);
			if (!result.isSuccess()) {
				resultMap.put(configName, result.getMsg());
				return Result.fail(resultMap);
			}
			CollectBaseConfig data = result.getData();
			dataList.add(data);

			// 保存数据源表和字段
			List<CollectBaseConfig> configList = saveDirectory(data);
			if (CollectionUtil.isNotEmpty(configList)) {
				saveBatch(configList);
			}
			// 构建问数配置
			wenshuList.addAll(configList);
			// 更新问数配置
			updateExcelInfoData(wenshuList);

			return Result.success(dataList);
		}
		return Result.fail("数据类型异常");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result deleteCollectBaseConfig(List<String> idList){
		if (CollectionUtil.isEmpty(idList)) {
			return Result.success();
		}

		List<CollectBaseConfig> list = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_ID.in(idList))
				.list();

		if (CollectionUtil.isEmpty(list)) {
			return Result.success();
		}

		// 删除数据源配置表信息
		UpdateChain.of(CollectBaseConfig.class)
				.set(COLLECT_BASE_CONFIG.DELETED_FLAG, 1)
				.where(COLLECT_BASE_CONFIG.CONFIG_ID.in(idList))
				.update();
		CollectBaseConfig firstOne = list.get(0);
		// 如果不是文件数据源，则不需要删除文件，不需要删除数据源
		if (!"file".equals(firstOne.getType())) {
			return Result.success();
		}

		// 删除对应的数据表
		for (CollectBaseConfig collectBaseConfig : list) {
			String databaseConf = collectBaseConfig.getDatabaseConf();
			CollectFileTable bean = JSONUtil.toBean(databaseConf, CollectFileTable.class);
			if (StringUtils.isNotBlank(bean.getTableName())) {
				String dropTable = "drop table if exists " + bean.getTableName();
				log.info("dropTable:{}", dropTable);
				Db.updateBySql(dropTable);
			}
		}

		// 删除对应的问数表
		List<String> collectIdList = list.stream().map(CollectBaseConfig::getCollectId).distinct().collect(Collectors.toList());
		List<String>  tableNameList = list.stream().map(p -> {
			String databaseConf = p.getDatabaseConf();
			CollectFileTable bean = JSONUtil.toBean(databaseConf, CollectFileTable.class);
			return bean.getTableName();
		}).collect(Collectors.toList());
		Wrappers<ExcelInfoData> wrappers = Wrappers.of(excelInfoDataMapper)
				.and(EXCEL_INFO_DATA.KNOWLEDGE_ID.in(collectIdList))
				.and(EXCEL_INFO_DATA.TABLE_NAME.in(tableNameList));
		excelInfoDataMapper.deleteByQuery(wrappers);

		// 删除 minio 数据
		for (CollectBaseConfig collectBaseConfig : list) {
			String fileUrl = collectBaseConfig.getFileUrl();
			if (StringUtils.isNotBlank(fileUrl)) {
				String fileKey = WosUtil.getFileKey(fileUrl);
				wosUtil.delete(bucket, fileKey);
			}
		}

		return Result.success();
	}

	@Override
	public Result editCollectData(CollectDataEditParam editParam) {
		if (StringUtils.isBlank(editParam.getConfigId())) {
			return Result.fail("请选择表");
		}

		// 获取表信息
		CollectBaseConfig one = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_ID.eq(editParam.getConfigId()))
				.limit(1)
				.one();
		if (null == one) {
			return Result.fail("表不存在");
		}

		String fieldConfig = one.getFieldConfig();
		if (StringUtils.isBlank(fieldConfig)) {
			return Result.fail("请先配置字段");
		}
		JSONArray jsonArray = JSONUtil.parseArray(fieldConfig);
		String databaseConf = one.getDatabaseConf();
		CollectFileTable fileTable = JSONUtil.toBean(databaseConf, CollectFileTable.class);
		List<CollectField> fields = jsonArray.toList(CollectField.class);
		// 删除数据
		if (CollectionUtil.isNotEmpty(editParam.getDeleteIdList())) {
			String tableName = fileTable.getTableName();
			String[] split = tableName.split("\\.");
			Db.deleteBatchByIds(split[0], split[1], FILE_ID, editParam.getDeleteIdList());
		}

		// 新增数据
		if (CollectionUtil.isNotEmpty(editParam.getAddDataList())) {
			List<String> titleNameList = fields.stream().map(CollectField::getTitleName).collect(Collectors.toList());
			List<String> columnList = fields.stream().map(CollectField::getFieldName).collect(Collectors.toList());
			List<List<String>> dataList = editParam.getAddDataList().stream().map(p -> {
				List<String> list = Lists.newArrayList();
				for (String column : titleNameList) {
					if (!p.containsKey(column)) {
						list.add(null);
					} else {
						String str = p.getStr(column);
						list.add(str);
					}
				}
				return list;
			}).collect(Collectors.toList());
			if (dataList.isEmpty()) {
				return Result.fail("文件数据不能为空");
			}
			insertData(fileTable.getTableName(), columnList, dataList);
		}
		return Result.success();
	}

	@Override
	public Result editCollectConfig(CollectConfigEditParam param) {
		if (StringUtils.isBlank(param.getConfigId())) {
			return Result.fail("请选择表");
		}

		// 验证参数
		CollectBaseConfig one = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_ID.eq(param.getConfigId()))
				.limit(1)
				.one();
		if (null == one) {
			return Result.fail("表不存在");
		}
		List<CollectField> fields = param.getFields();
		if (CollectionUtil.isEmpty(fields)) {
			return Result.fail("请先配置字段");
		}

		// 校验表名称是否存在
		boolean exists = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_NAME.eq(param.getConfigName()))
				.and(COLLECT_BASE_CONFIG.COLLECT_ID.eq(one.getCollectId()))
				.and(COLLECT_BASE_CONFIG.CONFIG_ID.ne(param.getConfigId()))
				.exists();
		if (exists) {
			return Result.fail("数据表名称已存在");
		}

		// 获取字段
		String fieldConfig = one.getFieldConfig();
		if (StringUtils.isNotBlank(fieldConfig)) {
			Map<String, CollectField> fieldMap = fields.stream().collect(Collectors.toMap(
					CollectField::getFieldName,
					p -> p,
					(k1, k2) -> k1,
					HashMap::new
			));

			// 获取数据库字段
			List<CollectField> fieldsList = JSONUtil.toList(fieldConfig, CollectField.class);
			// 获取数据库配置
			JSONObject entries = JSONUtil.parseObj(one.getDatabaseConf());
			CollectFileTable table = entries.toBean(CollectFileTable.class);
			// List<CollectField> fieldsList = table.getFields();

			// 遍历字段
			for (CollectField collectField : fieldsList) {
				CollectField field = fieldMap.get(collectField.getFieldName());
				if (null != field) {
					collectField.setFieldDesc(field.getFieldDesc());
					collectField.setExample(field.getExample());
				}
			}
			one.setConfigName(param.getConfigName());
			one.setDescribe(param.getDescribe());
			one.setFieldConfig(JSONUtil.toJsonStr(fieldsList));
			one.setDatabaseConf(JSONUtil.toJsonStr(table));
			saveOrUpdate(one);

			// 更新问数配置
			updateExcelInfoData(ListUtil.toList(one));
		}

		return Result.success();
	}

	@Override
	public Result getCollectBaseConfig(StringParam param) {
		if (StringUtils.isBlank(param.getParam())) {
			return Result.success();
		}
		List<CollectBaseConfig> list = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.COLLECT_ID.eq(param.getParam()))
				.list();
		for (CollectBaseConfig collectBaseConfig : list) {
			if (Objects.isNull(collectBaseConfig.getFieldConfig())) {
				// 获取第一条数据
				if (Objects.isNull(collectBaseConfig.getDatabaseConf())) {
					continue;
				}
				CollectFileTable fileTable = JSONUtil.toBean(collectBaseConfig.getDatabaseConf(), CollectFileTable.class);
				// 删除数据
				String tableName = fileTable.getTableName();
				String[] split = tableName.split("\\.");
				Page paginate = Db.paginate(split[0], split[1], 1, 1, QueryCondition.createEmpty());
				List<Row> records = paginate.getRecords();
				if (CollectionUtil.isEmpty(records)) {
					continue;
				}

			}
		}

		return Result.success(list);
	}

	@Override
	public Result getCollectDataList(CollectDataListParam param) {
		if (StringUtils.isBlank(param.getConfigId())) {
			return Result.fail("请选择表");
		}

		CollectBaseConfig one = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_ID.eq(param.getConfigId()))
				.limit(1)
				.one();
		if (null == one) {
			return Result.fail("表不存在");
		}
		if (Objects.isNull(one.getDatabaseConf())) {
			return Result.fail("表不存在");
		}
		CollectFileTable fileTable = JSONUtil.toBean(one.getDatabaseConf(), CollectFileTable.class);
		// 删除数据
		String tableName = fileTable.getTableName();
		String database = fileTable.getDatabase();
		if (tableName.contains(".")) {
			tableName = tableName.split("\\.")[1];
		}
		Page paginate = Db.paginate(database, tableName, param.getPageNo(), param.getPageSize(), QueryCondition.createEmpty());
		List<Row> records = paginate.getRecords();
		List<JSONObject> dataList = Lists.newArrayList();
		List<CollectField> fields = JSONUtil.toList(one.getFieldConfig(), CollectField.class);

		if (CollectionUtil.isNotEmpty(records)) {
			HashMap<String, String> fieldMap = fields.stream().collect(Collectors.toMap(
					CollectField::getFieldName,
					CollectField::getTitleName,
					(k1, k2) -> k1,
					HashMap::new
			));
			dataList = records.stream().map(p -> {
				JSONObject entries1 = new JSONObject();
				p.forEach((k, v) -> {
					String filedName = fieldMap.get(k);
					if (StringUtils.isBlank(filedName)) {
						entries1.set(k, v);
					} else {
						entries1.set(filedName, v);
					}
				});

				return entries1;
			}).collect(Collectors.toList());
		}
		paginate.setRecords(dataList);
		return Result.success(paginate);
	}

	@Override
	public Result getWenShuConfig(WenShuConfigGetParam param) {
		if (CollectionUtil.isEmpty(param.getKnowledgeIdList())) {
			return Result.success(Lists.newArrayList());
		}
		List<ExcelInfoData> list = Wrappers.of(excelInfoDataMapper)
				.where(EXCEL_INFO_DATA.KNOWLEDGE_ID.in(param.getKnowledgeIdList()))
				.list();

		return Result.success(list);
	}

	@Override
	public Map<String, Long> getTableNum(List<String> collectIds) {
		if (CollectionUtil.isEmpty(collectIds)) {
			return Maps.newHashMap();
		}

		List<CollectBaseConfig> list = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.COLLECT_ID.in(collectIds))
				.list();

		return list.stream().collect(Collectors.groupingBy(CollectBaseConfig::getCollectId, Collectors.counting()));
	}

	@Override
	public List<String> getConfigIdListByCollectId(List<String> collectIds) {
		if (CollectionUtil.isEmpty(collectIds)) {
			return Lists.newArrayList();
		}

		List<CollectBaseConfig> list = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.COLLECT_ID.in(collectIds))
				.list();

		return list.stream().map(CollectBaseConfig::getConfigId).collect(Collectors.toList());
	}

	/**
	 * 保存文件
	 * @param param
	 * @return
	 */
	private Result<CollectBaseConfig> saveFile(CollectConfigAddParam param) {
		MultipartFile file = param.getFile();
		if (null == file) {
			return Result.fail("请选择文件");
		}

		// 提取文件名称，例如：用户信息表.xlsx
		if (StringUtils.isNotBlank(param.getConfigName())) {
			String configName = param.getConfigName();
			if (configName.contains(".")) {
				param.setConfigName(configName.substring(0, configName.lastIndexOf(".")));
			}
		}

		List<CollectBaseConfig> list = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_NAME.in(param.getConfigName()))
				.and(COLLECT_BASE_CONFIG.COLLECT_ID.eq(param.getCollectId()))
				.and(COLLECT_BASE_CONFIG.TYPE.eq("file"))
				.list();
		if (CollectionUtil.isNotEmpty(list)) {
			return Result.fail("数据库已存在");
		}

		// 构建配置
		CollectBaseConfig collectBaseConfig = new CollectBaseConfig();
		try {
			collectBaseConfig.setConfigId(IdUtil.simpleUUID());
			collectBaseConfig.setType("file");
			collectBaseConfig.setCollectId(param.getCollectId());
			collectBaseConfig.setDescribe(param.getDescribe());
			collectBaseConfig.setConfigName(param.getConfigName());
			MinioInfoResult minioInfoResult = wosUtil.upload(bucket, "collect-data", file, true);
			collectBaseConfig.setFileUrl(minioInfoResult.getUrlPath());
			collectBaseConfig.setFormatter(minioInfoResult.getFileType());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 保存文件失败
		if (StringUtils.isBlank(collectBaseConfig.getFileUrl())) {
			return Result.fail("保存文件失败");
		}
		// 记录存在失败的
		return Result.success(collectBaseConfig);
	}

	/**
	 * 插入数据
	 *
	 * @param param
	 */
	private void initTable(CollectConfigAddParam param, List<CollectBaseConfig> collectBaseConfigs) {
		MultipartFile multipartFile = param.getFile();
		try {
			// 获取文件名
			String originalFilename = multipartFile.getOriginalFilename();
			ExcelReader reader = ExcelUtil.getReader(multipartFile.getInputStream());
			// 第一行为  标题
			int row = 1;
			List<List<Object>> read = reader.read();
			if (CollectionUtil.isEmpty(read)) {
				throw new RuntimeException("【" + originalFilename + "】文件内容不能为空");
			}
			List<Object> titleList = read.get(0);
			if (CollectionUtil.isEmpty(titleList)) {
				throw new RuntimeException("【" + originalFilename + "】文件标题不能为空");
			}

			// 获取标题
			int size = titleList.size();
			List<String> titles = Lists.newArrayList();
			String value = StringConstant.BLANK;
			for (int i = 0; i < size; i++) {
				value = knowledgeDataServiceImpl.getValue(titleList, i);
				if (StringUtils.isBlank(value)) {
					throw new RuntimeException("【" + originalFilename + "】文件第" + (i + 1) + "列标题不能为空");
				}
				titles.add(value);
			}

			// 从第二行开始读取
			read = read.subList(row, read.size());
			List<List<String>> dataList = Lists.newArrayList();
			List<String> dataDetais = Lists.newArrayList();
			for (List<Object> dataDetailList : read) {
				if (CollectionUtil.isEmpty(dataDetailList)) {
					continue;
				}
				dataDetais = Lists.newArrayList();
				for (int i = 0; i < titles.size(); i++) {
					Object o = dataDetailList.get(i);
					if (null == o) {
						dataDetais.add(null);
					} else {
						dataDetais.add(o.toString().replace("'", "''"));
					}
				}
				dataList.add(dataDetais);
			}

			CollectBaseConfig collectBaseConfig = collectBaseConfigs.get(0);
			// 设置表名,COLLECT_DABABASE+环境+配置表自增id
			String tableName = COLLECT_DABABASE + "." + profiles + "_" + collectBaseConfig.getId();
			List<CollectField> columnNameList = Lists.newArrayList();

			List<String> firstData = Lists.newArrayList();
			if (CollectionUtil.isNotEmpty(dataList)) {
				firstData = dataList.get(0);
			}
			Set<String> fieldNames = new HashSet<>();
			for (int i = 0; i < titles.size(); i++) {
				// 创建字段
//				columnNameList.add(new CollectField("F_" + i, "text", titles.get(i), titles.get(i), firstData.get(i)));
				// 创建字段
				// 直接使用表格第一行字段作为
				String firstTitle = titles.get(i);
				if (StringUtils.isBlank(firstTitle)) {
					// 如果字段为空的话，则取
					firstTitle = "F_" + i;
				} else if (fieldNames.contains(firstTitle)) {
					firstTitle = firstTitle + i;
				}
				fieldNames.add(firstTitle);
				columnNameList.add(new CollectField(firstTitle, "text", firstTitle, titles.get(i), firstData.get(i)));
			}

			// 设置对应的数据库信息
			CollectFileTable collectFileTable = buildCollectFileTable(tableName);
			collectBaseConfig.setFieldConfig(JSONUtil.toJsonStr(columnNameList));
			collectBaseConfig.setDatabaseConf(JSONUtil.toJsonStr(collectFileTable));
			// 创建表
			createTable(tableName, columnNameList);
			// 插入数据
			List<String> columnList = columnNameList.stream().map(CollectField::getFieldName).distinct().collect(Collectors.toList());
			insertData(tableName, columnList, dataList);
			if (dataList.isEmpty()) {
				throw new RuntimeException("文件数据不能为空");
			}

			updateBatch(collectBaseConfigs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("文件导入失败");
		}
	}

	/**
	 * 创建表
	 * @param tableName
	 * @param columnList
	 */
	private void createTable(String tableName, List<CollectField> columnList) {
		String dropTable = "drop table if exists " + tableName;
		log.info("dropTable:{}", dropTable);
		Db.updateBySql(dropTable);

		StringBuilder createTableSql = new StringBuilder("CREATE TABLE " + tableName);
		createTableSql.append(" ( " + FILE_ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ");
		for (int i = 0; i < columnList.size(); i++) {
			createTableSql.append("`").append(columnList.get(i).getFieldName()).append("` ").append(columnList.get(i).getFieldType()).append(" ").append("COMMENT '").append(columnList.get(i).getFieldDesc()).append("'");
			if (i < columnList.size() - 1) {
				createTableSql.append(",");
			}
		}
		createTableSql.append(")");
		log.info("createTableSql:{}", createTableSql);
		Db.updateBySql(createTableSql.toString());
	}

	/**
	 * 插入数据
	 * @param tableName
	 * @param columnNameList
	 * @param dataList
	 */
	private void insertData(String tableName, List<String> columnNameList, List<List<String>> dataList) {
		StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
		for (int i = 0; i < columnNameList.size(); i++) {
			insertSql.append("`").append(columnNameList.get(i)).append("`");
			if (i < columnNameList.size() - 1) {
				insertSql.append(",");
			}
		}
		insertSql.append(") VALUES ");
		for (int i = 0; i < dataList.size(); i++) {
			insertSql.append("(");
			for (int j = 0; j < dataList.get(i).size(); j++) {
				if (null == dataList.get(i).get(j)) {
					insertSql.append(" NULL ");
				} else {
					insertSql.append("'").append(dataList.get(i).get(j)).append("'");
				}
				if (j < dataList.get(i).size() - 1) {
					insertSql.append(",");
				}
			}
			insertSql.append(")");
			if (i < dataList.size() - 1) {
				insertSql.append(",");
			}
		}
		log.info("insertSql:{}", insertSql);
		Db.insertBySql(insertSql.toString());
	}

	/**
	 * 构建数据库配置
	 * @param tableName
	 * @return
	 */
	private CollectFileTable buildCollectFileTable(String tableName) {
		CollectFileTable dbConfig = new CollectFileTable();
		// 正则表达式匹配 IP:PORT
		Pattern pattern = Pattern.compile("//([^:/]+)(?::(\\d+))?");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			String ip = matcher.group(1);
			String port = matcher.group(2) != null ? matcher.group(2) : "3306"; // 默认端口 3306
			dbConfig.setHost(ip);
			dbConfig.setPort(port);
		} else {
			System.out.println("No IP and port found in JDBC URL");
		}
		dbConfig.setDatabase(COLLECT_DABABASE);
		dbConfig.setUsername(username);
		AES aes = SecureUtil.aes(AES_KEY.getBytes());
		dbConfig.setPwkey(aes.encryptBase64(mybatiskey));
		dbConfig.setTableName(tableName);
		return dbConfig;
	}

	/**
	 * 更新问数表信息
	 * @param dataList
	 */
	private void updateExcelInfoData(List<CollectBaseConfig> dataList) {
		List<String> configIdList = dataList.stream().map(CollectBaseConfig::getConfigId).distinct().collect(Collectors.toList());
		if (CollectionUtil.isEmpty(configIdList)) {
			return;
		}

		// 构建问数表配置数据
		List<ExcelInfoData> datas = dataList.stream().map(p -> {
			// 删除历史记录
			String databaseConf = p.getDatabaseConf();
			CollectFileTable fileTable = JSONUtil.toBean(databaseConf, CollectFileTable.class);
			Wrappers<ExcelInfoData> wrappers = Wrappers.of(excelInfoDataMapper)
					.where(EXCEL_INFO_DATA.KNOWLEDGE_ID.eq(p.getCollectId()))
					.and(EXCEL_INFO_DATA.TABLE_NAME.eq(fileTable.getTableName()));
			excelInfoDataMapper.deleteByQuery(wrappers);

			// 获取字段信息
			String fieldConfig = p.getFieldConfig();
			List<CollectField> fields = JSONUtil.parseArray(fieldConfig).toList(CollectField.class);
			AES aes = SecureUtil.aes(AES_KEY.getBytes());
			// 构建数据源
			SqlSourceVo sqlSource = SqlSourceVo.builder()
					.host(fileTable.getHost())
					.port(fileTable.getPort())
					.user(fileTable.getUsername())
					.password(aes.decryptStr(fileTable.getPwkey()))
					.database(fileTable.getDatabase())
					.prefix("jdbc:mysql://")
					.build();

			// 构建数据库信息
			// List<CollectField> fields = fileTable.getFields();
			List<JSONObject> allFieldListSz = fields.stream().map(field -> {
				JSONObject entries = new JSONObject();
				entries.set("字段名称", field.getFieldName());
				entries.set("字段类型", field.getFieldType());
				entries.set("字段注释", field.getFieldDesc());
				return entries;
			}).collect(Collectors.toList());
			Map<String, Object> map = new HashMap<>();
			map.put("sql表名", fileTable.getTableName());
			map.put("sql表字段名称、字段类型和字段注释信息", allFieldListSz);

			// 构建数据示例
			JSONObject example = new JSONObject();
			for (CollectField field : fields) {
				example.set(field.getFieldName(), field.getExample());
			}
			map.put("数据示例如下", example);

			return ExcelInfoData.builder()
					.SqlSource(JSONUtil.toJsonStr(sqlSource))
					.tableName(fileTable.getTableName())
					.knowledgeId(p.getCollectId())
					.excelInfo(JSONUtil.toJsonStr(map))
					.build();
		}).collect(Collectors.toList());

		// 批量插入
		if (CollectionUtil.isNotEmpty(datas)) {
			excelInfoDataMapper.insertBatch(datas);
		}
	}

	/**
	 * 保存数据库
	 * @param param
	 * @return
	 */
	private Result<CollectBaseConfig> saveDb(CollectConfigAddParam param) {
		boolean exists = Wrappers.of(mapper)
				.where(COLLECT_BASE_CONFIG.CONFIG_NAME.eq(param.getConfigName()))
				.and(COLLECT_BASE_CONFIG.TYPE.eq("db"))
				.and(COLLECT_BASE_CONFIG.COLLECT_ID.eq(param.getCollectId()))
				.exists();
		if (exists) {
			return Result.fail("名称已存在");
		}
		CollectBaseConfig collectBaseConfig = new CollectBaseConfig();
		collectBaseConfig.setConfigId(IdUtil.simpleUUID());
		param.setConfigId(collectBaseConfig.getConfigId());
		collectBaseConfig.setType("db");
		collectBaseConfig.setCollectId(param.getCollectId());
		collectBaseConfig.setDescribe(param.getDescribe());
		collectBaseConfig.setConfigName(param.getConfigName());
		String pwkey = param.getDbConfig().getPwkey();
		AES aes = SecureUtil.aes(AES_KEY.getBytes());
		String encryptBase64 = aes.encryptBase64(pwkey);
		param.getDbConfig().setPwkey(encryptBase64);
		collectBaseConfig.setDatabaseConf(JSONUtil.toJsonStr(param.getDbConfig()));

		return Result.success(collectBaseConfig);
	}

	/**
	 * 保存数据源表和字段
	 * @param param
	 */
	private List<CollectBaseConfig> saveDirectory(CollectBaseConfig param) {
		String databaseConf = param.getDatabaseConf();
		CollectFileTable fileTable = JSONUtil.parseObj(databaseConf).toBean(CollectFileTable.class);
		DataSource dataSource = dataSourceFactory.getDataSource(1);
		String url = "jdbc:mysql://{host}:{port}/{database}";
		String replace = url
				.replace("{host}", fileTable.getHost())
				.replace("{port}", fileTable.getPort())
				.replace("{database}", fileTable.getDatabase());
		// 获取所有表
		List<String> allTables = dataSource.getAllTables(replace, fileTable.getUsername(), SecureUtil.aes(AES_KEY.getBytes()).decryptStr(fileTable.getPwkey()));
		if (CollectionUtil.isEmpty(allTables)) {
			return Lists.newArrayList();
		}

		// 获取所有字段
        return allTables.stream().parallel().map(p -> {
			CollectBaseConfig config = BeanUtil.toBean(param, CollectBaseConfig.class);
			config.setConfigName(p);
			config.setConfigId(IdUtil.simpleUUID());
			config.setDescribe(p);
			String dbConf = config.getDatabaseConf();
			CollectFileTable bean = JSONUtil.toBean(dbConf, CollectFileTable.class);
			bean.setTableName(p);
			config.setDatabaseConf(JSONUtil.toJsonStr(bean));

			// 获取该表字段信息
			Map<String, TableInfoVo> columnsInfoMap = JdbcUtils.getTableColumnsInfo(replace, fileTable.getUsername(), SecureUtil.aes(AES_KEY.getBytes()).decryptStr(fileTable.getPwkey()), bean.getDatabase() + "." + p, "com.mysql.cj.jdbc.Driver");
			// 获取该表部分数据
			List<JSONObject> tableData = JdbcUtils.getTableData(replace, fileTable.getUsername(), SecureUtil.aes(AES_KEY.getBytes()).decryptStr(fileTable.getPwkey()), bean.getDatabase() + "." + p, 10, 1);
			JSONObject entries = new JSONObject();
			if (CollectionUtil.isNotEmpty(tableData)) {
				entries.putAll(tableData.get(0));
			}
			List<TableInfoVo> allFieldList = new ArrayList<>();
			columnsInfoMap.forEach((columnName, tableInfo) -> allFieldList.add(tableInfo));

			// 获取该表所有字段信息
			List<CollectField> fieldList = allFieldList.stream().map(tableInfoVo -> {
				CollectField field = new CollectField();
				field.setFieldName(tableInfoVo.getColumnName());
				field.setFieldType(tableInfoVo.getColumnType());
				field.setFieldDesc(tableInfoVo.getRemarks());
				field.setTitleName(tableInfoVo.getColumnName());
				field.setExample(entries.getStr(tableInfoVo.getColumnName()));
				return field;
			}).collect(Collectors.toList());

			config.setFieldConfig(JSONUtil.toJsonStr(fieldList));
			return config;
		}).collect(Collectors.toList());
	}

}