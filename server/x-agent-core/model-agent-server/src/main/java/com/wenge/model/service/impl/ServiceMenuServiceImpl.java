package com.wenge.model.service.impl;


import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ServiceMenuPageParam;
import com.wenge.model.entity.ServiceMenu;
import com.wenge.model.mapper.ServiceMenuMapper;
import com.wenge.model.service.ServiceMenuService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ServiceMenuTableDef.SERVICE_MENU;

/**
 * Description: 服务菜单服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-05 11:34:24
 *
 */
@Service
@Slf4j
public class ServiceMenuServiceImpl extends ServiceImpl<ServiceMenuMapper, ServiceMenu> implements ServiceMenuService {
	/**
	 * 	服务菜单数据库处理类
	 */
	@Autowired
	private ServiceMenuMapper serviceMenuMapper;

	@Override
	public Result addServiceMenu(ServiceMenu serviceMenu){
		saveOrUpdate(serviceMenu);
		return Result.success();
	}

	@Override
	public Result getServiceMenuList(ServiceMenuPageParam param){
		Page<ServiceMenu> pages = ServiceMenu.create()
				.where(SERVICE_MENU.SERVICE_TYPE.eq(param.getServiceType()))
				.and(SERVICE_MENU.TAG_NAME.like(param.getTagName()))
				.and(SERVICE_MENU.MENU_STATUS.eq(param.getStatus()))
				.and(SERVICE_MENU.APPLICATION_ID.eq(param.getApplicationId()))
				.and(SERVICE_MENU.MENU_NAME.like(param.getMenuName()))
				.and(SERVICE_MENU.SERVICE_TYPE.eq(param.getServiceType()).when(StringUtils.isNotBlank(param.getServiceType())))
				.orderBy(SERVICE_MENU.MENU_SORTED.asc())
				.pages(Page.of(param.getPageNo(), param.getPageSize()));

		return Result.success(pages);
	}

	@Override
	public Result updateServiceMenu(ServiceMenu serviceMenu){
		updateById(serviceMenu);
		return Result.success();
	}

	@Override
	public Result deleteServiceMenu(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public Result<List<String>> getServiceTypeList(ServiceMenuPageParam param) {
		List<ServiceMenu> lists = ServiceMenu.create()
				.select(SERVICE_MENU.SERVICE_TYPE)
				.where(SERVICE_MENU.APPLICATION_ID.eq(param.getApplicationId()))
				.and(SERVICE_MENU.MENU_STATUS.eq("1"))
				.lists();
		List<String> types = lists.stream().map(ServiceMenu::getServiceType).distinct().collect(Collectors.toList());
		return Result.success(types);
	}

	@Override
	public List<ServiceMenu> getServiceByAppId(String appId) {
		if (StringUtils.isBlank(appId)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.where(SERVICE_MENU.APPLICATION_ID.eq(appId))
				.and(SERVICE_MENU.MENU_STATUS.eq(NumberConstants.ONE))
				.and(SERVICE_MENU.MENU_URL.isNotNull())
				.and(SERVICE_MENU.MENU_URL.ne(StringConstant.BLANK))
				.list();
	}

	@Override
	public List<ServiceMenu> getGuideByAppId(String appId, String type) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(type)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.where(SERVICE_MENU.APPLICATION_ID.eq(appId))
				.and(SERVICE_MENU.MENU_STATUS.eq(NumberConstants.ONE))
				.and(SERVICE_MENU.SERVICE_CODE.eq(type))
				.and(SERVICE_MENU.MENU_URL.isNotNull())
				.and(SERVICE_MENU.MENU_URL.ne(StringConstant.BLANK))
				.list();
	}
}