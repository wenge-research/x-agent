package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.NodeTableDataAddParam;
import com.wenge.model.dto.param.NodeTableDataListParam;
import com.wenge.model.dto.param.NodeTableFieldAddParam;
import com.wenge.model.entity.ComponentNodeTableField;
import com.wenge.model.entity.ComponentNodeTableInfo;
import com.wenge.model.mapper.ComponentNodeTableFieldMapper;
import com.wenge.model.service.ComponentNodeTableFieldService;
import com.wenge.model.service.ComponentNodeTableInfoService;
import com.wenge.model.strategy.dataSourceParser.MySQLDataSource;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ComponentNodeTableFieldTableDef.COMPONENT_NODE_TABLE_FIELD;

/**
 * Description: 数据库节点表字段数据服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:47:59
 *
 */
@Service
@Slf4j
public class ComponentNodeTableFieldServiceImpl extends ServiceImpl<ComponentNodeTableFieldMapper, ComponentNodeTableField> implements ComponentNodeTableFieldService {
	/**
	 * 	数据库节点表字段数据数据库处理类
	 */
	@Autowired
	private ComponentNodeTableFieldMapper componentNodeTableFieldMapper;

	@Autowired
	private ComponentNodeTableInfoService tableInfoService;

	// 表字段查询
	private static String TABLE_FIELD_SQL_FORMATTER = "SELECT COLUMN_NAME,DATA_TYPE,IS_NULLABLE,COLUMN_COMMENT from information_schema.COLUMNS where TABLE_SCHEMA = '" + ComponentNodeTableInfoServiceImpl.MYSQL_DEFAULT_DATABASE + "' and TABLE_NAME = 'node_{}'";

	// 表名规则
	public static String TABLE_NAME_FORMATTER = ComponentNodeTableInfoServiceImpl.MYSQL_DEFAULT_DATABASE + ".`node_{}`";

	// 创建表
	private static String TABLE_CREATE_FORMATTER = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FORMATTER + " ( id bigint auto_increment comment '自增 id' primary key, uuid varchar(40) default (uuid()) not null comment '业务id', ";

	// 删除表
	private static String TABLE_DROP_FORMATTER = "drop table if exists " + TABLE_NAME_FORMATTER;

	private static String DELETE_SQL_FORMATTER = "delete from {} where id in ({})";


	@PostConstruct
	public void init() {
		TABLE_NAME_FORMATTER = ComponentNodeTableInfoServiceImpl.MYSQL_DEFAULT_DATABASE + ".`node_{}`";
		TABLE_CREATE_FORMATTER = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FORMATTER + " ( id bigint auto_increment comment '自增 id' primary key, uuid varchar(40) default (uuid()) not null comment '业务id', ";
		TABLE_DROP_FORMATTER = "drop table if exists " + TABLE_NAME_FORMATTER;
		TABLE_FIELD_SQL_FORMATTER = "SELECT COLUMN_NAME,DATA_TYPE,IS_NULLABLE,COLUMN_COMMENT from information_schema.COLUMNS where TABLE_SCHEMA = '" + ComponentNodeTableInfoServiceImpl.MYSQL_DEFAULT_DATABASE + "' and TABLE_NAME = 'node_{}'";
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result addComponentNodeTableField(NodeTableFieldAddParam param){
		if (StringUtils.isBlank(param.getTableId())) {
			return Result.fail("请指定一张表");
		}
		ComponentNodeTableInfo tableInfo = tableInfoService.getByTableId(param.getTableId());
		if (null == tableInfo) {
			return Result.fail("表已不存在");
		}

		// 查询表字段
		String findColumSql = StrUtil.format(TABLE_FIELD_SQL_FORMATTER, tableInfo.getTableName());
		List<Row> rows = Db.selectListBySql(findColumSql);

		// 新字段
		List<ComponentNodeTableField> fieldList = param.getFieldList();

		long count = fieldList.stream().map(ComponentNodeTableField::getFieldName).distinct().count();
		if (count != fieldList.size()) {
			return Result.fail("有存在重复字段");
		}
		fieldList.forEach(item -> {
			item.setTableId(param.getTableId());
			if (StringUtils.isBlank(item.getFieldId())) {
				item.setFieldId(IdUtil.simpleUUID());
			}
		});

		// 历史没有字段，有新字段，则创建新表
		if (CollectionUtil.isEmpty(rows) && CollectionUtil.isNotEmpty(fieldList)) {
			createNewTable(fieldList, tableInfo);
		} else if (CollectionUtil.isEmpty(rows) && CollectionUtil.isEmpty(fieldList)) {
			// 历史空字段，没有新字段，则不建表
		} else if (CollectionUtil.isNotEmpty(rows) && CollectionUtil.isNotEmpty(fieldList)) {
			// 历史字段，有新字段，则更新表
			updateTable(fieldList, tableInfo, rows);
		} else if (CollectionUtil.isNotEmpty(rows) && CollectionUtil.isEmpty(fieldList)) {
			// 历史字段，没有新字段，则删除表
			String dropTable = StrUtil.format(TABLE_DROP_FORMATTER, tableInfo.getTableName());
			Db.updateBySql(dropTable);
		}

		// 清空字段
		Wrappers<ComponentNodeTableField> clearWarpper = Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_FIELD.TABLE_ID.eq(param.getTableId()));
		remove(clearWarpper);

		// 保存字段
		if (CollectionUtil.isNotEmpty(fieldList)) {
			saveBatch(fieldList);
		}
		return Result.success();
	}

