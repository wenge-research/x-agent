package com.wenge.model.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.SmartPromptConfig;
import com.wenge.model.mapper.SmartPromptConfigMapper;
import com.wenge.model.service.SmartPromptConfigService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.SmartPromptConfigTableDef.SMART_PROMPT_CONFIG;

/**
 * Description: 文档提示词配置表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-16 17:26:10
 *
 */
@Service
@Slf4j
public class SmartPromptConfigServiceImpl extends ServiceImpl<SmartPromptConfigMapper, SmartPromptConfig> implements SmartPromptConfigService {
	/**
	 * 	文档提示词配置表数据库处理类
	 */
	@Autowired
	private SmartPromptConfigMapper smartPromptConfigMapper;

	@Override
	public Result addSmartPromptConfig(SmartPromptConfig smartPromptConfig){
		save(smartPromptConfig);
		return Result.success();
	}

	@Override
	public Result getSmartPromptConfigList(SmartPromptConfig smartPromptConfig){
		String appId = smartPromptConfig.getApplicationId();
		String module = smartPromptConfig.getModule();
		return Result.success(Wrappers.of(smartPromptConfigMapper)
				.where(StringUtils.isNotBlank(appId), SMART_PROMPT_CONFIG.APPLICATION_ID.eq(appId))
				.and(StringUtils.isNotBlank(module), SMART_PROMPT_CONFIG.MODULE.eq(module))
				.orderBy(SMART_PROMPT_CONFIG.CREATE_TIME.desc())
				.list());
	}

	@Override
	public Result updateSmartPromptConfig(SmartPromptConfig smartPromptConfig){
		updateById(smartPromptConfig);
		return Result.success();
	}

	@Override
	public Result deleteSmartPromptConfig(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<SmartPromptConfig> getPromptConfigByAppId(String appId, String module) {
		if (StringUtils.isBlank(appId)) {
			return Lists.newArrayList();
		}
        return Wrappers.of(smartPromptConfigMapper)
                .where(StringUtils.isNotBlank(appId), SMART_PROMPT_CONFIG.APPLICATION_ID.eq(appId))
                .and(StringUtils.isNotBlank(module), SMART_PROMPT_CONFIG.MODULE.eq(module))
                .list();
	}

	@Override
	public void updateSmartPromptConfigByAppId(SmartPromptConfig smartPromptConfig) {
		update(smartPromptConfig, Wrappers.of(smartPromptConfigMapper)
			.where(SMART_PROMPT_CONFIG.APPLICATION_ID.eq(smartPromptConfig.getApplicationId()))
			.and(StringUtils.isNotBlank(smartPromptConfig.getModule()), SMART_PROMPT_CONFIG.MODULE.eq(smartPromptConfig.getModule())));
	}

}