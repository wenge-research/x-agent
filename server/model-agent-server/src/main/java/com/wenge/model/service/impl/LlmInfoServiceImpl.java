package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.Label;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.LlmManufacturerModel;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.model.mapper.LlmInfoMapper;
import com.wenge.model.service.LabelService;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.LlmManufacturerModelService;
import com.wenge.model.strategy.aiAudio.AiAudioStrategy;
import com.wenge.model.strategy.aiImage.AiImageStrategy;
import com.wenge.model.strategy.aiVideo.AiVideoStrategy;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.holder.ContextHolders;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wenge.oauth.util.RemoteCommandUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
import static com.wenge.model.entity.table.LlmInfoTableDef.LLM_INFO;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;
import static com.wenge.model.enums.BusinessPermissionEnum.LLM;
import static com.wenge.oauth.constants.AppConfigContant.MAX_TOKENS;

/**
 * Description: 大模型信息表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-14 14:53:29
 *
 */
@Service
@Slf4j
public class LlmInfoServiceImpl extends ServiceImpl<LlmInfoMapper, LlmInfo> implements LlmInfoService {
	/**
	 * 	大模型信息表数据库处理类
	 */
	@Autowired
	private LlmInfoMapper llmInfoMapper;

	@Autowired
	private Map<String, LlmStrategy> llmStrategyMap;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired(required = false)
	private Map<String, AiImageStrategy> aiImageStrategyMap;

	@Autowired(required = false)
	private Map<String, AiVideoStrategy> aiVideoStrategyMap;

	@Autowired(required = false)
	private Map<String, AiAudioStrategy> aiAudioStrategy;

	@Autowired
	private LabelService labelService;

	@Autowired
	private UserService userService;

	@Autowired
	private LlmManufacturerModelService llmManufacturerModelService;

	@Override
	public Result getLlmInfoList(LlmInfoListParam param){
		// 工作流或应用中添加模型时的接口【推荐all，我的personal】
		String ownerType = param.getOwnerType();
		String powerType = AppContextHolder.getTokenUserInfo().getPowerType();
		Wrappers wrappers = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(param.getStatus()), LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(StringUtils.isNotBlank(param.getTag()), LLM_INFO.TAG.eq(param.getTag()))
				.and(StringUtils.isNotBlank(param.getModelType()), LLM_INFO.MODEL_TYPE.eq(param.getModelType()))
				.and(StringUtils.isNotBlank(param.getManufacturer()), LLM_INFO.MANUFACTURER.eq(param.getManufacturer()))
				.and(StringUtils.isNotBlank(param.getModelName()), LLM_INFO.MODEL_NAME.like(param.getModelName()));
		// .page(Page.of(param.getPageNo(), param.getPageSize()));
		// 根据权限查询
		//	PermissionControlUtils.buildPermission(wrappers, LLM);

		PermissionControlUtils.buildPermission(wrappers, LLM, OwnerTypeEnum.getByCode(ownerType));

		List<LlmInfo> list = list(wrappers);
		List<LlmManufacturerModel> llmManufacturerModels =  llmManufacturerModelService.getLlmManufacturerList();

		Map<String, LlmManufacturerModel> modelMap = llmManufacturerModels.stream()
				.collect(Collectors.toMap(
						LlmManufacturerModel::getManufacturerModelId,
						model -> model
				));
		list.forEach(llmInfo -> {
			Integer defaultTokenL = 1; // 默认最小回复数
			Integer defaultTokenR = 4096; // 默认最大回复数
			if (StringUtils.isNotBlank(llmInfo.getManufacturerModelId())) {
				LlmManufacturerModel llmManufacturerModel =  modelMap.get(llmInfo.getManufacturerModelId());
				if (llmManufacturerModel != null) {
					llmInfo.setMaxTokenL(llmManufacturerModel.getMinResponseTokens());
					llmInfo.setMaxTokenR(llmManufacturerModel.getMaxResponseTokens());
					llmInfo.setManufacturerIcon(llmManufacturerModel.getManufacturerIcon());
					if ("雅意".equals(llmInfo.getManufacturer())) {
						// 页面渲染模型名称
						llmInfo.setModel(llmManufacturerModel.getModel());
					}
				}
			}
			llmInfo.setMaxTokenL(defaultTokenL);
			llmInfo.setMaxTokenR(defaultTokenR);

		});

		return Result.success(list);
	}

