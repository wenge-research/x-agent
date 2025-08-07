package com.wenge.oauth.util;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.oauth.dto.param.SmsTencentParam;
import com.wenge.oauth.dto.result.SmsTencentResult;
import com.wenge.oauth.entity.SmsTencentRecord;
import com.wenge.oauth.service.SmsTencentRecordService;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * 腾讯云短信
 */
@Slf4j
public class SmsTencentUtil {

    /**
     * 发送短信
     *
     * @param param
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static SmsTencentResult sendMsg(SmsTencentParam param) {
        SmsTencentResult.Error error = null;
        try {
            if (CollectionUtil.isEmpty(param.getPhoneNumberSet())) {
                return null;
            }
            initConfig(param);
            param.setService("sms");
            param.setAction("SendSms");
            String body = JSON.toJSONString(param);
            JSONObject jsonObject = JSON.parseObject(body);
            JSONObject params = new JSONObject();
            jsonObject.forEach((k, v) -> {
                // 将 k首字母大写
                params.put(k.substring(0, 1).toUpperCase() + k.substring(1), v);
            });
            params.remove("Version");
            params.remove("Region");
            params.remove("Action");
            params.remove("SecretId");
            params.remove("SecretKey");
            params.remove("Service");
            params.remove("Token");
            String resp = doRequest(params.toJSONString(), param);
            if (StringUtils.isNotBlank(resp)) {
                SmsTencentResult smsTencentResult = JSON.parseObject(resp, SmsTencentResult.class);
                if (null != smsTencentResult) {
                    SmsTencentResult.Response response = smsTencentResult.getResponse();
                    if (null != response) {
                        error = response.getError();
                        if (null == error) {
                            List<SmsTencentResult.SendStatus> sendStatusSet = response.getSendStatusSet();
                            if (CollectionUtil.isNotEmpty(sendStatusSet)) {
                                Map<String, SmsTencentResult.SendStatus> smsStatus = sendStatusSet.stream().collect(Collectors.toMap(
                                        SmsTencentResult.SendStatus::getPhoneNumber,
                                        p -> p,
                                        (k1, k2) -> k1
                                ));

                                // 记录短信
                                ApplicationContext context = CoreContextProvider.getContext();
                                SmsTencentRecordService smsTencentRecordService = context.getBean(SmsTencentRecordService.class);
                                List<SmsTencentRecord> tencentRecords = param.getPhoneNumberSet()
                                        .stream().map(phoneNumber -> {
                                            SmsTencentResult.SendStatus sendStatus = smsStatus.getOrDefault("+86" + phoneNumber, new SmsTencentResult.SendStatus());
                                            SmsTencentRecord record = new SmsTencentRecord();
                                            record.setSendTime(DateUtil.getCurrentDateString());
                                            record.setStatus("Ok".equals(sendStatus.getCode()) ? "成功" : "失败:" + sendStatus.getCode());
                                            record.setPhoneNumber(phoneNumber);
                                            record.setTemplateId(param.getTemplateId());
                                            record.setTemplateParam(JSONUtil.toJsonStr(param.getTemplateParamSet()));
                                            record.setSessionContext(sendStatus.getSessionContext());
                                            record.setCallbackContent(JSONUtil.toJsonStr(sendStatus));
                                            return record;
                                        }).collect(Collectors.toList());
                                smsTencentRecordService.addSmsTencentRecord(tencentRecords);
                                return smsTencentResult;
                            }
                        }
                    }
                }
            }
            sendFail(param, error);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送短信失败", e);
            sendFail(param, error);
        }
        return null;
    }


    /**
     * 初始化参数
     * @param param
     */
    private static void initConfig(SmsTencentParam param) {
        ApplicationContext context = CoreContextProvider.getContext();
        if (StringUtils.isBlank(param.getSecretId())) {
            Environment environment = context.getEnvironment();
            String secretId = environment.getProperty("sms.tencent.secretId");
            param.setSecretId(secretId);
        }
        if (StringUtils.isBlank(param.getSecretKey())) {
            Environment environment = context.getEnvironment();
            String secretKey = environment.getProperty("sms.tencent.secretKey");
            param.setSecretKey(secretKey);
        }
        if (StringUtils.isBlank(param.getVersion())) {
            Environment environment = context.getEnvironment();
            String version = environment.getProperty("sms.tencent.version");
            param.setVersion(version);
        }
        if (StringUtils.isBlank(param.getRegion())) {
            Environment environment = context.getEnvironment();
            String region = environment.getProperty("sms.tencent.region");
            param.setRegion(region);
        }
        if (StringUtils.isBlank(param.getSessionContext())) {
            param.setSessionContext("wg_" + IdUtil.simpleUUID());
        }
        param.setToken(StringConstant.BLANK);
    }

