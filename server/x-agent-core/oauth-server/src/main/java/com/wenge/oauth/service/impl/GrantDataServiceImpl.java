package com.wenge.oauth.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.dto.param.GrantDataGetParam;
import com.wenge.oauth.dto.param.GrantDataParam;
import com.wenge.oauth.entity.GrantData;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.Tenant;
import com.wenge.oauth.mapper.GrantDataMapper;
import com.wenge.oauth.service.GrantDataService;
import com.wenge.oauth.service.OauthTenantService;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.oauth.entity.table.GrantDataTableDef.GRANT_DATA;

/**
 * Description: 授权数据服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-08 19:59:43
 *
 */
@Service
@Slf4j
public class GrantDataServiceImpl extends ServiceImpl<GrantDataMapper, GrantData> implements GrantDataService {
	/**
	 * 	授权数据数据库处理类
	 */
	@Autowired
	private GrantDataMapper grantDataMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private OauthTenantService  tenantService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result addGrantData(GrantDataParam param){

		// 先清空
		Wrappers<Object> wrappers = Wrappers.init()
				.where(GRANT_DATA.DATA_ID.eq(param.getDataId()))
				.and(GRANT_DATA.TARGET_TYPE.eq(param.getTargetType()));
		remove(wrappers);

		// 批量插入
		List<String> targetIdList = param.getTargetIdList();
		if (CollectionUtil.isNotEmpty(targetIdList)) {
			List<GrantData> collect = targetIdList.stream().map(p -> {
				GrantData grantData = new GrantData();
				grantData.setDataId(param.getDataId());
				grantData.setDataType(param.getDataType());
				grantData.setRemark(param.getRemark());
				grantData.setTargetId(p);
				grantData.setTargetType(param.getTargetType());
				grantData.setCopyPermission(param.getCopyPermission());
				return grantData;
			}).collect(Collectors.toList());
			saveBatch(collect);
		}

		return Result.success();
	}

	@Override
	public Result getGrantDataList(GrantDataGetParam param){
		if (StringUtils.isBlank(param.getDataId())) {
			return Result.success();
		}

		Wrappers<Object> wrappers = Wrappers.init()
				.where(GRANT_DATA.DATA_ID.eq(param.getDataId()))
				.and(StringUtils.isNotBlank(param.getDataType()), GRANT_DATA.DATA_TYPE.eq(param.getDataType()))
				.and(StringUtils.isNotBlank(param.getTargetType()), GRANT_DATA.TARGET_TYPE.eq(param.getTargetType()));
		List<GrantData> list = list(wrappers);
		if (CollectionUtil.isEmpty(list)) {
			return Result.success(list);
		}

		// 查询用户名称
		List<String> userIds = list.stream()
				.filter(grantData -> "user".equals(grantData.getTargetType()))
				.map(GrantData::getTargetId)
				.collect(Collectors.toList());
		List<OauthUser> users = userService.getUsersByUserIds(userIds);
		Map<Long, String> userMap =  users.stream().collect(Collectors
				.toMap(OauthUser::getId, OauthUser::getUsername));

		// 查询租户名称
		List<String> tenantIds = list.stream()
				.filter(grantData -> "tenant".equals(grantData.getTargetType()))
				.map(GrantData::getTargetId)
				.collect(Collectors.toList());
		List<Tenant> tenants = tenantService.getListByTenantIds(tenantIds);
		Map<String, String> tenantMap =  tenants.stream().collect(Collectors
				.toMap(Tenant::getTenantId, Tenant::getTenantName));


		List<GrantData> newList = list.stream()
				.peek(grantData -> {
					if (grantData.getCopyPermission() == null) {
						grantData.setCopyPermission(0);
					}
					if (grantData.getTargetType().equals("tenant") ) {
						grantData.setTargetName(tenantMap.get(grantData.getTargetId()));
					} else if (grantData.getTargetType().equals("user") ) {
						grantData.setTargetName(userMap.get(Long.parseLong(grantData.getTargetId())));
					}
				})
				.collect(Collectors.toList());
		return Result.success(newList);
	}

	@Override
	public Result updateGrantData(GrantData grantData){
		updateById(grantData);
		return Result.success();
	}

	@Override
	public Result deleteGrantData(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}
}