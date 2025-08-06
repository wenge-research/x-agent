package com.wenge.oauth.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.UserRole;
import com.wenge.oauth.mapper.UserRoleMapper;
import com.wenge.oauth.service.UserRoleService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.oauth.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * Description: 用户-角色关联表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:12:35
 *
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
	/**
	 * 	用户-角色关联表数据库处理类
	 */
	@Autowired
	private UserRoleMapper userRoleMapper;

	@Override
	public Result addUserRole(UserRole userRole){
		saveOrUpdate(userRole);
		return Result.success();
	}

	@Override
	public Result getUserRoleList(UserRole userRole){
		//LambdaQueryWrapper<UserRole> wrapper = Wrappers.lambdaQuery(UserRole.class);
		//Page<UserRole> page = page(Page.of(userRole.getPageNo(), userRole.getPageSize()), wrapper);
		return Result.success(null);
	}

	@Override
	public Result updateUserRole(UserRole userRole){
		updateById(userRole);
		return Result.success();
	}

	@Override
	public Result deleteUserRole(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public boolean removeAllRole(List<String> userIdList) {
		return UserRole.create()
				.where(USER_ROLE.USER_ID.in(userIdList))
				.remove();
	}
}