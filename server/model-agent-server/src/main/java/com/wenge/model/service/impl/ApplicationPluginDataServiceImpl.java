package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Maps;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.controller.ApplicationInfoController;
import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.dto.param.ApplicationConfigEditParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.ApplicationPlugin;
import com.wenge.model.entity.ApplicationPluginData;
import com.wenge.model.enums.AppPluginEnum;
import com.wenge.model.mapper.ApplicationPluginDataMapper;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.ApplicationPluginDataService;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationPluginDataTableDef.APPLICATION_PLUGIN_DATA;
import static com.wenge.model.entity.table.ApplicationPluginTableDef.APPLICATION_PLUGIN;
import static com.wenge.oauth.constants.AppConfigContant.*;

/**
 * Description: 服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 15:31:30
 *
 */
@Service
@Slf4j
public class ApplicationPluginDataServiceImpl extends ServiceImpl<ApplicationPluginDataMapper, ApplicationPluginData> implements ApplicationPluginDataService {
	/**
	 * 	数据库处理类
	 */
	@Autowired
	private ApplicationPluginDataMapper applicationPluginDataMapper;

	/**
	 * 	应用nacos配置服务类
	 */
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	@Autowired
	private ApplicationInfoService applicationInfoService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result addApplicationPluginData(ApplicationConfigEditParam applicationPluginData){
		if (StringUtils.isBlank(applicationPluginData.getApplicationId())) {
			return Result.fail("应用已不存在");
		}

		// 先移除旧的配置
		UpdateChain.create(mapper)
				.where(APPLICATION_PLUGIN_DATA.APPLICATION_ID.eq(applicationPluginData.getApplicationId()))
				.remove();

		// 再批量新增
		List<ApplicationPlugin> pluginList = applicationPluginData.getPluginList().stream()
				.filter(p -> YesNoEnum.YES.getName().equals(p.getStatus())).collect(Collectors.toList());
		List<ApplicationPluginData> dataList = pluginList.stream()
				.filter(p -> YesNoEnum.YES.getName().equals(p.getStatus()))
				.map(p -> {
					ApplicationPluginData data = new ApplicationPluginData();
					data.setApplicationId(applicationPluginData.getApplicationId());
					data.setPluginId(p.getPluginId());
					data.setStatus(p.getStatus());
					return data;
				}).collect(Collectors.toList());

		if (CollectionUtil.isNotEmpty(dataList)) {
			saveBatch(dataList);
		}
		// 等待应用配置更新完成
		ThreadUtil.sleep(1000 * 10);
		// 根据插件，设置应用配置
		ApplicationInfo applicationInfo = applicationInfoService.getByAppId(applicationPluginData.getApplicationId());
		// 如果此时应用在更新，那么就要等应用更新结束后再更新应用配置，因为两边都会更新配置，防止冲突
		String cache = ApplicationInfoController.APP_UPDATE_MAP.get(applicationInfo.getApplicationId());
		if (StringConstant.ONE.equals(cache)) {
			while (true) {
				cache = ApplicationInfoController.APP_UPDATE_MAP.get(applicationInfo.getApplicationId());
				if (!StringConstant.ONE.equals(cache)) {
					break;
				}
				ThreadUtil.sleep(500);
			}
		}

		// 获取应用配置，更新应用配置后，需要在查询的时候同步一下回显
		List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(applicationPluginData.getApplicationId());
		// 模糊引导相关配置
		vague(applicationInfo, pluginList, configurationList);
		// 推荐问题插件
		recommended(applicationPluginData.getApplicationId(), pluginList, configurationList);
		// 联想问题插件
		association(applicationPluginData.getApplicationId(), pluginList, configurationList);
		// 联网插件
		network(applicationPluginData.getApplicationId(), pluginList, configurationList);

		applicationConfigurationService.updateAppConfig(configurationList, applicationPluginData.getApplicationId());
		return Result.success();
	}

