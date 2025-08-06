package com.wenge.oauth.controller;

import com.wenge.oauth.entity.OauthLoginLog;
import com.wenge.oauth.service.OauthLoginLogService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 15:48:49
 *
 */
@RestController
@RequestMapping("/oauthLoginLog")
@Slf4j
public class OauthLoginLogController {

	/**
	 * 	服务类
	 */
	@Autowired
	private OauthLoginLogService oauthLoginLogService;

	/**
	 * 新增
	 */
	@PostMapping("/addOauthLoginLog")
	public Result addOauthLoginLog(@RequestBody OauthLoginLog oauthLoginLog) {
		return oauthLoginLogService.addOauthLoginLog(oauthLoginLog);
	}

	/**
	 * 查询列表
	 */
	@PostMapping("/getOauthLoginLogList")
	public Result getOauthLoginLogList(@RequestBody OauthLoginLog oauthLoginLog) {
		return oauthLoginLogService.getOauthLoginLogList(oauthLoginLog);
	}

	/**
	 * 更新
	 */
	@PostMapping("/updateOauthLoginLog")
	public Result updateOauthLoginLog(@RequestBody OauthLoginLog oauthLoginLog) {
		return oauthLoginLogService.updateOauthLoginLog(oauthLoginLog);
	}

	/**
	 * 删除
	 */
	@PostMapping("/deleteOauthLoginLog")
	public Result deleteOauthLoginLog(@RequestBody List<String> idList) {
		return oauthLoginLogService.deleteOauthLoginLog(idList);
	}


	/**
	 * @author: caohaifeng
	 * @date: 2024/8/19 17:13
	 * @Description: 解析统一认证平台的token 免登录进入问搜平台
	 * @Version:1.0
	 **/
	@GetMapping("/analyzePortalToken")
	public Object analyzePortalToken(@RequestParam(value = "portaltoken", required = true) String portaltoken) {
		return oauthLoginLogService.analyzePortalToken(portaltoken);
	}

}