	@Override
	public Result<Page<JSONObject>> getComponentNodeTableFieldList(NodeTableDataListParam param){
		if (StringUtils.isBlank(param.getTableId())) {
			return Result.fail("请指定一张表");
		}
		ComponentNodeTableInfo tableInfo = tableInfoService.getByTableId(param.getTableId());
		if (null == tableInfo) {
			return Result.fail("表已不存在");
		}
		Page<JSONObject> page = new Page<>();
		page.setPageNumber(param.getPageNo());
		page.setPageSize(param.getPageSize());
		try {
			String tableName = StrUtil.format(TABLE_NAME_FORMATTER, tableInfo.getTableName());
			// 分页查询
			List<Row> rows1 = Db.selectListBySql("select count(0) from " + tableName);
			Row row = rows1.get(0);
			Long total = row.getLong("count(0)");
			List<Row> rows = Db.selectListBySql("select * from " + tableName + " limit " + param.getPageSize() + " offset " + (param.getPageNo() - 1) * param.getPageSize());
			List<JSONObject> dataList = rows.stream().map(p -> {
				JSONObject data = new JSONObject();
				p.forEach(data::set);
				return data;
			}).collect(Collectors.toList());
			page.setRecords(dataList);
			page.setTotalRow(total);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.success(page);
	}

	@Override
	public Result<List<ComponentNodeTableField>> getNodeTableFields(StringParam param) {
		if (StringUtils.isBlank(param.getParam())) {
			return Result.success(new ArrayList<>());
		}
		List<ComponentNodeTableField> list = Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_FIELD.TABLE_ID.eq(param.getParam()))
				.list();
		return Result.success(list);
	}

	@Override
	public Result addNodeTableData(NodeTableDataAddParam param) {
		if (StringUtils.isBlank(param.getTableId())) {
			return Result.fail("请指定一张表");
		}
		ComponentNodeTableInfo tableInfo = tableInfoService.getByTableId(param.getTableId());
		if (null == tableInfo) {
			return Result.fail("表已不存在");
		}
		String tableName = StrUtil.format(TABLE_NAME_FORMATTER, tableInfo.getTableName());
		List<JSONObject> dataList = param.getDataList();
		addTableData(dataList, tableName);
		return Result.success();
	}

	/**
	 * 添加表数据
	 * @param dataList
	 * @param tableName
	 */
	public static void addTableData(List<JSONObject> dataList, String tableName) {
		if (CollectionUtil.isNotEmpty(dataList)) {
			dataList.forEach(item->{
				if (StringUtils.isBlank(item.getStr("id"))) {
					item.remove("id");
				}
				if (StringUtils.isBlank(item.getStr("uuid"))) {
					item.remove("uuid");
				}
			});


			String collect = dataList.stream()
					.filter(p -> StringUtils.isNotBlank(p.getStr("id")))
					.map(p -> p.getStr("id")).collect(Collectors.joining(","));
			if (StringUtils.isNotBlank(collect)) {
				String deleteSql = StrUtil.format(DELETE_SQL_FORMATTER, tableName, collect);
				Db.insertBySql(deleteSql);
			}

			// 查询表字段
			String tableNameTemp = tableName.replace((ComponentNodeTableInfoServiceImpl.MYSQL_DEFAULT_DATABASE + "."), StringConstant.BLANK);
			tableNameTemp = tableNameTemp
					.replace("`node_", StringConstant.BLANK)
					.replace("`", StringConstant.BLANK);

			String findColumSql = StrUtil.format(TABLE_FIELD_SQL_FORMATTER, tableNameTemp);
			List<Row> rows = Db.selectListBySql(findColumSql);
			List<String> columnName = rows.stream().map(p -> p.getString("COLUMN_NAME")).distinct().collect(Collectors.toList());
			String sql = StringConstant.BLANK;
			Object value = null;
			List<String> columnNameTemp;
			List<String> columnNameTemp2 = ListUtil.toList();
			List<Object> valueList = null;
			for (JSONObject entries : dataList) {
				columnNameTemp2.clear();
				columnNameTemp = new ArrayList<>(columnName);
				valueList = Lists.newArrayList();
				int size = columnNameTemp.size();
				for (int i = 0; i < size; i++) {
					String colume = columnNameTemp.get(i);
					value = entries.get(colume);
					if (null != value) {
						columnNameTemp2.add(colume);
						valueList.add(value);
					}
				}

				if (CollectionUtil.isNotEmpty(columnNameTemp2)) {
					sql = MySQLDataSource.buildInsertSql(tableName, columnNameTemp2);
					for (int i = 0; i < columnNameTemp2.size(); i++) {
						sql = sql.replaceFirst("\\?", "'" + valueList.get(i).toString() + "'");
					}
					Db.insertBySql(sql);
				}
			}
		}
	}

