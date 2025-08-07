package com.wenge.oauth.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.dto.param.DeptStreetByTenantParam;
import com.wenge.oauth.dto.param.OauthDeptParam;
import com.wenge.oauth.dto.result.OauthDeptResult;
import com.wenge.oauth.entity.Menu;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.OauthPermissionEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.OauthDeptMapper;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.FlexModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.oauth.entity.table.MenuTableDef.MENU;
import static com.wenge.oauth.entity.table.OauthDeptTableDef.OAUTH_DEPT;

/**
 * Description: 部门信息服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-25 10:27:28
 *
 */
@Service
@Slf4j
public class OauthDeptServiceImpl extends ServiceImpl<OauthDeptMapper, OauthDept> implements OauthDeptService {
	/**
	 * 	部门信息数据库处理类
	 */
	@Autowired
	private OauthDeptMapper oauthDeptMapper;

	@Override
	public Result addOauthDeptList(List<OauthDept> oauthDepts) {
		log.info("智巡---addOauthDeptList:{}", oauthDepts.size());
		return Result.success(saveBatch(oauthDepts));
	}

	@Override
	public Result updateOauthDeptList(List<OauthDept> oauthDepts) {
		log.info("智巡---updateOauthDeptList:{}", oauthDepts.size());
		int res = 0;
		for (OauthDept oathDept : oauthDepts) {
			if (StringUtils.isBlank(oathDept.getDeptId())) {
				return Result.fail("部门id不能为空");
			}
			oathDept.setId(null);
			long count = Menu.create()
					.where(OAUTH_DEPT.DEPT_ID.eq(oathDept.getDeptId()))
					.count();
			if (count > 0) {
				log.info("部门deptId：{} 已存在，执行更新", oathDept.getDeptId());
				//更新
				QueryWrapper queryWrapper =  QueryWrapper.create();
				queryWrapper.where(OAUTH_DEPT.DEPT_ID.eq(oathDept.getDeptId()));
				res += (update(oathDept, queryWrapper)) ? 1 : 0;
				log.info("更新部门表成功：{}", oathDept.toString());
			}else {
				res += (save(oathDept)) ? 1 : 0;
				log.info("新增部门表成功：{}", oathDept.toString());
			}

		}
		return Result.success(res);
	}

	@Override
	public Result delOauthDeptByDeptId(String deptId) {
		log.info("智巡---delOauthDeptByDeptId:{}", deptId);
		return Result.success(OauthDept.create()
				.where(OAUTH_DEPT.DEPT_ID.eq(deptId))
				.remove());
	}

	@Override
	public Result addOauthDept(OauthDept oauthDept){
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		long count = OauthDept.create()
				.where(OAUTH_DEPT.DEPT_NAME.eq(oauthDept.getDeptName()))
				.and(StringUtils.isNotBlank(oauthDept.getDeptId()), OAUTH_DEPT.DEPT_ID.ne(oauthDept.getDeptId()))
				.and(StringUtils.isNotBlank(tokenUserInfo.getTenantId()), OAUTH_DEPT.TENANT_ID.ne(tokenUserInfo.getTenantId()))
				.count();
		if (count > 0) {
			return Result.fail("部门名称已存在或该部门名称已存在当前租户中");
		}
		// 获取当前用户信息, 获取租户id
		if (StringUtils.isBlank(oauthDept.getTenantId())) {
			oauthDept.setTenantId(tokenUserInfo.getTenantId());
		}
		String parentCode = StringConstant.BLANK;
		if (StringUtils.isNotBlank(oauthDept.getPid())) {
			List<OauthDept> lists = OauthDept.create()
					.where(OAUTH_DEPT.DEPT_ID.eq(oauthDept.getPid()))
					.limit(1)
					.lists();
			if (CollectionUtils.isNotEmpty(lists)) {
				parentCode = lists.get(0).getDeptCode();
			}
		}

		if (StringUtils.isBlank(oauthDept.getDeptCode())) {
			oauthDept.setDeptCode(parentCode + "/" + RandomUtil.randomString(6).toUpperCase());
		}

		if (StringUtils.isBlank(oauthDept.getDeptId())) {
			oauthDept.setDeptId(IdUtil.simpleUUID());
		}

		saveOrUpdate(oauthDept);
		return Result.success();
	}

	@Override
	public Result<Page<OauthDept>> getOauthDeptList(OauthDeptParam oauthDept) {
		FlexModel<OauthDept> oauthDeptFlexModel = OauthDept.create()
				.where(StringUtils.isNotBlank(oauthDept.getDeptName()), OAUTH_DEPT.DEPT_NAME.like(oauthDept.getDeptName()))
				.orderBy(OAUTH_DEPT.DEPT_NAME.desc());

		// 根据权限查询
		PermissionControlUtils.buildPermission(oauthDeptFlexModel, OauthPermissionEnum.DEPT);

		Page<OauthDept> pages = oauthDeptFlexModel.pages(Page.of(oauthDept.getPageNo(), oauthDept.getPageSize()));
		return Result.success(pages);
	}

