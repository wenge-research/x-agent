package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wenge.model.entity.UrlParserInfo;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.mapper.UrlParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeUrlDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.UrlParserInfoService;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wenge.model.entity.table.UrlParserInfoTableDef.URL_PARSER_INFO;

/**
 * 媒体解析
 */
@RestController
@Api(tags = "知识库url解析接口")
@RequestMapping("/agentTask/url")
@Slf4j
public class KnowledgeUrlParserTask {

    @Autowired
    private WosUtil wosUtil;

    @Autowired
    private UrlParserInfoService urlParserInfoService;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private UrlParserInfoMapper urlParserInfoMapper;

    @Autowired
    private KnowledgeUrlDataMapper knowledgeUrlDataMapper;

    @Value("${task.url.enable}")
    private boolean enable;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Resource(name = "yayiPaserUrlPool")
    private ThreadPoolExecutor yayiPaserUrlPool;

    @Value("${third.parse.url}")
    private String urlApi;

    @Value("${appframe.yayi.generate128k.appKey}")
    private String appKey;

    @Value("${appframe.yayi.generate128k.appSecret}")
    private String appSecret;


    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisConstant.RUN_URL_PARSER, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_URL_PARSER);
            redisService.unlock(RedisConstant.LOCK_URL_PARSER);
        }
    }

    @RequestMapping("/urlParser")
    // @Scheduled(cron = "${task.url.cron}")
    public void urlParser() {
        if (!enable) {
            return;
        }

        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_URL_PARSER);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_URL_PARSER);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_URL_PARSER, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 获取所有未成功解析的url
            List<UrlParserInfo> urlParserInfos = urlParserInfoService.getParseredKnowledgeUrl();
            if (CollectionUtil.isEmpty(urlParserInfos)) {
                return;
            }

            log.info("*************** start-启动定时任务开始解析所有未成功解析的url");
            urlParserInfos.forEach(this::parseUrl);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_URL_PARSER);
            redisService.unlock(RedisConstant.LOCK_URL_PARSER);
        }
    }


    @RequestMapping("/urlParserById")
    public Result urlParser(@RequestParam("id") Long id) {

        // 获取所有未成功解析的url
        UrlParserInfo urlParserInfos = urlParserInfoService.getById(id);
        if (urlParserInfos == null) {
            return Result.success();
        }

        parseUrl(urlParserInfos);
        return Result.success();
    }

    private void parseUrl(UrlParserInfo urlParserInfo) {
        // 更新状态为解析中
        updateStatusParsing(urlParserInfo);

        // 清空该文件的所有es数据
        clearEsData(urlParserInfo);

        // 解析url为文本
        parseUrlToContent(urlParserInfo);
    }

    /**
     * 更新状态为解析中
     * @param urlParserInfo url解析信息
     */
    private void updateStatusParsing(UrlParserInfo urlParserInfo) {
        UpdateChain.of(UrlParserInfo.class)
                .set(URL_PARSER_INFO.STATUS, FileStatusEnum.PARSING.getCode())
                .set(URL_PARSER_INFO.ERROR_MSG, "")
                .where(URL_PARSER_INFO.URL_ID.eq(urlParserInfo.getUrlId()))
                .update();
    }

    /**
     * 清空该文件的所有es数据
     * @param urlParserInfo url解析信息
     */
    private void clearEsData(UrlParserInfo urlParserInfo) {
        LambdaEsQueryWrapper<KnowledgeUrlData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
                .eq(KnowledgeUrlData::getUrlId, urlParserInfo.getUrlId());
        knowledgeUrlDataMapper.delete(esQueryWrapper);
    }

    /**
     * 解析url内容
     * @param urlParserInfo url解析信息
     */
    private void parseUrlToContent(UrlParserInfo urlParserInfo) {
        String knowledgeId = urlParserInfo.getKnowledgeId();
        String pageUrl = urlParserInfo.getPageUrl();
        String urlId = urlParserInfo.getUrlId();
        Integer errorNum = Objects.isNull(urlParserInfo.getErrorNum()) ? 0 : urlParserInfo.getErrorNum();
        ParserUrlTask parserUrlTask = ParserUrlTask.builder()
                .knowledgeId(knowledgeId)
                .parserUrl(urlApi)
                .pageUrl(pageUrl)
                .urlId(urlId)
                .errorNum(errorNum)
                .knowledgeUrlDataMapper(knowledgeUrlDataMapper)
                .urlParserInfoMapper(urlParserInfoMapper)
                .yayiServer(yayiServer)
                .wosUtil(wosUtil)
                .denseVectorService(denseVectorService)
                .bucket(bucket)
                .appKey(appKey)
                .appSecret(appSecret)
                .build();
        yayiPaserUrlPool.submit(parserUrlTask);
    }

}
