package com.wenge.model.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Builder;
import lombok.Data;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @version 1.0.0
 * @description 客户调用接口工具类，因本工具类生成涉及到JSON。请注意JSON版本！！！最好为1.2.83版本，fastjson2无法使用。
 * @ClassName CsSignUtil
 * @date 2022/12/12 11:32
 **/
public class CsSignUtil {

    public static void main(String[] args) {
        // 您申请的应用Key
        String appKey = "GO448o0I";
        // 您申请的应用密钥
        String secretKey = "428eb3ceabf759e77ff5bb003da72126ddacf681";
        //随机数
        String nonce = RandomUtil.randomNumbers(10);
        String timestamp = String.valueOf(System.currentTimeMillis());
        //注意这个是接口调用参数，此值一定以接口入参为主，否则签名校验通不过

        String str = " {\n" +
                "            \"word\": \"美国\",\n" +
                "            \"engine\": \"baidu\",\n" +
                "            \"dataType\":\"snapshot\",\n" +
                "            \"isRender\": 1,\n" +
                "            \"isForeignMedia\": 1\n" +
                "        }";
        JSONObject param = JSONUtil.parseObj(str);
        param.set("", "");
        String signature = CsSignUtil.sign(appKey, secretKey, nonce, timestamp, param);
        HashMap<String, String> headerParams = new HashMap<>(4);
        headerParams.put("appKey", appKey);
        headerParams.put("nonce", nonce);
        headerParams.put("timestamp", timestamp);
        headerParams.put("signature", signature);
        JSONObject result = doPost(headerParams, param, "https://api-sc.wengegroup.com/openapi/v1/interactionVolume/yayiMetaSearch");
        System.out.println("sign--------:" + sign(appKey, secretKey, nonce, timestamp, param));
        // System.out.println(result.toJSONString());
    }

    /**
     * 构建header
     *
     * @param appKey
     * @param nonce
     * @param timestamp
     * @return
     */
    public static JSONObject buildHeader(String appKey, String secretKey, JSONObject param) {
        String nonce = RandomUtil.randomNumbers(10);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = CsSignUtil.sign(appKey, secretKey, nonce, timestamp, param);
        JSONObject headers = new JSONObject();
        headers.set("appKey", appKey);
        headers.set("nonce", nonce);
        headers.set("timestamp", timestamp);
        headers.set("signature", signature);
        return headers;
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
        return CsSignUtil.sign(build);
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
                        } else if (obj instanceof String) {
                            String str = (String) obj;
                            joiner.add("\"" + str + "\"");
                        } else if (obj instanceof Integer) {
                            Integer integer = (Integer) obj;
                            joiner.add(String.valueOf(integer));
                        } else if (obj instanceof Long) {
                            Long aLong = (Long) obj;
                            joiner.add(Long.toString(aLong));
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
        return JSONUtil.toJsonStr(jsonAlone);
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


    //仅供测试接口输出，与签名无关
    public static JSONObject doPost(Map<String, String> headerParams, JSONObject json, String url) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                //设置连接超时时间
                .connectTimeout(30, TimeUnit.SECONDS)
                //写入超时时间
                .writeTimeout(30, TimeUnit.SECONDS)
                //从连接成功到响应的总时间
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType contentType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(contentType, String.valueOf(json));
        Headers headers = setHeaderParams(headerParams);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String res = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = JSONUtil.parseObj(res);
            return jsonObject;
        } catch (Exception e) {
            System.out.println("doPost 方法异常！" + e);
            return null;
        }
    }


    //仅供测试接口输出，与签名无关
    private static Headers setHeaderParams(Map<String, String> headerParams) {
        Headers headers;
        Headers.Builder headersBuilder = new Headers.Builder();
        if (CollectionUtil.isNotEmpty(headerParams)) {
            for (String key : headerParams.keySet()) {
                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(headerParams.get(key))) {
                    // 如果参数不是null并且不是""，就拼接起来
                    headersBuilder.add(key, headerParams.get(key));
                }
            }
        }
        headers = headersBuilder.build();
        return headers;
    }


}