	@Override
	public Result updateOauthDept(OauthDept oauthDept){
		updateById(oauthDept);
		return Result.success();
	}

	@Override
	public Result deleteOauthDept(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public Result<List<OauthDeptResult>> getDeptStreet(OauthDeptParam oauthDept) {
		OauthDept oauthDeptWrapper = OauthDept.create()
				.where(StringUtils.isNotBlank(oauthDept.getTenantId()), OAUTH_DEPT.TENANT_ID.eq(oauthDept.getTenantId()));
		PermissionControlUtils.buildPermission(oauthDeptWrapper, OauthPermissionEnum.DEPT);

		List<OauthDept> lists = oauthDeptWrapper.lists();
		if (CollectionUtils.isNotEmpty(lists)) {
			List<OauthDeptResult> deptResultList = lists.stream().map(p -> BeanUtil.toBean(p, OauthDeptResult.class)).collect(Collectors.toList());

			Map<String, List<OauthDeptResult>> groupByPid = deptResultList.stream().collect(Collectors.groupingBy(OauthDeptResult::getPid));
			deptResultList.forEach(item->{
				item.setChildren(groupByPid.getOrDefault(item.getDeptId(), Lists.newArrayList()));
			});
			List<OauthDeptResult> collect = deptResultList.stream()
					.filter(p -> "0".equals(p.getPid()))
					.collect(Collectors.toList());
			return Result.success(collect);
		}
		return Result.success(Lists.newArrayList());
	}

	@Override
	public List<String> getAllChildrenId(String deptId) {
		if (StringUtils.isBlank(deptId)) {
			return Lists.newArrayList();
		}

		OauthDept oauthDept = OauthDept.create();
		PermissionControlUtils.buildPermission(oauthDept, OauthPermissionEnum.DEPT);

		List<OauthDept> list = oauthDept.lists();
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		List<OauthDept> depts = list.stream().filter(p -> p.getDeptId().equals(deptId)).collect(Collectors.toList());
		Map<String, List<OauthDept>> groupPid = list.stream().collect(Collectors.groupingBy(OauthDept::getPid));
		List<String> idList = Lists.newArrayList();
		idList.addAll(depts.stream().map(OauthDept::getDeptId).collect(Collectors.toList()));
		Stream.iterate(depts, current -> {
					List<OauthDept> deptList = current.stream().flatMap(p -> {
								List<OauthDept> depts1 = groupPid.getOrDefault(p.getDeptId(), Lists.newArrayList());
								return depts1.stream();
							})
							.collect(Collectors.toList());
					idList.addAll(deptList.stream().map(OauthDept::getDeptId).collect(Collectors.toList()));
					return deptList;
				})
				.filter(List::isEmpty)
				.findAny();

		return idList;
	}

	@Override
	public OauthDept getDeptDetail(String deptId) {
		if (StringUtils.isBlank(deptId)) {
			return null;
		}

		List<OauthDept> lists = OauthDept.create()
				.where(OAUTH_DEPT.DEPT_ID.eq(deptId))
				.limit(1)
				.lists();
		if (CollectionUtils.isEmpty(lists)) {
			return null;
		}
		return lists.get(0);
	}

	@Override
	public Result<List<OauthDeptResult>> getDeptStreetByTenant(DeptStreetByTenantParam oauthDept) {
		if (StringUtils.isBlank(oauthDept.getTenantId())) {
			return Result.success(Lists.newArrayList());
		}

		FlexModel<OauthDept> where = OauthDept.create()
				.where(OAUTH_DEPT.TENANT_ID.eq(oauthDept.getTenantId()));


		List<OauthDept> lists = where.lists();
		if (CollectionUtils.isNotEmpty(lists)) {
			List<OauthDeptResult> deptResultList = lists.stream().map(p -> BeanUtil.toBean(p, OauthDeptResult.class)).collect(Collectors.toList());

			Map<String, List<OauthDeptResult>> groupByPid = deptResultList.stream().collect(Collectors.groupingBy(OauthDeptResult::getPid));
			deptResultList.forEach(item->{
				item.setChildren(groupByPid.getOrDefault(item.getDeptId(), Lists.newArrayList()));
			});
			List<OauthDeptResult> collect = deptResultList.stream()
					.filter(p -> "0".equals(p.getPid()))
					.collect(Collectors.toList());
			return Result.success(collect);
		}
		return Result.success(Lists.newArrayList());
	}

	@Override
	public List<OauthDept> getAllDept() {
		return list();

	}

	@Override
	public OauthDept getDeptByDeptName(String deptName) {
		if (StringUtils.isBlank(deptName)) {
			return null;
		}
		List<OauthDept> lists = OauthDept.create()
				.where(OAUTH_DEPT.DEPT_NAME.eq(deptName))
				.and(OAUTH_DEPT.PID.eq(NumberConstants.ZERO))
				.limit(1)
				.lists();

		if (CollectionUtils.isEmpty(lists)) {
			return null;
		}
		return lists.get(0);
	}

}