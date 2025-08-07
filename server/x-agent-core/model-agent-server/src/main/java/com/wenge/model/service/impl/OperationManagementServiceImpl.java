package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.mapper.ApplicationInfoMapper;
import com.wenge.model.service.*;
import com.wenge.model.utils.DateRangeExample;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.OperaLog;
import com.wenge.oauth.mapper.ApplicationUserMapper;
import com.wenge.oauth.service.OperaLogService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.job.results.Bucket;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;

@Service
@Slf4j
public class OperationManagementServiceImpl implements OperationManagementService {

    @Resource
    private ApplicationUserMapper applicationUserMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${esIndex.dialogue}")
    private String dialogueIndex;

    @Value("${esIndex.knowledgeData}")
    private String knowledgeDataIndex;

    @Autowired
    private OperaLogService operaLogService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private ApplicationInfoMapper applicationsMapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private FileService fileService;

    @Autowired
    private KnowledgeDataService knowledgeDataService;

    @Autowired
    private UrlParserInfoService urlParserInfoService;

    @Autowired
    private DataSourceParserInfoService dataSourceParserInfoService;

    @Autowired
    private DialogueRecordService dialogueRecordService;

    @Autowired
    private ApplicationAnalysisService applicationAnalysisService;

    private long getApplicationCount(String type){
        long count = applicationsMapper.selectCountByQuery(Wrappers.init()
                .where(APPLICATION_INFO.DELETE_FLAG.eq(0))
                .and(APPLICATION_INFO.TYPE.ne("workflow"))
                .and(APPLICATION_INFO.TYPE.eq(type))
        );
        return count;
    }

    @Override
    public Result applicationCount() {
        JSONObject jsonObject = new JSONObject();

        ApplicationInfoPageParam applicationInfoPage =  new ApplicationInfoPageParam();
        applicationInfoPage.setPageNo(1);applicationInfoPage.setPageSize(1);
        applicationInfoPage.setTypeLists(Lists.newArrayList("qa", "dialogue", "search",""));
        final long zntCount = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getTotalRow(); //聊天助手
        applicationInfoPage.setTypeLists(Lists.newArrayList("workflow", "text-agent"));
        final long ptCount = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getTotalRow(); //文本生成

        WorkFlowPageParam workFlowPageParam = new WorkFlowPageParam();
        workFlowPageParam.setPageNo(1);workFlowPageParam.setPageSize(1);
        applicationInfoPage.setTypeLists(Lists.newArrayList("qa", "dialogue", "search", "", "workflow", "text-agent"));
        final long gzlCount = componentService.page(workFlowPageParam).getData().getTotalRow();//多Agent(工作流模式)

        jsonObject.put("allCount", zntCount + ptCount);
        jsonObject.put("zntCount", zntCount < 0L ? 0L : zntCount);
        jsonObject.put("ptCount", ptCount < 0L ? 0L : ptCount);
        jsonObject.put("gzlCount", gzlCount < 0L ? 0L : gzlCount);
        return Result.success(jsonObject);
    }

    @Override
    public Result knowledgeCount() {
        JSONObject jsonObject = new JSONObject();

        //知识库数量
        KnowledgeInfoPageParam knowledgeInfoPageParam =  new KnowledgeInfoPageParam();
        knowledgeInfoPageParam.setPageNo(1);knowledgeInfoPageParam.setPageSize(1);
        final long allCount = knowledgeInfoService.getKnowledgeInfoList(knowledgeInfoPageParam).getData().getTotalRow();
        knowledgeInfoPageParam.setPageSize(Integer.valueOf(allCount + "") == 0 ? 1 : Integer.valueOf(allCount + ""));
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.getKnowledgeInfoList(knowledgeInfoPageParam).getData().getRecords();
        // 使用Stream API将List<User>转换为List<String>
        List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).collect(Collectors.toList());
        if (knowledgeInfoList == null || knowledgeIdList.size() < 1) {
            knowledgeIdList = new ArrayList<>();
            knowledgeIdList.add("zhishikuid");
        }
        //知识库文档数
        FilePageParam filePageParam = new FilePageParam();
        filePageParam.setFileTypes(Arrays.asList(0));
        filePageParam.setKnowledgeIds(knowledgeIdList);
        filePageParam.setPageNo(1);filePageParam.setPageSize(1);
        //文件夹类型 2文档数据 3url 5结构化 6多媒体数据
//        filePageParam.setFileTypes(Arrays.asList(2));
        final long wjCount = fileService.getFileList(filePageParam).getData().getTotalRow();

