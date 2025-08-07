package com.wenge.model.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @version 1.0.0
 * @description 必看！！！客户调用接口工具类，maven需要引入httpclient 版本为4.5.13，fastjson版本必须为1.2.83版本
 * @ClassName CustomerSignUtil
 * @date 2022/12/12 11:32
 */
@Slf4j
public class CustomerSignUtil {

    private static final String URL = "https://wenhai.wengegroup.com/openapi/v1/search/getSearchList";

    public static void main(String[] args) {
        // 调用示例
        String appKey = "xxxxx";
        String secretKey = "xxxxxxxx";
        String nonce = RandomUtil.randomNumbers(5);
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 注意这个是接口调用参数，此值一定以接口入参为主，否则签名校验通不过
        String str = "{\"articleInfo\":{\"searchTime\":{\"realTime\":7,\"now\":\"2025-01-07 14:28:00\",\"startPubTime\":\"2024-10-08 14:28:00\",\"endPubTime\":\"2025-01-07 14:28:00\"},\"dataSources\":[\"10302\"]},\"articleUser\":{\"userIds\":[\"1706957692282249216\"]},\"pageInfo\":{\"currentPage\":1,\"pageSize\":1},\"sortInfo\":{\"sortField\":1,\"sortWay\":\"desc\"}}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        String signature = sign(appKey, secretKey, nonce, timestamp, jsonObject);
        JSONObject headerParams = new JSONObject();
        headerParams.put("appKey", appKey);
        headerParams.put("nonce", nonce);
        headerParams.put("timestamp", timestamp);
        headerParams.put("signature", signature);
        System.out.println(requestForHttp(URL, str, headerParams));
    }

    /**
     * 生成签名
     *
     * @param appKey     appKey
     * @param secretKey  对应秘钥
     * @param nonce      随机数
     * @param timestamp  当前时间戳
     * @param jsonObject 入参
     * @return signature
     */
    public static String sign(String appKey, String secretKey, String nonce, String timestamp, JSONObject jsonObject) {
        MapParam build = MapParam
                .builder()
                .param(jsonObject)
                .appKey(appKey)
                .secretKey(secretKey)
                .nonce(nonce)
                .timestamp(timestamp)
                .build();
        return CustomerSignUtil.sign(build);
    }


    /**
     * 生成签名
     */
    public static String sign(MapParam mapParam) {
        JSONObject param = mapParam.getParam();
        Map<String, String> parameterMap = getParameterMap(param);
        String timestamp = mapParam.getTimestamp();
        String nonce = mapParam.getNonce();
        String appKey = mapParam.getAppKey();
        String secretKey = mapParam.getSecretKey();
        parameterMap.put("nonce", nonce);
        parameterMap.put("appKey", appKey);
        parameterMap.put("secretKey", secretKey);
        parameterMap.put("timestamp", timestamp);
        Map<String, String> sPara = paraFilter(parameterMap);
        String linkString = createLinkString(sPara);
        return DigestUtils.md5DigestAsHex(getContentBytes(linkString, "UTF-8"));
    }

    private static String createLinkString(Map<String, String> params) {
        return createLinkString(params, false);
    }

