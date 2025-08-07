package com.wg.appframe.elasticsearch.service.impl;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wg.appframe.elasticsearch.dto.EsDocumentVo;
import com.wg.appframe.elasticsearch.service.EsRestHighLevelClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * @ClassName: ElasticsearchUtil
 * @Package: com.springboot.cloud.elasticsearch.service
 * @Description:
 * @Author: ztf
 * @Date: 2021/5/1815:01
 * @Version: 1.0
 */
@Slf4j
@Service
public class EsRestHighLevelClientServiceImpl implements EsRestHighLevelClientService {


    @Resource
    private RestHighLevelClient client;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public SearchResponse searchByTermQuery(String index, Integer pageNo, Integer pageSize, String keyword, List<String> fieldList, String orderField, String orderBy) throws Exception {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
        if (StringUtils.isNotBlank(keyword)) {
            if (CollectionUtils.isNotEmpty(fieldList)) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                fieldList.forEach(field -> {
//                    boolQueryBuilder.must(QueryBuilders.termQuery(field + ".keyword", keyword));
                    boolQueryBuilder.should(QueryBuilders.termQuery(field, keyword));
                });
                searchSourceBuilder.query(boolQueryBuilder);
            } else {
                MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword);
                searchSourceBuilder.query(multiMatchQueryBuilder);
            }
        }
        this.setSearchBuilder(searchSourceBuilder, pageNo, pageSize, orderField, orderBy);
        request.source(searchSourceBuilder);
        log.debug("DSL:" + searchSourceBuilder.toString());
        return client.search(request, RequestOptions.DEFAULT);
    }

    @Override
    public SearchResponse searchByTermQueryMust(String index, Integer pageNo, Integer pageSize, Map<String, String> conditionMap, String orderField, String orderBy) throws Exception {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
        if (CollectionUtils.isNotEmpty(conditionMap)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            conditionMap.forEach((k, v) -> boolQueryBuilder.must(QueryBuilders.termQuery(k + ".keyword", v)));
            searchSourceBuilder.query(boolQueryBuilder);
        }
        this.setSearchBuilder(searchSourceBuilder, pageNo, pageSize, orderField, orderBy);
        request.source(searchSourceBuilder);
        log.debug("DSL:" + searchSourceBuilder.toString());
        return client.search(request, RequestOptions.DEFAULT);
    }

    @Override
    public boolean saveBatch(String index, List<EsDocumentVo> collection) throws Exception {
//        if (!isIndexExist(index)) {
//            createIndex(index);
//        }
        BulkRequest bulkRequest = new BulkRequest();
        BulkResponse response = null;
//        //最大数量不得超过20万
        collection.stream().forEach(doc -> {
            IndexRequest request = new IndexRequest(index, doc.getType(),
                    null == doc.getDocumentId() ? IdUtil.randomUUID() : doc.getDocumentId());
            request.source(JSON.toJSONString(doc.getJsonDocument()), XContentType.JSON);
            bulkRequest.add(request);
        });

        try {
            response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (null == response) {
            return false;
        }
        if (response.hasFailures()) {
            Arrays.stream(response.getItems()).forEach(item -> {
                if (item.isFailed()) {
                    log.error("EsRestHighLevelClientServiceImpl.saveBatch:{}", item.getFailureMessage());
                }
            });
            return false;
        }
        return true;
    }

    @Override
    public boolean indexExits(String index) throws IOException {
        return isIndexExist(index);
    }

    @Override
    public UpdateResponse updateDataById(Object object, String index, String id, Boolean isRealTime) throws IOException {
        UpdateRequest update = new UpdateRequest(index, id);
        if (isRealTime != null && isRealTime) {
            update.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        }
        update.timeout("1s");
        update.doc(JSONUtil.toJsonStr(object), XContentType.JSON);
        UpdateResponse response = client.update(update, RequestOptions.DEFAULT);
        return response;
    }

    @Override
    public IndexResponse addData(Object object, String index) throws IOException {
        return addData(object, index, IdUtil.randomUUID());
    }

    @Override
    public IndexResponse addData(Object object, String index, String id) throws IOException {
        //创建请求
        IndexRequest request = new IndexRequest(index);
        //规则 put /test_index/_doc/1
        request.id(id);
        request.timeout(TimeValue.timeValueSeconds(1));
        //将数据放入请求 json
        request.source(JSONUtil.toJsonStr(object), XContentType.JSON);
        //客户端发送请求
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response;
    }

    /**
     * 判断是否存在索引
     *
     * @param index 传入索引名
     * @return 返回验证结果
     * @throws IOException
     */
    private boolean isIndexExist(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }


    @Override
    public SearchResponse query(SearchRequest request) throws Exception {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("EsRestHighLevelClientServiceImpl.query.error:{}", e.getMessage(), e);
        }

        return searchResponse;
    }

    @Override
    public boolean clearEsCache(String indexName) {

        ClearIndicesCacheRequest clearIndicesCacheRequest = new ClearIndicesCacheRequest(indexName)
                .queryCache(true)
                .requestCache(true)
                .fieldDataCache(true);
        try {
            ClearIndicesCacheResponse clearCacheResponse = client.indices().clearCache(clearIndicesCacheRequest, RequestOptions.DEFAULT);
            if (clearCacheResponse.getFailedShards() > 0) {
                return false;
            }
        } catch (Exception e) {
            log.error("清除ES缓存异常:{},参数为:{}", e.getLocalizedMessage(), indexName);
            return false;
        }
        return true;
    }

    @Override
    public SearchResponse getDataByScroll(String scrollId) throws IOException {
        SearchScrollRequest request = new SearchScrollRequest(scrollId);
        Scroll scroll = new Scroll(new TimeValue(1000 * 60 * 60 * 24));
        request.scroll(scroll);
        return client.scroll(request, RequestOptions.DEFAULT);
    }

    @Override
    public void cleanScroll(String scrollId) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        //也可以选择setScrollIds()将多个scrollId一起使用
        clearScrollRequest.addScrollId(scrollId);
        try {
            client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
        }
    }



    @Override
    public SearchResponse searchByMatchQuery(String index, Integer pageNo, Integer pageSize, String keyword, List<String> fieldList, String orderField, String orderBy) throws Exception {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
        if (StringUtils.isNotBlank(keyword)) {
            MultiMatchQueryBuilder multiMatchQueryBuilder = null;
            if (CollectionUtils.isNotEmpty(fieldList)) {
                multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, ArrayUtil.toArray(fieldList, String.class));
            } else {
                multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword);
            }
            searchSourceBuilder.query(multiMatchQueryBuilder);
        }
        this.setSearchBuilder(searchSourceBuilder, pageNo, pageSize, orderField, orderBy);
        request.source(searchSourceBuilder);
        log.debug("DSL:" + searchSourceBuilder.toString());
        return client.search(request, RequestOptions.DEFAULT);
    }

    private void setSearchBuilder(SearchSourceBuilder searchSourceBuilder, Integer pageNo, Integer pageSize, String orderField, String orderBy) {
        pageNo = pageNo <= 0 ? 0 : (pageNo - 1) * pageSize;
        //设置确定结果要从哪个索引开始搜索的from选项，默认为0
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        if (StringUtils.isNotBlank(orderField)) {
            String[] orderFields = orderField.split(",");
            for (String field : orderFields) {
                if (StringUtils.isNotBlank(orderBy) && String.valueOf(orderBy).toLowerCase().equals(SortOrder.DESC.toString().toLowerCase())) {
                    searchSourceBuilder.sort(field, SortOrder.DESC);
                } else {
                    searchSourceBuilder.sort(field, SortOrder.ASC);
                }
            }
        }
    }


    private static class MappingSetting {
        protected Settings.Builder builder;
        protected String mappingSource;
    }

    @Override
    public SearchResponse queryStatistical(SearchRequest request) throws Exception {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("EsRestHighLevelClientServiceImpl.queryStatistical.error:{}", e.getMessage(), e);
        }

        return searchResponse;
    }

}
