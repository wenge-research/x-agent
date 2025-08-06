package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.AiImageResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.ApplicationPublishRecord;
import com.wenge.model.service.ApplicationApiMarkdownService;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.ApplicationPublishRecordService;
import com.wenge.model.service.PresetQuestionService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Description: 应用信息接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
@Api(tags = "Description: 应用信息接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-03 19:02:51")
@RestController
@RequestMapping("/applicationInfo")
@Slf4j
public class ApplicationInfoController {

	/**
	 * 	应用信息服务类
	 */
	@Autowired
	private ApplicationInfoService applicationInfoService;

	@Autowired
	private ApplicationPublishRecordService applicationPublishRecordService;

	@Autowired
	private ApplicationApiMarkdownService applicationApiMarkdownService;

	@Autowired
	private PresetQuestionService presetQuestionService;

	public static Map<String, String> APP_UPDATE_MAP = new ConcurrentHashMap<>();

	/**
	 * 新增应用信息
	 */
    @ApiOperation(value = "新增应用信息",tags = "新增应用信息", notes = "新增应用信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addApplicationInfo")
	@OperaLogs
	public Result<ApplicationInfo> addApplicationInfo(@RequestBody ApplicationInfo applicationInfo) {
		Result<ApplicationInfo> result = Result.success(new ApplicationInfo());
		try {
			// 缓存应用 id，避免应用信息更新时，同步修改应用配置
			if (StringUtils.isNotBlank(applicationInfo.getApplicationId())) {
				APP_UPDATE_MAP.put(applicationInfo.getApplicationId(), StringConstant.ONE);
			}
			result = applicationInfoService.addApplicationInfo(applicationInfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (StringUtils.isNotBlank(applicationInfo.getApplicationId())) {
				APP_UPDATE_MAP.remove(applicationInfo.getApplicationId());
			}
		}
		return result;
	}

	/**
	 * 查询应用信息列表
	 */
    @ApiOperation(value = "查询应用信息列表",tags = "查询应用信息列表", notes = "查询应用信息列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationInfoList")
	public Result<Page<ApplicationInfo>> getApplicationInfoList(@RequestBody ApplicationInfoPageParam applicationInfo) {
		return applicationInfoService.getApplicationInfoList(applicationInfo);
	}

	/**
	 * 更新应用信息
	 */
    @ApiOperation(value = "更新应用信息",tags = "更新应用信息", notes = "更新应用信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateApplicationInfo")
	@OperaLogs
	public Result updateApplicationInfo(@RequestBody ApplicationInfo applicationInfo) {
		return applicationInfoService.updateApplicationInfo(applicationInfo);
	}

	/**
	 * 下架应用
	 */
	@ApiOperation(value = "更新应用上架状态",tags = "更新应用上架状态", notes = "更新应用上架状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateApplicationPublishAppStoreState")
	@OperaLogs
	public Result updateApplicationPublishAppStoreState(@RequestBody ApplicationPublishAppStoreUpdateParam param) {
		return applicationInfoService.updateApplicationPublishAppStoreState(param);
	}

	/**
	 * 绑定知识库
	 */
    @ApiOperation(value = "绑定知识库",tags = "绑定知识库", notes = "绑定知识库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/bindingKnowledge")
	@OperaLogs
	public Result bindingKnowledge(@RequestBody ApplicationKnowledgeBindedParam param) {
		return applicationInfoService.bindingKnowledge(param);
	}

	/**
	 * 删除应用信息
	 */
    @ApiOperation(value = "删除应用信息",tags = "删除应用信息", notes = "删除应用信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteApplicationInfo")
	@OperaLogs
	public Result deleteApplicationInfo(@RequestBody StringParam param) {
		return applicationInfoService.deleteApplicationInfo(param);
	}

	/**
	 * 查询应用详细
	 */
	@ApiOperation(value = "查询应用详细", tags = "查询应用详细", notes = "查询应用详细", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationDetail/{appCode}")
	@OperaLogs
	public Result<ApplicationInfo> getApplicationDetail(@RequestBody StringParam applicationCode, @PathVariable("appCode") String appCode) {
		if (StringUtils.isBlank(applicationCode.getParam())) {
			applicationCode.setParam(appCode);
		}
		return applicationInfoService.getApplicationDetail(applicationCode);
	}

	/**
	 * 查询应用详细
	 */
	@ApiOperation(value = "查询应用详细", tags = "查询应用详细", notes = "查询应用详细", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationDetail")
	@OperaLogs
	public Result<ApplicationInfo> getApplicationDetail(@RequestBody StringParam applicationCode) {
		return applicationInfoService.getApplicationDetail(applicationCode);
	}

	/**
	 * 查询应用详细
	 */
	@ApiOperation(value = "查询应用详细", tags = "查询应用详细", notes = "查询应用详细", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@GetMapping("/getApplicationDetailByAppId")
	@OperaLogs
	public Result getApplicationDetailByAppId(@RequestParam("applicationId") String applicationId) {
		final ApplicationInfo byAppId = applicationInfoService.getByAppId(applicationId);
		if(byAppId==null){
			return Result.error("应用不存在");
		}
		StringParam stringParam = new StringParam();
		stringParam.setParam(byAppId.getApplicationCode());
		return applicationInfoService.getApplicationDetail(stringParam);
	}

	/**
	 * 复制应用
	 */
	@ApiOperation(value = "复制应用",tags = "复制应用", notes = "复制应用", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/copyApp")
	public Result copyApp(@RequestBody CopyAppParam param) {
		return applicationInfoService.copyApp(param);
	}

	/**
	 * 获取默认应用
	 */
	@ApiOperation(value = "获取默认应用",tags = "获取默认应用", notes = "获取默认应用", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getDefaultApp")
	public Result<ApplicationInfo> getDefaultApp(@RequestBody EmptyParam param) {
		return applicationInfoService.getDefaultApp(param);
	}

	/**
	 * 生成指令
	 */
	@ApiOperation(value = "生成指令",tags = "生成指令", notes = "生成指令", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/generatePrompt")
	public SseEmitter generatePrompt(@RequestBody PromptGenerateParam param) {
		return applicationInfoService.generatePrompt(param);
	}

	/**
	 * AI图片
	 */
	@ApiOperation(value = "AI图片",tags = "AI图片", notes = "AI图片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/aiImage")
	public Result<String> aiImage(@RequestBody PromptGenerateParam param) {
		return applicationInfoService.aiImage(param);
	}



	/**
	 * 根据应用ID查询应用发布记录
	 */
	@ApiOperation(value = "根据应用ID查询应用发布记录",tags = "根据应用ID查询应用发布记录", notes = "根据应用ID查询应用发布记录", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getAppPublishRecordList")
	public Result<Page<ApplicationPublishRecord>> getAppPublishRecordList(@RequestBody ApplicationInfoPageParam applicationInfo) {
		return applicationPublishRecordService.getAppPublishRecordList(applicationInfo);
	}


	/**
	 * 返回md格式的API文档说明-私有发布对接
	 */
	@ApiOperation(value = "返回md格式的API文档说明",tags = "返回md格式的API文档说明", notes = "返回md格式的API文档说明", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	@GetMapping("/getApplicationMarkdownApiDesc")
	public Result getApplicationMarkdownApiDesc() {
		return Result.success(applicationApiMarkdownService.getApplicationApiMarkdown());
	}


	/**
	 * 应用导出xlsx文件
	 */
	@ApiOperation(value = "应用导出xlsx文件",tags = "应用导出xlsx文件", notes = "应用导出xlsx文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/export")
	public Result export(@RequestBody ApplicationInfo applicationInfo,HttpServletResponse response) throws IOException {
		return applicationInfoService.export(applicationInfo,response);
	}
	/**
	 * 应用导入xlsx文件
	 */
	@ApiOperation(value = "应用导入xlsx文件",tags = "应用导入xlsx文件", notes = "应用导入xlsx文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/importApp")
	public Result importApp(MultipartFile file) throws IOException {
		return applicationInfoService.importApp(file);
	}

	/**
	 * AI生成图片
	 */
	@ApiOperation(value = "AI图片",tags = "AI图片", notes = "AI图片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/generaImage")
	public Result<AiImageResult> generaImage(@RequestBody PromptGenerateParam param) {
		return applicationInfoService.generaImage(param);
	}

	/**
	 * AI视频
	 */
	@ApiOperation(value = "AI视频",tags = "AI视频", notes = "AI视频", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/aiVideo")
	public Result<String> aiVideo(@RequestBody AiVideoParam param) {
		return applicationInfoService.aiVideo(param);
	}

	/**
	 * AI音频
	 */
	@ApiOperation(value = "AI音频",tags = "AI音频", notes = "AI音频", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/aiAudio")
	public Result<String> aiAudio(@RequestBody AiAudioParam param) {
		return applicationInfoService.aiAudio(param);
	}

	/**
	 * AI生成预设问题
	 */
	@PostMapping("/getQuestionByAI")
	public Result getPresetQuestionByAI(@RequestBody PromptGenerateParam param) {
		return presetQuestionService.getPresetQuestionByAI(param);
	}
}