package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.config.llm.*;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.enums.LLMType;
import com.wg.appframe.yayi.result.DoubaoCompletionResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;

import static cn.hutool.core.date.DatePattern.HTTP_DATETIME_FORMAT;

/**
 * 通用策略
 */
@Slf4j
public class GeneralStrategy implements Serializable {
    private static final long serialVersionUID = -6220984948076057292L;

    public DoubaoCompletionResult run(String content, GeneralConfig param, Consumer<DoubaoCompletionResult> consumer) {
        DoubaoCompletionResult result = new DoubaoCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            param = getParam(content, param);
            // 鉴权
            authorization(param, builder);

            // 请求 doubaoCompletions接口
            log.info("===>GeneralStrategy url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                final String contentType = "application/json; charset=utf-8";

                final MediaType mediaType = MediaType.parse(contentType);
                final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param));
                Request request = builder
                        .url(param.getUri())
                        .post(body)
                        .build();
                OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);

                // 流式输出
                if (param.getStream()) {
                    if (null == consumer) {
                        return new DoubaoCompletionResult();
                    }

                    try (final Response response = okHttpClient.newCall(request).execute();
                         final InputStream it = response.body().byteStream()) {
                        final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
                        responseByStream(bufferedReader, consumer);
                    }
                } else {
                    try (Response response = okHttpClient.newCall(request).execute();
                         ResponseBody responseBody = response.body()) {
                        if (null != responseBody) {
                            String responses = responseBody.string();
                            log.info("===>generalCompletions body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                return entries.toBean(DoubaoCompletionResult.class);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用大模型算法失败");
            }
            return new DoubaoCompletionResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void authorization(GeneralConfig param, Request.Builder builder) {
        // appKey
        builder.addHeader("Authorization", "Bearer " + param.getAppKey());
        // appSecret

        if (LLMType.MENGZI.equals(param.getModelName())) {
            // 孟子大模型的鉴权逻辑
            mengziAuthorization(param, builder);
        }
//        else if (LLMType.HUNYUAN.equals(param.getModelName())) {
//            // 混元大模型的鉴权逻辑
//            hunyuanAuthorization(param, builder);
//        }
    }
    private final static String CHARSET = "UTF-8";

//    public static String sign(String s, String key, String method) throws Exception {
//        Mac mac = Mac.getInstance(method);
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), mac.getAlgorithm());
//        mac.init(secretKeySpec);
//        byte[] hash = mac.doFinal(s.getBytes(CHARSET));
//        return DatatypeConverter.printBase64Binary(hash);
//    }
//
//    public static String getStringToSign(TreeMap<String, Object> params) {
//        StringBuilder s2s = new StringBuilder("GETcvm.tencentcloudapi.com/?");
//        // 签名时要求对参数进行字典排序，此处用TreeMap保证顺序
//        for (String k : params.keySet()) {
//            s2s.append(k).append("=").append(params.get(k).toString()).append("&");
//        }
//        return s2s.toString().substring(0, s2s.length() - 1);
//    }
//
//    public static String getUrl(TreeMap<String, Object> params) throws UnsupportedEncodingException {
//        StringBuilder url = new StringBuilder("https://cvm.tencentcloudapi.com/?");
//        // 实际请求的url中对参数顺序没有要求
//        for (String k : params.keySet()) {
//            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
//            url.append(k).append("=").append(URLEncoder.encode(params.get(k).toString(), CHARSET)).append("&");
//        }
//        return url.toString().substring(0, url.length() - 1);
//    }
//
//    private void hunyuanAuthorization(GeneralConfig param, Request.Builder builder) {
//        TreeMap<String, Object> params = new TreeMap<>(); // TreeMap可以自动排序
//        // 实际调用时应当使用随机数，例如：params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));
//        builder.addHeader("Nonce", String.valueOf(new Random().nextInt(Integer.MAX_VALUE))); // 公共参数
//        // 实际调用时应当使用系统当前时间，例如：   params.put("Timestamp", System.currentTimeMillis() / 1000);
//        builder.addHeader("Timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 公共参数
//        // 需要设置环境变量 TENCENTCLOUD_SECRET_ID，值为示例的 AKID********************************
//        builder.addHeader("SecretId", param.getAppSecret()); // 公共参数
//        builder.addHeader("Action", "DescribeInstances"); // 公共参数
//        builder.addHeader("Version", "2017-03-12"); // 公共参数
//        builder.addHeader("Region", "ap-guangzhou"); // 公共参数
//        builder.addHeader("Limit", String.valueOf(20)); // 业务参数
//        builder.addHeader("Offset", String.valueOf(0)); // 业务参数
//        builder.addHeader("InstanceIds.0", "ins-09dx96dg"); // 业务参数
//        // 需要设置环境变量 TENCENTCLOUD_SECRET_KEY，值为示例的 ********************************
//        builder.addHeader("Signature", sign(getStringToSign(params), System.getenv("TENCENTCLOUD_SECRET_KEY"), "HmacSHA1")); // 公共参数
//        System.out.println(getUrl(params));
//    }

    private static void mengziAuthorization(GeneralConfig param, Request.Builder builder) {
        String contentType = "application/json";
        builder.addHeader("Content-Type", contentType);
        String date = DateUtil.format(new Date(), HTTP_DATETIME_FORMAT);
        builder.addHeader("Date", date);
        String accept = "application/json";
        builder.addHeader("Accept", accept);
        String method = "HMAC-SHA256";
        builder.addHeader("x-langboat-signature-method", method);
        String nonce = UUID.randomUUID().toString();
        builder.addHeader("x-langboat-signature-nonce", nonce);

        // 1. 计算 body 的 MD5 值，然后转换为 Base64 编码，编码后的值设置到 Content-MD5 Header
        String base64;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(JSONUtil.toJsonStr(param).getBytes());
            base64 = Base64.getEncoder().encodeToString(md5Bytes);
            builder.addHeader("Content-MD5", base64);
            // 2. 使用请求中的 Header 参数构造待签名的 StringToSign：
            String stringToSign =
                    "POST" + "\n" +      //HTTP-Verb只支持POST
                            accept + "\n" +         //Accept为application/json
                            base64 + "\n" +    //第1步中计算出来的MD5值
                            contentType + "\n" +   //Content-Type值为application/json
                            date + "\n" +           //Date值为GMT时间
                            method + "\n" +  // 只支持 HMAC-SHA256
                            nonce + "\n";

            // 3. 计算签名 signature。按照 RFC2104 的定义，计算待签名字符串 stringToSign 的 HMAC 值，按照 Base64 编码规则把 HMAC 值编码成字符串，并在前面加上 AccessKey ，即得到签名值（Authorization）
            String name = "HmacSHA256";
            Mac hmac = Mac.getInstance(name);
            hmac.init(new SecretKeySpec(param.getAppSecret().getBytes(), hmac.getAlgorithm()));
            String authorization = param.getAppKey() + ":" + Base64.getEncoder().encodeToString(hmac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
            builder.addHeader("Authorization", authorization);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static void responseByStream(BufferedReader bufferedReader, Consumer<DoubaoCompletionResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        DoubaoCompletionResult bean = entries.toBean((Class<? extends DoubaoCompletionResult>) DoubaoCompletionResult.class);
                        consumer.accept(bean);
                    }
                }
            }
        }
    }

    private GeneralConfig getParam(String content, GeneralConfig param) {
        String manufacturer = param.getManufacturer();
        GeneralConfig paramTemp = conversion(manufacturer, param);

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        paramTemp.setRetryTimes(1);
        paramTemp.setRetryInterval(3000);

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            paramTemp.setMessages(messagesLists);
        }

        // 选择模型
//        setModel(paramTemp, content);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

    private static GeneralConfig conversion(String manufacturer, GeneralConfig param) {
        switch (manufacturer) {
            case LLMType.TONGYI:
                return new TongYiCompletionsConfig(param);
            case LLMType.PANGU:
                // todo 未找到申请链接，暂时无法测试
                // 替换uri的占位符
                String uri = param.getUri();
                Map<String, String> path = param.getPath();
                for (Map.Entry<String, String> entry : path.entrySet()) {
                    uri = uri.replace("{" + entry.getKey() + "}", entry.getValue());
                }
                param.setUri(uri);
                return new PanGuCompletionsConfig(param);
            case LLMType.SHUSHENG:
                return new ShuShengCompletionsConfig(param);
            case LLMType.XINGHUO:
                return new XingHuoCompletionsConfig(param);
            case LLMType.MENGZI:
                return new MengZiCompletionsConfig(param);
            case LLMType.HUNYUAN:
                // todo 鉴权逻辑复杂，暂未调通
                return new HunYuanCompletionsConfig(param);
            default:
                throw new RuntimeException("暂不支持的大模型类型");
        }
    }

}
