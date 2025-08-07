package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.DataCollectPageParam;
import com.wenge.model.entity.DataCollectInfo;
import com.wenge.model.mapper.DataCollectInfoMapper;
import com.wenge.model.service.CollectBaseConfigService;
import com.wenge.model.service.DataCollectInfoService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.DataCollectInfoTableDef.DATA_COLLECT_INFO;

/**
 * Description: 数据集信息表服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 18:05:32
 *
 */
@Service
@Slf4j
public class DataCollectInfoServiceImpl extends ServiceImpl<DataCollectInfoMapper, DataCollectInfo> implements DataCollectInfoService {
	/**
	 * 	数据集信息表数据库处理类
	 */
	@Autowired
	private DataCollectInfoMapper dataCollectInfoMapper;

	@Autowired
	private CollectBaseConfigService collectBaseConfigService;

	@Override
	public Result<DataCollectInfo> addDataCollectInfo(DataCollectInfo dataCollectInfo){
		if (StringUtils.isEmpty(dataCollectInfo.getName())) {
			return Result.fail("名称不能为空");
		}
		boolean exists = Wrappers.of(mapper)
				.where(DATA_COLLECT_INFO.NAME.eq(dataCollectInfo.getName()))
				.and(DATA_COLLECT_INFO.COLLECT_ID.ne(dataCollectInfo.getCollectId()).when(StringUtils.isNotBlank(dataCollectInfo.getCollectId())))
				.exists();
		if (exists) {
			return Result.fail("名称已存在");
		}
		if (StringUtils.isBlank(dataCollectInfo.getCollectId())) {
			dataCollectInfo.setCollectId(IdUtil.simpleUUID());
		}

		saveOrUpdate(dataCollectInfo);
		return Result.success(dataCollectInfo);
	}

	@Override
	public Result<Page<DataCollectInfo>> getDataCollectInfoList(DataCollectPageParam param){
		Page<DataCollectInfo> page = Wrappers.of(mapper)
				.where(DATA_COLLECT_INFO.NAME.like(param.getName()).when(StringUtils.isNotBlank(param.getName())))
				.and(QueryMethods.findInSet(QueryMethods.column("'" + param.getTag() + "'"), DATA_COLLECT_INFO.TAG).gt(0).when(StringUtils.isNotBlank(param.getTag())))
				.orderBy(DATA_COLLECT_INFO.UPDATE_TIME.desc())
				.page(Page.of(param.getPageNo(), param.getPageSize()));

		// 获取数据集表数量
		List<DataCollectInfo> records = page.getRecords();
		if (CollectionUtil.isNotEmpty(records)) {
			List<String> collectIdList = records.stream().map(DataCollectInfo::getCollectId).distinct().collect(Collectors.toList());
			Map<String, Long> tableNumMap = collectBaseConfigService.getTableNum(collectIdList);

			for (DataCollectInfo record : records) {
				record.setTableNum(tableNumMap.get(record.getCollectId()));
			}

		}
		return Result.success(page);
	}

	@Override
	public Result deleteDataCollectInfo(List<String> idList){
		if (CollectionUtil.isEmpty(idList)) {
			return Result.success();
		}

		 UpdateChain.of(DataCollectInfo.class)
				.set(DATA_COLLECT_INFO.DELETED_FLAG, 1)
				.where(DATA_COLLECT_INFO.COLLECT_ID.in(idList))
				.update();


		List<String> configIdListByCollectId = collectBaseConfigService.getConfigIdListByCollectId(idList);
		if (CollectionUtil.isNotEmpty(configIdListByCollectId)) {
			collectBaseConfigService.deleteCollectBaseConfig(configIdListByCollectId);
		}
		return Result.success();
	}
}