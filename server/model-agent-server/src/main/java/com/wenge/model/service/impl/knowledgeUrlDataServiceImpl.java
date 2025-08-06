package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.wenge.model.dto.param.AddKnowledgeUrlDataParam;
import com.wenge.model.dto.param.DeleteKnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeUrlDataParam;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wenge.model.entity.SmartAgentLog;
import com.wenge.model.entity.UrlParserInfo;
import com.wenge.model.enums.ParserUrlStatusEnum;
import com.wenge.model.mapper.KnowledgeInfoMapper;
import com.wenge.model.mapper.UrlParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeUrlDataMapper;
import com.wenge.model.mapper.es.SmartAgentLogMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.KnowledgeUrlDataService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.LogUtils;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;

@Service
@Slf4j
public class knowledgeUrlDataServiceImpl implements KnowledgeUrlDataService {

    @Autowired
    private KnowledgeUrlDataMapper knowledgeUrlDataMapper;
    @Autowired
    private KnowledgeInfoMapper knowledgeInfoMapper;

    @Autowired
    private SmartAgentLogMapper smartAgentLogMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private WosUtil wosUtil;

    @Autowired
    private UrlParserInfoMapper urlParserInfoMapper;

    @Override
    public Result addKnowledgeUrlData(AddKnowledgeUrlDataParam param) {
        String knowledgeId = param.getKnowledgeId();
        // 验证知识库是否存在
        if (StringUtils.isBlank(knowledgeId)) {
            return Result.fail("知识库不存在");
        }

        Map<String,Object> query = new HashMap<>();
        query.put("knowledge_id",knowledgeId);
        KnowledgeInfo knowledgeInfo = knowledgeInfoMapper.selectOneByMap(query);
        if (Objects.isNull(knowledgeInfo)) {
            return Result.fail("知识库不存在");
        }
        /*if (!knowledgeTypeEnum.URL_PARSE.getCode().equals(knowledgeInfo.getKnowledgeType())) {
            return Result.fail("知识库类型必须为" + knowledgeTypeEnum.URL_PARSE.getName());
        }*/

        // 先调用接口读取页面，在把内容输出到文本中，然后调用大模型切片，切片之后输入到切出来的变成多个文本，每个文本是一个ES文档
        List<String> urls = param.getUrls();
        // 把URL集合处理为map，k：这个url本身  v: 1表示未解析  2表示正在解析 3表示解析完成 4表示解析失败
        Map<String, Integer> collect = urls.stream().collect(Collectors.toMap(s -> s, s -> ParserUrlStatusEnum.BEGIN.getCode()));
        for (String url : urls) {
            // 写初始状态
            String urlId = IdUtil.randomUUID();
            UrlParserInfo urlParserInfo = UrlParserInfo.builder()
                    .urlId(urlId)
                    .knowledgeId(knowledgeId)
                    .status(5)
                    .pageUrl(url)
                    .foldersId(param.getFoldersId())
                    .errorNum(0)
                    .deleteFlag(0)
                    .enable(YesNoEnum.YES.getName())
                    .createTime(DateUtil.format(new Date(),DateUtil.PATTERN_1))
                    .updateTime(DateUtil.format(new Date(),DateUtil.PATTERN_1))
                    .build();
            urlParserInfoMapper.insert(urlParserInfo);
        }

        // 添加日志
        //addLog(Collections.singletonList(null), AppContextHolder.getTokenUserInfo(),"添加URL知识库");
        return Result.success();
    }

    @Override
    public Result deleteKnowledgeUrlData(DeleteKnowledgeDataParam param) {
        if (CollectionUtil.isNotEmpty(param.getId())) {
            // 插入日志
            List<KnowledgeUrlData> knowledgeUrlDataList = knowledgeUrlDataMapper.selectBatchIds(param.getId());
            addLog(knowledgeUrlDataList, AppContextHolder.getTokenUserInfo(),"删除URL知识库数据");
            knowledgeUrlDataMapper.deleteBatchIds(param.getId());
        }
        if (CollectionUtil.isNotEmpty(param.getKnowledgeId())) {
            LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
                    .in(CollectionUtil.isNotEmpty(param.getKnowledgeId()), KnowledgeUrlData::getKnowledgeId, param.getKnowledgeId());
            // 插入日志
            List<KnowledgeUrlData> knowledgeUrlDataList = knowledgeUrlDataMapper.selectList(wrapper);
            addLog(knowledgeUrlDataList, AppContextHolder.getTokenUserInfo(),"删除QA知识库数据");

            knowledgeUrlDataMapper.delete(wrapper);
        }
        return Result.success();
    }

