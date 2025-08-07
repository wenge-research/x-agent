package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.config.WebSocketServer;
import com.wenge.model.constants.KnowledgeConstant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.Folders;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wenge.model.entity.UrlParserInfo;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.mapper.KnowledgeInfoMapper;
import com.wenge.model.mapper.UrlParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeUrlDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.service.UrlParserInfoService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.vo.ParserUrlDetailVo;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.UrlParserInfoTableDef.URL_PARSER_INFO;

/**
 * Description: 文件服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 *
 */
@Service
@Slf4j
public class UrlParserInfoServiceImpl extends ServiceImpl<UrlParserInfoMapper, UrlParserInfo> implements UrlParserInfoService {

	@Autowired
	private KnowledgeUrlDataMapper knowledgeUrlDataMapper;

	@Autowired
	private UrlParserInfoMapper urlParserInfoMapper;

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private KnowledgeInfoMapper knowledgeInfoMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private FoldersService foldersService;

	@Autowired
	private DenseVectorService denseVectorService;

	@Override
	public Result add(UrlParserInfo param){

		return Result.success();
	}

	@Override
	public Result<Page<UrlParserInfo>> getList(ParserInfoPageParam info){
		String knowledgeId = info.getKnowledgeId();
		// 初始化空集合，兼容原始没有文件夹的数据
		List<Folders> allFolders = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(info.getKnowledgeIds())){
			// 查询当前文件夹及下的所有子文件夹
				allFolders = Folders.creat()
						.where(FOLDERS.KNOWLEDGE_ID.in(info.getKnowledgeIds()))
						.and(FOLDERS.TYPE.eq("3"))
						.lists();
		}else {
			if (StringUtils.isNotBlank(knowledgeId)) {
				// 查询当前文件夹及下的所有子文件夹
				String foldersId = info.getFoldersId();
				if (StringUtils.isNotBlank(foldersId)) {
					allFolders = Folders.creat()
							.where(FOLDERS.KNOWLEDGE_ID.eq(knowledgeId))
							.lists();
					allFolders = foldersService.getTreeByFolderId(foldersId, allFolders);
				}
			}
		}

