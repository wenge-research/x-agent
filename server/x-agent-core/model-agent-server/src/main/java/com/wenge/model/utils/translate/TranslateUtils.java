package com.wenge.model.utils.translate;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.IntelligentTranslationRecord;
import com.wenge.model.utils.SseEmitterUtils;
import com.wenge.oauth.util.JsonUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.result.YayiTranslationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @Author:caohaifeng
 * @createTime:2024-09-24 17:14
 * @Description: 翻译工具类 集成第三方
 * @Version:1.0
 */
@Slf4j
@Service
public class TranslateUtils {

    private static YayiServer yayiServer;

    private static RestTemplate restTemplate;

    private static String aliyunTranslateTextUrl;
    private static String aliyunTranslateFileUrl;
    private static String aliyunTranslateUploadUrl;


    private static String wengeTranslateTextUrl;

    private static String wengeTranslateXAppKey;

    @Value("${aliyun-translate.textUrl}")
    public void setAliyunTranslateTextUrl(String aliyunTranslateTextUrl) {
        this.aliyunTranslateTextUrl = aliyunTranslateTextUrl;
    }

    @Value("${aliyun-translate.fileUrl}")
    public void setAliyunTranslateFileUrl(String aliyunTranslateFileUrl) {
        this.aliyunTranslateFileUrl = aliyunTranslateFileUrl;
    }

    @Value("${aliyun-translate.uploadUrl}")
    public void setAliyunTranslateUploadUrl(String aliyunTranslateUploadUrl) {
        this.aliyunTranslateUploadUrl = aliyunTranslateUploadUrl;
    }

    @Value("${wenge-translate.textUrl}")
    public void setWengeTranslateTextUrl(String wengeTranslateTextUrl) {
        this.wengeTranslateTextUrl = wengeTranslateTextUrl;
    }

    @Value("${wenge-translate.x-app-key}")
    public void setWengeTranslateXAppKey(String wengeTranslateXAppKey) {
        this.wengeTranslateXAppKey = wengeTranslateXAppKey;
    }


    @Autowired
    public TranslateUtils(YayiServer yayiServer, RestTemplate restTemplate) {
        this.yayiServer = yayiServer;
        this.restTemplate = restTemplate;
    }

    /**
     * @author: caohaifeng
     * @date: 2024/9/24 14:57
     * @Description: 实际执行翻译的逻辑 目前使用雅意 后续需要替换为aliyun
     * @Version:1.0
     **/
    public static String getTranslateContent(String text, String srcLang, String tgtLang) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        try {
            //雅意翻译
            YayiTranslationResult yayiTranslationResult = yayiServer.yayiTranslate(text, srcLang, tgtLang);
            if (StringConstans.CODE.equals(yayiTranslationResult.getCode())) {
                YayiTranslationResult.TranslationData data = yayiTranslationResult.getData();
                return data.getTgt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("调用翻译失败了....");
            return text;
        }
        return text;
    }


    public static JSONObject getAliyunTranslateContent(String text, String srcLang, String tgtLang, String fileUrl) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("source_language", srcLang);
        jsonObject.put("target_language", tgtLang);
        if (StringUtils.isNotBlank(fileUrl)) {
            jsonObject.put("file_url", fileUrl);
            jsonObject.put("upload_url", aliyunTranslateUploadUrl);
            log.info(jsonObject.toJSONString());
            JSONObject result = restTemplate.postForObject(
                    aliyunTranslateFileUrl,
                    jsonObject,
                    JSONObject.class);
            return result;
        } else {
            jsonObject.put("source_text", text);
            log.info(jsonObject.toJSONString());
            JSONObject result = restTemplate.postForObject(
                    aliyunTranslateTextUrl,
                    jsonObject,
                    JSONObject.class);
            return result;
        }
    }


    public static JSONObject getWengeTranslateContent(String text, String srcLang, String tgtLang, String fileUrl) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sourceLang", srcLang);
        jsonObject.put("targetLang", tgtLang);
        jsonObject.put("termbaseId", "123456");
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-app-key", wengeTranslateXAppKey);

        // 创建 HttpEntity
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        if (StringUtils.isNotBlank(fileUrl)) {
            return null;
        } else {
            jsonObject.put("context", text);
            log.info(jsonObject.toJSONString());
            // 使用 RestTemplate 发送 POST 请求
            ResponseEntity<InputStream> responseEntity = restTemplate.exchange(
                    wengeTranslateTextUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<InputStream>() {
                    });

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                InputStream inputStream = responseEntity.getBody(); // 注意：这里实际上RestTemplate不直接返回InputStream，这里只是为了说明
                // 由于RestTemplate不直接支持返回InputStream，我们需要变通处理
                // 一种方法是使用ResponseExtractor，但这里为了简化，我们假设已经有了一个InputStream
                // 在实际应用中，你可能需要使用其他HTTP客户端库来获取InputStream

                // 下面的代码假设你已经有了InputStream
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                log.info(stringBuilder.toString());
                return null;
            } else {
                throw new RuntimeException("请求失败: " + responseEntity.getStatusCode());
            }
        }
    }

    public static SseEmitter getWengeTranslate(IntelligentTranslationRecord param) {
        SseEmitter sseEmitter = SseEmitterUtils.getConnection(param.getClientId());
//        try {
//            sseEmitter.send("22222312");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-app-key", wengeTranslateXAppKey);

        URI url = URI.create(wengeTranslateTextUrl);

        String requestBody = constructRequestBody(param.getText(), param.getSrcLang(), param.getTgtLang(), "123");
        StringBuilder stringBuilder = new StringBuilder();

        RequestCallback requestCallback = clientHttpRequest -> {
            clientHttpRequest.getHeaders().addAll(headers);
            clientHttpRequest.getBody().write(requestBody.getBytes(StandardCharsets.UTF_8));
        };
        JSONObject paramObj = JSONUtil.toBean(JsonUtil.objectToJson(param), JSONObject.class);
        ResponseExtractor<Void> responseExtractor = clientHttpResponse -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    clientHttpResponse.getBody(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("event:data")) {
                    String dataLine = reader.readLine();
                    if (dataLine != null && dataLine.startsWith("data:")) {
                        JSONObject jsonObject = JSONUtil.toBean(dataLine.substring(5), JSONObject.class);
                        String data = jsonObject.get("context").toString();
                        if (!"".equals(data) && !"[DONE]".equals(data)) {
                            jsonObject.put("context", stringBuilder.append(data));
                            jsonObject.putAll(paramObj);
                            SseEmitterUtils.sendMsg(param.getClientId(), JSONUtil.toJsonStr(jsonObject)); //发送当前行的数据
                        } else if ("[DONE]".equals(data)) {
                            break;
                        }
                    }
                }
            }
            return null;
        };
        restTemplate.execute(url, HttpMethod.POST, requestCallback, responseExtractor);

        SseEmitterUtils.completeDelay(param.getClientId());
        param.setTextTranslate(stringBuilder.toString());
        return sseEmitter;
    }


    private static String constructRequestBody(String context, String sourceLang, String targetLang, String termbaseId) {
        return String.format("{\"context\":\"%s\",\"sourceLang\":\"%s\",\"targetLang\":\"%s\",\"termbaseId\":\"%s\"}",
                context, sourceLang, targetLang, termbaseId);
    }
}