    private static void sendFail(SmsTencentParam param, SmsTencentResult.Error error) {
        if (CollectionUtil.isEmpty(param.getPhoneNumberSet())) {
            return;
        }
        // 记录短信
        ApplicationContext context = CoreContextProvider.getContext();
        SmsTencentRecordService smsTencentRecordService = context.getBean(SmsTencentRecordService.class);
        List<SmsTencentRecord> tencentRecords = param.getPhoneNumberSet()
                .stream().map(phoneNumber -> {
                    SmsTencentResult.SendStatus sendStatus = new SmsTencentResult.SendStatus();
                    SmsTencentRecord record = new SmsTencentRecord();
                    record.setSendTime(DateUtil.getCurrentDateString());
                    record.setStatus("失败:" + (null != error ? error.getMessage() : StringConstant.BLANK));
                    record.setPhoneNumber(phoneNumber);
                    record.setTemplateId(param.getTemplateId());
                    record.setTemplateParam(JSONUtil.toJsonStr(param.getTemplateParamSet()));
                    record.setSessionContext(param.getSessionContext());
                    record.setCallbackContent(JSONUtil.toJsonStr(sendStatus));
                    return record;
                }).collect(Collectors.toList());
        smsTencentRecordService.addSmsTencentRecord(tencentRecords);
    }

    // singleton client for connection reuse and better performance
    private static final OkHttpClient client = new OkHttpClient();

    public static String doRequest(String body, SmsTencentParam param) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        Request request = buildRequest(body, param);

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static Request buildRequest(String body, SmsTencentParam param
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String host = "sms.tencentcloudapi.com";
        String endpoint = "https://" + host;
        String contentType = "application/json; charset=utf-8";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String auth = getAuth(param.getSecretId(), param.getSecretKey(), host, contentType, timestamp, body);
        return new Request.Builder()
                .header("Host", host)
                .header("X-TC-Timestamp", timestamp)
                .header("X-TC-Version", param.getVersion())
                .header("X-TC-Action", param.getAction())
                .header("X-TC-Region", param.getRegion())
                .header("X-TC-Token", param.getToken())
                .header("X-TC-RequestClient", "SDK_JAVA_BAREBONE")
                .header("Authorization", auth)
                .url(endpoint)
                .post(RequestBody.create(MediaType.parse(contentType), body))
                .build();
    }

    private static String getAuth(
            String secretId, String secretKey, String host, String contentType,
            String timestamp, String body
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:" + contentType + "\nhost:" + host + "\n";
        String signedHeaders = "content-type;host";

        String hashedRequestPayload = sha256Hex(body.getBytes(StandardCharsets.UTF_8));
        String canonicalRequest = "POST"
                + "\n"
                + canonicalUri
                + "\n"
                + canonicalQueryString
                + "\n"
                + canonicalHeaders
                + "\n"
                + signedHeaders
                + "\n"
                + hashedRequestPayload;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(Long.valueOf(timestamp + "000")));
        String service = host.split("\\.")[0];
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest =
                sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        String stringToSign =
                "TC3-HMAC-SHA256\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature =
                printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();
        return "TC3-HMAC-SHA256 "
                + "Credential="
                + secretId
                + "/"
                + credentialScope
                + ", "
                + "SignedHeaders="
                + signedHeaders
                + ", "
                + "Signature="
                + signature;
    }

    public static String sha256Hex(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(b);
        return printHexBinary(d).toLowerCase();
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static byte[] hmac256(byte[] key, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }
}
