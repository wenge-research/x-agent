package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.FileDataTypeConstants;
import com.wenge.model.constants.KnowledgeApiConstant;
import com.wenge.model.dto.param.ApiDataPageParam;
import com.wenge.model.dto.param.KnowledgeApiBatchDeleteParamParam;
import com.wenge.model.dto.param.KnowledgeApiPageQueryParam;
import com.wenge.model.entity.*;
import com.wenge.model.event.TaskManageEvent;
import com.wenge.model.mapper.KnowledgeApiMapper;
import com.wenge.model.mapper.KnowledgeApiRequestRecordMapper;
import com.wenge.model.mapper.es.ApiDataMapper;
import com.wenge.model.service.ApiDataService;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.KnowledgeApiRequestRecordService;
import com.wenge.model.service.KnowledgeApiService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.FileUtils;
import com.wenge.model.workflow.component.dto.ApiRequestBody;
import com.wenge.model.workflow.entity.TriggerConfig;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.wos.exception.WosException;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.KnowledgeResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.entity.table.KnowledgeApiRequestRecordTableDef.KNOWLEDGE_API_REQUEST_RECORD;
import static com.wenge.model.entity.table.KnowledgeApiTableDef.KNOWLEDGE_API;

@Service
@Slf4j
public class KnowledgeApiServiceImpl extends ServiceImpl<KnowledgeApiMapper, KnowledgeApi> implements KnowledgeApiService {

    @Autowired
    private KnowledgeApiMapper knowledgeApiMapper;

    @Autowired
    private KnowledgeApiRequestRecordMapper knowledgeApiRequestRecordMapper;

    @Autowired
    private ApiDataService apiDataService;

    @Autowired
    private ApiDataMapper apiDataMapper;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private WosUtil wosUtil;

    @Autowired
    private DenseVectorService denseVectorService;
    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private KnowledgeApiRequestRecordService knowledgeApiRequestRecordService;

    @Override
    public Result addOrUpdate(KnowledgeApi knowledgeApi) {
        boolean isNew = false;
        if (Objects.isNull(knowledgeApi.getId())) {
            isNew = true;
            knowledgeApi.setKnowledgeApiId(IdUtil.simpleUUID());
        }
        boolean isValid = CronExpression.isValidExpression(knowledgeApi.getScheduledTask());
        if (!isValid) {
            return Result.fail("Cron表达式无效");
        }
        // 默认开启
        knowledgeApi.setStatus(1);
        knowledgeApiMapper.insertOrUpdate(knowledgeApi);
        // 发布事件
        TaskManageEvent.ChangeType type = isNew ? TaskManageEvent.ChangeType.CREATE : TaskManageEvent.ChangeType.UPDATE;
        publisher.publishEvent(new TaskManageEvent(this, knowledgeApi, type));
        return Result.success(knowledgeApi);
    }

    @Override
    public void updateKnowledgeApi(KnowledgeApi knowledgeApi) {
        knowledgeApiMapper.update(knowledgeApi);
        // 发布事件
        TaskManageEvent.ChangeType type = TaskManageEvent.ChangeType.UPDATE;
        publisher.publishEvent(new TaskManageEvent(this, knowledgeApi, type));
    }


    @Override
    public Result<Page<KnowledgeApi>> pageQuery(KnowledgeApiPageQueryParam param) {
        Wrappers<Object> queryWrapper = Wrappers.init();
        queryWrapper.where(StringUtils.isNotBlank(param.getName()), KNOWLEDGE_API.NAME.like(param.getName()))
                .and(StringUtils.isNotBlank(param.getKnowledgeId()), KNOWLEDGE_API.KNOWLEDGE_ID.eq(param.getKnowledgeId()))
                .orderBy(KNOWLEDGE_API.CREATE_TIME.desc());
        Page<KnowledgeApi> page = page(Page.of(param.getPageNo(), param.getPageSize()), queryWrapper);

        // 设置最新的一次执行时间和执行结果
        List<KnowledgeApi> knowledgeApis = page.getRecords();
        if (CollectionUtils.isNotEmpty(knowledgeApis)) {
            List<String> knowledgeApiIds = knowledgeApis.stream().map(KnowledgeApi::getKnowledgeApiId).collect(Collectors.toList());
            Wrappers recordWrapper = Wrappers.init()
                    .where(KNOWLEDGE_API_REQUEST_RECORD.KNOWLEDGE_API_ID.in(knowledgeApiIds))
                    .orderBy(KNOWLEDGE_API_REQUEST_RECORD.CREATE_TIME.desc());
            List<KnowledgeApiRequestRecord> knowledgeApiRequestRecords = knowledgeApiRequestRecordMapper.selectListByQuery(recordWrapper);
            Map<String, List<KnowledgeApiRequestRecord>> recordMap = knowledgeApiRequestRecords.stream().collect(Collectors.groupingBy(KnowledgeApiRequestRecord::getKnowledgeApiId));
            knowledgeApis.forEach(v -> {
                List<KnowledgeApiRequestRecord> records = recordMap.get(v.getKnowledgeApiId());
                if (CollectionUtils.isNotEmpty(records)) {
                    KnowledgeApiRequestRecord newRecord = records.get(0);
                    v.setScheduledTaskLastTime(newRecord.getCreateTime());
                    v.setScheduledTaskLastState(newRecord.getState());
                }
            });
        }
        return Result.success(page);
    }


