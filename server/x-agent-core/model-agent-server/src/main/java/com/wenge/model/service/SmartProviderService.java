package com.wenge.model.service;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.param.wenhai.WenHaiParameter;
import com.wenge.model.dto.result.AddressInfoDto;
import com.wenge.model.dto.result.McpResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.GuangXinMatter;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.McpServer;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.yayi.param.MathModelParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SmartProviderService {

    Result<String> gxClassifyMatter(ProviderGuanxinParam param);

    Result<List<GuangXinMatter>> getGxMatterDetail(ProviderGuanxinParam param);

    Result<ApplicationInfo> getAppDetail(ApiAppDetailParam param, HttpServletRequest request);

    SseEmitter dialogueApiStream(DialogueApiParam param, HttpServletRequest request);

    SseEmitter commonApi(CommonApiParam param);

    Result<AddressInfoDto> getAddressInfo(GenerateAddressParam param);

    SseEmitter aiReview(AIReviewParam param, HttpServletRequest request);

    JSONObject yayi(CommonApiParam param);

    com.alibaba.fastjson.JSONObject wenhai(WenHaiParameter param);

    SseEmitter dialogueRunApiStream(DialogueRunApiParam param, HttpServletRequest request);

    Result<DialogueByStreamParam> dialogueRunApiString(DialogueRunApiParam param, HttpServletRequest request);

    SseEmitter completions(LlmCompletionsParam param, HttpServletRequest request);

    Result<List<McpResult>> runMcp(RunMcpApiParam param, HttpServletRequest request);

    Result<List<McpServer>> findMcpServerList(McpFindParam param, HttpServletRequest request);

    Result<List<LlmInfo>> queryLlmInfo();

    Result mathModel(MathModelParam param);

    Result summary(SummaryParam param);

    Result saveKnowledgeData(SaveCollectPlatformDataParam param);

    Result saveKnowledgeContentData(SaveCollectPlatformContentDataParam param);

    Result deductionV2(DeductionV2Param param, HttpServletRequest request);
}
