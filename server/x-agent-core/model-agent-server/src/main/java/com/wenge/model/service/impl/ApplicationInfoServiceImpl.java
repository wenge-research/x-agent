package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.ApplicationConstant;
import com.wenge.model.constants.DialogueConstant;
import com.wenge.model.constants.UserInfoConstant;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.AiImageResult;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.dto.result.ComponentNodeDto;
import com.wenge.model.dto.result.WorkFlowDto;
import com.wenge.model.dto.template.ApplicationInfoVersionDTO;
import com.wenge.model.entity.*;
import com.wenge.model.entity.table.ApplicationInfoTableDef;
import com.wenge.model.entity.table.DialogTemplateTableDef;
import com.wenge.model.enums.*;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.mapper.*;
import com.wenge.model.mapper.es.ApplicationInfoVersionIndexMapper;
import com.wenge.model.service.*;
import com.wenge.model.strategy.aiAudio.AiAudioStrategy;
import com.wenge.model.strategy.aiAudio.MinmaxAudioStrategy;
import com.wenge.model.strategy.aiImage.AiImageStrategy;
import com.wenge.model.strategy.aiVideo.AiVideoStrategy;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.SseEmitterUtils;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.ComponentNodeRel;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentTypeEnum;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wenge.oauth.dto.param.ApplicationConfigurationParam;
import com.wenge.oauth.entity.*;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.holder.ContextHolders;
import com.wenge.oauth.mapper.GrantDataMapper;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wenge.oauth.service.AuthChannelService;
import com.wenge.oauth.service.OauthTenantService;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.JsonUtil;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.yayi.api.DeepseekServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;
import static com.wenge.model.entity.table.ApplicationPluginDataTableDef.APPLICATION_PLUGIN_DATA;
import static com.wenge.model.entity.table.ApplicationPluginTableDef.APPLICATION_PLUGIN;
import static com.wenge.model.entity.table.ApplicationQuickCommandTableDef.APPLICATION_QUICK_COMMAND;
import static com.wenge.model.entity.table.DialogTemplateTableDef.DIALOG_TEMPLATE;
import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.InterceptWordHouseApplicationRelTableDef.INTERCEPT_WORD_HOUSE_APPLICATION_REL;
import static com.wenge.model.entity.table.InterceptWordHouseTableDef.INTERCEPT_WORD_HOUSE;
import static com.wenge.model.entity.table.MyFavoriteTableDef.MY_FAVORITE;
import static com.wenge.model.entity.table.PresetQuestionTableDef.PRESET_QUESTION;
import static com.wenge.model.entity.table.UrlParserInfoTableDef.URL_PARSER_INFO;
import static com.wenge.model.workflow.entity.table.ComponentTableDef.COMPONENT;
import static com.wenge.oauth.constants.AppConfigContant.*;
import static com.wenge.oauth.entity.table.GrantDataTableDef.GRANT_DATA;
import static com.wg.appframe.core.constant.StringConstant.ONE;

