package com.wenge.model.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.WebsiteConfig;
import com.wenge.model.entity.table.WebsiteConfigTableDef;
import com.wenge.model.mapper.WebsiteConfigMapper;
import com.wenge.model.service.WebsiteConfigService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 网址置表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 15:33:14
 *
 */
@Service
@Slf4j
public class WebsiteConfigServiceImpl extends ServiceImpl<WebsiteConfigMapper, WebsiteConfig> implements WebsiteConfigService {
	/**
	 * 	网址置表数据库处理类
	 */
	@Autowired
	private WebsiteConfigMapper websiteConfigMapper;

	@Override
	public Result addWebsiteConfig(WebsiteConfig websiteConfig){
		saveOrUpdate(websiteConfig);
		return Result.success();
	}

	@Override
	public Result getWebsiteConfigList(WebsiteConfig websiteConfig){
		return Result.success(null);
	}

	@Override
	public Result updateWebsiteConfig(WebsiteConfig websiteConfig){
		updateById(websiteConfig);
		return Result.success();
	}

	@Override
	public Result deleteWebsiteConfig(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<WebsiteConfig> getWebConfList(WebsiteConfig param) {
        return WebsiteConfig.create()
				.where(StringUtils.isNotBlank(param.getApplicationId()), WebsiteConfigTableDef.WEBSITE_CONFIG.APPLICATION_ID.eq(param.getWebsiteId()))
				.and(StringUtils.isNotBlank(param.getType()), WebsiteConfigTableDef.WEBSITE_CONFIG.TYPE.eq(param.getType()))
				.lists();
	}
}