    private static String createLinkString(Map<String, String> params, boolean encode) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (encode) {
                value = urlEncode(value, "UTF-8");
            }
            stringBuilder.append(key).append("=").append(value);
            if (i != keys.size() - 1) {
                stringBuilder.append("&");
            }
        }
        return stringBuilder.toString();
    }

    private static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<>(sArray.size());
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (isBlank(value) || "signature".equalsIgnoreCase(key)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    private static Map<String, String> getParameterMap(JSONObject jsonObject) {
        Map<String, String> returnMap = new TreeMap<>();
        if (jsonObject == null) {
            return returnMap;
        }
        Iterator entries = jsonObject.entrySet().iterator();
        return getStringMap(returnMap, entries);
    }

    private static String urlEncode(String content, String charset) {
        try {
            return URLEncoder.encode(content, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    private static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
        if (strLen == 0) {
            return true;
        } else {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    private static Map<String, String> getStringMap(Map<String, String> returnMap, Iterator entries) {
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (String value1 : values) {
                    value = value1 + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else if (valueObj instanceof JSONObject) {
                JSONObject values = (JSONObject) valueObj;
                if (null != values) {
                    value = startSort(values);
                }
            } else if (valueObj instanceof JSONArray) {
                JSONArray valueObj1 = (JSONArray) valueObj;
                if (null != valueObj1) {
                    StringJoiner joiner = new StringJoiner(",", "[", "]");
                    for (Object obj : valueObj1) {
                        if (obj instanceof JSONObject) {
                            JSONObject values = (JSONObject) obj;
                            if (null != values) {
                                String value1 = startSort(values);
                                joiner.add(value1);
                            }
                        }
                    }
                    value = joiner.toString();
                }
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * 对JSONObject的key根据首字母排序 若首字母相同对比下一个字母 依次类推
     * 备注：当前未覆盖JSONArray的场景
     *
     * @param json
     * @return 排序后的新json
     */
    public static String startSort(JSONObject json) {
        // 第1层
        JSONObject jsonAlone = getNewAloneKeys(json);
        // 第2-n层
        get(jsonAlone);
        return JSON.toJSONString(jsonAlone);
    }

    private static final String $1 = "1";
    private static final String $2 = "2";
    private static final String $3 = "3";
    private static final String CONNECT = "_";

    /**
     * 对单层json排序
     *
     * @param json
     */
    private static JSONObject getAloneKeys(JSONObject json) {
        ArrayList<String> aloneKeys = new ArrayList<>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            StringJoiner stringJoiner = new StringJoiner(CONNECT);
            String key = entry.getKey();
            stringJoiner.add(key);
            String keySuffix;
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                keySuffix = $1;
            } else if (value instanceof JSONArray) {
                keySuffix = $2;
            } else {
                keySuffix = $3;
            }
            stringJoiner.add(keySuffix);
            aloneKeys.add(stringJoiner.toString());
        }
        // 排序
        Collections.sort(aloneKeys);
        // 整理排序后的json
        JSONObject newJson = new JSONObject(new LinkedHashMap<>());
        for (String key : aloneKeys) {
            String[] s = key.split(CONNECT);
            if ($1.equals(s[1])) {
                newJson.put(s[0], json.getJSONObject(s[0]));
            } else if ($2.equals(s[1])) {
                newJson.put(s[0], json.getJSONArray(s[0]));
            } else {
                newJson.put(s[0], json.get(s[0]));
            }
        }
        return newJson;
    }

    /**
     * 新-对单层json排序
     *
     * @param json
     */
    private static JSONObject getNewAloneKeys(JSONObject json) {
        List<String> collect = json.keySet().stream().collect(Collectors.toList());
        //json的keySet排序后的值的集合
        Collections.sort(collect);
        ArrayList<String> aloneKeys = new ArrayList<>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            StringJoiner stringJoiner = new StringJoiner(CONNECT);
            String key = entry.getKey();
            stringJoiner.add(key);
            String keySuffix;
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                keySuffix = $1;
            } else if (value instanceof JSONArray) {
                keySuffix = $2;
            } else {
                keySuffix = $3;
            }
            stringJoiner.add(keySuffix);
            aloneKeys.add(stringJoiner.toString());
        }
        // 整理排序后的json
        JSONObject newJson = new JSONObject(new LinkedHashMap<>());
        for (String key : collect) {
            String flag = "";
            for (String aloneKey : aloneKeys) {
                String substring = aloneKey.substring(0, aloneKey.lastIndexOf(CONNECT));
                if (substring.equals(key)) {
                    flag = aloneKey.substring(aloneKey.lastIndexOf(CONNECT));
                }
            }
            //找到对应的_后缀
            if ($1.equals(flag)) {
                newJson.put(key, json.getJSONObject(key));
            } else if ($2.equals(flag)) {
                newJson.put(key, json.getJSONArray(key));
            } else {
                newJson.put(key, json.get(key));
            }
        }
        return newJson;
    }

    private static JSONObject getAloneKeysRec(JSONObject json) {
        JSONObject newJson = getNewAloneKeys(json);
        get(newJson);
        return newJson;
    }

    private static void get(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (null == entry.getValue()) {
                continue;
            }
            if (entry.getValue().getClass().equals(JSONObject.class)) {
                jsonObject.put(entry.getKey(), getAloneKeysRec((JSONObject) entry.getValue()));
            }
            if (entry.getValue().getClass().equals(JSONArray.class)) {
                String key = entry.getKey();
                JSONArray value = (JSONArray) entry.getValue();
                JSONArray jsonArray = new JSONArray();
                for (Object obj : value) {
                    if (obj.getClass().equals(JSONObject.class)) {
                        JSONObject jo = getAloneKeysRec((JSONObject) obj);
                        jsonArray.add(jo);
                    } else {
                        jsonArray.add(obj);
                    }
                }
                jsonObject.put(key, jsonArray);
            }
        }
    }

    /**
     * 签名封装对象
     *
     * @author tyz
     */
    @Data
    @Builder
    protected static class MapParam {
        /**
         * appKey
         */
        private String appKey;

        /**
         * secret
         */
        private String secretKey;

        /**
         * 随机数
         */
        private String nonce;

        /**
         * 时间戳
         */
        private String timestamp;

        /**
         * 接口请求参数
         */
        private JSONObject param;
    }

    /**
     * post调用url，json格式传输数据
     *
     * @param url     url
     * @param param   value
     * @param headers 请求参数
     * @return 响应结果
     */
    public static JSONObject requestForHttp(String url, String param, JSONObject headers) {
        // 创建HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // httpPost请求头以及请求体
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((k, v) -> httpPost.addHeader(k, v.toString()));
        }
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8));
        CloseableHttpResponse httpResponse = null;
        try {
            // 发送请求
            httpResponse = httpClient.execute(httpPost);
            // 解析请求，返回json格式的数据
            httpResponse.getHeaders("Content-Type");
            HttpEntity httpEntity = httpResponse.getEntity();
            String result = EntityUtils.toString(httpEntity, "UTF-8");
            EntityUtils.consume(httpEntity);
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            log.error("post请求调用接口失败：{},异常信息：{}", url, e.getMessage());
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
            }
        }
        return new JSONObject();
    }

}



