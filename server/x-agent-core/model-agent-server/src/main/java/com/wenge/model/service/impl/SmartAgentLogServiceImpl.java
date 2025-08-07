package com.wenge.model.service.impl;

import com.wenge.model.dto.param.SmartAgentLogParam;
import com.wenge.model.entity.SmartAgentLog;
import com.wenge.model.mapper.es.SmartAgentLogMapper;
import com.wenge.model.service.SmartAgentLogService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author DELL
 */
@Service
@Slf4j
public class SmartAgentLogServiceImpl implements SmartAgentLogService {

    @Autowired
    private SmartAgentLogMapper smartAgentLogMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Result addSmartAgentLog(SmartAgentLog smartAgentLog) {
        try {
            return Result.success(smartAgentLogMapper.insert(smartAgentLog));
        }catch (Exception e){
            return Result.fail();
        }
    }

    @Override
    public Result addSmartAgentLogBatch(List<SmartAgentLog> smartAgentLogs) {
        try {
            return Result.success(smartAgentLogMapper.insertBatch(smartAgentLogs));
        }catch (Exception e){
            return Result.fail();
        }
    }

    @Override
    public Result smartAgentLogList(SmartAgentLogParam param) throws IOException {
        LambdaEsQueryWrapper<SmartAgentLog> wrapper = EsWrappers.lambdaQuery(SmartAgentLog.class)
                .eq(StringUtils.isNotBlank(param.getKnowledgeId()), SmartAgentLog::getKnoId, param.getKnowledgeId());
        EsPageInfo<SmartAgentLog> pageInfo = smartAgentLogMapper.pageQuery(wrapper, param.getPageNo(),
                param.getPageSize());
        return Result.success(pageInfo);
    }

    @Override
    public Result smartAgentLogListGroupTime(SmartAgentLogParam agentLogParam) throws IOException {
        //SearchRequest searchRequest = new SearchRequest("smart_agent_log_info");
        //SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.size(0).aggregation(
        //        AggregationBuilders
        //                .dateHistogram("aggTime")
        //                .field("opTime")
        //                .minDocCount(1L)
        //                .calendarInterval(DateHistogramInterval.HOUR)
        //                .subAggregation(
        //                        AggregationBuilders.topHits("topHitDocs").fetchSource(new String[] {"opType"}, null)
        //                                .size(10).sort("opTime", SortOrder.DESC)
        //                )
        //);
        //searchRequest.source(searchSourceBuilder);
        //SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //ParsedDateHistogram agg = searchResponse.getAggregations().get("aggTime");
        //List<? extends Histogram.Bucket> buckets = agg.getBuckets();
        //LinkedHashMap <String, LinkedList<SmartAgentLog>>result = new LinkedHashMap<>();
        //for (Histogram.Bucket bucket : buckets) {
        //    if(Objects.nonNull(bucket)){
        //        String opTime = bucket.getKeyAsString();
        //        ParsedTopHits topHits = bucket.getAggregations().get("topHitDocs");
        //        LinkedList<SmartAgentLog> agentLogs = new LinkedList<>();
        //        for (SearchHit hit : topHits.getHits().getHits()) {
        //            String opType = (String) hit.getSourceAsMap().get("opType");
        //            agentLogs.add(SmartAgentLog.builder().opType(opType).build());
        //        }
        //        result.put(opTime,agentLogs);
        //    }
        //}
        //// 获取键列表
        //List<String> keys = new ArrayList<>(result.keySet());
        //
        //// 按照键倒序排序
        //Collections.sort(keys, Collections.reverseOrder());
        //
        //// 创建一个新的LinkedHashMap，并按倒序键插入元素
        //LinkedHashMap<String, LinkedList<SmartAgentLog>> reversedMap = new LinkedHashMap<>();
        //for (String key : keys) {
        //    reversedMap.put(key, result.get(key));
        //}
        return Result.success(null);
    }
}

