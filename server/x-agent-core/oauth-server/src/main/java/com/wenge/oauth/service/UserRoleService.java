package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.UserRole;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 用户-角色关联表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:12:35
 *
 */
public interface UserRoleService extends IService<UserRole> {

    Result addUserRole(UserRole userRole);

    Result getUserRoleList(UserRole userRole);

    Result updateUserRole(UserRole userRole);

    Result deleteUserRole(List<String> idList);

    boolean removeAllRole(List<String> userIdList);
}