		List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());

		Page<UrlParserInfo> page = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(info.getKnowledgeId()), URL_PARSER_INFO.KNOWLEDGE_ID.eq(info.getKnowledgeId()))
				.and(CollectionUtil.isNotEmpty(foldersIdList), URL_PARSER_INFO.FOLDERS_ID.in(foldersIdList))
				.and(info.isNeedParam(), URL_PARSER_INFO.UPDATE_USER.isNotNull())
				.and(StringUtils.isNotBlank(info.getTitle()), and -> {
					and.or(URL_PARSER_INFO.TITLE.like(info.getTitle()));
					and.or(URL_PARSER_INFO.PAGE_URL.like(info.getTitle()));
				})
				.page(Page.of(info.getPageNo(), info.getPageSize()));
		return Result.success(page);
	}


	@Override
	public Result update(UrlParserInfo info){
		updateById(info);
		LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
				.notSelect(KnowledgeUrlData::getContentDense)
				.eq(KnowledgeUrlData::getUrlId, info.getUrlId());
		List<KnowledgeUrlData> urlData = knowledgeUrlDataMapper.selectList(wrapper);
		if (CollectionUtil.isNotEmpty(urlData)) {
			urlData.forEach(knowledgeUrlData -> {
				knowledgeUrlData.setEffectiveStartTime(info.getPeriodStart());
				knowledgeUrlData.setEffectiveEndTime(info.getPeriodEnd());
				knowledgeUrlData.setEnable(YesNoEnum.YES.getName().equals(info.getEnable()) ? 1 : 2);
			});
			knowledgeUrlDataMapper.updateBatchByIds(urlData);
		}
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result deleteBatch(ParserInfoDeleteParam param) {

		if (CollectionUtils.isEmpty(param.getIds())) {
			return Result.success();
		}

		// 删除元信息
		Wrappers wrappers = Wrappers.init().where(new QueryColumn("url_id").in(param.getIds()));
		mapper.deleteByQuery(wrappers);

		// 删除ES中的关联信息
		LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
				.in(!CollectionUtils.isEmpty(param.getIds()), KnowledgeUrlData::getUrlId, param.getIds());
		knowledgeUrlDataMapper.delete(wrapper);

		return Result.success();
	}

	@Override
	public UrlParserInfo getById(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}

        return Wrappers.of(mapper)
                .where(URL_PARSER_INFO.URL_ID.eq(id))
                .limit(1)
                .one();
	}

	@Override
	public Result getDetail(UrlParserInfoDetailParam param) throws IOException {
		//GetIndexResponse index = knowledgeUrlDataMapper.getIndex();

		LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
				.notSelect(KnowledgeUrlData::getContentDense, KnowledgeUrlData::getTitleDense, KnowledgeUrlData::getContentDense1024)
				.eq(KnowledgeUrlData::getUrlId, param.getUrlId());

		EsPageInfo<KnowledgeUrlData> knowledgeUrlDataEsPageInfo = knowledgeUrlDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());

		Map<String, Object> condition = new HashMap<>();
		condition.put("url_id",param.getUrlId());
		UrlParserInfo urlParserInfo = urlParserInfoMapper.selectOneByMap(condition);
		//long totalRow = searchResponse.getHits().getTotalHits().value;
		ParserUrlDetailVo parserUrlDetailVo = ParserUrlDetailVo.builder()
				.title(urlParserInfo.getTitle())
				.pageUrl(urlParserInfo.getPageUrl())
				.wordCount(urlParserInfo.getWordCount())
				.avgParagraphsCount(urlParserInfo.getAvgParagraphsCount())
				.paragraphsNum(urlParserInfo.getParagraphsNum())
				.createTime(urlParserInfo.getCreateTime())
				.updateTime(urlParserInfo.getUpdateTime())
				.dataList(knowledgeUrlDataEsPageInfo.getList())
				.totalRow(knowledgeUrlDataEsPageInfo.getTotal())
				.pageNo(param.getPageNo())
				.pageSize(param.getPageSize())
				.totalPage(knowledgeUrlDataEsPageInfo.getPages())
				.build();
		return Result.success(parserUrlDetailVo);
	}

	/**
	 * 获取要解析的知识库url
	 */
	@Override
	public List<UrlParserInfo> getParseredKnowledgeUrl() {
		Wrappers wrappers = Wrappers.init()
				.and(URL_PARSER_INFO.PAGE_URL.isNotNull())
				.and(URL_PARSER_INFO.PAGE_URL.ne(""))
				.and(URL_PARSER_INFO.STATUS.in(FileStatusEnum.FAIL.getCode(), FileStatusEnum.PARSING.getCode(), FileStatusEnum.UPLOADING.getCode(), FileStatusEnum.UPLOAD_SUCCESS.getCode()))
				.and(URL_PARSER_INFO.KNOWLEDGE_ID.isNotNull())
				.and(URL_PARSER_INFO.DELETE_FLAG.eq(0))
				.and(URL_PARSER_INFO.KNOWLEDGE_ID.ne(""))
				.and(URL_PARSER_INFO.ERROR_NUM.lt(5))
				.orderBy(URL_PARSER_INFO.CREATE_TIME.asc())
				.limit(60);
		return mapper.selectListByQuery(wrappers);
	}

	@Override
	public void downloadKnowledgeUrlTemp(HttpServletResponse response) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			Resource resource = resourceLoader.getResource("classpath:temp/knowledgeUrlTemp.xlsx");
			inputStream = resource.getInputStream();

			// 设置响应头
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			// 获取文件名，并进行UTF-8编码
			String fileName = URLEncoder.encode("知识库url模板.xlsx", StandardCharsets.UTF_8.toString());
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

	@Override
	public void saveCollectPlatformKnowledgeContent(SaveCollectPlatformContentDataParam param) {
		List<SaveCollectPlatformContentDataParam.KnowledgeContentDataInfo> knowledgeDataInfos = param.getKnowledgeDataInfos();
		if (CollectionUtil.isEmpty(knowledgeDataInfos)) {
			return;
		}

		String knowledgeId = param.getKnowledgeId();
		// 查询知识库父文件夹（名字为“全部”）
		Folders parentFolder = foldersService.getByFoldersName(KnowledgeConstant.ALL, knowledgeId);
		if (null == parentFolder) {
			throw new RuntimeException("当前知识库名为【全部】的文件夹已被删除");
		}
		String parentFoldersId =  parentFolder.getFoldersId();
		int collectPlatformTaskId = param.getTaskId();
		Folders collectFolders = foldersService.getByDesignatedFoldersName(KnowledgeConstant.COLLECT_DATA_FOLDER_NAME, knowledgeId, parentFoldersId);
		if (null == collectFolders) {
			// 如果名为“采集数据”得文件夹不存在，就创建
			Folders newFolders = Folders.creat();
			newFolders.setParentId(parentFoldersId);
			newFolders.setKnowledgeId(knowledgeId);
			newFolders.setName(KnowledgeConstant.COLLECT_DATA_FOLDER_NAME);
			collectFolders = foldersService.createFolders(newFolders);

			// 直接保存数据
			saveCollectPlatformData(knowledgeId, knowledgeDataInfos, collectFolders.getFoldersId(), collectPlatformTaskId);
		} else {
			// 如果知识库没有推送的文件，就更新
			String collectFoldersId = collectFolders.getFoldersId();
			List<String> urls = knowledgeDataInfos.stream().map(SaveCollectPlatformContentDataParam.KnowledgeContentDataInfo::getUrl).collect(Collectors.toList());
			Wrappers existQueryWrappers = Wrappers.init()
					.where(URL_PARSER_INFO.PAGE_URL.in(urls))
					.and(URL_PARSER_INFO.FOLDERS_ID.eq(collectFoldersId))
					.and(URL_PARSER_INFO.COLLECT_PLATFORM_TASK_ID.eq(param.getTaskId()));
			List<UrlParserInfo> existUrlParserInfos = mapper.selectListByQuery(existQueryWrappers);

			// 获取新增的推送数据
			Set<String> existingPageUrls = existUrlParserInfos.stream()
					.map(UrlParserInfo::getPageUrl)
					.collect(Collectors.toSet());
			List<SaveCollectPlatformContentDataParam.KnowledgeContentDataInfo> newKnowledgeDataInfoss = knowledgeDataInfos.stream()
					.filter(knowledgeDataInfo -> !existingPageUrls.contains(knowledgeDataInfo.getUrl()))
					.collect(Collectors.toList());
			saveCollectPlatformData(knowledgeId, newKnowledgeDataInfoss, collectFoldersId, collectPlatformTaskId);
		}
	}

	private void saveCollectPlatformData(String knowledgeId, List<SaveCollectPlatformContentDataParam.KnowledgeContentDataInfo> knowledgeDataInfos, String foldersId, int collectPlatformTaskId) {
		if (CollectionUtil.isEmpty(knowledgeDataInfos)) {
			return;
		}

		// url数据
		List<UrlParserInfo> urlParserInfoList = new ArrayList<>();
		// 内容数据
		List<KnowledgeUrlData> knowledgeUrlDataList = new ArrayList<>();
		for (SaveCollectPlatformContentDataParam.KnowledgeContentDataInfo dataInfo: knowledgeDataInfos) {
			String time = DateUtil.format(new Date(), DateUtil.PATTERN_1);
			String accountName = AppContextHolder.getAccountName();
			String url = dataInfo.getUrl();
			String content = dataInfo.getContent();			UrlParserInfo urlParserInfo = UrlParserInfo.builder()
					.urlId(IdUtil.randomUUID())
					.knowledgeId(knowledgeId)
					.foldersId(foldersId)
					.status(FileStatusEnum.SUCCESS.getCode())
					.pageUrl(url)
					.title(dataInfo.getTitle())
					.errorNum(0)
					.collectPlatformTaskId(collectPlatformTaskId)
					.deleteFlag(0)
					.createUser(accountName)
					.updateUser(accountName)
					.createTime(time)
					.updateTime(time)
					.build();
			urlParserInfoList.add(urlParserInfo);

			KnowledgeUrlData knowledgeUrlData = KnowledgeUrlData.builder()
					.knowledgeId(knowledgeId)
					.urlId(urlParserInfo.getUrlId())
					.content(dataInfo.getContent())
					.enable(1)
					.url(url)
					.encodeType(1)
					.createTime(time)
					.updateTime(time)
					.createUser(accountName)
					.updateUser(accountName)
					.category("原文切片")
					.id(IdUtil.randomUUID())
					.build();
			denseVectorService.modelEncode(content, knowledgeId, knowledgeUrlData, CONTENT_DENSE_FILED);
			knowledgeUrlDataList.add(knowledgeUrlData);
		}
		this.saveBatch(urlParserInfoList);
		knowledgeUrlDataMapper.insertBatch(knowledgeUrlDataList);
	}

	@Override
	public Result<String> importUrlParserInfoData(importUrlParserDataParam param) {
		Map<String, Object> query = new HashMap<>();
		query.put("knowledge_id", param.getKnowledgeId());
		KnowledgeInfo knowledgeInfo = knowledgeInfoMapper.selectOneByMap(query);
		if (null == knowledgeInfo) {
			return Result.fail("知识库不存在");
		}

		String accountName = AppContextHolder.getAccountName();
		String key = RedisKey.IMPORTING_URL + (StringUtils.isNotBlank(accountName) ? accountName : "admin");
		if (redisService.hasKey(key)) {
			return Result.fail("当前有其他导入任务在进行中");
		}
		log.info("===>开始导入url数据");
		MultipartFile file = param.getFile();
		if (file == null) {
			return Result.fail("文件不能为空");
		} else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
			return Result.fail("不支持该文件类型，请上传xlsx类型的文件");
		}

		// 异步导入数据
		redisService.set(key, param.getKnowledgeId(), 60 * 60 * 24 * 7);
		new Thread(() -> {
			try {
				importUrlData(file, param);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				redisService.del(key);
			}
		}).start();
		return Result.success("正在导入中");
	}

	/**
	 * 导入数据
	 */
	private void importUrlData(MultipartFile file, importUrlParserDataParam param) throws IOException {
		String knowledgeId = param.getKnowledgeId();
		String foldersId = param.getFoldersId();
		ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
		// 第一行为标题，从第二行开始读取
		int row = 1;
		List<List<Object>> read = reader.read();
		read = read.subList(row, read.size());
		int total = read.size();
		Vector<UrlParserInfo> urlParserInfoList = new Vector<>();
		ThreadPoolExecutor poolExecutor = ExecutorBuilder.create().setCorePoolSize(8).setMaxPoolSize(8).setKeepAliveTime(0).setWorkQueue(new LinkedBlockingQueue<>()).build();
		// 读取数据，解析每一行数据
		for (List<Object> dataDetailList : read) {
			List<Object> objects = JSON.parseArray(JSON.toJSONString(dataDetailList));
			poolExecutor.execute(() -> {
				String url = getValue(objects, 0);
				if (StringUtils.isNotEmpty(url)) {
					UrlParserInfo urlParserInfo = UrlParserInfo.builder()
							.urlId(IdUtil.randomUUID())
							.knowledgeId(knowledgeId)
							.foldersId(foldersId)
							.status(FileStatusEnum.UPLOAD_SUCCESS.getCode())
							.pageUrl(url)
							.errorNum(0)
							.deleteFlag(0)
							.createTime(DateUtil.format(new Date(), DateUtil.PATTERN_1))
							.updateTime(DateUtil.format(new Date(), DateUtil.PATTERN_1))
							.build();
					urlParserInfoList.add(urlParserInfo);
				}
			});
		}

		// websocket通知进度
		JSONObject websocket = new JSONObject();
		websocket.put("business", "url数据正在导入");
		websocket.put("nums", 0);
		websocket.put("msg", "");

		boolean isEmpty = CollectionUtil.isEmpty(read);
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		String accountName = "admin";
		if (StringUtils.isNotBlank(tokenOauthUserInfo.getAccountName())) {
			accountName = tokenOauthUserInfo.getAccountName();
		}

		// 通知错误信息
		if (isEmpty) {
			websocket.put("msg", "导入url失败，数据不能为空");
			WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);
			poolExecutor.shutdown();
			return;
		}

		int size = urlParserInfoList.size();
		// 通知导入进度
		while (poolExecutor.getActiveCount() != 0) {
			ThreadUtil.safeSleep(1000);
			websocket.put("nums", BigDecimal.valueOf(size).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);
		}
		poolExecutor.shutdown();

		websocket.put("business", "url数据正在保存");
		websocket.put("nums", 0);

		// 保存数据
		if (CollectionUtil.isNotEmpty(urlParserInfoList)) {
			saveUrlParserInfoData(urlParserInfoList, websocket, total, accountName);
			String key = RedisKey.IMPORTING_URL + (StringUtils.isNotBlank(accountName) ? accountName : "admin");
			redisService.del(key);
		}
		log.info("===>导入url数据完成，共导入：{}条数据", urlParserInfoList.size());
	}


	/**
	 * 从单元格获取数据
	 */
	public static String getValue(List<Object> dataList, Integer index) {
		if (dataList.size() > index && null != dataList.get(index) && StringUtils.isNotBlank(dataList.get(index).toString())) {
			return dataList.get(index).toString();
		}
		return "";
	}

	private void saveUrlParserInfoData(Vector<UrlParserInfo> urlParserInfoList, JSONObject websocket, int total, String accountName) throws IOException {
		List<UrlParserInfo> toSaveList = ListUtil.toList();
		long pageNo = 1;
		int num = 0;
		int limit = 200;
		while (true) {
			toSaveList = urlParserInfoList.stream().skip((pageNo - 1) * limit).limit(limit).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(toSaveList)) {
				break;
			}
			urlParserInfoMapper.insertBatch(toSaveList);
			num += toSaveList.size();
			pageNo++;
			websocket.put("nums", BigDecimal.valueOf(num).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);
		}
	}

}