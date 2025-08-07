package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.PermissionGrantParam;
import com.wenge.oauth.dto.param.RolePageParam;
import com.wenge.oauth.dto.param.RoleStatusUpdateParam;
import com.wenge.oauth.entity.Role;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

/**
 * Description: 角色表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-01 20:03:04
 *
 */
public interface RoleService extends IService<Role> {

    Result addRoleList(List<Role> roles);

    Result updateRoleList(List<Role> roles);

    Result delRoleByRoleId(String roleId);

    Result addRole(Role role);

    Result getRoleList(RolePageParam role);

    Result updateRole(Role role);

    Result deleteRole(ListStringParam idList);

    Result grantPermission(PermissionGrantParam param);

    Result<List<Role>> getRole(StringParam userId);

    Result updateStatus(RoleStatusUpdateParam param);

    List<String> findParentIds(String roleId);

    Role getRoleById(String roleId);

    /**
     * 根据角色code查询角色
     */
    List<Role> getUserRoleByRoleCode(List<String> roleCode);
}