    @Override
    public Result queryDetailById(Long id) {
        KnowledgeApi knowledgeApi = this.getById(id);
        return Result.success(knowledgeApi);
    }

    @Override
    public KnowledgeApi queryDetail(Long id) {
        return this.getById(id);
    }


    @Override
    public void deleteByIdList(KnowledgeApiBatchDeleteParamParam param) {
        if (CollectionUtils.isEmpty(param.getIds())) {
            return;
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.and(KNOWLEDGE_API.ID.in(param.getIds()));
        List<KnowledgeApi> knowledgeApiList = knowledgeApiMapper.selectListByQuery(queryWrapper);
        knowledgeApiList.forEach(knowledgeApi -> {
            // 发布事件
            TaskManageEvent.ChangeType type = TaskManageEvent.ChangeType.DELETE;
            publisher.publishEvent(new TaskManageEvent(this, knowledgeApi, type));
        });

        this.removeByIds(param.getIds());
    }


    /**
     * 调用API
     *
     * @return
     */
    @Override
    public JSONObject request(KnowledgeApi knowledgeApi) throws NoSuchAlgorithmException, KeyManagementException {
        JSONObject paramObj = JSONUtil.parseObj(knowledgeApi.getConfig());
        String method = knowledgeApi.getRequestType() == 0 ? KnowledgeApiConstant.METHOD_GET : KnowledgeApiConstant.METHOD_POST;
        String contentType = "";

        Map headers = new HashMap<String, String>();

        String headersStr = paramObj.getStr(KnowledgeApiConstant.REQUEST_HEADERS);
        if (StringUtils.isNotBlank(headersStr) && JSONUtil.isTypeJSONObject(headersStr)) {
            headers = JSONUtil.parseObj(headersStr);
            contentType = headers.get(KnowledgeApiConstant.REQUEST_CONTENT_TYPE).toString();
        }


        String requestBody = paramObj.getStr(KnowledgeApiConstant.REQUEST_BODY);
        boolean responseType = paramObj.getBool(KnowledgeApiConstant.RESPONSE_TYPE);
        // responseArray(todo: 后续需要根据配置的响应结果来获取)
        if (JSONUtil.isTypeJSONArray(knowledgeApi.getResponseConfigDesc())) {
            JSONArray responseArray = JSONUtil.parseArray(knowledgeApi.getResponseConfigDesc());
        } else if (JSONUtil.isTypeJSONObject(knowledgeApi.getResponseConfigDesc())) {
            JSONObject responseObj = JSONUtil.parseObj(knowledgeApi.getResponseConfigDesc());
        }

        // 替换变量`
        if (StringUtils.isNotBlank(requestBody)) {
            requestBody = replaceVariable(paramObj, requestBody);
        }
        JSONObject paramBody = new JSONObject();
        if (StringUtils.isNotBlank(requestBody)) {
            if (JSONUtil.isTypeJSONArray(requestBody)) {
                List<ApiRequestBody> list = JSONUtil.parseArray(requestBody).toList(ApiRequestBody.class);
                if (CollUtil.isNotEmpty(list)) {
                    paramBody = setParam(list);
                }
            } else {
                paramBody = JSONUtil.parseObj(requestBody);
            }
        }

        // 创建信任所有证书的 TrustManager
        X509TrustManager[] trustAllCerts = new X509TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }
        };
        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance(KnowledgeApiConstant.RESPONSE_SSL);
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpRequest httpRequest = HttpUtil.createRequest(Method.valueOf(method.toUpperCase()), knowledgeApi.getApiAddress())
                .setSSLSocketFactory(sslContext.getSocketFactory())
                .setHostnameVerifier((hostname, session) -> true)
                .timeout(300000); // 5分钟超时
        Map<String, Object> params = BeanUtil.beanToMap(paramObj);

