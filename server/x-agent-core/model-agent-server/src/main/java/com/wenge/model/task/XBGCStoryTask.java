package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryColumn;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wenge.model.entity.UrlParserInfo;
import com.wenge.model.entity.XBGCStoryData;
import com.wenge.model.mapper.KnowledgeInfoMapper;
import com.wenge.model.mapper.UrlParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeUrlDataMapper;
import com.wenge.model.mapper.es.XBGCStoryMapper;
import com.wenge.model.utils.GkDesUtil;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取稿件【西部国传问答】
 */
@RestController
@RequestMapping("/agentTask/xibuguochuan")
@Slf4j
public class XBGCStoryTask {

    @Value("${task.XBGCStory.enable}")
    private Boolean enable;

    @Value("${task.XBGCStory.columnApi}")
    private String columnApi;
    @Value("${task.XBGCStory.channelId}")
    private String channelId;
    @Value("${task.XBGCStory.appKey}")
    private String appKey;
    @Value("${task.XBGCStory.appSecret}")
    private String appSecret;
    @Value("${task.XBGCStory.storyApi}")
    private String storyApi;

    @Autowired
    private XBGCStoryMapper xbgcStoryMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private KnowledgeInfoMapper knowledgeInfoMapper;

    @Autowired
    private UrlParserInfoMapper urlParserInfoMapper;

    @Autowired
    private KnowledgeUrlDataMapper knowledgeUrlDataMapper;




    @PostConstruct
    public void init() {

        String value = redisService.get(RedisKey.XBGC_STORY, String.class);
        String localhostStr = NetUtil.getLocalhostStr();

        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.XBGC_STORY);
            redisService.unlock(RedisConstant.XBGC_LOCK_STORY);
        }
    }


    /**
     * 获取稿件 定时任务建议每天凌晨执行一次
     */
    @GetMapping("/story")
    // @Scheduled(cron = "${task.XBGCStory.cron}")
    public void story() {
        if (!enable) {
            return;
        }

        boolean lock = redisService.lock(RedisConstant.XBGC_LOCK_STORY);
        if (!lock) {
            log.info("==>任务未获取到锁，跳过当前任务");
            return;
        }

        String redisKey = RedisKey.XBGC_STORY;
        try {
            if (redisService.hasKey(redisKey)) {
                log.info("定时器正在执行，本次跳过");
                return;
            }
            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = DateUtil.getCurrentDateString();
            redisService.set(redisKey, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 先清空稿件数据
            LambdaEsQueryWrapper<XBGCStoryData> queryWrapper = EsWrappers.lambdaQuery(XBGCStoryData.class)
                    .matchAllQuery();
            xbgcStoryMapper.delete(queryWrapper);

            // 再保存稿件数据
            List<XBGCStoryData> storyDataList = saveStory();

            // 提取数据中的url并保存到配置的知识库
            saveUrlData(storyDataList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(redisKey);
            redisService.unlock(RedisConstant.XBGC_LOCK_STORY);
        }

    }


    /**
     * 分页获取稿件
     */
    private List<XBGCStoryData> saveStory() {
        List<XBGCStoryData> dataList = new ArrayList<>();
        Integer totalPages = run(0, dataList);
        if (totalPages > 1) {
            int pageNo = 1;
            while (pageNo <= totalPages) {
                run(pageNo, dataList);
                pageNo++;
            }
        }

        return dataList;
    }


    /**
     * 获取栏目列表,返回总页数
     *
     * @param pageNumber
     * @param dataList
     * @return
     */
    private Integer run(int pageNumber, List<XBGCStoryData> dataList) {
        try {
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
            List<XBGCStoryData> list = content.toList(XBGCStoryData.class);
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

            dataList.addAll(list);
            xbgcStoryMapper.insertBatch(list);

            return totalPages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 保存url数据
     *
     * @param storyDataList 稿件数据
     */
    private void saveUrlData(List<XBGCStoryData> storyDataList) {
        String knowledgeId = AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_XBGC_GPT_KNOWLEDGE_ID);
        if (StringUtils.isBlank(knowledgeId)) {
            log.info("未配置知识库ID，跳过保存");
            return;
        }

        Map<String, Object> query = new HashMap<>();
        query.put("knowledge_id", knowledgeId);
        KnowledgeInfo knowledgeInfo = knowledgeInfoMapper.selectOneByMap(query);
        if (Objects.isNull(knowledgeInfo)) {
            log.info("知识库不存在");
            return;
        }

        // 清空url
        List<String> urlIds = storyDataList.stream()
                .map(XBGCStoryData::getId)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());;
        Wrappers wrappers = Wrappers.init().where(new QueryColumn("url_id").in(urlIds));
        urlParserInfoMapper.deleteByQuery(wrappers);
        // 清空url对应ES中关联切片信息
        LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
                .in(!CollectionUtils.isEmpty(urlIds), KnowledgeUrlData::getUrlId, urlIds);
        knowledgeUrlDataMapper.delete(wrapper);

        List<UrlParserInfo> urlParserInfoList = new ArrayList<>();
        for (XBGCStoryData data : storyDataList) {
            UrlParserInfo urlParserInfo = UrlParserInfo.builder()
                    .urlId(data.getId())
                    .knowledgeId(knowledgeId)
                    .status(5)
                    .pageUrl(data.getUrl())
                    .errorNum(0)
                    .deleteFlag(0)
                    .createTime(com.wenge.model.utils.DateUtil.format(new Date(), com.wenge.model.utils.DateUtil.PATTERN_1))
                    .updateTime(com.wenge.model.utils.DateUtil.format(new Date(), com.wenge.model.utils.DateUtil.PATTERN_1))
                    .build();
            urlParserInfoList.add(urlParserInfo);
        }
        urlParserInfoMapper.insertBatch(urlParserInfoList);
    }

}
