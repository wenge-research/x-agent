package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.InterceptWordHousePageParam;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.entity.InterceptWordHouseApplicationRel;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.model.mapper.InterceptWordHouseMapper;
import com.wenge.model.service.InterceptWordHouseApplicationRelService;
import com.wenge.model.service.InterceptWordHouseService;
import com.wenge.model.service.InterceptWordService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.InterceptWordHouseApplicationRelTableDef.INTERCEPT_WORD_HOUSE_APPLICATION_REL;
import static com.wenge.model.entity.table.InterceptWordHouseTableDef.INTERCEPT_WORD_HOUSE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
import static com.wenge.model.entity.table.LlmInfoTableDef.LLM_INFO;
import static com.wenge.model.entity.table.PromptConfigTableDef.PROMPT_CONFIG;
import static com.wenge.oauth.enums.OwnerTypeEnum.OFFICIAL;
import static com.wenge.oauth.enums.OwnerTypeEnum.PERSONAL;


/**
 * @author: caohaifeng
 * @date: 2024/8/22 15:35
 * @Description: 拦截词库服务实现类
 * @Version:1.0
 **/

@Service
@Slf4j
public class InterceptWordHouseServiceImpl extends ServiceImpl<InterceptWordHouseMapper, InterceptWordHouse> implements InterceptWordHouseService {

	@Autowired
	private InterceptWordHouseApplicationRelService interceptWordHouseApplicationRelService;

	@Autowired
	private InterceptWordService interceptWordService;

