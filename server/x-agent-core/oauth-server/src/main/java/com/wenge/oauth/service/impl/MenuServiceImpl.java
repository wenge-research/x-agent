package com.wenge.oauth.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.dto.param.MenuHiddenParam;
import com.wenge.oauth.dto.param.MenuPageParam;
import com.wenge.oauth.dto.param.MenuUpdateStatusParam;
import com.wenge.oauth.entity.Menu;
import com.wenge.oauth.entity.Role;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.MenuMapper;
import com.wenge.oauth.service.MenuService;
import com.wenge.oauth.service.RoleService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.oauth.entity.table.MenuTableDef.MENU;
import static com.wenge.oauth.entity.table.RoleMenuTableDef.ROLE_MENU;
import static com.wenge.oauth.entity.table.RoleTableDef.ROLE;

/**
 * Description: 菜单表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:03:36
 *
 */
@Service
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
	/**
	 * 	菜单表数据库处理类
	 */
	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private RoleService roleService;

	@Override
	public Result addMenuList(List<Menu> menus) {
		log.info("智巡---addMenuList:{}", menus.size());
		return Result.success(saveBatch(menus));
	}

	@Override
	public Result updateMenuList(List<Menu> menus) {
		log.info("智巡---updateMenuList:{}", menus.size());
		int res = 0;
		for (Menu menu : menus) {
			if (StringUtils.isBlank(menu.getMenuId())) {
				return Result.fail("菜单id不能为空");
			}
			menu.setId(null);
			long count = Menu.create()
					.where(MENU.MENU_ID.eq(menu.getMenuId()))
					.count();
			if (count > 0) {
				log.info("菜单menuId：{} 已存在，执行更新", menu.getMenuId());
				//更新
				QueryWrapper queryWrapper =  QueryWrapper.create();
				queryWrapper.where(MENU.MENU_ID.eq(menu.getMenuId()));
				res += (update(menu, queryWrapper)) ? 1 : 0;
				log.info("更新菜单表成功：{}", menu.toString());
			}else {
				res += (save(menu)) ? 1 : 0;
				log.info("新增菜单表成功：{}", menu.toString());
			}

		}
		return Result.success(res);
	}

	@Override
	public Result delMenuByMenuId(String menuId) {
		log.info("智巡---delMenuByMenuId:{}", menuId);
		return Result.success(Menu.create()
				.where(MENU.MENU_ID.eq(menuId))
				.remove());
	}

	@Override
	public Result addMenu(Menu menu) {
		if (StringUtils.isBlank(menu.getMenuCode())) {
			return Result.fail("菜单编码不能为空");
		}

		long count = Menu.create()
				.where(MENU.MENU_CODE.eq(menu.getMenuCode()))
				.and(StringUtils.isNotBlank(menu.getMenuId()), MENU.MENU_ID.ne(menu.getMenuId()))
				.count();
		if (count > 0) {
			return Result.fail("菜单编码已存在");
		}

		if (StringUtils.isNotBlank(menu.getPid())) {
			if (menu.getPid().equals(menu.getMenuId())) {
				return Result.fail("父级不能是本身");
			}
		}

		if (StringUtils.isBlank(menu.getMenuId())) {
			menu.setMenuId(IdUtil.simpleUUID());
		}

		saveOrUpdate(menu);
		return Result.success();
	}

	@Override
	public Result getMenuList(MenuPageParam param){
		Page<Menu> pages = Menu.create()
				.where(StringUtils.isNotBlank(param.getStatus()), MENU.STATUS.eq(param.getStatus()))
				.and(StringUtils.isNotBlank(param.getMenuType()), MENU.MENU_TYPE.eq(param.getMenuType()))
				.and(StringUtils.isNotBlank(param.getMenuName()), MENU.MENU_NAME.like(param.getMenuName()))
				.and(StringUtils.isNotBlank(param.getStartDate()), MENU.CREATE_TIME.ge(param.getStartDate() + " 00:00:00"))
				.and(StringUtils.isNotBlank(param.getEndDate()), MENU.CREATE_TIME.le(param.getEndDate() + " 23:59:59"))
				.orderBy(MENU.CREATE_TIME.desc())
				.pages(Page.of(param.getPageNo(), param.getPageSize()));
		return Result.success(pages);
	}

	@Override
	public Result updateMenu(Menu menu){
		updateById(menu);
		return Result.success();
	}

	@Override
	public Result deleteMenu(List<String> idList){
		if (CollectionUtil.isNotEmpty(idList)) {
			Menu.create()
					.where(MENU.MENU_ID.in(idList))
					.remove();
		}

		return Result.success();
	}

	@Override
	public Result<List<Menu>> getPermission(StringParam roleId) {
		if (StringUtils.isBlank(roleId.getParam())) {
			return Result.success(Lists.newArrayList());
		}

		Role role = roleService.getRoleById(roleId.getParam());

		// 通过角色id获取菜单
		List<Menu> menuByRoleIds = getMenuByRoleIds(ListUtil.toList(role));

		return Result.success(menuByRoleIds);
	}

	@Override
	public Result<List<Menu>> getUserPermission(StringParam userId) {
		List<Menu> menuByUserId = getMenuByUserId(userId);
		return Result.success(menuByUserId);
	}

	@Override
	public Result<List<Menu>> getAllMenuTree(EmptyParam param) {
		// 根据权限查询
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		// 非系统管理员，则只检索当前用户对应的权限菜单
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
			StringParam userIdParam = new StringParam();
			userIdParam.setParam(tokenOauthUserInfo.getId() + "");
			List<Menu> menuByUserId = getMenuByUserId(userIdParam);
			return Result.success(menuByUserId);
		}

		List<Menu> lists = Menu.create()
				.where(MENU.STATUS.eq(1))
				.lists();
		List<Menu> menus = buildChildren(lists);
		return Result.success(menus);
	}

	@Override
	public Result<List<String>> getMenuIdListByRole(StringParam roleId) {
		if (StringUtils.isBlank(roleId.getParam())) {
			return Result.success(Lists.newArrayList());
		}

		// 根据角色获取权限菜单
		Result<List<Menu>> permissionResult = getPermission(roleId);
		List<Menu> menuList = permissionResult.getData();
		if (CollectionUtil.isEmpty(menuList)) {
			return Result.success(Lists.newArrayList());
		}

		// 收集权限菜单id
		List<String> menuIdList = Lists.newArrayList();
		menuIdList.addAll(menuList.stream().map(Menu::getMenuId).distinct().collect(Collectors.toList()));

		// 递归获取子级菜单
		Stream.iterate(menuList, currentList -> {
					List<Menu> children = Lists.newArrayList();
					List<Menu> menus = currentList.stream().flatMap(p ->{
						if (CollectionUtil.isNotEmpty(p.getChildren())) {
							return p.getChildren().stream();
						}
						return children.stream();
					}).collect(Collectors.toList());
					// 添加子级菜单id
					menuIdList.addAll(menus.stream().map(Menu::getMenuId).distinct().collect(Collectors.toList()));
					return menus;
				})
				// 菜单为空，则结束递归
				.filter(List::isEmpty)
				.findAny();

		return Result.success(menuIdList);
	}

	@Override
	public Result updateStatus(MenuUpdateStatusParam param) {
		if (StringUtils.isNotBlank(param.getMenuId())) {
			Menu.create()
					.setStatus(param.getStatus())
					.where(MENU.MENU_ID.eq(param.getMenuId()))
					.update();
		}
		return Result.success();
	}

	@Override
	public Result updateHidden(MenuHiddenParam param) {
		if (StringUtils.isNotBlank(param.getMenuId())) {
			Menu.create()
					.setIsHidden(param.getIsHidden())
					.where(MENU.MENU_ID.eq(param.getMenuId()))
					.update();
		}
		return Result.success();
	}

	@Override
	public List<Menu> getAdminPermission() {
		// 通过用户id检索菜单权限
		Wrappers wrappers = Wrappers.init()
				.select(MENU.DEFAULT_COLUMNS)
				.and(MENU.STATUS.eq(1));

		List<Menu> list = list(wrappers);
		return buildChildren(list);
	}

	/**
	 * 初始化子级菜单，封装树形结构
	 * @param list
	 * @return
	 */
	private List<Menu> buildChildren(List<Menu> list) {
		// 去重菜单
		list = list.stream().distinct().collect(Collectors.toList());
		Map<String, Menu> menuHashMap = list.stream().collect(Collectors.toMap(
				Menu::getMenuId,
				p -> p,
				(k1, k2) -> k1,
				Maps::newHashMap
		));
		Map<String, List<Menu>> groupByPid = menuHashMap.values().stream().collect(Collectors.groupingBy(Menu::getPid,
				Collectors.collectingAndThen(Collectors.toList(), lists -> {
					// 排序
					return lists.stream().sorted((k1, k2)->{
						if (null == k1.getOrderNo()) {
							k1.setOrderNo(NumberConstants.ZERO);
						}
						if (null == k2.getOrderNo()) {
							k2.setOrderNo(NumberConstants.ZERO);
						}
						return k1.getOrderNo() - k2.getOrderNo();
					}).collect(Collectors.toList());
				})));

		list.forEach(item -> item.setChildren(groupByPid.get(item.getMenuId())));
		return list.stream()
				.filter(p -> p.getPid().equals("0"))
				.sorted(Comparator.comparingInt(Menu::getOrderNo))
				.collect(Collectors.toList());
	}

	/**
	 * 通过角色id获取菜单
	 * @param roleId
	 * @return
	 */
	private List<Menu> getMenuByRoleId(String roleId) {
		StringParam userIdParam = new StringParam();
		userIdParam.setParam(roleId);
		Result<List<Role>> roleResult = roleService.getRole(userIdParam);
		List<Role> roles = roleResult.getData();
		if (CollectionUtil.isEmpty(roles)) {
			return Lists.newArrayList();
		}
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roles) {
			roleIdList.add(role.getRoleId());
			roleIdList.addAll(roleService.findParentIds(role.getRoleId()));
		}

		// 通过用户id检索菜单权限
		Wrappers wrappers = Wrappers.init()
				.select(MENU.DEFAULT_COLUMNS)
				.innerJoin(ROLE_MENU).on(ROLE_MENU.MENU_ID.eq(MENU.MENU_ID))
				.innerJoin(ROLE).on(ROLE_MENU.ROLE_ID.eq(ROLE.ROLE_ID))
				.where(ROLE_MENU.ROLE_ID.in(roleIdList))
				.and(MENU.STATUS.eq(1))
				.and(ROLE.STATUS.eq(1));

		List<Menu> list = list(wrappers);
		if (CollectionUtil.isNotEmpty(list)) {
			return buildChildren(list);
		}
		return Lists.newArrayList();
	}

	/**
	 * 通过角色id获取菜单
	 * @param roleList
	 * @return
	 */
	private List<Menu> getMenuByRoleIds(List<Role> roleList) {
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getRoleId());
			roleIdList.addAll(roleService.findParentIds(role.getRoleId()));
		}

		// 通过用户id检索菜单权限
		Wrappers wrappers = Wrappers.init()
				.select(MENU.DEFAULT_COLUMNS)
				.innerJoin(ROLE_MENU).on(ROLE_MENU.MENU_ID.eq(MENU.MENU_ID))
				.innerJoin(ROLE).on(ROLE_MENU.ROLE_ID.eq(ROLE.ROLE_ID))
				.where(ROLE_MENU.ROLE_ID.in(roleIdList))
				.and(MENU.STATUS.eq(1))
				.and(ROLE.STATUS.eq(1));

		List<Menu> list = list(wrappers);
		if (CollectionUtil.isNotEmpty(list)) {
			return buildChildren(list);
		}
		return Lists.newArrayList();
	}

	/**
	 * 通过用户id获取菜单
	 * @param userId
	 * @return
	 */
	private List<Menu> getMenuByUserId(StringParam userId) {
		Result<List<Role>> roleResult = roleService.getRole(userId);
		List<Role> roles = roleResult.getData();
		if (CollectionUtil.isEmpty(roles)) {
			return Lists.newArrayList();
		}
        return getMenuByRoleIds(roles);
	}

}