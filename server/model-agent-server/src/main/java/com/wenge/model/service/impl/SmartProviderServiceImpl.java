package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.AddressInfoConstant;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.KnowledgeConstant;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.param.wenhai.WenHaiHeader;
import com.wenge.model.dto.param.wenhai.WenHaiParameter;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.*;
import com.wenge.model.enums.DialogueVerifyStatusEnum;
import com.wenge.model.enums.GuangXinMatterEnum;
import com.wenge.model.enums.SmartAgentApiTypeEnum;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.mapper.ComponentNodeRelMapper;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.mapper.es.SmartAgentLlmApiRecordMapper;
import com.wenge.model.service.*;
import com.wenge.model.strategy.answer.McpStrategy;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.*;
import com.wenge.model.workflow.component.dto.WorkflowRunStatus;
import com.wenge.model.workflow.component.file.FileTransferMethod;
import com.wenge.model.workflow.component.file.FileType;
import com.wenge.model.workflow.constants.SettingConstants;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.ComponentNodeRel;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.ResultCodeBase;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.CommoneLLServer;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.CommoneCompletionParam;
import com.wg.appframe.yayi.param.Generate30BParam;
import com.wg.appframe.yayi.param.MathModelParam;
import com.wg.appframe.yayi.result.CommoneCompletionResult;
import com.wg.appframe.yayi.result.Generate30BResult;
import com.wg.appframe.yayi.result.MathModelResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wenge.model.constants.RedisKey.LLM_API_TOKEN;
import static com.wenge.model.entity.table.FileLanguageTableDef.FILE_LANGUAGE;
import static com.wenge.model.entity.table.LlmInfoTableDef.LLM_INFO;
import static com.wenge.model.workflow.constants.SettingConstants.*;

@Service
@Slf4j
public class SmartProviderServiceImpl implements SmartProviderService {

    @Autowired
    private LlmInfoService llmInfoService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private GuangXinMatterService guangXinMatterService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private DialogueService dialogueService;

    @Autowired
    private LlmGenerateUtil llmGenerateUtil;

    @Autowired
    private DialogueMapper dialogueMapper;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private ComponentService componentService;

    @Value("${address-analysis.prompt}")
    private String addressAnalysisPrompt;

    @Value("${ai-review.workflowId}")
    private String workflowId;

    @Value("${agentX.workflow.api}")
    private String agentFlowApi;

    @Value("${agentX.workflow.stop}")
    private String stop;

    @Resource(name = "addressAnalysisPool")
    private ThreadPoolExecutor addressAnalysisPool;

    @Autowired
    private ModelPluginApiService modelPluginApiService;

    @Autowired
    private CommoneLLServer commoneLLServer;

    @Autowired
    private SmartAgentLlmApiRecordMapper smartAgentLlmApiRecordMapper;

    @Autowired
    private ComponentNodeService componentNodeService;

    @Autowired
    private McpServerService mcpServerService;

    @Autowired
    private FileLanguageService fileLanguageService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private ComponentNodeRelMapper componentNodeRelMapper;

    @Value("${deduction.v2baseId:ab7ebcf1e44b46a19aeabb0380291966_4}")
    private String v2baseId;

    @Autowired
    private FileService fileService;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private UrlParserInfoService urlParserInfoService;


    @Override
    public Result<String> gxClassifyMatter(ProviderGuanxinParam param) {
        ProviderGuanxinResult providerGuanxinResult = new ProviderGuanxinResult();
        providerGuanxinResult.setId(param.getId());
        providerGuanxinResult.setDataId(param.getDataId());

        GuangXinMatter guangXinMatter = new GuangXinMatter();
        guangXinMatter.setParamContent(JSON.toJSONString(param));
        guangXinMatter.setTraceId(param.getId());
        guangXinMatter.setDataId(param.getDataId());
        guangXinMatter.setCallBackUrl(param.getCallBackUrl());
        Result result = checkDeductionV2Param(param);
        if (!Result.success().getCode().equals(result.getCode())) {
            guangXinMatter.setStatus(GuangXinMatterEnum.PARAM_NOT_FULL.getCode());
            guangXinMatter.setResultContent(JSON.toJSONString(result));
            guangXinMatter.setErrorMsg(result.getMsg());
            saveGuanxinMatter(guangXinMatter);
            return result;
        }

        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, YesNoEnum.YES.getName());
        if (null == applicationInfo) {
            result = Result.fail("应用不存在");
            guangXinMatter.setStatus(GuangXinMatterEnum.PARAM_NOT_FULL.getCode());
            guangXinMatter.setResultContent(JSON.toJSONString(result));
            guangXinMatter.setErrorMsg(result.getMsg());
            saveGuanxinMatter(guangXinMatter);
            return result;
        }


