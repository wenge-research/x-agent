package com.wenge.model.controller;

import com.wenge.model.dto.param.PresetQuestionPageParam;
import com.wenge.model.entity.PresetQuestion;
import com.wenge.model.service.PresetQuestionService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 预置问题接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 14:01:21
 *
 */
@RestController
@RequestMapping("/presetQuestion")
@Slf4j
public class PresetQuestionController {

	/**
	 * 	预置问题服务类
	 */
	@Autowired
	private PresetQuestionService presetQuestionService;

	/**
	 * 新增预置问题
	 */
	@PostMapping("/addPresetQuestion")
	public Result addPresetQuestion(@RequestBody PresetQuestion presetQuestion) {
		return presetQuestionService.addPresetQuestion(presetQuestion);
	}

	/**
	 * 查询预置问题列表
	 */
	@PostMapping("/getPresetQuestionList")
	public Result getPresetQuestionList(@RequestBody PresetQuestionPageParam presetQuestion) {
		return presetQuestionService.getPresetQuestionList(presetQuestion);
	}

	/**
	 * 更新预置问题
	 */
	@PostMapping("/updatePresetQuestion")
	public Result updatePresetQuestion(@RequestBody PresetQuestion presetQuestion) {
		return presetQuestionService.updatePresetQuestion(presetQuestion);
	}

	/**
	 * 删除预置问题
	 */
	@PostMapping("/deletePresetQuestion")
	public Result deletePresetQuestion(@RequestBody List<String> idList) {
		return presetQuestionService.deletePresetQuestion(idList);
	}

}