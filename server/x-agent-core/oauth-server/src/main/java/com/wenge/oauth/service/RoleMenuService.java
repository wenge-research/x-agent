package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.RoleMenu;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 角色-菜单关联表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:12:25
 *
 */
public interface RoleMenuService extends IService<RoleMenu> {

    Result addRoleMenu(RoleMenu roleMenu);

    Result getRoleMenuList(RoleMenu roleMenu);

    Result updateRoleMenu(RoleMenu roleMenu);

    Result deleteRoleMenu(List<String> idList);

    boolean removeAllPermission(String roleId);
}