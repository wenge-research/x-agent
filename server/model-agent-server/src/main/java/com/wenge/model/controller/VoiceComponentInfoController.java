package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.TemplateStatusUpdateParam;
import com.wenge.model.dto.param.VoiceComponentInfoDeleteParam;
import com.wenge.model.dto.param.VoiceComponentInfoPageParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.service.VoiceComponentInfoService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.wos.exception.WosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 语音组件接口
 * @author DELL
 */
@RestController
@RequestMapping("/voiceComponentInfo")
@Slf4j
public class VoiceComponentInfoController {

	@Autowired
	private VoiceComponentInfoService componentInfoService;


	/**
	 * 查询语音组件
	 * @param param
	 * @return
	 */
	@PostMapping("/getList")
	public Result<Page<VoiceComponentInfo>> getList(@RequestBody VoiceComponentInfoPageParam param) {
		return componentInfoService.getList(param);
	}


	/**
	 * 新增和修改语音组件
	 * @param param
	 * @return
	 */
	@PostMapping("/addInfo")
	public Result addInfo(@RequestBody VoiceComponentInfo param) {
		return componentInfoService.addInfo(param);
	}

	/**
	 * 删除语音组件
	 * @param param
	 * @return
	 */
	@PostMapping("/deleteInfo")
	public Result deleteInfo(@RequestBody VoiceComponentInfoDeleteParam param) {
		return componentInfoService.deleteByIds(param);
	}

	/**
	 * 修改语音组件
	 * @param param
	 * @return
	 */
	@PostMapping("/updateInfo")
	public Result updateInfo(@RequestBody VoiceComponentInfo param) {
		return componentInfoService.update(param);
	}

	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@GetMapping("/getDetail/{id}")
	public Result<VoiceComponentInfo> getDetail(@PathVariable(name = "id") Integer id) {
		return componentInfoService.getDetail(id);
	}

	/**
	 * 上传图片
	 * @param file
	 * @return
	 */
	@PostMapping("/uploadPic")
	public Result uploadPic(@RequestBody MultipartFile file) throws WosException {
		return componentInfoService.uploadPic(file);
	}

	/**
	 * 获取标签
	 * @param param
	 * @return
	 */
	@PostMapping("/getTtsTags")
	public Result getTtsTags(@RequestBody EmptyParam param) {
		return componentInfoService.getTtsTags(param);
	}

	/**
	 * 将语音设为预置
	 * @param param
	 * @return Result
	 */
	@PostMapping("/setPreset")
	public Result setPreset(@RequestBody VoiceComponentInfo param) {
		return componentInfoService.setPreset(param);
	}

	/**
	 * 语音表刷表
	 * @return Result
	 */
	@GetMapping("/flush")
	public Result flush() {
		return componentInfoService.flush();
	}
}