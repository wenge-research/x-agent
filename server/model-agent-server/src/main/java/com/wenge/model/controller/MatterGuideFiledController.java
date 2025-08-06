package com.wenge.model.controller;

import com.wenge.model.dto.param.MatterFiledInfoParam;
import com.wenge.model.dto.result.AllFieldsResult;
import com.wenge.model.dto.result.MatterGuideFormResult;
import com.wenge.model.entity.MatterGuideFiled;
import com.wenge.model.service.MatterGuideFiledService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.wos.exception.WosException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/matterGuideFiled")
@Slf4j
public class MatterGuideFiledController {


	@Autowired
	private MatterGuideFiledService matterGuideFiledService;

	@ApiOperation(value = "获取表单数据")
	@PostMapping("/getFormInfo")
	public Result getFormInfo(@RequestBody MatterFiledInfoParam param) {
		return matterGuideFiledService.getFormInfo(param);
	}

	@ApiOperation(value = "获取表单数据-新")
	@PostMapping("/getFormInfoNew")
	public Result getFormInfoNew(@RequestBody MatterFiledInfoParam param) {
		return matterGuideFiledService.getFormInfoNew(param);
	}

	@PostMapping("/uploadPic")
	public Result uploadPic(MultipartFile file) throws WosException {
		return matterGuideFiledService.uploadPic(file);
	}


	/**
	 * @author: caohaifeng
	 * @date: 2024/10/25 14:32
	 * @Description: 上传营业执照照片
	 * @Version:1.0
	 **/
	@PostMapping("/uploadYYZZPic")
	public Result uploadYYZZPic(MultipartFile file) throws WosException {
		return matterGuideFiledService.uploadYYZZPic(file);
	}

	/**
	 * 获取表字段
	 * @param matterId
	 * @return
	 */
	@PostMapping("/getTableFileds")
	public Result getTableFields(@RequestBody StringParam matterId) {
		return matterGuideFiledService.getTableFields(matterId);
	}

	/**
	 * 获取检索字段
	 */
	@PostMapping("/getSearchFileds")
	public Result getSearchFields(@RequestBody StringParam matterId) {
		return matterGuideFiledService.getSearchFields(matterId);
	}

	/**
	 * 获取所有字段
	 */
	@PostMapping("/getAllFields")
	public Result<List<AllFieldsResult>> getAllFields(@RequestBody StringParam matterId) {
		return matterGuideFiledService.getAllFields(matterId);
	}

	/**
	 * 编辑字段
	 */
	@PostMapping("/editFields")
	public Result editFields(@RequestBody List<MatterGuideFiled> filedList) {
		return matterGuideFiledService.addFields(filedList);
	}


	/**
	 * 检查名称是否存在
	 */
	@PostMapping("/checkNameCodeExists")
	public Result checkNameCodeExists(@RequestBody MatterFiledInfoParam param) {
		return matterGuideFiledService.checkNameCodeExists(param);
	}

	/**
	 * 获取表单数据-新场景
	 * @param param
	 * @return
	 */
	@ApiOperation(value = "获取表单数据-新场景")
	@PostMapping("/getMatterGuideForm")
	public Result<MatterGuideFormResult> getMatterGuideForm(@RequestBody MatterFiledInfoParam param) {
		return matterGuideFiledService.getMatterGuideForm(param);
	}

}