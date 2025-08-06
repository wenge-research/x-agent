package com.wenge.model.controller;


import com.wenge.model.dto.param.DialogTemplateParam;
import com.wenge.model.dto.param.PluginParam;
import com.wenge.model.dto.param.TemplateStatusUpdateParam;
import com.wenge.model.entity.DialogTemplate;
import com.wenge.model.service.DialogTemplateService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 聊天模板接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-25 17:16:27
 *
 */
@RestController
@RequestMapping("/dialogTemplate")
@Slf4j
public class DialogTemplateController {

	/**
	 * 	聊天模板服务类
	 */
	@Autowired
	private DialogTemplateService dialogTemplateService;

	/**
	 * 新增聊天模板
	 */
	@PostMapping("/addDialogTemplate")
	public Result addDialogTemplate(@RequestBody DialogTemplate dialogTemplate) {
		return dialogTemplateService.addDialogTemplate(dialogTemplate);
	}

	/**
	 * 查询聊天模板列表
	 */
	@PostMapping("/getDialogTemplateList")
	public Result<List<DialogTemplate>> getDialogTemplateList(@RequestBody DialogTemplateParam param) {
		return dialogTemplateService.getDialogTemplateList(param);
	}

	/**
	 * 更新聊天模板
	 */
	@PostMapping("/updateDialogTemplate")
	public Result updateDialogTemplate(@RequestBody DialogTemplate dialogTemplate) {
		return dialogTemplateService.updateDialogTemplate(dialogTemplate);
	}

	/**
	 * 删除聊天模板
	 */
	@PostMapping("/deleteDialogTemplate")
	public Result deleteDialogTemplate(@RequestBody List<String> idList) {
		return dialogTemplateService.deleteDialogTemplate(idList);
	}

	/**
	 * 更新聊天模板状态 0-禁用 1-启用
	 * @param param
	 * @return
	 */
	@PostMapping("/updateStatus")
	public Result updateStatus(@RequestBody TemplateStatusUpdateParam param) {
		return dialogTemplateService.updateStatus(param);
	}

	/**
	 * 将聊天模板设为预置
	 * @param param
	 * @return Result
	 */
	@PostMapping("/setPreset")
	public Result setPreset(@RequestBody TemplateStatusUpdateParam param) {
		return dialogTemplateService.setPreset(param);
	}
}