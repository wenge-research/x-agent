package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.FoldersDeleteParam;
import com.wenge.model.dto.param.FoldersPageParam;
import com.wenge.model.entity.Folders;
import com.wenge.model.service.FoldersService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 文件夹 接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 17:48:54
 *
 */
@Api(tags = "Description: 文件夹 接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-07 17:48:54")
@RestController
@RequestMapping("/folders")
@Slf4j
public class FoldersController {

	/**
	 * 	文件夹 服务类
	 */
	@Autowired
	private FoldersService foldersService;

	/**
	 * 新增文件夹 
	 */
    @ApiOperation(value = "新增文件夹",tags = "新增文件夹", notes = "新增文件夹", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addFolders")
	@OperaLogs
	public Result<Folders> addFolders(@RequestBody Folders folders) {
		return foldersService.addFolders(folders);
	}

	/**
	 * 查询文件夹 列表
	 */
    @ApiOperation(value = "查询文件夹 列表",tags = "查询文件夹 列表", notes = "查询文件夹 列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getFoldersList")
	public Result<Page<Folders>> getFoldersList(@RequestBody FoldersPageParam folders) {
		return foldersService.getFoldersList(folders);
	}

	/**
	 * 更新文件夹 
	 */
    @ApiOperation(value = "更新文件夹",tags = "更新文件夹", notes = "更新文件夹", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateFolders")
	@OperaLogs
	public Result updateFolders(@RequestBody Folders folders) {
		return foldersService.updateFolders(folders);
	}

	/**
	 * 删除文件夹 
	 */
    @ApiOperation(value = "删除文件夹",tags = "删除文件夹", notes = "删除文件夹", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteFolders")
	@OperaLogs
	public Result deleteFolders(@RequestBody FoldersDeleteParam param) {
		return foldersService.deleteFolders(param);
	}

}