	/**
	 * @param llmInfoPageParam
	 * @description: 分页查询
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:55
	 */
	@Override
	public Result getLlmInfoPageList(LlmInfoPageParam llmInfoPageParam) {
		Wrappers<Object> wrapper = Wrappers.init()
				.where(LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.orderBy(LLM_INFO.ID.desc());
		if (StringUtils.isNotBlank(llmInfoPageParam.getOrder()) && StringUtils.isNotBlank(llmInfoPageParam.getSort())) {
			wrapper.orderBy(llmInfoPageParam.getOrder() + " " + llmInfoPageParam.getSort());
		}else {
			wrapper.orderBy(LLM_INFO.CREATE_TIME.desc()); // 默认按更新时间排序
		}
		List<LlmManufacturerModel> llmManufacturerModels =  llmManufacturerModelService.getLlmManufacturerList();

		Map<String, LlmManufacturerModel> modelMap = llmManufacturerModels.stream()
				.collect(Collectors.toMap(
						LlmManufacturerModel::getManufacturerModelId,
						model -> model
				));

		Page<LlmInfo> page = page(Page.of(llmInfoPageParam.getPageNo(), llmInfoPageParam.getPageSize()), wrapper);
		page.getRecords().forEach(llmInfo -> {
			if ("雅意".equals(llmInfo.getManufacturer())) {
				// 页面渲染模型名称
				LlmManufacturerModel llmManufacturerModel = modelMap.get(llmInfo.getManufacturerModelId());
				if (llmManufacturerModel != null) {
					llmInfo.setModel(llmManufacturerModel.getModel());
				}
			}
		});
		return Result.success(page);
	}

	/**
	 * @param llmInfo
	 * @description: 添加模型
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:55
	 */
	@Override
	public Result addLlmInfo(LlmInfoAddUpdateParam llmInfo) {
		//设置模型id
		llmInfo.setModelId(IdUtil.simpleUUID());

		//校验必要参数
		if(checkNeedFullParam(llmInfo)){
			return Result.fail("缺失必要参数，请检查重试");
		}
//		//校验是否重复
//		if(false){
//			return Result.fail("当前模型已经存在，不能重复添加");
//		}
		// 根据供应商字段，获取到对应的默认配置
		LlmInfo defaultConfig = Wrappers.of(mapper).where(LLM_INFO.MANUFACTURER.eq(llmInfo.getManufacturer()))
				// .and(LLM_INFO.DEFAULT_CONFIG.eq(YesNoEnum.YES.getCode()))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(1))
				.and(LLM_INFO.MODEL_TYPE.eq("对话"))
				.one();
		if (defaultConfig == null) {
			return Result.fail("尚未适配该供应商，请联系管理员");
		}
		if (StringUtils.isBlank(llmInfo.getUri())) {
			if ("deploy".equals(llmInfo.getWay())) {
				String llmPort = "11434";
				switch (llmInfo.getModelVersion()) {
					case "1_5b":
						llmPort = "11434";
						break;
					case "7b":
						llmPort = "11435";
						break;
					case "8b":
						llmPort = "11436";
						break;
					default:
						break;
				}
				String uri = "";
				if ("linux".equals(llmInfo.getPlatform())) {
					if (StringUtils.isNotBlank(llmInfo.getHostIp())) {
						uri = "http://" + llmInfo.getHostIp() + ":" + llmPort + "/api/chat";
					}
					defaultConfig.setTag("服务器部署");
				} else if ("window".equals(llmInfo.getPlatform())) {
					uri = "http://agent_x_" + llmInfo.getManufacturer() + "_r1_" + llmInfo.getModelVersion() + ":" + llmPort + "/api/chat";
					defaultConfig.setTag("本地部署");
				}
				defaultConfig.setUri(uri);
			}
		}

		TokenUser tokenUserInfo = ContextHolders.getTokenUserInfo();
		String now = DateUtil.now();

		defaultConfig.setCreateUser(tokenUserInfo.getAccountName());
		defaultConfig.setUpdateUser(tokenUserInfo.getAccountName());
		defaultConfig.setUpdateTime(now);
		defaultConfig.setCreateTime(now);


		defaultConfig.setModelId(llmInfo.getModelId());
		defaultConfig.setModelName(llmInfo.getModelName());
		String model = llmInfo.getModel();
		if ("雅意".equals(llmInfo.getManufacturer())) {
			model = subModel(llmInfo.getModel());
		}
		defaultConfig.setModel(model);
		defaultConfig.setAppSecret(llmInfo.getAppSecret());
		defaultConfig.setAppKey(llmInfo.getAppKey());
		defaultConfig.setDefaultConfig(Integer.valueOf(YesNoEnum.NO.getCode()));
		defaultConfig.setStatus(llmInfo.getStatus());
		defaultConfig.setTag(llmInfo.getTag());
		defaultConfig.setModelType(llmInfo.getModelType());
		defaultConfig.setDesc(llmInfo.getDesc());
		defaultConfig.setManufacturerModelId(llmInfo.getManufacturerModelId());
		if (StringUtils.isNotBlank(llmInfo.getUri())) {
			defaultConfig.setUri(llmInfo.getUri());
		}

		// 添加后是personal，预置后是official，群众没有权限进行操作
		if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
			defaultConfig.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		} else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
			return Result.fail("无权限操作");
		} else {
			defaultConfig.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		}

