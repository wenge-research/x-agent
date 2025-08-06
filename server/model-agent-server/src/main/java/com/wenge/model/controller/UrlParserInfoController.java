package com.wenge.model.controller;

import com.wenge.model.dto.param.ParserInfoDeleteParam;
import com.wenge.model.dto.param.ParserInfoPageParam;
import com.wenge.model.dto.param.UrlParserInfoDetailParam;
import com.wenge.model.dto.param.importUrlParserDataParam;
import com.wenge.model.entity.UrlParserInfo;
import com.wenge.model.service.UrlParserInfoService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/urlParserInfo")
@Slf4j
public class UrlParserInfoController {

	@Autowired
	private UrlParserInfoService urlParserInfoService;

	/**
	 * 查询文件列表
	 */
	@PostMapping("/getList")
	public Result getList(@RequestBody ParserInfoPageParam param) {
		return urlParserInfoService.getList(param);
	}

	/**
	 * 更新url
	 */
	@PostMapping("/update")
	public Result update(@RequestBody UrlParserInfo param) {
		// 简单处理防止""存日期报错
		if (StringUtils.isBlank(param.getPeriodEnd())) {
			param.setPeriodEnd(null);
		}
		if (StringUtils.isBlank(param.getPeriodStart())) {
			param.setPeriodStart(null);
		}
		return urlParserInfoService.update(param);
	}


	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public Result delete(@RequestBody ParserInfoDeleteParam param) {
		return urlParserInfoService.deleteBatch(param);
	}

	/**
	 * 查询详情
	 */
	@PostMapping("/getDetail")
	public Result getDetail(@RequestBody UrlParserInfoDetailParam param) {
		try {
			return urlParserInfoService.getDetail(param);
		} catch (IOException e) {
			return Result.fail();
		}
	}

	/**
	 * 导入知识库数据
	 *
	 * @return
	 */
	@ApiOperation(value = "导入知识库url数据",tags = "导入知识库url数据", notes = "导入知识库url数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/importUrlParserInfoData")
	@OperaLogs
	public Result<String> importUrlParserInfoData(importUrlParserDataParam param) {
		return urlParserInfoService.importUrlParserInfoData(param);
	}

	/**
	 * 下载模板
	 *
	 * @return
	 */
	@ApiOperation(value = "下载模板 @return",tags = "下载模板 @return", notes = "下载模板 @return", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@GetMapping("/downloadKnowledgeUrlTemp")
	@OperaLogs
	public void downloadKnowledgeUrlTemp(HttpServletResponse response) {
		urlParserInfoService.downloadKnowledgeUrlTemp(response);
	}


}