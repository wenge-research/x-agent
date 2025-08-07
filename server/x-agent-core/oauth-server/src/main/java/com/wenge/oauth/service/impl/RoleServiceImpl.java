package com.wenge.oauth.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.dto.param.PermissionGrantParam;
import com.wenge.oauth.dto.param.RolePageParam;
import com.wenge.oauth.dto.param.RoleStatusUpdateParam;
	import com.wenge.oauth.entity.Role;
	import com.wenge.oauth.entity.RoleMenu;
import com.wenge.oauth.enums.OauthPermissionEnum;
import com.wenge.oauth.mapper.RoleMapper;
import com.wenge.oauth.service.MenuService;
import com.wenge.oauth.service.RoleMenuService;
import com.wenge.oauth.service.RoleService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;
import static com.wenge.oauth.entity.table.RoleTableDef.ROLE;
import static com.wenge.oauth.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * Description: 角色表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-01 20:03:04
 *
 */
@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
	/**
	 * 	角色表数据库处理类
	 */
	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private RoleMenuService roleMenuService;

	@Autowired
	private MenuService menuService;

	@Override
	public Result addRoleList(List<Role> roles) {
		log.info("智巡---addRoleList:{}", roles.size());
		return Result.success(saveBatch(roles));
	}

	@Override
	public Result updateRoleList(List<Role> roles) {
		log.info("智巡---updateRoleList:{}", roles.size());
		int res = 0;
		for (Role role : roles) {
			if (StringUtils.isBlank(role.getRoleId())) {
				return Result.fail("角色id不能为空");
			}
			role.setId(null);
			long count = Role.create()
					.where(ROLE.ROLE_ID.eq(role.getRoleId()))
					.count();
			if (count > 0) {
				log.info("角色roleId：{} 已存在，执行更新", role.getRoleId());
				//更新
				QueryWrapper queryWrapper =  QueryWrapper.create();
				queryWrapper.where(ROLE.ROLE_ID.eq(role.getRoleId()));
				res += (update(role, queryWrapper)) ? 1 : 0;
				log.info("更新角色表成功：{}", role.toString());
			}else {
				res += (save(role)) ? 1 : 0;
				log.info("新增角色表成功：{}", role.toString());
			}
		}
		return Result.success(res);
	}

	@Override
	public Result delRoleByRoleId(String roleId) {
		log.info("智巡---delRoleByRoleId:{}", roleId);
		return Result.success(Role.create()
				.where(ROLE.ROLE_ID.eq(roleId))
				.remove());
	}

	@Override
	public Result addRole(Role role){
		if (StringUtils.isBlank(role.getRoleName())) {
			return Result.fail("角色名称不能为空");
		}

		long count = Role.create()
				.where(ROLE.ROLE_CODE.eq(role.getRoleCode()))
				.and(StringUtils.isNotBlank(role.getRoleId()), ROLE.ROLE_ID.ne(role.getRoleId()))
				.count();
		if (count > 0) {
			return Result.fail("角色编码已存在");
		}
		if (StringUtils.isBlank(role.getRoleId())) {
			role.setRoleId(IdUtil.simpleUUID());
		}

		// 设置权限
		PermissionGrantParam param = new PermissionGrantParam();
		param.setRoleId(role.getRoleId());
		param.setMenuId(role.getMenuIdList());
		grantPermission(param);

		saveOrUpdate(role);
		return Result.success();
	}

	@Override
	public Result getRoleList(RolePageParam role) {
		FlexModel<Role> roleFlexModel = Role.create()
				.where(StringUtils.isNotBlank(role.getRoleName()), ROLE.ROLE_NAME.like(role.getRoleName()))
				.and(StringUtils.isNotBlank(role.getStatus()), ROLE.STATUS.eq(role.getStatus()))
				.and(StringUtils.isNotBlank(role.getStartDate()), ROLE.CREATE_TIME.ge(role.getStartDate()))
				.and(StringUtils.isNotBlank(role.getEndDate()), ROLE.CREATE_TIME.le(role.getEndDate()))
				.orderBy(ROLE.CREATE_TIME.desc());

		PermissionControlUtils.buildPermission(roleFlexModel, OauthPermissionEnum.ROLE_TABLE);
		Page<Role> pages = roleFlexModel.pages(Page.of(role.getPageNo(), role.getPageSize()));

		return Result.success(pages);
	}

	@Override
	public Result updateRole(Role role){
		updateById(role);
		return Result.success();
	}

	@Override
	public Result deleteRole(ListStringParam idList){
		if (CollectionUtil.isEmpty(idList.getParam())) {
			return Result.fail("角色id不能为空");
		}
		Role.create()
				.where(ROLE.ROLE_ID.in(idList.getParam()))
				.remove();
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result grantPermission(PermissionGrantParam param) {
		if (StringUtils.isBlank(param.getRoleId())) {
			return Result.fail("角色id不能为空");
		}

		// 移除所有权限
		roleMenuService.removeAllPermission(param.getRoleId());
		String now = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);

		// 重新添加所有指定权限
		List<String> menuIdList = param.getMenuId();
		List<RoleMenu> roleMenuList = menuIdList.stream().map(p ->
						RoleMenu.create()
								.setRoleId(param.getRoleId())
								.setMenuId(p)
								.setCreateTime(now)
								.setUpdateTime(now)
				)
				.collect(Collectors.toList());

		roleMenuService.saveBatch(roleMenuList);
		return Result.success();
	}

	@Override
	public Result<List<Role>> getRole(StringParam userId) {
		if (StringUtils.isBlank(userId.getParam())) {
			return Result.fail("用户id不能为空");
		}

		Wrappers<Object> wrappers = Wrappers.init()
				.select(ROLE.DEFAULT_COLUMNS)
				.innerJoin(USER_ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
				.innerJoin(OAUTH_USER).on(OAUTH_USER.ID.eq(USER_ROLE.USER_ID))
				.where(OAUTH_USER.ID.eq(userId.getParam()))
				.where(ROLE.STATUS.eq("1"));
		List<Role> list = list(wrappers);
		return Result.success(list);
	}

	@Override
	public Result updateStatus(RoleStatusUpdateParam param) {
		if (StringUtils.isBlank(param.getRoleId())) {
			return Result.fail("角色id不能为空");
		}

		Role.create()
				.setStatus(param.getStatus())
				.where(ROLE.ROLE_ID.eq(param.getRoleId()))
				.update();

		return Result.success();
	}

	@Override
	public List<String> findParentIds(String roleId) {
		if (StringUtils.isBlank(roleId)) {
			return Lists.newArrayList();
		}
		// 查询所有角色
		List<Role> list = Role.create()
				.where(ROLE.STATUS.eq("1"))
				.lists();

		// 查询当前角色
		Optional<Role> optionalRole = list.stream().filter(p -> p.getRoleId().equals(roleId)).findFirst();
		if (!optionalRole.isPresent()) {
			return Lists.newArrayList();
		}
		Map<String, Role> roleMap = list.stream().collect(Collectors.toMap(
				Role::getRoleId,
				p -> p,
				(v1, v2) -> v1,
				HashMap::new
		));
		Role role = optionalRole.get();

		// 查询当前角色的所有层级的父级
		List<String> parentIdList = Lists.newArrayList();
		// 使用无限流，直到父级为0
		Stream.iterate(role, current -> {
					Role pRole = roleMap.getOrDefault(current.getPid(), Role.create());
					// 添加父级
					parentIdList.add(pRole.getRoleId());
					return pRole;
				})
				.filter(p -> "0".equals(p.getPid()))
				.findAny();
		return parentIdList;
	}

	@Override
	public Role getRoleById(String roleId) {
		if (StringUtils.isBlank(roleId)) {
			return null;
		}
		List<Role> lists = Role.create()
				.where(ROLE.ROLE_ID.eq(roleId))
				.limit(1)
				.lists();
		return lists.get(0);
	}

	@Override
	public List<Role> getUserRoleByRoleCode(List<String> roleCode) {
		if (CollectionUtil.isEmpty(roleCode)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.where(ROLE.ROLE_CODE.in(roleCode))
				.list();
	}

}