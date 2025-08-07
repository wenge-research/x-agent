package com.wenge.oauth.service.impl;


import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.dto.param.ApplicationConfigurationParam;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.ApplicationConfigurationMapper;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.oauth.entity.table.ApplicationConfigurationTableDef.APPLICATION_CONFIGURATION;


/**
 * @author: caohaifeng
 * @date: 2024/8/30 11:39
 * @Description: 应用nacos配置服务实现类
 * @Version:1.0
 **/
@Service
@Slf4j
public class ApplicationConfigurationServiceImpl extends ServiceImpl<ApplicationConfigurationMapper, ApplicationConfiguration> implements ApplicationConfigurationService {

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Result addApplicationConfiguration(ApplicationConfiguration param) {
		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		if (StringUtils.isBlank(param.getApplicationId())) {
			return Result.fail("应用ID不能为空");
		}
		if (StringUtils.isBlank(param.getKeyInfo())) {
			return Result.fail("KEY不能为空");
		}
		if (StringUtils.isBlank(param.getValueInfo())) {
			return Result.fail("VALUE不能为空");
		}

		//查重
		if (checkRepeat(param)) {
			return Result.fail("当前KEY已存在");
		}
		Date date = new Date();
		param.setCreateTime(sdf.format(date));
		param.setUpdateTime(sdf.format(date));
		param.setStatus(0);
		param.setUpdateUserId(tokenUserInfo.getId() + "");
		if (save(param)) {
			//写入缓存
			AppConfigContant.setConfiguration(param.getApplicationId() + "@" + param.getKeyInfo().trim(), param.getValueInfo().trim());
		}

		return Result.success();
	}

	//查重
	private boolean checkRepeat(ApplicationConfiguration applicationConfiguration) {
		applicationConfiguration.getKeyInfo();
		QueryWrapper queryWrapper = Wrappers.init()
				.where(APPLICATION_CONFIGURATION.KEY_INFO.eq(applicationConfiguration.getKeyInfo()))
				.and(APPLICATION_CONFIGURATION.APPLICATION_ID.eq(applicationConfiguration.getApplicationId()))
				.and(APPLICATION_CONFIGURATION.STATUS.eq(0));
		if (applicationConfiguration.getId() != null) {
			queryWrapper.and(APPLICATION_CONFIGURATION.ID.ne(applicationConfiguration.getId()));
		}
		if (null != getOne(queryWrapper)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}


	@Override
	public Result<Page<ApplicationConfiguration>> getApplicationConfigurationList(ApplicationConfigurationParam param){
		Wrappers wrappers = Wrappers.init()
				.where(APPLICATION_CONFIGURATION.STATUS.eq(0));
		if (param.getApplicationId() != null) {
			wrappers.and(APPLICATION_CONFIGURATION.APPLICATION_ID.eq(param.getApplicationId()));
		}
		wrappers.and(APPLICATION_CONFIGURATION.STATUS.eq(0))
				.and(APPLICATION_CONFIGURATION.KEY_INFO.like(param.getKeyword())
						.or(APPLICATION_CONFIGURATION.VALUE_INFO.like(param.getKeyword()))
						.or(APPLICATION_CONFIGURATION.REMARK.like(param.getKeyword()))
				);
		wrappers.orderBys(APPLICATION_CONFIGURATION.KEY_INFO.asc()).orderBys(APPLICATION_CONFIGURATION.CREATE_TIME.asc());
		Page<ApplicationConfiguration> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
		return Result.success(page);
	}

	@Override
	public Result updateApplicationConfiguration(ApplicationConfiguration param) {
		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		if (StringUtils.isBlank(param.getApplicationId())) {
			return Result.fail("应用ID不能为空");
		}
		if (StringUtils.isBlank(param.getKeyInfo())) {
			return Result.fail("KEY不能为空");
		}
		if (StringUtils.isBlank(param.getValueInfo())) {
			return Result.fail("VALUE不能为空");
		}
		//查重
		if (checkRepeat(param)) {
			return Result.fail("当前KEY已存在");
		}
		Date date = new Date();
		param.setUpdateTime(sdf.format(date));
		param.setUpdateUserId(tokenUserInfo.getId() + "");
		if (updateById(param)) {
			//更新缓存
			AppConfigContant.setConfiguration(param.getApplicationId() + "@" + param.getKeyInfo().trim(), param.getValueInfo().trim());
		}
		return Result.success();
	}

	@Override
	public Result deleteApplicationConfiguration(ApplicationConfigurationParam applicationConfigurationParam){
		if (CollectionUtils.isEmpty(applicationConfigurationParam.getDelIds())) {
			return Result.fail("需要删除的参数delIds不能为空");
		}
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.setStatus(1);
		applicationConfiguration.setUpdateTime(sdf.format(new Date()));
		applicationConfiguration.setUpdateUserId(tokenUserInfo.getId() + "");
		QueryWrapper queryWrapper = Wrappers.init()
				.where(APPLICATION_CONFIGURATION.ID.in(applicationConfigurationParam.getDelIds()));
		if (update(applicationConfiguration, queryWrapper)) {
			return Result.success("删除成功");
		}
		return Result.fail("删除失败");
	}

	@Override
	public List<ApplicationConfiguration> getConfigByAppId(String appId) {
		if (StringUtils.isBlank(appId)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.where(APPLICATION_CONFIGURATION.APPLICATION_ID.eq(appId))
				.list();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAppConfig(List<ApplicationConfiguration> configurationList, String appId) {
		if (StringUtils.isBlank(appId)) {
			return;
		}
		Wrappers<ApplicationConfiguration> where = Wrappers.of(mapper)
				.where(APPLICATION_CONFIGURATION.APPLICATION_ID.eq(appId));
		// 删除
		remove(where);
		if (CollectionUtils.isNotEmpty(configurationList)) {
			// 添加
			saveBatch(configurationList);
		}
		// 刷新缓存
		AppConfigContant.setConfiguration("1", "1");
	}

	@Override
	public Map<String, List<ApplicationConfiguration>> getConfigByAppIds(List<String> appIds) {
		if (CollectionUtils.isEmpty(appIds)) {
			return Maps.newHashMap();
		}

		List<ApplicationConfiguration> list = Wrappers.of(mapper)
				.where(APPLICATION_CONFIGURATION.APPLICATION_ID.in(appIds))
				.list();
		if (CollectionUtils.isNotEmpty(list)) {
			return list.stream().collect(Collectors.groupingBy(ApplicationConfiguration::getApplicationId));
		}
		return Maps.newHashMap();
	}

}