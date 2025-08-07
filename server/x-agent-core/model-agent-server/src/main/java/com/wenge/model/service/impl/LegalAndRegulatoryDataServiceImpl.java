package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.LegalAndRegulatoryDataPageParam;
import com.wenge.model.dto.param.SyncLegalAndRegulatoryDataParam;
import com.wenge.model.dto.result.LegalAndRegulatoryDateCrawlResult;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.entity.LegalAndRegulatoryData;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.mapper.es.LegalAndRegulatoryDataMapper;
import com.wenge.model.service.LegalAndRegulatoryDataService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description 法律法规库服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
@Service
@Slf4j
public class LegalAndRegulatoryDataServiceImpl implements LegalAndRegulatoryDataService {

    @Value("${wangxinban.legal.crawl.url:'http://172.16.100.37:9009/crawl'}")
    private String LegalAndRegulatoryDataCrawlingUrl;

    @Autowired
    private LegalAndRegulatoryDataMapper legalAndRegulatoryDataMapper;


    @Override
    public Result getLegalAndRegulatoryDataList(LegalAndRegulatoryDataPageParam legalAndRegulatoryDataPageParam) {
        LambdaEsQueryWrapper<LegalAndRegulatoryData> wrapper = EsWrappers.lambdaQuery(LegalAndRegulatoryData.class)
                .eq(LegalAndRegulatoryData::getStatus, 0)
                .eq(StringUtils.isNotBlank(legalAndRegulatoryDataPageParam.getCompanyName()),LegalAndRegulatoryData::getCompanyName, legalAndRegulatoryDataPageParam.getCompanyName())
                .eq(legalAndRegulatoryDataPageParam.getType()!=null,LegalAndRegulatoryData::getType, legalAndRegulatoryDataPageParam.getType())
                .eq(StringUtils.isNotBlank(legalAndRegulatoryDataPageParam.getTab()),LegalAndRegulatoryData::getTab, legalAndRegulatoryDataPageParam.getTab())
                .like(StringUtils.isNotBlank(legalAndRegulatoryDataPageParam.getKeyword()), LegalAndRegulatoryData::getTitle, legalAndRegulatoryDataPageParam.getKeyword())
                .orderByDesc("pushTimeStr.keyword");
        EsPageInfo<LegalAndRegulatoryData> page = legalAndRegulatoryDataMapper.pageQuery(wrapper, legalAndRegulatoryDataPageParam.getPageNo(), legalAndRegulatoryDataPageParam.getPageSize());
        return Result.success(page);
    }

    @Override
    public Result synchronousLegalAndRegulatoryData(SyncLegalAndRegulatoryDataParam syncLegalAndRegulatoryDataParam) {
        //POST请求
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(7200, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Request.Builder builder = new Request.Builder();
        Map<Object, Object> param = new HashMap<>();
        param.put("start_url", syncLegalAndRegulatoryDataParam.getStart_url());
        param.put("tar_page", syncLegalAndRegulatoryDataParam.getTar_page());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONUtil.toJsonStr(param));
        builder.url(this.LegalAndRegulatoryDataCrawlingUrl).post(body);
        log.info("synchronousLegalAndRegulatoryData 网信办 法律法规数据爬取同步:{}, requestUrl:{}", syncLegalAndRegulatoryDataParam.getStart_url(), this.LegalAndRegulatoryDataCrawlingUrl);
        Call call = httpClient.newCall(builder.build());
        Response response;
        try {
            long startTime = System.currentTimeMillis();
            response = call.execute();
            long endTime = System.currentTimeMillis();
            log.info("synchronousLegalAndRegulatoryData 网信办 法律法规数据爬取，耗时:{}ms", syncLegalAndRegulatoryDataParam.getStart_url(), endTime - startTime);

            String string = response.body().string();
            LegalAndRegulatoryDateCrawlResult result = JSONUtil.toBean(string, LegalAndRegulatoryDateCrawlResult.class);
            if (result != null && result.getSubUrl() != null && result.getSubUrl().size() > 0) {
                result.getSubUrl().stream().forEach(item -> {
                    if (StringUtils.isNotBlank(item.getUrl())) {
                        LegalAndRegulatoryData legalAndRegulatoryData = new LegalAndRegulatoryData();
                        legalAndRegulatoryData.setType(syncLegalAndRegulatoryDataParam.getType());
                        legalAndRegulatoryData.setCompanyName("wxm");
                        legalAndRegulatoryData.setTitle(item.getTitle());
                        legalAndRegulatoryData.setUrl(item.getUrl());
                        legalAndRegulatoryData.setTab(syncLegalAndRegulatoryDataParam.getTab());
                        legalAndRegulatoryData.setStatus(0 + "");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timeStamp = sdf.format(new Date());
                        legalAndRegulatoryData.setCreateTime(timeStamp);
                        legalAndRegulatoryData.setUpdateTime(timeStamp);

                        final Integer startZw = item.getContent().indexOf("正文标题|");
                        String contentZw = item.getContent().substring(startZw + 5);
                        final Integer endZw = contentZw.indexOf("|");

                        final Integer startFt =contentZw.indexOf("发布时间|");
                        String contentFt =contentZw.substring(startFt+5);
                        final Integer endFt = contentFt.indexOf("|");

                        final Integer startLy =contentFt.indexOf("来源|");
                        String contentLy =contentFt.substring(startLy+3);
                        final Integer endLy = contentLy.indexOf("|");

                        final Integer startCon =contentLy.indexOf("正文内容|");
                        String contentCon =contentLy.substring(startCon+5);

                        legalAndRegulatoryData.setTitle(contentZw.substring(0, endZw));
                        legalAndRegulatoryData.setPushTimeStr(contentFt.substring(0, endFt));
                        legalAndRegulatoryData.setSource(contentLy.substring(0, endLy));
                        legalAndRegulatoryData.setContent(contentCon);
                        //判断是否已经存在
                        if (!isExist(legalAndRegulatoryData.getTitle())) {
                            final int save = legalAndRegulatoryDataMapper.insert(legalAndRegulatoryData);
                            log.info("synchronousLegalAndRegulatoryData 网信办 法律法规数据爬 {}", save);
                        }else {
                            log.info("当前标题已经存在:  {}", legalAndRegulatoryData.getTitle());
                        }
                    }
                });
            }
//            log.info(result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean isExist(String title){
        if (StringUtils.isBlank(title)) {
            return true;
        }
//        QueryWrapper.create().where(LEGAL_AND_REGULATORY_DATA.TITLE.eq(title)).and(LEGAL_AND_REGULATORY_DATA.STATUS.eq("0"))
        LambdaEsQueryWrapper<LegalAndRegulatoryData> dialogueLambdaEsQueryWrapper = EsWrappers.lambdaQuery(LegalAndRegulatoryData.class);
        dialogueLambdaEsQueryWrapper.eq(LegalAndRegulatoryData::getTitle, title);
        dialogueLambdaEsQueryWrapper.eq(LegalAndRegulatoryData::getStatus, "0");
        return legalAndRegulatoryDataMapper.selectList(dialogueLambdaEsQueryWrapper).size() > 0 ? true : false;
    }

}