	@Override
	public void deleteFieldByTableId(List<String> idList) {
		if (CollectionUtil.isEmpty(idList)) {
			return;
		}
		// 删除字段信息
		Wrappers<ComponentNodeTableField> wrappers = Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_FIELD.TABLE_ID.in(idList));
		remove(wrappers);

		// 删除表
		for (String tableId : idList) {
			ComponentNodeTableInfo tableInfo = tableInfoService.getByTableId(tableId);
			if (null != tableInfo) {
				String dropTable = StrUtil.format(TABLE_DROP_FORMATTER, tableInfo.getTableName());
				Db.updateBySql(dropTable);
			}
		}
	}

	/**
	 * 创建新表
	 * @param fieldList
	 * @param tableInfo
	 */
	private void createNewTable(List<ComponentNodeTableField> fieldList, ComponentNodeTableInfo tableInfo) {
		// 创建表
		StringBuilder createTableSql = new StringBuilder(StrUtil.format(TABLE_CREATE_FORMATTER, tableInfo.getTableName()));
		String comment = StringConstant.BLANK;
		String requestFlag = StringConstant.BLANK;

		// 删除掉默认的 id 和 uuid 字段
		fieldList = fieldList.stream()
				.filter(p -> !p.getFieldName().equals("uuid") && !p.getFieldName().equals("id"))
				.collect(Collectors.toList());

		// 构建创建表的SQL语句
		for (ComponentNodeTableField field : fieldList) {
			field.setFieldId(IdUtil.simpleUUID());
			field.setTableId(tableInfo.getTableId());
			if (StringUtils.isBlank(field.getComment())) {
				comment = StringConstant.BLANK;
			} else {
				comment = "comment '" + field.getComment() + "' ";
			}

			if ("1".equals(field.getRequestFlag())) {
				requestFlag = " not null ";
			} else {
				requestFlag = StringConstant.BLANK;
			}
			createTableSql.append("\n").append("`").append(field.getFieldName()).append("` ").append(field.getFieldType()).append(" ").append(requestFlag).append(comment).append(",");
		}
		createTableSql.deleteCharAt(createTableSql.length() - 1);
		createTableSql.append(")");

		log.info("createTableSql: {}", createTableSql);
		// 执行创建表
		Db.updateBySql(createTableSql.toString());
	}

	/**
	 * 更新表
	 *
	 * @param fieldList
	 * @param tableInfo
	 */
	private void updateTable(List<ComponentNodeTableField> fieldList, ComponentNodeTableInfo tableInfo, List<Row> rows) {
		String tableName = StrUtil.format(TABLE_NAME_FORMATTER, tableInfo.getTableName());
		List<String> newFieldList = fieldList.stream().map(ComponentNodeTableField::getFieldName).distinct().collect(Collectors.toList());

		// 需要新增的字段
		List<ComponentNodeTableField> toAddFieldList = fieldList.stream()
				.filter(p -> !rows.stream().map(q -> q.getString("COLUMN_NAME")).collect(Collectors.toList()).contains(p.getFieldName()))
				.collect(Collectors.toList());
		String comment = StringConstant.BLANK;
		String requestFlag = StringConstant.BLANK;
		if (CollectionUtil.isNotEmpty(toAddFieldList)) {
			for (ComponentNodeTableField field : toAddFieldList) {
				if (StringUtils.isBlank(field.getComment())) {
					comment = StringConstant.BLANK;
				} else {
					comment = " comment '" + field.getComment() + "' ";
				}

				if ("1".equals(field.getRequestFlag())) {
					requestFlag = " not null ";
				} else {
					requestFlag = StringConstant.BLANK;
				}
				String addColumnSql = "alter table " + tableName + " add column " + "`" + field.getFieldName() + "` " + field.getFieldType() + " " + requestFlag + comment;
				log.info("addColumnSql: {}", addColumnSql);
				Db.updateBySql(addColumnSql);
			}
		}

		// 需要删除的字段
		List<String> toDropFieldList = rows.stream().filter(p -> !newFieldList.contains(p.getString("COLUMN_NAME")))
				.map(p -> p.getString("COLUMN_NAME"))
				.collect(Collectors.toList());
		if (CollectionUtil.isNotEmpty(toDropFieldList)) {
			for (String fieldName : toDropFieldList) {
				String dropColumnSql = "alter table " + tableName + " drop column `" + fieldName + "`";
				log.info("dropColumnSql: {}", dropColumnSql);
				Db.updateBySql(dropColumnSql);
			}
		}
	}
}