	@Override
	public Result<List<ApplicationPluginData>> getApplicationPluginDataList(AppConfigListParam applicationPluginData){
		if (StringUtils.isBlank(applicationPluginData.getApplicationId())) {
			return Result.success(Lists.newArrayList());
		}

		List<ApplicationPluginData> list = Wrappers.of(mapper)
				.select(APPLICATION_PLUGIN_DATA.ALL_COLUMNS, APPLICATION_PLUGIN.PLUGIN_CODE)
				.leftJoin(APPLICATION_PLUGIN).on(APPLICATION_PLUGIN_DATA.PLUGIN_ID.eq(APPLICATION_PLUGIN.PLUGIN_ID))
				.where(APPLICATION_PLUGIN_DATA.APPLICATION_ID.eq(applicationPluginData.getApplicationId()))
				.list();

		List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(applicationPluginData.getApplicationId());
		if (CollectionUtil.isEmpty(configurationList)) {
			return Result.success(list);
		}
		// 显示配置
		showConfig(list, configurationList);

		return Result.success(list);
	}

	@Override
	public Result updateApplicationPluginData(ApplicationPluginData applicationPluginData){
		updateById(applicationPluginData);
		return Result.success();
	}

	@Override
	public Result deleteApplicationPluginData(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	/**
	 * 模糊引导
	 *
	 * @param pluginList
	 */
	private void vague(ApplicationInfo applicationInfo, List<ApplicationPlugin> pluginList, List<ApplicationConfiguration> configurationList) {
		String appId = applicationInfo.getApplicationId();
		List<String> pluginCodeList = pluginList.stream()
				.map(ApplicationPlugin::getPluginCode)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		// 如果没有模糊引导那么将相关配置设置为关
		if (!pluginCodeList.contains(AppPluginEnum.VAGUE_QUESTION.getCode())) {
			// 关闭模糊问题引导开关
			updateConfig(configurationList, VAGUE_GUIDE_ENABLE, YesNoEnum.NO.getName());
			// 是否打开模糊和风险问题总开关，默认否，是/否
			updateConfig(configurationList, VAGUE_RISK_ENABLE, YesNoEnum.NO.getName());
			// 关闭引导数据来源于知识库开关
			updateConfig(configurationList, VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE, YesNoEnum.NO.getName());
			// 关闭推荐问题是否启用来源于模糊问题引导
			updateConfig(configurationList, RECOMMENDED_SOURCE_VAGUE_ENABLE, YesNoEnum.NO.getName());
			// 关闭模糊问题时，是否启用直接返回精确问题，无需要考虑是否多伦对话，是/否
			updateConfig(configurationList, VAGUE_NEED_ENABLE, YesNoEnum.NO.getName());
			// 关闭危险问题
			updateConfig(configurationList, DOORSILL_RISK_ENABLE, YesNoEnum.NO.getName());
			// 关闭模糊问题引导是否使用LLM
			updateConfig(configurationList, VAGUE_FROM_LLM_ENABLE, YesNoEnum.NO.getName());
			// 关闭模糊问题引导是否使用QA
			updateConfig(configurationList, VAGUE_GUIDE_QA_ENABLE, YesNoEnum.NO.getName());
			// 关闭引导数据来源于知识库
			updateConfig(configurationList, VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE, YesNoEnum.NO.getName());
		} else {
			JSONObject configExtend = getConfigExtend(pluginList, AppPluginEnum.VAGUE_QUESTION.getCode());
			if (null == configExtend) {
				return;
			}

			// 识别模糊问题的大模型，给 modelId
			String vagueGuideModelId = configExtend.getStr(VAGUE_GUIDE_MODEL_ID);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_GUIDE_MODEL_ID, vagueGuideModelId, "识别模糊问题的大模型，给 modelId");

			// 模糊问题引导是否使用QA，是/否
			String vagueGuideQaEnable = configExtend.getStr(VAGUE_GUIDE_QA_ENABLE);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_GUIDE_QA_ENABLE, vagueGuideQaEnable, "模糊问题引导是否使用QA，是/否");
			if (YesNoEnum.YES.getName().equals(vagueGuideQaEnable)) {
				// 模糊问题：引导问题数量-QA场景
				ApplicationInfoServiceImpl.updateConfig(configurationList, appId, GUIDE_QUESTION_NUMBER_CONFIG, "3", "模糊问题：引导问题数量-QA场景");
				// 问答形式检索标题ES分数阈值配置-QA场景
				ApplicationInfoServiceImpl.updateConfig(configurationList, appId, QA_TITLE_SCORE_CONFIG, "1.75", "问答形式检索标题ES分数阈值配置-QA场景");
				// 模糊问题：问答形式重排分数阈值配置，QA场景
				ApplicationInfoServiceImpl.updateConfig(configurationList, appId, QA_RANGE_TITLE_SCORE, "0.81", "模糊问题：问答形式重排分数阈值配置，QA场景");
				// 根据已有问题让大模型组织语言-使用QA引导场景
				ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ORGANIZATE_ANSWER_PROMPT, ORGANIZATE_ANSWER_PROMPT_DEFAULT, "根据已有问题让大模型组织语言-使用QA引导场景");
			}

