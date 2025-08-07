package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.PromptConfigListParam;
import com.wenge.model.entity.PromptConfig;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.model.mapper.PromptConfigMapper;
import com.wenge.model.service.PromptConfigService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.PromptConfigTableDef.PROMPT_CONFIG;
import static com.wenge.model.workflow.entity.table.ComponentTableDef.COMPONENT;

/**
 * Description: 提示词配置服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-12-18 11:02:37
 *
 */
@Service
@Slf4j
public class PromptConfigServiceImpl extends ServiceImpl<PromptConfigMapper, PromptConfig> implements PromptConfigService {
	/**
	 * 	提示词配置数据库处理类
	 */
	@Autowired
	private PromptConfigMapper promptConfigMapper;

	@Autowired
	private UserService userService;


	@Override
	public Result addPromptConfig(PromptConfig promptConfig){

		boolean exists = Wrappers.of(mapper)
				.where(PROMPT_CONFIG.PROMPT_TITLE.eq(promptConfig.getPromptTitle()))
				.and(PROMPT_CONFIG.PROMPT_ID.ne(promptConfig.getPromptId()).when(StringUtils.isNotBlank(promptConfig.getPromptId())))
				.exists();
//		if (exists) {
//			return Result.fail("标题已存在");
//		}
		if (StringUtils.isBlank(promptConfig.getPromptId())) {
			promptConfig.setPromptId(IdUtil.simpleUUID());
		}

		// 添加后是personal，预置后是official，群众没有权限进行操作
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
			promptConfig.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		} else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
			return Result.fail("无权限操作");
		} else {
			promptConfig.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		}

		saveOrUpdate(promptConfig);
		return Result.success();
	}

	@Override
	public Result<Page<PromptConfig>> getPromptConfigList(PromptConfigListParam promptConfig){
		// 调用处：菜单工具提示词显示页面【personalGrantTenant】，应用中提示词引用时显示页面【推荐official，我的（个人、授权）personalGrant】
		// 参数校验
		if (promptConfig == null) {
			return Result.fail("请求参数为空");
		}
		if (promptConfig.getPageNo() == null || promptConfig.getPageNo() < 1) {
			return Result.fail("页面参数为空或错误");
		}
		if (promptConfig.getPageSize() == null || promptConfig.getPageSize() < 1) {
			return Result.fail("页面大小参数为空或错误");
		}

		String ownerType = promptConfig.getOwnerType();
		Wrappers wrappers = Wrappers.of(mapper)
				// 根据类型查询，system:系统提示词,user:用户提示词,yayi:机器人提示词
				.where(PROMPT_CONFIG.TYPE.eq(promptConfig.getType()).when(StringUtils.isNotBlank(promptConfig.getType())))
				// 不是菜单提示词时，只能看见启用的,0-有效 1-无效
				// 当前菜单的提示词界面传PERSONAL_GRANT_TENANT，即对于非超管只能看到个人/授权/租户的，是菜单界面时查看菜单提示词时无论是否启用都可见
				.and(ObjectUtil.isNotEmpty(promptConfig.getStatus()), PROMPT_CONFIG.STATUS.eq(promptConfig.getStatus()))
				.and(StringUtils.isNotBlank(promptConfig.getPromptTitle()), PROMPT_CONFIG.PROMPT_TITLE.like(promptConfig.getPromptTitle()));

		// 根据前端传参按时间排序
		if (org.apache.commons.lang3.StringUtils.isNotBlank(promptConfig.getOrder()) && org.apache.commons.lang3.StringUtils.isNotBlank(promptConfig.getSort())) {
			wrappers.orderBy(promptConfig.getOrder() + " " + promptConfig.getSort());
		}else{
			wrappers.orderBy(PROMPT_CONFIG.CREATE_TIME.desc()); // 默认按更新时间排序
		}

		// 根据权限查询，除非查看的是官方official或私人personal的，否则该查询对超管无效；其余的对超管和普通用户都有效
		PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.PROMPT, OwnerTypeEnum.getByCode(ownerType));

		Page<PromptConfig> page = wrappers.page(Page.of(promptConfig.getPageNo(), promptConfig.getPageSize()));

		return Result.success(page);
	}

	@Override
	public Result deletePromptConfig(List<String> idList){
		if (CollectionUtil.isEmpty(idList)) {
			return Result.success();
		}
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<PromptConfig> getBuiltInPrompt(PromptConfigListParam promptConfigListParam) {
		Wrappers<PromptConfig> wrappers = Wrappers.of(mapper)
				.where(StrUtil.isNotBlank(promptConfigListParam.getPromptTitle()), PROMPT_CONFIG.PROMPT_TITLE.eq(promptConfigListParam.getPromptTitle()))
				.and(PROMPT_CONFIG.DELETE_FLAG.eq(0)).and(PROMPT_CONFIG.STATUS.eq(0));
		return list(wrappers);
	}

	@Override
	public Result setPreset(PromptConfigListParam promptConfigListParam) {
		if (promptConfigListParam == null) {
			return Result.fail("请求参数为空");
		}
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		// 只有超管才能操作
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
			return Result.fail("无操作权限");
		}

		// 获取传入的提示词主键id
		Integer Id = promptConfigListParam.getId();
		PromptConfig updateConfig = getById(Id);
		if (updateConfig == null) {
			return Result.fail("提示词不存在");
		}

		// 超级管理员集合
		List<OauthUser> superManageUser = userService.getSuperManageUser();
		List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

		// 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
		if (accountNames.contains(updateConfig.getCreateUser())){
			// 构造更新对象，已经是预置就取消，否则设置为预置,
			if (OwnerTypeEnum.OFFICIAL.getCode().equals(updateConfig.getOwnerType())
			) {
				updateConfig.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
			} else {
				updateConfig.setOwnerType(OwnerTypeEnum.OFFICIAL.getCode());
			}
		}else{
			return Result.fail("该提示词非超级管理员创建，无法进行预置操作");
		}

		// 执行更新
		promptConfigMapper.update(updateConfig,false);

		return Result.success("提示词预置更改成功");
	}
}