package com.wenge.model.controller.api;


import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.param.wenhai.WenHaiParameter;
import com.wenge.model.dto.result.AddressInfoDto;
import com.wenge.model.dto.result.McpResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.GuangXinMatter;
import com.wenge.model.entity.McpServer;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.service.SmartProviderService;
import com.wenge.model.service.YayiModelService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.redis.annotation.RedisLimit;
import com.wg.appframe.yayi.param.MathModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 对外提供智能服务
 */
@RestController
@RequestMapping("/smartApi/llmSmart")
@Slf4j
@Api(tags = "对外提供智能服务")
public class SmartProviderController {

    @Autowired
    private SmartProviderService smartProviderService;

    @Autowired
    private YayiModelService yayiModelService;

    /**
     * 关芯智巡：通过现场卫生环境状态的描述，识别属于问题的类型和指标是否存在问题
     */
    @ApiOperation(value = "关芯智巡", tags = "关芯智巡", notes = "关芯智巡", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/gxClassifyMatter")
    @OperaLogs
    public Result<String> gxClassifyMatter(@RequestBody ProviderGuanxinParam param) {
        return smartProviderService.gxClassifyMatter(param);
    }

    /**
     * 查询关芯智巡解析结果
     */
    @ApiOperation(value = "查询关芯智巡解析结果", tags = "查询关芯智巡解析结果", notes = "查询关芯智巡解析结果", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getGxMatterDetail")
    @OperaLogs
    public Result<List<GuangXinMatter>> getGxMatterDetail(@RequestBody ProviderGuanxinParam param) {
        return smartProviderService.getGxMatterDetail(param);
    }

    /**
     * 查询应用详细
     */
    @ApiOperation(value = "查询应用详细", tags = "查询应用详细", notes = "查询应用详细", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getAppDetail")
    @OperaLogs
    public Result<ApplicationInfo> getAppDetail(@RequestBody ApiAppDetailParam param, HttpServletRequest request) {
        return smartProviderService.getAppDetail(param, request);
    }

    /**
     * api流式对话
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/dialogueApiStream")
    @ApiOperation(value = "api流式对话")
    @RedisLimit(period = 10, count = 20)
    public SseEmitter dialogueApiStream(@RequestBody DialogueApiParam param, HttpServletRequest request) {
        return smartProviderService.dialogueApiStream(param, request);
    }

    /**
     * api流式对话-工作流
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/dialogueRunApiStream")
    @ApiOperation(value = "api流式对话工作流")
    @RedisLimit(period = 10, count = 20)
    public SseEmitter dialogueRunApiStream(@RequestBody DialogueRunApiParam param, HttpServletRequest request) {
        return smartProviderService.dialogueRunApiStream(param, request);
    }

    /**
     * api非流式对话-工作流
     * @param param
     * @return
     */
    @PostMapping("/dialogueRunApiString")
    @ApiOperation(value = "api非流式对话工作流")
    @RedisLimit(period = 10, count = 20)
    public Result<DialogueByStreamParam> dialogueRunApiString(@RequestBody DialogueRunApiParam param, HttpServletRequest request) {
        return smartProviderService.dialogueRunApiString(param, request);
    }

    /**
     * 通用接口
     * @param param
     * @return
     */
    @PostMapping("/comoneApi")
    public SseEmitter commonApi(@RequestBody CommonApiParam param) {
        return smartProviderService.commonApi(param);
    }
    /**
     * 解析地址
     */
    @ApiOperation(value = "关芯智巡根据内容解析地址", tags = "关芯智巡根据内容解析地址", notes = "关芯智巡根据内容解析地址", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getAddressInfo")
    @RedisLimit(period = 1, count = 50)
    @OperaLogs
    public Result<AddressInfoDto> getAddressInfo(@RequestBody GenerateAddressParam param) {
        return smartProviderService.getAddressInfo(param);
    }

    /**
     * AI审查接口
     */
    @ApiOperation(value = "AI审查接口", tags = "AI审查接口", notes = "AI审查接口", response = SseEmitter.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/aiReview")
    @OperaLogs
    public SseEmitter aiReview(@RequestBody AIReviewParam param, HttpServletRequest request) {
        return smartProviderService.aiReview(param, request);
    }

    /**
     * 雅意服务通用接口
     * @param param
     * @return
     */
    @PostMapping("/yayi")
    public JSONObject yayi(@RequestBody CommonApiParam param) {
        return smartProviderService.yayi(param);
    }

    /**
     * 调用闻海接口
     * @param param
     * @return
     */
    @PostMapping("/wenhai")
    public com.alibaba.fastjson.JSONObject wenhai(@RequestBody WenHaiParameter param) {
        return smartProviderService.wenhai(param);
    }

    /**
     * 大模型透传通用接口
     *
     * @param param
     * @return
     */
    @PostMapping("/llmCompletions")
    public SseEmitter completions(@RequestBody LlmCompletionsParam param, HttpServletRequest request) {
        return smartProviderService.completions(param, request);
    }

    /**
     * mcp对外服务
     *
     * @param param
     * @return
     */
    @PostMapping("/runMcp")
    public Result<List<McpResult>> runMcp(@RequestBody RunMcpApiParam param, HttpServletRequest request) {
        return smartProviderService.runMcp(param, request);
    }

    /**
     * 获取 mcp 服务列表
     *
     * @param param
     * @return
     */
    @PostMapping("/findMcpServerList")
    public Result<List<McpServer>> findMcpServerList(@RequestBody McpFindParam param, HttpServletRequest request) {
        return smartProviderService.findMcpServerList(param, request);
    }

    /**
     * 模型查询接口（公文写作平台专用）
     */
    @GetMapping("/queryLlmInfo")
    public Result<List<LlmInfo>> queryLlmInfo() {
        return smartProviderService.queryLlmInfo();
    }

    /**
     * 提供图表分析能力
     * @param param
     * @return
     */
    @PostMapping("/mathModel")
    public Result mathModel(@RequestBody MathModelParam param) {
        return smartProviderService.mathModel(param);
    }

    /**
     * 提供模型能力，总结大纲等，元枢智库使用
     */
    @PostMapping(value = "/summary")
    public Result summary(@RequestBody SummaryParam param) {
        return smartProviderService.summary(param);
    }

    /**
     * 解析推演，v2版本
     */
    @PostMapping(value = "/deductionV2")
    public Result deductionV2(DeductionV2Param param, HttpServletRequest request) {
        return smartProviderService.deductionV2(param, request);
    }

    /**
     * 提供保存知识库文档的接口，采集平台使用
     */
    @PostMapping("/saveKnowledgeData")
    public Result saveKnowledgeData(@RequestBody SaveCollectPlatformDataParam param) {
        return smartProviderService.saveKnowledgeData(param);
    }

    /**
     * 提供保存知识库内容的接口（采集到的url及对应的内容），采集平台使用
     */
    @PostMapping("/saveKnowledgeContentData")
    public Result saveKnowledgeContentData(@RequestBody SaveCollectPlatformContentDataParam param) {
        return smartProviderService.saveKnowledgeContentData(param);
    }

}
