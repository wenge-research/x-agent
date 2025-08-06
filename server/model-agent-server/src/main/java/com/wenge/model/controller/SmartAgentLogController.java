package com.wenge.model.controller;

import com.wenge.model.dto.param.SmartAgentLogParam;
import com.wenge.model.entity.SmartAgentLog;
import com.wenge.model.service.SmartAgentLogService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 应用信息接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
@RestController
@RequestMapping("/smartAgentLog")
@Slf4j
public class SmartAgentLogController {

	/**
	 * 	应用信息服务类
	 */
	@Autowired
	private SmartAgentLogService agentLogService;

	/**
	 * 新增应用信息
	 */
	@PostMapping("/addSmartAgentLog")
	public Result addSmartAgentLog(@RequestBody SmartAgentLog agentLog) {
		return agentLogService.addSmartAgentLog(agentLog);
	}
	@PostMapping("/smartAgentLogList")
	public Result smartAgentLogList(@RequestBody SmartAgentLogParam smartAgentLogParam) {
		try {
			return agentLogService.smartAgentLogList(smartAgentLogParam);
		}catch (Exception e){
			e.printStackTrace();
			return Result.fail();
		}
	}
	@PostMapping("/smartAgentLogListGroupTime")
	public Result smartAgentLogListGroupTime(@RequestBody SmartAgentLogParam smartAgentLogParam) {
		try {
			return agentLogService.smartAgentLogListGroupTime(smartAgentLogParam);
		}catch (Exception e){
			e.printStackTrace();
			return Result.fail();
		}
	}
}