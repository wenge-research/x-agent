package com.wenge.model.controller;

import cn.hutool.db.PageResult;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ImportInterceptWordDataParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.dto.param.LlmInfoPageParam;
import com.wenge.model.dto.param.importKnowledgeDataParam;
import com.wenge.model.entity.InterceptWord;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.service.InterceptWordService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Description: 拦截词接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
@Api(tags = "Description: 拦截词接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-09 21:15:57")
@RestController
@RequestMapping("/interceptWord")
@Slf4j
public class InterceptWordController {

	/**
	 * 	拦截词服务类
	 */
	@Autowired
	private InterceptWordService interceptWordService;

	/**
	 * 新增拦截词
	 */
    @ApiOperation(value = "新增拦截词",tags = "新增拦截词", notes = "新增拦截词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addInterceptWord")
	@OperaLogs
	public Result addInterceptWord(@RequestBody InterceptWord interceptWord) {
		return interceptWordService.addInterceptWord(interceptWord);
	}

	/**
	 * 查询拦截词列表
	 */
    @ApiOperation(value = "查询拦截词列表",tags = "查询拦截词列表", notes = "查询拦截词列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getInterceptWordList")
	public Result<Page<InterceptWord>> getInterceptWordList(@RequestBody InterceptWordPageParam interceptWord) {
		return interceptWordService.getInterceptWordList(interceptWord);
	}

	/**
	 * 更新拦截词
	 */
    @ApiOperation(value = "更新拦截词",tags = "更新拦截词", notes = "更新拦截词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateInterceptWord")
	@OperaLogs
	public Result updateInterceptWord(@RequestBody InterceptWord interceptWord) {
		return interceptWordService.updateInterceptWord(interceptWord);
	}

	/**
	 * 删除拦截词
	 */
    @ApiOperation(value = "删除拦截词",tags = "删除拦截词", notes = "删除拦截词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteInterceptWord")
	@OperaLogs
	public Result deleteInterceptWord(@RequestBody List<String> idList) {
		return interceptWordService.deleteInterceptWord(idList);
	}

	/**
	 * 关闭-开启
	 * @param interceptWord
	 * @return
	 */
	@PostMapping("/updateStatus")
	public Result updateStatus(@RequestBody InterceptWord interceptWord) {
		return interceptWordService.updateStatus(interceptWord);
	}


	/**
	 * 敏感词类型列表
	 * @return
	 */
	@GetMapping("/getInterceptWordTypeList")
	public Result getInterceptWordTypeList() {
		return interceptWordService.getInterceptWordTypeList();
	}

	/**
	 * 敏感词处理方式下拉框列表
	 * @return
	 */
	@GetMapping("/getInterceptWordHandlingMethodList")
	public Result getInterceptWordHandlingMethodList() {
		return interceptWordService.getInterceptWordHandlingMethodList();
	}


	/**
	 * 导入敏感词数据
	 *
	 * @return
	 */
	@ApiOperation(value = "导入敏感词数据", tags = "导入敏感词数据", notes = "导入敏感词数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/importInterceptWordData")
	@OperaLogs
	public Result<String> importInterceptWordData(ImportInterceptWordDataParam param) throws IOException {
		return interceptWordService.importInterceptWordData(param);
	}

	@ApiOperation(value = "下载导入敏感词模板",tags = "下载导入敏感词模板", notes = "下载导入敏感词模板", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@GetMapping("/downloadInterceptWordDataTemp")
	@OperaLogs
	public void downloadInterceptWordDataTemp(HttpServletResponse response) {
		interceptWordService.downloadInterceptWordDataTemp(response);
	}




}