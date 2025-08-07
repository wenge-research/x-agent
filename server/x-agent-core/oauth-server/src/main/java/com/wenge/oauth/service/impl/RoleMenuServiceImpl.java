package com.wenge.oauth.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.RoleMenu;
import com.wenge.oauth.mapper.RoleMenuMapper;
import com.wenge.oauth.service.RoleMenuService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.oauth.entity.table.RoleMenuTableDef.ROLE_MENU;

/**
 * Description: 角色-菜单关联表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:12:25
 *
 */
@Service
@Slf4j
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
	/**
	 * 	角色-菜单关联表数据库处理类
	 */
	@Autowired
	private RoleMenuMapper roleMenuMapper;

	@Override
	public Result addRoleMenu(RoleMenu roleMenu){
		saveOrUpdate(roleMenu);
		return Result.success();
	}

	@Override
	public Result getRoleMenuList(RoleMenu roleMenu){
		//LambdaQueryWrapper<RoleMenu> wrapper = Wrappers.lambdaQuery(RoleMenu.class);
		//Page<RoleMenu> page = page(Page.of(roleMenu.getPageNo(), roleMenu.getPageSize()), wrapper);
		return Result.success(null);
	}

	@Override
	public Result updateRoleMenu(RoleMenu roleMenu){
		updateById(roleMenu);
		return Result.success();
	}

	@Override
	public Result deleteRoleMenu(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public boolean removeAllPermission(String roleId) {
		if (StringUtils.isBlank(roleId)) {
			return true;
		}
		return RoleMenu.create()
				.where(ROLE_MENU.ROLE_ID.eq(roleId))
				.remove();
	}
}