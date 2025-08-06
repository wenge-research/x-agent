package com.wenge.oauth.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.DeptStreetByTenantParam;
import com.wenge.oauth.dto.param.OauthDeptParam;
import com.wenge.oauth.dto.result.OauthDeptResult;
import com.wenge.oauth.entity.OauthDept;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 部门信息服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-25 10:27:28
 *
 */
public interface OauthDeptService extends IService<OauthDept> {

    Result addOauthDeptList(List<OauthDept> oauthDepts);

    Result updateOauthDeptList(List<OauthDept> oauthDepts);

    Result delOauthDeptByDeptId(String deptId);

    Result addOauthDept(OauthDept oauthDept);

    Result<Page<OauthDept>> getOauthDeptList(OauthDeptParam oauthDept);

    Result updateOauthDept(OauthDept oauthDept);

    Result deleteOauthDept(List<String> idList);

    Result<List<OauthDeptResult>> getDeptStreet(OauthDeptParam oauthDept);

    /**
     * 查询子级部门的id
     * @param deptId
     * @return
     */
    List<String> getAllChildrenId(String deptId);

    OauthDept getDeptDetail(String deptId);

    Result<List<OauthDeptResult>> getDeptStreetByTenant(DeptStreetByTenantParam oauthDept);

    List<OauthDept> getAllDept();

    /**
     * 根据部门名称查询部门信息
     * @param deptName
     * @return
     */
    OauthDept getDeptByDeptName(String deptName);
}