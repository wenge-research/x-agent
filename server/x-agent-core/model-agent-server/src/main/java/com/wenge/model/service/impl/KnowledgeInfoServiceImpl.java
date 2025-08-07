package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.KnowledgeConstant;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.*;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.enums.KnowledgeDataTypeEnum;
import com.wenge.model.enums.KnowledgeTypeEnum;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.mapper.ApplicationKnowledgeMapper;
import com.wenge.model.mapper.KnowledgeDataAnalysisMapper;
import com.wenge.model.mapper.KnowledgeInfoMapper;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.*;
import com.wenge.model.utils.DateRangeExample;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.StructureParserVo;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.GrantDataMapper;
import com.wenge.oauth.mapper.UserMapper;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.SAPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.KnowledgeDataAnalysisTableDef.KNOWLEDGE_DATA_ANALYSIS;
import static com.wenge.model.entity.table.KnowledgeDataTypeTableDef.KNOWLEDGE_DATA_TYPE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
import static com.wenge.oauth.entity.table.OauthDeptTableDef.OAUTH_DEPT;
import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;
import static com.wenge.oauth.enums.OwnerTypeEnum.*;

/**
 * Description: 知识库管理服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 14:10:23
 *
 */
@Service
@Slf4j
public class KnowledgeInfoServiceImpl extends ServiceImpl<KnowledgeInfoMapper, KnowledgeInfo> implements KnowledgeInfoService {
	/**
	 * 	知识库管理数据库处理类
	 */
	@Autowired
	private KnowledgeInfoMapper knowledgeInfoMapper;

	@Autowired
	private KnowledgeDataService knowledgeDataService;

	@Autowired
	private FoldersService foldersService;

	@Autowired
	private ApplicationKnowledgeService applicationKnowledgeService;

	@Autowired
	private DenseVectorService denseVectorService;

	@Autowired
	private FileService fileService;

	@Autowired
	private KnowledgeDataTypeService knowledgeDataTypeService;

	@Autowired
	private KnowledgeInfoService knowledgeInfoService;

	@Autowired
	private UrlParserInfoService urlParserInfoService;

	@Autowired
	private DataSourceParserInfoService dataSourceParserInfoService;

	@Autowired
	private ApplicationInfoService applicationInfoService;

	@Autowired
	private ApplicationKnowledgeMapper applicationKnowledgeMapper;


	@Autowired
	private OauthDeptService deptService;
	@Autowired
	private KnowledgeDataMapper dataMapper;


	@Autowired
	private UserMapper userMapper;

	@Autowired
	private KnowledgeDataAnalysisMapper knowledgeDataAnalysisMapper;

	@Autowired
	private GrantDataMapper grantDataMapper;

	@Autowired
	private UserService userService;

	@Override
	public Result addKnowledgeInfo(KnowledgeInfo knowledgeInfo) {
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();

		Wrappers<Object> wrappers = Wrappers.init()
				.and(StringUtils.isNotBlank(tokenOauthUserInfo.getAccountName()), KNOWLEDGE_INFO.CREATE_USER.eq(tokenOauthUserInfo.getAccountName()))
				.and(KNOWLEDGE_INFO.KNOWLEDGE_NAME.eq(knowledgeInfo.getKnowledgeName()))
				.and(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeId()), KNOWLEDGE_INFO.KNOWLEDGE_ID.ne(knowledgeInfo.getKnowledgeId()));

		if (count(wrappers) > 0) {
			return Result.fail("知识库名称已存在");
		}
		if (StringUtils.isBlank(knowledgeInfo.getKnowledgeId())) {
			knowledgeInfo.setKnowledgeId(IdUtil.simpleUUID());
		}
		if (knowledgeInfo.getId() == null) {
			// 新增的时候给默认的文件夹
			CompletableFuture.runAsync(() -> {
				KnowledgeTypeEnum[] values = KnowledgeTypeEnum.values();
				for (KnowledgeTypeEnum value : values) {
					if (value == KnowledgeTypeEnum.Q_A) {
						// QA类型使用的不是Folders
						addAllKnowledgeType(knowledgeInfo.getKnowledgeId());
						continue;
					}
					Folders folders = Folders.creat();
					folders.setKnowledgeId(knowledgeInfo.getKnowledgeId());
					folders.setName("全部");
					folders.setType(value.getCode());
					folders.setParentId("0");
					folders.setFoldersId(IdUtil.simpleUUID());
					foldersService.addFolders(folders);
				}
			});
		}
		// 无向量化模型时候，设置默认模型
		if (StringUtils.isBlank(knowledgeInfo.getDenseVectorId())) {
			DenseVector defaultDenseVector = denseVectorService.getOneDefaultDenseVector();
			knowledgeInfo.setDenseVectorId(null == defaultDenseVector ? StringConstant.BLANK : defaultDenseVector.getVectorId());
		}