        if (StringUtils.isBlank(applicationInfo.getPromptTemplate())) {
            result = Result.fail("未配置指令");
            guangXinMatter.setStatus(GuangXinMatterEnum.PARAM_NOT_FULL.getCode());
            guangXinMatter.setResultContent(JSON.toJSONString(result));
            guangXinMatter.setErrorMsg(result.getMsg());
            saveGuanxinMatter(guangXinMatter);
            return result;
        }

        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);

        // 获取模型信息
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationInfo.getModelId());

        if (null == llmStrategy) {
            result = Result.fail("未配置大模型");
            guangXinMatter.setStatus(GuangXinMatterEnum.PARAM_NOT_FULL.getCode());
            guangXinMatter.setResultContent(JSON.toJSONString(result));
            guangXinMatter.setErrorMsg(result.getMsg());
            saveGuanxinMatter(guangXinMatter);
            return result;
        }

        List<ProviderGuanxinParam.Content> contentList = param.getContentList();
        if (CollectionUtil.isEmpty(contentList)) {
            result = Result.fail("未配置事项");
            guangXinMatter.setStatus(GuangXinMatterEnum.PARAM_NOT_FULL.getCode());
            guangXinMatter.setResultContent(JSON.toJSONString(result));
            guangXinMatter.setErrorMsg(result.getMsg());
            saveGuanxinMatter(guangXinMatter);
            return result;
        }
        SmartAgentLlmApiRecord record = getSmartAgentLlmApiRecord(result);
        record.setApplicationId(applicationInfo.getApplicationId());
        record.setModelId(param.getModule());
        record.setParam(JSONUtil.toJsonStr(param));
        record.setCreateTime(DateUtil.getCurrentDateString());
        record.setApiKey(applicationInfo.getApiKey());
        record.setType(SmartAgentApiTypeEnum.GXZX.getCode());
        smartAgentLlmApiRecordMapper.insert(record);

        guangXinMatter.setStatus(GuangXinMatterEnum.NOT_START.getCode());
        saveGuanxinMatter(guangXinMatter);
        guangXinMatterService.runMatterTask(guangXinMatter);
        return Result.success("已接受请求");

    }

    @Override
    public Result<List<GuangXinMatter>> getGxMatterDetail(ProviderGuanxinParam param) {
        if (StringUtils.isBlank(param.getDataId())) {
            return Result.success(Lists.newArrayList());
        }
        List<GuangXinMatter> gxMatterDetail = guangXinMatterService.getGxMatterDetail(param.getDataId());

        return Result.success(gxMatterDetail);
    }

    @Override
    public Result<ApplicationInfo> getAppDetail(ApiAppDetailParam param, HttpServletRequest request) {
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(null, param.getApiKey(), YesNoEnum.YES.getName());
        Result check = check(param, request, applicationInfo);
        if (!Result.success().getCode().equals(check.getCode())) {
            return check;
        }
        return Result.success(applicationInfo);
    }

    /**
     * 验证参数
     * @param param
     * @return
     */
    private Result checkDeductionV2Param(ProviderGuanxinParam param) {
        if (StringUtils.isBlank(param.getSceneDescription())) {
            return Result.fail("没有场景描述");
        }

        if (CollectionUtil.isEmpty(param.getContentList())) {
            return Result.fail("请提供事项和指标数据");
        }

        if (StringUtils.isBlank(param.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }

        if (StringUtils.isBlank(param.getCallBackUrl())) {
            return Result.fail("回调地址不能为空");
        }

        return Result.success();
    }

    /**
     * 保存事项
     * @param guangXinMatter
     */
    private void saveGuanxinMatter(GuangXinMatter guangXinMatter) {
        guangXinMatterService.save(guangXinMatter);
    }


    /**
     * 校验参数
     *
     * @param param
     * @param request
     * @param applicationInfo
     * @return
     */
    private Result check(ApiAppDetailParam param, HttpServletRequest request, ApplicationInfo applicationInfo) {
        if (StringUtils.isBlank(param.getApiKey())) {
            return Result.fail("appkey不能为空");
        }
        String signature = request.getHeader("signature");

        if (StringUtils.isBlank(signature)) {
            return Result.fail("signature不能为空");
        }
        String timestamp = request.getHeader("timestamp");
        if (StringUtils.isBlank(param.getApiKey())) {
            return Result.fail("timestamp不能为空");
        }

        // 5min内请求有效，误差 5min
        if (Math.abs(System.currentTimeMillis() - Long.parseLong(timestamp)) > 300_000) {
            return Result.fail("请求超时");
        }

        if (null == applicationInfo) {
            return Result.fail("appkey不存在");
        }

        String signatured = getSignature(applicationInfo, Long.parseLong(timestamp));
        if (!signature.equals(signatured)) {
            return Result.fail("签名错误");
        }

        if (redisService.hasKey(signatured)) {
            return Result.fail("重复请求");
        }

        redisService.set(signatured, "1", 60 * 60 * 24 * 3);
        return Result.success();
    }

    /**
     * 获取签名
     *
     * @param applicationInfo
     * @param timestamp
     * @return
     */
    private String getSignature(ApplicationInfo applicationInfo, long timestamp) {
        String signatureContent = applicationInfo.getApiKey() + "|\n" + applicationInfo.getApiSecret() + "|\n" + timestamp;
        return SecureUtil.md5(signatureContent);
    }

    @Override
    public SseEmitter dialogueApiStream(DialogueApiParam param, HttpServletRequest request) {
        // 获取客户端id
        String getClientId = param.getParam().getClientId();
        SseEmitter emitter = SseEmitterUtils.getConnection(getClientId);

        // 获取应用信息
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(null, param.getApiKey(), YesNoEnum.YES.getName());
        DialogueByStreamParam dialogueByStreamParam = param.getParam();

        String content = param.getParam().getContent();
        // 验证问题是否为空
        if (StringUtils.isBlank(content)) {
            authFail(dialogueByStreamParam, ResultCodeEnum.BUSINESS_FAILED.getCode(), "问题内容不能为空");
            return emitter;
        }

        // 验证参数
        ApiAppDetailParam apiAppDetailParam = new ApiAppDetailParam();
        apiAppDetailParam.setApiKey(param.getApiKey());
        dialogueByStreamParam.setApplicationId(applicationInfo.getApplicationId());
        Result check = check(apiAppDetailParam, request, applicationInfo);
        if (!Result.success().getCode().equals(check.getCode())) {
            authFail(dialogueByStreamParam, check.getCode(), check.getMsg());
            return emitter;
        }
        SmartAgentLlmApiRecord record = getSmartAgentLlmApiRecord(check);
        record.setApplicationId(applicationInfo.getApplicationId());
        record.setClientId(param.getParam().getClientId());
        record.setModelId(param.getParam().getModelId());
        record.setParam(JSONUtil.toJsonStr(param));
        record.setCreateTime(DateUtil.getCurrentDateString());
        record.setApiKey(applicationInfo.getApiKey());
        record.setType(SmartAgentApiTypeEnum.DIALOGUE.getCode());
        smartAgentLlmApiRecordMapper.insert(record);

        Thread.currentThread().setName(MDC.get(MDCTraceUtils.KEY_TRACE_ID));
        dialogueService.dialogueByStream(dialogueByStreamParam, request);
        return emitter;
    }

    @Override
    public SseEmitter commonApi(CommonApiParam param) {
        if (StringUtils.isBlank(param.getClientId())) {
            return null;
        }
        SseEmitter sseEmitter = SseEmitterUtils.getConnection(param.getClientId());
        new Thread(() -> {
            cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
            try {
                StepEntity step = new StepEntity();
                jsonObject.set(AnswerStrategyContant.LLM_PARAM_OBJECT, param.getParam());
                step.setParam(jsonObject);
                // 获取应用信息
                ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, YesNoEnum.YES.getName());
                if (null == applicationInfo) {
                    log.warn("应用不存在");
                    SseEmitterUtils.send(param.getClientId(), "应用不存在");
                    return;
                }
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                llmGenerateUtil.generate(StringConstant.BLANK, null, step, false, result -> {
                    try {
                        SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(result));
                        jsonObject.put("result", result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SseEmitterUtils.complete(param.getClientId());
                insertDialogue(param, (AnswerStepData) jsonObject.get("result"));
            }
        }).start();

        return sseEmitter;
    }

    @Override
    public Result<AddressInfoDto> getAddressInfo(GenerateAddressParam param) {
        String content = param.getContent();
        if (StringUtils.isEmpty(content)) {
            return Result.success(null);
        }


        Future<AddressInfoDto> future = addressAnalysisPool.submit(() -> analysisAddress(content));

        try {
            AddressInfoDto result = future.get();
            return Result.success(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            log.error("雅意解析地址信息识别，具体识别原因为：{}", e.getMessage());
        }

        return Result.success(null);
    }

    @Override
    public JSONObject yayi(CommonApiParam param) {
        return YayiUtils.commonApi(param);
    }

    @Override
    public com.alibaba.fastjson.JSONObject wenhai(WenHaiParameter param) {

        WenHaiHeader header = param.getHeader();
        String appKey = header.getAppKey();
        String secretKey = header.getSecretKey();
        String nonce = RandomUtil.randomNumbers(5);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String str = JSON.toJSONString(param.getBody());
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(str);
        com.alibaba.fastjson.JSONObject headerParams = new com.alibaba.fastjson.JSONObject();
        headerParams.put("appKey", appKey);
        headerParams.put("nonce", nonce);
        headerParams.put("timestamp", timestamp);
        headerParams.put("signature", CustomerSignUtil.sign(appKey, secretKey, nonce, timestamp, jsonObject));
        return CustomerSignUtil.requestForHttp(param.getUrl(), str, headerParams);

    }

    @Override
    public SseEmitter dialogueRunApiStream(DialogueRunApiParam param, HttpServletRequest request) {
        // 获取客户端id
        String getClientId = param.getParam().getClientId();
        SseEmitter emitter = SseEmitterUtils.getConnection(getClientId);

        // 获取应用信息
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(null, param.getApiKey(), YesNoEnum.YES.getName());
        RunComponentNodeParam nodeParam = param.getParam();

        // 验证参数
        ApiAppDetailParam apiAppDetailParam = new ApiAppDetailParam();
        apiAppDetailParam.setApiKey(param.getApiKey());
        Result check = check(apiAppDetailParam, request, applicationInfo);
        if (!Result.success().getCode().equals(check.getCode())) {
            try {
                SseEmitterUtils.sendMsg(nodeParam.getClientId(), check.getMsg());
                SseEmitterUtils.completeDelay(nodeParam.getClientId());
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
            return emitter;
        }

        // 验证是否传入大模型 apikey 参数
        // String msg = checkLllmParam(nodeParam);
        // if (StringUtils.isNotBlank(msg)) {
        //     try {
        //         SseEmitterUtils.sendMsg(nodeParam.getClientId(), "大模型参数不合法");
        //         SseEmitterUtils.completeDelay(nodeParam.getClientId());
        //     } catch (IOException ignored) {
        //         ignored.printStackTrace();
        //     }
        //     return emitter;
        // }
        SmartAgentLlmApiRecord record = getSmartAgentLlmApiRecord(check);
        record.setApplicationId(applicationInfo.getApplicationId());
        record.setModelId(applicationInfo.getModelId());
        record.setClientId(param.getParam().getClientId());
        record.setParam(JSONUtil.toJsonStr(param));
        record.setCreateTime(DateUtil.getCurrentDateString());
        record.setApiKey(param.getApiKey());
        record.setType(SmartAgentApiTypeEnum.WORKFLOW_STREAM.getCode());
        smartAgentLlmApiRecordMapper.insert(record);

        // Thread.currentThread().setName(MDC.get(MDCTraceUtils.KEY_TRACE_ID));
        componentService.run(nodeParam, WorkflowRunStatus.DEBUG);

        return emitter;
    }

    @Override
    public Result<DialogueByStreamParam> dialogueRunApiString(DialogueRunApiParam param, HttpServletRequest request) {
        if (null == param.getParam()) {
            return Result.fail("参数不能为空");
        }
        String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        // 获取应用信息
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(null, param.getApiKey(), YesNoEnum.YES.getName());

        // 验证参数
        ApiAppDetailParam apiAppDetailParam = new ApiAppDetailParam();
        apiAppDetailParam.setApiKey(param.getApiKey());
        Result check = check(apiAppDetailParam, request, applicationInfo);
        if (!Result.success().getCode().equals(check.getCode())) {
            return Result.fail(check.getMsg());
        }

        // 验证是否传入大模型 apikey 参数
        // String msg = checkLllmParam(param.getParam());
        // if (StringUtils.isNotBlank(msg)) {
        //     log.info("大模型参数不合法");
        //     return Result.fail(msg);
        // }

        DialogueByStreamParam dialogue = dialogueByWorkflowApiStream(param);
        dialogue.setTraceId(traceId);
        MDC.put(MDCTraceUtils.KEY_TRACE_ID, traceId);

        // 保存记录
        Result<DialogueByStreamParam> success = Result.success(dialogue);
        SmartAgentLlmApiRecord record = getSmartAgentLlmApiRecord(success);
        record.setApplicationId(applicationInfo.getApplicationId());
        record.setModelId(applicationInfo.getModelId());
        record.setParam(JSONUtil.toJsonStr(param));
        record.setApiKey(param.getApiKey());
        record.setType(SmartAgentApiTypeEnum.WORKFLOW_STRING.getCode());
        smartAgentLlmApiRecordMapper.insert(record);
        return success;
    }

    private SmartAgentLlmApiRecord getSmartAgentLlmApiRecord(Result result) {
        SmartAgentLlmApiRecord record = new SmartAgentLlmApiRecord();
        record.setId(IdUtil.simpleUUID());
        record.setCode(result.getCode());
        record.setMsg(result.getMsg());
        record.setResult(JSONUtil.toJsonStr(result));
        record.setTraceId(result.getTraceId());
        record.setCreateTime(DateUtil.getCurrentDateString());
        // 流式的拿不到token
        // record.setTotal_tokens();
        return record;
    }

    private DialogueByStreamParam dialogueByWorkflowApiStream(DialogueRunApiParam param) {
        RunComponentNodeParam nodeParam = param.getParam();
        Map<String, Object> concurrentHashMap = componentService.runFlow(nodeParam, WorkflowRunStatus.DEBUG, true);

        DialogueByStreamParam dialogue = new DialogueByStreamParam();
        String keyTraceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        dialogue.setStream("true");
        dialogue.setTraceId(keyTraceId);
        dialogue.setOutput(concurrentHashMap);
        Object finishReason = concurrentHashMap.get(GLOBAL_FINISHREASON);
        if (null != finishReason) {
            StepStatusEnum byCode = StepStatusEnum.getByCode(Integer.parseInt(finishReason.toString()));
            dialogue.setFinishReason(byCode);
        }
        // 全局变量：对话id
        Object globalDialogueId = concurrentHashMap.get(GLOBAL_DIALOGUE_ID);
        if (null != globalDialogueId) {
            dialogue.setDialogueId(globalDialogueId.toString());
        }
        dialogue.setAnswerFlag("true");
        // 自定义返回事项
        Object fullContent = concurrentHashMap.get(GLOBAL_ANSWER);
        Object fullReasonContentKey = concurrentHashMap.get(GLOBAL_REASON_CONTENT);
        if (null != fullContent && StringUtils.isNotBlank(fullContent.toString())) {
            dialogue.setAnswer(fullContent.toString());
        }
        if (null != fullReasonContentKey && StringUtils.isNotBlank(fullReasonContentKey.toString())) {
            dialogue.setReasoningContent(fullReasonContentKey.toString());
        }

        // final DialogueVO vo = new DialogueVO();
        // try (final Response response = invokeWordflowStreamApi(agentFlowApi, appkey, nodeParam, clientId)) {
        //     if (response.isSuccessful()) {
        //         final InputStream it = response.body().byteStream();
        //         final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
        //         dialogue = responseByStream(bufferedReader);
        //         // nodeParam.setAnswer(vo.getAnswer());
        //     } else {
        //         final String responseBody = response.body().string();
        //         log.error("调用工作流响应异常，responseBody = {}", responseBody);
        //     }
        // } catch (Exception e) {
        //     log.error("调用工作流响应异常，url = {}", agentFlowApi, e);
        // }
        return dialogue;
    }

    private Response invokeWordflowStreamApi(String url, String appkey, RunComponentNodeParam nodeParam, String clientId) throws Exception {
        try {
            final String contentType = "application/json; charset=utf-8";
            final String accept = "text/event-stream";

            final MediaType mediaType = MediaType.parse(contentType);

            JSONObject param = new JSONObject();
            cn.hutool.json.JSONObject input = nodeParam.getInputs();

            param.put("clientId", clientId);
            param.put("clientType", "PC");
            param.put("componentId", nodeParam.getComponentId());
            param.put("debuggerFlag", "是");
            param.put("inputs", input);
            param.put("llmInfoList", nodeParam.getLlmInfoList());
            final RequestBody body = RequestBody.create(mediaType, param.toJSONString());

            final Request.Builder builder = new Request.Builder();
            Request request = builder
                    .header("Authorization", appkey)
                    .header(MDCTraceUtils.TRACE_ID_HEADER, MDC.get(MDCTraceUtils.KEY_TRACE_ID))
                    .header("Content-Type", contentType)
                    .header("Accept", accept)
                    .url(url)
                    .post(body)
                    .header("User-Agent", "agent-x")
                    .build();


            log.info("开始请求工作流流式接口, url = {}，请求体: {}", url, param.toJSONString());
            // OkHttpClient okHttpClient = new OkHttpClient();
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(7200, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            return httpClient.newCall(request).execute();
        } catch (Exception e) {
            stopStream(clientId);
            log.error("请求工作流接口失败, error = {}", e.getMessage(), e);
            throw new Exception("请求工作流接口失败");
        }
    }

    private DialogueByStreamParam responseByStream(BufferedReader bufferedReader) {
        String line = StringConstant.BLANK;
        String dataStr = StringConstant.BLANK;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // final StringBuilder finalAnswer = new StringBuilder();
        try {
            log.info("******************** 开始接收智川平台的工作流数据");
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();
            result.put("data", data);
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    // log.info("工作流流式接口返回数据: {}", line);
                    // 返回结果中，如果包含answerFlag，说明是 结束节点推送的正常结果
                    if (line.contains("answerFlag")) {
                        dataStr = line.replaceFirst("data:", StringConstant.BLANK);
                    }
                }
            }
            if (StringUtils.isNotBlank(dataStr) && JSONUtil.isTypeJSONObject(dataStr)) {
                return JSONUtil.parseObj(dataStr).toBean(DialogueByStreamParam.class);
            }
        } catch (Exception e) {
            log.error("请求工作流接口异常，e = {}", e.getMessage(), e);
        } finally {
            // vo.setAnswer(finalAnswer.toString());
        }

        // stopWatch.stop();
        // vo.setSource("agent-x");
        // Float time = (float) stopWatch.getTotalTimeSeconds();
        // vo.setConsumingTime(time);
        // log.info("工作流推送回答完成，耗时 = {}秒，finalAnswer = {}", time, vo.getAnswer());
        return new DialogueByStreamParam();
    }

    private void stopStream(String clientId) {
        try {
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("clientId", clientId);
            String responseBody = HttpUtil.post(stop, jsonObject.toJSONString());
            log.info("停止调用工作流流式接口, url = {}，responseBody = {}", stop, responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SseEmitter completions(LlmCompletionsParam param, HttpServletRequest request) {
        LlmCompletionsResult result = new LlmCompletionsResult();
        String traceId =  MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        result.setTraceId(traceId);

        log.info("completions.traceId:{},param:{}", traceId, JSON.toJSONString(param));
        if (StringUtils.isBlank(param.getClientId())) {
            log.info("clientId不能为空,traceId:{}", traceId);
            final SseEmitter emitter = new SseEmitter(600_000L);
            result.setCode("403");
            result.setMsg("clientId不能为空");
            try {
                emitter.send(result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                emitter.complete();
            }
            return emitter;
        }
        SseEmitter sseEmitter = SseEmitterUtils.getConnection(param.getClientId());

        addressAnalysisPool.execute(() -> {
            boolean needDecr = false;
            try {
                result.setCode(Result.success().getCode());
                result.setCode(Result.success().getMsg());
                String authorization = request.getHeader("Authorization");

                // 验证请求
                Result checkRequest = checkRequest(request);
                if (!Result.success().getCode().equals(checkRequest.getCode())) {
                    SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(checkRequest));
                    return;
                }
                // 请求次数加 1
                incr(request);

                needDecr = true;
                if (null == param.getLlmParam()) {
                    log.info("模型参数不能为空,traceId:{}", traceId);
                    result.setCode("405");
                    result.setMsg("模型参数不能为空");
                    SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(result));
                    return;
                }

                ApplicationInfo applicationInfo = null;
                String modelId = StringConstant.BLANK;
                if (StringUtils.isNotBlank(param.getModelId())) {
                    modelId = param.getModelId();
                } else if (StringUtils.isNotBlank(param.getApplicationId())) {
                    applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, YesNoEnum.YES.getName());
                    if (null != applicationInfo) {
                        modelId = applicationInfo.getModelId();
                    }
                }

                if (StringUtils.isBlank(modelId)) {
                    log.info("模型不存在,traceId:{}", traceId);
                    result.setCode("501");
                    result.setMsg("模型不存在");
                    SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(result));
                    return;
                }
                Wrappers<Object> wrappers = Wrappers.init()
                        .select(LLM_INFO.MODEL_NAME, LLM_INFO.MODEL_ID, LLM_INFO.MODEL_CODE, LLM_INFO.APP_KEY, LLM_INFO.URI, LLM_INFO.MODEL)
                        .where(LLM_INFO.MODEL_ID.eq(modelId))
                        .limit(1);
                LlmInfo llmInfo = llmInfoService.getOne(wrappers);
                if (null == llmInfo) {
                    log.info("模型未接入,traceId:{}", traceId);
                    result.setCode("501");
                    result.setMsg("模型未接入");
                    SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(result));
                    return;
                }
                //
                // LlmStrategy llmStrategy = llmStrategyMap.get(llmInfo.getModelCode());
                // if (null == llmStrategy) {
                //     log.info("模型未接入,traceId:{}", traceId);
                //     result.setCode("501");
                //     result.setMsg("模型不存在");
                //     SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(result));
                //     return;
                // }
                result.setLlmInfo(llmInfo);
                SmartAgentLlmApiRecord record = new SmartAgentLlmApiRecord();

                cn.hutool.json.JSONObject llmParam1 = param.getLlmParam();
                CommoneCompletionParam llmParam = new CommoneCompletionParam();
                llmParam.setStream(true);
                if (llmParam1.containsKey("stream")) {
                    llmParam.setStream(llmParam1.getBool("stream"));
                }
                llmParam.setStrategy(llmParam1.getStr("strategy"));
                llmParam.setAppKey(llmInfo.getAppKey());
                llmParam.setUri(llmInfo.getUri());
                llmParam.setLlmParam(param.getLlmParam());
                param.getLlmParam().put("model", llmInfo.getModel());
                CommoneCompletionResult llmResult = commoneLLServer.generate(llmParam, llmResults -> {
                    long totalTokens = consumer(result, llmInfo, llmResults, param.getClientId(), traceId);
                    record.setTotal_tokens(totalTokens);
                });
                if (!llmParam.getStream()) {
                    long totalTokens = consumer(result, llmInfo, llmResult, param.getClientId(), traceId);
                    record.setTotal_tokens(totalTokens);
                }

                record.setId(IdUtil.simpleUUID());
                record.setApplicationId(param.getApplicationId());
                record.setModelId(modelId);
                record.setCode(result.getCode());
                record.setMsg(result.getMsg());
                record.setClientId(param.getClientId());
                record.setResult(JSONUtil.toJsonStr(result));
                record.setParam(JSONUtil.toJsonStr(param));
                record.setTraceId(traceId);
                record.setCreateTime(DateUtil.getCurrentDateString());
                record.setApiKey(authorization);
                record.setType(SmartAgentApiTypeEnum.LLM.getCode());
                smartAgentLlmApiRecordMapper.insert(record);
            } catch (Exception e) {
                try {
                    log.info("模型异常,traceId:{}", traceId);
                    result.setCode("504");
                    result.setMsg("模型异常");
                    SseEmitterUtils.send(param.getClientId(), JSON.toJSONString(result));
                    return;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                log.info("模型未接入,traceId:{}", traceId, e);
            } finally {
                SseEmitterUtils.complete(param.getClientId());

                // 请求次数减去 1
                if (needDecr) {
                    decr(request);
                }
            }
        });
        // CompletableFuture.runAsync(() -> {}, addressAnalysisPool);
        return sseEmitter;
    }

    @Override
    public Result<List<McpResult>> runMcp(RunMcpApiParam param, HttpServletRequest request) {
        String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        log.info("runMcp.param:{}", JSON.toJSONString(param));
        List<McpResult> mcpResults = Lists.newArrayList();
        try {
            // 空参数，则返回空数据
            if (StringUtils.isBlank(param.getQuery()) || CollectionUtil.isEmpty(param.getMcpServerIdList())) {
                return Result.success(Lists.newArrayList());
            }
            SmartAgentLlmApiRecord record = new SmartAgentLlmApiRecord();
            // 验证请求
            Result checkRequest = checkRequest(request);
            if (!Result.success().getCode().equals(checkRequest.getCode())) {
                return checkRequest;
            }
            // 请求次数加 1
            incr(request);

            List<McpServer> mcpServerByIds = mcpServerService.getMcpServerByIds(param.getMcpServerIdList());
            if (CollectionUtil.isEmpty(mcpServerByIds)) {
                return Result.success(Lists.newArrayList());
            }
            mcpResults = McpStrategy.getMcpResult(param.getQuery(), mcpServerByIds, new StepEntity());

            record.setId(IdUtil.simpleUUID());
            record.setApplicationId(StringConstant.BLANK);
            record.setModelId(StringConstant.BLANK);
            record.setCode(Result.success().getCode());
            record.setMsg(Result.success().getMsg());
            record.setClientId(StringConstant.BLANK);
            record.setResult(JSONUtil.toJsonStr(mcpResults));
            record.setParam(JSONUtil.toJsonStr(param));
            record.setTraceId(traceId);
            record.setCreateTime(DateUtil.getCurrentDateString());
            String authorization = request.getHeader("Authorization");
            record.setApiKey(authorization);
            record.setType(SmartAgentApiTypeEnum.MCP_SERVER.getCode());
            smartAgentLlmApiRecordMapper.insert(record);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            decr(request);
        }

        return Result.success(mcpResults);
    }

    @Override
    public Result<List<McpServer>> findMcpServerList(McpFindParam param, HttpServletRequest request) {
        List<McpServer> list = mcpServerService.getActifList(param);
        list.forEach(item -> {
            item.setCreateUserId(null);
            item.setUpdateUserId(null);
            item.setCreateUserName(null);
            item.setUpdateUserName(null);
        });
        return Result.success(list);
    }

    /**
     * 检查请求头
     *
     * @param request
     * @return
     */
    private Result checkRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isBlank(authorization)) {
            log.info("authorization is blank");
            return Result.error("401", "请求头[Authorization]不能为空");
        }
        Result<ModelPluginApiAuthUser> checkResult = modelPluginApiService.checkApiToken(authorization, null);
        if (!Result.success().getCode().equals(checkResult.getCode())) {
            log.info("checkResult is failed");
            return checkResult;
        }

        ModelPluginApiAuthUser data = checkResult.getData();
        String redisKey = LLM_API_TOKEN + authorization;
        Integer count = redisService.getObject(redisKey, Integer.class);
        if (null == data.getQps()) {
            data.setQps(1);
        }
        if (null != count && count >= data.getQps()) {
            log.info("QPS不足");
            return Result.error("407", "QPS不足");
        }
        return Result.success();
    }

    /**
     * 获取redisKey
     * @param request
     * @return
     */
    private void incr(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String redisKey = LLM_API_TOKEN + authorization;
        // 请求次数加 1
        redisService.incr(redisKey, 1);
    }

    /**
     * 减请求次数
     * @param request
     */
    private void decr(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String redisKey = LLM_API_TOKEN + authorization;
        Integer count = redisService.getObject(redisKey, Integer.class);
        if (null != count && count > 0) {
            redisService.decr(redisKey, 1);
        }
    }

    @Override
    public SseEmitter aiReview(AIReviewParam param, HttpServletRequest request) {
        // 验证参数
        ApiAppDetailParam apiAppDetailParam = new ApiAppDetailParam();
        apiAppDetailParam.setApiKey(param.getApiKey());
        // 为了兼容调用方旧方式的入参，加如下配置
        if (StringUtils.isNotBlank(param.getApplicationId())) {
            workflowId = param.getApplicationId();
        }
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(workflowId, param.getApiKey(), YesNoEnum.YES.getName());

        Result check = check(apiAppDetailParam, request, applicationInfo);
        if (!Result.success().getCode().equals(check.getCode())) {
            SseEmitter emitter = SseEmitterUtils.getConnection(param.getClientId());
            try {
                SseEmitterUtils.sendMsg(param.getClientId(), check.getMsg());
                SseEmitterUtils.completeDelay(param.getClientId());
            } catch (IOException ignored) {

            }
            return emitter;
        }

        RunComponentNodeParam nodeParam = new RunComponentNodeParam();
        nodeParam.setComponentId(workflowId);
        cn.hutool.json.JSONObject file = param.getFile();
        file.set("type", FileType.DOC.getType());
        file.set("transferMethod", FileTransferMethod.REMOTE_URL.getMethod());
        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
        JSONArray objects = new JSONArray();
        objects.add(file);
        jsonObject.set("question", objects);
        nodeParam.setInputs(jsonObject);
        nodeParam.setClientId(param.getClientId());
        return componentService.run(nodeParam, WorkflowRunStatus.RUN);
    }

    @Override
    public Result<List<LlmInfo>> queryLlmInfo() {
        Wrappers wrappers = Wrappers.init()
                .where(LLM_INFO.STATUS.eq("启用"))
                .and(LLM_INFO.DELETE_FLAG.eq(0));
        List<LlmInfo> llmInfos = llmInfoService.list(wrappers);
        return Result.success(llmInfos);
    }

    @Override
    public Result mathModel(MathModelParam param) {
        MathModelResult mathModelResult = yayiServer.mathModel("分析一下该表格的内容", param);
        return Result.success(mathModelResult.getData());
    }

    @Override
    public Result summary(SummaryParam param) {
        SummaryParam.Param p = param.getParam();
        FileLanguage fileLanguage = param.getFileLanguage();
        FileLanguage language = fileLanguageService.getOne(QueryWrapper.create(fileLanguage).where(FILE_LANGUAGE.ID.eq(fileLanguage.getId())));
        String type = p.getType();

        if (StringUtils.equals(type, "大纲提取") && StringUtils.isNotBlank(language.getOutline())) {
            return Result.success(language.getOutline());
        } else if (StringUtils.equals(type, "知识提取") && StringUtils.isNotBlank(language.getKnowledgePoint())) {
            return Result.success(language.getKnowledgePoint());
        } else if (StringUtils.equals(type, "全文概况") && StringUtils.isNotBlank(language.getFullTextOverview())) {
            return Result.success(language.getFullTextOverview());
        }


        // 调用模型能力总结大纲
        String content = p.getContent();
        String overviewPrompt = "以下为文档内容，请提取全文概况，以markdown格式输出 要求：不允许出现``` markdown" + content;
        String knowledgePrompt = "以下为文档内容，请提取知识点信息，以markdown格式输出 要求：不允许出现``` markdown" + content;
        String outlinePrompt = "以下为文档内容，请提取大纲，以markdown格式输出 要求：不允许出现``` markdown" + content;
        String finalPrompt;
        if (StringUtils.equals(type, "大纲提取")) {
            finalPrompt = outlinePrompt;
        } else if (StringUtils.equals(type, "知识提取")) {
            finalPrompt = knowledgePrompt;
        } else {
            finalPrompt = overviewPrompt;
        }
        Generate30BResult generate30BResult = yayiServer.generate30B(finalPrompt);
        String result = generate30BResult.getData().getChoices().get(0).getMessage().getContent();
        // 保存大纲，便于下次访问节省token
        if (StringUtils.equals(type, "大纲提取")) {
            fileLanguage.setOutline(result);
        } else if (StringUtils.equals(type, "知识提取")) {
            fileLanguage.setKnowledgePoint(result);
        } else {
            fileLanguage.setFullTextOverview(result);
        }
        fileLanguageService.updateById(fileLanguage);
        return Result.success(result);
    }

    @Override
    public Result deductionV2(DeductionV2Param param, HttpServletRequest request) {
        try {
            // 读取为UTF-8字符串
            Result result = checkDeductionV2Param(param, request);
            if (!result.getCode().equals(Result.success().getCode())) {
                return result;
            }
            String content = new String(param.getFile().getBytes(), StandardCharsets.UTF_8);
            cn.hutool.json.JSONObject entries = JSONUtil.parseObj(content);

            String name = "推演-" + param.getFile().getOriginalFilename()
                    .replace(".txt", StringConstant.BLANK)
                    .replace(".json", StringConstant.BLANK)
                    + "【" + com.wenge.model.utils.DateUtil.getTime() + "】";

            // 获取环境刻画
            Map<String, JSONObject> environmentCapture = Maps.newHashMap();
            entries.forEach((k, v) -> {
                if (k.endsWith("环境刻画")) {
                    if (v instanceof JSONArray) {
                        JSONArray array = (JSONArray) v;
                        List<JSONObject> list = array.toList(JSONObject.class);

                        // 提取 key 中出现的字母
                        String letter = k.replaceAll("[^a-zA-Z]", StringConstant.BLANK);
                        JSONObject envs = new JSONObject();
                        envs.put(k, list);
                        environmentCapture.put(letter, envs);
                    }
                }
            });

            // 先复制一个工作流，然后再修改节点
            Component component = new Component();
            component.setComponentName("name");
            component.setComponentId(v2baseId);
            Result<WorkFlowDto> app = workFlowService.copy(component, "flowwork", name);
            WorkFlowDto workFlowDto = app.getData();
            Component component1 = workFlowDto.getComponent();

            List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(component1.getComponentId()));
            if (CollectionUtil.isEmpty(componentDtoList)) {
                return Result.fail("未找到对应的工作流");
            }
            ComponentDto componentDto = componentDtoList.get(0);

            // 获取画布
            String canvas = componentDto.getCanvas();
            cn.hutool.json.JSONObject canvasObj = JSONUtil.parseObj(canvas);
            List<cn.hutool.json.JSONObject> cells = canvasObj.getJSONArray("cells").toList(cn.hutool.json.JSONObject.class).stream().collect(Collectors.toList());

            // 获取画布的节点数据
            List<CanvasData> cardList = canvasObj.getJSONArray("cells").toList(JSONObject.class).stream()
                    .filter(p -> p.containsKey("data"))
                    .map(p -> {
                        return p.toJavaObject(CanvasData.class);
                    }).collect(Collectors.toList());

            // 获取画布的连线数据
            List<CanvasLine> lineList = canvasObj.getJSONArray("cells").toList(JSONObject.class).stream()
                    .filter(p -> !p.containsKey("position"))
                    .map(p -> {
                        return p.toJavaObject(CanvasLine.class);
                    }).collect(Collectors.toList());

            // 获取主体数据，这里解析类似（A,B,0.2）\n(B,C,0.3)\n(B,D,0.5)\n(C,D,0,3)\n(D,C,0.2)
            List<CanvasNodeLink> linkList = getLinkList(entries);
            List<CanvasNodeLink> topList = linkList.stream().filter(p -> "1".equals(p.getIsTop())).collect(Collectors.toList());

            CanvasData environmentalNode = cardList.stream().filter(p -> p.getData().getStr("label").equals("环境刻画")).findAny().get();
            CanvasData subjectNode = cardList.stream().filter(p -> p.getData().getStr("label").equals("主体结果")).findAny().get();
            CanvasData aNode = cardList.stream().filter(p -> p.getData().getStr("label").equals("A")).findAny().get();
            CanvasData bNode = cardList.stream().filter(p -> p.getData().getStr("label").equals("B")).findAny().get();

            List<ComponentNodeDto> nodes = componentDto.getNodes();
            List<ComponentNodeRel> nodeRel = componentDto.getNodeRel();

            // 添加节点
            for (int i = 0; i < topList.size(); i++) {
                CanvasNodeLink canvasNodeLink = topList.get(i);
                initNode(nodes, nodeRel, canvasNodeLink, aNode, environmentalNode, cardList, lineList, subjectNode, i, 0);
            }

            // 删除A，B节点
            removeCard(nodes, cardList, ListUtil.toList(aNode.getId(), bNode.getId()));

            // 移除连接线
            removeLink(nodeRel, lineList, ListUtil.toList(aNode.getId(), bNode.getId()));

            // 智能体列表
            JSONArray jsonArray = entries.getJSONArray("智能体列表");
            List<cn.hutool.json.JSONObject> subjectList = jsonArray.toList(cn.hutool.json.JSONObject.class);
            HashMap<String, cn.hutool.json.JSONObject> subjectMap = subjectList.stream().collect(Collectors.toMap(
                    p -> p.getStr("name"),
                    p -> p,
                    (k1, k2) -> k1,
                    Maps::newHashMap
            ));

            HashMap<String, ComponentNodeDto> nodeMap = nodes.stream().collect(Collectors.toMap(
                    ComponentNodeDto::getNodeName,
                    p -> p,
                    (k1, k2) -> k1,
                    Maps::newHashMap
            ));

            // 处理主体的系统提示词和引入变量
            CanvasNodeLink last = null;
            for (CanvasNodeLink canvasNodeLink : linkList) {
                String systemPrompt = "你的角色设定如下：\n{role}\n{env}\n {lastLabel}";
                CanvasData canvasData = cardList.stream().filter(p -> p.getData().getStr("label").equals(canvasNodeLink.getLabel())).findAny().get();
                cn.hutool.json.JSONObject data = canvasData.getData();
                cn.hutool.json.JSONObject appForm = JSONUtil.parseObj(data.getStr("appForm"));
                List<cn.hutool.json.JSONObject> inputs = JSONUtil.parseArray(appForm.getStr("inputs")).toList(cn.hutool.json.JSONObject.class);
                List<cn.hutool.json.JSONObject> selectedGroup = inputs.stream().filter(p -> p.getStr("selectedGroup").equals(environmentalNode.getId())).collect(Collectors.toList());

                // 获取环境因数
                cn.hutool.json.JSONObject subject = subjectMap.get(canvasNodeLink.getLabel());
                List<JSONObject> envList = Lists.newArrayList();
                subject.forEach((k, v) -> {
                    if (k.contains("环境影响强度")) {
                        String letter = k.replaceAll("[^a-zA-Z]", StringConstant.BLANK);
                        JSONObject jsonObject = environmentCapture.get(letter);
                        envList.add(jsonObject);
                    }
                });

                // 设置变量
                String lastLabel = StringConstant.BLANK;
                if (null != last) {
                    lastLabel = "主体{subject}的数据如下:\n{lastResult}"
                            .replace("{subject}", last.getLabel())
                            .replace("{lastResult}", "${" + last.getLabel() + "}");
                    cn.hutool.json.JSONObject entries1 = inputs.get(1);

                    // 替换selectedGroup和label
                    entries1.set("selectedGroup", last.getId());
                    entries1.set("label", last.getLabel());
                    entries1.set("value", "text");
                    selectedGroup.add(entries1);
                    inputs.clear();
                    inputs.addAll(selectedGroup);
                    appForm.set("inputs", inputs);

                    ComponentNodeDto componentNodeDto = nodeMap.get(canvasNodeLink.getLabel());
                    List<MetaParam> inputMetaParams = componentNodeDto.getInput();

                    ComponentNodeDto envNode = nodeMap.get(environmentalNode.getData().getStr("label"));
                    MetaParam metaParam = inputMetaParams.stream().filter(p -> p.getReferenceNodeId().equals(envNode.getNodeId())).findAny().get();
                    List<MetaParam> inputList = ListUtil.toList(metaParam);
                    MetaParam metaParam1 = inputMetaParams.get(1);
                    metaParam1.setReferenceNodeId(last.getId());
                    metaParam1.setVariable(last.getLabel());
                    metaParam1.setLabel(last.getLabel());
                    metaParam1.setDesc(last.getLabel());
                    metaParam1.setValue("text");
                    inputList.add(metaParam1);
                } else {
                    String userPrompt = appForm.getStr("userPrompt");
                    // 替换userPrompt
                    appForm.set("userPrompt", userPrompt + "\n用户提供的社会事件信息如下：\n${event}");
                }
                systemPrompt = systemPrompt
                        .replace("{role}", JSONUtil.toJsonStr(subjectMap.get(canvasNodeLink.getLabel())))
                        .replace("{lastLabel}", lastLabel);
                String env = StringConstant.BLANK;
                if (CollectionUtil.isNotEmpty(envList)) {
                    env = "\n环境影响数据如下:\n" + JSONUtil.toJsonStr(envList);
                }
                systemPrompt = systemPrompt.replace("{env}", env);
                appForm.set("systemPrompt", systemPrompt);
                data.set("appForm", JSONUtil.toJsonStr(appForm));
                data.set("inputs", JSONUtil.toJsonStr(appForm.getJSONArray("inputs")));

                last = canvasNodeLink;
            }

            cn.hutool.json.JSONObject data = subjectNode.getData();
            cn.hutool.json.JSONObject appForm = JSONUtil.parseObj(data.getStr("appForm"));
            List<cn.hutool.json.JSONObject> inputs = JSONUtil.parseArray(appForm.getStr("inputs")).toList(cn.hutool.json.JSONObject.class);
            cn.hutool.json.JSONObject entries1 = inputs.get(0);
            // 替换selectedGroup和label
            entries1.set("selectedGroup", last.getId());
            appForm.set("inputs", inputs);
            data.set("appForm", JSONUtil.toJsonStr(appForm));
            data.set("inputs", JSONUtil.toJsonStr(appForm.getJSONArray("inputs")));

            List<cn.hutool.json.JSONObject> lines = lineList.stream().map(JSONUtil::parseObj).collect(Collectors.toList());
            List<cn.hutool.json.JSONObject> cards = cardList.stream().map(JSONUtil::parseObj).collect(Collectors.toList());

            cells.clear();
            cells.addAll(lines);
            cells.addAll(cards);
            canvasObj.clear();
            canvasObj.set("cells", cells);
            componentDto.setCanvas(JSONUtil.toJsonStr(canvasObj));

            componentDto.setComponentName(name);
            TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
            componentDto.getApplicationInfo().setCreateUser(tokenUserInfo.getAccountName());
            componentDto.getApplicationInfo().setUpdateUser(tokenUserInfo.getAccountName());
            componentDto.getApplicationInfo().setCreateTime(DateUtil.getCurrentDateString());
            componentDto.getApplicationInfo().setUpdateTime(DateUtil.getCurrentDateString());

            componentService.draft(componentDto);

            DeductionV2Result deductionV2Result = new DeductionV2Result();
            deductionV2Result.setApplicationName(componentDto.getApplicationInfo().getApplicationName());
            deductionV2Result.setComponentId(componentDto.getComponentId());
            deductionV2Result.setApiKey(componentDto.getApplicationInfo().getApiKey());
            deductionV2Result.setApiSecret(componentDto.getApplicationInfo().getApiSecret());
            return Result.success(deductionV2Result);
        } catch (IOException e) {
            log.error("文件读取失败", e);
            return Result.fail("文件读取失败");
        }
        // 读取文件内容MultipartFile
    }



    /**
     * 调用大模型生成问答结果
     * @param content 内容
     * @return 结果
     */
    private AddressInfoDto analysisAddress(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        Generate30BParam generate30BParam = new Generate30BParam();
        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        messages.setContent(content + "，" + addressAnalysisPrompt);
        messages.setRole("user");
        messagesList.add(messages);

        generate30BParam.setMessages(messagesList);
        generate30BParam.setMax_new_tokens(content.length() + 512);
        generate30BParam.setStream(false);
        generate30BParam.setTemperature(0.011f);

        AddressInfoDto addressInfoDto = null;
        try {
            String result = yayiServer.generate30BStr(content, generate30BParam);
            addressInfoDto = analysisAddressInfo(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("雅意解析地址信息识别，具体识别原因为：{}", e.getMessage());
        }
        return addressInfoDto;
    }

    /**
     * 解析雅意大模型答案
     * @param answer yayi原始答案
     * @return 结果
     */
    private AddressInfoDto analysisAddressInfo(String answer) {
        String province = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.PROVINCE);
        String city = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.CITY);
        String area = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.AREA);
        String streetTown = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.STREET_TOWN);
        String quarter = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.QUARTER);
        String building = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.BUILDING);
        String unit = answerUtils.analyticAnswerByJsonKey(answer, AddressInfoConstant.UNIT);
        AddressInfoDto addressInfoDto = new AddressInfoDto();
        addressInfoDto.setProvince(province);
        addressInfoDto.setCity(city);
        addressInfoDto.setArea(area);
        addressInfoDto.setStreetTown(streetTown);
        addressInfoDto.setQuarter(quarter);
        addressInfoDto.setBuilding(building);
        addressInfoDto.setUnit(unit);

        return addressInfoDto;
    }


    /**
     * 认证失败
     *
     * @param param
     */
    private void authFail(DialogueByStreamParam param, String code, String msg) {
        try {
            Result result = Result.fail(new ResultCodeBase() {
                @Override
                public String getCode() {
                    return code;
                }

                @Override
                public String getMsg() {
                    return msg;
                }
            });
            SseEmitterUtils.sendMsg(param.getClientId(), JSON.toJSONString(result));
            SseEmitterUtils.complete(param.getClientId());
        } catch (Exception e) {
            log.warn("没有用户");
        }
    }

    /**
     * 保存对话
     */
    protected void insertDialogue(CommonApiParam param, AnswerStepData answerStepData) {
        final Dialogue entity = new Dialogue();
        entity.setDialogueId(param.getClientId());
        entity.setQuestion(JSON.toJSONString(param.getParam()));
        entity.setAnswer(JSON.toJSONString(answerStepData));
        entity.setApplicationId(param.getApplicationId());
        String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        entity.setTraceId(traceId);
        entity.setCreateTime(LocalDateTimeUtil.now().format(DateTimeFormatter.ofPattern(DateUtil.DEFAULT_FORMAT)));
        entity.setVerifyStatus(DialogueVerifyStatusEnum.WAIT_VERIFY.getCode());
        dialogueMapper.insert(entity);
    }

    public static void main(String[] args) {
        String requestJson = "{\"apiKey\":\"abf2848a77aec871bc0f96d969d8c893c015e0bc\",\"clientId\":\"8e190b\",\"file\":{\"extension\":\"docx\",\"filename\":\"西藏自治区国有资产管理委员会关于印发《区政府国资委法治宣传用品采购项目询价通知书》\",\"remoteUrl\":\"https://gongpingjingzheng.oss-cn-chengdu.aliyuncs.com/files/20250228/西藏自治区国有资产管理委员会关于印发《区政府国资委法治宣传用品采购项目询价通知书》.docx\"}}";
        String url = "http://127.0.0.1:8686/smartApi/llmSmart/aiReview";

        SmartProviderServiceImpl service = new SmartProviderServiceImpl();
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApiSecret("a47d3bcaa81fa9655e0d24daa6ad11b0");
        applicationInfo.setApiKey("4f00c00859447773bb92d8eed18d0b33f8c6897e");
        long l = System.currentTimeMillis();
        System.out.println(l);
        System.out.println(DateUtil.getCurrentDateString());
        String signature = service.getSignature(applicationInfo, l);
        System.out.println(signature);

    }

    /**
     * 消费者，推送消息到客户端
     * @param result
     * @param llmInfo
     * @param llmResult
     * @param clientId
     * @param traceId
     */
    private long consumer(LlmCompletionsResult result, LlmInfo llmInfo, CommoneCompletionResult llmResult, String clientId, String traceId) {
        Integer totalTokens = 0;
        try {
            llmInfo.setAppKey(null);
            llmInfo.setUri(null);
            cn.hutool.json.JSONObject llmResult1 = llmResult.getLlmResult();
            result.setResult(llmResult1);
            if (llmResult1.containsKey("usage")) {
                cn.hutool.json.JSONObject usage = llmResult1.getJSONObject("usage");
                if (null != usage && usage.containsKey("total_tokens")) {
                    totalTokens = usage.getInt("total_tokens");
                    log.info("本次消耗:{},traceId:{}", totalTokens, traceId);
                }
            }
            SseEmitterUtils.send(clientId, JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalTokens;
    }

    /**
     * 检查大模型参数，通过 api 调用工作流，需要传入大模型的 appkey
     * @param nodeParam
     * @return
     */
    private String checkLllmParam(RunComponentNodeParam nodeParam) {
        // 验证是否传入大模型 apikey 参数
        List<ComponentNode> nodeList = componentNodeService.getNodeListByComponentId(nodeParam.getComponentId());
        if (CollectionUtil.isNotEmpty(nodeList)) {
            Map<String, LlmInfo> llmMap = Maps.newHashMap();
            List<LlmInfo> llmInfoList = nodeParam.getLlmInfoList();
            if (CollectionUtil.isNotEmpty(llmInfoList)) {
                HashMap<String, LlmInfo> collect = llmInfoList
                        .stream()
                        .filter(p -> StringUtils.isNotBlank(p.getModelId()))
                        .collect(Collectors.toMap(
                                LlmInfo::getModelId,
                                p -> p,
                                (k1, k2) -> k1,
                                HashMap::new
                        ));
                llmMap.putAll(collect);
            }

            boolean allMatch = nodeList.stream()
                    .filter(p -> p.getNodeType().equals(ComponentNodeEnum.LLM.getCode()))
                    .allMatch(p -> {
                        if (StringUtils.isNotBlank(p.getSettings())) {
                            cn.hutool.json.JSONObject settings = JSONUtil.parseObj(p.getSettings());
                            String modelId = settings.getStr(SettingConstants.MODEL_ID);
                            LlmInfo llmInfo = llmMap.get(modelId);
                            return null != llmInfo && StringUtils.isNotBlank(llmInfo.getAppKey());
                        }
                        return false;
                    });
            if (!allMatch) {
                return "大模型参数不合法";
            }
        }
        return StringConstant.BLANK;
    }

    /**
     * 检测推演参数
     *
     * @param param
     * @param request
     * @return
     */
    private Result checkDeductionV2Param(DeductionV2Param param, HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(param.getApiKey())) {
                return Result.fail("appkey不能为空");
            }

            // 验证参数
            ApiAppDetailParam apiAppDetailParam = new ApiAppDetailParam();
            apiAppDetailParam.setApiKey(param.getApiKey());
            ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(null, param.getApiKey(), YesNoEnum.YES.getName());
            if (null == applicationInfo) {
                return Result.fail("appkey不存在");
            }

            Result check = check(apiAppDetailParam, request, applicationInfo);
            if (!Result.success().getCode().equals(check.getCode())) {
                return check;
            }

            if (null == param.getFile()) {
                return Result.fail("文件不能为空");
            }

            // 只允许 txt 和 json 格式
            int index = param.getFile().getOriginalFilename().lastIndexOf(".");
            if (index == -1) {
                return Result.fail("文件格式错误");
            }

            String suffix = param.getFile().getOriginalFilename().substring(index + 1);
            if (!StringUtils.equalsAny(suffix, "txt", "json")) {
                return Result.fail("只允许 txt 和 json 格式的文件");
            }

            String content = new String(param.getFile().getBytes(), StandardCharsets.UTF_8);
            cn.hutool.json.JSONObject entries = JSONUtil.parseObj(content);

            // 获取智能体列表
            JSONArray jsonArray = entries.getJSONArray("智能体列表");
            if (CollectionUtil.isEmpty(jsonArray)) {
                return Result.fail("智能体列表不能为空");
            }

            // 获取交互图
            String interactionDiagram = entries.getStr("交互图");
            if (StringUtils.isBlank(interactionDiagram)) {
                return Result.fail("交互图不能为空");
            }

            // 获取交互模式
            String interactiveMode = entries.getStr("交互模式");
            if (StringUtils.isBlank(interactiveMode)) {
                return Result.fail("交互模式不能为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    /**
     * 获取主体链
     *
     * @param entries
     * @return
     */
    private List<CanvasNodeLink> getLinkList(cn.hutool.json.JSONObject entries) {
        String interactionDiagram = entries.getStr("交互图");
        CanvasNodeLink canvasNodeLink = null;
        CanvasNodeLink canvasNodeNext = null;
        List<CanvasNodeLink> linkList = Lists.newArrayList();
        String[] interactionArray = interactionDiagram.split("\n");

        List<String> subjectList = Arrays.stream(interactionArray).map(p -> {
            String[] split = p.replace(" ", StringConstant.BLANK).split(",");
            return split[0].substring(1) + "," + split[1];
        }).collect(Collectors.toList());
        List<String> reverseList = Arrays.stream(interactionArray).map(p -> {
            String[] split = p.replace(" ", StringConstant.BLANK).split(",");
            return StrUtil.reverse(split[0].substring(1) + "," + split[1]);
        }).collect(Collectors.toList());
        List<String> intersectionList = subjectList.stream().filter(reverseList::contains).flatMap(p -> Arrays.stream(p.split(","))).distinct().collect(Collectors.toList());
        List<String> filterList = subjectList.stream().filter(p -> {
            boolean subFlag = false;
            for (String intersection : intersectionList) {
                if (p.contains(intersection)) {
                    subFlag = true;
                    break;
                }
            }
            return !subFlag;
        }).collect(Collectors.toList());

        boolean exitFlag;
        for (String interaction : filterList) {
            String[] detailArray = interaction.split(",");
            Optional<CanvasNodeLink> any = linkList.stream().filter(p -> p.getLabel().equals(detailArray[0])).findAny();
            if (!any.isPresent()) {
                canvasNodeLink = new CanvasNodeLink();
                canvasNodeLink.setId(IdUtil.simpleUUID());
                canvasNodeLink.setNextList(Lists.newArrayList());
                canvasNodeLink.setLabel(detailArray[0]);
                linkList.add(canvasNodeLink);
            } else {
                canvasNodeLink = any.get();
            }

            any = linkList.stream().filter(p -> p.getLabel().equals(detailArray[1])).findAny();
            if (!any.isPresent()) {
                canvasNodeNext = new CanvasNodeLink();
                canvasNodeNext.setId(IdUtil.simpleUUID());
                canvasNodeNext.setNextList(Lists.newArrayList());
                canvasNodeNext.setLabel(detailArray[1]);
                linkList.add(canvasNodeNext);
            } else {
                canvasNodeNext = any.get();
            }
            CanvasNodeLink canvasNodeNextTemp = canvasNodeNext;
            exitFlag = canvasNodeLink.getNextList().stream().anyMatch(p -> p.getLabel().equals(canvasNodeNextTemp.getLabel()));
            if (!exitFlag) {
                canvasNodeLink.getNextList().add(canvasNodeNextTemp);
            }

            // CanvasNodeLink canvasNodePreTemp = canvasNodeLink;
            // exitFlag = canvasNodeNext.getPreList().stream().anyMatch(p -> p.getLabel().equals(canvasNodePreTemp.getLabel()));
            // if (!exitFlag) {
            //     canvasNodeNext.getPreList().add(canvasNodeLink);
            // }
        }

        linkList.forEach(item -> {
            for (CanvasNodeLink nodeLink : linkList) {
                if (CollectionUtil.isNotEmpty(nodeLink.getNextList())) {
                    boolean anyMatch = nodeLink.getNextList().stream().anyMatch(p -> p.getId().equals(item.getId()));
                    if (anyMatch) {
                        item.setIsTop("0");
                        break;
                    }
                }
            }
        });

        for (CanvasNodeLink nodeLink : linkList) {
            if (!"0".equals(nodeLink.getIsTop())) {
                nodeLink.setIsTop("1");
            }
        }

        return linkList;
    }

    /**
     * 初始化节点
     *
     * @param canvasNodeLink
     * @param aNode
     * @param leftNode
     * @param cardList
     * @param lineList
     * @param subjectNode
     * @param i
     * @param j
     * @return
     */
    private void initNode(List<ComponentNodeDto> nodes, List<ComponentNodeRel> nodeRel, CanvasNodeLink canvasNodeLink, CanvasData aNode, CanvasData leftNode, List<CanvasData> cardList, List<CanvasLine> lineList, CanvasData subjectNode, int i, int j) {
        // 复制节点
        CanvasData copy = copyNode(aNode, canvasNodeLink, i, j);
        cardList.add(copy);

        ComponentNodeDto componentNodeDto = nodes.stream().filter(p -> p.getNodeName().equals("A")).findAny().get();
        ComponentNodeDto newNode = JSONUtil.parseObj(componentNodeDto).toBean(ComponentNodeDto.class);
        newNode.setNodeId(copy.getId());
        newNode.setNodeName(copy.getData().getStr("label"));
        nodes.add(newNode);

        // 创建连接线
        CanvasLine canvasLine = buildLine(leftNode, copy);
        lineList.add(canvasLine);

        ComponentNodeRel rel = new ComponentNodeRel();
        rel.setSourceNodeId(leftNode.getData().getStr("id"));
        rel.setTargetNodeId(copy.getId());
        rel.setComponentId(nodeRel.get(0).getComponentId());
        nodeRel.add(rel);

        if (CollectionUtil.isNotEmpty(canvasNodeLink.getNextList())) {
            int size = canvasNodeLink.getNextList().size() + j;
            for (int k = j; k < size; k++) {
                initNode(nodes, nodeRel, canvasNodeLink.getNextList().get(k - j), aNode, copy, cardList, lineList, subjectNode, i, ++k);
            }
        } else {
            canvasLine = buildLine(copy, subjectNode);
            lineList.add(canvasLine);

            rel = new ComponentNodeRel();
            rel.setSourceNodeId(copy.getId());
            rel.setTargetNodeId(subjectNode.getId());
            rel.setComponentId(nodeRel.get(0).getComponentId());
            nodeRel.add(rel);
        }
    }

    /**
     * 拷贝节点
     *
     * @param aNode
     * @param canvasNodeLink
     * @return
     */
    private CanvasData copyNode(CanvasData aNode, CanvasNodeLink canvasNodeLink, int i, int j) {
        CanvasData copy = JSONUtil.parseObj(JSONUtil.toJsonStr(aNode)).toBean(CanvasData.class);
        copy.setId(canvasNodeLink.getId());
        // cardList.add(copy);
        cn.hutool.json.JSONObject data = copy.getData();
        data.set("label", canvasNodeLink.getLabel());
        data.set("id", canvasNodeLink.getId());

        copy.setPosition(new CanvasData.Position(aNode.getPosition().getX() + (j * 450), aNode.getPosition().getY() + (i * 450)));
        CanvasData.Ports ports = new CanvasData.Ports();
        List<CanvasData.PortsItems> items = aNode.getPorts().getItems();
        List<CanvasData.PortsItems> portsItems = JSONUtil.parseArray(JSONUtil.toJsonStr(items)).toList(CanvasData.PortsItems.class);
        // 前后端点的 id
        if (CollectionUtil.isNotEmpty(portsItems)) {
            portsItems.forEach(item -> {
                String[] split = item.getId().split("-");
                item.setId(split[0] + "-" + IdUtil.simpleUUID());
            });
        }

        ports.setItems(portsItems);
        copy.setPorts(ports);
        return copy;
    }

    /**
     * 构建连线
     * @param leftNode
     * @param rightNode
     * @return
     */
    private CanvasLine buildLine(CanvasData leftNode, CanvasData rightNode) {
        CanvasLine canvasLine = new CanvasLine();
        canvasLine.setId(IdUtil.simpleUUID());

        CanvasLine.NodeCar nodeCar = getNodeCard(leftNode, "right");
        canvasLine.setSource(nodeCar);

        nodeCar = getNodeCard(rightNode, "left");
        canvasLine.setTarget(nodeCar);
        return canvasLine;
    }

    /**
     * 获取端端点信息
     * @param canvasData
     * @param way
     * @return
     */
    private CanvasLine.NodeCar getNodeCard(CanvasData canvasData, String way) {
        List<CanvasData.PortsItems> items = canvasData.getPorts().getItems();
        CanvasData.PortsItems portsItems = items.stream().filter(p -> p.getGroup().equals(way)).findAny().get();
        return new CanvasLine.NodeCar(canvasData.getId(), portsItems.getId());
    }

    /**
     * 删除节点
     *
     * @param cardList
     * @param idList
     */
    private void removeCard(List<ComponentNodeDto> nodes, List<CanvasData> cardList, List<String> idList) {
        cardList.removeIf(p -> idList.contains(p.getId()));
        nodes.removeIf(p -> idList.contains(p.getNodeId()));
    }

    /**
     * 移除连接线
     *
     * @param cardList
     * @param idList
     */
    private void removeLink(List<ComponentNodeRel> nodeRel, List<CanvasLine> cardList, List<String> idList) {
        cardList.removeIf(p -> idList.contains(p.getTarget().getCell()) || idList.contains(p.getSource().getCell()));
        nodeRel.removeIf(p -> idList.contains(p.getSourceNodeId()) || idList.contains(p.getTargetNodeId()));
    }

    /**
     * 保存采集平台采集到的文档
     * @param param
     * @return
     */
    @Override
    public Result saveKnowledgeData(SaveCollectPlatformDataParam param) {
        log.info("===>开始保存采集平台文档数据，入参{}", JSONUtil.toJsonStr(param));
        List<SaveCollectPlatformDataParam.KnowledgeDataInfo> knowledgeDataInfos = param.getKnowledgeDataInfos();
        if (CollectionUtil.isEmpty(knowledgeDataInfos)) {
            return Result.success();
        }

        // 查询知识库父文件夹（名字为“全部”）
        Folders parentFolder = foldersService.getByFoldersName(KnowledgeConstant.ALL, param.getKnowledgeId());
        if (null == parentFolder) {
            return Result.fail("当前知识库名为【全部】的文件夹已被删除");
        }

        CollectPlatformFileSaveParam collectPlatformFileSaveParam = new CollectPlatformFileSaveParam();
        List<CollectPlatformFileSaveParam.FileDataInfo> fileDataInfos = new ArrayList<>();
        knowledgeDataInfos.forEach(v -> {
            CollectPlatformFileSaveParam.FileDataInfo fileDataInfo = new CollectPlatformFileSaveParam.FileDataInfo();
            fileDataInfo.setFileUrl(v.getFileUrl());
            fileDataInfo.setFileName(v.getFileName());
            fileDataInfos.add(fileDataInfo);
        });
        collectPlatformFileSaveParam.setKnowledgeId(parentFolder.getKnowledgeId());
        collectPlatformFileSaveParam.setFoldersId(parentFolder.getFoldersId());
        collectPlatformFileSaveParam.setTaskId(param.getTaskId());
        collectPlatformFileSaveParam.setFileDataInfos(fileDataInfos);
        fileService.saveCollectPlatformFile(collectPlatformFileSaveParam);

        log.info("===>结束保存采集平台文档数据");
        return Result.success();
    }

    /**
     * 保存采集平台的采集到的内容
     * @param param
     * @return
     */
    @Override
    public Result saveKnowledgeContentData(SaveCollectPlatformContentDataParam param) {
        log.info("===>开始保存采集平台内容数据，入参{}", JSONUtil.toJsonStr(param));
        urlParserInfoService.saveCollectPlatformKnowledgeContent(param);
        log.info("===>结束保存采集平台内容数据");

        return Result.success();
    }

}
