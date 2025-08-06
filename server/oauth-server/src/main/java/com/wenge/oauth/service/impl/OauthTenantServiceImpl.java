package com.wenge.oauth.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.dto.param.OauthTenantParam;
import com.wenge.oauth.dto.param.TenantUpdateStatusParam;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.Tenant;
import com.wenge.oauth.entity.table.TenantTableDef;
import com.wenge.oauth.enums.OauthPermissionEnum;
import com.wenge.oauth.enums.StatusTypeEnum;
import com.wenge.oauth.mapper.OauthTenantMapper;
import com.wenge.oauth.service.OauthTenantService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.mybatisflex.core.FlexModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;
import static com.wenge.oauth.entity.table.TenantTableDef.TENANT;


/**
 * Description: 租户表服务实现类
 * @Author: author
 * Version: 1.0
 * Create Date Time: 2024-06-28 14:31:45
 *
 */
@Service
@Slf4j
public class OauthTenantServiceImpl extends ServiceImpl<OauthTenantMapper, Tenant> implements OauthTenantService {
	/**
	 * 	租户表数据库处理类
	 */
	@Autowired
	private OauthTenantMapper oauthTenantMapper;

	@Override
	public Result addOauthTenant(Tenant tenant) {
		if (StringUtils.isBlank(tenant.getTenantName())) {
			return Result.fail("名称不能为空");
		}

		long count = Tenant.create()
				.where(TenantTableDef.TENANT.TENANT_NAME.eq(tenant.getTenantName()))
				.and(StringUtils.isNotBlank(tenant.getTenantId()), TenantTableDef.TENANT.TENANT_ID.ne(tenant.getTenantId()))
				.count();
		if (count > 0) {
			return Result.fail("名称已存在");
		}

		if (StringUtils.isBlank(tenant.getLogoUrl())) {
			return Result.fail("LOGO不能为空");
		}
		
		if(StringUtils.isBlank(tenant.getTenantId())) {
			tenant.setTenantId(IdUtil.simpleUUID());
		}
		if (StringUtils.isNotBlank(tenant.getFullId())) {
			tenant.setFullId(tenant.getFullId() + "/" + tenant.getTenantId());
		} else {
			tenant.setFullId(tenant.getTenantId());
		}
		if (StringUtils.isNotBlank(tenant.getFullName())) {
			tenant.setFullName(tenant.getFullName() + "/" + tenant.getTenantName());
		} else {
			tenant.setFullName(tenant.getTenantName());
		}
		tenant.saveOrUpdate();
		return Result.success();
	}

	@Override
	public Result getOauthTenantList(OauthTenantParam param){
		Tenant tenant = Tenant.create()
				.where(StringUtils.isNoneBlank(param.getTenantName()), TENANT.TENANT_NAME.like(param.getTenantName()))
				.where(StringUtils.isNotBlank(param.getEnDate()), TENANT.CREATE_TIME.lt(param.getEnDate()))
				.where(StringUtils.isNotBlank(param.getStartDate()), TENANT.CREATE_TIME.gt(param.getStartDate()))
				.where(StringUtils.isNoneBlank(param.getStatus()), TENANT.STATUS.eq(param.getStatus()))
				.orderBys(TENANT.CREATE_TIME.desc());

		// 根据权限查询
		PermissionControlUtils.buildPermission(tenant, OauthPermissionEnum.TENANT_TABLE);

		Page<Tenant> pages = tenant.pages(Page.of(param.getPageNo(), param.getPageSize()));
		return Result.success(pages);
	}

	@Override
	public Result updateOauthTenant(Tenant tenant){
		updateById(tenant);
		return Result.success();
	}

	@Override
	public Result deleteOauthTenant(ListStringParam idList){
		if (CollectionUtil.isEmpty(idList.getParam())) {
			return Result.fail("ID不能为空");
		}
		Tenant.create()
				.where(TENANT.TENANT_ID.in(idList.getParam()))
				.remove();
		return Result.success();
	}

	@Override
	public Result updateStatus(TenantUpdateStatusParam param) {
		if (StringUtils.isBlank(param.getTenantId())) {
			return Result.fail("租户ID不能为空");
		}

		Tenant.create()
				.setStatus(param.getStatus())
				.where(TENANT.TENANT_ID.eq(param.getTenantId()))
				.update();
		return Result.success();
	}

	@Override
	public String getLogoById(String tenantId) {
		if (StringUtils.isBlank(tenantId)) {
			return StringConstant.BLANK;
		}

		List<Tenant> lists = Tenant.create()
				.where(TENANT.TENANT_ID.eq(tenantId))
				.lists();
		if (CollectionUtil.isNotEmpty(lists)) {
			return lists.get(0).getLogoUrl();
		}
		return StringConstant.BLANK;
	}

	@Override
	public Tenant getDeatail(String tenantId) {
		if (StringUtils.isBlank(tenantId)) {
			return null;
		}

		return QueryChain.of(mapper)
				.where(TENANT.TENANT_ID.eq(tenantId).when(StringUtils.isNoneBlank(tenantId)))
				.limit(1)
				.one();
	}

	@Override
	public List<Tenant> getListByTenantIds(List<String> tenantIds) {

		if (CollectionUtil.isEmpty(tenantIds)) {
			return  Collections.emptyList();
		}
		List<Tenant> tenantList = Tenant.create()
				.where(TENANT.TENANT_ID.in(tenantIds))
				.lists();
		return tenantList ;
	}

	@Override
	public Tenant getTenantByName(String tenantName) {
		Tenant tenant = QueryChain.of(mapper)
				.where(TENANT.TENANT_NAME.eq(tenantName))
				.limit(1)
				.one();
		return tenant;
	}
}