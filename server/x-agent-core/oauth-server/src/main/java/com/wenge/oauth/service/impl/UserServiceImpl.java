package com.wenge.oauth.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.dto.param.*;
import com.wenge.oauth.entity.*;
import com.wenge.oauth.enums.*;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.UserMapper;
import com.wenge.oauth.service.*;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.core.utils.AESUtils;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.oauth.constants.AppConfigContant.*;
import static com.wenge.oauth.entity.table.OauthDeptTableDef.OAUTH_DEPT;
import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;
import static com.wenge.oauth.entity.table.RoleTableDef.ROLE;
import static com.wenge.oauth.entity.table.TenantTableDef.TENANT;


/**
 * Description: 用户表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-05 14:13:29
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, OauthUser> implements UserService {

	/**
	 * 	用户表数据库处理类
	 */
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private OauthDeptService deptService;

	@Autowired
	private OauthTenantService oauthTenantService;

	@Autowired
	private RedisService redisService;

	/**
	 * 关芯群众用户角色  修改为配置缓存中读取
	 */
//	@Value("${appConfig.guanxin.h5.residentRoleCodes}")
//	private List<String> residentRoleCodes;

	/**
	 * 关芯工作人员用户角色  修改为配置缓存中读取
	 */
//	@Value("${appConfig.guanxin.h5.staffRoleCodes}")
//	private List<String> staffRoleCodes;

	@Autowired
	private RoleService roleService;

	@Override
	public Result updateUserList(List<OauthUser> oauthUsers) {
		log.info("智巡---updateUserList:{}", oauthUsers.size());
		int res = 0;
		for (OauthUser user : oauthUsers) {
			if (StringUtils.isBlank(user.getId() + "")) {
				return Result.fail("用户id不能为空");
			}
			long count = Role.create()
					.where(OAUTH_USER.ID.eq(user.getId()))
					.count();
			if (count > 0) {
				log.info("用户id：{} 已存在，执行更新", user.getId());
				//更新
				QueryWrapper queryWrapper =  QueryWrapper.create();
				queryWrapper.where(ROLE.ROLE_ID.eq(user.getId()));
				res += (update(user, queryWrapper)) ? 1 : 0;
				log.info("更新用户表成功：{}", user.toString());
			}else {
				res += (save(user)) ? 1 : 0;
				log.info("新增用户表成功：{}", user.toString());
			}
		}
		return Result.success(res);
	}

	@Override
	public Result addUser(OauthUser oauthUser){
		Long userId = oauthUser.getId();
		if (StringUtils.isBlank(oauthUser.getAccountName())) {
			return Result.fail("账号不能为空");
		}
		if (StringUtils.isBlank(oauthUser.getUsername())) {
			return Result.fail("用户名不能为空");
		}

		if (StringUtils.isBlank(oauthUser.getPassword())) {
			return Result.fail("密码不能为空");
		}

		//if (StringUtils.isBlank(oauthUser.getTenantId()) && StringUtils.isBlank(oauthUser.getDeptId())) {
		//	return Result.fail("租户或部门不能同时为空不能为空");
		//}
		if (StringUtils.isBlank(oauthUser.getExpireTime())) {
			oauthUser.setExpireTime(null);
		}

		if (StringUtils.isBlank(oauthUser.getPasswordExpiredTime())) {
			oauthUser.setPasswordExpiredTime(null);
		}
		long count = OauthUser.create()
				.where(OAUTH_USER.ACCOUNT_NAME.eq(oauthUser.getAccountName()))
				.and(null != oauthUser.getId(), OAUTH_USER.ID.ne(oauthUser.getId()))
				.count();
		if (count > 0) {
			return Result.fail("账号已存在");
		}

		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		if (StringUtils.isBlank(oauthUser.getTenantId())) {
			// if (StringUtils.isBlank(tokenUserInfo.getTenantId()) && !PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
			// 	return Result.fail("您尚未归属任何租户");
			// }
			oauthUser.setTenantId(tokenUserInfo.getTenantId());
		}

		if (StringUtils.isBlank(oauthUser.getUserType())) {
			// 不修改已有的用户类型
			if (null != oauthUser.getId()) {
				OauthUser fromDb = Wrappers.of(mapper)
						.select(OAUTH_USER.USER_TYPE)
						.where(OAUTH_USER.ID.eq(oauthUser.getId()))
						.limit(1)
						.one();
				if (StringUtils.isBlank(fromDb.getUserType())) {
					oauthUser.setUserType(UserTypeEnum.GOV.getCode());
				}
			} else {
				oauthUser.setUserType(UserTypeEnum.GOV.getCode());
			}
		}

		oauthUser.saveOrUpdate();

		// 新增用户时，同步工作人员到关芯智巡
		if (null == userId) {
			syncWorker(oauthUser);
		}

		return Result.success();
	}

	@Override
	public Result getUserList(UserParam param){
		String deptId = param.getDeptId();
		List<String> allChildrenId = Lists.newArrayList();
		if (StringUtils.isNotBlank(deptId)) {
			// 查询子级部门
			allChildrenId.addAll(deptService.getAllChildrenId(deptId));
		}
		Wrappers<Object> wrappers = Wrappers.init()
				.select(OAUTH_USER.DEFAULT_COLUMNS)
				.select(TENANT.TENANT_NAME)
				.select(OAUTH_DEPT.DEPT_NAME)
				.leftJoin(TENANT).on(OAUTH_USER.TENANT_ID.eq(TENANT.TENANT_ID))
				.leftJoin(OAUTH_DEPT).on(OAUTH_USER.DEPT_ID.eq(OAUTH_DEPT.DEPT_ID))
				.where(StringUtils.isNotBlank(param.getAccountName()), OAUTH_USER.ACCOUNT_NAME.like(param.getAccountName()))
				.and(CollectionUtils.isNotEmpty(allChildrenId), OAUTH_USER.DEPT_ID.in(allChildrenId))
				.and(StringUtils.isNotBlank(param.getCondition()), or -> {
					or.or(OAUTH_USER.USER_NAME.like(param.getCondition()));
					or.or(OAUTH_USER.PHONE.like(param.getCondition()));
				})
				.and(StringUtils.isNotBlank(param.getEndDate()), OAUTH_USER.CREATE_TIME.lt(param.getEndDate()+" 23:59:59"))
				.and(StringUtils.isNotBlank(param.getStartDate()), OAUTH_USER.CREATE_TIME.gt(param.getStartDate()+" 00:00:00"))
				.and(StringUtils.isNotBlank(param.getTenantId()), OAUTH_USER.TENANT_ID.eq(param.getTenantId()))
				.and(QueryMethods.findInSet(QueryMethods.column("'" + param.getWorkPosition() + "'"), OAUTH_USER.WORK_POSITION).gt(0).when(StringUtils.isNotBlank(param.getWorkPosition())))
				.and(StringUtils.isNotBlank(param.getWorkStatus()), OAUTH_USER.WORK_STATUS.eq(param.getWorkStatus()))
				.and(StringUtils.isNotBlank(param.getUserType()), OAUTH_USER.USER_TYPE.likeLeft(param.getUserType()))
				.and(StringUtils.isNotBlank(param.getModuleType()), OAUTH_USER.USER_TYPE.likeLeft(param.getModuleType()))
				.and(null != param.getStatus(), OAUTH_USER.STATUS.eq(param.getStatus()))
				.and(OAUTH_USER.POWER_TYPE.ne(PowerTypeEnum.SYSTEM_ADMIN.getCode()))
				.orderBys(OAUTH_USER.CREATE_TIME.desc());
		// 有效用户（用户状态为：未激活、正常、锁定）
		if (StringUtils.equals(param.getCancelFlag(), UserCancelStateEnum.VALID.getCode())) {
			wrappers.and(OAUTH_USER.STATUS.in(UserStatusEnum.UNACTIVATED.getCode(), UserStatusEnum.NORMAL.getCode(), UserStatusEnum.LOCKED.getCode()));
		}

		// 根据权限查询(如果不是系统管理员,只能查询自己所在租户的数据)
		TokenUser tokenUser = AppContextHolder.getTokenUserInfo();
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUser.getPowerType())) {
			wrappers.and(OAUTH_USER.CREATE_USER.eq(tokenUser.getAccountName()).or(OAUTH_USER.TENANT_ID.eq(tokenUser.getTenantId())));
		}

		// 获取所有部门
		List<OauthDept> allDept = deptService.getAllDept();
		Map<String, String> deptIdToPidMap = allDept.stream().collect(Collectors.toMap(
                OauthDept::getDeptId,
                OauthDept::getPid,
				(k1, k2) -> k1
		));

		Page<OauthUser> pages = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);

		// 处理前端部门树回显
		List<OauthUser> records = pages.getRecords();
		if (CollectionUtil.isNotEmpty(records)) {
			records.forEach(item->{
				if (StringUtils.isNotBlank(item.getDeptId())) {
					List<String> deptIds = Lists.newArrayList();
					deptIds.add(item.getDeptId());
					getDeptIds(item.getDeptId(), deptIdToPidMap, deptIds);
					// list反转
					Collections.reverse(deptIds);
					item.setDeptIds(deptIds);
				}

			});
		}

		return Result.success(pages);
	}

	/**
	 * 递归获取部门id
	 * @param deptId
	 * @param deptIdToPidMap
	 * @param deptIds
	 */
	private void getDeptIds(String deptId, Map<String, String> deptIdToPidMap, List<String> deptIds) {
		if (StringUtils.isBlank(deptId)) {
			return;
		}
		String pid = deptIdToPidMap.get(deptId);
		if (!"0".equals(pid)) {
			deptIds.add(pid);
			getDeptIds(pid, deptIdToPidMap, deptIds);
		}

	}


	@Override
	public Result updateUser(OauthUser oauthUser){
		updateById(oauthUser);
		return Result.success();
	}

	@Override
	public Result deleteUser(ListStringParam idList){
		if (CollectionUtil.isEmpty(idList.getParam())) {
			return Result.fail("用户id不能为空");
		}
		OauthUser.create()
				.where(OAUTH_USER.ID.in(idList.getParam()))
				.remove();
		//removeByIds(idList);
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result updateTenant(UpdateTenantParam param) {
		if (StringUtils.isBlank(param.getTenantId())) {
			return Result.fail("租户不能为空");
		}
		if (CollectionUtil.isEmpty(param.getUserIdList())) {
			return Result.fail("用户不能为空");
		}
		// 校验租户配额
		Tenant tenant = oauthTenantService.getDeatail(param.getTenantId());
		Integer numLimit = tenant.getUserNumLimit();
		if (param.getUserIdList().size() > numLimit) {
			return Result.fail("租户配额不足");
		}

		// 清除用户的租户id
		OauthUser.create()
				.setTenantId(StringConstant.BLANK)
				.where(OAUTH_USER.TENANT_ID.eq(param.getTenantId()))
				.update();

		// 设置用户的租户id
		OauthUser.create()
				.setTenantId(param.getTenantId())
				.where(OAUTH_USER.ID.in(param.getUserIdList()))
				.update();

		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result bindingRole(UserBindingRoleParam param) {
		if (CollectionUtil.isEmpty(param.getUserIdList())) {
			return Result.fail("用户id不能为空");
		}

		// 移除所有角色
		userRoleService.removeAllRole(param.getUserIdList());

		String now = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
		// 添加角色
		List<String> roleIds = param.getRoleIdList();
		if (CollectionUtil.isEmpty(roleIds)) {
			return Result.success();
		}

		List<UserRole> userRoleList = param.getUserIdList().stream()
				.flatMap(userId ->
						param.getRoleIdList().stream()
								.map(p -> UserRole.create()
										.setUserId(userId)
										.setRoleId(p)
										.setUpdateTime(now)
										.setCreateTime(now)))
				.collect(Collectors.toList());

		//List<UserRole> userRoleList = roleIds.stream().map(p -> {
		//	return UserRole.create()
		//			.setUserId(param.getUserId())
		//			.setRoleId(p)
		//			.setUpdateTime(now)
		//			.setCreateTime(now);
		//}).collect(Collectors.toList());
		userRoleService.saveBatch(userRoleList);
		return Result.success();
	}

	@Override
	public Result deregisterUser(ListStringParam userIdList) {
		if (CollectionUtil.isEmpty(userIdList.getParam())) {
			return Result.fail("用户id不能为空");
		}

		updateStatus(userIdList.getParam(), UserStatusEnum.DEREGISTERED);
		return Result.success();
	}

	@Override
	public Result unlock(ListStringParam userIdList) {
		if (CollectionUtil.isEmpty(userIdList.getParam())) {
			return Result.fail("用户id不能为空");
		}
		// 成功登录,密码错误次数清零
		OauthUser.create()
				.setStatus(UserStatusEnum.NORMAL.getCode())
				.setLockTime(StringConstant.BLANK)
				.setPwErrorNum(0)
				.where(OAUTH_USER.ID.in(userIdList.getParam()))
				.update();
		return Result.success();
	}

	@Override
	public Result getManageUserByTenant(StringParam tenantId) {
		// allFlag为true时，查询所有用户
		if (StringUtils.isBlank(tenantId.getParam())) {
			return Result.fail("租户不能为空");
		}

		List<OauthUser> lists = OauthUser.create()
				.where(OAUTH_USER.TENANT_ID.eq(tenantId.getParam()))
				.and(OAUTH_USER.POWER_TYPE.eq(PowerTypeEnum.NORMAL_ADMIN.getCode()))
				.lists();
		return Result.success(lists);
	}

	@Override
	public Result getAllUserByTenant(StringParam tenantId) {
		// allFlag为true时，查询所有用户
		if (StringUtils.isBlank(tenantId.getParam())) {
			return Result.fail("租户不能为空");
		}

		List<OauthUser> lists = OauthUser.create()
				.where(OAUTH_USER.TENANT_ID.eq(tenantId.getParam()))
				.lists();
		return Result.success(lists);
	}

	@Override
	public Result getManageUser(StringParam param) {
		List<OauthUser> lists = OauthUser.create()
				.where(OAUTH_USER.POWER_TYPE.eq(PowerTypeEnum.NORMAL_ADMIN.getCode()))
				.and(StringUtils.isNotBlank(param.getParam()), OAUTH_USER.USER_NAME.like(param.getParam()))
				.lists();

		return Result.success(lists);
	}

	@Override
	public List<OauthUser> getSuperManageUser() {
		List<OauthUser> lists = OauthUser.create()
				.where(OAUTH_USER.POWER_TYPE.eq(PowerTypeEnum.SYSTEM_ADMIN.getCode()))
				.lists();

		return lists;
	}

	@Override
	public Result checkStaff(StaffCheckParam param) {
		if (null == param.getUserId()) {
			return Result.fail("用户不能为空");
		}
		if (StringUtils.isBlank(param.getCheckStatus())) {
			return Result.fail("审核状态不能为");
		}
		if (StringUtils.isBlank(param.getCheckOpinion())) {
			param.setCheckOpinion(StringConstant.BLANK);
		}

		OauthUser flexModel = (OauthUser) OauthUser.create()
				.where(OAUTH_USER.ID.eq(param.getUserId()))
				.one();
		OauthUser oauthUser = OauthUser.create()
				.setCheckStatus(param.getCheckStatus())
				.setCheckOpinion(param.getCheckOpinion())
				.setUserName(param.getUserName())
				.setDeptId(param.getDeptId())
				.setWorkPosition(param.getWorkPosition())
				.setMainDuty(param.getMainDuty())
				.setPhone(param.getPhone())
				.setUserType(param.getUserType())
				.setLandline(param.getLandline())
				.setWorkStatus(param.getWorkStatus());

		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		// 审核通过时
		if (UserCheckStatusEnum.PASS.getCode().equals(param.getCheckStatus())) {
			// 设置用户类型为工作人员
			if (UserTypeEnum.STAFF_STREET.getCode().equals(param.getUserType())) {
				oauthUser.setUserType(UserTypeEnum.GOV_STREET.getCode());
			} else if (UserTypeEnum.STAFF_COMMUNITY.getCode().equals(param.getUserType())) {
				oauthUser.setUserType(UserTypeEnum.GOV_COMMUNITY.getCode());
			} else if (UserTypeEnum.RESIDENT.getCode().equals(param.getUserType())) {
				oauthUser.setUserType(UserTypeEnum.RESIDENT.getCode());
			} else {
				oauthUser.setUserType(UserTypeEnum.GOV.getCode());
			}
			// 设置账号，优先设置为手机号，如果没有手机号，用座机
			String accountName = StringUtils.isNotBlank(flexModel.getPhone()) ? flexModel.getPhone() : flexModel.getLandline();
			if (StringUtils.isBlank(accountName)) {
				return Result.fail("请填写手机号或者座机号");
			}

			// 判断账号是否存在
			boolean exists = Wrappers.of(mapper)
					.where(OAUTH_USER.ACCOUNT_NAME.eq(accountName))
					.and(OAUTH_USER.ID.ne(param.getUserId()).when(null != param.getUserId()))
					.exists();
			if (exists) {
				return Result.fail("账号【{accountName}】已存在".replace("{accountName}", accountName));
			}

			oauthUser.setAccountName(accountName);
			// 从租户获取默认密码
			String pwStr = getPw(tokenUserInfo);
			oauthUser.setPassword(pwStr);
			// 设置状态
			oauthUser.setStatus(UserStatusEnum.NORMAL.getCode());
			// 设置租户
			if (StringUtils.isNotBlank(tokenUserInfo.getTenantId())) {
				oauthUser.setTenantId(tokenUserInfo.getTenantId());
			}
			// 设置权限类型:普通用户
			oauthUser.setPowerType(PowerTypeEnum.NORMAL_USER.getCode());
		}

		// 更新用户
		oauthUser
				.where(OAUTH_USER.ID.eq(param.getUserId()))
				.update();

		// 绑定工作人员角色
		if (UserCheckStatusEnum.PASS.getCode().equals(param.getCheckStatus())) {
			UserBindingRoleParam bindingRoleParam = new UserBindingRoleParam();
			bindingRoleParam.setUserIdList(ListUtil.toList(param.getUserId() + StringConstant.BLANK));

			final List<String> staffRoleCodes = AppConfigContant.getConfigurations(AppConfigContant.APP_CONFIG_GUANXIN_H5_STAFF_ROLE_CODES);
			if (CollectionUtil.isEmpty(staffRoleCodes)) {
				log.error("key值：{}  获取失败，请检查", AppConfigContant.APP_CONFIG_GUANXIN_H5_STAFF_ROLE_CODES);
				new RuntimeException("AppConfigContant.APP_CONFIG_GUANXIN_H5_STAFF_ROLE_CODES value 获取失败，请检查");
			}
			bindingRoleParam.setRoleCodeList(ListUtil.toList(staffRoleCodes));
			bindingRoleByCode(bindingRoleParam);
		}

		// 同步工作人员到关芯智巡
		if (UserCheckStatusEnum.PASS.getCode().equals(param.getCheckStatus())) {
			syncWorker(oauthUser);
		}

		return Result.success();
	}

	@Override
	public Result fillInPersonal(PersonalFillInParam param) {
		if (null == param.getUserId()) {
			return Result.fail("用户不能为空");
		}

		if (StringUtils.isBlank(param.getUserType())) {
			return Result.fail("用户类型不能为空");
		}

		// 如果用户类型为政务类型，那么将用户类型重置为工作人员状态
		if (UserTypeEnum.GOV_STREET.getCode().equals(param.getUserType())) {
			param.setUserType(UserTypeEnum.STAFF_STREET.getCode());
		} else if (UserTypeEnum.GOV_COMMUNITY.getCode().equals(param.getUserType())) {
			param.setUserType(UserTypeEnum.STAFF_COMMUNITY.getCode());
		}

		// 判断是否为工作人员
		boolean staffFlg = UserTypeEnum.STAFF_STREET.getCode().equals(param.getUserType())
				|| UserTypeEnum.STAFF_COMMUNITY.getCode().equals(param.getUserType());

		// 如果是工作人员，则需要填写用户名，手机号和部门信息
		if (staffFlg) {
			if (StringUtils.isBlank(param.getDeptId())) {
				return Result.fail("部门不能为空");
			}
			if (StringUtils.isBlank(param.getUserName())) {
				return Result.fail("用户名不能为空");
			}
			if (StringUtils.isBlank(param.getPhone()) && StringUtils.isBlank(param.getLandline())) {
				return Result.fail("座机或手机号不能同时为空");
			}
			if (StringUtils.isBlank(param.getWorkStatus())) {
				return Result.fail("工作状态不能为空");
			}
		}

		if (StringUtils.isNotBlank(param.getPhone())) {
			boolean exists = OauthUser.create()
					.where(OAUTH_USER.PHONE.eq(param.getPhone()))
					.and(OAUTH_USER.ID.ne(param.getUserId()))
					.exists();
			if (exists) {
				return Result.fail("手机号已存在");
			}
		}

		// 如果没有座机，这里设置为空字符串，触发更新字段
		if (StringUtils.isBlank(param.getLandline())) {
			param.setLandline(StringConstant.BLANK);
		}
		// 如果没有手机，这里设置为空字符串，触发更新字段
		if (StringUtils.isBlank(param.getPhone())) {
			param.setPhone(StringConstant.BLANK);
		}

		OauthUser oauthUser = OauthUser.create()
				.setUserType(param.getUserType())
				.setPowerType(PowerTypeEnum.WECHAT_USER.getCode())
				.setUserName(param.getUserName())
				.setDeptId(param.getDeptId())
				.setWorkPosition(param.getWorkPosition())
				.setLandline(param.getLandline())
				.setMainDuty(param.getMainDuty())
				.setPhone(param.getPhone())
				.setWorkStatus(param.getWorkStatus())
				.setCheckStatus(StringConstant.BLANK)
				.setCheckOpinion(StringConstant.BLANK);

		// 如果是工作人员，设置审核状态为待审核
		if (staffFlg) {
			oauthUser.setCheckStatus(UserCheckStatusEnum.WAITING.getCode());
		}
		oauthUser
				.where(OAUTH_USER.ID.eq(param.getUserId()))
				.update();

		// 默认绑定群众角色
		UserBindingRoleParam bindingRoleParam = new UserBindingRoleParam();
		bindingRoleParam.setUserIdList(ListUtil.toList(param.getUserId() + StringConstant.BLANK));
		final List<String> residentRoleCodes = AppConfigContant.getConfigurations(AppConfigContant.APP_CONFIG_GUANXIN_H5_RESIDENT_ROLE_CODES);
		if (CollectionUtil.isEmpty(residentRoleCodes)) {
			log.error("key值：{}  获取失败，请检查", AppConfigContant.APP_CONFIG_GUANXIN_H5_RESIDENT_ROLE_CODES);
			new RuntimeException("AppConfigContant.APP_CONFIG_GUANXIN_H5_RESIDENT_ROLE_CODES value 获取失败，请检查");
		}
		bindingRoleParam.setRoleCodeList(ListUtil.toList(residentRoleCodes));
		bindingRoleByCode(bindingRoleParam);

		return Result.success();

	}

	@Override
	public Result<OauthUser> getUserDetail(StringParam userId) {
		if (StringUtils.isBlank(userId.getParam())) {
			return Result.fail("用户不能为空");
		}
		List<OauthUser> lists = OauthUser.create()
				.where(OAUTH_USER.ID.eq(userId.getParam()))
				.limit(1)
				.lists();
		if (CollectionUtil.isNotEmpty(lists)) {
			OauthUser oauthUser = lists.get(0);
			OauthDept deptDetail = deptService.getDeptDetail(oauthUser.getDeptId());
			if (null != deptDetail) {
				oauthUser.setDeptName(deptDetail.getDeptName());
			}
			oauthUser.setPassword(StringConstant.BLANK);
			return Result.success(lists.get(0));
		}
		return Result.success(OauthUser.create());
	}

	@Override
	public void bindingRoleByCode(UserBindingRoleParam param) {
		if (CollectionUtil.isEmpty(param.getUserIdList())) {
			return;
		}

		List<String> roleCodeList = param.getRoleCodeList();
		if (CollectionUtil.isNotEmpty(roleCodeList)) {
			List<Role> roleList = roleService.getUserRoleByRoleCode(roleCodeList);

			List<String> roleIdList = roleList.stream().map(Role::getRoleId).distinct().collect(Collectors.toList());
			param.setRoleIdList(roleIdList);
		}

		bindingRole(param);

	}

	@Override
	public Result changPw(PwChangParam param) {
		if (StringUtils.isBlank(param.getOldPw()) || StringUtils.isBlank(param.getNewPw()) || StringUtils.isBlank(param.getConfirmNewPw())) {
			return Result.fail("新密码或确认密码不能为空");
		}

		if (!param.getNewPw().equals(param.getConfirmNewPw())) {
			return Result.fail("新密码与确认密码不一致");
		}

		OauthUser user = Wrappers.of(mapper)
				.select(OAUTH_USER.PASSWORD)
				.where(OAUTH_USER.ID.eq(param.getUserId()))
				.limit(1)
				.one();

		if (null == user) {
			return Result.fail("用户不存在");
		}

		if (!param.getOldPw().equals(user.getPassword())) {
			return Result.fail("原密码错误");
		}

		boolean update = OauthUser.create()
				.setPassword(param.getNewPw())
				.where(OAUTH_USER.ID.eq(param.getUserId()))
				.update();
		return Result.success();
	}

	@Override
	public List<OauthUser> getUserByTenant() {
		Wrappers oauthUserWrappers = Wrappers.init()
				.from(OAUTH_USER);
		// 根据权限查询
		PermissionControlUtils.buildPermission(oauthUserWrappers, OauthPermissionEnum.USER);

        return mapper.selectListByQuery(oauthUserWrappers);
	}

	@Override
	public List<OauthUser> getUserByTenantId(String tenantId) {
		if (StringUtils.isEmpty(tenantId)) {
			return new ArrayList<>();
		}

		List<OauthUser> oauthUserList = OauthUser.create()
				.where(OAUTH_USER.TENANT_ID.eq(tenantId))
				.lists();
		return oauthUserList;
	}

	@Override
	public Result currentAccount() {
		//数据隔离
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
			OauthUser oauthUser = getById(tokenUserInfo.getId());
			return Result.success(oauthUser);
		}else{
			OauthUser oauthUser = getById(1);
			return Result.success(oauthUser);
		}
	}

	@Override
	public Result updateCurrentAccount(OauthUser oauthUser) {
		String now = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
		//oauthUser.setCreateTime(now);
		oauthUser.setUpdateTime(now);
		updateById(oauthUser);
		return Result.success(oauthUser);
	}

	@Override
	public Result updatePhone(RetrievePasswordParam param) {
		if (StringUtils.isBlank(param.getCode()) || StringUtils.isBlank(param.getCodeKey())) {
			return Result.fail("验证码不能为空");
		}
		if (StringUtils.isBlank(param.getPhoneNum())) {
			return Result.fail("手机号不能为空");
		}
		// 验证验证码是否有效
		if (!redisService.hasKey(param.getCodeKey())) {
			return Result.fail("验证码已过期");
		}

		if (!param.getCode().equals(redisService.get(param.getCodeKey()))) {
			return Result.fail("验证码错误");
		}

		// 删除使用过的验证码
		redisService.del(param.getCodeKey());
		// 获取当前登录人信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		// 验证手机号是否被占用
		OauthUser phoneOne = Wrappers.of(mapper)
				.where(OAUTH_USER.PHONE.eq(param.getPhoneNum()))
				.and(OAUTH_USER.ID.ne(tokenUserInfo.getId()))
				.limit(1)
				.one();
		if (null != phoneOne) {
			return Result.fail("该手机号已被占用，请用该手机号登录");
		}

		OauthUser one = Wrappers.of(mapper)
				.where(OAUTH_USER.ID.eq(tokenUserInfo.getId()))
				.limit(1)
				.one();
		one.setPhone(param.getPhoneNum());
		updateById(one);

		return Result.success();
	}

	// @Override
	// public Result updatePassword(RetrievePasswordParam param) {
	// 	if (StringUtils.isBlank(param.getCode()) || StringUtils.isBlank(param.getCodeKey())) {
	// 		return Result.fail("验证码不能为空");
	// 	}
	// 	if (StringUtils.isBlank(param.getPhoneNum())) {
	// 		return Result.fail("手机号不能为空");
	// 	}
	// 	if (StringUtils.isBlank(param.getPassword()) || StringUtils.isBlank(param.getConfirmPassword())) {
	//
	// 	}
	// 	return Result.success();
	// }

	@Override
	public Result retrievePassword(RetrievePasswordParam param) {
		// 校验参数
		Result result = checkUpdatePassword(param);
		if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
			return result;
		}
		OauthUser one = Wrappers.of(mapper)
				.where(OAUTH_USER.PHONE.eq(param.getPhoneNum()))
				.limit(1)
				.one();
		if (null == one) {
			return Result.fail("账号不存在");
		}
		// 删除使用过的验证码
		redisService.del(param.getCodeKey());

		one.setPassword(param.getPassword());
		updateById(one);

		return Result.success();
	}

	@Override
	public Result updatePassword(RetrievePasswordParam param) {
		// 校验参数
		Result result = checkUpdatePassword(param);
		if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
			return result;
		}
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		OauthUser one = Wrappers.of(mapper)
				.where(OAUTH_USER.ID.eq(tokenUserInfo.getId()))
				.limit(1)
				.one();
		if (null == one) {
			return Result.fail("账号不存在");
		}
		if (!param.getPhoneNum().equals(one.getPhone())) {
			return Result.fail("当前手机号已绑定其他账号");
		}
		// 删除使用过的验证码
		redisService.del(param.getCodeKey());
		one.setPassword(param.getPassword());
		updateById(one);

		return Result.success();
	}

	/**
	 * 校验修改密码
	 * @param param
	 */
	private Result checkUpdatePassword(RetrievePasswordParam param) {
		if (StringUtils.isBlank(param.getPassword()) || StringUtils.isBlank(param.getConfirmPassword())) {
			return Result.fail("新密码或确认密码不能为空");
		}
		if (StringUtils.isBlank(param.getCode()) || StringUtils.isBlank(param.getCodeKey())) {
			return Result.fail("验证码不能为空");
		}
		if (StringUtils.isBlank(param.getPhoneNum())) {
			return Result.fail("手机号不能为空");
		}
		if (!param.getPassword().equals(param.getConfirmPassword())) {
			return Result.fail("新密码与确认密码不一致");
		}
		// 验证验证码是否有效
		if (!redisService.hasKey(param.getCodeKey())) {
			return Result.fail("验证码已过期");
		}
		if (!param.getCode().equals(redisService.get(param.getCodeKey()))) {
			return Result.fail("验证码错误");
		}

		return Result.success();
	}

	/**
	 * 设置租户
	 * @param oauthUser
	 * @return
	 */
	private Result setTenantId(OauthUser oauthUser) {
		String deptTenantId = "";
		if (StringUtils.isNotBlank(oauthUser.getDeptId())) {
			List<OauthDept> lists = OauthDept.create()
					.where(OAUTH_DEPT.DEPT_ID.eq(oauthUser.getDeptId()))
					.limit(1)
					.lists();
			if (CollectionUtil.isNotEmpty(lists)) {
				deptTenantId = lists.get(0).getTenantId();
			}
		}
		if (StringUtils.isBlank(deptTenantId) && StringUtils.isBlank(oauthUser.getTenantId())) {
			return Result.fail("租户不能为空");
		}

		if (StringUtils.isNotBlank(deptTenantId) && StringUtils.isNotBlank(oauthUser.getTenantId()) && !deptTenantId.equals(oauthUser.getTenantId())) {
			return Result.fail("用户和部门的租户不一致");
		}

		if (StringUtils.isBlank(oauthUser.getTenantId())) {
			oauthUser.setTenantId(deptTenantId);
		}

		return Result.success();
	}

	@Override
	public List<OauthUser> getUserDetailByAccountNameList(List<String> accountNameList) {
		if (CollectionUtil.isEmpty(accountNameList)) {
			return new ArrayList<>();
		}

		List<OauthUser> oauthUserList = OauthUser.create()
				.where(OAUTH_USER.ACCOUNT_NAME.in(accountNameList))
				.lists();
		return oauthUserList;
	}

	/**
	 * 更新用户状态
	 * @param userIdList
	 * @param statusEnum
	 */
	private void updateStatus(List<String> userIdList, UserStatusEnum statusEnum) {
		//UpdateChain.of(OauthUser.class)
		//		.set(OAUTH_USER.STATUS, statusEnum.getCode())
		//		.where(OAUTH_USER.ID.in(userIdList))
		//		.update();
		OauthUser.create()
				.setStatus(statusEnum.getCode())
				.where(OAUTH_USER.ID.in(userIdList))
				.update();
	}

	/**
	 * 获取默认密码
	 * @param tokenUserInfo
	 * @return
	 */
	private String getPw(TokenUser tokenUserInfo ) {
		Tenant tenantDetail = oauthTenantService.getDeatail(tokenUserInfo.getTenantId());
		String pwStr = StringConstant.BLANK;

		if (null != tenantDetail && StringUtils.isNotBlank(tenantDetail.getDefaultPassword())) {
			pwStr = SecureUtil.md5(tenantDetail.getDefaultPassword());
		}
		if (StringUtils.isBlank(pwStr)) {
			pwStr = SecureUtil.md5("zgc123456");
		}

		return pwStr;
	}

	/**
	 * 同步工作人员到关芯智巡
	 * 	2025228 需要将所有的用户推送到关芯智巡
	 */
	@Override
	public void syncWorker(OauthUser oauthUser) {
		log.info("执行小程序用户同步关芯智巡....{}", oauthUser);
		String syncZhixunAppid = AppConfigContant.getConfiguration(SYNC_GUANXIN_ZHIXUN_USER_APPID);
		if (StringUtils.isBlank(syncZhixunAppid)) {
			log.error("未配置 sync_guanxin_zhixun_user_appid 不同步");
			return;
		}
		String syncZhixunUserEnable = AppConfigContant.getConfiguration(syncZhixunAppid, SYNC_GUANXIN_ZHIXUN_USER_ENABLE);
		if (!YesNoEnum.YES.getName().equals(syncZhixunUserEnable)) {
			log.error("未配置 sync_guanxin_zhixun_user_enable 不同步");
			return;
		}

		String aesKey = getConfiguration(syncZhixunAppid, APP_CONFIG_GUANXIN_ZHIXUN_AES_KEY);
		String encrypt = AESUtils.encrypt(oauthUser.getPhone(), aesKey, aesKey);

		JSONObject param = new JSONObject();
		// 校验
		param.put("aseCheck", encrypt);
		// 用户名称
		param.put("userName", oauthUser.getUsername());
		// 手机号
		param.put("phone", oauthUser.getPhone());
		// openId
		param.put("openId", oauthUser.getOpenId());
		//座机号码
		param.put("landline", oauthUser.getLandline());
		//用户状态
		param.put("status", oauthUser.getStatus()); //用户状态 （0.未激活 1.正常 2.锁定 3.删除）
		//租户
		param.put("tenantId", oauthUser.getTenantId());
		//用户权限类型
		param.put("powerType", oauthUser.getPowerType()); //用户权限类型,0-系统管理员(超级管理员),1-普通管理员，2-普通用户,3-群众无任何权限

		//用户类型：resident：群众，staff-street：工作人员-街道工作人员，staff-community：工作人员-社区工作人员，
		// gov：政务人员，gov-street：政务人员-街道工作人员，gov-community：政务人员-社区工作人员
		param.put("userType", oauthUser.getUserType());


		if (StringUtils.isNotBlank(oauthUser.getDeptId())) {
			OauthDept deptDetail = deptService.getDeptDetail(oauthUser.getDeptId());
			// 部门名称
			String remark = deptDetail.getRemark();
			String deptName = StringConstant.BLANK;
			// 如果备注包含“对应关心智巡的部门名称：”文本，则优先使用备注，备注中的部门名称是对应关心智巡的部门名称
			if (StringUtils.isNotBlank(remark) && remark.contains("对应关心智巡的部门名称：")) {
				deptName = remark.replace("对应关心智巡的部门名称：", StringConstant.BLANK);
			}
			if (StringUtils.isBlank(deptName)) {
				deptName = deptDetail.getDeptName();
			}
			param.put("deptName", deptName);
		}

		String registerUri = getConfiguration(syncZhixunAppid, APP_CONFIG_GUANXIN_ZHIXUN_REGISTER_URI);
		log.info("syncWorker.url:{} param:{}",registerUri, JSON.toJSONString(param));
		String post = HttpUtil.post(registerUri, JSON.toJSONString(param));
		log.info("syncWorker.result:{}", post);
	}

	@Override
	public List<OauthUser> getUsersByUserIds(List<String> userIds) {
		if (CollectionUtil.isEmpty(userIds)) {
			return  Collections.emptyList();
		}
		List<OauthUser> oauthUserList = OauthUser.create()
				.where(OAUTH_USER.ID.in(userIds))
				.lists();
		return oauthUserList;
	}

	@Override
	public OauthUser getUserByAccount(String accountName) {
		return Wrappers.of(mapper)
				.where(OAUTH_USER.ACCOUNT_NAME.eq(accountName))
				.limit(1)
				.one();
	}
}
