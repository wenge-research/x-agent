package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.entity.ApplicationPlugin;
import com.wenge.model.mapper.ApplicationPluginMapper;
import com.wenge.model.service.ApplicationPluginService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wenge.model.entity.table.ApplicationPluginTableDef.APPLICATION_PLUGIN;

/**
 * Description: 应用配置服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-28 10:38:16
 *
 */
@Service
@Slf4j
public class ApplicationPluginServiceImpl extends ServiceImpl<ApplicationPluginMapper, ApplicationPlugin> implements ApplicationPluginService {
	/**
	 * 	应用配置数据库处理类
	 */
	@Autowired
	private ApplicationPluginMapper applicationPluginMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result addApplicationPlugin(ApplicationPlugin param) {
		boolean exists = Wrappers.of(mapper)
				.where(APPLICATION_PLUGIN.PLUGIN_CODE.eq(param.getPluginCode()))
				.and(APPLICATION_PLUGIN.PLUGIN_ID.ne(param.getPluginId()).when(StringUtils.isNotBlank(param.getPluginId())))
				.exists();
		if (exists) {
			return Result.fail("应用插件编码已存在");
		}
		saveOrUpdate(param);
		return Result.success();
	}

	@Override
	public Result<List<ApplicationPlugin>> getApplicationPluginList(AppConfigListParam param){
		List<ApplicationPlugin> list = list();
		return Result.success(list);
	}

	@Override
	public Result updateApplicationPlugin(ApplicationPlugin applicationPlugin){
		updateById(applicationPlugin);
		return Result.success();
	}

	@Override
	public Result deleteApplicationPlugin(List<String> idList){
		if (CollectionUtil.isEmpty(idList)) {
			return Result.success();
		}
		UpdateChain.create(mapper)
				.where(APPLICATION_PLUGIN.PLUGIN_ID.in(idList))
				.remove();
		return Result.success();
	}
}