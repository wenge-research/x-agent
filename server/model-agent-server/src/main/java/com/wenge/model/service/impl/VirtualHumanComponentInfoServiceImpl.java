package com.wenge.model.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.VirtualHumanComponentInfoPageParam;
import com.wenge.model.dto.param.VirtualHumanManageParam;
import com.wenge.model.entity.VirtualHumanComponentInfo;
import com.wenge.model.entity.VirtualHumanSdkList;
import com.wenge.model.entity.table.ApplicationInfoTableDef;
import com.wenge.model.mapper.VirtualHumanComponentInfoMapper;
import com.wenge.model.mapper.VirtualHumanSdkListMapper;
import com.wenge.model.service.VirtualHumanComponentInfoService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wenge.model.entity.table.VirtualHumanComponentInfoTableDef.VIRTUAL_HUMAN_COMPONENT_INFO;
import static com.wenge.model.entity.table.VirtualHumanSdkListTableDef.VIRTUAL_HUMAN_SDK_LIST;

@Slf4j
@Service
public class VirtualHumanComponentInfoServiceImpl extends ServiceImpl<VirtualHumanComponentInfoMapper, VirtualHumanComponentInfo> implements VirtualHumanComponentInfoService {

	@Resource
	private VirtualHumanComponentInfoMapper mapper;

	@Value("${xiaoice.gettoken-url}")
	private String xiaoiceGetTokenUrl;

	@Value("${xiaoice.gettoken-url}")
	private String xiaoiceDestroyTokenUrl;

	@Autowired
	private VirtualHumanSdkListMapper virtualHumanSdkListMapper;

	@Autowired
	private RedisService redisService;

	final private String XIAOBING_AuthToken_KEY = "getAuthTokenBySubscriptionkey-key";


	@Override
	public Result getInfoList(VirtualHumanComponentInfoPageParam param) {
		Wrappers<Object> wrapper = Wrappers.init()
				.where(VIRTUAL_HUMAN_COMPONENT_INFO.DELETE_FLAG.eq(0))
				.orderBy(VIRTUAL_HUMAN_COMPONENT_INFO.UPDATE_TIME.desc());
		Page<VirtualHumanComponentInfo> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrapper);
		return Result.success(page);
	}

	@Override
	public Result addInfo(VirtualHumanComponentInfo param) {
		mapper.insert(param);
		return Result.success();
	}

	@Override
	public Result delInfo(String componentId) {
		Wrappers<Object> wrappers = Wrappers.init().and(VIRTUAL_HUMAN_COMPONENT_INFO.COMPONENT_ID.eq(componentId));
		return Result.success(mapper.deleteByQuery(wrappers));
	}

	@Override
	public Result updateInfo(VirtualHumanComponentInfo param) {
		Wrappers<Object> wrappers = Wrappers.init().and(VIRTUAL_HUMAN_COMPONENT_INFO.COMPONENT_ID.eq(param.getComponentId()));
		VirtualHumanComponentInfo oldInfo = mapper.selectOneByQuery(wrappers);
		param.setId(oldInfo.getId());
		mapper.update(param);
		return Result.success();
	}

	@Override
	public Result getInfo(String componentId) {
		Wrappers<Object> wrappers = Wrappers.init().and(VIRTUAL_HUMAN_COMPONENT_INFO.COMPONENT_ID.eq(componentId));
		return Result.success(mapper.selectOneByQuery(wrappers));
	}

	/**
	 * @author: caohaifeng
	 * @date: 2024/8/8 17:30
	 * @Description: 获取所有订阅的企业subscriptionList
	 * @Version:1.0
	 **/
	@Override
	public Result getSubscriptionList() {
		Wrappers<Object> wrapper = Wrappers.init()
				.where(VIRTUAL_HUMAN_COMPONENT_INFO.DELETE_FLAG.eq(0))
				.orderBy(VIRTUAL_HUMAN_COMPONENT_INFO.UPDATE_TIME.desc());
		List<VirtualHumanComponentInfo> list = this.list(wrapper);
		return Result.success(list);
	}

	/**
	 * @param subscription
	 * @author: caohaifeng
	 * @date: 2024/8/8 17:30
	 * @Description: 生成小冰token
	 * @Version:1.0
	 */
	@Override
	public Result getAuthTokenBySubscription(String subscription) {

		Map<String, Object> redisTokenMap = redisService.get(XIAOBING_AuthToken_KEY, Map.class);
		if (redisTokenMap != null) {
			redisTokenMap.put("source", "redis-cache");
			log.info("getAuthTokenBySubscription-走redis缓存");
			return Result.success(redisTokenMap);
		}
		String httpUrl = xiaoiceGetTokenUrl + "?effectiveDurationMilliseconds=" + (24 * 60 * 60 * 1000L);
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(httpUrl);
			request.setHeader("subscription-key", subscription);
			CloseableHttpResponse response = httpClient.execute(request);
			// 获取响应状态码
			log.info("getAuthTokenBySubscriptionkey Response Code: " + response.getStatusLine().getStatusCode());
			String responseBody = EntityUtils.toString(response.getEntity());
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
			if (response.getStatusLine().getStatusCode() == 200) {
				redisService.setExpire(XIAOBING_AuthToken_KEY, jsonMap, 23 * 60 * 60L, TimeUnit.SECONDS);
			}
			return Result.success(jsonMap);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail("获取AuthToken异常，请重试");
		}
	}

	/**
	 * @param virtualHumanManageParam
	 * @description: 销毁小冰token
	 * @author: caohaifeng
	 * @date: 2024/8/9 11:36
	 */
	@SneakyThrows
	@Override
	public Result destroyAuthTokenBySubscription(VirtualHumanManageParam virtualHumanManageParam) {
		if (StringUtils.isBlank(virtualHumanManageParam.getSignature())) {
			return Result.fail("签名不能为空");
		}
		if (StringUtils.isBlank(virtualHumanManageParam.getInvalidateSignature())) {
			return Result.fail("失效签名不能为空");
		}
		String httpUrl = xiaoiceDestroyTokenUrl + "?invalidateSignature=" + URLEncoder.encode(virtualHumanManageParam.getInvalidateSignature(), StandardCharsets.UTF_8.toString());
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(httpUrl);
			request.setHeader("signature", virtualHumanManageParam.getSignature());
			CloseableHttpResponse response = httpClient.execute(request);
			// 获取响应状态码
			log.info("destroyAuthTokenBySubscription Response Code: " + response.getStatusLine().getStatusCode());
			String responseBody = EntityUtils.toString(response.getEntity());
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
			return Result.success(jsonMap);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail("销毁Token异常，请重试");
		}
	}

	/**
	 * @description: 获取可用的数字人列表
	 * @author: caohaifeng
	 * @date: 2024/10/18 10:36
	 **/
	@Override
	public Result getAvailableHumanList() {
		List<VirtualHumanSdkList> virtualHumanSdkLists = virtualHumanSdkListMapper.selectListByQuery(QueryWrapper.create().where(VIRTUAL_HUMAN_SDK_LIST.DELETE_FLAG.eq(0)));
		return Result.success(virtualHumanSdkLists);
	}

}