        //知识库QA数
        KnowledgeDataPageParam knowledgeDataPageParam = new KnowledgeDataPageParam();
        knowledgeDataPageParam.setPageNo(1);knowledgeDataPageParam.setPageSize(1);
        knowledgeDataPageParam.setKnowledgeIds(knowledgeIdList);
        final long wddCount = knowledgeDataService.getKnowledgeDataList(knowledgeDataPageParam).getData().getTotal();

        //知识库url数
        ParserInfoPageParam parserInfoPageParam = new ParserInfoPageParam();
        parserInfoPageParam.setPageNo(1);parserInfoPageParam.setPageSize(1);
        parserInfoPageParam.setKnowledgeIds(knowledgeIdList);
        final long urlCount = urlParserInfoService.getList(parserInfoPageParam).getData().getTotalRow();

        //知识库结构化数
        UnionParam unionParam = new UnionParam();
        unionParam.setPageNo(1);unionParam.setPageSize(1);
        unionParam.setKnowledgeIds(knowledgeIdList);
        final long jghCount = dataSourceParserInfoService.unionData(unionParam).getData().getTotal();

        jsonObject.put("allCount", allCount);
        jsonObject.put("wjCount", wjCount);
        jsonObject.put("wddCount", wddCount);
        jsonObject.put("urlCount", urlCount);
        jsonObject.put("jghCount", jghCount);
        return Result.success(jsonObject);
    }

    @Override
    public Result getUsingInfo() {
        JSONObject jsonObject = new JSONObject();

        ApplicationInfoPageParam applicationInfoPage =  new ApplicationInfoPageParam();
        applicationInfoPage.setPageNo(1);applicationInfoPage.setPageSize(1);
        final long count = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getTotalRow();

        //总应用list
        applicationInfoPage.setPageSize(Integer.valueOf(count + "") == 0 ? 1 : Integer.valueOf(count + ""));
        final List<ApplicationInfo> applicationInfoList = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getRecords();
        List<String> appCodes = applicationInfoList.stream().map(ApplicationInfo::getApplicationCode).collect(Collectors.toList());
        List<String> appIds = applicationInfoList.stream().map(ApplicationInfo::getApplicationId).collect(Collectors.toList());
        if (appCodes == null || appCodes.size() < 1) {
            appCodes = new ArrayList<>();
            appIds = new ArrayList<>();
            appCodes.add("yingyongid");
            appIds.add("yingyongid");
        }

        //总使用量
        OperaLog operaLog = new OperaLog();
        List<String> operUrls = new ArrayList<>();
        appCodes.stream().forEach(appCode -> {
            operUrls.add("/applicationInfo/getApplicationDetail/{appCode}".replace("{appCode}", appCode));
        });
        operaLog.setOperUrls(operUrls);
        final long zsylCount = operaLogService.getOperaLogCount(operaLog);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("total", zsylCount);
        jsonObject1.put("unit", "次");
        jsonObject1.put("QOQ", "暂无");
        jsonObject.put("zsylInfo", jsonObject1);

        //总使用用户数
        // 创建一个DecimalFormat对象，并设置保留四位小数
        DecimalFormat df = new DecimalFormat("#.####");
        ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam = new ApplicationOverviewIndicatorsParam();
        applicationOverviewIndicatorsParam.setApplicationIds(appIds);
        long thisTotal2 = getUsageUserTotal(applicationOverviewIndicatorsParam);
        ApplicationOverviewIndicatorsParam thisParam2 = new ApplicationOverviewIndicatorsParam();
        BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam2);
        final String[] thisMonthStrings = DateUtil.startDayOfThisMonth(LocalDate.now());
        final String[] lastMonthStrings = DateUtil.startDayOfLastMonth(LocalDate.now());
        thisParam2.setStartTime(thisMonthStrings[0]);
        thisParam2.setEndTime(thisMonthStrings[1]);
        long thisMouthTotal2 = getUsageUserTotal(thisParam2);
        thisParam2.setStartTime(lastMonthStrings[0]);
        thisParam2.setEndTime(lastMonthStrings[1]);
        long lastMouthTotal2 = getUsageUserTotal(thisParam2);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("total", thisTotal2);
        jsonObject2.put("unit", "人");
        jsonObject2.put("QOQ", Double.valueOf(lastMouthTotal2) != 0.0d ? df.format((Double.valueOf(thisMouthTotal2) - Double.valueOf(lastMouthTotal2)) / Double.valueOf(lastMouthTotal2) * 100) + "%" : "暂无");
        jsonObject.put("zsyyhInfo", jsonObject2);
        Result result = dialogueRecordService.getCountByAppId(new RecordPageParam());
        jsonObject.put("llmInvokeInfo", result.getData());
        return Result.success(jsonObject);
    }

    @Override
    public Result getQaCount() {
        JSONObject jsonObject = new JSONObject();
        ApplicationInfoPageParam applicationInfoPage =  new ApplicationInfoPageParam();
        applicationInfoPage.setPageNo(1);applicationInfoPage.setPageSize(1);
        final long count = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getTotalRow();

        //总应用list
        applicationInfoPage.setPageSize(Integer.valueOf(count + "") == 0 ? 1 : Integer.valueOf(count + ""));
        final List<ApplicationInfo> applicationInfoList = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getRecords();
        List<String> appIds = applicationInfoList.stream().map(ApplicationInfo::getApplicationId).collect(Collectors.toList());
        if (appIds == null || appIds.size() < 1) {
            appIds = new ArrayList<>();
            appIds.add("yingyongid");
        }
        RecordPageParam recordPageParam = new RecordPageParam();
        recordPageParam.setPageNo(1);
        recordPageParam.setPageSize(1);
        recordPageParam.setApplicationIds(appIds);
        final long qaCount = dialogueRecordService.getRecord(recordPageParam).getData().getTotal();
        jsonObject.put("qaCount", qaCount);
        return Result.success(jsonObject);
    }

    @Override
    public Result questionChartsTop50(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        ApplicationInfoPageParam applicationInfoPage =  new ApplicationInfoPageParam();
        applicationInfoPage.setPageNo(1);applicationInfoPage.setPageSize(1);
        final long count = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getTotalRow();

        //总应用list
        applicationInfoPage.setPageSize(Integer.valueOf(count + "") == 0 ? 1 : Integer.valueOf(count + ""));
        final List<ApplicationInfo> applicationInfoList = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getRecords();
        List<String> appIds = applicationInfoList.stream().map(ApplicationInfo::getApplicationId).collect(Collectors.toList());
        if (appIds == null || appIds.size() < 1) {
            appIds = new ArrayList<>();
            appIds.add("yingyongid");
        }
        applicationOverviewIndicatorsParam.setApplicationIds(appIds);
        return applicationAnalysisService.questionChartsTop50(applicationOverviewIndicatorsParam);
    }

    @Override
    public Result getTokenConsumption(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        if (StringUtils.isBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            applicationOverviewIndicatorsParam.setEndTime(dateFormat.format(now));
        }else {
            applicationOverviewIndicatorsParam.setEndTime(applicationOverviewIndicatorsParam.getEndTime().split(" ")[0]);
        }
        if (StringUtils.isBlank(applicationOverviewIndicatorsParam.getStartTime())) {
            // 创建一个Calendar实例并设置为当前时间
            Calendar calendar = Calendar.getInstance();
            // 往前推10天
            calendar.add(Calendar.DAY_OF_MONTH, -10);
            // 获取修改后的时间
            Date date = calendar.getTime();
            applicationOverviewIndicatorsParam.setStartTime(dateFormat.format(date));
        }else {
            applicationOverviewIndicatorsParam.setStartTime(applicationOverviewIndicatorsParam.getStartTime().split(" ")[0]);
        }
        ApplicationInfoPageParam applicationInfoPage =  new ApplicationInfoPageParam();
        applicationInfoPage.setPageNo(1);applicationInfoPage.setPageSize(1);
        final long count = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getTotalRow();

        //总应用list
        applicationInfoPage.setPageSize(Integer.valueOf(count + "") == 0 ? 1 : Integer.valueOf(count + ""));
        final List<ApplicationInfo> applicationInfoList = applicationInfoService.getApplicationInfoList(applicationInfoPage).getData().getRecords();
        List<String> appIds = applicationInfoList.stream().map(ApplicationInfo::getApplicationId).collect(Collectors.toList());
        if (appIds == null || appIds.size() < 1) {
            appIds = new ArrayList<>();
            appIds.add("yingyongid");
        }
//        appIds.add("00759c5a4ae74622971ee51142287e2d");
        applicationOverviewIndicatorsParam.setApplicationIds(appIds);

        Map<String, Long> tokenDateMap = new HashMap<>();
        //获取分组聚合统计list
        List<JSONObject> jsonObjectList = getGroupByDateCountToken(applicationOverviewIndicatorsParam);
        jsonObjectList.stream().forEach(jsonObject -> {
            tokenDateMap.put(jsonObject.getString("date"), jsonObject.getLong("count"));
        });

        //获取两个时间之间有多少个日期 列表
        List<String> dates = DateRangeExample.getDatesBetween(applicationOverviewIndicatorsParam.getStartTime(), applicationOverviewIndicatorsParam.getEndTime());
        List<JSONObject> list = new ArrayList<>();
        for (String date : dates){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", date);
            //根据对话日志数据 查询消耗token数
            jsonObject.put("tokenTotal", tokenDateMap.get(date) == null ? 0 : tokenDateMap.get(date));
            list.add(jsonObject);
        }
        return Result.success(list);
    }

    private List<JSONObject> getGroupByDateCountToken(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        List<JSONObject> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getApplicationId())){
            boolQuery.must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()));
        }
        if(CollectionUtils.isNotEmpty(applicationOverviewIndicatorsParam.getApplicationIds())){
            boolQuery.must(QueryBuilders.termsQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationIds()));
        }
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime() + " 00:00:00").lte(applicationOverviewIndicatorsParam.getEndTime() + " 23:59:59"));
        }
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.size(0);
        // 创建日期直方图聚合
        DateHistogramAggregationBuilder dateHistogramAgg = AggregationBuilders
                .dateHistogram("by_date")
                .field("createTime")
                .calendarInterval(DateHistogramInterval.DAY) // 按天分组
                .format("yyyy-MM-dd") // 日期格式
                .timeZone(ZoneId.of("UTC")) // 时区
                .subAggregation(AggregationBuilders.sum("sum_value").field("consumeTokensTotal"));
        searchSourceBuilder.aggregation(dateHistogramAgg);
        searchRequest.source(searchSourceBuilder);


        // 执行搜索请求
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取聚合结果
        Aggregation aggregation = searchResponse.getAggregations().get("by_date");
        if (aggregation instanceof Histogram) {
            Histogram histogram = (Histogram) aggregation;
            List<? extends Histogram.Bucket> buckets = (List<? extends Histogram.Bucket>) histogram.getBuckets();
            for (Histogram.Bucket bucket : buckets) {
//                String keyAsString = bucket.getKeyAsString(); // 获取桶的键（日期）
//                System.out.println("Date: " + keyAsString);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", bucket.getKeyAsString());
                // 获取子聚合（求和）的结果
                Aggregation sumAgg = bucket.getAggregations().get("sum_value");
                if (sumAgg instanceof Sum) {
                    Sum sum = (Sum) sumAgg;
                    double sumValue = sum.getValue();
//                    System.out.println("Sum Value: " + sumValue);
                    jsonObject.put("count", sumValue);
                }
                list.add(jsonObject);
            }
        }
        return list;
    }


    /**
     * @description: 总使用用户量
     * @author: caohaifeng
     * @date: 2024/8/12 14:25
     **/
    private long getUsageUserTotal(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        return applicationUserMapper.getApplicationUserCount(applicationOverviewIndicatorsParam.getStartTime(),
                applicationOverviewIndicatorsParam.getEndTime(), applicationOverviewIndicatorsParam.getApplicationId(), applicationOverviewIndicatorsParam.getApplicationIds());
    }

}
