package com.wenge.model.controller;

import com.wenge.model.dto.param.MatterGuideGroupParam;
import com.wenge.model.entity.MatterGuideGroup;
import com.wenge.model.entity.MatterGuideType;
import com.wenge.model.service.LabelTypeService;
import com.wenge.model.service.MatterGuideGroupService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matterGuideGroup")
@Slf4j
public class MatterGuideGroupController {

	@Autowired
	private MatterGuideGroupService matterGuideGroupService;

	@Autowired
	private LabelTypeService labelTypeService;


	/**
	 * @description: 添加字段分组信息
	 * @author: caohaifeng
	 * @date: 2024/9/4 14:05
	 **/
	@PostMapping("/addMatterGuideGroup")
	public Result addMatterGuideGroup(@RequestBody MatterGuideGroup matterGuideGroup) {
		return matterGuideGroupService.addMatterGuideGroup(matterGuideGroup);
	}

	/**
	 * 添加或者编辑字段分组信息
	 * @param matterGuideGroupList
	 * @return
	 */
	@PostMapping("/addOrUpdateMatterGroupList")
	public Result addOrUpdateMatterGroupList(@RequestBody List<MatterGuideGroup> matterGuideGroupList) {
		return matterGuideGroupService.addOrUpdateMatterGroupList(matterGuideGroupList);
	}

	/**
	 * @description: 获取所有的字段分组信息
	 * @author: caohaifeng
	 * @date: 2024/8/16 18:01
	 **/
	@ApiOperation(value = "获取所有的字段分组信息", tags = "获取所有的字段分组信息", notes = "获取所有的字段分组信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getMatterGuideGroupList")
	public Result<List<MatterGuideGroup>> getMatterGuideGroupList(@RequestBody MatterGuideGroupParam matterGuideGroupParam) {
		return matterGuideGroupService.getMatterGuideGroupList(matterGuideGroupParam);
	}

	/**
	 * @description: 根据id删除分组信息
	 * @author: caohaifeng
	 * @date: 2024/8/16 18:01
	 **/
	@ApiOperation(value = "根据id删除分组信息", tags = "根据id删除分组信息", notes = "根据id删除分组信息", response = Result.class, httpMethod = "GET")
	@GetMapping("/delMatterGuideGroupById")
	public Result delMatterGuideGroupById(@RequestParam("id") Long id) {
		return matterGuideGroupService.delMatterGuideGroupById(id);
	}



	/**
	 * @description: 获取所有事项分组类型
	 * @author: caohaifeng
	 * @date: 2024/8/16 18:01
	 **/
	@ApiOperation(value = "获取所有事项分组类型", tags = "获取所有事项分组类型", notes = "获取所有事项分组类型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getMatterGuideTypeList")
	public Result getMatterGuideTypeList(@RequestBody MatterGuideType matterGuideType) {
		return matterGuideGroupService.getMatterGuideTypeList(matterGuideType);
	}


	/**
	 * @description: 获取所有标签类型-插件/应用
	 * @author: caohaifeng
	 **/
	@ApiOperation(value = "获取所有标签类型", tags = "获取所有标签类型", notes = "获取所有标签类型", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	@GetMapping("/getLabelTypes")
	public Result getLabelTypes(@RequestParam(value = "type", defaultValue = "1") Integer type) {
		return Result.success(labelTypeService.getLabelTypes(type));
	}

	/**
	 * @description: 获取所有标签类型-应用商店
	 * @author: caohaifeng
	 **/
	@ApiOperation(value = "获取所有标签类型", tags = "获取所有标签类型", notes = "获取所有标签类型", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	@GetMapping("/getAppLabelTypes")
	public Result getAppLabelTypes() {
		return Result.success(labelTypeService.getLabelTypes(2));
	}





}