package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.VoiceComponentInfoConfigPageParam;
import com.wenge.model.dto.param.VoiceComponentInfoConfigUpdateParam;
import com.wenge.model.entity.VoiceComponentInfoConfig;
import com.wenge.model.service.VoiceComponentInfoConfigService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 语音组件配置接口
 * @author DELL
 */
@RestController
@RequestMapping("/voiceComponentInfoConfig")
@Api(tags = "语音组件配置接口")
@Slf4j
public class VoiceComponentInfoConfigController {

	@Autowired
	private VoiceComponentInfoConfigService componentInfoConfigService;


	/**
	 * 分页查询语音组件配置信息
	 * @param param
	 * @return
	 */
	@PostMapping("/getConfigByPage")
	public Result<Page<VoiceComponentInfoConfig>> getConfigByPage(@RequestBody VoiceComponentInfoConfigPageParam param) {
		return componentInfoConfigService.getConfigByPage(param);
	}


	/**
	 * 更新语音组件配置信息
	 * @param param
	 * @return
	 */
	@PostMapping("/updateConfig")
	public Result updateConfig(@RequestBody VoiceComponentInfoConfigUpdateParam param) {
		return componentInfoConfigService.updateConfig(param);
	}

}