        if (CollUtil.isNotEmpty(headers)) {
            // 构造请求头
            Map<String, String> map = headers;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                httpRequest.header(entry.getKey(), replaceVariable(params, entry.getValue()));
            }
        }
        if (Method.POST.equals(httpRequest.getMethod()) || Method.PUT.equals(httpRequest.getMethod())) {
            if (contentType.equals(KnowledgeApiConstant.REQUEST_CONTENT_TYPE_FORM)) {
                // 处理 multipart/form-data
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    Object fileUrlObj = params.get(key);
                    String fileUrl = StringConstans.BLANK;
                    if (null != fileUrlObj) {
                        fileUrl = fileUrlObj.toString();
                    }
                    // 尝试下载文件
                    byte[] fileBytes = downloadFileAsBytes(fileUrl);
                    if (fileBytes.length > 0) {
                        httpRequest.form(key, getFileNameFromUrl(fileUrl), fileBytes);
                    }
                    // 非链接的参数，作为 String 类型添加
                    httpRequest.form(key, fileUrl);
                }
            } else {
                String replace = JSONUtil.toJsonStr(paramBody);
                if (StringUtils.isNotBlank(replace)) {
                    replace = replaceVariable(params, replace);
                }
                log.info("API请求参数:{}", replace);
                httpRequest.body(replace);
            }
        }
        HttpResponse execute = httpRequest.execute();
        String result = execute.body();
        log.info("API执行结果:{}", result);
        if (execute.getStatus() != 200) {
            throw new RuntimeException("远程调用接口异常");
        }
        // 尝试解析为 JSON 数组, 这里主要是对响应数组格式的怪数据进行兼容
        JSON json = JSONUtil.parse(replaceNullWithNullString(result));
        if (json instanceof JSONArray) {
            JSONArray json1 = (JSONArray) json;
            // 这里默认取数组的第一个元素
            JSONObject mainObject = json1.getJSONObject(0);
            JSONObject resultObject = handleResponse(mainObject, responseType);
            this.addOrUpdateApiData(resultObject, knowledgeApi);
            return resultObject;
        } else if (json instanceof JSONObject) {
            // 如果解析失败，尝试解析为 JSON 对象
            JSONObject jsonObject = (JSONObject) json;
            JSONObject resultObject = handleResponse(jsonObject, responseType);
            this.addOrUpdateApiData(resultObject, knowledgeApi);
            return resultObject;
        }
        return new JSONObject();
    }

    /**
     * 处理内容
     *
     * @param content
     * @param knowledgeApi
     * @throws
     */
    private void saveAnalysisContent(String content, KnowledgeApi knowledgeApi) throws WosException {
        log.info("请求后的的结果数据: {}", content);
        if (JSONUtil.isNull(content)) {
            return;
        }
        // 增量
        if (knowledgeApi.getStorageType() == 0) {
            // 清空es
            apiDataService.clearEsData(knowledgeApi);
        }
        // 写入文件
        MultipartFile contentFile;
        try {
            contentFile = FileUtils.str2MultipartFile(content);
        } catch (IOException e) {
            log.info("url解析内容写入文件异常");
            throw new RuntimeException(e.getMessage());
        }
        MinioInfoResult contentUploadResult = wosUtil.upload(bucket, "parserUrl", contentFile, true);

        // 发给雅意，进行切片
        String resultUrl = contentUploadResult.getUrlPath();
        log.info("解析文件地址{}", resultUrl);
        String encode = URLUtil.encode(resultUrl);
        ContentParsingNewVersionResult documentResult = yayiServer.contentParsingNewVersion(encode);
        if (null == documentResult) {
            return;
        }

        ContentParsingNewVersionResult.ContentParsingNewVersionData data = documentResult.getData();
        if (null == data) {
            return;
        }
        ContentParsingNewVersionResult.FileContent fileContents = data.getFile_content();
        if (null == fileContents) {
            return;
        }

        List<ContentParsingNewVersionResult.ContentValue> fileContentLists = fileContents.getContent();
        if (CollectionUtil.isEmpty(fileContentLists)) {
            return;
        }
        // 删除minio上传的临时文件
        wosUtil.delete(bucket, resultUrl.substring(resultUrl.lastIndexOf('/') + 1));

        if (documentResult.getCode() != HttpStatus.HTTP_OK) {
            log.info("雅意切片失败");
            return;
        }

        KnowledgeParam knowledgeParam = new KnowledgeParam();
        KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
        knowledgeParam.setModel(KnowledgeParam.KNOWLEDGE_SPLIT_CONFIG);
        knowledgeContent.setRobot_id(IdUtil.simpleUUID());
        knowledgeContent.setFunction("updateDocs");
        List<KnowledgeParam.FileContent> fileContentList = BeanUtil.copyToList(fileContentLists, KnowledgeParam.FileContent.class);
        fileContentList.forEach(item -> {
            KnowledgeParam.FileInfo fileInfo = new KnowledgeParam.FileInfo();
            fileInfo.setFile_id(knowledgeContent.getRobot_id());
            fileInfo.setFile_name(knowledgeContent.getRobot_id());
            fileInfo.setFile_type("txt");
            fileInfo.setPage_num(item.getPage_num());
            item.setInfo(fileInfo);
        });
        knowledgeContent.setFile_content(fileContentList);
        knowledgeParam.setContent(knowledgeContent);
        KnowledgeResult knowledge = yayiServer.knowledgeSplit(knowledgeParam);
        if (knowledge.getCode() != HttpStatus.HTTP_OK) {
            log.info("知识库分段失败");
            return;
        }
        String accountName = AppContextHolder.getAccountName();
        KnowledgeResult.DocumentData knowledgeData = knowledge.getData();
        List<KnowledgeResult.ChunkResult> chunkResultList = knowledgeData.getChunk_result();
        if (CollectionUtil.isNotEmpty(chunkResultList)) {
            List<KnowledgeResult.TitleContent> contentList = chunkResultList.get(0).getTitle_content();
            // 进入ES批量容器
            List<ApiData> apiDataList = new ArrayList<>();
            String time = DateUtil.format(new Date(), DateUtil.PATTERN_1);
            for (KnowledgeResult.TitleContent titleContent : contentList) {
                String text = titleContent.getText();
                String id = IdUtil.randomUUID();
                ApiData apiData = ApiData.builder()
                        .knowledgeId(knowledgeApi.getKnowledgeId())
                        .knowledgeApiId(knowledgeApi.getKnowledgeApiId())
                        .createUser(accountName)
                        .updateUser(accountName)
                        .createTime(time)
                        .updateTime(time)
                        .content(content)
                        .apiName(knowledgeApi.getName())
                        .knowledgeApiId(knowledgeApi.getKnowledgeApiId())
                        .knowledgeId(knowledgeApi.getKnowledgeId())
                        .status("1")
                        .type(FileDataTypeConstants.FILE)
                        .id(id)
                        .build();
                denseVectorService.modelEncode(text, knowledgeApi.getKnowledgeId(), apiData, CONTENT_DENSE_FILED);

                apiDataList.add(apiData);
            }

            apiDataMapper.insertBatch(apiDataList);
        }
    }

    /**
     * 添加数据到es
     * ，不需要删除如果是增量的
     * 如果是全量的，需要全部删除
     *
     * @param result
     * @param knowledgeApi
     */
    private void addOrUpdateApiData(JSONObject result, KnowledgeApi knowledgeApi) {

        ApiDataPageParam param = new ApiDataPageParam();
        param.setKnowledgeApiId(knowledgeApi.getKnowledgeApiId());
        String content = setJSONObjResult(result, knowledgeApi);
        try {
            // 保存数据
            saveAnalysisContent(content, knowledgeApi);
        } catch (Exception e) {
            //todo
        }
    }

    /**
     * 获取需要保存到向量数据的字段
     *
     * @param result
     * @param knowledgeApi
     * @return
     */
    private String setJSONObjResult(JSONObject result, KnowledgeApi knowledgeApi) {
        JSONObject responseObj = convertJsonArrayToJsonObjByKeyField(
                knowledgeApi.getResponseConfigDesc(), result);
        return JSONUtil.toJsonStr(responseObj);
    }

    /**
     * 处理响应对象
     *
     * @param jsonObject   响应的 JSON 对象
     * @param responseType 响应类型
     * @return 处理后的 JSON 对象
     */
    private JSONObject handleResponse(JSONObject jsonObject, boolean responseType) {
        if (responseType) {
            // 返回类型为字符串
            JSONObject entries = new JSONObject();
            entries.set(KnowledgeApiConstant.TEXT, jsonObject.toString());
            return entries;
        }
        return jsonObject;
    }

    private static String replaceNullWithNullString(String input) {
        // 使用正则表达式匹配null
        Pattern pattern = Pattern.compile("null");
        Matcher matcher = pattern.matcher(input);
        // 替换所有匹配到的null为"null"
        return matcher.replaceAll("\"null\"");
    }

    /**
     * 替换文本中的${}
     *
     * @param params  变量名-> 变量值
     * @param replace 待替换文本
     * @return
     */
    private static String replaceVariable(Map<String, Object> params, String replace) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            // 这里的类型可能是ArrayFile
            if (entry.getValue() instanceof String || entry.getValue() instanceof Number || entry.getValue() instanceof Boolean) {
                String convertedText = entry.getValue().toString().replace("\r\n", "\\n").replace("\n", "\\n");
                // 替换制表符
                convertedText = convertedText.replaceAll("\\u0009", "\n");
                String json = new Gson().toJson(convertedText);
                replace = StringUtils.replace(replace, "${" + entry.getKey() + "}", json.substring(1, json.length() - 1));
            }
        }
        return replace;
    }

    /**
     * 递归设置参数
     *
     * @param list
     * @return
     */
    private JSONObject setParam(List<ApiRequestBody> list) {
        JSONObject paramBody = new JSONObject();
        list.forEach(config -> {
            config.setFullName(config.getName());
            if (CollUtil.isEmpty(config.getChildren())) {
                if (KnowledgeApiConstant.PARAM_TYPE_ARRAY.equals(config.getType()) && JSONUtil.isTypeJSONArray(config.getValue().toString())) {
                    JSONArray objects = JSONUtil.parseArray(config.getValue());
                    paramBody.set(config.getName(), objects);
                } else if (KnowledgeApiConstant.PARAM_TYPE_NUMBER.equals(config.getType())) {
                    paramBody.set(config.getName(), Double.valueOf(config.getValue().toString()));
                } else if (KnowledgeApiConstant.PARAM_TYPE_OBJECT.equals(config.getType())) {
                    paramBody.set(config.getName(), JSONUtil.parse(config.getValue()));
                } else {
                    paramBody.set(config.getName(), config.getValue());
                }
            } else {
                List<ApiRequestBody> children = config.getChildren();
                JSONObject entries = setParam(children);
                paramBody.set(config.getName(), entries);
            }
        });
        return paramBody;
    }

    /**
     * 下载文件为字节流
     *
     * @param fileUrl 文件链接
     * @return 文件字节流
     */
    private byte[] downloadFileAsBytes(String fileUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fileUrl).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("文件下载失败: " + response);
            }
            return response.body().bytes();
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败: " + fileUrl, e);
        }
    }

    /**
     * 从文件链接中获取文件名
     *
     * @param fileUrl 文件链接
     * @return 文件名
     */
    private String getFileNameFromUrl(String fileUrl) {
        String[] parts = fileUrl.split("/");
        return parts[parts.length - 1];
    }

    /**
     * 运行api
     *
     * @param knowledgeApi
     * @return
     */
    @Override
    public Result runApi(KnowledgeApi knowledgeApi) {
        try {
            if (!ObjectUtils.isEmpty(knowledgeApi.getId())) {
                KnowledgeApi knowledgeApiInfo = getById(knowledgeApi.getId());

                return Result.success(request(knowledgeApiInfo));
            } else {
                return Result.success(request(knowledgeApi));
            }
        } catch (Exception e) {
            log.error("api执行出现异常");
            e.printStackTrace();
        }
        return Result.success(new JSONObject());
    }

    /**
     * 处理业务
     *
     * @param knowledgeApi
     */
    @Override
    public void requestTask(KnowledgeApi knowledgeApi) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("开始同步知识库文件数据");
        JSONObject result;
        try {
            result = this.request(knowledgeApi);
            stopWatch.stop();
            log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeMillis() + "ms");
            if (!result.isEmpty()) {
                saveExec(result, knowledgeApi, stopWatch.getTotalTimeMillis(), true);
            }
        } catch (Exception e) {
            result = new JSONObject();
            result.set("errorMsg", e.getMessage());
            //  失败
            saveExec(result, knowledgeApi, stopWatch.getTotalTimeMillis(), false);
            log.error("api执行出现异常");
            e.printStackTrace();

        }
    }

    /**
     * 保存接口请求记录(暂时写死，后续需要根据返回的结果集合来判断)
     *
     * @param result       接口返回结果
     * @param knowledgeApi
     * @param executeTime  接口执行时间
     */
    private void saveExec(JSONObject result, KnowledgeApi knowledgeApi, long executeTime, boolean isSuccess) {
        String code = result.getStr(KnowledgeApiConstant.RESPONSE_CODE);
        String msg = result.getStr(KnowledgeApiConstant.RESPONSE_MSG);
        boolean success = result.getBool(KnowledgeApiConstant.RESPONSE_SUCCESS);
        KnowledgeApiRequestRecord knowledgeApiRequestRecord = new KnowledgeApiRequestRecord();
        knowledgeApiRequestRecord.setRequestRecordId(IdUtil.simpleUUID());
        knowledgeApiRequestRecord.setKnowledgeApiId(knowledgeApi.getKnowledgeId());
        knowledgeApiRequestRecord.setKnowledgeApiId(knowledgeApi.getKnowledgeApiId());
        knowledgeApiRequestRecord.setDeleteFlag(0);
        knowledgeApiRequestRecord.setState(isSuccess
                && (Result.success().getCode().equals(code)
                || Result.success().getMsg().equals(msg))
                || success ? 1 : 0);
        if (!isSuccess) {
            knowledgeApiRequestRecord.setExecuteMsg(result.getStr("errorMsg"));
        } else {
            knowledgeApiRequestRecord.setExecuteMsg(result.getStr("msg"));
        }
        knowledgeApiRequestRecord.setExecuteTime(executeTime);
        // 添加请求记录
        knowledgeApiRequestRecordService.save(knowledgeApiRequestRecord);
        KnowledgeApi updateKnowledgeApi = new KnowledgeApi();
        updateKnowledgeApi.setId(knowledgeApi.getId());
        updateKnowledgeApi.setKnowledgeApiId(knowledgeApi.getKnowledgeApiId());
        String now = cn.hutool.core.date.DateUtil.now();
        updateKnowledgeApi.setScheduledTaskLastTime(now);
        updateKnowledgeApi.setScheduledTaskLastState(knowledgeApiRequestRecord.getState());
        updateKnowledgeApi.setUpdateTime(now);
        updateKnowledgeApi(updateKnowledgeApi);

    }

    /**
     * 将 JSON 数组转换为 JSON 对象，使用数组中指定字段的值作为新对象的键。
     *
     * @param jsonArrayStr JSON 数组字符串
     * @return 转换后的 JSON 对象
     */
    public JSONObject convertJsonArrayToJsonObjByKeyField(String jsonArrayStr, JSONObject result) {
        // 解析JSON数组
        JSONArray jsonArray = JSONUtil.parseArray(jsonArrayStr);

        // 创建一个新的JSONObject
        JSONObject returnObj = new JSONObject();
        // 遍历数组并将每个元素添加到新的JSONObject中，使用指定字段作为键
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            JSONObject item = jsonArray.getJSONObject(i);
            String name = item.getStr("name"); // 字段描述，主要用于存储
            String key = item.getStr("parms"); // 使用指定字段作为键
            String isSave = item.getStr("isSave"); // 使用指定字段作为键

            if (StringUtils.isNotBlank(name)) {
                jsonObject.set("param", key);

                jsonObject.set("description", name);
                jsonObject.set("id", item.getStr("id"));

                String children = item.getStr("children");
                if (StringUtils.isNotBlank(children)) {
                    JSONObject child = JSONUtil.parseObj(result.getStr(key));
                    JSONObject childrenObject = convertJsonArrayToJsonObjByKeyField(children, child);
                    for (String keyStr : childrenObject.keySet()) {
                        childrenObject.set(keyStr, childrenObject.get(keyStr));
                    }
                    jsonObject.set("value", childrenObject);
                } else {
                    jsonObject.set("value", result.getStr(key));

                }
                returnObj.set(key, jsonObject);
            }
        }
        return returnObj;
    }

}