package com.wenge.model.controller;

import com.wenge.model.dto.param.AddMatterGuideDataParam;
import com.wenge.model.service.MatterGuideDataService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/matterGuideData")
@Slf4j
public class MatterGuideDataController {


	@Autowired
	private MatterGuideDataService matterGuideDataService;

	@ApiOperation(value = "提交表单信息")
	@PostMapping("/submitFormInfo")
	public Result submitFormInfo(@RequestBody AddMatterGuideDataParam param) {
		try {
			return matterGuideDataService.submitFormInfo(param);
		}catch (Exception e){
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
}