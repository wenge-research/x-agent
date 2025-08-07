package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.VoiceComponentInfoDeleteParam;
import com.wenge.model.dto.param.VoiceComponentInfoPageParam;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.entity.VoiceComponentInfoConfig;
import com.wenge.model.enums.FrequenceUseFlagEnum;
import com.wenge.model.mapper.VoiceComponentInfoConfigMapper;
import com.wenge.model.mapper.VoiceComponentInfoMapper;
import com.wenge.model.service.VoiceComponentInfoConfigService;
import com.wenge.model.service.VoiceComponentInfoService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.results.EmptyResult;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.model.entity.table.VoiceComponentInfoConfigTableDef.VOICE_COMPONENT_INFO_CONFIG;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;
import static com.wenge.oauth.enums.OwnerTypeEnum.OFFICIAL;
import static com.wenge.oauth.enums.OwnerTypeEnum.PERSONAL;

/**
 * Description: 应用信息服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
@Service
@Slf4j
public class VoiceComponentInfoServiceImpl extends ServiceImpl<VoiceComponentInfoMapper,
		VoiceComponentInfo> implements VoiceComponentInfoService {

	@Autowired
	private VoiceComponentInfoMapper voiceComponentInfoMapper;

	@Autowired
	private VoiceComponentInfoConfigMapper voiceComponentInfoConfigMapper;

	@Autowired
	private WosUtil wosUtil;

	@Value("${appframe.minio.bucket}")
	private String bucket;

	@Autowired
	private UserService userService;

	@Autowired
	private VoiceComponentInfoConfigService voiceComponentInfoConfigService;
	@Override
	public Result<Page<VoiceComponentInfo>> getList(VoiceComponentInfoPageParam param) {
		// 语音输出
		// 登录用户信息
		TokenUser user = AppContextHolder.getTokenUserInfo();

		if (null == user) {
			return Result.fail("无法获取到登录用户信息");
		}
		if (StringUtils.isBlank(user.getPowerType())) {
			return Result.fail("用户权限类型为空,暂无权限");
		}

		Wrappers<VoiceComponentInfo> wrappers = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(param.getName()), VOICE_COMPONENT_INFO.COMPONENT_NAME.like(param.getName()))
				.and(StringUtils.isNotBlank(param.getCategory()), VOICE_COMPONENT_INFO.CATEGORY.eq(param.getCategory()))
				.and(!Objects.isNull(param.getFrequenceUseFlag()), VOICE_COMPONENT_INFO.FREQUENCE_USE_FLAG.eq(param.getFrequenceUseFlag())
						.or(VOICE_COMPONENT_INFO.FREQUENCE_USE_FLAG.ne(param.getFrequenceUseFlag())
				.and(QueryMethods.findInSet(QueryMethods.column("'" + param.getTag() + "'"), VOICE_COMPONENT_INFO.TAG).gt(0)
						.when(StringUtils.isNotBlank(param.getTag())))))
								.orderBy(VOICE_COMPONENT_INFO.UPDATE_TIME.desc());
		/*
		 *用户数据权限控制
		 */
		// 普通用户
//		if (StringUtils.isNotBlank(user.getPowerType()) && PowerTypeEnum.NORMAL_USER.getCode().equals(user.getPowerType())) {
//			wrappers.and(VOICE_COMPONENT_INFO.CREATE_USER.eq(user.getAccountName()));
//		}
		// 普通管理员(租户管理员)
