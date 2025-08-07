package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.service.LlmInfoService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.ListStringParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 大模型信息表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-14 14:53:29
 *
 */
@RestController
@RequestMapping("/llmInfo")
@Slf4j
public class LlmInfoController {

	/**
	 * 	大模型信息表服务类
	 */
	@Autowired
	private LlmInfoService llmInfoService;

	/**
	 * 查询大模型信息表列表
	 */
	@PostMapping("/getLlmInfoList")
	public Result getLlmInfoList(@RequestBody LlmInfoListParam param) {
		return llmInfoService.getLlmInfoList(param);
	}

	/**
	 * 查询大模型厂商列表
	 */
	@PostMapping("/getLlmManufacturer")
	public Result<List<String>> getLlmManufacturer(@RequestBody EmptyParam param) {
		return llmInfoService.getLlmManufacturer(param);
	}

	/**
	 * 查询大模型分页信息表列表
	 */
	@PostMapping("/getLlmPageList")
	public Result<Page<LlmInfo>> getLlmPageList(@RequestBody LlmPageListParam param) {
		return llmInfoService.getLlmPageList(param);
	}

	/**
	 * 编辑大模型信息
	 */
	@PostMapping("/editLlm")
	public Result editLlm(@RequestBody LlmInfo llmInfo) {
		return llmInfoService.editLlm(llmInfo);
	}

	/**
	 * 删除大模型信息
	 */
	@PostMapping("/deleteLlm")
	public Result deleteLlm(@RequestBody ListStringParam idList) {
		return llmInfoService.deleteLlm(idList);
	}


	/**
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:24
	 * @Description: 查询大模型信息表列表-分页
	 * @Version:1.0
	 **/
	@PostMapping("/getLlmInfoPageList")
	public Result getLlmInfoPageList(@RequestBody LlmInfoPageParam llmInfoPageParam) {
		if (llmInfoPageParam.getPageNo() == null) {
			llmInfoPageParam.setPageNo(1);
		}
		if (llmInfoPageParam.getPageSize() == null) {
			llmInfoPageParam.setPageSize(10);
		}
		return llmInfoService.getLlmInfoPageList(llmInfoPageParam);
	}


	/**
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:24
	 * @Description: 添加模型接口
	 * @Version:1.0
	 **/
	@PostMapping("/addLlmInfo")
	public Result addLlmInfo(@RequestBody LlmInfoAddUpdateParam llmInfo) {
		return llmInfoService.addLlmInfo(llmInfo);
	}

	/**
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:24
	 * @Description: 编辑模型接口
	 * @Version:1.0
	 **/
	@PostMapping("/editLlmInfo")
	public Result editLlmInfo(@RequestBody LlmInfoAddUpdateParam llmInfo) {
		if (llmInfo.getId() == null) {
			return Result.fail("编辑模型，参数id不能为空");
		}
		return llmInfoService.editLlmInfo(llmInfo);
	}

	/**
	 * @author: caohaifeng
	 * @date: 2024/8/14 14:24
	 * @Description: 删除模型接口
	 * @Version:1.0
	 **/
	@GetMapping("/delLlmInfoById")
	public Result delLlmInfoById(@RequestParam(value = "id", required = true) Long id) {
		return llmInfoService.delLlmInfoById(id);
	}

	/**
	 * 一键部署
	 * @date: 2024/8/14 14:24
	 * @Description: 一键部署
	 * @Version:1.0
	 **/
	@PostMapping("/deployLlm")
	public void deployLlm(@RequestBody LlmDeployParam llmDeployParam, HttpServletResponse response) {
		llmInfoService.deployLlm(llmDeployParam, response);
	}

	/**
	 * 测试连接服务器
	 *
	 * @date: 2024/8/14 14:24
	 * @Description: 测试连接服务器
	 * @Version:1.0
	 **/
	@PostMapping("/testConnectLinux")
	public Result testConnectLinux(@RequestBody LlmDeployParam llmDeployParam) {
		return llmInfoService.testConnectLinux(llmDeployParam);
	}

	/**
	 * 将大模型设为预设
	 * @param llmInfoPageParam
	 * @return Result
	 */
	@PostMapping("/setPreset")
	public Result setPreset(@RequestBody LlmInfoPageParam llmInfoPageParam) {
		return llmInfoService.setPreset(llmInfoPageParam);
	}


}