package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.VoiceComponentInfoConfigPageParam;
import com.wenge.model.dto.param.VoiceComponentInfoConfigUpdateParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.entity.VoiceComponentInfoConfig;
import com.wenge.model.mapper.VoiceComponentInfoConfigMapper;
import com.wenge.model.mapper.VoiceComponentInfoMapper;
import com.wenge.model.service.VoiceComponentInfoConfigService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.VoiceComponentInfoConfigTableDef.VOICE_COMPONENT_INFO_CONFIG;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;
import static com.wenge.oauth.enums.OwnerTypeEnum.OFFICIAL;

/**
 * Description: 应用信息服务实现类
 * @Author: zs
 * Version: 1.0
 * Create Date Time: 2025-04-22 11:02:51
 *
 */
@Service
@Slf4j
public class VoiceComponentInfoConfigServiceImpl extends ServiceImpl<VoiceComponentInfoConfigMapper,
		VoiceComponentInfoConfig> implements VoiceComponentInfoConfigService {

	@Autowired
	private VoiceComponentInfoMapper voiceComponentInfoMapper;


	@Override
	public Result<Page<VoiceComponentInfoConfig>> getConfigByPage(VoiceComponentInfoConfigPageParam param) {
		String category = param.getCategory();
		Integer frequenceUseFlag = param.getFrequenceUseFlag();
		if (StringUtils.isBlank(category)) {
			throw new IllegalArgumentException("组件分类不能为空");
		}
		if (Objects.isNull(frequenceUseFlag)) {
			throw new IllegalArgumentException("常用标识不能为空");
		}
		// 查询语音组件,语音播报
		QueryWrapper voiceComponentWrapper = Wrappers.init()
				.where(VOICE_COMPONENT_INFO.CATEGORY.eq(category))
				.and(VOICE_COMPONENT_INFO.DELETE_FLAG.eq(0))
				.and(StringUtils.isNotBlank(param.getOwnerType()),VOICE_COMPONENT_INFO.OWNER_TYPE.eq(OFFICIAL.getCode()));
		List<VoiceComponentInfo> voiceComponentInfoList = voiceComponentInfoMapper.selectListByQuery(voiceComponentWrapper);
		List<String> voiceComponentInfoIds = voiceComponentInfoList.stream().map(VoiceComponentInfo::getComponentId).collect(Collectors.toList());
		if (CollectionUtil.isEmpty(voiceComponentInfoIds)) {
			return Result.success(new Page<>());
		}
		Map<String, VoiceComponentInfo> voiceComponentInfoMap = voiceComponentInfoList.stream()
				.collect(Collectors.toMap(
						VoiceComponentInfo::getComponentId,
						voiceComponentInfo -> voiceComponentInfo
				));
		// 查询语音组件配置
		Wrappers configWrappers = Wrappers.of(mapper)
				.where(VOICE_COMPONENT_INFO_CONFIG.FREQUENCE_USE_FLAG.eq(frequenceUseFlag))
				.and(VOICE_COMPONENT_INFO_CONFIG.VOICE_COMPONENT_ID.in(voiceComponentInfoIds))
				.and(VOICE_COMPONENT_INFO_CONFIG.DELETE_FLAG.eq(0))
				.orderBy(VOICE_COMPONENT_INFO_CONFIG.UPDATE_TIME.desc());

		Page<VoiceComponentInfoConfig> page = configWrappers.page(Page.of(param.getPageNo(), param.getPageSize()));
		List<VoiceComponentInfoConfig> records = page.getRecords();
		if (CollectionUtil.isNotEmpty(records)) {
			records.forEach(v -> {
				VoiceComponentInfo voiceComponentInfo = voiceComponentInfoMap.get(v.getVoiceComponentId());
				v.setFullStyle(voiceComponentInfo.getComponentName()+ "-" + v.getStyle());
			});
		}

		return Result.success(page);
	}


	@Override
	public Result updateConfig(VoiceComponentInfoConfigUpdateParam param) {
		if (StringUtils.isBlank(param.getConfigId())) {
			throw new IllegalArgumentException("声音组件配置业务id为空");
		}
		if (Objects.isNull(param.getFrequenceUseFlag())) {
			throw new IllegalArgumentException("声音组件配置常用标识为空");
		}

		VoiceComponentInfoConfig.create()
				.setFrequenceUseFlag(param.getFrequenceUseFlag())
				.where(VOICE_COMPONENT_INFO_CONFIG.CONFIG_ID.eq(param.getConfigId()))
				.update();
		return Result.success();
	}

}