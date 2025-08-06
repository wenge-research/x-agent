package com.wenge.oauth.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.constants.OauthContant;
import com.wenge.oauth.entity.OauthLoginLog;
import com.wenge.oauth.mapper.OauthLoginLogMapper;
import com.wenge.oauth.service.OauthLoginLogService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Description: 服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 15:48:49
 *
 */
@Service
@Slf4j
public class OauthLoginLogServiceImpl extends ServiceImpl<OauthLoginLogMapper, OauthLoginLog> implements OauthLoginLogService {

	@Value("${iam.url}")
	private String iamUrl;

	@Value("${local-server.ip}")
	private String localserverIp;

	@Value("${server.port}")
	private Long serverPort;

	@Value("${spring.profiles.active}")
	private String runEnv;


	public OauthLoginLogServiceImpl() {
	}

	/**
	 * 	数据库处理类
	 */
	@Autowired
	private OauthLoginLogMapper oauthLoginLogMapper;


	@Override
	public Result addOauthLoginLog(OauthLoginLog oauthLoginLog){
		save(oauthLoginLog);
		return Result.success();
	}

	@Override
	public Result getOauthLoginLogList(OauthLoginLog oauthLoginLog){
		return Result.success();
	}

	@Override
	public Result updateOauthLoginLog(OauthLoginLog oauthLoginLog){
		updateById(oauthLoginLog);
		return Result.success();
	}

	@Override
	public Result deleteOauthLoginLog(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public Object analyzePortalToken(String portaltoken) {
		Map<String, Object> oauthUser;
		if (StringUtils.equals(OauthContant.NO_LOGIN_FLAG, portaltoken) && StringUtils.equals(OauthContant.DEMO, runEnv)) {
			// 默认给超管权限-仅限demo环境免登录
			oauthUser = oauthLoginLogMapper.getOauthUserById(1);
		} else {
			//解析token
			final Map<String, Object> userInfoFromToken = getUserInfoFromToken(portaltoken);
			if(userInfoFromToken == null){
				return Result.fail("无效token，请登录后重试");
			}

			final Object phone = userInfoFromToken.get("phone");
			final Object userName = userInfoFromToken.get("userName");

			//根据解析的手机号获取用户信息
			oauthUser = oauthLoginLogMapper.getOauthUserByPhone(phone + "");
		}

		if(oauthUser == null){
			return Result.fail("用户不存在或没有权限");
		}

		//模拟调用login登录接口
		StringBuilder response = imitateLogin(oauthUser);

		return response == null ? Result.fail("登录异常") : response;
	}

	private StringBuilder imitateLogin(Map<String, Object> oauthUser) {
		try {
			URL url = new URL("http://" + localserverIp + ":" + serverPort + "/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			String input = "password=" + oauthUser.get("password") + "&username=" + oauthUser.get("account_name") + "&loginType=portaltoken";
			try (OutputStream os = conn.getOutputStream()) {
				byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
				os.write(inputBytes, 0, inputBytes.length);
			}
			try (java.io.BufferedReader br = new java.io.BufferedReader(
					new java.io.InputStreamReader(conn.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				log.info(response.toString());
				return response;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	private Map<String, Object> getUserInfoFromToken(String token) {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(iamUrl);
			httpGet.addHeader("Authorization", "Bearer " + token);

			HttpResponse response = httpclient.execute(httpGet);
			String responseBody = EntityUtils.toString(response.getEntity());

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> data = (Map<String, Object>) objectMapper.readValue(responseBody, Map.class);

			Integer code = (Integer) data.get("code");
			if (code != 200) {
				throw new Exception("调用认证平台获取用户信息异常，code:" + code);
			}
			return (Map<String, Object>) data.get("data");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}