		// 管理员添加后是null，预置后是official，普通用户添加后都是personal，群众没有权限进行操作
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
		if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
			knowledgeInfo.setOwnerType(null);
		} else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
			return Result.fail("无权限操作");
		} else {
			knowledgeInfo.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
		}

		saveOrUpdate(knowledgeInfo);
		// 清空知识库向量模型缓存
		clear();
		CacheClearServiceImpl.clearComCache("/cacheClear/clearDenseVector", null);
		return Result.success(knowledgeInfo);
	}

	/**
	 * 清缓存
	 */
	public static void clear() {
		DenseVectorServiceImpl.DENSE_VECTOR_MAP.clear();
	}

    @Override
    public Result<Page<KnowledgeInfo>> getKnowledgeInfoList(KnowledgeInfoPageParam knowledgeInfo) {
        // 参数校验
        if (knowledgeInfo == null) {
            return Result.fail("请求参数为空");
        }
        if (knowledgeInfo.getPageNo() == null || knowledgeInfo.getPageNo() < 1) {
            return Result.fail("页面参数为空或错误");
        }
        if (knowledgeInfo.getPageSize() == null || knowledgeInfo.getPageSize() < 1) {
            return Result.fail("页面大小参数为空或错误");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isNotBlank(knowledgeInfo.getEndTime())) {

                endDate = format.parse(knowledgeInfo.getEndTime());
            }
            if (StringUtils.isNotBlank(knowledgeInfo.getStartTime())) {
                startDate = format.parse(knowledgeInfo.getStartTime());
            }
        } catch (ParseException e) {
            log.info("日期格式异常：startDate: {}, endDate: {}", startDate, endDate);
            e.printStackTrace();
        }

        String accountName = AppContextHolder.getAccountName();
        Wrappers<Object> wrapper = Wrappers.init()
                .leftJoin(APPLICATION_KNOWLEDGE, StringUtils.isNotBlank(knowledgeInfo.getApplicationId())).on(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
                .where(StringUtils.isNotBlank(knowledgeInfo.getApplicationId()), APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(knowledgeInfo.getApplicationId()))
                .and(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeName()), KNOWLEDGE_INFO.KNOWLEDGE_NAME.like(knowledgeInfo.getKnowledgeName()))
                .and(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeId()), KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeInfo.getKnowledgeId()))
                //.and(StringUtils.isNotBlank(knowledgeInfo.getCreateUser()), KNOWLEDGE_INFO.CREATE_USER.eq(accountName))
                .and(StringUtils.isNotBlank(knowledgeInfo.getEndTime()), KNOWLEDGE_INFO.CREATE_TIME.le(endDate))
                .and(StringUtils.isNotBlank(knowledgeInfo.getStartTime()), KNOWLEDGE_INFO.CREATE_TIME.ge(startDate))
				.and(StringUtils.isNotBlank(knowledgeInfo.getStatus()),KNOWLEDGE_INFO.STATUS.eq(knowledgeInfo.getStatus())); // "是"启用 "否"-禁用

        if (StringUtils.isNotBlank(knowledgeInfo.getOrder()) && StringUtils.isNotBlank(knowledgeInfo.getSort())) {
            wrapper.orderBy(knowledgeInfo.getOrder() + " " + knowledgeInfo.getSort());
        }

        String ownerType = knowledgeInfo.getOwnerType();

        // 根据权限查询
        PermissionControlUtils.buildPermission(wrapper, BusinessPermissionEnum.KNOWLEDGE, OwnerTypeEnum.getByCode(ownerType));

        Page<KnowledgeInfo> page = page(Page.of(knowledgeInfo.getPageNo(), knowledgeInfo.getPageSize()), wrapper);
        List<KnowledgeInfo> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            List<DenseVector> denseVectors = denseVectorService.getAllDenseVector();
            Map<String, String> vectorNameMap = denseVectors.stream()
                    .filter(vector -> vector.getVectorId() != null)
                    .filter(vector -> vector.getVectorName() != null)
                    .collect(Collectors.toMap(
                            DenseVector::getVectorId,
                            DenseVector::getVectorName,
                            (k1, k2) -> k1
                    ));
            Map<String, Integer> appCount = applicationKnowledgeService.getAppCount();
            Map<String, Long> map = fileService.getFileCount();
            Map<String, Long> parerMap = dataSourceParserInfoService.getDataSourceParserInfoNum();
            records.forEach(item -> {
                item.setDenseVectorName(vectorNameMap.getOrDefault(item.getDenseVectorId(), StringConstant.BLANK));
                item.setAssociatedApp(appCount.getOrDefault(item.getKnowledgeId(), 0));
                if (KnowledgeTypeEnum.DOCUMENT.getCode().equals(item.getKnowledgeType())) {
                    // 文档知识库需要显示上传的文件数量
                    Long fileNum = map.getOrDefault(item.getKnowledgeId(), 0L);
                    item.setFileNum(fileNum.intValue());
                }
                Long parserInfoNum = parerMap.getOrDefault(item.getKnowledgeId(), 0L);
                item.setDataSourceParserInfoNum(parserInfoNum.intValue());
            });

            // 在授权相关场景下，经过权限查询后，如果不是当前用户的知识库，就是授权的
			String powerType = tokenUserInfo.getPowerType();
			if (null != ownerType && ownerType.toLowerCase().contains(GRANT.getCode())&& !powerType.equals(PowerTypeEnum.SYSTEM_ADMIN.getCode())) {
                records.forEach(record -> {
                    Boolean isMe = accountName.equals(record.getCreateUser());
                    record.setIsMe(isMe);
                    record.setGranted(!isMe);
                });
            }
        }
        return Result.success(page);
    }

	@Override
	public Result updateKnowledgeInfo(KnowledgeInfo knowledgeInfo){
		updateById(knowledgeInfo);
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result deleteKnowledgeInfo(List<String> idList) {
		if (CollectionUtil.isEmpty(idList)) {
			return Result.fail("请选择要删除的知识库");
		}

		// 验证是否有应用关联

		List<String> appNameByKnowledgeId = applicationKnowledgeService.getAppNameByKnowledgeId(idList.get(0));
		if (appNameByKnowledgeId.size() > 0) {
			String appName = String.join(",", appNameByKnowledgeId);
			return Result.fail("知识库已关联 [{appName}]，不可删除".replace("{appName}", appName));
		}

		// 删除各个文档
		Wrappers wrappers = Wrappers.init()
				.where(FOLDERS.KNOWLEDGE_ID.in(idList));
		List<Folders> foldersList = foldersService.list(wrappers);
		if (CollectionUtil.isNotEmpty(foldersList)) {
			FoldersDeleteParam param = new FoldersDeleteParam();
			List<String> foldersIdList = foldersList.stream().map(Folders::getFoldersId).distinct().collect(Collectors.toList());
			param.setFoldersIdList(foldersIdList);
			foldersService.deleteFolders(param);
		}

		// 删除知识库数据
		DeleteKnowledgeDataParam knownledgeDataParam = new DeleteKnowledgeDataParam();
		knownledgeDataParam.setKnowledgeId(idList);
		knowledgeDataService.deleteKnowledgeData(knownledgeDataParam);

		// 最后再删除知识库
		wrappers = Wrappers.init()
				.where(KNOWLEDGE_INFO.KNOWLEDGE_ID.in(idList));
		mapper.deleteByQuery(wrappers);
		return Result.success();
	}

	@Override
	public Result updateStatus(StatusUpdateParam param) {
		boolean update = UpdateChain.of(KnowledgeInfo.class)
				.set(KNOWLEDGE_INFO.STATUS, param.getStatus())
				.where(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(param.getKnowledgeId()))
				.update();
		return Result.success();
	}

	@Override
	public List<KnowledgeInfo> effectiveKnowledge(String applicationId) {
		String period = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
		Wrappers<Object> wrappers = Wrappers.init()
				.select(KNOWLEDGE_INFO.KNOWLEDGE_ID, KNOWLEDGE_INFO.DENSE_VECTOR_ID)
				.innerJoin(APPLICATION_KNOWLEDGE).on(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
				.where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationId))
				.and(and -> {
					and.or(KNOWLEDGE_INFO.PERIOD_START.le(period));
					and.or(KNOWLEDGE_INFO.PERIOD_START.isNull());
				})
				.and(and -> {
					and.or(KNOWLEDGE_INFO.PERIOD_END.ge(period));
					and.or(KNOWLEDGE_INFO.PERIOD_END.isNull());
				})
				.and(KNOWLEDGE_INFO.STATUS.eq(YesNoEnum.YES.getName()));
		return knowledgeInfoMapper.selectListByQuery(wrappers);
	}

	@Override
	public KnowledgeInfo getByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

        return Wrappers.of(mapper)
                .where(KNOWLEDGE_INFO.KNOWLEDGE_NAME.eq(name))
                .limit(1)
                .one();
	}

	@Override
	public KnowledgeInfo getById(String knowledgeId) {
		if (StringUtils.isBlank(knowledgeId)) {
			return null;
		}
		return Wrappers.of(mapper)
				.where(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeId))
				.limit(1)
				.one();
	}

	@Override
	public List<KnowledgeInfo> getAllKnowledgeInfo() {
		return list();
	}

	@Override
	public List<KnowledgeInfo> getByIds(List<String> knowledgeIds) {
		if (CollectionUtil.isEmpty(knowledgeIds)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.where(KNOWLEDGE_INFO.KNOWLEDGE_ID.in(knowledgeIds))
				.list();
	}

	@Override
	public Result knowledgeCount(KnowledgeDataParam knowledgeDataParam) {

		JSONObject jsonObject = new JSONObject();

		//知识库数量
		KnowledgeInfoPageParam knowledgeInfoPageParam =  new KnowledgeInfoPageParam();
		knowledgeInfoPageParam.setPageNo(1);
		knowledgeInfoPageParam.setPageSize(1);
		knowledgeInfoPageParam.setKnowledgeId(knowledgeDataParam.getKnowledgeId());
		knowledgeInfoPageParam.setKnowledgeName(knowledgeDataParam.getKnowledgeName());
		knowledgeInfoPageParam.setStartTime(knowledgeDataParam.getStartTime());
		knowledgeInfoPageParam.setEndTime(knowledgeDataParam.getEndTime());

		final long zskCount = knowledgeInfoService.getKnowledgeInfoList(knowledgeInfoPageParam).getData().getTotalRow();
		knowledgeInfoPageParam.setPageSize(Integer.valueOf(zskCount + "") == 0 ? 1 : Integer.valueOf(zskCount + ""));
		List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.getKnowledgeInfoList(knowledgeInfoPageParam).getData().getRecords();
		// 使用Stream API将List<User>转换为List<String>
		List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).collect(Collectors.toList());
		if (knowledgeInfoList == null || knowledgeIdList.size() < 1) {
			knowledgeIdList = new ArrayList<>();
			knowledgeIdList.add("zhishikuid");
		}
		//知识库文档数
		FilePageParam filePageParam = new FilePageParam();
		filePageParam.setFileTypes(Arrays.asList(0));
		filePageParam.setKnowledgeIds(knowledgeIdList);
		filePageParam.setPageNo(1);
		filePageParam.setPageSize(1);
		//文件夹类型 2文档数据 3url 5结构化 6多媒体数据
		final long wjCount = fileService.getFileList(filePageParam).getData().getTotalRow();

		//知识库QA数
		KnowledgeDataPageParam knowledgeDataPageParam = new KnowledgeDataPageParam();
		knowledgeDataPageParam.setPageNo(1);knowledgeDataPageParam.setPageSize(1);
		knowledgeDataPageParam.setKnowledgeIds(knowledgeIdList);
		final long wddCount = knowledgeDataService.getKnowledgeDataList(knowledgeDataPageParam).getData().getTotal();

		//知识库url数
		ParserInfoPageParam parserInfoPageParam = new ParserInfoPageParam();
		parserInfoPageParam.setNeedParam(true);
		parserInfoPageParam.setPageNo(1);
		parserInfoPageParam.setPageSize(1);
		parserInfoPageParam.setKnowledgeIds(knowledgeIdList);
		final long urlCount = urlParserInfoService.getList(parserInfoPageParam).getData().getTotalRow();

		//知识库结构化数
		UnionParam unionParam = new UnionParam();
		unionParam.setPageNo(1);
		unionParam.setPageSize(1);
		unionParam.setKnowledgeIds(knowledgeIdList);
		final long jghCount = dataSourceParserInfoService.unionData(unionParam).getData().getTotal();
		long allCount = wjCount + wddCount + urlCount + jghCount;
		jsonObject.put("allCount", allCount);
		jsonObject.put("wjCount", wjCount);
		jsonObject.put("wddCount", wddCount);
		jsonObject.put("urlCount", urlCount);
		jsonObject.put("jghCount", jghCount);
		jsonObject.put("zskCount", zskCount);
		return Result.success(jsonObject);
	}

	@Override
	public Result applicationCount(KnowledgeDataParam knowledgeDataParam) {
		JSONObject jsonObject = new JSONObject();
		ApplicationInfoPageParam applicationInfoPage =  new ApplicationInfoPageParam();
		applicationInfoPage.setPageNo(1);applicationInfoPage.setPageSize(1);
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (StringUtils.isNotBlank(knowledgeDataParam.getEndTime())) {

				endDate = format.parse(knowledgeDataParam.getEndTime());
			}
			if (StringUtils.isNotBlank(knowledgeDataParam.getStartTime())) {
				startDate = format.parse(knowledgeDataParam.getStartTime());
			}
		} catch (ParseException e) {
			log.info("日期格式异常：startDate: {}, endDate: {}", startDate, endDate);
			e.printStackTrace();
		}
		// 构建 QueryWrapper 对象
		Wrappers<Object> queryWrapper = Wrappers.init()
				.leftJoin(KNOWLEDGE_INFO).on(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
				.where(StringUtils.isNotBlank(knowledgeDataParam.getKnowledgeName()), KNOWLEDGE_INFO.KNOWLEDGE_NAME.like(knowledgeDataParam.getKnowledgeName()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getKnowledgeId()), KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeDataParam.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getEndTime()), KNOWLEDGE_INFO.CREATE_TIME.le(endDate))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getStartTime()), KNOWLEDGE_INFO.CREATE_TIME.ge(startDate));

		List<ApplicationKnowledge> applicationKnowledgeList =
				applicationKnowledgeMapper.selectListByQuery(queryWrapper);
		List<String> applicationIds = applicationKnowledgeList.stream().map(ApplicationKnowledge::getApplicationId).distinct().collect(Collectors.toList());
		applicationInfoPage.setApplicationIdLists(applicationIds);
		applicationInfoPage.setNeedParam(true);
		applicationInfoPage.setKnowledgeId(knowledgeDataParam.getKnowledgeId());
		// AI客服 qa LLM
		applicationInfoPage.setTypeLists(com.google.common.collect.Lists.newArrayList("qa"));
		final long llmCount = applicationInfoService.getApplicationCount(applicationInfoPage);
		// 工作流 workflow
		applicationInfoPage.setTypeLists(com.google.common.collect.Lists.newArrayList("workflow"));
		final long workflowCount = applicationInfoService.getApplicationCount(applicationInfoPage);

		// 对话流 dialogue
		applicationInfoPage.setTypeLists(com.google.common.collect.Lists.newArrayList("dialogue"));
		final long dialogueCount = applicationInfoService.getApplicationCount(applicationInfoPage);

		// 文本生成 text-agent
		applicationInfoPage.setTypeLists(com.google.common.collect.Lists.newArrayList("text-agent"));
		final long textAgentCount = applicationInfoService.getApplicationCount(applicationInfoPage);


		// 关联应用总个数
		final long totalCount = llmCount + dialogueCount + workflowCount + textAgentCount;

		jsonObject.put("llmCount", llmCount);
		jsonObject.put("workflowCount", workflowCount);
		jsonObject.put("dialogueCount", dialogueCount);
		jsonObject.put("textAgentCount", textAgentCount);
		jsonObject.put("totalCount", totalCount);
		return Result.success(jsonObject);
	}

	@Override
	public Result knowledgeCountTrend(KnowledgeDataParam knowledgeDataParam) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		if (StringUtils.isBlank(knowledgeDataParam.getEndTime())) {
			knowledgeDataParam.setEndTime(dateFormat.format(now));
		}else {
			knowledgeDataParam.setEndTime(knowledgeDataParam.getEndTime().split(" ")[0]);
		}
		if (StringUtils.isBlank(knowledgeDataParam.getStartTime())) {
			// 创建一个Calendar实例并设置为当前时间
			Calendar calendar = Calendar.getInstance();
			// 往前推10天
			calendar.add(Calendar.DAY_OF_MONTH, -10);
			// 获取修改后的时间
			Date date = calendar.getTime();
			knowledgeDataParam.setStartTime(dateFormat.format(date));
		}else {
			knowledgeDataParam.setStartTime(knowledgeDataParam.getStartTime().split(" ")[0]);
		}
		Wrappers<Object> wrappers = Wrappers.init()
				.where(StringUtils.isNotBlank(knowledgeDataParam.getKnowledgeId()), KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeDataParam.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getKnowledgeName()), KNOWLEDGE_INFO.KNOWLEDGE_NAME.like(knowledgeDataParam.getKnowledgeName()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getEndTime()), KNOWLEDGE_INFO.CREATE_TIME.le(knowledgeDataParam.getEndTime()));
		//知识库数量
		List<KnowledgeInfo> knowledgeInfos = knowledgeInfoMapper.selectListByQuery(wrappers);
		Map<LocalDate, Long> dailyCounts = knowledgeInfos.stream()
				.map(date -> cn.hutool.core.date.DateUtil.parseDateTime(date.getCreateTime())
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
				.collect(Collectors.groupingBy(
						date -> date,
						Collectors.counting()
				));
		// 按日期排序
		List<LocalDate> sortedDates = dailyCounts.keySet().stream()
				.sorted()
				.collect(Collectors.toList());


		int cumulativeTotal = 0;
		Map<String, Integer> cumulativeCounts = new LinkedHashMap<>();
		for (LocalDate date : sortedDates) {
			cumulativeTotal += dailyCounts.get(date).intValue();

			cumulativeCounts.put(date.toString(), cumulativeTotal);
		}
		List<JSONObject> list = new ArrayList<>();
		//获取两个时间之间有多少个日期 列表
		List<String> dates = DateRangeExample.getDatesBetween(knowledgeDataParam.getStartTime(),
				knowledgeDataParam.getEndTime());
		for (String date : dates){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("date", date);

			jsonObject.put("totalCount", cumulativeCounts.getOrDefault(date, cumulativeTotal));
			list.add(jsonObject);
		}
		return Result.success(list);
	}

	@Override
	public List<KnowledgeInfo> getKnowledgeList(KnowledgeInfoPageParam knowledgeInfo) {
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (StringUtils.isNotBlank(knowledgeInfo.getEndTime())) {
				endDate = format.parse(knowledgeInfo.getEndTime());
			}
			if (StringUtils.isNotBlank(knowledgeInfo.getStartTime())) {
				startDate = format.parse(knowledgeInfo.getStartTime());
			}
		} catch (ParseException e) {
			log.info("日期格式异常：startDate: {}, endDate: {}", startDate, endDate);
			e.printStackTrace();
		}

		String accountName = AppContextHolder.getAccountName();
		Wrappers<Object> wrapper = Wrappers.init()
				.leftJoin(APPLICATION_KNOWLEDGE, StringUtils.isNotBlank(knowledgeInfo.getApplicationId())).on(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
				.where(StringUtils.isNotBlank(knowledgeInfo.getApplicationId()), APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(knowledgeInfo.getApplicationId()))
				.and(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeName()), KNOWLEDGE_INFO.KNOWLEDGE_NAME.like(knowledgeInfo.getKnowledgeName()))
				.and(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeId()), KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeInfo.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeInfo.getCreateUser()), KNOWLEDGE_INFO.CREATE_USER.eq(accountName))
				.and(StringUtils.isNotBlank(knowledgeInfo.getEndTime()), KNOWLEDGE_INFO.CREATE_TIME.le(endDate))
				.and(StringUtils.isNotBlank(knowledgeInfo.getStartTime()), KNOWLEDGE_INFO.CREATE_TIME.ge(startDate))
				;
		if (StringUtils.isNotBlank(knowledgeInfo.getOrder()) && StringUtils.isNotBlank(knowledgeInfo.getSort())) {
			wrapper.orderBy(knowledgeInfo.getOrder() + " " + knowledgeInfo.getSort());
		}else {
			wrapper.orderBy(KNOWLEDGE_INFO.CREATE_TIME.desc()); // 默认按更新时间排序
		}

		// 根据权限查询
		PermissionControlUtils.buildNoConditionPermission(wrapper, BusinessPermissionEnum.KNOWLEDGE);
		List<KnowledgeInfo> records =this.list(wrapper);

		if (CollectionUtil.isNotEmpty(records)) {
			List<DenseVector> denseVectors = denseVectorService.getAllDenseVector();
			Map<String, String> vectorNameMap = denseVectors.stream().collect(Collectors.toMap(
					DenseVector::getVectorId,
					DenseVector::getVectorName,
					(k1, k2) -> k1
			));
			Map<String, Integer> appCount = applicationKnowledgeService.getAppCount();
			Map<String, Long> map = fileService.getFileCount();
			Map<String, Long> parerMap = dataSourceParserInfoService.getDataSourceParserInfoNum();
			records.forEach(item -> {
				item.setDenseVectorName(vectorNameMap.getOrDefault(item.getDenseVectorId(), StringConstant.BLANK));
				item.setAssociatedApp(appCount.getOrDefault(item.getKnowledgeId(), 0));
				if (KnowledgeTypeEnum.DOCUMENT.getCode().equals(item.getKnowledgeType())) {
					// 文档知识库需要显示上传的文件数量
					Long fileNum = map.getOrDefault(item.getKnowledgeId(), 0L);
					item.setFileNum(fileNum.intValue());
				}
				Long parserInfoNum = parerMap.getOrDefault(item.getKnowledgeId(), 0L);
				item.setDataSourceParserInfoNum(parserInfoNum.intValue());
			});
		}
		return records;
	}

	/**
	 * 1. 先根据查询条件找知识库
	 * 2. 然后根据知识库对应的文档、url、qa对、结构化数据、多媒体来查找用户
	 * 3. 根据用户查询关联的部门数据
	 * 4. 统计不同部门数据结果集合
	 *
	 * @param knowledgeDataParam
	 * @return
	 */
	@Override
	public Result<DeptKnowleageResult> getDeptKnowledgeData(KnowledgeInfoPageParam knowledgeDataParam) {

		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (StringUtils.isNotBlank(knowledgeDataParam.getEndTime())) {

				endDate = format.parse(knowledgeDataParam.getEndTime());
			}
			if (StringUtils.isNotBlank(knowledgeDataParam.getStartTime())) {
				startDate = format.parse(knowledgeDataParam.getStartTime());
			}
		} catch (ParseException e) {
			log.info("日期格式异常：startDate: {}, endDate: {}", startDate, endDate);
			e.printStackTrace();
		}

		List<OauthDept> allDept = deptService.getAllDept();

		List<OauthDept> firstDept = allDept.stream()
				.filter(dept -> "0".equals(dept.getPid())) // 过滤条件
				.collect(Collectors.toList());

		DeptKnowleageResult deptKnowleageResult = new DeptKnowleageResult();
		deptKnowleageResult.setDeptCount(firstDept.size());
		deptKnowleageResult.setChildrenCount(allDept.size() - firstDept.size());

		// 构建查询条件
		Wrappers<Object> queryWrapper = Wrappers.init()
				.select(KNOWLEDGE_DATA_ANALYSIS.DEPT_ID.as("deptId"))
				.select(KNOWLEDGE_DATA_ANALYSIS.DATA_TYPE.as("dataType"))
				.select("COUNT(*) AS count")
				.where((StringUtils.isNotEmpty(knowledgeDataParam.getKnowledgeId())), KNOWLEDGE_DATA_ANALYSIS.KNOWLEDGE_ID.eq(knowledgeDataParam.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getEndTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.le(endDate))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getStartTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.ge(startDate))
				.and(KNOWLEDGE_DATA_ANALYSIS.DEPT_ID.isNotNull())
				.and(KNOWLEDGE_DATA_ANALYSIS.DATA_TYPE.isNotNull())
				.groupBy(KNOWLEDGE_DATA_ANALYSIS.DEPT_ID)
				.groupBy(KNOWLEDGE_DATA_ANALYSIS.DATA_TYPE)
				.orderBys(KNOWLEDGE_DATA_ANALYSIS.DEPT_ID.desc());


		List<DeptCountDto> deptCountDtoList = knowledgeDataAnalysisMapper.selectListByQueryAs(queryWrapper, DeptCountDto.class);
		List<DeptKnowleageDto> deptDataList = new ArrayList<>();
		// 外层 Map 的键：部门 ID
		// 内层 Map 的键：数据类型
		Map<String, Map<String, Integer>> deptListMap = deptCountDtoList.stream()
				.collect(Collectors.groupingBy(
						DeptCountDto::getDeptId,
						Collectors.toMap(
								DeptCountDto::getDataType,
								DeptCountDto::getCount
						)
				));
		for (OauthDept oauthDept : firstDept) {
			DeptKnowleageDto deptKnowleageDto = new DeptKnowleageDto();
			deptKnowleageDto.setDeptId(oauthDept.getDeptId());
			deptKnowleageDto.setDeptCode(oauthDept.getDeptCode());
			deptKnowleageDto.setDeptName(oauthDept.getDeptName());

			Map<String, Integer> deptMap = deptListMap.get(oauthDept.getDeptId());
			if (CollectionUtil.isEmpty(deptMap)) {
				deptMap = new HashMap<>();
			}
			Integer firstWjCount = deptMap.getOrDefault(KnowledgeDataTypeEnum.wj.getCode(), 0);
			Integer firstJghCount = deptMap.getOrDefault(KnowledgeDataTypeEnum.jgh.getCode(), 0);
			Integer firstWddCount = deptMap.getOrDefault(KnowledgeDataTypeEnum.wdd.getCode(), 0);
			Integer firstUrlCount = deptMap.getOrDefault(KnowledgeDataTypeEnum.url.getCode(), 0);
			Integer firstAllCount = firstWjCount + firstWddCount + firstJghCount + firstUrlCount;

			List<OauthDept> children = allDept.stream()
					.filter(dept -> dept.getPid().equals(oauthDept.getDeptId())) // 过滤条件
					.collect(Collectors.toList()); // 收集结果
			List<DeptKnowleageDto> child = new ArrayList<>();
			for (OauthDept childOauthDept : children) {
				Map<String, Integer> childDeptMap = deptListMap.get(childOauthDept.getDeptId());
				if (CollectionUtil.isEmpty(childDeptMap)) {
					childDeptMap = new HashMap<>();
				}
				Integer childWjCount = childDeptMap.getOrDefault(KnowledgeDataTypeEnum.wj.getCode(), 0);
				Integer childJghCount = childDeptMap.getOrDefault(KnowledgeDataTypeEnum.jgh.getCode(), 0);
				Integer childWddCount = childDeptMap.getOrDefault(KnowledgeDataTypeEnum.wdd.getCode(), 0);
				Integer childUrlCount = childDeptMap.getOrDefault(KnowledgeDataTypeEnum.url.getCode(), 0);
				Integer childAllCount = childWjCount + childJghCount + childWddCount + childUrlCount;

				firstAllCount = firstAllCount + childAllCount;
				firstWjCount = firstWjCount + childWjCount;
				firstJghCount = firstJghCount + childJghCount;
				firstWddCount = firstWddCount + childWddCount;
				firstUrlCount = firstUrlCount + childUrlCount;

				DeptKnowleageDto childDeptKnowleageDto = new DeptKnowleageDto();
				childDeptKnowleageDto.setDeptId(childOauthDept.getDeptId());
				childDeptKnowleageDto.setDeptCode(childOauthDept.getDeptCode());
				childDeptKnowleageDto.setDeptName(childOauthDept.getDeptName());
				childDeptKnowleageDto.setWjCount(childWjCount);
				childDeptKnowleageDto.setAllCount(childAllCount);
				childDeptKnowleageDto.setJghCount(childJghCount);
				childDeptKnowleageDto.setUrlCount(childUrlCount);
				childDeptKnowleageDto.setWddCount(childWddCount);

				child.add(childDeptKnowleageDto);
			}
			Collections.sort(child, Comparator.comparing(DeptKnowleageDto::getAllCount).reversed());

			deptKnowleageDto.setWjCount(firstWjCount);
			deptKnowleageDto.setAllCount(firstAllCount);
			deptKnowleageDto.setJghCount(firstJghCount);
			deptKnowleageDto.setUrlCount(firstUrlCount);
			deptKnowleageDto.setWddCount(firstWddCount);
			deptKnowleageDto.setChildren(child);
			deptDataList.add(deptKnowleageDto);
		}
		Collections.sort(deptDataList, Comparator.comparing(DeptKnowleageDto::getAllCount).reversed());
		deptKnowleageResult.setDeptDataList(deptDataList);

		return Result.success(deptKnowleageResult);
	}


	/**
	 * 知识库文档数
	 * @param knowledgeIdList
	 * @return
	 */
	public Map<String, Integer> queryWj(List<String> knowledgeIdList) {
		//知识库文档数
		FilePageParam filePageParam = new FilePageParam();
		filePageParam.setFileTypes(Collections.singletonList(0));
		filePageParam.setKnowledgeIds(knowledgeIdList);
		filePageParam.setPageNo(1);
		filePageParam.setPageSize(1);
		// 文件夹类型 2文档数据 3 url 5结构化 6多媒体数据
		final long totalSize = fileService.getFileList(filePageParam).getData().getTotalRow();
		filePageParam.setPageNo(1);
		filePageParam.setPageSize(totalSize > 0 ? Math.toIntExact(totalSize) : 1);
		List<String> userAccount = fileService.getFileList(filePageParam).getData()
				.getRecords().stream().map(File::getUpdateUser).collect(Collectors.toList());
		return queryDeptCount(userAccount);
	}

	/**
	 * 知识库QA数
	 * @param knowledgeIdList
	 * @return
	 */
	public Map<String, Integer> queryWdd(List<String> knowledgeIdList) {
		//知识库QA数
		KnowledgeDataPageParam knowledgeDataPageParam = new KnowledgeDataPageParam();
		knowledgeDataPageParam.setPageNo(1);
		knowledgeDataPageParam.setPageSize(1);
		knowledgeDataPageParam.setKnowledgeIds(knowledgeIdList);
		knowledgeDataPageParam.setNeedParam(true);
		long totalSize = knowledgeDataService
				.getKnowledgeDataList(knowledgeDataPageParam)
				.getData().getTotal();
		knowledgeDataPageParam.setPageSize(totalSize > 0 ? Math.toIntExact(totalSize) : 1);
		List<String> userAccount = new ArrayList<>();
		Long countTimes =  totalSize/10000 + 1;
		for (int i = 1; i <= countTimes; i++) {
			knowledgeDataPageParam.setPageNo(i);
			knowledgeDataPageParam.setPageSize(10000);
			List<String>  userAccountTemp = knowledgeDataService
					.getKnowledgeDataList(knowledgeDataPageParam)
					.getData().getList()
					.stream().map(KnowledgeDataResult::getUpdateUser).collect(Collectors.toList());
			userAccount.addAll(userAccountTemp);
		}

		return queryDeptCount(userAccount);
	}

	/**
	 * 知识库结构化数
	 * @param knowledgeIdList
	 * @return
	 */
	public Map<String, Integer> queryJgh(List<String> knowledgeIdList) {

		UnionParam unionParam = new UnionParam();
		unionParam.setPageNo(1);
		unionParam.setPageSize(1);

		long totalSize = dataSourceParserInfoService.unionData(unionParam)
				.getData().getTotal();

		unionParam.setKnowledgeIds(knowledgeIdList);
		unionParam.setPageSize(totalSize > 0 ? Math.toIntExact(totalSize) : 1);
		List<String> userAccount = dataSourceParserInfoService.unionData(unionParam)
				.getData().getRecords().stream().map(StructureParserVo::getUpdateUser)
				.collect(Collectors.toList());

		return queryDeptCount(userAccount);
	}

	/**
	 * 知识库url数
	 * @param knowledgeIdList
	 * @return
	 */
	public Map<String, Integer> queryUrl(List<String> knowledgeIdList) {

		ParserInfoPageParam parserInfoPageParam = new ParserInfoPageParam();
		parserInfoPageParam.setPageNo(1);
		parserInfoPageParam.setPageSize(1);
		parserInfoPageParam.setKnowledgeIds(knowledgeIdList);
		parserInfoPageParam.setNeedParam(true);
		long totalSize = urlParserInfoService.getList(parserInfoPageParam)
				.getData().getTotalRow();
		parserInfoPageParam.setPageSize(totalSize >0 ? Math.toIntExact(totalSize) : 1);
		List<String> userAccount = urlParserInfoService.getList(parserInfoPageParam)
				.getData().getRecords().stream().map(UrlParserInfo::getUpdateUser)
				.collect(Collectors.toList());

		return queryDeptCount(userAccount);
	}



	/**
	 * 根据用户账号查询部门数量
	 * @param userAccount
	 * @return
	 */
	private Map<String, Integer> queryDeptCount(List<String> userAccount) {

		Wrappers<Object> queryWrapper = Wrappers.init()
				.select("`oauth_user`.`dept_id` AS deptId")
				.select("COUNT(*) AS count")
				.leftJoin(OAUTH_DEPT).on(OAUTH_USER.DEPT_ID.eq(OAUTH_USER.DEPT_ID))
				.where((CollectionUtils.isNotEmpty(userAccount)), OAUTH_USER.ACCOUNT_NAME.in(userAccount))
				.groupBy(OAUTH_DEPT.DEPT_ID)
				.orderBys(OAUTH_DEPT.DEPT_ID.desc());
		List<DeptCountDto> deptCountDtoList = userMapper.selectListByQueryAs(queryWrapper, DeptCountDto.class);
		return deptCountDtoList.stream().collect(Collectors.toMap(
				DeptCountDto::getDeptId,
				DeptCountDto::getCount,
				(k1, k2) -> k1
		));
	}

	private Integer getInteger(Integer count) {
		return ObjectUtils.isEmpty(count) ? 0 : count;
	}


	/**
	 * 汇总知识库数据
	 */
	@Override
	public void summaryData() {
		List<KnowledgeInfo> allKnowledgeInfo = knowledgeInfoService.getAllKnowledgeInfo();
		Map<String, KnowledgeInfo> knowledgeInfoMap = allKnowledgeInfo.stream()
				.filter(knowledgeInfo -> knowledgeInfo.getKnowledgeId() != null && !knowledgeInfo.getKnowledgeId().isEmpty())
				.collect(Collectors.toMap(KnowledgeInfo::getKnowledgeId, knowledgeInfo -> knowledgeInfo));
		Wrappers<Object> queryWrapper = Wrappers.init()
				.where(OAUTH_USER.DEPT_ID.isNotNull());
		List<OauthUser> oauthUsers = userMapper.selectListByQuery(queryWrapper);
		Map<String, OauthUser> oauthUserMap = oauthUsers.stream()
				.filter(oauthUser -> oauthUser.getAccountName() != null && !oauthUser.getAccountName().isEmpty())
				.collect(Collectors.toMap(OauthUser::getAccountName, oauthUser -> oauthUser));

		StopWatch stopWatch = new StopWatch();
		// 清空表格数据
		clearTable();
		stopWatch.start("开始同步知识库文件数据");
		summaryWj(knowledgeInfoMap, oauthUserMap);
		stopWatch.stop();
		log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());

		stopWatch.start("开始同步知识库结构化数据");
		summaryJgh(knowledgeInfoMap, oauthUserMap);
		stopWatch.stop();
		log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());

		stopWatch.start("开始同步知识库url数据");
		summaryUrl(knowledgeInfoMap, oauthUserMap);
		stopWatch.stop();
		log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());

		stopWatch.start("开始同步知识库多媒体数据");
		summaryMultimedia(knowledgeInfoMap, oauthUserMap);
		stopWatch.stop();
		log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());

		stopWatch.start("开始同步知识库问答对数据");
		summaryWdd(knowledgeInfoMap, oauthUserMap);
		stopWatch.stop();
		log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());
	}

	public void clearTable() {
		// 清除表格统计数据
		knowledgeDataAnalysisMapper.truncate();
	}


	/**
	 * 同步插入文件数据(文件：排除多媒体类型)
	 */
	private void summaryWj(Map<String, KnowledgeInfo> knowledgeInfoMap,
						   Map<String, OauthUser> oauthUserMap) {
		FilePageParam file = new FilePageParam();
		file.setFileTypes(Arrays.stream(new Integer[]{0})
				.collect(Collectors.toList()));
		file.setDataType(KnowledgeDataTypeEnum.wj.getCode());
		handlerFile(knowledgeInfoMap, oauthUserMap, file);
	}

	/**
	 * 同步插入多媒体数据(文件：只包含多媒体类型)
	 */
	private void summaryMultimedia(Map<String, KnowledgeInfo> knowledgeInfoMap,
						   Map<String, OauthUser> oauthUserMap) {
		 // 每页大小
		FilePageParam file = new FilePageParam();
		file.setFileTypes(Arrays.stream(new Integer[]{1, 2, 3})
				.collect(Collectors.toList()));
		file.setDataType(KnowledgeDataTypeEnum.dmt.getCode());
		handlerFile(knowledgeInfoMap, oauthUserMap, file);
	}

	/**
	 * 同步插入文件数据
	 * @param knowledgeInfoMap
	 * @param oauthUserMap
	 * @param file
	 */
	private void handlerFile(Map<String, KnowledgeInfo> knowledgeInfoMap,
							 Map<String, OauthUser> oauthUserMap,
							 FilePageParam file) {
		int PAGE_NO = 1;
		int PAGE_SIZE = 1000;
		file.setPageSize(PAGE_SIZE);
		while (true) {
			try {
				file.setPageNo(PAGE_NO);
				Page<File> fileList = fileService.getFileList(file).getData();
				List<KnowledgeDataAnalysis> dataAnalyses = new ArrayList<>();
				for (File record : fileList.getRecords()) {
					String userName = StringUtils.isNotBlank(record.getCreateUser()) ? record.getCreateUser() : record.getUpdateUser();
					if(StringUtils.isEmpty(userName)) {
						continue;
					}
					KnowledgeDataAnalysis knowledgeDataAnalysis = new KnowledgeDataAnalysis();
					knowledgeDataAnalysis.setKnowledgeId(record.getKnowledgeId());

					List<String> foldersIds = new ArrayList<>();
					foldersIds.add(record.getFoldersId());
					List<Folders> knowledgeIds = foldersService.getKnowledgeIdsByFoldersId(foldersIds);
					if (CollectionUtil.isEmpty(knowledgeIds) || StringUtils.isEmpty(knowledgeIds.get(0).getKnowledgeId())) {
						continue;
					}
					KnowledgeInfo knowledgeInfo = knowledgeInfoMap.get(knowledgeIds.get(0).getKnowledgeId());
					if (ObjectUtils.isEmpty(knowledgeInfo)) {
						continue;
					}
					knowledgeDataAnalysis.setKnowledgeId(knowledgeInfo.getKnowledgeId());
					knowledgeDataAnalysis.setKnowledgeName(knowledgeInfo.getKnowledgeName());
					// 定义日期时间格式
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					// 解析为 LocalDateTime 对象
					LocalDateTime localDateTime = LocalDateTime.parse(record.getCreateTime(), formatter);

					// 提取日期部分并格式化为字符串
					String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					knowledgeDataAnalysis.setCreateDate(date);
					knowledgeDataAnalysis.setDataType(file.getDataType());
					knowledgeDataAnalysis.setCreateUser(record.getCreateUser());
					knowledgeDataAnalysis.setUpdateUser(record.getUpdateUser());
					knowledgeDataAnalysis.setCreateTime(record.getCreateTime());
					knowledgeDataAnalysis.setUpdateTime(record.getUpdateTime());
					knowledgeDataAnalysis.setRelationId(String.valueOf(record.getId()));
					knowledgeDataAnalysis.setDeleteFlag(Integer.valueOf(record.getDeleteFlag()));
					if (MultimediaEnum.getByFileType(record.getFileType()) != null) {
						knowledgeDataAnalysis.setExtendedType(MultimediaEnum.getByFileType(record.getFileType()).getName());
					} else {
						knowledgeDataAnalysis.setExtendedType(record.getFileType());

					}

					OauthUser oauthUser = oauthUserMap.get(userName);
					if (ObjectUtils.isNotEmpty(oauthUser)) {
						knowledgeDataAnalysis.setDeptId(oauthUser.getDeptId());
						knowledgeDataAnalysis.setDeptName(oauthUser.getDeptName());
					}

					dataAnalyses.add(knowledgeDataAnalysis);
				}
				if (CollectionUtil.isNotEmpty(dataAnalyses)) {
					knowledgeDataAnalysisMapper.insertBatch(dataAnalyses);
				}
				if (!fileList.hasNext()) {
					log.info("数据同步完成");
					break;
				}
				PAGE_NO++;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("同步数据失败:" + e.getMessage());
				System.err.println("同步数据失败: " + e.getMessage());
				break;
			}
		}
	}


	private OauthUser queryUserInfo(String accountName, String userId) {
		List<OauthUser> lists = OauthUser.create()
				.where(StringUtils.isNotBlank(accountName) ,OAUTH_USER.ACCOUNT_NAME.eq(accountName))
				.and(StringUtils.isNotBlank(userId) ,OAUTH_USER.ID.eq(userId))
				.limit(1)
				.lists();
		OauthUser oauthUser = null;
		if (CollectionUtil.isNotEmpty(lists)) {
			oauthUser = lists.get(0);
			OauthDept deptDetail = deptService.getDeptDetail(oauthUser.getDeptId());
			if (null != deptDetail) {
				oauthUser.setDeptName(deptDetail.getDeptName());
			}
		}

		return oauthUser;
	}

	/**
	 * 同步插入问答对数据
	 */
	private void summaryWdd(Map<String, KnowledgeInfo> knowledgeInfoMap,
							Map<String, OauthUser> oauthUserMap) {
		int PAGE_SIZE = 1000; // 每页大小
		LambdaEsQueryWrapper<KnowledgeData> lambdaEsQueryWrapper = EsWrappers.lambdaQuery(KnowledgeData.class);
		lambdaEsQueryWrapper.size(PAGE_SIZE);
		lambdaEsQueryWrapper.orderByDesc(KnowledgeData::getId);
		List<Object> searchAfterValues = null;
		while (true) {
			// 执行分页查询
			SAPageInfo<KnowledgeData> saPageInfo = dataMapper.searchAfterPage(lambdaEsQueryWrapper, searchAfterValues, PAGE_SIZE);
			// 获取当前页的数据
			List<KnowledgeData> currentPageData = saPageInfo.getList();
			if (currentPageData == null || currentPageData.isEmpty()) {
				// 如果当前页没有数据，说明已经没有下一页了，退出循环
				break;
			}
			List<KnowledgeDataAnalysis> dataAnalyses = new ArrayList<>();
			for (KnowledgeData record : currentPageData) {
				String userName = StringUtils.isNotBlank(record.getCreateUser()) ? record.getCreateUser() : record.getUpdateUser();
				if((StringUtils.isEmpty(userName) && StringUtils.isEmpty(record.getUserId())) || StringUtils.isEmpty(record.getKnowledgeId())) {
					continue;
				}
				KnowledgeDataAnalysis knowledgeDataAnalysis = new KnowledgeDataAnalysis();
				knowledgeDataAnalysis.setKnowledgeId(record.getKnowledgeId());

				KnowledgeInfo knowledgeInfo =knowledgeInfoMap.get(record.getKnowledgeId());
				if (ObjectUtils.isNotEmpty(knowledgeInfo)) {
					knowledgeDataAnalysis.setKnowledgeName(knowledgeInfo.getKnowledgeName());
				}
				// 定义日期时间格式
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				// 解析为 LocalDateTime 对象
				LocalDateTime localDateTime = LocalDateTime.parse(record.getCreateTime(), formatter);

				// 提取日期部分并格式化为字符串
				String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				knowledgeDataAnalysis.setCreateDate(date);
				knowledgeDataAnalysis.setDataType(KnowledgeDataTypeEnum.wdd.getCode());
				knowledgeDataAnalysis.setCreateUser(record.getCreateUser());
				knowledgeDataAnalysis.setUpdateUser(record.getUpdateUser());
				knowledgeDataAnalysis.setCreateTime(record.getCreateTime());
				knowledgeDataAnalysis.setUpdateTime(record.getUpdateTime());
				knowledgeDataAnalysis.setRelationId(record.getId());
				knowledgeDataAnalysis.setDeleteFlag(Integer.valueOf(record.getDelStatus()));
				OauthUser oauthUser = oauthUserMap.get(userName);
				if (ObjectUtils.isNotEmpty(oauthUser)) {
					knowledgeDataAnalysis.setDeptId(oauthUser.getDeptId());
					knowledgeDataAnalysis.setDeptName(oauthUser.getDeptName());
				}
				dataAnalyses.add(knowledgeDataAnalysis);
			}
			if (CollectionUtil.isNotEmpty(dataAnalyses)) {
				knowledgeDataAnalysisMapper.insertBatch(dataAnalyses);
			}

			searchAfterValues = saPageInfo.getNextSearchAfter();
			// 如果返回的结果少于每页大小，说明已经是最后一页
			if (currentPageData.size() < PAGE_SIZE) {
				break;
			}
		}
	}

	/**
	 * 同步插入Url数据
	 */
	private void summaryUrl(Map<String, KnowledgeInfo> knowledgeInfoMap,
							Map<String, OauthUser> oauthUserMap) {
		int PAGE_NO = 1;
		int PAGE_SIZE = 1000; // 每页大小
		ParserInfoPageParam parserInfoPageParam = new ParserInfoPageParam();
		parserInfoPageParam.setPageSize(PAGE_SIZE);
		parserInfoPageParam.setNeedParam(true);
		while (true) {
			try {
				parserInfoPageParam.setPageNo(PAGE_NO);
				Page<UrlParserInfo> urlParserInfoPage = urlParserInfoService.getList(parserInfoPageParam).getData();
				log.info("urlParserInfoPage 的数量：{}" , urlParserInfoPage.getTotalRow());
				List<KnowledgeDataAnalysis> dataAnalyses = new ArrayList<>();
				for (UrlParserInfo record : urlParserInfoPage.getRecords()) {
					String userName = StringUtils.isNotBlank(record.getCreateUser()) ? record.getCreateUser() : record.getCreateUser();
					if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(record.getKnowledgeId())) {
						continue;
					}
					KnowledgeDataAnalysis knowledgeDataAnalysis = new KnowledgeDataAnalysis();
					knowledgeDataAnalysis.setKnowledgeId(record.getKnowledgeId());
					KnowledgeInfo knowledgeInfo = knowledgeInfoMap.get(record.getKnowledgeId());
					if (ObjectUtils.isNotEmpty(knowledgeInfo)) {
						knowledgeDataAnalysis.setKnowledgeName(knowledgeInfo.getKnowledgeName());
					} else {
						continue;
					}
					// 定义日期时间格式
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					// 解析为 LocalDateTime 对象
					LocalDateTime localDateTime = LocalDateTime.parse(record.getCreateTime(), formatter);

					// 提取日期部分并格式化为字符串
					String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					knowledgeDataAnalysis.setCreateDate(date);
					knowledgeDataAnalysis.setDataType(KnowledgeDataTypeEnum.url.getCode());
					knowledgeDataAnalysis.setCreateUser(record.getCreateUser());
					knowledgeDataAnalysis.setUpdateUser(record.getUpdateUser());
					knowledgeDataAnalysis.setCreateTime(record.getCreateTime());
					knowledgeDataAnalysis.setUpdateTime(record.getUpdateTime());
					knowledgeDataAnalysis.setRelationId(String.valueOf(record.getId()));
					knowledgeDataAnalysis.setDeleteFlag(record.getDeleteFlag());
					OauthUser oauthUser = oauthUserMap.get(userName);
					if (ObjectUtils.isNotEmpty(oauthUser)) {
						knowledgeDataAnalysis.setDeptId(oauthUser.getDeptId());
						knowledgeDataAnalysis.setDeptName(oauthUser.getDeptName());
					}
					dataAnalyses.add(knowledgeDataAnalysis);
				}
				if (CollectionUtil.isNotEmpty(dataAnalyses)) {
					knowledgeDataAnalysisMapper.insertBatch(dataAnalyses);
				}
				if (!urlParserInfoPage.hasNext()) {
					log.info("数据同步完成");
					break;
				}
				PAGE_NO++;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("同步数据失败:" + e.getMessage());
				System.err.println("同步数据失败: " + e.getMessage());
				break;
			}
		}
	}


	/**
	 * 同步插入结构化数据
	 */
	private void summaryJgh(Map<String, KnowledgeInfo> knowledgeInfoMap,
							Map<String, OauthUser> oauthUserMap) {

		int PAGE_NO = 1;
		int PAGE_SIZE = 1000; // 每页大小
		UnionParam unionParam = new UnionParam();
		unionParam.setPageSize(PAGE_SIZE);
		while (true) {
			try {
				unionParam.setPageNo(PAGE_NO);
				PageInfo<StructureParserVo> parserVoPageInfo = dataSourceParserInfoService.unionData(unionParam)
						.getData();
				List<KnowledgeDataAnalysis> dataAnalyses = new ArrayList<>();
				for (StructureParserVo record : parserVoPageInfo.getRecords()) {
					String userName = StringUtils.isNotBlank(record.getCreateUser()) ? record.getCreateUser() : record.getUpdateUser();
					if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(record.getKnowledgeId())) {
						continue;
					}
					KnowledgeDataAnalysis knowledgeDataAnalysis = new KnowledgeDataAnalysis();

					knowledgeDataAnalysis.setKnowledgeId(record.getKnowledgeId());
					KnowledgeInfo knowledgeInfo = knowledgeInfoMap.get(record.getKnowledgeId());
					if (ObjectUtils.isNotEmpty(knowledgeInfo)) {
						knowledgeDataAnalysis.setKnowledgeName(knowledgeInfo.getKnowledgeName());
					}

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					LocalDateTime localDateTime = LocalDateTime.parse(record.getCreateTime(), formatter);

					String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					knowledgeDataAnalysis.setCreateDate(date);
					knowledgeDataAnalysis.setDataType(KnowledgeDataTypeEnum.jgh.getCode());
					knowledgeDataAnalysis.setExtendedType(record.getType());
					knowledgeDataAnalysis.setCreateUser(record.getCreateUser());
					knowledgeDataAnalysis.setUpdateUser(record.getUpdateUser());
					knowledgeDataAnalysis.setCreateTime(record.getCreateTime());
					knowledgeDataAnalysis.setUpdateTime(record.getCreateTime());
					knowledgeDataAnalysis.setRelationId(record.getId().toString());
					knowledgeDataAnalysis.setDeleteFlag((record.getDeleteFlag()));

					OauthUser oauthUser = oauthUserMap.get(userName);
					if (ObjectUtils.isNotEmpty(oauthUser)) {
						knowledgeDataAnalysis.setDeptId(oauthUser.getDeptId());
						knowledgeDataAnalysis.setDeptName(oauthUser.getDeptName());
					}

					dataAnalyses.add(knowledgeDataAnalysis);

				}
				if (CollectionUtil.isNotEmpty(dataAnalyses)) {
					knowledgeDataAnalysisMapper.insertBatch(dataAnalyses);
				}
				if (parserVoPageInfo.getTotal() < PAGE_NO * PAGE_SIZE) {
					log.info("数据同步完成");
					break;
				}
				PAGE_NO++;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("同步数据失败:" + e.getMessage());
				System.err.println("同步数据失败: " + e.getMessage());
				break;
			}
		}
	}

	@Override
	public Result knowledgeCount2(KnowledgeDataParam knowledgeDataParam) {

		JSONObject jsonObject = new JSONObject();
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (StringUtils.isNotBlank(knowledgeDataParam.getEndTime())) {

				endDate = format.parse(knowledgeDataParam.getEndTime());
			}
			if (StringUtils.isNotBlank(knowledgeDataParam.getStartTime())) {
				startDate = format.parse(knowledgeDataParam.getStartTime());
			}
		} catch (ParseException e) {
			log.info("日期格式异常：startDate: {}, endDate: {}", startDate, endDate);
			e.printStackTrace();
		}
		// 构建查询条件
		Wrappers<Object> queryWrapper = Wrappers.init()
				.select("knowledge_data_analysis.data_type AS dataType")
				.select("COUNT(*) AS count")
				.where((StringUtils.isNotEmpty(knowledgeDataParam.getKnowledgeId())), KNOWLEDGE_DATA_ANALYSIS.KNOWLEDGE_ID.eq(knowledgeDataParam.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getEndTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.le(endDate))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getStartTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.ge(startDate))
				.groupBy(KNOWLEDGE_DATA_ANALYSIS.DATA_TYPE);

		// 执行查询并返回结果为 List<Map>
		List<KnowledgeCountDto> groupData = knowledgeDataAnalysisMapper.selectListByQueryAs(queryWrapper, KnowledgeCountDto.class);
		Map<String, Integer> knowledgeMap = groupData.stream().collect(Collectors.toMap(
				KnowledgeCountDto::getDataType,
				KnowledgeCountDto::getCount,
				(k1, k2) -> k1
		));
		Integer wjCount = knowledgeMap.getOrDefault(KnowledgeDataTypeEnum.wj.getCode(), 0);
		Integer wddCount = knowledgeMap.getOrDefault(KnowledgeDataTypeEnum.wdd.getCode(), 0);
		Integer urlCount = knowledgeMap.getOrDefault(KnowledgeDataTypeEnum.url.getCode(), 0);
		Integer jghCount = knowledgeMap.getOrDefault(KnowledgeDataTypeEnum.jgh.getCode(), 0);

		jsonObject.put("wjCount", wjCount);
		jsonObject.put("wddCount", wddCount);
		jsonObject.put("urlCount", urlCount);
		jsonObject.put("jghCount", jghCount);

		// 构建查询条件
		Wrappers<Object> queryWrapper2 = Wrappers.init()
				.select("knowledge_data_analysis.extended_type AS extendedType")
				.select("COUNT(*) AS count")
				.where((StringUtils.isNotEmpty(knowledgeDataParam.getKnowledgeId())), KNOWLEDGE_DATA_ANALYSIS.KNOWLEDGE_ID.eq(knowledgeDataParam.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getEndTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.le(endDate))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getStartTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.ge(startDate))
				.and(KNOWLEDGE_DATA_ANALYSIS.DATA_TYPE.ge(KnowledgeDataTypeEnum.dmt.getCode()))
				.groupBy(KNOWLEDGE_DATA_ANALYSIS.EXTENDED_TYPE);
		// 执行查询并返回结果为 List<Map>
		List<KnowledgeCountDto> groupDmtData = knowledgeDataAnalysisMapper.selectListByQueryAs(queryWrapper2, KnowledgeCountDto.class);
		Map<String, Integer> knowledgeDmtMap = groupDmtData.stream().collect(Collectors.toMap(
				KnowledgeCountDto::getExtendedType,
				KnowledgeCountDto::getCount,
				(k1, k2) -> k1
		));

		Integer imageCount = knowledgeDmtMap.getOrDefault(MultimediaEnum.IMAGE.getName(), 0);
		Integer audioCount = knowledgeDmtMap.getOrDefault(MultimediaEnum.AUDIO.getName(), 0);
		Integer videoCount = knowledgeDmtMap.getOrDefault(MultimediaEnum.VIDEO.getName(), 0);
		Integer dmtCount = imageCount + audioCount + videoCount;
		jsonObject.put("imageCount", imageCount);
		jsonObject.put("audioCount", audioCount);
		jsonObject.put("videoCount", videoCount);
		jsonObject.put("dmtCount", dmtCount);
		jsonObject.put("allCount", wjCount + wddCount + urlCount + jghCount + dmtCount);
		return Result.success(jsonObject);
	}

	@Override
	public Result knowledgeCountTrend2(KnowledgeDataParam knowledgeDataParam) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String startDate;
		String endDate;
		if (StringUtils.isBlank(knowledgeDataParam.getEndTime())) {
			endDate = dateFormat.format(now);
		}else {
			endDate = knowledgeDataParam.getEndTime().split(" ")[0];
		}
		if (StringUtils.isBlank(knowledgeDataParam.getStartTime())) {
			// 创建一个Calendar实例并设置为当前时间
			Calendar calendar = Calendar.getInstance();
			// 往前推10天
			calendar.add(Calendar.DAY_OF_MONTH, -10);
			// 获取修改后的时间
			Date date = calendar.getTime();
			startDate = dateFormat.format(date);
		}else {
			startDate =  knowledgeDataParam.getStartTime().split(" ")[0];
		}
		Wrappers<Object> wrappers = Wrappers.init()
				.select("knowledge_data_analysis.create_date AS date")
				.select("COUNT(*) AS totalCount")
				.where(StringUtils.isNotBlank(knowledgeDataParam.getKnowledgeId()), KNOWLEDGE_DATA_ANALYSIS.KNOWLEDGE_ID.eq(knowledgeDataParam.getKnowledgeId()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getKnowledgeName()), KNOWLEDGE_DATA_ANALYSIS.KNOWLEDGE_NAME.like(knowledgeDataParam.getKnowledgeName()))
				.and(KNOWLEDGE_DATA_ANALYSIS.DATA_TYPE.ne(KnowledgeDataTypeEnum.dmt.getCode()))
				.and(StringUtils.isNotBlank(knowledgeDataParam.getEndTime()), KNOWLEDGE_DATA_ANALYSIS.CREATE_TIME.le(knowledgeDataParam.getEndTime()))
				.groupBy(KNOWLEDGE_DATA_ANALYSIS.CREATE_DATE)
				.orderBy(KNOWLEDGE_DATA_ANALYSIS.CREATE_DATE.asc());
		//知识库数量
		List<KnowledgeDateCountDto> knowledgeDateCountDtos = knowledgeDataAnalysisMapper.selectListByQueryAs(wrappers, KnowledgeDateCountDto.class);

		Map<String, Integer> knowledgeMap = knowledgeDateCountDtos.stream().collect(Collectors.toMap(
				KnowledgeDateCountDto::getDate,
				KnowledgeDateCountDto::getTotalCount,
				(k1, k2) -> k1
		));
		int cumulativeTotal = 0;
		//获取两个时间之间有多少个日期 列表
		List<String> dates = DateRangeExample.getDatesBetween(startDate,
				endDate);
		List<KnowledgeDateCountDto> resultCount = new ArrayList<>();
		for (String date : dates){
			cumulativeTotal += knowledgeMap.getOrDefault(date, 0);
			KnowledgeDateCountDto result = new KnowledgeDateCountDto();
			result.setDate(date);
			result.setTotalCount(cumulativeTotal);
			resultCount.add(result);
		}
		return Result.success(resultCount);

	}

	@Override
	public Result<Page<KnowledgeNameInfoResult>> queryAllKnowledgeNameList(KnowledgeInfoPageParam knowledgeInfo) {
		Wrappers<Object> wrapper = Wrappers.init()
				.select(KNOWLEDGE_INFO.KNOWLEDGE_ID, KNOWLEDGE_INFO.KNOWLEDGE_NAME)
				.where(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeName()), KNOWLEDGE_INFO.KNOWLEDGE_NAME.like(knowledgeInfo.getKnowledgeName()))
				.and(StringUtils.isNotBlank(knowledgeInfo.getKnowledgeId()), KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeInfo.getKnowledgeId()));
		Page<KnowledgeNameInfoResult> page = pageAs(Page.of(knowledgeInfo.getPageNo(), knowledgeInfo.getPageSize()), wrapper, KnowledgeNameInfoResult.class);
		return Result.success(page);

	}

	@Override
	public Result setPreset(KnowledgeInfoPageParam knowledgeInfo) {
		if (knowledgeInfo == null) {
			return Result.fail("请求参数为空");
		}
		TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

		// 只有超管才能操作
		if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
			return Result.fail("无操作权限");
		}

		// 获取传入的知识库主键id
		Long Id = knowledgeInfo.getId();
		KnowledgeInfo updateKnowledgeInfo = getById(Id);
		if (updateKnowledgeInfo == null) {
			return Result.fail("知识库不存在");
		}

		// 超级管理员集合
		List<OauthUser> superManageUser = userService.getSuperManageUser();
		List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

		// 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
		if (accountNames.contains(updateKnowledgeInfo.getCreateUser())){
			// 构造更新对象，已经是预置就取消，否则设置为预置,
			if (OFFICIAL.getCode().equals(updateKnowledgeInfo.getOwnerType())
			) {
				updateKnowledgeInfo.setOwnerType(PERSONAL.getCode());
			} else {
				updateKnowledgeInfo.setOwnerType(OFFICIAL.getCode());
			}
		}else{
			return Result.fail("该知识库非超级管理员创建，无法进行预置操作");
		}

		// 执行更新
		updateById(updateKnowledgeInfo,false);

		return Result.success("知识库预置更改成功");

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result fixKnowledgeDataType(FixKnowledgeDataTypeParam fixKnowledgeDataTypeParam) {
		List<String> knowledgeIds = fixKnowledgeDataTypeParam.getKnowledgeIds();
		Wrappers<Object> know= Wrappers.init()
				.where(KNOWLEDGE_INFO.DELETE_FLAG.eq(0))
				.and(CollectionUtils.isNotEmpty(knowledgeIds), KNOWLEDGE_INFO.KNOWLEDGE_ID.in(knowledgeIds));
		List<KnowledgeInfo> knowledgeInfos = mapper.selectListByQuery(know);
		// 取出非全部的一级目录
		Wrappers<Object> firstOther= Wrappers.init()
				.where(KNOWLEDGE_DATA_TYPE.TYPE.ne(KnowledgeConstant.ALL))
				.and(KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.isNotNull())
				.and(KNOWLEDGE_DATA_TYPE.PID.eq(0))
				.and(KNOWLEDGE_DATA_TYPE.DELETE_FLAG.eq(0));
		List<KnowledgeDataType> otherCategoryList = knowledgeDataTypeService.list(firstOther);
		// 转成 Map<knowledgeId, List<KnowledgeDataType>>
		Map<String, List<KnowledgeDataType>>  otherCategoryMap = otherCategoryList.stream()
				.collect(Collectors.groupingBy(KnowledgeDataType::getKnowledgeId));
		// 取出为全部的一级目录
		Wrappers<Object> allFirst = Wrappers.init()
				.where(KNOWLEDGE_DATA_TYPE.TYPE.eq(KnowledgeConstant.ALL))
				.and(KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.isNotNull())
				.and(KNOWLEDGE_DATA_TYPE.PID.eq(0))
				.and(KNOWLEDGE_DATA_TYPE.DELETE_FLAG.eq(0));
		List<KnowledgeDataType> allCategoryList = knowledgeDataTypeService.list(allFirst);
		Map<String, List<KnowledgeDataType>>  allCategoryMap = allCategoryList.stream()
				.collect(Collectors.groupingBy(KnowledgeDataType::getKnowledgeId));
		knowledgeInfos.forEach(knowledgeInfo -> {
			String knowledgeId = knowledgeInfo.getKnowledgeId();
			log.info("知识库id：[{}]开始修复数据", knowledgeId);
			List<KnowledgeDataType> knowledgeDataTypes = allCategoryMap.get(knowledgeId);
			KnowledgeDataType knowledgeDataType;
			if (CollectionUtils.isEmpty(knowledgeDataTypes)) {
				 knowledgeDataType =  addAllKnowledgeType(knowledgeId);
			} else {
				knowledgeDataType = knowledgeDataTypes.get(0);
			}
			Long pid = knowledgeDataType.getId();
			List<KnowledgeDataType> dataTypes = otherCategoryMap.get(knowledgeId);
			if (CollectionUtils.isNotEmpty(dataTypes)) {
				log.info("知识库id：[{}]开始修复QA目录数据", knowledgeId);

				// 处理qa文件目录
				dataTypes.forEach(dataType -> dataType.setPid(pid));
				knowledgeDataTypeService.updateBatch(dataTypes);
			}
			List<String> knowledgeIdsAdd = Lists.newArrayList();
			knowledgeIdsAdd.add(knowledgeId);
			// 处理qa数据
			List<KnowledgeData> knowledgeData = knowledgeDataService.getKnowledgeData(knowledgeIdsAdd);
			if(CollectionUtils.isNotEmpty(knowledgeData)) {
				log.info("知识库id：[{}]开始修复QA数据,修复数据量：[{}]", knowledgeId, knowledgeData.size());
				// 处理qa数据
				knowledgeData.forEach(item -> item.setCategory(String.valueOf(pid)));
				knowledgeDataService.updateKnowledgeData(knowledgeData);
			}
			log.info("知识库id：[{}]修复数据成功", knowledgeId);
		});
		return Result.success("知识库数据修改成功");
	}

	/**
	 * 添加全部文件夹
	 * @param knowledgeId
	 * @return
	 */
	private KnowledgeDataType addAllKnowledgeType(String knowledgeId) {
		// QA类型使用的不是Folders
		KnowledgeDataType knowledgeDataType = new KnowledgeDataType();
		knowledgeDataType.setType(KnowledgeConstant.ALL);
		knowledgeDataType.setKnowledgeId(knowledgeId);
		knowledgeDataType.setStatus(Integer.valueOf(YesNoEnum.YES.getCode()));
		knowledgeDataType.setPid(0L);
		knowledgeDataTypeService.addKnowledgeDataType(knowledgeDataType);

		return knowledgeDataType;
	}
}