	@Autowired
	private UserService userService;
	@Override
	public Result addInterceptWordHouse(InterceptWordHouse interceptWordHouse){
		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		interceptWordHouse.setCreateTime(DateUtil.getCurrentTime());
		interceptWordHouse.setUpdateTime(interceptWordHouse.getCreateTime());
		interceptWordHouse.setCreateUserId(tokenUserInfo.getId() + "");
		interceptWordHouse.setCreateUserName(tokenUserInfo.getUserName());
		interceptWordHouse.setCreateUserAccount(tokenUserInfo.getAccountName());
		interceptWordHouse.setTenantId(tokenUserInfo.getTenantId());

		//查重
		if (checkRepeat(interceptWordHouse)) {
			return Result.fail("当前词库名称已存在");
		}

		// 添加后是personal，预置后是official，群众没有权限进行操作
		if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
			interceptWordHouse.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		} else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
			return Result.fail("无权限操作");
		} else {
			interceptWordHouse.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		}

		save(interceptWordHouse);
		return Result.success(interceptWordHouse);
	}

	@Override
	public Result getInterceptWordHouseList(InterceptWordHousePageParam interceptWordHousePageParam){
		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		Wrappers wrappers = Wrappers.init()
				.where(INTERCEPT_WORD_HOUSE.DELETED_FLAG.eq(0))
				.and(StringUtils.isNotBlank(tokenUserInfo.getTenantId()), INTERCEPT_WORD_HOUSE.TENANT_ID.eq(tokenUserInfo.getTenantId()))
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getKeyword()), INTERCEPT_WORD_HOUSE.NAME.like(interceptWordHousePageParam.getKeyword()))
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getStartCreateTime()), INTERCEPT_WORD_HOUSE.CREATE_TIME.ge(interceptWordHousePageParam.getStartCreateTime()))
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getEndCreateTime()), INTERCEPT_WORD_HOUSE.CREATE_TIME.le(interceptWordHousePageParam.getEndCreateTime()))
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getCreateUser()), INTERCEPT_WORD_HOUSE.CREATE_USER_NAME.eq(tokenUserInfo.getAccountName()));

		if (interceptWordHousePageParam.getSort() != null && StrUtil.isNotBlank(interceptWordHousePageParam.getOrder())) {
			wrappers.orderBy(interceptWordHousePageParam.getOrder() + " " + interceptWordHousePageParam.getSort());
		} else {
			wrappers.orderBy(INTERCEPT_WORD_HOUSE.CREATE_TIME.desc()); // 默认按更新时间排序
		}

		// 根据权限查询
		PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.INTERCEPT_WORD);

		Page<InterceptWordHouse> page = page(Page.of(interceptWordHousePageParam.getPageNo(), interceptWordHousePageParam.getPageSize()), wrappers);

		new Thread(()->{
			List<InterceptWordHouse> wordHouseList = page.getRecords();
			wordHouseList.forEach(interceptWordHouse -> {

				//更新词库关联应用数量
				updateHouseApplicationCount(interceptWordHouse.getId());

				//更新词库数量
				interceptWordService.updateInterceptWordData(interceptWordHouse.getId());
			});
		}).start();
		return Result.success(page);
	}

	/**
	 * @description: 更新词库关联应用数量
	 * @author: caohaifeng
	 * @date: 2024/9/11 11:00
	 **/
	@Override
	public void updateHouseApplicationCount(Long interceptWordHouseId){
		QueryWrapper queryWrapper = QueryWrapper.create();
		queryWrapper.where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq("1"))
				.and(INTERCEPT_WORD_HOUSE_APPLICATION_REL.INTERCEPT_WORD_HOUSE_ID.eq(interceptWordHouseId + ""));
		final Long count = interceptWordHouseApplicationRelService.count(queryWrapper);
		InterceptWordHouse update = InterceptWordHouse.builder()
				.id(interceptWordHouseId)
				.applicationCount(count)
				.build();
		updateById(update);
	}



	@Override
	public Result getInterceptWordHouseListAll(InterceptWordHousePageParam interceptWordHousePageParam){
		// 工作流或应用中添加拦截词的接口【推荐official，我的personal】
		// 参数校验
		if (interceptWordHousePageParam == null) {
			return Result.fail("请求参数为空");
		}
		if(interceptWordHousePageParam.getApplicationId() == null){
			return Result.fail("应用ID不能为空");
		}
		if (interceptWordHousePageParam.getPageNo() == null || interceptWordHousePageParam.getPageNo() < 1) {
			return Result.fail("页面参数为空或错误");
		}
		if (interceptWordHousePageParam.getPageSize() == null || interceptWordHousePageParam.getPageSize() < 1) {
			return Result.fail("页面大小参数为空或错误");
		}
		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		// 根据权限查询
		String ownerType = interceptWordHousePageParam.getOwnerType();
		Wrappers wrappers = Wrappers.init()
				//.where(INTERCEPT_WORD_HOUSE.DELETED_FLAG.eq(0))
//				.where(StringUtils.isNotBlank(tokenUserInfo.getTenantId())
//								&& ownerType != null
//								&& !(ownerType.equals(OFFICIAL.getCode()) || ownerType.equals(PERSONAL.getCode())), // 对于拦截词，如果查询的是官方或自己的，就不需要租户条件
//						INTERCEPT_WORD_HOUSE.TENANT_ID.eq(tokenUserInfo.getTenantId()))  // 租户id
				.and(INTERCEPT_WORD_HOUSE.STATUS.eq(1))
				// 模糊查询
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getKeyword()), INTERCEPT_WORD_HOUSE.NAME.like(interceptWordHousePageParam.getKeyword()))
				// 大于创建时间
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getStartCreateTime()), INTERCEPT_WORD_HOUSE.CREATE_TIME.ge(interceptWordHousePageParam.getStartCreateTime()))
				// 小于结束时间
				.and(StringUtils.isNotBlank(interceptWordHousePageParam.getEndCreateTime()), INTERCEPT_WORD_HOUSE.CREATE_TIME.le(interceptWordHousePageParam.getEndCreateTime()));
		// 账户名查询,目前表中CREATE_USER_ACCOUNT大部分是null，而且会有不同名同拼音的账号名冲突问题，改用id筛出个人拦截词
		//.and(StringUtils.isNotBlank(interceptWordHousePageParam.getCreateUser()), INTERCEPT_WORD_HOUSE.CREATE_USER_ACCOUNT.eq(tokenUserInfo.getAccountName()));

		// 按什么排序，方便以后添加降序升序等其他功能
		if (StrUtil.isNotBlank(interceptWordHousePageParam.getSort()) && StrUtil.isNotBlank(interceptWordHousePageParam.getOrder())) {
			wrappers.orderBy(interceptWordHousePageParam.getOrder() + " " + interceptWordHousePageParam.getSort());
		}else {
			wrappers.orderBy(INTERCEPT_WORD_HOUSE.CREATE_TIME.desc()); // 默认按更新时间排序
		}

		PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.INTERCEPT_WORD, OwnerTypeEnum.getByCode(ownerType));

		// 分页查询
		Page<InterceptWordHouse> page = page(Page.of(interceptWordHousePageParam.getPageNo(), interceptWordHousePageParam.getPageSize()), wrappers);

		Map<Long, InterceptWordHouseApplicationRel> applicationIdMap = new HashMap<>();
		// 根据应用id获取关联的敏感词列表，且要求启用
		List<InterceptWordHouseApplicationRel> interceptWordHouseApplicationRels = interceptWordHouseApplicationRelService.list(
				QueryWrapper.create().where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq("1"))
				.and(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(interceptWordHousePageParam.getApplicationId())));
		if(CollectionUtil.isNotEmpty(interceptWordHouseApplicationRels)){
			interceptWordHouseApplicationRels.forEach(interceptWordHouseApplicationRel -> {
				applicationIdMap.put(interceptWordHouseApplicationRel.getInterceptWordHouseId(), interceptWordHouseApplicationRel);
			});
		}
		page.getRecords().forEach(interceptWordHouse -> {
				final InterceptWordHouseApplicationRel interceptWordHouseApplicationRel = applicationIdMap.get(interceptWordHouse.getId());
				if (interceptWordHouseApplicationRel != null) {
					interceptWordHouse.setApplicationId(interceptWordHouseApplicationRel.getApplicationId());
					interceptWordHouse.setRelId(interceptWordHouseApplicationRel.getId());
				}
			});
		return Result.success(page);
	}

	@Override
	public Result updateInterceptWordHouse(InterceptWordHouse interceptWordHouse){
		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		interceptWordHouse.setUpdateTime(DateUtil.getCurrentTime());
		interceptWordHouse.setUpdateUserId(tokenUserInfo.getId() + "");
		interceptWordHouse.setTenantId(tokenUserInfo.getTenantId());
		//查重
		if (checkRepeat(interceptWordHouse)) {
			return Result.fail("当前词库名称已存在");
		}

		updateById(interceptWordHouse);
		return Result.success();
	}

	@Override
	public Result deleteInterceptWordHouse(List<String> idList){
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		InterceptWordHouse interceptWordHouse = InterceptWordHouse.builder()
				.deletedFlag(1)
				.updateTime(DateUtil.getCurrentTime())
				.updateUserId(tokenUserInfo.getId().toString())
				.build();
		final boolean update = update(interceptWordHouse, Wrappers.init().where(INTERCEPT_WORD_HOUSE.ID.in(idList)));
		return Result.success(update);
	}

	@Override
	public Result updateStatus(InterceptWordHouse interceptWordHouse) {
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		InterceptWordHouse interceptWordHouseUpdate = InterceptWordHouse.builder()
				.id(interceptWordHouse.getId())
				.status(interceptWordHouse.getStatus())
				.updateTime(DateUtil.getCurrentTime())
				.updateUserId(tokenUserInfo.getId().toString())
				.build();
		return Result.success(updateById(interceptWordHouseUpdate));
	}

	@Override
	public Result setPreset(InterceptWordHousePageParam interceptWordHouse) {
		if (interceptWordHouse == null) {
			return Result.fail("请求参数为空");
		}

		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		// 只有超管才能操作
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
			return Result.fail("无操作权限");
		}

		// 获取传入的敏感词主键id
		long id = interceptWordHouse.getId();
		InterceptWordHouse updateInterceptWordHouse = getById(id);
		if (updateInterceptWordHouse == null) {
			return Result.fail("敏感词不存在");
		}

		// 超级管理员id集合
		List<OauthUser> superManageUser = userService.getSuperManageUser();
		List<Long> superIds = superManageUser.stream().map(OauthUser::getId).distinct().collect(Collectors.toList());
		List<String> stringIds = new ArrayList<>();
		superIds.forEach(superId -> stringIds.add(superId + ""));
		// 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
		if (stringIds.contains(updateInterceptWordHouse.getCreateUserId())){
			// 构造更新对象，已经是预置就取消，否则设置为预置
			updateInterceptWordHouse.setOwnerType(
					OFFICIAL.getCode().equals(updateInterceptWordHouse.getOwnerType()) ? PERSONAL.getCode() : OFFICIAL.getCode());
		}else{
			return Result.fail("该拦截词非超级管理员创建，无法进行预置操作");
		}

		// 执行更新
		updateById(updateInterceptWordHouse,false);

		return Result.success("敏感词预置更改成功");
	}


	//查重
	private boolean checkRepeat(InterceptWordHouse interceptWordHouse) {
		QueryWrapper queryWrapper = Wrappers.init()
				.where(INTERCEPT_WORD_HOUSE.NAME.eq(interceptWordHouse.getName()))
				.and(INTERCEPT_WORD_HOUSE.DELETED_FLAG.eq(0))
				.and(StringUtils.isNotBlank(interceptWordHouse.getTenantId()), INTERCEPT_WORD_HOUSE.TENANT_ID.eq(interceptWordHouse.getTenantId()));
		if (interceptWordHouse.getId() != null) {
			queryWrapper.and(INTERCEPT_WORD_HOUSE.ID.ne(interceptWordHouse.getId()));
		}
		if (null != getOne(queryWrapper)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

}