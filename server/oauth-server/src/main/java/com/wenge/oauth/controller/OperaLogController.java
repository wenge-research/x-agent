package com.wenge.oauth.controller;

import com.wenge.oauth.entity.OperaLog;
import com.wenge.oauth.service.OperaLogService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 操作日志接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 16:17:49
 *
 */
@RestController
@RequestMapping("/operaLog")
@Slf4j
public class OperaLogController {

	/**
	 *  操作日志服务类
	 */
	@Autowired
	private OperaLogService operaLogService;

	/**
	 * 新增操作日志
	 */
	@PostMapping("/addOperaLog")
	public Result addOperaLog(@RequestBody OperaLog operaLog) {
		return operaLogService.addOperaLog(operaLog);
	}

	/**
	 * 查询操作日志列表
	 */
	@PostMapping("/getOperaLogList")
	public Result getOperaLogList(@RequestBody OperaLog operaLog) {
		return operaLogService.getOperaLogList(operaLog);
	}

	/**
	 * 更新操作日志
	 */
	@PostMapping("/updateOperaLog")
	public Result updateOperaLog(@RequestBody OperaLog operaLog) {
		return operaLogService.updateOperaLog(operaLog);
	}

	/**
	 * 删除操作日志
	 */
	@PostMapping("/deleteOperaLog")
	public Result deleteOperaLog(@RequestBody List<String> idList) {
		return operaLogService.deleteOperaLog(idList);
	}

}