		if (StringUtils.isNotEmpty(llmInfo.getLabels())) {
			String[] labels = llmInfo.getLabels().split(",");
			String labelType = "1";
			List<Label> labelList = Lists.newArrayList();
			for (String labelName : labels) {
				Label label = new Label();
				label.setLabelName(labelName);
				label.setLabelType(labelType);
				labelList.add(label);
			}
			labelService.batchAdd(labelList,labelType);
			defaultConfig.setLabels(llmInfo.getLabels());
		}

		final int add = llmInfoMapper.insertSelective(defaultConfig);
		if (add > 0) {
			return Result.success("创建成功");
		}
		return Result.fail("创建失败");
	}

	private String subModel(String model) {
		return model.split("-")[0];
	}

	/**
	 * @param llmInfo
	 * @description: 编辑模型
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:55
	 */
	@Override
	public Result editLlmInfo(LlmInfoAddUpdateParam llmInfo) {
		//校验必要参数
		if(checkNeedFullParam(llmInfo)){
			return Result.fail("缺失必要参数，请检查重试");
		}

		Wrappers<Object> wrappers = Wrappers.init().and(LLM_INFO.ID.eq(llmInfo.getId()));
		LlmInfo oldInfo = llmInfoMapper.selectOneByQuery(wrappers);
		llmInfo.setId(oldInfo.getId());
		String model;
		if ("雅意".equals(llmInfo.getManufacturer())) {
			model = subModel(llmInfo.getModel());
			llmInfo.setModel(model);
		}
		final int update = llmInfoMapper.update(llmInfo);
		if (StringUtils.isNotEmpty(llmInfo.getLabels())) {
			String[] labels = llmInfo.getLabels().split(",");
			String labelType = "1";
			List<Label> labelList = Lists.newArrayList();
			for (String labelName : labels) {
				Label label = new Label();
				label.setLabelName(labelName);
				label.setLabelType(labelType);
				labelList.add(label);
			}
			labelService.batchAdd(labelList,labelType);
		}
		if (update > 0) {
			return Result.success("修改成功");
		}
		return Result.fail("修改失败");
	}

	/**
	 * @param id
	 * @description: 删除模型
	 * @author: caohaifeng
	 * @date: 2024/8/14 15:55
	 */
	@Override
	public Result delLlmInfoById(Long id) {
		if (id == null) return null;
		LlmInfo llmInfo = new LlmInfo();
		llmInfo.setId(id);
		llmInfo.setDeleteFlag(1);
		final boolean delete = updateById(llmInfo, true);
		if (delete) {
			return Result.success("删除成功");
		}
		return Result.fail("删除失败");
	}

	@Override
	public LlmStrategy getActiveLLm(String modelId) {
		// 获取模型信息
		Wrappers<Object> wrappers = Wrappers.init()
				.where(LLM_INFO.MODEL_ID.eq(modelId))
				.limit(1);
		LlmInfo llmInfo = getOne(wrappers);
		if (null == llmInfo) {
			log.warn("未找到模型：{}", modelId);
			return null;
		}
		ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
		if (null != applicationInfo) {
			String maxToken = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), MAX_TOKENS);
			if (!Objects.isNull(maxToken)) {
				applicationInfo.setMaxToken(Integer.valueOf(maxToken));
				llmInfo.setMaxTokens(applicationInfo.getMaxToken());
				llmInfo.setMaxNewTokens(applicationInfo.getMaxToken());
				llmInfo.setMaxNewTokens(applicationInfo.getMaxToken());
			}
			applicationInfo.setLlmInfo(llmInfo);
		}
		LlmStrategy llmStrategy = llmStrategyMap.get(llmInfo.getModelCode());
		if (null == llmStrategy) {
			log.warn("未找到大模型：{}", modelId);
			return null;
		}
		return llmStrategy;
	}

	@Override
	public Result<List<String>> getLlmManufacturer(EmptyParam param) {
		List<LlmInfo> list = Wrappers.of(mapper)
				.select(QueryMethods.distinct(LLM_INFO.MANUFACTURER))
				.list();
		List<String> manufacturerList = list.stream().map(LlmInfo::getManufacturer).distinct().collect(Collectors.toList());
		return Result.success(manufacturerList);
	}

	@Override
	public Result<Page<LlmInfo>> getLlmPageList(LlmPageListParam param) {
		Wrappers wrappers = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(param.getStatus()), LLM_INFO.STATUS.eq(param.getStatus()))
				.and(StringUtils.isNotBlank(param.getManufacturer()), LLM_INFO.MANUFACTURER.eq(param.getManufacturer()))
				.and(StringUtils.isNotBlank(param.getTags()), LLM_INFO.MANUFACTURER.eq(param.getTags()))
				.and(QueryMethods.findInSet(QueryMethods.column("'" + param.getTags() + "'"), VOICE_COMPONENT_INFO.TAG).gt(0).when(StringUtils.isNotBlank(param.getTags())))
				.and(StringUtils.isNotBlank(param.getModelType()), LLM_INFO.MANUFACTURER.eq(param.getModelType()))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(StringUtils.isNotBlank(param.getModelName()), LLM_INFO.MODEL_NAME.like(param.getModelName()));
		// .page(Page.of(param.getPageNo(), param.getPageSize()));
		// 根据权限查询，如果是前台对话，则不需要根据权限查询
		if (!YesNoEnum.YES.getName().equals(param.getFromClientFlag())) {
			PermissionControlUtils.buildPermission(wrappers, LLM);
		}
		wrappers.orderBy(LLM_INFO.CREATE_TIME.desc());
		Page<LlmInfo> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
		List<LlmManufacturerModel> llmManufacturerModels =  llmManufacturerModelService.getLlmManufacturerList();

		Map<String, LlmManufacturerModel> modelMap = llmManufacturerModels.stream()
				.collect(Collectors.toMap(
						LlmManufacturerModel::getManufacturerModelId,
						model -> model
				));
		for (LlmInfo llmInfo : page.getRecords()) {
			Integer defaultTokenL = 1; // 默认最小回复数
			Integer defaultTokenR = 4096; // 默认最大回复数
			if (StringUtils.isNotBlank(llmInfo.getManufacturerModelId())) {
				LlmManufacturerModel llmManufacturerModel =  modelMap.get(llmInfo.getManufacturerModelId());
				if (llmManufacturerModel != null) {
					llmInfo.setMaxTokenL(llmManufacturerModel.getMinResponseTokens());
					llmInfo.setMaxTokenR(llmManufacturerModel.getMaxResponseTokens());
					llmInfo.setManufacturerIcon(llmManufacturerModel.getManufacturerIcon());
					llmInfo.setBaseModelName(llmManufacturerModel.getModelName());
					if ("雅意".equals(llmInfo.getManufacturer())) {
						// 页面渲染模型名称
						llmInfo.setModel(llmManufacturerModel.getModel());
					}
				}

			}
			llmInfo.setMaxTokenL(defaultTokenL);
			llmInfo.setMaxTokenR(defaultTokenR);
		}

		return Result.success(page);
	}

	@Override
	public Result editLlm(LlmInfo llmInfo) {
		if (StringUtils.isBlank(llmInfo.getModelName())) {
			return Result.fail("模型名称不能为空");
		}

		LlmInfo llm = Wrappers.of(mapper)
				.where(LLM_INFO.MODEL_ID.eq(llmInfo.getModelId()))
				.one();
		if (Objects.equals(llm.getDefaultConfig(), Integer.valueOf(YesNoEnum.YES.getCode()))) {
			return Result.fail("默认模型不能修改");
		}
		// 确保不会影响到默认模型
		llmInfo.setDefaultConfig(Integer.valueOf(YesNoEnum.NO.getCode()));
		if (StringUtils.isBlank(llmInfo.getModelId())) {
			llmInfo.setModelId(IdUtil.simpleUUID());
		}
		String model;
		if ("雅意".equals(llmInfo.getManufacturer())) {
			model = subModel(llmInfo.getModel());
			llmInfo.setModel(model);
		}

		saveOrUpdate(llmInfo);
		return Result.success();
	}

	@Override
	public Result deleteLlm(ListStringParam idList) {
		if (CollectionUtil.isEmpty(idList.getParam())) {
			return Result.fail("模型不能为空");
		}

		UpdateChain.create(mapper)
				.where(LLM_INFO.MODEL_ID.in(idList.getParam()))
				.remove();

		return Result.success();
	}

	@Override
	public LlmInfo getByModelName(String modelName) {
		// 获取模型信息
		Wrappers<Object> wrappers = Wrappers.init()
				.where(LLM_INFO.MODEL_NAME.eq(modelName))
				.limit(1);
        return getOne(wrappers);
	}

	@Override
	public LlmInfo selectModel(String modelId) {
		// 获取模型信息
		Wrappers<Object> wrappers = Wrappers.init()
				.where(LLM_INFO.MODEL_ID.eq(modelId))
				.limit(1);
		LlmInfo llmInfo = getOne(wrappers);
		if (null == llmInfo) {
			log.warn("未找到模型：{}", modelId);
			return null;
		}
		LlmStrategy llmStrategy = llmStrategyMap.get(llmInfo.getModelCode());
		if (null == llmStrategy) {
			log.warn("未找到大模型：{}", modelId);
			return null;
		}
		llmInfo.setLlmStrategy(llmStrategy);
		return llmInfo;
	}

	@Override
	public LlmInfo getByModelId(String modelId) {
		// 获取模型信息
		Wrappers<Object> wrappers = Wrappers.init()
				.where(LLM_INFO.MODEL_ID.eq(modelId))
				.limit(1);
		return getOne(wrappers);
	}

	@Override
	public LlmInfo getByDefaultImageModelCode(String modelCode) {
		if (StringUtils.isBlank(modelCode)) {
			return null;
		}

		LlmInfo llmInfo = Wrappers.of(mapper)
				.where(LLM_INFO.MODEL_CODE.eq(modelCode))
				.and(LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.TAG.like("图片"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(LLM_INFO.MODEL_TYPE.like("多模态"))
				.limit(1)
				.one();

		return llmInfo;
	}
	@Override
	public LlmInfo getByDefaultVideoModelCode(String modelCode) {
		if (StringUtils.isBlank(modelCode)) {
			return null;
		}

		LlmInfo llmInfo = Wrappers.of(mapper)
				.where(LLM_INFO.MODEL_CODE.eq(modelCode))
				.and(LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.TAG.like("视频"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(LLM_INFO.MODEL_TYPE.like("多模态"))
				.limit(1)
				.one();

		return llmInfo;
	}

	@Override
	public Result deployLlm(LlmDeployParam llmDeployParam, HttpServletResponse response) {
		if (StringUtils.isBlank(llmDeployParam.getPlatform())) {
			return Result.fail("平台不能为空");
		}
		if (StringUtils.isBlank(llmDeployParam.getManufacturer())) {
			return Result.fail("厂商不能为空");
		}
		if (StringUtils.isBlank(llmDeployParam.getModelVersion())) {
			return Result.fail("版本不能为空");
		}
		if ("linux".equals(llmDeployParam.getPlatform())) {
			if (StringUtils.isBlank(llmDeployParam.getHostIp())) {
				return Result.fail("主机ip不能为空");
			}
			if (StringUtils.isBlank(llmDeployParam.getHostUser())) {
				return Result.fail("主机用户不能为空");
			}
			if (StringUtils.isBlank(llmDeployParam.getHostPw())) {
				return Result.fail("主机密码不能为空");
			}
			if (null == llmDeployParam.getHostPort()) {
				llmDeployParam.setHostPort(22);
			}
		}
		switch (llmDeployParam.getPlatform()) {
			case "linux":
				Result result = deployLinux(llmDeployParam);
				if (!Result.success().getCode().equals(result.getCode())) {
					return result;
				}
				break;
			case "window":
				deployWindow(llmDeployParam, response);
				break;
			default:
				return Result.fail("平台不支持");
		}

		LlmInfoAddUpdateParam llmInfo = new LlmInfoAddUpdateParam();
		llmInfo.setManufacturer(llmDeployParam.getManufacturer());
		llmInfo.setStatus("启用");
		// 目前这是设置 deepseek 的 r1模型，如果后续有更多模型，这里要换个方式设置模型名称和模型版本号
		llmInfo.setModelName(llmDeployParam.getManufacturer() + "-r1-" + llmDeployParam.getModelVersion());
		llmInfo.setModel(llmDeployParam.getManufacturer() + "-r1:" + llmDeployParam.getModelVersion().replace("_", "."));

		llmInfo.setHostIp(llmDeployParam.getHostIp());
		llmInfo.setPlatform(llmDeployParam.getPlatform());
		llmInfo.setModelVersion(llmDeployParam.getModelVersion());
		llmInfo.setWay("deploy");
		llmInfo.setAppKey("default");
		llmInfo.setAppSecret("default");
		addLlmInfo(llmInfo);
		return Result.success();
	}

	@Override
	public Result testConnectLinux(LlmDeployParam llmDeployParam) {
		RemoteCommandUtil remoteCommandUtil = new RemoteCommandUtil(llmDeployParam.getHostIp(), llmDeployParam.getHostUser(), llmDeployParam.getHostPw(), llmDeployParam.getHostPort());
		boolean result = remoteCommandUtil.testSSHConnection();
		return Result.success(result);
	}

	@Override
	public AiImageStrategy getAiImageStrategy(String modelId) {
		if (StringUtils.isBlank(modelId)) {
			return null;
		}

		LlmInfo llmInfo = Wrappers.of(mapper)
				.where(LLM_INFO.MODEL_ID.eq(modelId))
				.and(LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.TAG.like("图片"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(LLM_INFO.MODEL_TYPE.like("多模态"))
				.limit(1)
				.one();
		if (null != aiImageStrategyMap && !aiImageStrategyMap.isEmpty()) {
			return aiImageStrategyMap.get(llmInfo.getModelCode());
		}

		return null;
	}

	@Override
	public AiVideoStrategy getAiVedioStrategy(String modelId) {
		if (StringUtils.isBlank(modelId)) {
			return null;
		}

		LlmInfo llmInfo = Wrappers.of(mapper)
				.where(LLM_INFO.MODEL_ID.eq(modelId))
				.and(LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.TAG.like("视频"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(LLM_INFO.MODEL_TYPE.like("多模态"))
				.limit(1)
				.one();
		if (null != aiVideoStrategyMap && !aiVideoStrategyMap.isEmpty()) {
			return aiVideoStrategyMap.get(llmInfo.getModelCode());
		}

		return null;
	}

	@Override
	public AiAudioStrategy getAiAudioStrategy(String modelId) {
		if (StringUtils.isBlank(modelId)) {
			return null;
		}

		LlmInfo llmInfo = Wrappers.of(mapper)
				.where(LLM_INFO.MODEL_ID.eq(modelId))
				.and(LLM_INFO.STATUS.eq("启用"))
				.and(LLM_INFO.TAG.like("音频"))
				.and(LLM_INFO.DEFAULT_CONFIG.eq(0))
				.and(LLM_INFO.MODEL_TYPE.like("多模态"))
				.limit(1)
				.one();
		if (null != aiAudioStrategy && !aiAudioStrategy.isEmpty()) {
			return aiAudioStrategy.get(llmInfo.getModelCode());
		}

		return null;
	}



	private boolean checkNeedFullParam(LlmInfoAddUpdateParam llmInfo) {
		if(StringUtils.isBlank(llmInfo.getModelName())){
			log.error("checkNeedFullParam 模型记录名称不能为空");
			return Boolean.TRUE;
		}
		if (StringUtils.isBlank(llmInfo.getAppKey())) {
			log.error("checkNeedFullParam 模型记录appKey不能为空");
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * window部署
	 * @param llmDeployParam
	 * @param response
	 */
	private void deployWindow(LlmDeployParam llmDeployParam, HttpServletResponse response) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			Resource resource = resourceLoader.getResource("classpath:llm/" + llmDeployParam.getManufacturer() + "/" + llmDeployParam.getModelVersion() + ".bat");
			inputStream = resource.getInputStream();

			// 设置响应头
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			// 获取文件名，并进行UTF-8编码
			String fileName = URLEncoder.encode(llmDeployParam.getManufacturer() + "/" + llmDeployParam.getModelVersion() + ".bat", StandardCharsets.UTF_8.toString());
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

			// 将文件写入响应输出流
			outputStream = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != outputStream) {
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * linux部署
	 * @param llmDeployParam
	 */
	private Result deployLinux(LlmDeployParam llmDeployParam) {
		RemoteCommandUtil remoteCommandUtil = new RemoteCommandUtil(llmDeployParam.getHostIp(), llmDeployParam.getHostUser(), llmDeployParam.getHostPw(), llmDeployParam.getHostPort());

		switch (llmDeployParam.getManufacturer()) {
			case "deepseek":
				break;
			default:
				log.error("linux部署失败，不支持的厂商：{}", llmDeployParam.getManufacturer());
				return Result.fail("不支持的厂商");
		}

		boolean successFlag = remoteCommandUtil.testSSHConnection();
		if (!successFlag) {
			log.error("linux部署失败，无法连接主机");
			return Result.fail("无法连接主机");
		}

		// 检测是否有 docker 环境
		String dockerVersion = remoteCommandUtil.executeCommand("docker --version");
		if (!dockerVersion.startsWith("Docker version ")) {
			log.error("linux部署失败，服务器未安装 docker 环境");
			return Result.fail("服务器未安装 docker 环境");
		}

		// 镜像仓库
		String dockerRegistry = "ccr.ccs.tencentyun.com/wenge/agent-llm";
		// 镜像版本
		String tag = llmDeployParam.getManufacturer() + "_" + llmDeployParam.getModelVersion();
		// 容器名称
		String containerName = "agent_x_" + llmDeployParam.getManufacturer() + "_r1_" + llmDeployParam.getModelVersion();
		// 拉取镜像
		String imageName = dockerRegistry + ":" + tag;
		String pullImage = "docker pull " + imageName;
		String result = remoteCommandUtil.executeCommand(pullImage);
		if (StringUtils.isBlank(result) && !result.contains("Status: Image is up to date") && !result.contains("Status: Downloaded newer image for")) {
			log.error("linux部署失败，拉取镜像失败:{}", result);
			return Result.fail("拉取镜像失败");
		}

		// 检测是否已部署大模型，如果是已部署就重启
		String checkCmd = "docker ps -a | grep " + containerName;
		// 重启大模型
		result = remoteCommandUtil.executeCommand(checkCmd);
		if (StringUtils.isNotBlank(result)) {
			String rmCmd = "docker rm -f " + containerName;
			result = remoteCommandUtil.executeCommand(rmCmd);
			if (StringUtils.isBlank(result)) {
				log.error("linux部署失败，重启大模型失败:{}", result);
				return Result.fail("部署大模型失败");
			}
		}

		// 启动大模型

		String llmPort = "11434";
		switch (llmDeployParam.getModelVersion()) {
			case "1_5b":
				llmPort = "11434";
				break;
			case "7b":
				llmPort = "11435";
				break;
			case "8b":
				llmPort = "11436";
				break;
			default:
				break;
		}
		String startCmd = "docker run --restart=always -d -p " + llmPort + ":11434  --name " + containerName + "  -e OLLAMA_HOST=0.0.0.0:11434 " + imageName;
		result = remoteCommandUtil.executeCommand(startCmd);
		if (StringUtils.isBlank(result) || result.contains("docker: Error")) {
			log.error("linux部署失败，启动大模型失败:{}", result);
			return Result.fail("部署大模型失败");
		}
		return Result.success();
	}

	@Override
	public Result setPreset(LlmInfoPageParam llmInfoPageParam) {
		if (llmInfoPageParam == null){
			return Result.fail("请求参数为空");
		}
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		// 只有超管才能操作
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
			return Result.fail("无操作权限");
		}

		// 获取传入的模型id
		Integer Id = llmInfoPageParam.getId();
		LlmInfo updateLlmInfo = llmInfoMapper.selectOneById(Id);
		if (updateLlmInfo == null) {
			return Result.fail("模型不存在");
		}

		// 超级管理员集合
		List<OauthUser> superManageUser = userService.getSuperManageUser();
		List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

		// 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
		if (accountNames.contains(updateLlmInfo.getCreateUser())){
			// 构造更新对象，已经是预置就取消，否则设置为预置,
			if (OwnerTypeEnum.OFFICIAL.getCode().equals(updateLlmInfo.getOwnerType())
			) {
				updateLlmInfo.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
			} else {
				updateLlmInfo.setOwnerType(OwnerTypeEnum.OFFICIAL.getCode());
			}
		}else{
			return Result.fail("该模型非超级管理员创建，无法进行预置操作");
		}

		// 执行更新
		llmInfoMapper.update(updateLlmInfo,false);

		return Result.success("模型预置更改成功");
	}
}