/**
 * Description: 应用信息服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
@Service
@Slf4j
public class ApplicationInfoServiceImpl extends ServiceImpl<ApplicationInfoMapper, ApplicationInfo> implements ApplicationInfoService {
	/**
	 * 	应用版本信息数据库处理类
	 */
	@Autowired
	private ApplicationInfoVersionMapper applicationInfoVersionMapper;

	@Autowired
	private ApplicationKnowledgeService applicationKnowledgeService;

	@Autowired
	private PresetQuestionService presetQuestionService;

	@Autowired
	private DialogTemplateService templateService;

	@Autowired
	private ApplicationPluginDataService applicationPluginDataService;

	@Autowired
	private ApplicationPluginService applicationPluginService;

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private LlmInfoService llmInfoService;

	@Autowired
	private AuthChannelService authChannelService;

	@Autowired
	private InterceptWordHouseApplicationRelService relService;

	@Autowired(required = false)
	private Map<String, AiImageStrategy> aiImageStrategyMap;

	@Autowired(required = false)
	private Map<String, AiVideoStrategy> aiVideoStrategyMap;

	@Autowired(required = false)
	private Map<String, MinmaxAudioStrategy> aiAudioStrategyMap;

	/**
	 * 	应用nacos配置服务类
	 */
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	@Autowired
	private FileMapper fileMapper;

	@Autowired
	private UrlParserInfoMapper urlParserInfoMapper;

	@Autowired
	private ApplicationUserConfigurationService applicationUserConfigurationService;

	@Autowired
	private DeepseekServer deepseekServer;

	@Autowired
	private ModelPluginApiService modelPluginApiService;

	@Autowired
	private ApplicationApiAuthService applicationAuthService;

	@Autowired
	private UserService userService;

	@Autowired
	private OauthTenantService oauthTenantService;

	@Autowired
	private ApplicationPublishRecordService applicationPublishRecordService;

	@Autowired
	private ServerPublishAuditService serverPublishAuditService;

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private GrantDataMapper grantDataMapper;

	@Autowired
	private MyFavoriteMapper myFavoriteMapper;
	@Autowired
	private ApplicationInfoMapper applicationInfoMapper;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private ComponentMapper componentMapper;
	@Autowired
	private ComponentNodeMapper componentNodeMapper;
	@Autowired
	private MetaParamMapper metaParamMapper;
	@Autowired
	private ComponentNodeRelMapper componentNodeRelMapper;
	@Autowired
	private ComponentNodeRelMapper nodeRelMapper;
	@Autowired
	private ApplicationKnowledgeMapper applicationKnowledgeMapper;
	@Autowired
	private KnowledgeInfoService knowledgeInfoService;
    @Autowired
    private ApplicationInfoVersionIndexMapper applicationInfoVersionIndexMapper;
	@Autowired
	private ApplicationMcpRefService applicationMcpRefService;
	@Autowired
	private PresetQuestionMapper presetQuestionMapper;
	@Autowired
	private InterceptWordHouseApplicationRelMapper interceptWordHouseApplicationRelMapper;
	@Autowired
	private InterceptWordHouseService interceptWordHouseService;
	@Autowired
	private ApplicationPluginDataMapper applicationPluginDataMapper;
	@Autowired
	private ApplicationQuickCommandService applicationQuickCommandService;
	@Autowired
	private DenseVectorService denseVectorService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<ApplicationInfo> addApplicationInfo(ApplicationInfo applicationInfo){
		if (StringUtils.isBlank(applicationInfo.getApplicationName())) {
			return Result.fail("应用名称不能为空");
		}
		if (StringUtils.isBlank(applicationInfo.getApplicationCode())) {
			return Result.fail("应用编码不能为空");
		}

		String defaultAppid = AppConfigContant.getConfiguration(APP_CONFIG_DEFAULT_APP_ID);
		if (StringUtils.isNotBlank(defaultAppid)) {
			if (defaultAppid.equals(applicationInfo.getApplicationId())) {
				return Result.fail("默认应用不允许编辑");
			}
		}

		// 目前token中未保存租户号
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		if (null != tokenUserInfo && null != tokenUserInfo.getId()) {
			OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
			if (null != oauthUser && StringUtils.isNotBlank(oauthUser.getTenantId())) {
				Tenant tenant = oauthTenantService.getDeatail(oauthUser.getTenantId());
				if (tenant != null && tenant.getAppNumLimit() != null) {
					QueryWrapper wrapper = Wrappers.init()
							.and(APPLICATION_INFO.APPLICATION_CODE.eq(applicationInfo.getApplicationCode()))
							.and(StringUtils.isNotBlank(applicationInfo.getApplicationId()),
									APPLICATION_INFO.APPLICATION_ID.ne(applicationInfo.getApplicationId()));
					// 查询当前租户数量
					long count = applicationInfoMapper.selectCountByQuery(wrapper);
					if (count >= tenant.getAppNumLimit()) {
						return Result.fail("租户智能体配额不足");
					}
				}
			}
		}

		QueryWrapper wrapper = Wrappers.init()
				.and(APPLICATION_INFO.APPLICATION_CODE.eq(applicationInfo.getApplicationCode()))
				.and(StringUtils.isNotBlank(applicationInfo.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.ne(applicationInfo.getApplicationId()));
		if (count(wrapper) > 0) {
			return Result.fail("应用编码已存在");
		}
		// 生成应用id
		if (StringUtils.isBlank(applicationInfo.getApplicationId())) {
			applicationInfo.setApplicationId(IdUtil.simpleUUID());
		} else {
			publishStatus(applicationInfo);
		}

		// 复制或编辑的时候，appid不为空
		if (StringUtils.isNotBlank(applicationInfo.getApplicationId())) {
			buildUser(applicationInfo, tokenUserInfo);
		}


		// 生成apiKey和apiSecret
		if (StringUtils.isBlank(applicationInfo.getApiKey())) {
			createApiKey(applicationInfo);
		}

		// 模型兜底参数设置
		modelFallbackFlagMethod(applicationInfo);

		// 如果未开启联网，则联网兜底默认关闭
		if (!StringUtils.equals(applicationInfo.getNetworkFlag(), YesNoEnum.YES.getName())) {
			applicationInfo.setFinalNetworkFlag(YesNoEnum.NO.getName());
		}

		applicationInfo.setClientLink(AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_DEFAULT_RELEASE_URL) + "/" + applicationInfo.getApplicationCode());

		// 设置聊天模板
		setDialogTemplate(applicationInfo);

		// 关联知识库和工作流
		bindingKnowledge(applicationInfo);

		// 关联预设问题
		bindingPresetQuestion(applicationInfo);

		// 关联应用快捷指令
		bindingApplicationQuickCommand(applicationInfo);

		// 关联敏感词库
		bindingInterceptWordHouse(applicationInfo);

		// 关联个性化配置
		bindingConfiguration(applicationInfo);

		// 关联 mcp 服务
		bindingMcpServer(applicationInfo);

		// 设置api接口地址
		if (StringUtils.isBlank(applicationInfo.getApi())) {
			applicationInfo.setApi(AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_DEFAULT_RELEASE_API));
		}

		// 前端未设置 answerTimeout 值时，设置默认值
		if (Objects.isNull(applicationInfo.getAnswerTimeout())) {
			applicationInfo.setAnswerTimeout(DialogueConstant.ANSWER_TIMEOUT);
		}

		// 设置默认重排模型
		if (StringUtils.isBlank(applicationInfo.getRearrangeModel())) {
			applicationInfo.setRearrangeModel(ApplicationRearrangeModelEnum.YAYI.getCode());
		}

		// 更新应用配置
		updateAppConfig(applicationInfo);

		if (StringUtils.isBlank(applicationInfo.getCreateTime())) {
			applicationInfo.setCreateTime(DateUtil.getCurrentTime());
		}
		applicationInfo.setUpdateTime(DateUtil.getCurrentTime());

		// 加下面逻辑是为了处理保存时候创建人有误的问题（因该问题不能复现，顾只做优化）
		if (StringUtils.isNotBlank(applicationInfo.getApplicationId())) {
			// 如果修改操作，创建人不允许保存入库
			Wrappers<Object> wrappers = Wrappers.init()
					.where(APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()));
			List<ApplicationInfo> existApplicationList = list(wrappers);
			if (CollectionUtils.isNotEmpty(existApplicationList)) {
				ApplicationInfo existApplicationInfo = existApplicationList.get(0);
				applicationInfo.setCreateUser(existApplicationInfo.getCreateUser());
			}
		}
		// 1. 幂等校验
		/*Boolean idempotent=false;
		if (null != tokenUserInfo) {
			StringBuilder redisKey=new StringBuilder();
			redisKey.append(tokenUserInfo.getTenantId()).append(applicationInfo.getApplicationId());
			if(CollectionUtil.isNotEmpty(applicationInfo.getPublishDesc())){
				redisKey.append(applicationInfo.getPublishDesc());
			}else{
				redisKey.append("v1.0.0");
			}
			idempotent=idempotentProcessor.checkIdempotent(String.valueOf(redisKey));
		}*/
		//设置默认版本号,防止，幂等校验,重复提交
		if ("1".equals(applicationInfo.getMakeType())) {
			recordVersion(applicationInfo, tokenUserInfo);
		} else {
			mapper.insertOrUpdate(applicationInfo, false);
		}
		return Result.success(applicationInfo);
	}

	@Override
	public Result<Page<ApplicationInfo>> getApplicationInfoList(ApplicationInfoPageParam applicationInfo) {
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		List<String> targetIds = new ArrayList<>();
		String userId = String.valueOf(tokenOauthUserInfo.getId());
		String tenantId = tokenOauthUserInfo.getTenantId();
		// 如何查询应用商店的应用且是中科闻歌的租户，则默认可以查到该租户下的所有上架的应用
		/*if (Objects.equals(applicationInfo.getPublishAppStore() ,PublishAppStoreEnum.COMPLETE_PUBLIS.getCode()) && StringUtils.isNotBlank(tenantId)) {
			Tenant tenant = oauthTenantService.getDeatail(tenantId);
			if (StringUtils.equals(tenant.getTenantName(), UserInfoConstant.WECHAT_DEFAULT_TENANT)) {
				List<OauthUser> users = userService.getUserByTenantId(tenantId);
				List<String> accoutNames = users.stream().map(OauthUser::getAccountName).collect(Collectors.toList());
				Page<ApplicationInfo> publisAppApplicationPage = getPagePublisAppApplication(applicationInfo, accoutNames);
				return Result.success(publisAppApplicationPage);
			}

		}*/
		// 用户id加入列表
		targetIds.add(userId);
		if (StringUtils.isNotBlank(tenantId)) {
			// 有的用户可能没有绑定租户
			targetIds.add(tenantId);
		}

		if(applicationInfo.isOperationXiazhuan()){
			applicationInfo.setTypeLists(com.google.common.collect.Lists.newArrayList("qa", "dialogue", "search", "", "workflow", "text-agent"));
		}
		Page<ApplicationInfo> page = getPageApplication(applicationInfo, true);
		List<ApplicationInfo> applicationInfoList = page.getRecords();
		List<GrantData> grantDataList = queryGrantData(applicationInfoList, targetIds);
		if (CollectionUtils.isNotEmpty(applicationInfoList)) {
			// 设置“复制”按钮权限
			if (StringUtils.equals(PowerTypeEnum.SYSTEM_ADMIN.getCode(), tokenOauthUserInfo.getPowerType())) {
				// 如果是超级管理员，“复制”按钮都有权限
				applicationInfoList.forEach(v->v.setCopyPermission(CopyPermissionEnum.YES.getCode()));
			} else {
				applicationInfoList.forEach(v->{
					// 当前用户权限
					Optional<GrantData> userGrantData = grantDataList.stream()
							.filter(p -> StringUtils.equals(v.getApplicationId(), p.getDataId())
									&& StringUtils.equals(userId, p.getTargetId())).findAny();
					if (userGrantData.isPresent()) {
						// 如果用户设置了“复制”权限，以用户权限为主
						v.setCopyPermission(Objects.isNull(userGrantData.get().getCopyPermission()) ? CopyPermissionEnum.YES.getCode() : userGrantData.get().getCopyPermission());
						v.setGranted(Boolean.TRUE);
					} else {
						v.setGranted(Boolean.FALSE);
						// 查找租户是否设置了“复制”权限
						Optional<GrantData> tenantGrantData = grantDataList.stream()
								.filter(p -> StringUtils.equals(v.getApplicationId(), p.getDataId())
										&& StringUtils.equals(tenantId, p.getTargetId())).findAny();
						if (tenantGrantData.isPresent()) {
							v.setCopyPermission(Objects.isNull(tenantGrantData.get().getCopyPermission()) ? CopyPermissionEnum.YES.getCode() : tenantGrantData.get().getCopyPermission());
						} else {
							// 如果用户和租户均未授权，默认是可复制
							v.setCopyPermission(CopyPermissionEnum.YES.getCode());
						}
					}
				});
			}
		}

		return Result.success(page);
	}


	private List<GrantData> queryGrantData(List<ApplicationInfo> applicationInfoList, List<String> targetIds) {
		if (CollectionUtils.isEmpty(applicationInfoList) || CollectionUtils.isEmpty(targetIds)) {
			return new ArrayList<>();
		}

		List<String> applicationIdsIdList = applicationInfoList.stream().map(ApplicationInfo::getApplicationId).collect(Collectors.toList());
		Wrappers<Object> grantDataWrappers = Wrappers.init()
				.where(CollectionUtils.isNotEmpty(applicationIdsIdList), GRANT_DATA.DATA_ID.in(applicationIdsIdList))
				.and(GRANT_DATA.DATA_TYPE.eq(MybatisFiledConstant.APP))
				.and(GRANT_DATA.TARGET_TYPE.in(MybatisFiledConstant.USER, MybatisFiledConstant.TENANT))
				.and(GRANT_DATA.TARGET_ID.in(targetIds));
		List<GrantData> grantDataList = grantDataMapper.selectListByQuery(grantDataWrappers);
		return grantDataList;
	}

	@Override
	public Result updateApplicationInfo(ApplicationInfo applicationInfo){
		updateById(applicationInfo);
		mapper.insertOrUpdate(applicationInfo, false);
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result deleteApplicationInfo(StringParam appId) {
		String defaultAppid = AppConfigContant.getConfiguration(APP_CONFIG_DEFAULT_APP_ID);
		if (StringUtils.isNotBlank(defaultAppid)) {
			if (defaultAppid.equals(appId.getParam())) {
				return Result.fail("默认应用不允许删除");
			}
		}

		Wrappers<Object> wrappers = Wrappers.init()
				.where(APPLICATION_INFO.APPLICATION_ID.eq(appId.getParam()));
		// 移除应用和知识库的关联
		Wrappers<Object> wrappers1 = Wrappers.init()
				.where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(appId.getParam()));
		applicationKnowledgeService.remove(wrappers1);
		remove(wrappers);
		// 删除被关联的工作流
		workFlowService.deleteById(appId.getParam() + "_" + ComponentTypeEnum.APPLICATION.getCode());
		// 删除被关联的对话流
		workFlowService.deleteById(appId.getParam() + "_" + ComponentTypeEnum.APPLICATION_FLOW.getCode());
		// 删除被关联的模型插件
		modelPluginApiService.deleteModelPluginApi(ListUtil.toList(appId.getParam()));
		return Result.success();
	}

	@Override
	public Result bindingKnowledge(ApplicationKnowledgeBindedParam param) {
		if (StringUtils.isBlank(param.getApplicationId())) {
			return Result.fail("知识库id不能为空");
		}
		if (CollectionUtils.isEmpty(param.getKnowledgeIdList())) {
			return Result.fail("知识库id不能为空");
		}

		// 清空关联表
		Wrappers<Object> wrappers = Wrappers.init()
				.where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(param.getApplicationId()));
		applicationKnowledgeService.remove(wrappers);
		List<ApplicationKnowledge> list = param.getKnowledgeIdList().stream().map(p -> {
			ApplicationKnowledge applicationKnowledge = new ApplicationKnowledge();
			applicationKnowledge.setApplicationId(param.getApplicationId());
			applicationKnowledge.setKnowledgeId(p);
			applicationKnowledge.setType(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType());
			return applicationKnowledge;
		}).collect(Collectors.toList());
		applicationKnowledgeService.saveBatch(list);
		return Result.success();
	}

	@Override
	public Result<ApplicationInfo> getApplicationDetail(StringParam applicationCode) {
		if (StringUtils.isBlank(applicationCode.getParam())) {
			return Result.success(new ApplicationInfo());
		}
		Wrappers<Object> wrappers = Wrappers.init()
				.where(APPLICATION_INFO.APPLICATION_CODE.eq(applicationCode.getParam()));

		List<ApplicationInfo> list = list(wrappers);
		if (CollectionUtils.isEmpty(list)) {
			return Result.success(new ApplicationInfo());
		}

		ApplicationInfo applicationInfo = list.get(0);
		applicationInfo.setCreateUser(null);
		applicationInfo.setUpdateUser(null);

		// 获取PC模板路由
		if (StringUtils.isNotBlank(applicationInfo.getTemplateId())) {
			List<DialogTemplate> lists = DialogTemplate.create()
					.where(DIALOG_TEMPLATE.TEMPLATE_ID.eq(applicationInfo.getTemplateId()))
					.lists();
			if (CollectionUtils.isNotEmpty(lists)) {
				applicationInfo.setTemplateRoute(lists.get(0).getTemplateRoute());
			}
		}

		// 获取移动端模板路由
		if (StringUtils.isNotBlank(applicationInfo.getMobileTemplateId())) {
			List<DialogTemplate> lists = DialogTemplate.create()
					.where(DIALOG_TEMPLATE.TEMPLATE_ID.eq(applicationInfo.getMobileTemplateId()))
					.lists();
			if (CollectionUtils.isNotEmpty(lists)) {
				applicationInfo.setMobileTemplateRoute(lists.get(0).getTemplateRoute());
			}
		}

		// 获取认证渠道
		List<String> AuthChannelIdList = Lists.newArrayList();
		if (StringUtils.isNotBlank(applicationInfo.getClientAuthChannel())) {
			AuthChannelIdList.add(applicationInfo.getClientAuthChannel());
		}
		if (StringUtils.isNotBlank(applicationInfo.getPcAuthChannel())) {
			AuthChannelIdList.add(applicationInfo.getPcAuthChannel());
		}
		List<AuthChannel> authChannelList = authChannelService.getByIds(AuthChannelIdList);
		if (CollectionUtils.isNotEmpty(authChannelList)) {
			HashMap<String, String> authChannelMap = authChannelList.stream().collect(Collectors.toMap(
                    AuthChannel::getAuthChannelId,
                    AuthChannel::getChannelCode,
					(k1, k2) -> k1,
					Maps::newHashMap
			));
			if (StringUtils.isNotBlank(applicationInfo.getClientAuthChannel())) {
				applicationInfo.setClientAuthChannelCode(authChannelMap.getOrDefault(applicationInfo.getClientAuthChannel(), StringConstant.BLANK));
			}
			if (StringUtils.isNotBlank(applicationInfo.getPcAuthChannel())) {
				applicationInfo.setPcAuthChannelCode(authChannelMap.getOrDefault(applicationInfo.getPcAuthChannel(), StringConstant.BLANK));
			}
		}

		// 获取应用快捷指令
		List<ApplicationQuickCommand> applicationQuickCommandList = applicationQuickCommandService.list();
		Map<String, List<ApplicationQuickCommand>> applicationQuickCommandMap =
				applicationQuickCommandList.stream()
						.collect(Collectors.groupingBy(
								ApplicationQuickCommand::getApplicationId
						));
		applicationInfo.setApplicationQuickCommandList(applicationQuickCommandMap.getOrDefault(applicationInfo.getApplicationId(), Lists.newArrayList()));

		// 获取关联知识库
		List<ApplicationKnowledge> applicationKnowledges = applicationKnowledgeService.list();
		Map<String, List<ApplicationKnowledge>> knowledgeMap = applicationKnowledges.stream()
				.filter(p -> p.getType().equals(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType()))
				.collect(Collectors.groupingBy(ApplicationKnowledge::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), listData -> listData)));
		List<ApplicationKnowledge> knowledge = knowledgeMap.getOrDefault(applicationInfo.getApplicationId(), Lists.newArrayList());
		applicationInfo.setKnowledgeIds(knowledge);
		// 获取关联工作流
		Map<String, List<ApplicationKnowledge>> workflowMap = applicationKnowledges.stream()
				.filter(p -> p.getType().equals(ApplicationKnowledgeTypeEnum.WORKFLOW.getType()))
				.collect(Collectors.groupingBy(ApplicationKnowledge::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), listData -> listData)));
		List<ApplicationKnowledge> workflowIds = workflowMap.getOrDefault(applicationInfo.getApplicationId(), Lists.newArrayList());
		applicationInfo.setWorkflowIds(workflowIds);

		// 收集文件、图片等数量
		setCollectFileTypeCount(applicationInfo);

		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		Long currentUserId = tokenOauthUserInfo.getId();
		List<ApplicationUserConfiguration> applicationUserConfigurations = applicationUserConfigurationService.selectByUserIdAndAppId(currentUserId, applicationInfo.getApplicationId());
		ApplicationUserConfiguration userConfig = new ApplicationUserConfiguration();
		if (CollectionUtils.isNotEmpty(applicationUserConfigurations)) {
			userConfig = applicationUserConfigurations.get(0);
		}
		// 流式语音播报优先使用配置项
		if (!Objects.isNull(userConfig.getStreamVoice())) {
			applicationInfo.setStreamVoice(userConfig.getStreamVoice() == 1 ? "是" : "否");
		}
		if (StringUtils.isBlank(applicationInfo.getStreamVoice())) {
			applicationInfo.setStreamVoice("否");
		}

		// 根据配置获取问答输入框内容长度限制标识
		String contentLengthLimit = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.CONTENT_LENGTH_LIMIT);
		if (StringUtils.isBlank(contentLengthLimit)) {
			// 不配置时候，默认跟原有通用逻辑一样
			applicationInfo.setContentLengthLimit(YesNoEnum.YES.getName());
		} else {
			applicationInfo.setContentLengthLimit(contentLengthLimit);
		}
		// 模型兜底参数设置
		modelFallbackFlagMethod(applicationInfo);

		// 获取应用配置
		List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(applicationInfo.getApplicationId());
		setConfig(configurationList, applicationInfo);

		// 获取模型信息
		setModelInfo(applicationInfo);

		return Result.success(applicationInfo);
	}

	/**
	 * @author: caohaifeng
	 * @date: 2025/3/4 13:59
	 * @Description: 模型兜底参数值设置
	 * @Version:1.0
	 **/
	private void modelFallbackFlagMethod(ApplicationInfo applicationInfo){
		if (StringUtils.isBlank(applicationInfo.getProcessStep())) {
			return;
		}
		if (StringUtils.isNotBlank(applicationInfo.getModelFallbackFlag()) && applicationInfo.getModelFallbackFlag().equals("否")) {
			if (StringUtils.isNotBlank(applicationInfo.getProcessStep())) {
				applicationInfo.setProcessStep(applicationInfo.getProcessStep().replaceAll("," + AnswerStrategyContant.FIND_ANSWER_BY_MODEL, ""));
			}
		}else {
			if (!applicationInfo.getProcessStep().contains(AnswerStrategyContant.FIND_ANSWER_BY_MODEL)) {
				applicationInfo.setProcessStep(applicationInfo.getProcessStep() + "," + AnswerStrategyContant.FIND_ANSWER_BY_MODEL);
			}
			applicationInfo.setModelFallbackFlag("是");
		}
	}

	@Override
	public ApplicationInfo getActiveApp(String appId, String apiKey, String debuggerFlag) {
		Wrappers wrappers = Wrappers.init()
				.where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(appId))
				.and(StringUtils.isNotBlank(apiKey), APPLICATION_INFO.API_KEY.eq(apiKey));

		if (!YesNoEnum.YES.getName().equals(debuggerFlag)) {
			wrappers.and(APPLICATION_INFO.PUBLISH_STATUS.eq(AppStatusEnum.PUBLISH.getCode()));
		}
		List<ApplicationInfo> list = list(wrappers);
		ApplicationInfo applicationInfo = null;
		if (CollectionUtils.isNotEmpty(list)) {
			applicationInfo = list.get(0);
		}
		if (null == applicationInfo) {
			return applicationInfo;
		}
		String maxToken = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), MAX_TOKENS);
		if (!Objects.isNull(maxToken)) {
			applicationInfo.setMaxToken(Integer.valueOf(maxToken));
		}
		// 获取词库数据
		if (!DialogueServiceImpl.APP_INTERCEPT_WORD_HOUSE_ID_MAP.containsKey(applicationInfo.getApplicationId())) {
			List<Long> houseIdByAppId = relService.getHouseIdByAppId(applicationInfo.getApplicationId());
			DialogueServiceImpl.APP_INTERCEPT_WORD_HOUSE_ID_MAP.put(applicationInfo.getApplicationId(), houseIdByAppId);
		}
		applicationInfo.setUpdateUser(null);
		applicationInfo.setCreateUser(null);
		return applicationInfo;
	}

	@Override
	public Result copyApp(CopyAppParam param) {
		if (StringUtils.isBlank(param.getApplicationId())) {
			return Result.fail("应用不能为空");
		}
		ApplicationInfoPageParam applicationInfo = new ApplicationInfoPageParam();
		applicationInfo.setApplicationId(param.getApplicationId());
		applicationInfo.setPageNo(1);
		applicationInfo.setPageSize(1);
        Page<ApplicationInfo> applicationInfoList = getPageApplication(applicationInfo, false);
        List<ApplicationInfo> records = applicationInfoList.getRecords();

		if (CollectionUtils.isEmpty(records)) {
			return Result.fail("没有默认应用");
		}

		// 获取当前用户信息
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		// 获取已有应用信息
		ApplicationInfo appCopy = records.get(0);
		// 清理属性
		clear(appCopy, tokenUserInfo, param.getCopyFlag());
		// 生成新的应用
		Result<ApplicationInfo> applicationInfoResult = addApplicationInfo(appCopy);
		// 获取新的应用的id
		ApplicationInfo data = applicationInfoResult.getData();
		String newAppId = data.getApplicationId();

		// 复制插件
		copyPlugin(newAppId, param.getApplicationId());

		// 复制应用配置项目
		copyApplicationConfiguration(newAppId, param.getApplicationId(), tokenUserInfo);

		// 复制工作流
		copyWorkflow(newAppId, param.getApplicationId(), ApplicationTypeEnum.WORKFLOW.getName(), appCopy.getApplicationName(), tokenUserInfo);

		// 复制对话流
		copyWorkflow(newAppId, param.getApplicationId(), ApplicationTypeEnum.DIALOGUE.getName(), appCopy.getApplicationName(), tokenUserInfo);

		// 复制模型插件
		copyModelPlugin(newAppId, param.getApplicationId(), appCopy.getApplicationName());

		// if (!StrUtil.equals(data.getType(), AnswerStrategyContant.APP_TYPE_QA)) {
		// 	Component component = new Component();
		// 	component.setComponentId(applicationId.getParam());
		// 	workFlowService.copy2Target(component, newAppId, "app");
		// }

		return applicationInfoResult;
	}

	@Override
	public Result<ApplicationInfo> getDefaultApp(EmptyParam param) {
		Wrappers<Object> wrappers = Wrappers.init()
				.where(APPLICATION_INFO.APPLICATION_ID.eq(AppConfigContant.getConfiguration(APP_CONFIG_DEFAULT_APP_ID)))
				.limit(1);
		List<ApplicationInfo> list = list(wrappers);
		if (CollectionUtils.isEmpty(list)) {
			return Result.fail("默认应用不存在");
		}
//		// 获取预设问题
//		List<PresetQuestion> presetQuestionList = presetQuestionService.list();
//		Map<String, List<String>> preQuestionMap = presetQuestionList.stream().collect(
//				Collectors.groupingBy(PresetQuestion::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), questionList ->
//						questionList.stream().map(PresetQuestion::getQuestion).distinct().collect(Collectors.toList()))));

		ApplicationInfo applicationInfo = list.get(0);
//		applicationInfo.setPresetQuestionList(preQuestionMap.getOrDefault(applicationInfo.getApplicationId(), Lists.newArrayList()));
		applicationInfo.setModelFallbackFlag("是");
		applicationInfo.setId(null);
		applicationInfo.setApplicationId(null);
		applicationInfo.setApplicationCode(null);
		applicationInfo.setApplicationName(null);
		applicationInfo.setClientLink(null);
		applicationInfo.setApi(null);
		applicationInfo.setApiKey(null);
		applicationInfo.setSystemPrompt(null);
		return Result.success(applicationInfo);
	}

	@Override
	public ApplicationInfo getByAppId(String appId) {
		if (StringUtils.isBlank(appId)) {
			return null;
		}

		if(appId.contains("_")){
			int indexOf = appId.indexOf("_");
			appId = appId.substring(0, indexOf);
		}

        return Wrappers.of(mapper)
                .where(APPLICATION_INFO.APPLICATION_ID.eq(appId))
                .limit(1)
                .one();
	}

	@Override
	public List<ApplicationInfo> getActiveApp(List<String> appIds) {
		if (CollectionUtils.isEmpty(appIds)) {
			return Lists.newArrayList();
		}

		appIds = appIds.stream().map(p -> {
			if (p.contains("_")) {
				return p.substring(0, p.indexOf("_"));
			}
			return p;
		}).collect(Collectors.toList());

		Wrappers wrappers = Wrappers.init()
				.where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.in(appIds));
				// .and(APPLICATION_INFO.PUBLISH_STATUS.eq(AppStatusEnum.PUBLISH.getCode()));
		List<ApplicationInfo> list = list(wrappers);

		Map<String, List<Long>> houseIdMap = relService.getHouseIdByAppId(appIds);

		// 获取预设问题
		List<PresetQuestion> presetQuestionList = presetQuestionService.list();
		Map<String, List<String>> preQuestionMap = presetQuestionList.stream().collect(
				Collectors.groupingBy(PresetQuestion::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), questionList ->
						questionList.stream().map(PresetQuestion::getQuestion).distinct().collect(Collectors.toList()))));

		// 获取应用快捷指令
		List<ApplicationQuickCommand> applicationQuickCommandList = applicationQuickCommandService.list();
		Map<String, List<ApplicationQuickCommand>> applicationQuickCommandMap =
				applicationQuickCommandList.stream()
						.collect(Collectors.groupingBy(
								ApplicationQuickCommand::getApplicationId
						));


		for (ApplicationInfo applicationInfo : list) {
			List<Long> houseIdByAppId = houseIdMap.get(applicationInfo.getApplicationId());
			DialogueServiceImpl.APP_INTERCEPT_WORD_HOUSE_ID_MAP.put(applicationInfo.getApplicationId(), houseIdByAppId);
			// 填充预设问题
			applicationInfo.setPresetQuestionList(preQuestionMap.getOrDefault(applicationInfo.getApplicationId(), Lists.newArrayList()));
			// 设置应用快捷指令
			applicationInfo.setApplicationQuickCommandList(applicationQuickCommandMap.getOrDefault(applicationInfo.getApplicationId(), Lists.newArrayList()));
		}
		DialogueServiceImpl.APP_INTERCEPT_WORD_HOUSE_ID_MAP.putAll(houseIdMap);
		return list;
	}

	@Override
	public List<ApplicationInfo> getByAppId(List<String> appIds) {
		if (CollectionUtils.isEmpty(appIds)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.where(APPLICATION_INFO.APPLICATION_ID.in(appIds))
				.list();
	}

	@Override
	public SseEmitter generatePrompt(PromptGenerateParam param) {
		if (StringUtils.isBlank(param.getClientId())) {
			return null;
		}
		String clientId = param.getClientId();
		SseEmitter emitter = SseEmitterUtils.getConnection(clientId);
		new Thread(() -> {

			try {
				if (StringUtils.isBlank(param.getTopic())) {
					param.setTopic("智能客服");
				}
				if (StringUtils.isBlank(param.getDescription())) {
					param.setDescription("智能客服");
				}
				String generatePrompt = AppConfigContant.getConfiguration(GENERATE_PROMPT);
				if (StringUtils.isBlank(generatePrompt)) {
					generatePrompt = "${topic}\n${description}";
				}
				generatePrompt = generatePrompt
						.replace("${topic}", param.getTopic())
						.replace("${description}", param.getDescription());

				StringBuilder prompt = new StringBuilder();
				JSONObject promptReuslt = new JSONObject();
				deepseekServer.generateStreamStr(generatePrompt, result -> {
					try {
						prompt.append(result);
						promptReuslt.put("prompt", prompt.toString());
						SseEmitterUtils.send(clientId, JSONUtil.toJsonStr(promptReuslt));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SseEmitterUtils.complete(clientId);
			}

		}, MDC.get("traceId")).start();
		return emitter;
	}

	@Override
	public Result<String> aiImage(PromptGenerateParam param) {
		if (null == aiImageStrategyMap || aiImageStrategyMap.isEmpty()) {
			log.error("未配置图片生成策略");
			return Result.fail("未配置图片生成策略");
		}
		// 默认雅意策略
		if (StringUtils.isBlank(param.getStrategy())) {
			param.setStrategy("yayiImageStrategy");
		}

		AiImageStrategy aiImageStrategy;
		String applicationId = param.getApplicationId();
		if (!StringUtils.isBlank(applicationId)) {
			ApplicationInfo applicationInfo = getByAppId(applicationId);
			String modelId = applicationInfo.getModelId();
			aiImageStrategy = llmInfoService.getAiImageStrategy(modelId);
		} else {
			aiImageStrategy = aiImageStrategyMap.get(param.getStrategy());
		}

		if (null == aiImageStrategy) {
			log.error("未配置图片生成策略: {}", param.getStrategy());
			return Result.fail("未配置图片生成策略: " + param.getStrategy());
		}


		List<String> aiImageUrls = aiImageStrategy.getAiImage(param);
		String aiImageUrl = StringConstant.BLANK;
		if (CollectionUtil.isNotEmpty(aiImageUrls)) {
			aiImageUrl = aiImageUrls.get(0);
		}
		return Result.success(aiImageUrl);
	}

	@Override
	public Result<String> aiVideo(AiVideoParam param) {
		if (null == aiVideoStrategyMap || aiVideoStrategyMap.isEmpty()) {
			log.error("未配置视频生成策略");
			return Result.fail("未配置视频生成策略");
		}

		String modelId = param.getModelId();
		if (StringUtils.isBlank(modelId) && StringUtils.isNotBlank(param.getApplicationId())) {
			ApplicationInfo applicationInfo = getByAppId(param.getApplicationId());
			modelId = applicationInfo.getModelId();
		}
		if (StringUtils.isBlank(modelId)) {
			// 默认使用豆包模型
			LlmInfo byModelCode = llmInfoService.getByDefaultVideoModelCode(param.getStrategy());
			modelId = byModelCode.getModelId();
			param.setStrategy(byModelCode.getModelCode());
		}
		AiVideoStrategy aiVideoStrategy = llmInfoService.getAiVedioStrategy(modelId);
		if (null == aiVideoStrategy) {
			log.error("未配置视频生成策略: {}", param.getStrategy());
			return Result.fail("未配置视频生成策略: " + param.getStrategy());
		}
		String aiVideo = aiVideoStrategy.getAiVideo(param);

		return Result.success(aiVideo);
	}

	@Override
	public void deleteByApplicationId(String appId) {
		remove(APPLICATION_INFO.APPLICATION_ID.eq(appId));
	}

	@Override
	public Result export(ApplicationInfo applicationInfo, HttpServletResponse response) throws IOException {
		if(applicationInfo==null){
			return Result.error("应用不存在");
		}
		if (StringUtils.isBlank(applicationInfo.getApplicationName())) {
			return Result.fail("应用名称不能为空");
		}
		if (StringUtils.isBlank(applicationInfo.getApplicationCode())) {
			return Result.fail("应用编码不能为空");
		}
		if (StringUtils.isBlank(applicationInfo.getApplicationId())) {
			return Result.fail("应用applicationId不能为空");
		}
		String applicationId = applicationInfo.getApplicationId();

		QueryWrapper wrapper = Wrappers.init()
				.and(StringUtils.isNotBlank(applicationInfo.getApplicationCode()),APPLICATION_INFO.APPLICATION_CODE.eq(applicationInfo.getApplicationCode()))
				.and(StringUtils.isNotBlank(applicationInfo.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()));
		List<ApplicationInfo> listApp= mapper.selectListByQuery(wrapper);
		if(CollectionUtils.isNotEmpty(listApp)&& !listApp.isEmpty()){
			applicationInfo= listApp.get(0);
		}
		applicationInfo.setApplicationId(IdUtil.simpleUUID());
		applicationInfo.setApplicationCode(IdUtil.simpleUUID());
		QueryWrapper repeatWrapper = Wrappers.init()
				.or(StringUtils.isNotBlank(applicationInfo.getApplicationCode()),APPLICATION_INFO.APPLICATION_CODE.eq(applicationInfo.getApplicationCode()))
				.or(StringUtils.isNotBlank(applicationInfo.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()));
		List<ApplicationInfo> repeatApp= mapper.selectListByQuery(repeatWrapper);
		// 生成应用id
		if (!repeatApp.isEmpty()) {
			return Result.fail("应用编码已存在,重新导出");
		}
		// 设置api接口地址
		if (StringUtils.isBlank(applicationInfo.getApi())) {
			applicationInfo.setApi(AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_DEFAULT_RELEASE_API));
		}
		//设置默认版本号
		Long currentTimeMillis=System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 使用Date构造函数，传入时间戳
		Date date = new Date(currentTimeMillis);
		String formattedDate = sdf.format(date);
		applicationInfo.setAppVersionNumber("v1.0_"+formattedDate);
		ApplicationInfoVersionDTO applicationInfoVersionDTO =new ApplicationInfoVersionDTO();
		BeanUtil.copyProperties(applicationInfo, applicationInfoVersionDTO);
		applicationInfoVersionDTO.setApplicationInfoId(String.valueOf(applicationInfo.getId()));
		StringBuilder copy = new StringBuilder(applicationInfo.getApplicationName()).append("-复制导入").append(formattedDate);
		applicationInfoVersionDTO.setApplicationName(String.valueOf(copy));
		applicationInfoVersionDTO.setAppVersionNumber("v1.0_"+formattedDate);
		applicationInfoVersionDTO.setMakeType(applicationInfo.getMakeType());
		//知识库
		QueryWrapper wrapper1 = Wrappers.init()
				.and(StringUtils.isNotBlank(applicationId), APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationId));
		List<ApplicationKnowledge> applicationKnowledgeList = applicationKnowledgeMapper.selectListByQuery(wrapper1);
		if(CollectionUtils.isNotEmpty(applicationKnowledgeList)){
			applicationInfo.setKnowledgeIds(applicationKnowledgeList);
		}
		if (CollectionUtil.isNotEmpty(applicationInfo.getKnowledgeIds())) {
			List<ApplicationKnowledge> knowledgeIds = applicationInfo.getKnowledgeIds();
            for (ApplicationKnowledge next : knowledgeIds) {
                next.setApplicationId(applicationInfo.getApplicationId());
                next.setId(null);
            }
			applicationInfoVersionDTO.setBindingKnowledgeIdsJson(JSONUtil.toJsonStr(knowledgeIds));
		}
		/*
		关联预设问题关联关系复制
		 */
		QueryWrapper presetQuestionWrapper = Wrappers.init()
				.and(PRESET_QUESTION.APPLICATION_ID.eq(applicationId))
				.and(PRESET_QUESTION.STATUS.eq(1));
		List<PresetQuestion> presetQuestions = presetQuestionMapper.selectListByQuery(presetQuestionWrapper);
		if(CollectionUtils.isNotEmpty(presetQuestions)){
			List<String> List=new ArrayList<>();
			for (PresetQuestion rel : presetQuestions) {
				List.add(rel.getQuestion());
			}
			applicationInfoVersionDTO.setBindingPresetQuestionJson(JSONUtil.toJsonStr(List));
		}
		/*
		关联应用快捷指令关联关系复制
		 */
		QueryWrapper applicationQuickCommandWrapper = Wrappers.init()
				.and(APPLICATION_QUICK_COMMAND.APPLICATION_ID.eq(applicationId))
				.and(APPLICATION_QUICK_COMMAND.STATUS.eq(1));
		List<ApplicationQuickCommand> applicationQuickCommandList = applicationQuickCommandService.list(applicationQuickCommandWrapper);
		if(CollectionUtils.isNotEmpty(applicationQuickCommandList)){
			List<ApplicationQuickCommand> applicationQuickCommandArrayList = new ArrayList<>();
			for (ApplicationQuickCommand rel : applicationQuickCommandList) {
				ApplicationQuickCommand copyApplicationQuickCommand = new ApplicationQuickCommand();
				copyApplicationQuickCommand.setCommandName(rel.getCommandName());
				copyApplicationQuickCommand.setCommandContent(rel.getCommandContent());
				copyApplicationQuickCommand.setCommandType(rel.getCommandType());
				applicationQuickCommandArrayList.add(copyApplicationQuickCommand);
			}
			applicationInfoVersionDTO.setBindingApplicationQuickCommandJson(JSONUtil.toJsonStr(applicationQuickCommandArrayList));
		}
		/*
		关联敏敢词关联关系复制
		 */
		QueryWrapper interceptWordWrapper = Wrappers.init()
				.and(StringUtils.isNotBlank(applicationInfo.getApplicationId()), INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(applicationId))
				.and(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq(1));
		// 1. 查询应用关联的敏感词id
		List<InterceptWordHouseApplicationRel> interceptWordHouseList = interceptWordHouseApplicationRelMapper.selectListByQuery(interceptWordWrapper);
		List<Long> interceptWordIdList = interceptWordHouseList.stream().map(InterceptWordHouseApplicationRel::getInterceptWordHouseId).collect(Collectors.toList());
		// 2. 根据敏感词id查询敏感词
		if (CollectionUtil.isNotEmpty(interceptWordIdList)) {
			List<InterceptWordHouse> interceptWordList = interceptWordHouseService.listByIds(interceptWordIdList);
			List<String> interceptNameList = interceptWordList.stream().map(InterceptWordHouse::getName).collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(interceptNameList)) {
				applicationInfoVersionDTO.setBindingInterceptWordhoseJson(JSONUtil.toJsonStr(interceptNameList));
			}
		}
		/*
		关联插件关联关系复制
		 */
		// 1. 查询应用关联的插件
		QueryWrapper pluginDataWrapper = Wrappers.init()
				.and(APPLICATION_PLUGIN_DATA.APPLICATION_ID.eq(applicationId))
				.and(APPLICATION_PLUGIN_DATA.STATUS.eq("是"));
		List<ApplicationPluginData> pluginDataList = applicationPluginDataMapper.selectListByQuery(pluginDataWrapper);
		// 2. 根据关联的插件查询插件数据,获取插件名称
		List<String> pluginIdList = pluginDataList.stream().map(ApplicationPluginData::getPluginId).collect(Collectors.toList());
		if (CollectionUtil.isNotEmpty(pluginIdList)) {
			QueryWrapper pluginWrapper = QueryWrapper.create()
					.select(APPLICATION_PLUGIN.PLUGIN_NAME)
					.where(APPLICATION_PLUGIN.PLUGIN_ID.in(pluginIdList));
			List<ApplicationPlugin> pluginList = applicationPluginService.list(pluginWrapper);
			List<String> pluginNameList = pluginList.stream().map(ApplicationPlugin::getPluginName).collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(pluginNameList)) {
				applicationInfoVersionDTO.setPluginDataJson(JSONUtil.toJsonStr(pluginNameList));
			}
		}
		/*
		关联个性化关联关系复制
		 */
		if(StringUtils.isNotBlank(applicationInfo.getStreamVoice())){
			applicationInfoVersionDTO.setBindingConfigrationJson(JSONUtil.toJsonStr(applicationInfo.getStreamVoice()));
		}
		//工作流和对话流
		Component component = new Component();
		Map<String, String> map = new HashMap<>();
		if(applicationInfo.getType().equals("workflow")){
			component.setComponentId(applicationId);
			component.setType(4);
			String componentId = ComponentServiceImpl.getComponentId(component);
			List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(componentId));
			component.setComponentId(applicationInfo.getApplicationId());
			String new_componentId = ComponentServiceImpl.getComponentId(component);
			if (CollectionUtil.isNotEmpty(componentDtoList)) {
				ComponentDto componentDto = componentDtoList.get(0);
				//节点映射表复制
				QueryWrapper where = QueryWrapper.create().where(COMPONENT.COMPONENT_ID.in(componentId));
				List<Component> components = componentMapper.selectListByQuery(where);
				if(CollectionUtil.isNotEmpty(components)){
					Iterator<Component> iterator = components.iterator();
					while (iterator.hasNext()) {
						Component componentJson = iterator.next();
						componentJson.setId(null);
						componentJson.setComponentId(new_componentId);
					}
					applicationInfoVersionDTO.setComponentJson(JSONUtil.toJsonStr(components));
				}
				//节点信息复制
				if(CollectionUtil.isNotEmpty(componentDto.getNodes())){
					List<ComponentNodeDto> nodes=componentDto.getNodes();
					Iterator<ComponentNodeDto> iterator = nodes.iterator();
					while (iterator.hasNext()) {
						ComponentNodeDto componentNodeDto = iterator.next();
						componentNodeDto.setId(null);
						componentNodeDto.setComponentId(new_componentId);
						String newNodeId = IdUtil.simpleUUID();
						map.put(componentNodeDto.getNodeId(), newNodeId);
						componentNodeDto.setNodeId(newNodeId);
					}
					applicationInfoVersionDTO.setNodesJson(JSONUtil.toJsonStr(nodes));
				}
				//参数复制
				List<MetaParam> metaParams = componentDto.getMetaParams();
				if (CollectionUtil.isNotEmpty(metaParams)) {
					metaParams.forEach(param -> {
						param.setId(null);
						param.setParamId(IdUtil.simpleUUID());
						param.setNodeId(map.get(param.getNodeId()));
						param.setReferenceNodeId(param.getReferenceNodeId());
					});
					applicationInfoVersionDTO.setMetaParamsJson(JSONUtil.toJsonStr(metaParams));
				}
				//节点关系复制
				if(CollectionUtil.isNotEmpty(componentDto.getNodeRel())){
					List<ComponentNodeRel> nodeRel=componentDto.getNodeRel();
					nodeRel.forEach(rel -> {
						rel.setId(null);
						rel.setComponentId(new_componentId);
						rel.setSourceNodeId(rel.getSourceNodeId());
						rel.setTargetNodeId(rel.getTargetNodeId());
					});
					applicationInfoVersionDTO.setNodeRelJson(JSONUtil.toJsonStr(nodeRel));
				}
			}
		}else if(applicationInfo.getType().equals("dialogue")){
			component.setComponentId(applicationId);
			component.setType(5);
			String componentId = ComponentServiceImpl.getComponentId(component);
			List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(componentId));
			component.setComponentId(applicationInfo.getApplicationId());
			String new_componentId = ComponentServiceImpl.getComponentId(component);
			if (CollectionUtil.isNotEmpty(componentDtoList)) {
				ComponentDto componentDto = componentDtoList.get(0);
				//节点映射表复制
				QueryWrapper where = QueryWrapper.create().where(COMPONENT.COMPONENT_ID.in(componentId));
				List<Component> components = componentMapper.selectListByQuery(where);
				if(CollectionUtil.isNotEmpty(components)){
					Iterator<Component> iterator = components.iterator();
					while (iterator.hasNext()) {
						Component componentJson = iterator.next();
						componentJson.setId(null);
						componentJson.setComponentId(new_componentId);
					}
					applicationInfoVersionDTO.setComponentJson(JSONUtil.toJsonStr(components));
				}
				//节点信息复制
				if(CollectionUtil.isNotEmpty(componentDto.getNodes())){
					List<ComponentNodeDto> nodes=componentDto.getNodes();
					Iterator<ComponentNodeDto> iterator = nodes.iterator();
					while (iterator.hasNext()) {
						ComponentNodeDto componentNodeDto = iterator.next();
						componentNodeDto.setId(null);
						componentNodeDto.setComponentId(new_componentId);
						String newNodeId = IdUtil.simpleUUID();
						map.put(componentNodeDto.getNodeId(), newNodeId);
						componentNodeDto.setNodeId(newNodeId);
					}
					applicationInfoVersionDTO.setNodesJson(JSONUtil.toJsonStr(nodes));
				}
				//参数复制
				List<MetaParam> metaParams = componentDto.getMetaParams();
				if (CollectionUtil.isNotEmpty(metaParams)) {
					metaParams.forEach(param -> {
						param.setId(null);
						param.setParamId(IdUtil.simpleUUID());
						param.setNodeId(map.get(param.getNodeId()));
						param.setReferenceNodeId(param.getReferenceNodeId());
					});
					applicationInfoVersionDTO.setMetaParamsJson(JSONUtil.toJsonStr(metaParams));
				}
				//节点关系复制
				if(CollectionUtil.isNotEmpty(componentDto.getNodeRel())){
					List<ComponentNodeRel> nodeRel=componentDto.getNodeRel();
					nodeRel.forEach(rel -> {
						rel.setId(null);
						rel.setComponentId(new_componentId);
						rel.setSourceNodeId(rel.getSourceNodeId());
						rel.setTargetNodeId(rel.getTargetNodeId());
					});
					applicationInfoVersionDTO.setNodeRelJson(JSONUtil.toJsonStr(nodeRel));
				}
			}
		}else {
			component.setComponentId(applicationId);
			String componentId = ComponentServiceImpl.getComponentId(component);
			List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(componentId));
			component.setComponentId(applicationInfo.getApplicationId());
			String new_componentId = ComponentServiceImpl.getComponentId(component);
			if (CollectionUtil.isNotEmpty(componentDtoList)) {
				ComponentDto componentDto = componentDtoList.get(0);
				//节点映射表复制
				QueryWrapper where = QueryWrapper.create().where(COMPONENT.COMPONENT_ID.in(componentId));
				List<Component> components = componentMapper.selectListByQuery(where);
				if(CollectionUtil.isNotEmpty(components)){
					Iterator<Component> iterator = components.iterator();
					while (iterator.hasNext()) {
						Component componentJson = iterator.next();
						componentJson.setId(null);
						componentJson.setComponentId(new_componentId);
					}
					applicationInfoVersionDTO.setComponentJson(JSONUtil.toJsonStr(components));
				}
				//节点信息复制
				if(CollectionUtil.isNotEmpty(componentDto.getNodes())){
					List<ComponentNodeDto> nodes=componentDto.getNodes();
					Iterator<ComponentNodeDto> iterator = nodes.iterator();
					while (iterator.hasNext()) {
						ComponentNodeDto componentNodeDto = iterator.next();
						componentNodeDto.setId(null);
						componentNodeDto.setComponentId(new_componentId);
						String newNodeId = IdUtil.simpleUUID();
						map.put(componentNodeDto.getNodeId(), newNodeId);
						componentNodeDto.setNodeId(newNodeId);
					}
					applicationInfoVersionDTO.setNodesJson(JSONUtil.toJsonStr(nodes));
				}
				//参数复制
				List<MetaParam> metaParams = componentDto.getMetaParams();
				if (CollectionUtil.isNotEmpty(metaParams)) {
					metaParams.forEach(param -> {
						param.setId(null);
						param.setParamId(IdUtil.simpleUUID());
						param.setNodeId(map.get(param.getNodeId()));
						param.setReferenceNodeId(param.getReferenceNodeId());
					});
					applicationInfoVersionDTO.setMetaParamsJson(JSONUtil.toJsonStr(metaParams));
				}
				//节点关系复制
				if(CollectionUtil.isNotEmpty(componentDto.getNodeRel())){
					List<ComponentNodeRel> nodeRel=componentDto.getNodeRel();
					nodeRel.forEach(rel -> {
						rel.setId(null);
						rel.setComponentId(new_componentId);
						rel.setSourceNodeId(rel.getSourceNodeId());
						rel.setTargetNodeId(rel.getTargetNodeId());
					});
					applicationInfoVersionDTO.setNodeRelJson(JSONUtil.toJsonStr(nodeRel));
				}
			}
		}
		List<ApplicationInfoVersionDTO>list=new ArrayList<>();
		list.add(applicationInfoVersionDTO);
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + URLEncoder.encode(applicationInfo.getApplicationName()+".xlsx", String.valueOf(StandardCharsets.UTF_8)));
		resetCellMaxTextLength();
		EasyExcel.write(response.getOutputStream(), ApplicationInfoVersionDTO.class)
				.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
				.sheet("应用数据")
				.doWrite(list);
		return Result.success();
	}



	@Transactional
	@Override
	public Result importApp(MultipartFile file) throws IOException {
		TokenUser userInfo = ContextHolders.getTokenUserInfo();

		try {
			//获取文件的输入流
			InputStream inputStream = file.getInputStream();
			List<ApplicationInfoVersionDTO> lst = EasyExcel.read(inputStream) //调用read方法
					//注册自定义监听器，字段校验可以在监听器内实现
					//.registerReadListener(new UserListener())
					.head(ApplicationInfoVersionDTO.class) //对应导入的实体类
					.sheet(0) //导入数据的sheet页编号，0代表第一个sheet页，如果不填，则会导入所有sheet页的数据
					.headRowNumber(1) //列表头行数，1代表列表头有1行，第二行开始为数据行
					.doReadSync(); //开始读Excel，返回一个List<T>集合，继续后续入库操作

			//模拟导入数据库操作
			for (ApplicationInfoVersionDTO applicationInfoVersionDTO :lst){
				if(applicationInfoVersionDTO==null){
					return Result.error("应用不存在");
				}
				if (StringUtils.isBlank(applicationInfoVersionDTO.getApplicationName())) {
					return Result.fail("应用名称不能为空");
				}
				if (StringUtils.isBlank(applicationInfoVersionDTO.getApplicationCode())) {
					return Result.fail("应用编码不能为空");
				}
				if (StringUtils.isBlank(applicationInfoVersionDTO.getApplicationId())) {
					return Result.fail("应用applicationId不能为空");
				}
				QueryWrapper repeatWrapper = Wrappers.init()
						.or(StringUtils.isNotBlank(applicationInfoVersionDTO.getApplicationCode()),APPLICATION_INFO.APPLICATION_CODE.eq(applicationInfoVersionDTO.getApplicationCode()))
						.or(StringUtils.isNotBlank(applicationInfoVersionDTO.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationInfoVersionDTO.getApplicationId()));
				List<ApplicationInfo> repeatApp= mapper.selectListByQuery(repeatWrapper);
				// 生成应用id
				if (!repeatApp.isEmpty()) {
					return Result.fail("应用编码已存在,重新导出");
				}
				ApplicationInfo applicationInfo = new ApplicationInfo();
				BeanUtil.copyProperties(applicationInfoVersionDTO, applicationInfo);
				/*
				知识库
				 */

				if(StringUtils.isNotBlank(applicationInfoVersionDTO.getBindingKnowledgeIdsJson())){
					// 1. 转换知识库字符串为对象
					List<KnowledgeInfo> knowledgeInfoList = JsonUtil.jsonToList(applicationInfoVersionDTO.getBindingKnowledgeIdsJson(), KnowledgeInfo.class);
					// 2. 更新知识库字段的值
					if(CollectionUtils.isNotEmpty(knowledgeInfoList)){
						knowledgeInfoList.forEach(knowledgeInfo -> {

						});
					}
				}
				//工作流和对话流
				if(StringUtils.isNotBlank(applicationInfoVersionDTO.getBindingWorkflowIdsJson())) {
					List<ApplicationKnowledge> workflowIds =JsonUtil.jsonToList(applicationInfoVersionDTO.getBindingWorkflowIdsJson(),ApplicationKnowledge.class);
					applicationInfo.setWorkflowIds(workflowIds);
				}
				/*
				回退关联预设问题关联关系备份
				 */
				if(StringUtils.isNotBlank(applicationInfoVersionDTO.getBindingPresetQuestionJson())) {
					List<String> presetQuestionList =JsonUtil.jsonToList(applicationInfoVersionDTO.getBindingPresetQuestionJson(),String.class);
					// 1. 封装应用关联的对话体验的预设问题
					if (CollectionUtil.isNotEmpty(presetQuestionList)) {
						List<PresetQuestion> questionList = presetQuestionList.stream().map(s -> {
							PresetQuestion question = new PresetQuestion();
							question.setApplicationId(applicationInfo.getApplicationId());
							question.setPresetId(IdUtil.simpleUUID());
							question.setQuestion(s);
							question.setType("推荐问题");
							question.setCreateTime(DateUtil.getCurrentTime());
							question.setUpdateTime(DateUtil.getCurrentTime());
							return question;
						}).collect(Collectors.toList());
						// 2. 执行新增
						presetQuestionService.saveBatch(questionList);
					}
				}
				// 回退关联应用快捷指令关联关系备份
				if(StringUtils.isNotBlank(applicationInfoVersionDTO.getBindingApplicationQuickCommandJson())) {
					List<ApplicationQuickCommand> applicationQuickCommandList =JsonUtil.jsonToList(applicationInfoVersionDTO.getBindingApplicationQuickCommandJson(), ApplicationQuickCommand.class);
					applicationInfo.setApplicationQuickCommandList(applicationQuickCommandList);
				}
				/*
				回退关联敏敢词关联关系备份
				 */
				if(StringUtils.isNotBlank(applicationInfoVersionDTO.getBindingInterceptWordhoseJson())) {
					List<String> interceptWordHousesList =JsonUtil.jsonToList(applicationInfoVersionDTO.getBindingInterceptWordhoseJson(),String.class);
					List<InterceptWordHouse> interceptWordHouseList = new ArrayList<>();
					List<Long> interceptWordIdList = new ArrayList<>();

					interceptWordHousesList.forEach(s -> {
						// 1.校验敏感词是否已存在
						QueryWrapper interceptWrapper = QueryWrapper.create()
								.select(INTERCEPT_WORD_HOUSE.ID, INTERCEPT_WORD_HOUSE.NAME)
								.where(INTERCEPT_WORD_HOUSE.NAME.eq(s))
								.and(INTERCEPT_WORD_HOUSE.CREATE_USER_NAME.eq(userInfo.getUserName()));
						InterceptWordHouse interceptWord = interceptWordHouseService.getOne(interceptWrapper);
						if (null != interceptWord) {
							interceptWordIdList.add(interceptWord.getId());
						} else {
							InterceptWordHouse wordHouse = InterceptWordHouse.builder().name(s).tenantId(userInfo.getTenantId())
									.applicationCount(1L).wordCount(0).ownerType("personal").deletedFlag(0)
									.createUserId(userInfo.getId().toString()).createUserName(userInfo.getUserName())
									.createUserAccount(userInfo.getAccountName()).updateUserId(userInfo.getId().toString())
									.createTime(DateUtil.getCurrentTime()).updateTime(DateUtil.getCurrentTime()).build();
							interceptWordHouseList.add(wordHouse);
						}
					});
					// 2. 新增敏感词
					if (CollectionUtil.isNotEmpty(interceptWordHouseList)) {
						interceptWordHouseService.saveBatch(interceptWordHouseList);
					}
					// 3. 将敏感词与应用关联
					List<Long> interceptIds = interceptWordHouseList.stream().map(InterceptWordHouse::getId).collect(Collectors.toList());
					interceptWordIdList.addAll(interceptIds);
					List<InterceptWordHouseApplicationRel> relList = interceptWordIdList.stream().map(id -> {
						InterceptWordHouseApplicationRel rel = new InterceptWordHouseApplicationRel();
						rel.setApplicationId(applicationInfo.getApplicationId());
						rel.setInterceptWordHouseId(id);
						rel.setStatus("1");
						rel.setTenantId(userInfo.getTenantId());
						return rel;
					}).collect(Collectors.toList());
					interceptWordHouseApplicationRelMapper.insertBatch(relList);
				}
				/*
				回退插件关联关系
				 */
				if (StringUtils.isNotBlank(applicationInfoVersionDTO.getPluginDataJson())) {
					List<String> pluginNameList = JsonUtil.jsonToList(applicationInfoVersionDTO.getPluginDataJson(), String.class);
					// 1.根据插件名称获取插件信息
					QueryWrapper pluginWrapper = QueryWrapper.create().where(APPLICATION_PLUGIN.PLUGIN_NAME.in(pluginNameList));
					List<ApplicationPlugin> pluginList = applicationPluginService.list(pluginWrapper);
					// 2.将应用与插件进行绑定
					List<ApplicationPluginData> appPluginDataList = pluginList.stream().map(plugin -> {
						ApplicationPluginData pluginData = new ApplicationPluginData();
						pluginData.setApplicationId(applicationInfo.getApplicationId());
						pluginData.setPluginId(plugin.getPluginId());
						pluginData.setStatus("是");
						return pluginData;
					}).collect(Collectors.toList());
					if (CollectionUtil.isNotEmpty(appPluginDataList)) {
						applicationPluginDataService.saveBatchSelective(appPluginDataList);
					}
				}

				//工作流和对话流
				if(ObjectUtil.isNotEmpty(applicationInfoVersionDTO.getNodesJson())) {
					List<Component> components = new ArrayList<>();
					List<ComponentNodeDto> nodes = new ArrayList<>();
					List<ComponentNodeRel> nodeRel = new ArrayList<>();
					List<MetaParam> metaParams = new ArrayList<>();
					if(StringUtils.isNotEmpty(applicationInfoVersionDTO.getComponentJson())){
						components =JsonUtil.jsonToList(applicationInfoVersionDTO.getComponentJson(),Component.class);
					}
					if(StringUtils.isNotEmpty(applicationInfoVersionDTO.getNodesJson())){
						nodes = JsonUtil.jsonToList(applicationInfoVersionDTO.getNodesJson(),ComponentNodeDto.class);
					}
					if(StringUtils.isNotEmpty(applicationInfoVersionDTO.getNodeRelJson())){
						nodeRel = JsonUtil.jsonToList(applicationInfoVersionDTO.getNodeRelJson(),ComponentNodeRel.class);
					}
					if(StringUtils.isNotEmpty(applicationInfoVersionDTO.getMetaParamsJson())){
						metaParams = JsonUtil.jsonToList(applicationInfoVersionDTO.getMetaParamsJson(),MetaParam.class);
					}
					List<ComponentNode> ComponentNodeList = new ArrayList<>();
					if (CollectionUtils.isNotEmpty(components)) {
						componentMapper.insertBatch(components);
					}
					if (CollectionUtils.isNotEmpty(nodes)) {
						for (ComponentNodeDto componentNodeDto : nodes) {
							ComponentNode componentNode = new ComponentNode();
							BeanUtil.copyProperties(componentNodeDto, componentNode);
							ComponentNodeList.add(componentNode);
						}
						componentNodeMapper.insertBatch(ComponentNodeList);
					}
					if (CollectionUtils.isNotEmpty(nodeRel)) {
						componentNodeRelMapper.insertBatch(nodeRel);
					}
					if (CollectionUtils.isNotEmpty(metaParams)) {
						metaParamMapper.insertBatch(metaParams);
					}
				}
				// 目前token中未保存租户号
				TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
				if (null != tokenUserInfo) {
					OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
					Long currentTimeMillis=System.currentTimeMillis();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// 使用Date构造函数，传入时间戳
					Date date = new Date(currentTimeMillis);
					String formattedDate = sdf.format(date);
					applicationInfo.setCreateTime(formattedDate);
					applicationInfo.setUpdateTime(formattedDate);
					applicationInfo.setCreateUser(oauthUser.getAccountName());
					applicationInfo.setUpdateUser(oauthUser.getAccountName());
					applicationInfo.setTenantId(oauthUser.getTenantId());
				}
				mapper.insert(applicationInfo, false);
			}

		}catch (IOException exception){
			//throw new  RuntimeException(exception);
			return Result.fail("导出失败，数据有问题，请重新导出新数据");
		}
		return Result.success();
	}

	@Override
	public Result<String> aiAudio(AiAudioParam param) {
		if (null == aiAudioStrategyMap || aiAudioStrategyMap.isEmpty()) {
			log.error("未配置音频生成策略");
			return Result.fail("未配置音频生成策略");
		}
		// 默认minimax策略
		if (StringUtils.isBlank(param.getStrategy())) {
			param.setStrategy("minmaxAudioStrategy");
		}
		AiAudioStrategy aiAudioStrategy;
		String applicationId = param.getApplicationId();
		if (!StringUtils.isBlank(applicationId)) {
			ApplicationInfo applicationInfo = getByAppId(applicationId);
			String modelId = applicationInfo.getModelId();
			aiAudioStrategy = llmInfoService.getAiAudioStrategy(modelId);
		} else {
			aiAudioStrategy = aiAudioStrategyMap.get(param.getStrategy());
		}
		String aiAudio = aiAudioStrategy.getAiAudio(param);
		return Result.success(aiAudio);
	}

	@Override
	public void updateApp(ApplicationInfo applicationInfo) {
		if (null == applicationInfo || StringUtils.isBlank(applicationInfo.getApplicationId())) {
			return;
		}

		UpdateChain.of(ApplicationInfo.class)
				.set(APPLICATION_INFO.TYPE, applicationInfo.getType())
				.where(APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
				.update();

		// Wrappers.of(mapper)
		// 		.where(APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()))

	}

	@Override
	public Result updateApplicationPublishAppStoreState(ApplicationPublishAppStoreUpdateParam param) {
		if (StringUtils.isBlank(param.getApplicationId())) {
				return Result.fail("应用业务id为空");
		}

		UpdateChain.of(ApplicationInfo.class)
				.set(APPLICATION_INFO.PUBLISH_APP_STORE, param.getPublishAppStore())
				.where(APPLICATION_INFO.APPLICATION_ID.eq(param.getApplicationId()))
				.update();
		return Result.success();
	}

	@Override
	public Result<AiImageResult> generaImage(PromptGenerateParam param) {
		String modelId = param.getModelId();
		if (StringUtils.isBlank(modelId) && StringUtils.isNotBlank(param.getApplicationId())) {
			ApplicationInfo applicationInfo = getByAppId(param.getApplicationId());
			modelId = applicationInfo.getModelId();
		}
		if (StringUtils.isBlank(modelId)) {
			// 默认使用豆包模型
			LlmInfo byModelCode = llmInfoService.getByDefaultImageModelCode(param.getStrategy());
			modelId = byModelCode.getModelId();
			param.setStrategy(byModelCode.getModelCode());
		}

		AiImageStrategy aiImageStrategy = llmInfoService.getAiImageStrategy(modelId);
		if (null == aiImageStrategy) {
			log.error("未配置图片生成策略: {}", param.getStrategy());
			return Result.fail("未配置图片生成策略: " + param.getStrategy());
		}
		List<String> aiImageUrls = aiImageStrategy.getAiImage(param);

		String aiImageUrl = StringConstant.BLANK;
		if (CollectionUtils.isNotEmpty(aiImageUrls)) {
			aiImageUrl = aiImageUrls.get(0);
		}


		AiImageResult aiImageResult = new AiImageResult();
		aiImageResult.setImageUrl(aiImageUrl);
		aiImageResult.setRatio(param.getRatio());
		return Result.success(aiImageResult);
	}

	/**
	 * @param applicationId
	 * @param publishAppStore
	 * @author: caohaifeng
	 * @date: 2025/2/15 16:22
	 * @Description: 审核通过 更新上架字段
	 * @Version:1.0
	 */
	@Override
	public int updateAppStoreByApplicationId(String applicationId, Integer publishAppStore) {
		if (StringUtils.isNotBlank(applicationId)) {
			ApplicationInfo applicationInfo = new ApplicationInfo();
			applicationInfo.setPublishAppStore(publishAppStore);
			return update(applicationInfo, QueryWrapper.create().where(APPLICATION_INFO.APPLICATION_ID.eq(applicationId))) ? 1 : 0;
		}
		return -1;
	}

	/**
	 *  清空应用信息
	 * @param applicationInfo
	 */
	private void clear(ApplicationInfo applicationInfo, TokenUser tokenUserInfo, Integer copyFlag) {
		String applicationNameSuffix = ApplicationConstant.FROM_APP_PAGE_SUFFIX;
		if (Objects.equals(copyFlag, CopyAppFlagEnum.FROM_APP_STORE_PAGE.getType())) {
			applicationNameSuffix = ApplicationConstant.FROM_APP_STORE_PAGE_SUFFIX;
		}
		applicationInfo.setId(null);
		applicationInfo.setApplicationId(IdUtil.simpleUUID());
		applicationInfo.setApplicationCode(IdUtil.simpleUUID());
		applicationInfo.setApplicationName(applicationInfo.getApplicationName() + applicationNameSuffix);
		applicationInfo.setClientLink(null);
		applicationInfo.setApi(null);
		applicationInfo.setApiKey(null);
		applicationInfo.setTenantId(null);
		applicationInfo.setPublishDesc(null);
		applicationInfo.setPublishStatus("4");
		applicationInfo.setPublishTime(null);
		applicationInfo.setPublishAppStore(0);
		applicationInfo.setCreateUser(tokenUserInfo.getAccountName());
		applicationInfo.setUpdateUser(tokenUserInfo.getAccountName());
	}

	/**
	 * 设置模板
	 * @param applicationInfo
	 */
	private void setDialogTemplate(ApplicationInfo applicationInfo) {
		// web端模板
		Wrappers<Object> templateWrappers = Wrappers.init()
				.where(DialogTemplateTableDef.DIALOG_TEMPLATE.TEMPLATE_ID.eq(applicationInfo.getTemplateId()))
				.limit(1);

		DialogTemplate template = templateService.getOne(templateWrappers);
		if (template != null) {
			applicationInfo.setTemplateRoute(template.getTemplateRoute());
			applicationInfo.setTemplateId(template.getTemplateId());
		}

		// 移动端模板
		Wrappers<Object> templateRouteWrappers = Wrappers.init().limit(1);
		if (Objects.isNull(applicationInfo.getId())) {
			// 新增应用时候，默认通用模版
			templateRouteWrappers.and(DIALOG_TEMPLATE.TEMPLATE_ROUTE.eq(ApplicationConstant.DEFAULT_TEMPLATE_ROUTE_H5));
		} else {
			// 修改时候，根据前端传入的移动端模版id取值即可
			templateRouteWrappers.and(DialogTemplateTableDef.DIALOG_TEMPLATE.TEMPLATE_ID.eq(applicationInfo.getMobileTemplateId()));
		}
		template = templateService.getOne(templateRouteWrappers);
		if (template != null) {
			applicationInfo.setMobileTemplateRoute(template.getTemplateRoute());
			applicationInfo.setMobileTemplateId(template.getTemplateId());
		}
	}

	/**
	 * 关联知识库
	 */
	private void bindingKnowledge(ApplicationInfo applicationInfo) {
		if (StrUtil.isNotBlank(applicationInfo.getApplicationId())) {
			// 清空关联表
			Wrappers<Object> wrappers = Wrappers.init()
					.where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
					.and(APPLICATION_KNOWLEDGE.TYPE.eq(ApplicationKnowledgeTypeEnum.KNOWLEDGE));
			applicationKnowledgeService.remove(wrappers);
		}
		// 新增关联表
		if (CollectionUtils.isNotEmpty(applicationInfo.getKnowledgeIds())) {
			List<ApplicationKnowledge> list = applicationInfo.getKnowledgeIds().stream()
					.map(p -> {
						ApplicationKnowledge applicationKnowledge = new ApplicationKnowledge();
						applicationKnowledge.setApplicationId(applicationInfo.getApplicationId());
						applicationKnowledge.setKnowledgeId(p.getKnowledgeId());
						applicationKnowledge.setType(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType());
						return applicationKnowledge;
					}).collect(Collectors.toList());

			// List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.getByIds(applicationInfo.getKnowledgeIds());
			// List<String> denseVectorIds = knowledgeInfoList.stream().map(KnowledgeInfo::getDenseVectorId).distinct().collect(Collectors.toList());
			// if (denseVectorIds.size() > 1) {
			// 	ResultCodeEnum badRequest = ResultCodeEnum.BUSINESS_FAILED;
			// 	throw new ParamException(new ResultCodeBase() {
			// 		@Override
			// 		public String getCode() {
			// 			return badRequest.getCode();
			// 		}
			//
			// 		@Override
			// 		public String getMsg() {
			// 			return "知识库向量模型不一致";
			// 		}
			// 	});
			// }
			applicationKnowledgeService.saveBatch(list);
		}

		// 新增关联表
		if (CollectionUtils.isNotEmpty(applicationInfo.getWorkflowIds())) {
			List<ApplicationKnowledge> list = applicationInfo.getWorkflowIds().stream()
					.map(p -> {
						ApplicationKnowledge applicationKnowledge = new ApplicationKnowledge();
						applicationKnowledge.setApplicationId(applicationInfo.getApplicationId());
						applicationKnowledge.setKnowledgeId(p.getKnowledgeId());
						applicationKnowledge.setType(ApplicationKnowledgeTypeEnum.WORKFLOW.getType());
						return applicationKnowledge;
					}).collect(Collectors.toList());
			applicationKnowledgeService.saveBatch(list);
		}
	}

	/**
	 * 关联应用快捷指令
	 * @param applicationInfo
	 */
	private void bindingApplicationQuickCommand(ApplicationInfo applicationInfo) {
		Wrappers<Object> wrappers = Wrappers.init()
				.where(APPLICATION_QUICK_COMMAND.APPLICATION_ID.eq(applicationInfo.getApplicationId()));
		applicationQuickCommandService.remove(wrappers);

		if (CollectionUtils.isNotEmpty(applicationInfo.getApplicationQuickCommandList())) {
			List<ApplicationQuickCommand> applicationQuickCommandList = applicationInfo.getApplicationQuickCommandList()
					.stream().map(p -> {
						ApplicationQuickCommand applicationQuickCommand = new ApplicationQuickCommand();
						applicationQuickCommand.setCommandId(IdUtil.simpleUUID());
						applicationQuickCommand.setCommandContent(p.getCommandContent());
						applicationQuickCommand.setCommandType(p.getCommandType());
						applicationQuickCommand.setCommandName(p.getCommandName());
						applicationQuickCommand.setStatus(1);
						applicationQuickCommand.setApplicationId(applicationInfo.getApplicationId());
						applicationQuickCommand.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
						applicationQuickCommand.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
						return applicationQuickCommand;
					}).collect(Collectors.toList());
			applicationQuickCommandService.saveBatch(applicationQuickCommandList);
		}
	}

	/**
	 * 关联预设问题
	 */
	private void bindingPresetQuestion(ApplicationInfo applicationInfo) {
		Wrappers<Object> wrappers = Wrappers.init()
				.where(PRESET_QUESTION.APPLICATION_ID.eq(applicationInfo.getApplicationId()));
		presetQuestionService.remove(wrappers);
		if (CollectionUtils.isNotEmpty(applicationInfo.getPresetQuestionList())) {
			List<PresetQuestion> presetQuestionList = applicationInfo.getPresetQuestionList()
					.stream().map(p -> {
						PresetQuestion presetQuestion = new PresetQuestion();
						presetQuestion.setPresetId(IdUtil.simpleUUID());
						presetQuestion.setQuestion(p);
						presetQuestion.setType("推荐问题");
						presetQuestion.setStatus(1);
						presetQuestion.setApplicationId(applicationInfo.getApplicationId());
						presetQuestion.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
						presetQuestion.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
						return presetQuestion;
					}).collect(Collectors.toList());
			presetQuestionService.saveBatch(presetQuestionList);
		}
	}

	/**
	 * 复制插件
	 */
	private void copyPlugin(String newAppId, String oldAppId) {
		AppConfigListParam applicationPluginData = new AppConfigListParam();
		applicationPluginData.setApplicationId(oldAppId);
		Result<List<ApplicationPluginData>> applicationPluginDataList = applicationPluginDataService.getApplicationPluginDataList(applicationPluginData);
		List<ApplicationPluginData> pluginDataList = applicationPluginDataList.getData();
		if (CollectionUtils.isNotEmpty(pluginDataList)) {
			List<ApplicationPlugin> collect = pluginDataList.stream()
					.map(p -> BeanUtil.toBean(p, ApplicationPlugin.class)).collect(Collectors.toList());
			ApplicationConfigEditParam addPlugDataParam = new ApplicationConfigEditParam();
			addPlugDataParam.setPluginList(collect);
			addPlugDataParam.setApplicationId(newAppId);
			applicationPluginDataService.addApplicationPluginData(addPlugDataParam);
		}
	}

	/**
	 * 复制应用配置项
	 */
	private void copyApplicationConfiguration(String newAppId, String oldAppId, TokenUser tokenUserInfo) {
		ApplicationConfigurationParam param = new ApplicationConfigurationParam();
		param.setApplicationId(oldAppId);
		param.setPageNo(1);
		param.setPageSize(999);
		Result<Page<ApplicationConfiguration>> configurationResult = applicationConfigurationService.getApplicationConfigurationList(param);
		Page<ApplicationConfiguration> data = configurationResult.getData();
		List<ApplicationConfiguration> records1 = data.getRecords();
		for (ApplicationConfiguration configuration : records1) {
			configuration.setApplicationId(newAppId);
			applicationConfigurationService.addApplicationConfiguration(configuration);
		}
	}

	/**
	 * 设置当前应用关联的知识库中收集的文档、视频、音频、图片个数
	 * @param applicationInfo 应用信息
	 */
	private void setCollectFileTypeCount(ApplicationInfo applicationInfo) {
		List<ApplicationKnowledge> knowledgeBases = applicationInfo.getKnowledgeIds();
		if (CollectionUtil.isEmpty(knowledgeBases)) {
			applicationInfo.setFileCount(0L);
			applicationInfo.setAudioCount(0L);
			applicationInfo.setImageCount(0L);
			applicationInfo.setVideoCount(0L);
			applicationInfo.setUrlCount(0L);
			return;
		}
		List<String> knowledgeIds = knowledgeBases.stream().map(ApplicationKnowledge::getKnowledgeId).collect(Collectors.toList());
		List<String> foldersIds = Lists.newArrayList();
		// 这里添加-1的目录，是为了防止查询到非当前知识库的文件
		foldersIds.add("-1");
		// 获取文件夹id
		List<Folders> list = Folders.creat()
				.where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
				.lists();
		List<String> foldersIdList = list.stream().map(Folders::getFoldersId).collect(Collectors.toList());
		foldersIds.addAll(foldersIdList);

		// 文档（含yayi知识库文档）、视频、音频、图片
		Wrappers<Object> fileWrappers = Wrappers.init().select("type", "count(1) count")
				.and((FILE.FOLDERS_ID.in(foldersIds)))
				.and(FILE.STATUS.eq(FileStatusEnum.SUCCESS.getCode()))
				.and(FILE.DELETE_FLAG.eq(0))
				.groupBy(FILE.TYPE);
		List<File> files = fileMapper.selectListByQuery(fileWrappers);
		// url链接
		Wrappers<Object> urlParserInfoWrappers = Wrappers.init()
				.and((URL_PARSER_INFO.KNOWLEDGE_ID.in(knowledgeIds)))
				.and(URL_PARSER_INFO.STATUS.eq(FileStatusEnum.SUCCESS.getCode()))
				.and(URL_PARSER_INFO.DELETE_FLAG.eq(0));
		long totalUrlCount =  urlParserInfoMapper.selectCountByQuery(urlParserInfoWrappers);


		Map<Integer, Long> typeCountMap = files.stream()
				.collect(Collectors.toMap(File::getType, File::getCount));
		long fileCount = typeCountMap.getOrDefault(FileTypeEnum.FILE.getType(), 0L) + typeCountMap.getOrDefault(FileTypeEnum.YAYI_DOC.getType(), 0L);
		long audioCount = typeCountMap.getOrDefault(FileTypeEnum.AUDIO.getType(), 0L);
		long imageCount = typeCountMap.getOrDefault(FileTypeEnum.IMAGE.getType(), 0L);
		long videoCount = typeCountMap.getOrDefault(FileTypeEnum.VIDEO.getType(), 0L);
		applicationInfo.setFileCount(fileCount);
		applicationInfo.setAudioCount(audioCount);
		applicationInfo.setImageCount(imageCount);
		applicationInfo.setVideoCount(videoCount);
		applicationInfo.setUrlCount(totalUrlCount);
	}

	/**
	 * 分页查询应用信息
	 * @param applicationInfo 参数
	 * @param flag 标识
	 * @return 结果
	 */
	private Page<ApplicationInfo> getPageApplication(ApplicationInfoPageParam applicationInfo, Boolean flag) {
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		Long currentUserId = tokenOauthUserInfo.getId();
		String defaultAppid = AppConfigContant.getConfiguration(APP_CONFIG_DEFAULT_APP_ID);
		Wrappers wrappers = Wrappers.init()
				.where(StringUtils.isNotBlank(applicationInfo.getApplicationName()), APPLICATION_INFO.APPLICATION_NAME.like(applicationInfo.getApplicationName()))
				.and(StringUtils.isNotBlank(applicationInfo.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
				.and(applicationInfo.isNeedParam() && StringUtils.isEmpty(applicationInfo.getApplicationId())
						&& CollectionUtil.isEmpty(applicationInfo.getApplicationIdLists()), APPLICATION_INFO.APPLICATION_ID
						.eq("yingyongid"))
				.and(applicationInfo.isOnlyMine(), APPLICATION_INFO.CREATE_USER.eq(tokenOauthUserInfo.getAccountName()))
				.and(StringUtils.isNotBlank(applicationInfo.getPublishType()), APPLICATION_INFO.PUBLISH_TYPE.eq(applicationInfo.getPublishType()))
				// 默认应用不展示
				.and(APPLICATION_INFO.APPLICATION_ID.ne(defaultAppid).when(StringUtils.isNotBlank(defaultAppid)))
				.and(applicationInfo.getStartTime()!=null&&applicationInfo.getEndTime()!=null,APPLICATION_INFO.CREATE_TIME.between(applicationInfo.getStartTime(),applicationInfo.getEndTime()))
				.orderBy(APPLICATION_INFO.CREATE_TIME.desc());
		// 当前用户收藏的应用
		Wrappers<Object> myFavoriteWrappers = Wrappers.init()
				.where(MY_FAVORITE.USER_ID.eq(currentUserId))
				.and(MY_FAVORITE.FAVORITE_FLAG.eq(FavoriteFlagEnum.FAVORITE.getCode()));
		List<MyFavorite> myFavorites = myFavoriteMapper.selectListByQuery(myFavoriteWrappers);
		if (Objects.equals(applicationInfo.getFavoriteFlag(), FavoriteFlagEnum.FAVORITE.getCode())) {
			List<String> myFavoriteApplicationIds = myFavorites.stream().map(MyFavorite::getApplicationId).collect(Collectors.toList());
			// 避免收藏为空的时候，查询出所有的内容
			myFavoriteApplicationIds.add("yingyongid");
			wrappers.and(CollectionUtil.isNotEmpty(myFavoriteApplicationIds), APPLICATION_INFO.APPLICATION_ID.in(myFavoriteApplicationIds));
		}
        if (flag && applicationInfo.getTypeLists() == null) {
			queryApplicationCondition(applicationInfo, wrappers);
		}

        //应用类型 运营模块统计参数使用
		wrappers.and(CollectionUtil.isNotEmpty(applicationInfo.getTypeLists()), APPLICATION_INFO.TYPE.in(applicationInfo.getTypeLists()));
		wrappers.and(CollectionUtil.isNotEmpty(applicationInfo.getApplicationIdLists()), APPLICATION_INFO.APPLICATION_ID.in(applicationInfo.getApplicationIdLists()));


        List<LlmInfo> llmInfoList = llmInfoService.list();
        HashMap<String, String> llmMap = llmInfoList.stream().collect(Collectors.toMap(
                LlmInfo::getModelId,
                LlmInfo::getModelName,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
        //ApplicationInfo applicationInfo1 = mapper.selectOneById(wrapper);
        // 获取关联知识库
        List<ApplicationKnowledge> applicationKnowledges = applicationKnowledgeService.list();
		Map<String, List<ApplicationKnowledge>> knowledgeMap = applicationKnowledges.stream().filter(p -> p.getType().equals(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType()))
				.collect(Collectors.groupingBy(ApplicationKnowledge::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), list -> list)));

		Map<String, List<ApplicationKnowledge>> workflowMap = applicationKnowledges.stream().filter(p -> p.getType().equals(ApplicationKnowledgeTypeEnum.WORKFLOW.getType()))
				.collect(Collectors.groupingBy(ApplicationKnowledge::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), list -> list)));

        // 获取预设问题
        List<PresetQuestion> presetQuestionList = presetQuestionService.list();
        Map<String, List<String>> preQuestionMap = presetQuestionList.stream().collect(Collectors.groupingBy(p -> p.getApplicationId(), Collectors.collectingAndThen(Collectors.toList(), list -> {
            return list.stream().map(PresetQuestion::getQuestion).distinct().collect(Collectors.toList());
        })));

        // 获取应用快捷指令
        List<ApplicationQuickCommand> applicationQuickCommandList = applicationQuickCommandService.list();
		Map<String, List<ApplicationQuickCommand>> applicationQuickCommandMap =
				applicationQuickCommandList.stream()
						.collect(Collectors.groupingBy(
								ApplicationQuickCommand::getApplicationId
						));

		List<InterceptWordHouseApplicationRel> applicationHouseList = relService.list(Wrappers.init().where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq(ONE)));
		Map<String, List<Long>> map = new HashMap<>();
		if (CollectionUtils.isNotEmpty(applicationHouseList)) {
			map = applicationHouseList.stream()
					.collect(Collectors.groupingBy(
							InterceptWordHouseApplicationRel::getApplicationId,
							Collectors.mapping(InterceptWordHouseApplicationRel::getInterceptWordHouseId, Collectors.toList())
					));
		}

		// 用户特定应用配置
		Map<String, ApplicationUserConfiguration> userConfigMap = new HashMap<>();
		if (null != currentUserId) {
			List<ApplicationUserConfiguration> applicationUserConfigurations = applicationUserConfigurationService.selectByUserId(currentUserId);
			if (CollectionUtil.isNotEmpty(applicationUserConfigurations)) {
				userConfigMap = applicationUserConfigurations.stream().collect(Collectors.toMap(ApplicationUserConfiguration::getApplicationId, i -> i));
			}
		}

		// 发布状态为空 || 发布状态不为1的需要根据权限查询
		if (Objects.isNull(applicationInfo.getPublishAppStore()) || PublishAppStoreEnum.COMPLETE_PUBLIS.getCode() != applicationInfo.getPublishAppStore() ) {
			// 根据权限查询
			PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.APPLICATION);
		} else {
			wrappers.and(APPLICATION_INFO.PUBLISH_APP_STORE.eq(applicationInfo.getPublishAppStore()));
		}

        Page<ApplicationInfo> page = page(Page.of(applicationInfo.getPageNo(), applicationInfo.getPageSize()), wrappers);
        List<ApplicationInfo> records = page.getRecords();
		// 设置应用是否收藏
		Map<String, MyFavorite> myFavoriteMap = myFavorites.stream()
				.collect(Collectors.toMap(MyFavorite::getApplicationId, favorite -> favorite));
		if (CollectionUtils.isNotEmpty(records)) {
			records.forEach(item -> {
				MyFavorite myFavorite = myFavoriteMap.get(item.getApplicationId());
				item.setFavoriteFlag(null==myFavorite?FavoriteFlagEnum.NOT_FAVORITE.getCode():myFavorite.getFavoriteFlag());
			});
		}

		// 获取用户信息
		List<String> accountNameList = records.stream().map(ApplicationInfo::getCreateUser).collect(Collectors.toList());
		List<String> appIds = records.stream().map(ApplicationInfo::getApplicationId).collect(Collectors.toList());
		List<OauthUser> userList = userService.getUserDetailByAccountNameList(accountNameList);
		Map<String, OauthUser> userMap = userList.stream()
				.collect(Collectors.toMap(
						OauthUser::getAccountName,
						p -> p,
						(k1, k2) -> k1
				));

		// 获取应用配置
		Map<String, List<ApplicationConfiguration>> configMap = applicationConfigurationService.getConfigByAppIds(appIds);

		// 获取mcp服务
		Map<String, List<McpServer>> serverByAppIds = applicationMcpRefService.getMcpServerByAppIds(appIds);

		for (ApplicationInfo item : records) {
            // 获取模型名称
            item.setModelName(llmMap.getOrDefault(item.getModelId(), StringConstant.BLANK));
            // 获取关联知识库
            item.setKnowledgeIds(knowledgeMap.getOrDefault(item.getApplicationId(), Lists.newArrayList()));
            // 获取预设问题
            item.setPresetQuestionList(preQuestionMap.getOrDefault(item.getApplicationId(), Lists.newArrayList()));
			// 设置应用快捷指令
			item.setApplicationQuickCommandList(applicationQuickCommandMap.getOrDefault(item.getApplicationId(), Lists.newArrayList()));
            // 关联敏感词库
			item.setInterceptWordHouses(map.getOrDefault(item.getApplicationId(), Lists.newArrayList()));
			// 获取关联工作流
			item.setWorkflowIds(workflowMap.getOrDefault(item.getApplicationId(), Lists.newArrayList()));

			// 如果空 给默认配置 防止空指针
			ApplicationUserConfiguration configuration = new ApplicationUserConfiguration();
			ApplicationUserConfiguration userConfig = userConfigMap.getOrDefault(item.getApplicationId(), configuration);
			// 流式语音播报优先使用配置项
			if (null != userConfig && !Objects.isNull(userConfig.getStreamVoice())) {
				item.setStreamVoice(userConfig.getStreamVoice() == 1 ? "是" : "否");
			}
			if (StringUtils.isBlank(item.getStreamVoice())) {
				item.setStreamVoice("否");
			}

			// 设置创建者真实姓名
			OauthUser oauthUser = userMap.get(item.getCreateUser());
			item.setAccountName(oauthUser == null ? StringConstant.BLANK : oauthUser.getUsername());

			// 模型兜底参数设置
			modelFallbackFlagMethod(item);

			// mcp服务
			List<McpServer> mcpServers = serverByAppIds.get(item.getApplicationId());
			if (CollectionUtil.isNotEmpty(mcpServers)) {
				List<String> mcpIds = mcpServers.stream().map(McpServer::getMcpId).distinct().collect(Collectors.toList());
				item.setMcpServerIds(mcpIds);
			}

			// 应用配置
			List<ApplicationConfiguration> configurationList = configMap.get(item.getApplicationId());
			setConfig(configurationList, item);
		};
		return page;
	}

	/**
	 * 分页查询已上架应用信息
	 */
	private Page<ApplicationInfo> getPagePublisAppApplication(ApplicationInfoPageParam applicationInfo, List<String> accoutNames) {
		if (CollectionUtil.isEmpty(accoutNames)) {
			return new Page<>();
		}

		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		Long currentUserId = tokenOauthUserInfo.getId();
		String defaultAppid = AppConfigContant.getConfiguration(APP_CONFIG_DEFAULT_APP_ID);
		Wrappers wrappers = Wrappers.init()
				.where(StringUtils.isNotBlank(applicationInfo.getApplicationName()), APPLICATION_INFO.APPLICATION_NAME.like(applicationInfo.getApplicationName()))
				.and(APPLICATION_INFO.PUBLISH_APP_STORE.eq(applicationInfo.getPublishAppStore()))
				.and(StringUtils.isNotBlank(applicationInfo.getPublishType()), APPLICATION_INFO.PUBLISH_TYPE.eq(applicationInfo.getPublishType()))
				// 默认应用不展示
				.and(APPLICATION_INFO.APPLICATION_ID.ne(defaultAppid).when(StringUtils.isNotBlank(defaultAppid)))
				.and(APPLICATION_INFO.CREATE_USER.in(accoutNames))
				.orderBy(APPLICATION_INFO.CREATE_TIME.desc());
		// 当前用户收藏的应用
		Wrappers<Object> myFavoriteWrappers = Wrappers.init()
				.where(MY_FAVORITE.USER_ID.eq(currentUserId))
				.and(MY_FAVORITE.FAVORITE_FLAG.eq(FavoriteFlagEnum.FAVORITE.getCode()));
		List<MyFavorite> myFavorites = myFavoriteMapper.selectListByQuery(myFavoriteWrappers);
		if (Objects.equals(applicationInfo.getFavoriteFlag(), FavoriteFlagEnum.FAVORITE.getCode())) {
			List<String> myFavoriteApplicationIds = myFavorites.stream().map(MyFavorite::getApplicationId).collect(Collectors.toList());
			wrappers.and(CollectionUtil.isNotEmpty(myFavoriteApplicationIds), APPLICATION_INFO.APPLICATION_ID.in(myFavoriteApplicationIds));
		}
		if (applicationInfo.getTypeLists() == null) {
			queryApplicationCondition(applicationInfo, wrappers);
		}

		Page<ApplicationInfo> page = page(Page.of(applicationInfo.getPageNo(), applicationInfo.getPageSize()), wrappers);
		List<ApplicationInfo> records = page.getRecords();
		// 设置应用是否收藏
		Map<String, MyFavorite> myFavoriteMap = myFavorites.stream()
				.collect(Collectors.toMap(MyFavorite::getApplicationId, favorite -> favorite));
		if (CollectionUtils.isNotEmpty(records)) {
			records.forEach(item -> {
				MyFavorite myFavorite = myFavoriteMap.get(item.getApplicationId());
				item.setFavoriteFlag(null==myFavorite?FavoriteFlagEnum.NOT_FAVORITE.getCode():myFavorite.getFavoriteFlag());
			});
		}

		return page;
	}

	private void queryApplicationCondition(ApplicationInfoPageParam applicationInfo, Wrappers wrappers) {
		// 区分应用是问答助手、智能搜索、工作流
		String type = applicationInfo.getType();
		if (StringUtils.equals(type, AnswerStrategyContant.APP_TYPE_WORKFLOW)) {
			// 工作流
			wrappers.and(APPLICATION_INFO.TYPE.eq(AnswerStrategyContant.APP_TYPE_WORKFLOW));
		} else if (StringUtils.equals(type, AnswerStrategyContant.APP_TYPE_SEARCH)) {
			// 智能搜索
			wrappers.and(APPLICATION_INFO.TYPE.eq(AnswerStrategyContant.APP_TYPE_SEARCH));
		} else if (StringUtils.equals(type, AnswerStrategyContant.APP_TYPE_DIALOGUE_SEARCH)) {
			// 智能搜索和问答助手（主要是“知识库”页面用到这个查下条件）
			wrappers.and(APPLICATION_INFO.TYPE.eq(AnswerStrategyContant.APP_TYPE_SEARCH).or(APPLICATION_INFO.TYPE.isNull()).or(APPLICATION_INFO.TYPE.eq("")).or(APPLICATION_INFO.TYPE.eq(AnswerStrategyContant.APP_TYPE_QA)));
		} else if (StringUtils.isBlank(type)) {
			// 工作流关联应用
			wrappers.and(APPLICATION_INFO.TYPE.ne(ApplicationTypeEnum.WORKFLOW_APPLICATION.getName()));
		} else {
			// 问答助手
			Object[] types = type.split(",");
			if (types.length > 0) {
				wrappers.and(APPLICATION_INFO.TYPE.in(types));
			} else {
				wrappers.and(APPLICATION_INFO.TYPE.ne(ApplicationTypeEnum.WORKFLOW_APPLICATION.getName()));
			}
		}
	}


	private void bindingConfiguration(ApplicationInfo applicationInfo) {
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		Long currentUserId = tokenOauthUserInfo.getId();
		if (null == currentUserId) {
			return;
		}
		Assert.notNull(currentUserId, "用户未登录");
		String streamVoice = applicationInfo.getStreamVoice();
		List<ApplicationUserConfiguration> configurations = applicationUserConfigurationService.selectByUserIdAndAppId(currentUserId, applicationInfo.getApplicationId());
		if (CollectionUtils.isEmpty(configurations)) {
			return;
		}
		ApplicationUserConfiguration configuration = configurations.get(0);
		if (StringUtils.equals(streamVoice, "是")) {
			configuration.setStreamVoice(1);
		} else {
			configuration.setStreamVoice(0);
		}
		applicationUserConfigurationService.saveOrUpdate(configuration);
	}


	private void bindingInterceptWordHouse(ApplicationInfo applicationInfo) {
		String applicationId = applicationInfo.getApplicationId();
		List<Long> houseIds = applicationInfo.getInterceptWordHouses();
		Assert.isTrue(StringUtils.isNotBlank(applicationId), "应用ID不能为空");
		// 重新关联敏感词库
		relService.remove(Wrappers.init().where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(applicationId)));
		if (CollectionUtils.isEmpty(houseIds)) {
			return;
		}
		publisher.publishEvent(new SceneEvent(null));
		houseIds.stream().map(id -> {
			InterceptWordHouseApplicationRel rel = new InterceptWordHouseApplicationRel();
			rel.setApplicationId(applicationId);
			rel.setInterceptWordHouseId(id);
			return rel;
		}).forEach(relService::addInterceptWordHouseApplicationRel);
	}

	/**
	 * 复制工作流
	 */
	private void copyWorkflow(String newAppId, String oldAppId, String type, String name, TokenUser tokenUserInfo) {
		ComponentTypeEnum typeEnum = null;
		if (StringUtils.isNotBlank(type)) {
			if (ApplicationTypeEnum.WORKFLOW.getName().equals(type)) {
				typeEnum = ComponentTypeEnum.APPLICATION;
			} else if (ApplicationTypeEnum.DIALOGUE.getName().equals(type)) {
				typeEnum = ComponentTypeEnum.APPLICATION_FLOW;
			}
		}
		if (null != typeEnum) {
			Component component = new Component();
			component.setComponentName(name);
			component.setComponentId(oldAppId + "_" + typeEnum.getCode());
			component.setComponentNewId(newAppId + "_" + typeEnum.getCode());
			component.setCreateUser(tokenUserInfo.getAccountName());
			component.setUpdateUser(tokenUserInfo.getAccountName());
			workFlowService.copy(component, "app", StringConstant.BLANK);
		}
	}

	/**
	 * 复制工作流
	 */
	private void copyModelPlugin(String newAppId, String oldAppId, String newAppName) {
		ModelPluginApiManage modelPluginApiManage = ModelPluginApiManage.create();
		modelPluginApiManage.setModelPluginApiId(oldAppId);
		modelPluginApiManage.setNewModelPluginApiId(newAppId);
		modelPluginApiManage.setType("app");
		modelPluginApiManage.setName(newAppName);
		modelPluginApiService.copy(modelPluginApiManage);
	}

	@Override
	public long getApplicationCount(ApplicationInfoPageParam applicationInfo) {


		Wrappers wrappers = Wrappers.init()
				.where(StringUtils.isNotBlank(applicationInfo.getApplicationName()), APPLICATION_INFO.APPLICATION_NAME.like(applicationInfo.getApplicationName()))
				// 默认应用不展示
				.orderBy(APPLICATION_INFO.CREATE_TIME.desc());
		wrappers.and(CollectionUtil.isNotEmpty(applicationInfo.getTypeLists()), APPLICATION_INFO.TYPE.in(applicationInfo.getTypeLists()));
		wrappers.and(CollectionUtil.isNotEmpty(applicationInfo.getApplicationIdLists()), APPLICATION_INFO.APPLICATION_ID.in(applicationInfo.getApplicationIdLists()));
		if(StringUtils.isNotBlank(applicationInfo.getKnowledgeId()) && CollectionUtil.isEmpty(applicationInfo.getApplicationIdLists())){
			wrappers.and(APPLICATION_INFO.APPLICATION_ID.eq("yingyongid"));
		}
		wrappers.and(APPLICATION_INFO.DELETE_FLAG.eq(0));

		return applicationInfoMapper.selectCountByQuery(wrappers);
	}
	public Result<Component> save(Component component) {

		if (StrUtil.isBlank(component.getComponentId())) {
			String componentId = ComponentServiceImpl.getComponentId(component);
			component.setComponentId(componentId);
		}
		if (component.getStatus() == null) {
			component.setStatus(0);
		}
		TokenUser info = ContextHolders.getTokenUserInfo();
		cn.hutool.core.lang.Assert.notNull(info, "用户未登录");
		if (StrUtil.isBlank(component.getCreateUser())) {
			component.setCreateUser(info.getAccountName());
		}
		if (StrUtil.isBlank(component.getCreateTime())) {
			component.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), com.wg.appframe.core.utils.DateUtil.DEFAULT_FORMAT));
		}
		if (StrUtil.isBlank(component.getTenantId())) {
			component.setTenantId(StrUtil.isBlank(info.getTenantId()) ? "" : info.getTenantId());
		}
		if (component.getType() == null) {
			component.setType(ComponentTypeEnum.WORKFLOW.getCode());
		}
		component.setUpdateTime(com.wg.appframe.core.utils.DateUtil.getCurrentTime(com.wg.appframe.core.utils.DateUtil.DEFAULT_FORMAT));
		component.setUpdateUser(info.getAccountName());
		componentMapper.insertOrUpdate(component);

		component.setApplicationInfo(getByAppId(component.getComponentId()));
		return Result.success(component);
	}
	public Result<WorkFlowDto> copy2Target(Component component, String targetId) {
		String componentId = component.getComponentId();
		cn.hutool.core.lang.Assert.notNull(componentId, "工作流id不能为空");
		// 1. 调用创建新工作流方法
		// ComponentDto componentDto = componentService.selectById(componentId);
		List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(componentId));
		if (CollectionUtil.isEmpty(componentDtoList)) {
			cn.hutool.core.lang.Assert.notEmpty(componentDtoList, "未找到工作流");
		}
		ComponentDto componentDto = componentDtoList.get(0);
		cn.hutool.core.lang.Assert.notNull(componentDto, "未找到工作流");
		Component componentCopy = com.wg.appframe.core.utils.BeanUtil.copy(componentDto, Component.class);
		componentCopy.setId(null);
		componentCopy.setComponentId(targetId);
		componentCopy.setComponentName(componentCopy.getComponentName() + "【复制】");

		// 2. copy节点信息
		// 2.1 建立一份新旧节点id映射
		Map<String, String> map = new HashMap<>();
		List<ComponentNodeDto> nodes = componentDto.getNodes();
		List<ComponentNode> nodesCopy = nodes.stream().map(node -> com.wg.appframe.core.utils.BeanUtil.copy(node, ComponentNode.class)).peek(node -> {
			node.setId(null);
			String newNodeId = IdUtil.simpleUUID();
			map.put(node.getNodeId(), newNodeId);
			node.setNodeId(newNodeId);
			node.setComponentId(componentCopy.getComponentId());
		}).collect(Collectors.toList());
		if (!nodesCopy.isEmpty()) {
			componentNodeMapper.insertBatch(nodesCopy);
		}

		// 3. copy参数信息
		List<MetaParam> metaParams = componentDto.getMetaParams();
		metaParams.forEach(param -> {
			param.setId(null);
			param.setParamId(IdUtil.simpleUUID());
			param.setNodeId(map.get(param.getNodeId()));
			param.setReferenceNodeId(map.get(param.getReferenceNodeId()));
		});
		if (!metaParams.isEmpty()) {
			metaParamMapper.insertBatch(metaParams);
		}
		// 4. copy节点关系
		List<ComponentNodeRel> nodeRel = componentDto.getNodeRel();
		if (CollectionUtil.isNotEmpty(nodeRel)) {
			nodeRel.forEach(rel -> {
				rel.setId(null);
				rel.setComponentId(componentCopy.getComponentId());
				rel.setSourceNodeId(map.get(rel.getSourceNodeId()));
				rel.setTargetNodeId(map.get(rel.getTargetNodeId()));
			});
			nodeRelMapper.insertBatch(nodeRel);
		}
		// * 更新前端画布信息中的节点数据
		String canvas = componentCopy.getCanvas();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			canvas = canvas.replace(entry.getKey(), entry.getValue());
		}
		componentCopy.setCanvas(canvas);
		componentService.save(componentCopy);

		// 5. 调用创建新应用的方法
		ApplicationInfo applicationInfo = componentDto.getApplicationInfo();
		if (null == applicationInfo) {
			applicationInfo = new ApplicationInfo();
		}
		// Assert.notNull(applicationInfo, "未找到工作流应用");
		clearProperties(applicationInfo, componentCopy);
		Result<ApplicationInfo> result = addApplicationInfo(applicationInfo);
		return Result.success(new WorkFlowDto(componentCopy, result.getData()));
	}
	private static void clearProperties(ApplicationInfo applicationInfo, Component componentCopy) {
		applicationInfo.setId(null);
		String applicationId = componentCopy.getComponentId();
		if (applicationId.contains("_")) {
			applicationId = applicationId.substring(0, componentCopy.getComponentId().indexOf("_"));
		}
		applicationInfo.setApplicationId(applicationId);
		applicationInfo.setApplicationName(componentCopy.getComponentName());
		applicationInfo.setApplicationCode(IdUtil.simpleUUID());
		applicationInfo.setClientLink(null);
		applicationInfo.setApi(null);
		applicationInfo.setApiKey(null);
		applicationInfo.setTenantId(null);
		applicationInfo.setPublishDesc(null);
		applicationInfo.setPublishStatus("4");
		applicationInfo.setPublishTime(null);
		applicationInfo.setPublishAppStore(0);
	}

	/**
	 * 绑定mcp服务
	 * @param applicationInfo
	 */
	private void bindingMcpServer(ApplicationInfo applicationInfo) {
		applicationMcpRefService.bindApplicationMcpRef(applicationInfo.getApplicationId(), applicationInfo.getMcpServerIds());
	}
	public void resetCellMaxTextLength() {
		SpreadsheetVersion excel2007 = SpreadsheetVersion.EXCEL2007;
		if (Integer.MAX_VALUE != excel2007.getMaxTextLength()) {
			Field field;
			try {
				field = excel2007.getClass().getDeclaredField("_maxTextLength");
				field.setAccessible(true);
				field.set(excel2007,Integer.MAX_VALUE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新应用配置
	 * @param applicationInfo
	 */
	private void updateAppConfig(ApplicationInfo applicationInfo) {
		List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(applicationInfo.getApplicationId());
		updateConfig(configurationList, applicationInfo.getApplicationId(), CONTENT_INDEX_FLAG, applicationInfo.getRefIndexFlag(), "是否引用索引");
		updateConfig(configurationList, applicationInfo.getApplicationId(), SUBJECT_TALK_LLM_NAME, applicationInfo.getSubjectLlmName(), "识别场景的大模型配置");
		if (!Objects.isNull(applicationInfo.getMaxToken())) {
			updateConfig(configurationList, applicationInfo.getApplicationId(), MAX_TOKENS, String.valueOf(applicationInfo.getMaxToken()), "最大回复数");
		}
		applicationConfigurationService.updateAppConfig(configurationList, applicationInfo.getApplicationId());
	}

	/**
	 * 添加应用配置
	 * @param configurationList 配置列表
	 * @param appId 应用id
	 * @param key 应用key
	 * @param value value值
	 * @param remark 备注
	 */
	public static void updateConfig(List<ApplicationConfiguration> configurationList, String appId, String key, String value, String remark) {
		Optional<ApplicationConfiguration> any = configurationList.stream().filter(p -> key.equals(p.getKeyInfo())).findAny();
		if (!any.isPresent()) {
			ApplicationConfiguration configuration = new ApplicationConfiguration();
			configuration.setApplicationId(appId);
			configuration.setKeyInfo(key);
			configuration.setValueInfo(value);
			configuration.setStatus(0);
			configuration.setRemark(remark);
			configurationList.add(configuration);
		} else {
			ApplicationConfiguration applicationConfiguration = any.get();
			applicationConfiguration.setValueInfo(value);
		}
	}

	/**
	 * 添加应用配置
	 *
	 * @param configurationList
	 * @param applicationInfo
	 */
	private void setConfig(List<ApplicationConfiguration> configurationList, ApplicationInfo applicationInfo) {
		if (CollectionUtil.isEmpty(configurationList)) {
			return;
		}
		configurationList.forEach(item -> {
			switch (item.getKeyInfo()) {
				case CONTENT_INDEX_FLAG:
					applicationInfo.setRefIndexFlag(item.getValueInfo());
					break;
				case SUBJECT_TALK_LLM_NAME:
					applicationInfo.setSubjectLlmName(item.getValueInfo());
					break;
				case MAX_TOKENS:
					applicationInfo.setMaxToken(Integer.valueOf(item.getValueInfo()));
				default:
					break;
			}
		});
	}

	/**
	 * 设置模型信息
	 * @param applicationInfo
	 */
	private void setModelInfo(ApplicationInfo applicationInfo) {
		if (StringUtils.isNotBlank(applicationInfo.getModelId())) {
			LlmInfo modelInfo = llmInfoService.getByModelId(applicationInfo.getModelId());
			modelInfo.setAppKey(StringConstant.BLANK);
			modelInfo.setAppSecret(StringConstant.BLANK);
			applicationInfo.setModelInfo(modelInfo);
		}
	}

	/**
	 * 发布状态
	 * @param applicationInfo
	 */
	private void publishStatus(ApplicationInfo applicationInfo) {
		if("1".equals(applicationInfo.getPublishStatus()) && applicationInfo.getClickPublish()){
			//每次发布都需要记录变更记录
			ApplicationPublishRecord applicationPublishRecord = ApplicationPublishRecord.create();
			applicationPublishRecord.setMessageSource(1);
			applicationPublishRecord.setApplicationId(applicationInfo.getApplicationId());
			applicationPublishRecord.setPublishDesc(StringUtils.isBlank(applicationInfo.getPublishDesc()) ? "首次发布" : applicationInfo.getPublishDesc());
			applicationPublishRecordService.save(applicationPublishRecord);
			log.info("添加 应用 发布记录成功");

			//上架应用商店 更新上架时间
			if (applicationInfo.getPublishAppStore() != null && applicationInfo.getPublishAppStore() == 1) { //0-未发布 1-发布 2-审核中
				applicationInfo.setPublishTime(new Date());
				applicationInfo.setPublishAppStore(2);
			}
			if (applicationInfo.getPublishAppStore() != null && applicationInfo.getPublishAppStore() == 2) { //0-未发布 1-发布 2-审核中
				applicationInfo.setPublishTime(new Date());
				//推送审核记录到审核记录表中
				ServerPublishAudit serverPublishAudit = ServerPublishAudit.create();
				serverPublishAudit.setMessageSource(1);
				serverPublishAudit.setApplicationId(applicationInfo.getApplicationId());
				serverPublishAudit.setApplicationName(applicationInfo.getApplicationName());
				serverPublishAudit.setIntroduce(applicationInfo.getIntroduce());
				serverPublishAudit.setFacadeImageUrl(applicationInfo.getFacadeImageUrl());
				serverPublishAudit.setCreateUserId(applicationInfo.getCreateUser());
				serverPublishAudit.setCreateUserName(applicationInfo.getCreateUser());
				serverPublishAudit.setPublishType(applicationInfo.getPublishType());
				serverPublishAudit.setApplicationType(applicationInfo.getType());
				serverPublishAuditService.add(serverPublishAudit);
				log.info("推送审核记录成功，应用ID:{}", applicationInfo.getApplicationId());
			}
		}
	}

	/**
	 * 构建用户信息
	 * @param applicationInfo
	 * @param tokenUserInfo
	 */
	private void buildUser(ApplicationInfo applicationInfo, TokenUser tokenUserInfo) {
		ApplicationInfo one = Wrappers.of(mapper)
				.where(APPLICATION_INFO.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
				.one();
		if (null != one) {
			if (StringUtils.isBlank(applicationInfo.getCreateUser())) {
				applicationInfo.setCreateUser(tokenUserInfo.getAccountName());
			}
			if (StringUtils.isBlank(applicationInfo.getCreateTime())) {
				applicationInfo.setCreateTime(DateUtil.getCurrentTime());
			}
			applicationInfo.setId(one.getId());
			applicationInfo.setUpdateUser(tokenUserInfo.getAccountName());
			applicationInfo.setUpdateTime(DateUtil.getCurrentTime());
		}
	}

	/**
	 * 创建API密钥
	 * @param applicationInfo
	 */
	private void createApiKey(ApplicationInfo applicationInfo) {
		applicationInfo.setApiKey(SecureUtil.sha1(applicationInfo.getApplicationName()));
		applicationInfo.setApiSecret(SecureUtil.md5(applicationInfo.getApplicationName()));
		try {
			// 添加记录推送一条记录到应用关联私有发布表 预留
			ApplicationApiAuth applicationApiAuth = ApplicationApiAuth.create();
			applicationApiAuth.setApplicationId(applicationInfo.getApplicationId());
			applicationApiAuth.setEnableFlag("1");
			applicationApiAuth.setDeleteFlag(0);
			applicationApiAuth.setApplicationId(applicationInfo.getApplicationId());
			applicationApiAuth.setApiKey(applicationInfo.getApiKey());
			applicationApiAuth.setSecretKey(applicationInfo.getApiSecret());
			applicationAuthService.save(applicationApiAuth);
			log.info("添加应用关联私有发布表记录成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录版本
	 * @param applicationInfo
	 * @param tokenUserInfo
	 */
	private void recordVersion(ApplicationInfo applicationInfo, TokenUser tokenUserInfo) {
		Long currentTimeMillis=System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 使用Date构造函数，传入时间戳
		Date date = new Date(currentTimeMillis);
		String formattedDate = sdf.format(date);
		applicationInfo.setAppVersionNumber("v1.0_" + formattedDate);
		mapper.insertOrUpdate(applicationInfo, false);
		ApplicationInfoVersionIndex applicationInfoVersion = new ApplicationInfoVersionIndex();
		BeanUtil.copyProperties(applicationInfo, applicationInfoVersion);
		applicationInfoVersion.setApplicationInfoId(String.valueOf(applicationInfo.getId()));
		applicationInfoVersion.setAppVersionNumber("v1.0_" + formattedDate);
		applicationInfoVersion.setMakeType(applicationInfo.getMakeType());
		applicationInfoVersion.setCreateTime(DateUtil.getCurrentTime());
		// 知识库和工作流关联关系版本记录
		if (CollectionUtil.isNotEmpty(applicationInfo.getKnowledgeIds())) {
			applicationInfoVersion.setBindingKnowledgeIdsJson(JSONUtil.toJsonStr(applicationInfo.getKnowledgeIds()));
		}
		if (CollectionUtil.isNotEmpty(applicationInfo.getWorkflowIds())) {
			applicationInfoVersion.setBindingWorkflowIdsJson(JSONUtil.toJsonStr(applicationInfo.getWorkflowIds()));
		}
		// 关联预设问题关联关系备份
		if (CollectionUtil.isNotEmpty(applicationInfo.getPresetQuestionList())) {
			applicationInfoVersion.setBindingPresetQuestionJson(JSONUtil.toJsonStr(applicationInfo.getPresetQuestionList()));
		}
		// 关联应用快捷指令关系备份
		if (CollectionUtil.isNotEmpty(applicationInfo.getApplicationQuickCommandList())) {
			applicationInfoVersion.setBindingApplicationQuickCommandJson(JSONUtil.toJsonStr(applicationInfo.getApplicationQuickCommandList()));
		}
		// 关联敏敢词关联关系备份
		if (CollectionUtil.isNotEmpty(applicationInfo.getInterceptWordHouses())) {
			applicationInfoVersion.setBindingInterceptWordhoseJson(JSONUtil.toJsonStr(applicationInfo.getInterceptWordHouses()));
		}
		// 关联个性化关联关系备份
		if (StringUtils.isNotBlank(applicationInfo.getStreamVoice())) {
			applicationInfoVersion.setBindingConfigrationJson(JSONUtil.toJsonStr(applicationInfo.getStreamVoice()));
		}
		// 目前token中未保存租户号
		if (null != tokenUserInfo && null != tokenUserInfo.getId()) {
			OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
			applicationInfoVersion.setCreateTime(formattedDate);
			applicationInfoVersion.setUpdateTime(formattedDate);
			applicationInfoVersion.setCreateUser(oauthUser.getAccountName());
			applicationInfoVersion.setUpdateUser(oauthUser.getAccountName());
			applicationInfoVersion.setTenantId(oauthUser.getTenantId());
			applicationInfoVersion.setUserId(String.valueOf(oauthUser.getId()));
			if (StringUtils.isEmpty(oauthUser.getTenantId())) {
				applicationInfoVersion.setTenantId(tokenUserInfo.getTenantId());
			}
		}
		applicationInfoVersionIndexMapper.insert(applicationInfoVersion);
		if (StringUtils.isNotBlank(applicationInfoVersion.getVersionId())) {
			applicationInfo.setRemark(applicationInfoVersion.getVersionId());
		}
	}

}