    @Override
    public Result addKnowledgeData(AddKnowledgeUrlDataParam param) {
        String accountName = AppContextHolder.getAccountName();
        String time = DateUtil.format(new Date(), DateUtil.PATTERN_1);
        KnowledgeUrlData knowledgeUrlData = KnowledgeUrlData.builder()
                .knowledgeId(param.getKnowledgeId())
                .urlId(param.getUrlId())
                .content(param.getContent())
                .enable(1)
                .url(param.getUrl())
                .encodeType(1)
                .createTime(time)
                .createUser(accountName)
                .updateUser(accountName)
                .updateTime(time)
                .category("原文切片")
                .id(IdUtil.randomUUID())
                .effectiveStartTime(StrUtil.isBlank(param.getEffectiveStartTime()) ? null : param.getEffectiveStartTime())
                .effectiveEndTime(StrUtil.isBlank(param.getEffectiveEndTime()) ? null : param.getEffectiveEndTime())
                .build();
        denseVectorService.modelEncode(param.getContent(), param.getKnowledgeId(), knowledgeUrlData, CONTENT_DENSE_FILED);

        return Result.success(knowledgeUrlDataMapper.insert(knowledgeUrlData));
    }

    @Override
    public Result updateKnowledgeData(KnowledgeUrlData param) {
        param.setEffectiveStartTime(StrUtil.isBlank(param.getEffectiveStartTime()) ? null : param.getEffectiveStartTime());
        param.setEffectiveEndTime(StrUtil.isBlank(param.getEffectiveEndTime()) ? null : param.getEffectiveEndTime());
        //String content = param.getContent();
        denseVectorService.modelEncode(param.getContent(), param.getKnowledgeId(), param, CONTENT_DENSE_FILED);

        //List<Double> doubles = knowledgeDataService.modelEncode(content);
        //param.setContentDense(doubles);
        return Result.success(knowledgeUrlDataMapper.updateById(param));
    }

    @Override
    public Result getKnowledgeUrlDataDetail(KnowledgeUrlData param) {
        return Result.success(knowledgeUrlDataMapper.selectById(param.getId()));
    }

    @Override
    public Result getKnowledgeUrlDataListByParentId(KnowledgeUrlData param) throws IOException {

        LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
                .eq(StringUtils.isNotBlank(param.getParentId()), KnowledgeUrlData::getParentId, param.getParentId())
                .size(10);

        List<KnowledgeUrlData> knowledgeUrlDataList = knowledgeUrlDataMapper.selectList(wrapper);

        //SearchRequest searchRequest = new SearchRequest("smart_agent_knowledge_url_data");
        //SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.size(10).query(QueryBuilders.termQuery("parentId",param.getParentId()));
        //searchRequest.source(searchSourceBuilder);
        //SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //SearchHit[] searchHits = searchResponse.getHits().getHits();
        //List<KnowledgeUrlData> knowledgeUrlDataList = new ArrayList<>();
        //for (SearchHit searchHit : searchHits) {
        //    KnowledgeUrlData data = JSONObject.parseObject(searchHit.getSourceAsString(), KnowledgeUrlData.class);
        //    data.setId(searchHit.getId());
        //    knowledgeUrlDataList.add(data);
        //}
        return Result.success(knowledgeUrlDataList);
    }

    @Override
    public Result getUrlDataList(KnowledgeUrlDataParam param) {
        LambdaEsQueryWrapper<KnowledgeUrlData> wrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class)
                .notSelect(KnowledgeUrlData::getTitleDense, KnowledgeUrlData::getContentDense)
                .eq(KnowledgeUrlData::getUrlId, param.getUrlId())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), KnowledgeUrlData::getContent, param.getContent());
        EsPageInfo<KnowledgeUrlData> pageInfo = knowledgeUrlDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        return Result.success(pageInfo);
    }

    private Integer addLog(List<KnowledgeUrlData> dataList, TokenUser tokenOauthUserInfo, String op){
        List<SmartAgentLog> smartAgentLogList = new ArrayList<>();
        for (KnowledgeUrlData data : dataList) {
            smartAgentLogList.add(LogUtils.buildSmartAgentLog(data.getKnowledgeId(),
                    op, tokenOauthUserInfo.getId(), tokenOauthUserInfo.getAccountName(),
                    op + data.getTitle(), com.wenge.model.utils.DateUtil.format(new Date(),
                            com.wenge.model.utils.DateUtil.PATTERN_1))
            );
        }
        return smartAgentLogMapper.insertBatch(smartAgentLogList);
    }

}
