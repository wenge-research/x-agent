package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.config.DecisionOneConfig;
import com.wenge.model.config.DecisionOneWBListen;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.DecisionJsonData;
import com.wenge.model.mapper.es.DecisionJsonDataMapper;
import com.wenge.model.service.DecisionOneService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.SseEmitterUtils;
import com.wenge.model.utils.YayiUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.elasticsearch.dto.EsDocumentVo;
import com.wg.appframe.elasticsearch.service.EsRestHighLevelClientService;
import com.wg.appframe.yayi.constants.StringConstans;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class DecisionOneServiceImpl implements DecisionOneService {

    @Autowired
    private DecisionOneConfig decisionOneConfig;

    @Autowired
    private DecisionJsonDataMapper decisionJsonDataMapper;

    @Autowired
    private EsRestHighLevelClientService esRestHighLevelClientService;

    @Resource(name = "decisionOne")
    private ThreadPoolExecutor decisionOne;

    @Override
    public Result<List<DecisionScenariosResult>> scenarios(EmptyParam param) {
        try {
            log.info("获取决策场景:{}", decisionOneConfig.getHost() + decisionOneConfig.getScenariosApi());
            String response = HttpUtil.get(decisionOneConfig.getHost() + decisionOneConfig.getScenariosApi());
            log.info("获取决策场景结果:{}", response);
            DecisionScenariosResult bean = new DecisionScenariosResult();
            if (StringUtils.isNotBlank(response)) {
                List<DecisionScenariosResult> list = JSONUtil.toList(response, DecisionScenariosResult.class);
                return Result.success(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(ListUtil.toList());
    }

    @Override
    public SseEmitter connectDecision(DecisionProcessParam param) {
        if (StringUtils.isBlank(param.getClientId()) || StringUtils.isBlank(param.getSessionId())) {
            return null;
        }
        SseEmitter sseEmitter = SseEmitterUtils.getConnection(param.getClientId(), 1000 * 60 * 90);
        JSONObject msg = new JSONObject();
        msg.set("type", "connect");
        try {
            // 获取websocket连接
            String url = decisionOneConfig.getWebsockt();
            url = url.replace("{session_id}", param.getSessionId());
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).build();
            DecisionOneWBListen decisionOneWBListen = DecisionOneWBListen.WEB_SOCKET_MAP.get(param.getSessionId());
            if (null == decisionOneWBListen) {
                decisionOneWBListen = new DecisionOneWBListen(param.getSessionId());
            }
            decisionOneWBListen.setClientId(param.getClientId());

            client.newWebSocket(request, decisionOneWBListen);
            // 处理状态更新
            msg.set("detail", "会话已连接");
            SseEmitterUtils.sendMsg(param.getClientId(), JSONUtil.toJsonStr(msg));

        } catch (Exception e) {
            e.printStackTrace();
            msg.set("detail", "连接推演服务异常:" + e.getMessage());
            try {
                SseEmitterUtils.sendMsg(param.getClientId(), JSONUtil.toJsonStr(msg));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        log.info("websocket连接成功:{}", param.getSessionId());
        return sseEmitter;
    }

    @Override
    public void exportDecision(StringParam param, HttpServletResponse response) {
        if (StringUtils.isBlank(param.getParam())) {
            return;
        }
        String exportApi = decisionOneConfig.getExportApi();
        exportApi = exportApi.replace("{session_id}", param.getParam());
        log.info("导出决策api:{}, param:{}",decisionOneConfig.getHost() + exportApi, param.getParam());
        String post = HttpUtil.post(decisionOneConfig.getHost() + exportApi, JSONUtil.toJsonStr(new JSONObject()));
        log.info("导出结果:{}", post);
        if (StringUtils.isNotBlank(post)) {
            JSONObject entries = JSONUtil.parseObj(post);
            if ("导出成功".equals(entries.getStr("message"))) {
                String filename = entries.getStr("filename");
                if (StringUtils.isBlank(filename)) {
                    return;
                }

                String downloadFIleApi = decisionOneConfig.getDownloadFIleApi();
                downloadFIleApi = downloadFIleApi
                        .replace("{session_id}", param.getParam())
                        .replace("{filename}", filename);
                try {
                    // 下载文件
                    response.setHeader("Content-Disposition", "attachment;filename=" + filename);
                    response.setContentType("application/octet-stream");
                    response.setCharacterEncoding("utf-8");
                    log.info("下载文件api:{}", decisionOneConfig.getHost() + downloadFIleApi);
                    HttpUtil.download(decisionOneConfig.getHost() + downloadFIleApi, response.getOutputStream(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public Result<DecisionInitResult> initDecision(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.success(new DecisionInitResult());
        }
        try {
            JSONObject entries = new JSONObject();
            entries.set("scenario_file", param.getParam());
            log.info("初始化推演会话api:{}, param:{}", decisionOneConfig.getHost() + decisionOneConfig.getInitApi(), JSONUtil.toJsonStr(entries));
            String response = HttpUtil.post(decisionOneConfig.getHost() + decisionOneConfig.getInitApi(), JSONUtil.toJsonStr(entries));
            log.info("初始化决策结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                DecisionInitResult bean = JSONUtil.toBean(response, DecisionInitResult.class);
                return Result.success(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(new DecisionInitResult());
    }

    @Override
    public Result<DecisionStatusResult> decisionStatus(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.success(new DecisionStatusResult());
        }
        try {
            String getStatusApi = decisionOneConfig.getStatusApi();
            getStatusApi = getStatusApi.replace("{session_id}", param.getParam());
            log.info("检查会话状态 api:{}", decisionOneConfig.getHost() + getStatusApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + getStatusApi);
            log.info("检查会话状态结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                DecisionStatusResult bean = JSONUtil.toBean(response, DecisionStatusResult.class);
                return Result.success(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(new DecisionStatusResult());
    }

    @Override
    public Result<DecisionRunResult> runDecision(DecisionRunParam param) {
        try {
            JSONObject entries = new JSONObject();
            entries.set("broadcast_targets", param.getBroadcastTargets());
            entries.set("user_event", param.getUserEvent());
            String nextRoundApi = decisionOneConfig.getNextRoundApi();
            nextRoundApi = nextRoundApi.replace("{session_id}", param.getSessionId());
            log.info("执行推演会话api:{}, param:{}", decisionOneConfig.getHost() + nextRoundApi, JSONUtil.toJsonStr(entries));
            String response = HttpUtil.post(decisionOneConfig.getHost() + nextRoundApi, JSONUtil.toJsonStr(entries));
            log.info("执行推演会话结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                DecisionRunResult bean = JSONUtil.toBean(response, DecisionRunResult.class);
                return Result.success(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(new DecisionRunResult());
    }

    @Override
    public Result<JSONObject> plans(DecisionPlansParam param) {
        if (StringUtils.isBlank(param.getSessionId())) {
            return Result.fail("会话id不能为空!");
        }
        if (StringUtils.isBlank(param.getRound())) {
            return Result.fail("轮次不能为空!");
        }
        try {
            String getPlansApi = decisionOneConfig.getPlans();
            getPlansApi = getPlansApi
                    .replace("{session_id}", param.getSessionId())
                    .replace("{round}", param.getRound());
            log.info("获取推演计划api:{}", decisionOneConfig.getHost() + getPlansApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + getPlansApi);
            log.info("获取推演计划结果:{}", response);
            if (StringUtils.isBlank(response)) {
                return Result.fail("获取推演计划失败!");
            }
            return Result.success(JSONUtil.parseObj(response));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<JSONObject>> agents(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail("会话id不能为空!");
        }
        try {
            String agentsApi = decisionOneConfig.getAgentsApi();
            agentsApi = agentsApi.replace("{session_id}", param.getParam());
            log.info("获取推演智能体信息api:{}", decisionOneConfig.getHost() + agentsApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + agentsApi);
            log.info("获取推演智能体信息结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                if (JSONUtil.isTypeJSONArray(response)) {
                    List<JSONObject> jsonObjects = JSONUtil.toList(JSONUtil.parseArray(response), JSONObject.class);
                    return Result.success(jsonObjects);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(new ArrayList<>());
    }

    @Override
    public Result<JSONObject> environment(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail("会话id不能为空!");
        }
        try {

            String environmentApi = decisionOneConfig.getEnvironmentApi();
            environmentApi = environmentApi.replace("{session_id}", param.getParam());
            log.info("获取推演环境信息api:{}", decisionOneConfig.getHost() + environmentApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + environmentApi);
            log.info("获取推演环境信息结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                JSONObject jsonObject = JSONUtil.parseObj(response);
                return Result.success(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }
        return Result.success(new JSONObject());
    }

    @Override
    public Result<String> saveJson(DecisionJsonSaveParam param) {
        if (StringUtils.isBlank(param.getSessionId())) {
            return Result.fail("会话id不能为空!");
        }
        if (null == param.getDetail()) {
            return Result.fail("会话详情不能为空!");
        }

        DecisionJsonData data = new DecisionJsonData();
        data.setSessionId(param.getSessionId());
        // 随机 3 位
        String fileName = DateUtil.getCurrentTime() + "_" + RandomUtil.randomNumbers(3) + ".json";
        data.setFileName(fileName);
        data.setDetail(param.getDetail());
        data.setId(IdUtil.simpleUUID());
        String indexName = decisionJsonDataMapper.getIndex().getIndices()[0];
        EsDocumentVo indexDocument = EsDocumentVo.builder()
                .index(indexName)
                .documentId(data.getId())
                .type("_doc")
                .jsonDocument(data)
                .build();
        boolean saveFlag = false;
        try {
            // 将 param.getDetail() 对象中，所有字段都改为 string，json 有多层 对象，需要处理
            saveFlag = esRestHighLevelClientService.saveBatch(indexName, ListUtil.toList(indexDocument));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!saveFlag) {
            return Result.fail("保存失败");
        }
        return Result.success(fileName);
    }

    @Override
    public Result<DecisionJsonData> getDecisionJson(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail("文件名称不能为空!");
        }
        // 获取文件
        DecisionJsonData decisionJsonData = getDecisionJsonData(param.getParam());

        if (null == decisionJsonData) {
            return Result.fail("文件不存在!");
        }
        return Result.success(decisionJsonData);
    }

    @Override
    public Result<JSONObject> interactions(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail("会话id不能为空!");
        }

        try {
            String interactionsApi = decisionOneConfig.getInteractionsApi();
            interactionsApi = interactionsApi.replace("{session_id}", param.getParam());
            log.info("获取会话交互信息api:{}", decisionOneConfig.getHost() + interactionsApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + interactionsApi);
            log.info("获取会话交互信息结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                JSONObject jsonObject = JSONUtil.parseObj(response);
                return Result.success(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }
        return Result.success(new JSONObject());
    }

    @Override
    public Result<JSONObject> actions(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail("会话id不能为空!");
        }
        try {
            String actionsApi = decisionOneConfig.getActionsApi();
            actionsApi = actionsApi.replace("{session_id}", param.getParam());
            log.info("获取会话动作信息api:{}", decisionOneConfig.getHost() + actionsApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + actionsApi);
            log.info("获取会话动作信息结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                JSONObject jsonObject = JSONUtil.parseObj(response);
                return Result.success(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(new JSONObject());
    }

    @Override
    public Result<JSONObject> decisions(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail("会话id不能为空!");
        }
        try {
            String decisionsApi = decisionOneConfig.getDecisionsApi();
            decisionsApi = decisionsApi.replace("{session_id}", param.getParam());
            log.info("智能体决策的api:{}", decisionOneConfig.getHost() + decisionsApi);
            String response = HttpUtil.get(decisionOneConfig.getHost() + decisionsApi);
            log.info("智能体决策的结果:{}", response);
            if (StringUtils.isNotBlank(response)) {
                JSONObject jsonObject = JSONUtil.parseObj(response);
                return Result.success(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("连接推演服务异常：" + e.getMessage());
        }

        return Result.success(new JSONObject());
    }

    @Override
    public SseEmitter decisionsReport(DecisionsReportParam param) {
        if (StringUtils.isBlank(param.getClientId())) {
            return null;
        }
        SseEmitter connection = SseEmitterUtils.getConnection(param.getClientId(), 1000 * 60 * 60 * 24);
        decisionOne.submit(() -> requestReport(param, decisionOneConfig.getReportBaseApi()));
        return connection;
    }

    @Override
    public SseEmitter decisionsSummary(DecisionsReportParam param) {
        if (StringUtils.isBlank(param.getClientId())) {
            return null;
        }
        SseEmitter connection = SseEmitterUtils.getConnection(param.getClientId(), 1000 * 60 * 60 * 24);
        decisionOne.submit(() -> requestReport(param, decisionOneConfig.getSummaryApi()));
        return connection;

    }

    /**
     * 获取决策json数据
     * @param fileName
     * @return
     */
    private DecisionJsonData getDecisionJsonData(String fileName) {
        LambdaEsQueryWrapper<DecisionJsonData> wrapper = EsWrappers.lambdaQuery(DecisionJsonData.class)
                .eq(StringUtils.isNotBlank(fileName), DecisionJsonData::getFileName, fileName);

        return decisionJsonDataMapper.selectOne(wrapper);
    }

    /**
     * 请求报告
     *
     * @param param
     * @return
     */
    private void requestReport(DecisionsReportParam param, String api) {
        try {
            JSONObject entries = new JSONObject();
            if (StringUtils.isBlank(param.getSessionId())) {
                sendMsg(param.getClientId(), "会话id不能为空!", Result.fail().getCode(), null);
                return;
            }
            if (StringUtils.isBlank(param.getFileName())) {
                sendMsg(param.getClientId(), "文件名称不能为空!", Result.fail().getCode(), null);
                return;
            }

            sendMsg(param.getClientId(), "正在检索 json 文件", Result.success().getCode(), null);
            DecisionJsonData decisionJsonData = getDecisionJsonData(param.getFileName());
            if (null == decisionJsonData) {
                sendMsg(param.getClientId(), "获取推演文件失败!", Result.fail().getCode(), null);
                return;
            }

            JSONObject detail = decisionJsonData.getDetail();
            sendMsg(param.getClientId(), "已获取 json 文件", Result.success().getCode(), detail);

            if (detail.containsKey("detail")) {
                detail = detail.getJSONObject("detail");
            }
            JSONObject metadata = detail.getJSONObject("metadata");

            // 获取环境
            JSONObject environments = detail.getJSONObject("environments");

            // 获取事件
            String event = metadata.getStr("event");
            // 获取上下文
            String context = metadata.getStr("context");

            // 获取智能体
            StringParam stringParam = new StringParam();
            stringParam.setParam(param.getSessionId());
            sendMsg(param.getClientId(), "正在检取智能体", Result.success().getCode(), null);
            Result<List<JSONObject>> agents = agents(stringParam);
            List<JSONObject> agentsData = agents.getData();
            if (CollectionUtil.isEmpty(agentsData)) {
                entries.set("code", Result.fail().getCode());
                entries.set("msg", "获取智能体失败!");
                SseEmitterUtils.sendMsg(param.getClientId(), JSONUtil.toJsonStr(entries));
                return;
            }
            sendMsg(param.getClientId(), "已获取智能体", Result.success().getCode(), agentsData);

            // 获取交互信息
            sendMsg(param.getClientId(), "正在检索交互信息", Result.success().getCode(), null);
            Result<JSONObject> actions = actions(stringParam);
            JSONObject actionsData = actions.getData();
            if (null == actionsData) {
                sendMsg(param.getClientId(), "获取交互信息失败!", Result.fail().getCode(), null);
                return;
            }

            JSONArray actionsList = actionsData.getJSONArray("actions");
            if (CollectionUtil.isEmpty(actionsList)) {
                sendMsg(param.getClientId(), "获取交互信息失败!", Result.fail().getCode(), null);
                return;
            }
            sendMsg(param.getClientId(), "已获取交互信息", Result.success().getCode(), actionsList);

            // 获取决策
            sendMsg(param.getClientId(), "正在检索决策信息", Result.success().getCode(), null);
            Result<JSONObject> decisions = decisions(stringParam);
            JSONObject decisionsData = decisions.getData();
            if (null == decisionsData) {
                sendMsg(param.getClientId(), "获取决策信息失败!", Result.fail().getCode(), null);
                return;
            }

            JSONArray decisionsList = decisionsData.getJSONArray("decisions");
            if (CollectionUtil.isEmpty(decisionsList)) {
                sendMsg(param.getClientId(), "获取决策信息失败!", Result.fail().getCode(), null);
                return;
            }

            sendMsg(param.getClientId(), "获取决策信息", Result.fail().getCode(), decisionsList);

            JSONObject params = new JSONObject();
            params.set("id", param.getSessionId());
            params.set("model", "gpt4");
            params.set("report_type", "full_report");
            params.set("workers", 4);
            params.set("event", event);
            params.set("context", context);
            params.set("agents", agentsData);
            params.set("environments", environments);
            params.set("interactions", actionsList);
            params.set("decisions", decisionsList);
            params.set("title", param.getTitle());
            params.set("stream", true);
            log.info("获取报告 api:{}, 参数:{}", api, JSONUtil.toJsonStr(params));

            // 构建请求体
            String requestBody = JSONUtil.toJsonStr(params);

            // 创建HTTP请求
            HttpRequest request = HttpUtil.createPost(api)
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .body(requestBody);
            CommonApiParam paramHead = new CommonApiParam();
            paramHead.setAppKey("68d32d7e72174088b58af3aab90bb986");
            paramHead.setAppSecret("159569a2c7b84990a244d86798145174");
            com.alibaba.fastjson2.JSONObject heanders = new com.alibaba.fastjson2.JSONObject();
            heanders.put("Content-Type", "application/json");
            heanders.put("Accept", "*/*");
            paramHead.setHeader(heanders);
            api = api.replace("https://yayi.wenge.com/saas-gateway", StringConstans.BLANK)
                    .replace("https://stg-yayi.wenge.com/saas-gateway", StringConstans.BLANK)
                    .replace("https://yayi.wenge.com/saas-gateway", StringConstans.BLANK)
                    // 兼容旧版本域名
                    .replace("https://tilake.wenge.com/saas-gateway", StringConstans.BLANK)
                    .replace("https://stg-tilake.wenge.com/saas-gateway", StringConstans.BLANK);
            YayiUtils.setHeader(request, api, paramHead);
            sendMsg(param.getClientId(), "正在生成内容", Result. success().getCode(), null);

            // 执行请求并处理流式响应
            try (HttpResponse response = request.execute()) {
                if (response.isOk()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.bodyStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("收到数据: " + line);
                        // 处理SSE格式的数据
                        if (line.startsWith("data: ")) {
                            String jsonData = line.substring(6).trim();
                            SseEmitterUtils.sendMsg(param.getClientId(), jsonData);
                        }
                    }
                } else {
                    sendMsg(param.getClientId(), "请求失败: " + response.getStatus() + " - " + response.body(), Result. fail().getCode(), null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SseEmitterUtils.completeDelay(param.getClientId());
        }
    }

    /**
     * 发送消息
     *
     * @param clientId
     * @param msg
     * @param code
     */
    private void sendMsg(String clientId, String msg, String code, Object data) {
        try {
            JSONObject entries = new JSONObject();
            entries.set("code", code);
            entries.set("msg", msg);
            if (null != data) {
                entries.set("data", data);
            }
            SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(entries));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
}
