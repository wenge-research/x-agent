package com.wenge.model.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.dto.param.ApplicationQuestionRecommendParam;
import com.wenge.model.service.ApplicationAnalysisService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


/**
 * @author: caohaifeng
 * @date: 2024/8/12 11:01
 * @Description:
 * @Version:1.0
 **/
@Api(tags = "应用分析-模块控制层")
@RestController
@RequestMapping("/applicationAnalysis")
@Slf4j
public class ApplicationAnalysisController {

	/**
	 * 	应用信息服务类
	 */
	@Autowired
	private ApplicationAnalysisService applicationAnalysisService;

	/**
	 * @description: 应用统计word文档
	 * @author: yijiazheng
	 * @date: 2025/4/30
	 **/
	@ApiOperation(value = "应用统计word文档")
	@PostMapping("/applicationStatistics")
	@UmsOperationLog(description = "应用统计word文档", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result applicationStatistics(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) throws IOException, InvalidFormatException, ParseException {
		if (StringUtils.isEmpty(applicationOverviewIndicatorsParam.getApplicationId())) {
			return Result.fail("应用ID参数不能为空");
		}
		if (StringUtils.isEmpty(applicationOverviewIndicatorsParam.getApplicationName())) {
			return Result.fail("应用名称参数不能为空");
		}
		if (ObjectUtil.isEmpty(applicationOverviewIndicatorsParam.getFileImages())) {
			return Result.fail("文件图片不能为空");
		}
		String [] images=applicationOverviewIndicatorsParam.getFileImages();
		List<InputStream> imagesStream=new ArrayList<>();
		for (int i=0;i<images.length;i++) {
			String strings=images[i].replaceAll("data:image/png;base64,", "");
			byte[] fileBytes = Base64.getDecoder().decode(strings);
			// 将字节数组写入文件
			InputStream inputStream = new ByteArrayInputStream(fileBytes);
			imagesStream.add(inputStream);
		}
		return applicationAnalysisService.applicationStatistics(applicationOverviewIndicatorsParam,imagesStream,response);
	}
	/**
	 * @description: 应用概览指标获取
	 * @author: caohaifeng
	 * @date: 2024/8/12 11:03
	 **/
	@ApiOperation(value = "应用分析-查阅概览指示")
	@PostMapping("/applicationOverviewIndicators")
	@UmsOperationLog(description = "应用分析-查阅概览指示", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result applicationOverviewIndicators(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.applicationOverviewIndicators(applicationOverviewIndicatorsParam);
	}

	/**
	 * @description: 应用概览指标-某一类详细指标获取
	 * @author: caohaifeng
	 * @date: 2024/8/12 11:03
	 **/
	@ApiOperation(value = "应用分析-查阅详细指标")
	@PostMapping("/getApplicationOverviewIndicatorsByType")
	@UmsOperationLog(description = "应用分析-查阅详细指标", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationOverviewIndicatorsByType(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationOverviewIndicatorsByType(applicationOverviewIndicatorsParam);
	}

	@ApiOperation(value = "应用分析-问答质量-问答成功率")
	@PostMapping("/getApplicationOverviewIndicatorsBySuccess")
	@UmsOperationLog(description = "应用分析-查阅详细指标-成功率", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationOverviewIndicatorsBySuccess(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationOverviewIndicatorsByTypeSuccess(applicationOverviewIndicatorsParam);
	}

	@ApiOperation(value = "应用分析-问答质量-问答满意度")
	@PostMapping("/getApplicationOverviewIndicatorsBySelect")
	@UmsOperationLog(description = "应用分析-查阅详细指标-满意度", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationOverviewIndicatorsBySelect(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null
				|| applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationOverviewIndicatorsBySelect(applicationOverviewIndicatorsParam);
	}

	@ApiOperation(value = "应用分析-查阅详细指标-用户活跃度")
	@PostMapping("/getApplicationActiveUser")
	@UmsOperationLog(description = "应用分析-查阅详细指标-用户活跃度", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationActiveUser(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null
				|| applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationActiveUser(applicationOverviewIndicatorsParam);
	}

	/**
	 * @description: 应用概览指标-某一类详细指标获取-问答质量
	 * @author: caohaifeng 应用概览指标-某一类详细指标获取-问答质量
	 * @date: 2025/1/6 15:25
	 **/
	@ApiOperation(value = "应用分析-问答质量-查阅详细指标")
	@PostMapping("/getApplicationOverviewQualityByType")
	@UmsOperationLog(description = "应用分析-问答质量-查阅详细指标", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationOverviewQualityByType(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationOverviewQualityByType(applicationOverviewIndicatorsParam);
	}

	@ApiOperation(value = "应用分析-导出问答质量-查阅详细指标")
	@PostMapping("/getApplicationOverviewQualityByTypeExport")
	@UmsOperationLog(description = "应用分析-导出问答质量-查阅详细指标", logType = 5, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public void getApplicationOverviewQualityByTypeExport(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return;
		}
		applicationAnalysisService.getApplicationOverviewQualityByTypeExport(applicationOverviewIndicatorsParam, response);
	}


	/**
	 * @description: 问题排行榜Top50
	 * @author: caohaifeng
	 * @date: 2024/8/12 18:13
	 **/
	@ApiOperation(value = "应用分析-查阅问题排行榜Top50/topN")
	@PostMapping("/questionChartsTop50")
	@UmsOperationLog(description = "应用分析-查阅问题排行榜Top50/topN", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result questionChartsTop50(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.questionChartsTop50(applicationOverviewIndicatorsParam);
	}

	/**
	 * @description: 智能搜索实时推荐
	 * @author: zs
	 * @date: 2024/9/14 15:10
	 **/
	@ApiOperation(value = "智能搜索-问题实时推荐topN")
	@PostMapping("/questionRealtimeRecommendTopN")
	public Result questionRealtimeRecommendTopN(@RequestBody ApplicationQuestionRecommendParam param) {
		if (param == null || param.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.questionRealtimeRecommendTopN(param);
	}



	/**
	 * @description: 查阅应用分析-详细指标（有效率、点赞率、失败率）
	 * @author: caohaifeng
	 * @date: 2024/8/27 17:03
	 **/
	@ApiOperation(value = "应用分析-查阅详细指标（有效率、点赞率、失败率）")
	@PostMapping("/getApplicationUsageRate")
	@UmsOperationLog(description = "应用分析-查阅详细指标（有效率、点赞率、失败率）", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationUsageRate(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationUsageRate(applicationOverviewIndicatorsParam);
	}


	@GetMapping("/getTest")
	public Result getTest() {
		return Result.success("执行成功");
	}



//	--------------------------------问答质量 统计------------------------------------------------

	/**
	 * @description: 应用分析-查阅点赞点踩数排名（部门维度）
	 * @author: caohaifeng
	 * @date: 2024/9/18 14:04
	 **/
	@ApiOperation(value = "应用分析-查阅点赞点踩数排名（部门维度）")
	@PostMapping("/getApplicationLikeStepRanking")
	@UmsOperationLog(description = "应用分析-查阅点赞点踩数排名", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationLikeStepRanking(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationLikeStepRanking(applicationOverviewIndicatorsParam);
	}

	/**
	 * @description: 应用分析-查阅对话日志审核数排名（部门维度）
	 * @author: caohaifeng
	 * @date: 2024/9/18 14:04
	 **/
	@ApiOperation(value = "应用分析-查阅对话日志审核数排名（部门维度）")
	@PostMapping("/getApplicationDialogueReviewRanking")
	@UmsOperationLog(description = "应用分析-查阅对话日志审核数排名", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationDialogueReviewRanking(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationDialogueReviewRanking(applicationOverviewIndicatorsParam);
	}

	/**
	 * @description: 应用分析-查阅知识库问答QA对添加数排名（部门维度）
	 * @author: caohaifeng
	 * @date: 2024/9/18 14:04
	 **/
	@ApiOperation(value = "应用分析-查阅知识库问答QA对添加数排名（部门维度）")
	@PostMapping("/getApplicationAddQARanking")
	@UmsOperationLog(description = "应用分析-查阅知识库问答QA对添加数排名", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationAddQARanking(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationAddQARanking(applicationOverviewIndicatorsParam);
	}


	@ApiOperation(value = "应用分析-导出点赞点踩数排名（部门维度）")
	@PostMapping("/getApplicationLikeStepRankingExport")
	@UmsOperationLog(description = "应用分析-导出点赞点踩数排名", logType = 5, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public void getApplicationLikeStepRankingExport(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
		applicationAnalysisService.getApplicationLikeStepRankingExport(applicationOverviewIndicatorsParam, response);
	}

	@ApiOperation(value = "应用分析-导出对话日志审核数排名（部门维度）")
	@PostMapping("/getApplicationDialogueReviewRankingExport")
	@UmsOperationLog(description = "应用分析-导出对话日志审核数排名", logType = 5, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public void getApplicationDialogueReviewRankingExport(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
		applicationAnalysisService.getApplicationDialogueReviewRankingExport(applicationOverviewIndicatorsParam, response);
	}

	@ApiOperation(value = "应用分析-导出知识库问答QA对添加数排名（部门维度）")
	@PostMapping("/getApplicationAddQARankingExport")
	@UmsOperationLog(description = "应用分析-导出知识库问答QA对添加数排名", logType = 5, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public void getApplicationAddQARankingExport(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
		applicationAnalysisService.getApplicationAddQARankingExport(applicationOverviewIndicatorsParam, response);
	}


	/**
	 * @description: 应用分析-查阅共计点踩数、推送公开知识库数、推送私有知识库数
	 * 查阅共计审核数（系统审核除外）、其中修正后的审核且推送公开知识库数、推送私有知识库数
	 * @author: caohaifeng
	 * @date: 2024/9/18 15:06
	 **/
	@ApiOperation(value = "应用分析-统计点踩数（推送公开、私有知识库数）审核数（修正后推送公开、私有知识库数）")
	@PostMapping("/getApplicationLikesAndReviewsCount")
	@UmsOperationLog(description = "应用分析-统计点踩数（推送公开、私有知识库数）审核数（修正后推送公开、私有知识库数）", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result getApplicationLikesAndReviewsCount(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		if (applicationOverviewIndicatorsParam == null || applicationOverviewIndicatorsParam.getApplicationId() == null) {
			return Result.fail("应用ID参数不能为空");
		}
		return applicationAnalysisService.getApplicationLikesAndReviewsCount(applicationOverviewIndicatorsParam);
	}

}
