package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.NodeTableInfoParam;
import com.wenge.model.entity.ComponentNodeTableInfo;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.mapper.ComponentNodeTableInfoMapper;
import com.wenge.model.service.ComponentNodeTableFieldService;
import com.wenge.model.service.ComponentNodeTableInfoService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.wenge.model.entity.table.ComponentNodeTableInfoTableDef.COMPONENT_NODE_TABLE_INFO;

/**
 * Description: 数据库节点表数据服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:11:33
 *
 */
@Service
@Slf4j
@DependsOn("componentNodeTableFieldServiceImpl")
public class ComponentNodeTableInfoServiceImpl extends ServiceImpl<ComponentNodeTableInfoMapper, ComponentNodeTableInfo> implements ComponentNodeTableInfoService {

	@Value("${spring.profiles.active}")
	public String profiles;

	// mysql 数据库节点的默认数据库
	public static String MYSQL_DEFAULT_DATABASE = "smart_customer_agent_table_";

	@PostConstruct
	public void init() {
		if (profiles == null || profiles.isEmpty()) {
			throw new IllegalArgumentException("spring.profiles.active 未配置");
		}
		MYSQL_DEFAULT_DATABASE = MYSQL_DEFAULT_DATABASE + profiles;
	}

	/**
	 * 	数据库节点表数据数据库处理类
	 */
	@Autowired
	private ComponentNodeTableInfoMapper componentNodeTableInfoMapper;

	@Autowired
	private ComponentNodeTableFieldService fieldService;

	@Override
	public Result addComponentNodeTableInfo(ComponentNodeTableInfo componentNodeTableInfo){
		if (StringUtils.isBlank(componentNodeTableInfo.getTableName())) {
			return Result.fail("表名不能为空");
		}

		// mysql 表名组成的字符，用正则表达式判断
		if (!componentNodeTableInfo.getTableName().matches("[a-zA-Z0-9_]+")) {
			return Result.fail("表名只能包含字母、数字和下划线");
		}
		boolean exists = Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_INFO.TABLE_NAME.eq(componentNodeTableInfo.getTableName().trim()))
				.exists();
		if (exists) {
			return Result.fail("表名已存在");
		}

		componentNodeTableInfo.setTableId(IdUtil.simpleUUID());
		// 默认数据库
		componentNodeTableInfo.setDataBase(MYSQL_DEFAULT_DATABASE);
		save(componentNodeTableInfo);

		return Result.success();
	}

	@Override
	public Result<Page<ComponentNodeTableInfo>> getComponentNodeTableInfoList(NodeTableInfoParam param){
		Wrappers wrappers = Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_INFO.TABLE_NAME.like(param.getTableName()).when(StringUtils.isNotBlank(param.getTableName())))
				.orderBy(COMPONENT_NODE_TABLE_INFO.CREATE_TIME.desc());

		PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.WORKFLOW_MYSQL_NODE);
		Page<ComponentNodeTableInfo> page = wrappers.page(Page.of(param.getPageNo(), param.getPageSize()));
		return Result.success(page);
	}

	@Override
	public ComponentNodeTableInfo getByTableId(String tableId) {
		if (StringUtils.isBlank(tableId)) {
			return null;
		}
		return Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_INFO.TABLE_ID.eq(tableId))
				.limit(1)
				.one();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result deleteNodeTableInfo(List<String> idList) {
		if (CollectionUtil.isEmpty(idList)) {
			return Result.success();
		}

		// 删除字段信息
		fieldService.deleteFieldByTableId(idList);

		// 删除表信息
		Wrappers<ComponentNodeTableInfo> remove = Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_INFO.TABLE_ID.in(idList));
		remove(remove);
		return Result.success();
	}

	@Override
	public ComponentNodeTableInfo getByTableName(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return null;
		}
		return Wrappers.of(mapper)
				.where(COMPONENT_NODE_TABLE_INFO.TABLE_NAME.eq(tableName))
				.limit(1)
				.one();
	}
}