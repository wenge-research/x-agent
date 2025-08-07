package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.OauthTenantParam;
import com.wenge.oauth.dto.param.TenantUpdateStatusParam;
import com.wenge.oauth.entity.Tenant;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;

import java.util.List;

/**
 * Description: 租户表服务类
 * @Author: author
 * Version: 1.0
 * Create Date Time: 2024-06-28 14:31:45
 *
 */
public interface OauthTenantService extends IService<Tenant> {

    Result addOauthTenant(Tenant tenant);

    Result getOauthTenantList(OauthTenantParam param);

    Result updateOauthTenant(Tenant tenant);

    Result deleteOauthTenant(ListStringParam idList);

    Result updateStatus(TenantUpdateStatusParam param);

    /**
     * 通过租户id获取logo
     * @param tenantId
     * @return
     */
    String getLogoById(String tenantId);

    /**
     * 获取租户详情
     * @param tenantId
     * @return
     */
    Tenant getDeatail(String tenantId);

    /**
     * 根据租户id获取租户信息
     * @param tenantIds
     * @return
     */
    List<Tenant> getListByTenantIds(List<String> tenantIds);

    /**
     * 根据租户名查询租户信息，如果不存在则创建新的租户
     * @param tenantName
     * @return
     */
    Tenant getTenantByName(String tenantName);
}