			// 模糊问题引导是否使用LLM，是/否
			String vagueFromLlmEnable = configExtend.getStr(VAGUE_FROM_LLM_ENABLE);
			// 模糊问题引导是否使用LLM，是/否
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_FROM_LLM_ENABLE, vagueFromLlmEnable, "模糊问题引导是否使用LLM，是/否");

			// 引导数据来源于知识库，是/否
			String vaguePromptFromKnowledgeEnable = configExtend.getStr(VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE, vaguePromptFromKnowledgeEnable, "引导数据来源于知识库，是/否");
			if (YesNoEnum.YES.getName().equals(vaguePromptFromKnowledgeEnable)) {
				// 引导数据来源于知识库的es分数阈值
				Float contentScore = applicationInfo.getContentScore();
				if (null == contentScore) {
					contentScore = 1.5f;
				}
				ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_PROMPT_FROM_KNOWLEDGE_ES_SCORE, contentScore.toString(), "引导数据来源于知识库的es分数阈值");
				// 引导数据来源于知识库的重排分数阈值
				Float rangeContentScore = applicationInfo.getRangeContentScore();
				if (null == rangeContentScore) {
					rangeContentScore = 0.7f;
				}
				ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_PROMPT_FROM_KNOWLEDGE_RERANK_SCORE, rangeContentScore.toString(), "引导数据来源于知识库的重排分数阈值");
			}

			// 开启模糊问题引导开关
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_GUIDE_ENABLE, YesNoEnum.YES.getName(), "模糊问题引导开关，默认关，开/关");
			// 开启推荐问题是否启用来源于模糊问题引导
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_SOURCE_VAGUE_ENABLE, YesNoEnum.YES.getName(), "推荐问题是否启用来源于模糊问题引导，默认否");
			// 开启模糊问题时，是否启用直接返回精确问题，无需要考虑是否多伦对话，是/否
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_NEED_ENABLE, YesNoEnum.NO.getName(), "模糊问题时，是否启用直接返回精确问题，无需要考虑是否多伦对话，是/否");
			// 用户问题检查的大模型user指令
			String doorsillLlmUsrPrompt = configExtend.getStr(DOORSILL_LLM_USR_PROMPT);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, DOORSILL_LLM_USR_PROMPT, doorsillLlmUsrPrompt, "用户问题检查的大模型user指令");
			// 模糊问题时，大模型引导的指令，修改指令输出格式可能会影响问题推荐
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_PROMPT_LLM_SYSTEM_PROMPT, DOORSILL_LLM_SYS_PROMPT_DEFAULT, "模糊问题时，大模型引导的指令，修改指令输出格式可能会影响问题推荐，其中${knowledge}将会库替换成知识库数据");
			// 模糊问题引导的只做推荐不做回答，默认否
			String vagueNotAnswerFlag = configExtend.getStr(VAGUE_NOT_ANSWER_FLAG);
			// 模糊问题引导的只做推荐不做回答，默认否，是/否
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_NOT_ANSWER_FLAG, vagueNotAnswerFlag, "模糊问题引导的只做推荐不做回答，默认否，是/否");
			// 开启风险判断开关
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, DOORSILL_RISK_ENABLE, YesNoEnum.YES.getName(), "风险判断开关，默认关，是/否");
			// 大模型发散：回答风险问题的系统prompt
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, DOORSILL_RISK_LLM_SYSTEM_PROMPT, DOORSILL_RISK_LLM_SYSTEM_PROMPT_DEFAULT, "大模型发散：回答风险问题的系统prompt");
			// 是否打开模糊和风险问题总开关，默认否，是/否
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, VAGUE_RISK_ENABLE, YesNoEnum.YES.getName(), "是否打开模糊和风险问题总开关，默认否，是/否");
		}
	}

	/**
	 * 设置开关
	 *
	 * @param configurationList
	 * @param key
	 * @param value
	 */
	private void updateConfig(List<ApplicationConfiguration> configurationList, String key, String value) {
		// 关闭引导数据来源于知识库开关
		Optional<ApplicationConfiguration> any = configurationList.stream()
				.filter(p -> key.equals(p.getKeyInfo()))
				.findAny();
		if (any.isPresent()) {
			ApplicationConfiguration applicationConfiguration = any.get();
			applicationConfiguration.setValueInfo(value);
		}
	}

	/**
	 * 回显模糊问题
	 *
	 * @param list
	 */
	private void showVague(List<ApplicationPluginData> list, HashMap<String, String> configMap) {
		// 判断模糊引导是否存在
		Optional<ApplicationPluginData> any = list.stream()
				.filter(p -> AppPluginEnum.VAGUE_QUESTION.getCode().equals(p.getPluginCode()))
				.findAny();

		if (any.isPresent()) {
			ApplicationPluginData applicationPluginData = any.get();
			JSONObject configExtend = new JSONObject();
			configExtend.set(VAGUE_GUIDE_MODEL_ID, configMap.get(VAGUE_GUIDE_MODEL_ID));
			configExtend.set(VAGUE_GUIDE_QA_ENABLE, configMap.get(VAGUE_GUIDE_QA_ENABLE));
			configExtend.set(VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE, configMap.get(VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE));
			configExtend.set(DOORSILL_LLM_USR_PROMPT, configMap.get(DOORSILL_LLM_USR_PROMPT));
			configExtend.set(VAGUE_NOT_ANSWER_FLAG, configMap.get(VAGUE_NOT_ANSWER_FLAG));
			configExtend.set(VAGUE_FROM_LLM_ENABLE, configMap.get(VAGUE_FROM_LLM_ENABLE));
			applicationPluginData.setConfigExtend(configExtend);
		}
	}

	/**
	 * 回显网络配置
	 *
	 * @param list
	 */
	private void showNetwork(List<ApplicationPluginData> list, HashMap<String, String> configMap) {
		// 判断模糊引导是否存在
		Optional<ApplicationPluginData> any = list.stream()
				.filter(p -> AppPluginEnum.SEARCH_ENGINE.getCode().equals(p.getPluginCode()))
				.findAny();
		if (any.isPresent()) {
			ApplicationPluginData applicationPluginData = any.get();
			JSONObject configExtend = new JSONObject();
			configExtend.set(INTERNET_SEARCH_COUNT, configMap.get(INTERNET_SEARCH_COUNT));
			configExtend.set(INTERNET_REWRITING_SCORE, configMap.get(INTERNET_REWRITING_SCORE));
			configExtend.set(INTERNET_REDIS_COUNT, configMap.get(INTERNET_REDIS_COUNT));
			configExtend.set(INTERNET_REDIS_ENABLE, configMap.get(INTERNET_REDIS_ENABLE));
			applicationPluginData.setConfigExtend(configExtend);
		}
	}

	/**
	 * 回显推荐
	 *
	 * @param list
	 */
	private void showRecommended(List<ApplicationPluginData> list, HashMap<String, String> configMap) {
		// 判断模糊引导是否存在
		Optional<ApplicationPluginData> any = list.stream()
				.filter(p -> AppPluginEnum.RECOMMENDATION.getCode().equals(p.getPluginCode()))
				.findAny();

		if (any.isPresent()) {
			ApplicationPluginData applicationPluginData = any.get();
			JSONObject configExtend = new JSONObject();
			configExtend.set(RECOMMENDED_Q_FLAG, configMap.get(RECOMMENDED_Q_FLAG));
			configExtend.set(RECOMMENDED_A_FLAG, configMap.get(RECOMMENDED_A_FLAG));
			configExtend.set(RECOMMENDED_KNN_FLAG, configMap.get(RECOMMENDED_KNN_FLAG));
			configExtend.set(RECOMMENDED_LLM_FLAG, configMap.get(RECOMMENDED_LLM_FLAG));
			configExtend.set(RECOMMENDED_NUM, configMap.get(RECOMMENDED_NUM));
			applicationPluginData.setConfigExtend(configExtend);
		}
	}

	/**
	 * 回显联想问题
	 *
	 * @param list
	 */
	private void showAssociation(List<ApplicationPluginData> list, HashMap<String, String> configMap) {
		// 判断模糊引导是否存在
		Optional<ApplicationPluginData> any = list.stream()
				.filter(p -> AppPluginEnum.ASSOCIATION.getCode().equals(p.getPluginCode()))
				.findAny();

		if (any.isPresent()) {
			ApplicationPluginData applicationPluginData = any.get();
			JSONObject configExtend = new JSONObject();
			configExtend.set(ASSOCIATION_Q_FLAG, configMap.get(ASSOCIATION_Q_FLAG));
			configExtend.set(ASSOCIATION_A_FLAG, configMap.get(ASSOCIATION_A_FLAG));
			configExtend.set(ASSOCIATION_KNN_FLAG, configMap.get(ASSOCIATION_KNN_FLAG));
			configExtend.set(ASSOCIATION_LLM_FLAG, configMap.get(ASSOCIATION_LLM_FLAG));
			configExtend.set(ASSOCIATION_NUM, configMap.get(ASSOCIATION_NUM));
			applicationPluginData.setConfigExtend(configExtend);
		}
	}

	/**
	 * 联网配置
	 * @param appId
	 * @param pluginList
	 * @param configurationList
	 */
	private void network(String appId, List<ApplicationPlugin> pluginList, List<ApplicationConfiguration> configurationList) {
		JSONObject configExtend = getConfigExtend(pluginList, AppPluginEnum.SEARCH_ENGINE.getCode());
		if (null == configExtend) {
			return;
		}

		// 互联网检索次数，整数
		String internetSearchCount = configExtend.getStr(INTERNET_SEARCH_COUNT);
		ApplicationInfoServiceImpl.updateConfig(configurationList, appId, INTERNET_SEARCH_COUNT, internetSearchCount, "互联网检索次数，整数");

		// 互联网数据重排分数阈值,0-1之间
		String internetRewritingScore = configExtend.getStr(INTERNET_REWRITING_SCORE);
		ApplicationInfoServiceImpl.updateConfig(configurationList, appId, INTERNET_REWRITING_SCORE, internetRewritingScore, "互联网数据重排分数阈值,0-1之间");

		// 互联网的redis保留记录数，整数
		String internetRedisCount = configExtend.getStr(INTERNET_REDIS_COUNT);
		ApplicationInfoServiceImpl.updateConfig(configurationList, appId, INTERNET_REDIS_COUNT, internetRedisCount, "互联网的redis保留记录数，整数");

		// 互联网数据重排次数，整数
		String internetRedisEnable = configExtend.getStr(INTERNET_REDIS_ENABLE);
		ApplicationInfoServiceImpl.updateConfig(configurationList, appId, INTERNET_REDIS_ENABLE, internetRedisEnable, "是否开启互联网检缓存，是/否，默认否");

	}

	/**
	 * 推荐问题插件
	 *
	 * @param appId
	 * @param pluginList
	 */
	private void recommended(String appId, List<ApplicationPlugin> pluginList, List<ApplicationConfiguration> configurationList) {
		List<String> pluginCodeList = pluginList.stream()
				.map(ApplicationPlugin::getPluginCode)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		// 如果没有推荐问题配置，那么取消所有的配置项
		if (!pluginCodeList.contains(AppPluginEnum.RECOMMENDATION.getCode())) {
			// 关闭推荐问题开关
			updateConfig(configurationList, RECOMMENDED_ENABLE, YesNoEnum.NO.getName());
		} else {
			JSONObject configExtend = getConfigExtend(pluginList, AppPluginEnum.RECOMMENDATION.getCode());
			if (null == configExtend) {
				return;
			}

			// 打开推荐问题开关，默认开
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_ENABLE, YesNoEnum.YES.getName(), "是否推荐问题开关，默认是，是/否");

			// 是否开启推荐qa的问题，是/否
			String recommendedQFlag = configExtend.getStr(RECOMMENDED_Q_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_Q_FLAG, recommendedQFlag, "是否开启推荐qa的问题，是/否");

			// 是否开启推荐qa的答案，是/否
			String recommendedAFlag = configExtend.getStr(RECOMMENDED_A_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_A_FLAG, recommendedAFlag, "是否开启推荐qa的答案，是/否");

			// 是否开启推荐知识库内容，是/否
			String recommendedKnnFlag = configExtend.getStr(RECOMMENDED_KNN_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_KNN_FLAG, recommendedKnnFlag, "是否开启推荐知识库内容，是/否");

			// 是否开启推荐LLM内容，是/否
			String recommendedLlmFlag = configExtend.getStr(RECOMMENDED_LLM_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_LLM_FLAG, recommendedLlmFlag, "是否开启推荐LLM内容，是/否");

			// 推荐问题的数量，默认 3
			String recommendedNum = configExtend.getStr(RECOMMENDED_NUM);
			if (StringUtils.isBlank(recommendedNum)) {
				recommendedNum = "3";
			}
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_NUM, recommendedNum, "推荐问题的数量，默认 3");

			// 推荐问题大模型发散的userPrompt
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, RECOMMENDED_PROMPT, RECOMMENDED_PROMPT_DEFAULT, "推荐问题大模型发散的userPrompt");
		}
	}

	/**
	 * 联想问题插件
	 *
	 * @param appId
	 * @param pluginList
	 */
	private void association(String appId, List<ApplicationPlugin> pluginList, List<ApplicationConfiguration> configurationList) {
		List<String> pluginCodeList = pluginList.stream()
				.map(ApplicationPlugin::getPluginCode)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		// 如果没有联想问题配置，那么取消所有的配置项
		if (!pluginCodeList.contains(AppPluginEnum.ASSOCIATION.getCode())) {
			// 关闭联想问题开关
			updateConfig(configurationList, ASSOCIATION_ENABLE, YesNoEnum.NO.getName());
		} else {
			JSONObject configExtend = getConfigExtend(pluginList, AppPluginEnum.ASSOCIATION.getCode());
			if (null == configExtend) {
				return;
			}

			// 打开联想问题开关，默认开
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_ENABLE, YesNoEnum.YES.getName(), "是否联想问题开关，默认是，是/否");

			// 是否开启联想qa的问题，是/否
			String recommendedQFlag = configExtend.getStr(ASSOCIATION_Q_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_Q_FLAG, recommendedQFlag, "是否开启联想qa的问题，是/否");

			// 是否开启联想qa的答案，是/否
			String recommendedAFlag = configExtend.getStr(ASSOCIATION_A_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_A_FLAG, recommendedAFlag, "是否开启联想qa的答案，是/否");

			// 是否开启联想知识库内容，是/否
			String recommendedKnnFlag = configExtend.getStr(ASSOCIATION_KNN_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_KNN_FLAG, recommendedKnnFlag, "是否开启联想知识库内容，是/否");

			// 是否开启联想LLM内容，是/否
			String recommendedLlmFlag = configExtend.getStr(ASSOCIATION_LLM_FLAG);
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_LLM_FLAG, recommendedLlmFlag, "是否开启联想LLM内容，是/否");

			// 联想问题的数量，默认 5
			String recommendedNum = configExtend.getStr(ASSOCIATION_NUM);
			if (StringUtils.isBlank(recommendedNum)) {
				recommendedNum = "5";
			}
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_NUM, recommendedNum, "联想问题的数量，默认 3");

			// 联想问题大模型发散的userPrompt
			ApplicationInfoServiceImpl.updateConfig(configurationList, appId, ASSOCIATION_PROMPT, ASSOCIATION_PROMPT_DEFAULT, "联想问题大模型发散的userPrompt");
		}
	}

	/**
	 * 获取配置项
	 * @param pluginList
	 * @param pluginCode
	 * @return
	 */
	private JSONObject getConfigExtend(List<ApplicationPlugin> pluginList, String pluginCode) {
		Optional<ApplicationPlugin> any = pluginList.stream().filter(p -> pluginCode.equals(p.getPluginCode())).findAny();
		if (!any.isPresent()) {
			return null;
		}
		ApplicationPlugin applicationPlugin = any.get();
		JSONObject configExtend = applicationPlugin.getConfigExtend();
		if (configExtend == null) {
			configExtend = new JSONObject();
		}
		return configExtend;
	}

	/**
	 * 回显配置项
	 * @param list
	 * @param configurationList
	 */
	private void showConfig(List<ApplicationPluginData> list, List<ApplicationConfiguration> configurationList) {
		// 获取配置
		HashMap<String, String> configMap = configurationList.stream()
				.filter(p -> StringUtils.isNotBlank(p.getKeyInfo()) && StringUtils.isNotBlank(p.getValueInfo()))
				.collect(Collectors.toMap(
						ApplicationConfiguration::getKeyInfo,
						ApplicationConfiguration::getValueInfo,
						(old, now) -> now,
						Maps::newHashMap
				));

		// 回显模糊引导配置
		showVague(list, configMap);

		// 回显推荐问题配置
		showRecommended(list, configMap);

		// 回显联想问题配置
		showAssociation(list, configMap);
		// 回显联网配置
		showNetwork(list, configMap);
	}
}