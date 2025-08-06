package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.enums.WhRespCodeEnum;
import com.wenge.model.service.KnowledgeDataService;
import com.wenge.model.service.WenhaiService;
import com.wenge.model.utils.SHA256Util;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.elasticsearch.service.EsRestHighLevelClientService;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WenhaiServiceImpl implements WenhaiService {

    @Autowired
    private EsRestHighLevelClientService esRestHighLevelClientService;

    private String appKey="6nTaDqFB";
    private String appSecret="fab60e39db6a61bcf43ea40e37f55a5bc1b05602";
    // private String sid="486726";
    private String getNewsHotRankBySubIdAndKws="/wenhai-api/subscribe/getByGidAndKws";

    private Integer days=30;
    private String host="http://wenhai.wengegroup.com";


    @Autowired
    private KnowledgeDataService knowledgeDataService;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisConstant.RUN_WH_DATA, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_WH_DATA);
            redisService.unlock(RedisConstant.LOCK_WH_DATA);
        }
    }


    @Override
    public void sentiment() {
        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_WH_DATA);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_WH_DATA);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_WH_DATA, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            ArrayList<String> list = ListUtil.toList("487155");
            for (String sid : list) {
                log.info("===>拉取热点定时器开始");
                String gid = "";
                String knowledgeid = "c30253a774a44b7394cf1866d4a30e90";
                while (true) {
                    JSONObject baseParam = getBaseParam("", "");
                    baseParam.put("sid", sid);
                    baseParam.put("gid", gid);
                    JSONObject jsonObject = JSON.parseObject(exeRequestForHttp(host + getNewsHotRankBySubIdAndKws, baseParam));
                    long timestamp = System.currentTimeMillis();
                    if (0 == jsonObject.getInteger("code") && jsonObject.containsKey("data")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data == null) {
                            break;
                        }

                        JSONArray articles = data.getJSONArray("articles");
                        if (CollectionUtil.isEmpty(articles)) {
                            break;
                        }

                        List<JSONObject> javaList = articles.toJavaList(JSONObject.class);
                        gid = javaList.get(javaList.size() - 1).getString("gid");

                        if (CollectionUtil.isNotEmpty(javaList)) {
                            for (JSONObject object : javaList) {
                                KnowledgeData knowledgeData = new KnowledgeData();
                                knowledgeData.setDataSource(1 + "");
                                knowledgeData.setContent(object.getString("content"));
                                knowledgeData.setAccurate("否");
                                knowledgeData.setKnowledgeId(knowledgeid);
                                knowledgeData.setTitle(object.getString("title"));
                                knowledgeDataService.addKnowledgeData(knowledgeData);
                            }
                        }
                    } else {
                        break;
                    }
                }
                log.info("===>拉取热点定时器结束");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_WH_DATA);
            redisService.unlock(RedisConstant.LOCK_WH_DATA);
        }

    }


    private void pdfText(String id, BufferedImage bufferedImage) {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter out = null;
        try {
            String fileName = "D:\\work\\pdf\\pdf_" + id + ".txt";
            String imgFileName = "D:\\work\\pdf\\pdf_" + id + ".png";

            File file1 = new File(fileName);
            if (!file1.exists()) {
                file1.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file1);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            out = new BufferedWriter(outputStreamWriter);

            HttpRequest post = HttpUtil.createPost("http://172.16.10.61:31523/analysis");

            File imageFile = saveBufferedImageToFile(bufferedImage, imgFileName);
            post.form("id", id);
            post.form("image", imageFile);
            HttpResponse execute = post.setConnectionTimeout(1000 * 20).setReadTimeout(1000 * 20).execute();
            String body = execute.body();
            JSONObject jsonObject = JSON.parseObject(body);
            if (200 == jsonObject.getInteger("code")) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (null != data) {
                    StringBuffer buffer = new StringBuffer();
                    JSONArray labels = data.getJSONArray("labels");
                    if (CollectionUtil.isNotEmpty(labels)) {
                        for (Object o : labels) {
                            JSONObject mergeDetail = (JSONObject) o;
                            String text = mergeDetail.getString("text");
                            buffer.append("\n");
                            buffer.append(text);
                        }
                    }
                    buffer.append("\n");
                    out.write(buffer.toString());
                    out.flush();
                }
            }
//            System.out.println("-----" + body);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
                if (null != outputStreamWriter) {
                    outputStreamWriter.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
                log.error("error:{}", e.getMessage(), e);
            };
        }
    }

    public static File saveBufferedImageToFile(BufferedImage image, String filePath) throws IOException {
        File imageFile = new File(filePath);

        // 将BufferedImage保存为图像文件
        ImageIO.write(image, "png", imageFile);

        return imageFile;
    }


    private JSONObject getBaseParam(String kws, String exclude) {
        JSONObject params = new JSONObject();
        if (StringUtils.isNotBlank(kws)) {
            params.put("kws_include", kws);
        }
        // params.put("kws_position", 4);
        if (StringUtils.isNotBlank(exclude)) {
            params.put("kws_exclude", exclude);
        }
        // params.put("sid", sid);

        params.put("is_duplicate", 1);
        params.put("page_size", 50);
        LocalDateTime now = LocalDateTimeUtil.now();
        LocalDateTime startTime = now.minusDays(days);
        params.put("start_time", LocalDateTimeUtil.format(startTime, "yyyy-MM-dd HH:mm:ss"));
        params.put("end_time", LocalDateTimeUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
        return params;
    }

    private String exeRequestForHttp(String url, JSONObject param) {
        Map jsonObject = null;
        String json = null;
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//        httpRequestFactory中可设置三个超时：RequestTimeout（连接池获取到连接的超时时间）、ConnectTimeout（建立连接的超时）、SocketTimeout（获取数据的超时时间）
        httpRequestFactory.setConnectionRequestTimeout(5 * 60 * 1000);
        httpRequestFactory.setConnectTimeout(5 * 60 * 1000);
        httpRequestFactory.setReadTimeout(5 * 60 * 1000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        HttpHeaders requestHeaders = new HttpHeaders();
        boolean changeCountFlag = false;
        boolean isSuccess = true;

        /** 解析请求，json格式的返回数据*/
        try {
            requestHeaders.clear();
            setRequestHeader(requestHeaders);
            log.info("IndustrySentimentSchedule.exeRequestForHttp,url:{},param:{}", url, param);
            HttpEntity<String> requestEntity = new HttpEntity<>(param.toJSONString(), requestHeaders);
            /** 发送请求*/
            json = restTemplate.postForObject(url, requestEntity, String.class);
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
            filter.getIncludes().add("code");
            filter.getIncludes().add("message");
            jsonObject = JSONObject.parseObject(json, Map.class);
            log.info("IndustrySentimentSchedule.exeRequestForHttp,json:{}", JSON.toJSONString(jsonObject, filter));
            WhRespCodeEnum whRespCodeEnum = WhRespCodeEnum.getWhRespEnumByCode(String.valueOf(jsonObject.get("code")));
            if (whRespCodeEnum.getCode().equals(WhRespCodeEnum.SUCCESS.getCode())) {
                isSuccess = true;
            } else {
                isSuccess = false;
//                    appAryIndex++;
                //调用成功，但接口返回 失败信息  解析失败信息，并判定是否切换用户重新调用
            }
        } catch (Exception e) {
            log.error("IndustrySentimentSchedule.exeRequestForHttp.error:{}", e.getMessage(), e);
        }
        if (changeCountFlag) {
            throw new RuntimeException("用户切换次数已到最大次数");
        }
        if (isSuccess) {
            return json;
        } else {
            throw new RuntimeException("查询失败：" + json);
        }
    }


    //根据appAryIndex 设置请求头
    public synchronized void setRequestHeader(HttpHeaders requestHeaders) {
        String currtime = "";
        currtime = String.valueOf(System.currentTimeMillis());
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        requestHeaders.setContentType(type);
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON.toString());
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("appKey", appKey);
        requestHeaders.add("sign", SHA256Util.getDigest(appKey + currtime + appSecret));
        requestHeaders.add("timeStamp", currtime);
    }


}
