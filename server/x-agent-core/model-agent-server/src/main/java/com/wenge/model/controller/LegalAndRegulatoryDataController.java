package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ImportInterceptWordDataParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.dto.param.LegalAndRegulatoryDataPageParam;
import com.wenge.model.dto.param.SyncLegalAndRegulatoryDataParam;
import com.wenge.model.entity.InterceptWord;
import com.wenge.model.entity.LegalAndRegulatoryData;
import com.wenge.model.service.InterceptWordService;
import com.wenge.model.service.LegalAndRegulatoryDataService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Description:法律法规库接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
@Api(tags = "Description: 法律法规库接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-09 21:15:57")
@RestController
@RequestMapping("/legalAndRegulatory")
@Slf4j
public class LegalAndRegulatoryDataController {

	@Autowired
	private LegalAndRegulatoryDataService legalAndRegulatoryDataService;


	@ApiOperation(value = "法律法规库列表", tags = "法律法规库列表", notes = "法律法规库列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getLegalAndRegulatoryDataList")
	public Result<Page<LegalAndRegulatoryData>> getLegalAndRegulatoryDataList(@RequestBody LegalAndRegulatoryDataPageParam legalAndRegulatoryDataPageParam) {
		if (StringUtils.isBlank(legalAndRegulatoryDataPageParam.getCompanyName())) {
			legalAndRegulatoryDataPageParam.setCompanyName("wxm");
		}
		if (legalAndRegulatoryDataPageParam.getType() == null) {
			legalAndRegulatoryDataPageParam.setType(1);
		}
		return legalAndRegulatoryDataService.getLegalAndRegulatoryDataList(legalAndRegulatoryDataPageParam);
	}


	@ApiOperation(value = "调用三方接口获取法律法规数据入库", tags = "调用三方接口获取法律法规数据入库", notes = "调用三方接口获取法律法规数据入库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/synchronousLegalAndRegulatoryData")
	public Result synchronousLegalAndRegulatoryData(@RequestBody SyncLegalAndRegulatoryDataParam syncLegalAndRegulatoryDataParam) {
		if (StringUtils.isBlank(syncLegalAndRegulatoryDataParam.getCompanyName())) {
			syncLegalAndRegulatoryDataParam.setCompanyName("wxm");
		}
		if (syncLegalAndRegulatoryDataParam.getType() == null) {
			return Result.error("type不能为空 1-网信法律法规 2-网信案例库 3-网信动态 4-普法动画 ");
		}
		if (StringUtils.isBlank(syncLegalAndRegulatoryDataParam.getStart_url())) {
			return Result.error("start_url不能为空");
		}
		return legalAndRegulatoryDataService.synchronousLegalAndRegulatoryData(syncLegalAndRegulatoryDataParam);
	}
}