//		if (StringUtils.isNotBlank(user.getPowerType()) && PowerTypeEnum.NORMAL_ADMIN.getCode().equals(user.getPowerType())) {
//			wrappers.and(VOICE_COMPONENT_INFO.TENANT_ID.eq(user.getTenantId()));
//		}

		if(StringUtils.isNotBlank(param.getOwnerType())){
			wrappers.and(VOICE_COMPONENT_INFO.OWNER_TYPE.eq(OFFICIAL.getCode()));
		}


		Page<VoiceComponentInfo> page = wrappers.page(Page.of(param.getPageNo(), param.getPageSize()));
		List<VoiceComponentInfo> records = page.getRecords();
		if (CollectionUtil.isNotEmpty(records)) {
			records.forEach(v -> {
				String tag = v.getTag();
				if (StringUtils.isNotBlank(tag)) {
					List<String> tagList = Arrays.asList(tag.split("，"));
					v.setTagList(tagList);
				}
			});
		}

		return Result.success(page);
	}

	@Override
	public Result addInfo(VoiceComponentInfo param) {
		boolean addFlag = true;
		if (!Objects.isNull(param.getId())) {
			// 修改
			addFlag = false;
			return update(param);
		}

		// 新增
		TokenUser user = AppContextHolder.getTokenUserInfo();
		if (null != user) {
			param.setCreateUser(user.getAccountName());
			param.setUpdateUser(user.getAccountName());
			param.setTenantId(user.getTenantId());
		}
		param.setComponentId(IdUtil.randomUUID());
		if (Objects.isNull(param.getFrequenceUseFlag())) {
			param.setFrequenceUseFlag(FrequenceUseFlagEnum.frequence.getCode());
		}

		save(param);
		if (addFlag) {
			VoiceComponentInfoConfig config = VoiceComponentInfoConfig.create()
					.setConfigId(IdUtil.randomUUID())
					.setVoiceId(param.getId())
					.setVoiceComponentId(param.getComponentId())
					.setStyle("默认")
					.setFrequenceUseFlag(param.getFrequenceUseFlag())
					.setCreateUser(param.getCreateUser())
					.setUpdateUser(param.getUpdateUser());

			voiceComponentInfoConfigMapper.insert(config);
		}

		return Result.success();
	}


	@Override
	public Result flush() {
		List<VoiceComponentInfo> voiceComponentInfos = list();
		ArrayList<VoiceComponentInfoConfig> voiceComponentInfoConfigs = new ArrayList<>();
		List<VoiceComponentInfoConfig> existVoiceComponentInfoConfigList = voiceComponentInfoConfigService.list();
		List<Long> voiceComponentInfoIds = existVoiceComponentInfoConfigList.stream().map(VoiceComponentInfoConfig::getVoiceId).distinct().collect(Collectors.toList());
		List<VoiceComponentInfo> notExistVoiceComponentInfos = voiceComponentInfos.stream().filter(voiceComponentInfo -> !voiceComponentInfoIds.contains(voiceComponentInfo.getId())).collect(Collectors.toList());
		for (VoiceComponentInfo voiceComponentInfo : notExistVoiceComponentInfos) {
			VoiceComponentInfoConfig config = VoiceComponentInfoConfig.create()
					.setConfigId(IdUtil.randomUUID())
					.setVoiceId(voiceComponentInfo.getId())
					.setVoiceComponentId(voiceComponentInfo.getComponentId())
					.setStyle("默认")
					.setFrequenceUseFlag(voiceComponentInfo.getFrequenceUseFlag())
					.setCreateUser(voiceComponentInfo.getCreateUser())
					.setUpdateUser(voiceComponentInfo.getUpdateUser());
			voiceComponentInfoConfigs.add(config);
		}

		if (CollectionUtil.isNotEmpty(voiceComponentInfoConfigs)) {
			voiceComponentInfoConfigMapper.insertBatch(voiceComponentInfoConfigs);
		}

		return Result.success("刷表");
	}
	@Override
	public Result update(VoiceComponentInfo param) {
		// 用户信息
		TokenUser user = AppContextHolder.getTokenUserInfo();

		Long id = param.getId();
		if(Objects.isNull(id)){
			return Result.fail("id is not empty...");
		}
		VoiceComponentInfo componentInfo = getById(id);
		BeanUtils.copyProperties(param,componentInfo);
		componentInfo.setUpdateTime(DateUtil.getCurrentTime(DateUtil.DEFAULT_FORMAT));
		componentInfo.setUpdateUser(user.getAccountName());
		voiceComponentInfoMapper.update(componentInfo);
		return Result.success();
	}

	@Override
	public Result deleteByIds(VoiceComponentInfoDeleteParam param) {
		List<Long> ids = param.getIds();
		if(Objects.isNull(ids) || ids.size() == 0){
			return Result.fail("ids is not empty...");
		}
		voiceComponentInfoMapper.deleteBatchByIds(ids);
		return Result.success();
	}

	@Override
	public Result<VoiceComponentInfo> getDetail(Integer id) {
		if (null == id) {
			return Result.success(new VoiceComponentInfo());
		}
		VoiceComponentInfo componentInfo = getById(id);
		if (null == componentInfo) {
			return Result.fail("组件已被删除");
		}
		// 设置标签列表
		String tag = componentInfo.getTag();
		if (StringUtils.isNotBlank(tag)) {
			List<String> tagList = Arrays.asList(tag.split("，"));
			componentInfo.setTagList(tagList);
		}
		// 设置关联的组件配置
		QueryWrapper voiceComponentConfigWrapper = Wrappers.init()
				.where(VOICE_COMPONENT_INFO_CONFIG.VOICE_COMPONENT_ID.eq(componentInfo.getComponentId()))
				.and(VOICE_COMPONENT_INFO_CONFIG.DELETE_FLAG.eq(0));
		List<VoiceComponentInfoConfig> voiceComponentInfoConfigList = voiceComponentInfoConfigMapper.selectListByQuery(voiceComponentConfigWrapper);
		componentInfo.setVoiceComponentInfoConfigList(voiceComponentInfoConfigList);
		return Result.success(componentInfo);
	}

	@Override
	public Result uploadPic(MultipartFile file) throws WosException {
		MinioInfoResult infoResult = wosUtil.upload(bucket, "voiceComponent", file, true);
		return Result.success(infoResult.getUrlPath());
	}

	@Override
	public Result getTtsTags(EmptyParam param) {
		List<VoiceComponentInfo> list = list();
		List<String> tagList = list.stream().flatMap(p -> {
			String tag = p.getTag();
			if (StringUtils.isNotBlank(tag)) {
				String[] split = tag.split(",");
				return Stream.of(split);
			}
			return Stream.empty();
		}).collect(Collectors.toList());
		return Result.success(tagList);
	}

	@Override
	public Result setPreset(VoiceComponentInfo param) {
		if (param == null) {
			return Result.fail("请求参数为空");
		}
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		// 只有超管才能操作
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
			return Result.fail("无操作权限");
		}

		// 获取传入的语音主键id
		Long Id = param.getId();
		VoiceComponentInfo updateVoice = getById(Id);
		if (updateVoice == null) {
			return Result.fail("语音不存在");
		}

		// 超级管理员集合
		List<OauthUser> superManageUser = userService.getSuperManageUser();
		List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

		// 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
		if (accountNames.contains(updateVoice.getCreateUser())){
			// 构造更新对象，已经是预置就取消，否则设置为预置,
			if (OFFICIAL.getCode().equals(updateVoice.getOwnerType())
			) {
				updateVoice.setOwnerType(PERSONAL.getCode());
			} else {
				updateVoice.setOwnerType(OFFICIAL.getCode());
			}
		}else{
			return Result.fail("该语音非超级管理员创建，无法进行预置操作");
		}

		// 执行更新
		updateById(updateVoice);

		return Result.success("语音预置更改成功");
	}


}