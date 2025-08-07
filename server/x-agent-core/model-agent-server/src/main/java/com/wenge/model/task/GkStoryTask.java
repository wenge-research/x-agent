package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.GkStoryData;
import com.wenge.model.mapper.es.GkStoryMapper;
import com.wenge.model.utils.GkDesUtil;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 获取稿件【国科大应用】
 */
@RestController
@RequestMapping("/agentTask/guangxin")
@Slf4j
public class GkStoryTask {

    @Value("${task.GkStory.enable}")
    private Boolean enable;

    @Value("${task.GkStory.columnApi}")
    private String columnApi;
    @Value("${task.GkStory.channelId}")
    private String channelId;
    @Value("${task.GkStory.appKey}")
    private String appKey;
    @Value("${task.GkStory.appSecret}")
    private String appSecret;
    @Value("${task.GkStory.storyApi}")
    private String storyApi;

    @Autowired
    private GkStoryMapper gkStoryMapper;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {

        String value = redisService.get(RedisKey.STORY, String.class);
        String localhostStr = NetUtil.getLocalhostStr();

        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.STORY);
            redisService.unlock(RedisConstant.LOCK_STORY);
        }
    }

    /**
     * 获取稿件
     */
    @GetMapping("/story")
    // @Scheduled(cron = "${task.GkStory.cron}")
    public void story() {
        if (!enable) {
            return;
        }

        boolean lock = redisService.lock(RedisConstant.LOCK_STORY);
        if (!lock) {
            log.info("==>任务未获取到锁，跳过当前任务");
            return;
        }

        String redisKey = RedisKey.STORY;
        try {
            if (redisService.hasKey(redisKey)) {
                log.info("定时器正在执行，本次跳过");
                return;
            }
            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = DateUtil.getCurrentDateString();
            redisService.set(redisKey, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 获取栏目，这里不用，因为在获取稿件的时候，不传入栏目id可以查询所有数据
            //List<JSONObject> columnList = runColumn();
            //if (CollectionUtil.isEmpty(columnList)) {
            //    return;
            //}

            // 先清空数据
            LambdaEsQueryWrapper<GkStoryData> queryWrapper = EsWrappers.lambdaQuery(GkStoryData.class)
                    .matchAllQuery();
            gkStoryMapper.delete(queryWrapper);
            getStory();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(redisKey);
            redisService.unlock(RedisConstant.LOCK_STORY);
        }

    }

    /**
     * 获取栏目id
     */
    private List<JSONObject> runColumn() {
        List<JSONObject> allDataList = Lists.newArrayList();
        try {
            long timeStamp = System.currentTimeMillis();
            String signatures = GkDesUtil.encode(timeStamp + appKey, appSecret);
            String apiUrl = columnApi.replace("{channelId}", channelId);
            HttpRequest get = HttpUtil.createGet(apiUrl);
            get.header("app_key", appKey);
            get.header("signatures", signatures);
            get.header("timeStamp", timeStamp + "");
            log.info("runColumn.api:{},headers:{}", apiUrl, JSON.toJSONString(get.headers()));
            String response = get.execute().body();
            log.info("runColumn.result:{}", response);
            if (StringUtils.isBlank(response) || !JSONUtil.isTypeJSONObject(response)) {
                return allDataList;
            }
            
            JSONObject responseData = JSON.parseObject(response);
            if (!"0".equalsIgnoreCase(responseData.getString("code"))) {
                log.info("runColumn.error:{}", responseData.getString("msg"));
                return allDataList;
            }
            List<JSONObject> dataList = responseData.getJSONArray("data").toList(JSONObject.class);
            if (CollectionUtil.isEmpty(dataList)) {
                log.info("runColumn.error:dataList is empty");
                return allDataList;
            }
            List<JSONObject> topList = dataList.stream().filter(p -> {
                JSONArray children = p.containsKey("children") ? p.getJSONArray("children") : new JSONArray();
                return CollectionUtil.isEmpty(children);
            }).collect(Collectors.toList());
            JSONObject remove = topList.remove(0);
            JSONObject jsonObject = topList.get(0);
            jsonObject.put("children", ListUtil.toList(remove));
            //JSONObject jsonObject = topList.get(0);
            allDataList.addAll(topList);
            Stream.iterate(dataList, item ->
                            item.stream()
                                    .filter(p -> p.containsKey("children"))
                                    .flatMap(p -> {
                                        List<JSONObject> childrenList = p.getJSONArray("children").toList(JSONObject.class);
                                        childrenList.forEach(detail -> {
                                            detail.put("routeId", p.getOrDefault("routeId", StringConstant.BLANK) + p.getString("uuid") + "/");
                                            detail.put("routeName", p.getOrDefault("routeName", StringConstant.BLANK) + p.getString("name") + "/");
                                        });
                                        allDataList.addAll(childrenList);
                                        return childrenList.stream();
                                    })
                                    .collect(Collectors.toList()))
                    .filter(CollectionUtil::isEmpty)
                    .findAny();
            log.info("allDataList:{}", JSON.toJSONString(allDataList));
            //log.info("collect:{}", JSON.toJSONString(collect));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allDataList;
    }

    /**
     * 分页获取稿件
     *
     */
    private void getStory() {
        Integer totalPages = run(0);
        if (totalPages > 1) {
            int pageNo = 1;
            while (pageNo <= totalPages) {
                run(pageNo);
                pageNo++;
            }
        }
    }

    /**
     * 获取栏目列表,返回总页数
     * @param pageNumber
     * @return
     */
    private Integer run(int pageNumber) {
        try {
            //String columnIds = columnList.stream().map(p -> p.getString("uuid")).collect(Collectors.joining(","));
            long timeStamp = System.currentTimeMillis();
            String signatures = GkDesUtil.encode(timeStamp + appKey, appSecret);

            String api = storyApi.replace("{channelId}", channelId);
            HttpRequest post = HttpUtil.createPost(api);
            post.header("Content-Type", "application/x-www-form-urlencoded");

            JSONObject param = new JSONObject();
            param.put("app", "2");
            param.put("timeStamp", timeStamp);
            param.put("key", appKey);
            param.put("signatures", signatures);
            param.put("pageNumber", pageNumber);
            param.put("pageSize", 10);
            post.form(param);
            log.info("runColumn.api:{},param:{}", api, JSON.toJSONString(param));
            String body = post.execute().body();
            log.info("runColumn.result:{}", body);
            if (StringUtils.isBlank(body) || !JSONUtil.isTypeJSONObject(body)) {
                return 0;
            }

            JSONObject responseData = JSON.parseObject(body);
            if (!"0".equalsIgnoreCase(responseData.getString("code"))) {
                return 0;

            }
            JSONObject data = responseData.getJSONObject("data");
            Integer totalPages = data.getInteger("totalPages");

            JSONArray content = data.getJSONArray("content");
            if (CollectionUtil.isEmpty(content)) {
                return 0;
            }
            List<GkStoryData> list = content.toList(GkStoryData.class);
            String currentTime = com.wenge.model.utils.DateUtil.getCurrentTime();
            list.forEach(item -> {
                item.setEsId(IdUtil.simpleUUID());
                item.setCreateTime(currentTime);

                if (StringUtils.isNotBlank(item.getJsonUrl())) {
                    try {
                        String jsonUrl = item.getJsonUrl();
                        jsonUrl = jsonUrl.replace("http://", "https://");
                        String response = HttpUtil.get(jsonUrl);
                        if (StringUtils.isNotBlank(response) && JSONUtil.isTypeJSONObject(response)) {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String contentHtml = jsonObject.getString("content");
                            item.setContent(contentHtml);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            gkStoryMapper.insertBatch(list);
            return totalPages;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
