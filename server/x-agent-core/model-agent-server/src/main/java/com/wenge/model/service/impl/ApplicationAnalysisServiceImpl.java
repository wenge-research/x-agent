package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.wenge.model.constants.SystemVerifyContant;
import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.dto.param.ApplicationQuestionRecommendParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.enums.ApplicationAnalysisTypeEnum;
import com.wenge.model.enums.DialogueVerifyStatusEnum;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.service.ApplicationAnalysisService;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.utils.DateRangeExample;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.EasyExcelUtil;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.OperaLog;
import com.wenge.oauth.mapper.ApplicationUserMapper;
import com.wenge.oauth.mapper.OauthLoginLogMapper;
import com.wenge.oauth.mapper.UserMapper;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.service.OperaLogService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
/**
 * @BelongsProject: yayi-model-application
 * @BelongsPackage:com.wenge.model.service.impl
 * @Author:caohaifeng
 * @createTime:2024-08-12 :
 * @Description:TODO
 * @Version:1.0
 */
@Service
@Slf4j
public class ApplicationAnalysisServiceImpl implements ApplicationAnalysisService {

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
    private DialogueMapper dialogueMapper;

    @Autowired
    private OauthDeptService oauthDeptService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    final private String APPLICATIONANALYSIS_DEPTINFO_KEY_ID = "applicationAnalysisService-deptinfo-key-id_";
    final private String APPLICATIONANALYSIS_USERINFO_KEY_ID = "applicationAnalysisService-userinfo-key-id_";

    @Override
    public Result applicationStatistics(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, List<InputStream> inputStreamList, HttpServletResponse response) throws IOException, InvalidFormatException, ParseException {
        Map<String, JSONObject> jsonObjectMap = new HashMap();
        // 创建一个DecimalFormat对象，并设置保留四位小数
        DecimalFormat df = new DecimalFormat("#.##");
        // 总使用量
        ApplicationOverviewIndicatorsParam thisParam1 = new ApplicationOverviewIndicatorsParam();
        //BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam1);
        thisParam1.setApplicationId(applicationOverviewIndicatorsParam.getApplicationId());
        long thisTotal1 = getUsageTotal(thisParam1);
        final String[] thisMonthStrings1 = DateUtil.startDayOfThisMonth(LocalDate.now());
        final String[] lastMonthStrings1 = DateUtil.startDayOfLastMonth(LocalDate.now());
        thisParam1.setStartTime(thisMonthStrings1[0]);
        thisParam1.setEndTime(thisMonthStrings1[1]);
        long thisMouthTotal1 = getUsageTotal(thisParam1);
        thisParam1.setStartTime(lastMonthStrings1[0]);
        thisParam1.setEndTime(lastMonthStrings1[1]);
        long lastMouthTotal1 = getUsageTotal(thisParam1);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("total", thisTotal1);
        jsonObject1.put("unit", "次");
        jsonObject1.put("QOQ", Double.valueOf(lastMouthTotal1) != 0.0d ? df.format((Double.valueOf(thisMouthTotal1) - Double.valueOf(lastMouthTotal1)) / Double.valueOf(lastMouthTotal1) * 100) + "%" : "暂无");

        /*long thisTotal2;
        long thisMouthTotal2;
        long lastMouthTotal2;

        try {
            //根据应用id获取所有的值
            thisTotal2 = statisticalUserTotal(applicationOverviewIndicatorsParam.getApplicationId(),
                    applicationOverviewIndicatorsParam.getStartTime(),
                    applicationOverviewIndicatorsParam.getEndTime());
            final String[] thisMonthStrings = DateUtil.startDayOfThisMonth(LocalDate.now());
            final String[] lastMonthStrings = DateUtil.startDayOfLastMonth(LocalDate.now());
            thisMouthTotal2 = statisticalUserTotal(applicationOverviewIndicatorsParam.getApplicationId(),
                    thisMonthStrings[0],
                    thisMonthStrings[1]);
            lastMouthTotal2 = statisticalUserTotal(applicationOverviewIndicatorsParam.getApplicationId(),
                    lastMonthStrings[0],
                    lastMonthStrings[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }*/
        // 总使用用户量数量
        //long thisTotal2 = getUsageUserTotal(applicationOverviewIndicatorsParam);
        ApplicationOverviewIndicatorsParam thisParam2 = new ApplicationOverviewIndicatorsParam();
        //BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam2);
        thisParam2.setApplicationId(applicationOverviewIndicatorsParam.getApplicationId());
        long thisTotal2 = getUsageUserTotal(thisParam2);
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


        // 总新增使用用户数量
        ApplicationOverviewIndicatorsParam thisParam3 = new ApplicationOverviewIndicatorsParam();
        //BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam3);
        thisParam3.setApplicationId(applicationOverviewIndicatorsParam.getApplicationId());
        final String[] thisDayStrings = DateUtil.startDayOfThisDay(LocalDate.now());
        thisParam3.setStartTime(thisDayStrings[0]);
        thisParam3.setEndTime(thisDayStrings[1]);
        long thisTotal3 = getUsageAddUserTotal(thisParam3);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("total", thisTotal3);
        jsonObject3.put("unit", "人");
        jsonObject3.put("QOQ", "暂无");


        // 使用用户平均日存率 -- 今日使用应用的用户数/绑定应用的用户数
        ApplicationOverviewIndicatorsParam thisParam4 = new ApplicationOverviewIndicatorsParam();
        //BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam4);
        thisParam4.setApplicationId(applicationOverviewIndicatorsParam.getApplicationId());
        final String[] thisDayStrings4 = DateUtil.startDayOfThisDay(LocalDate.now());
        thisParam4.setStartTime(thisDayStrings4[0]);
        thisParam4.setEndTime(thisDayStrings4[1]);
        double thisTotal4 = getUsageUserDayRetentionRate(thisParam4);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("total", df.format(thisTotal4 * 100));
        jsonObject4.put("unit", "%");
        jsonObject4.put("QOQ", "暂无");

        // 访问量
        double thisTotal5 = statisticalVisits(applicationOverviewIndicatorsParam);
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("total", thisTotal5);
        jsonObject5.put("unit", "次");
        jsonObject5.put("QOQ", "暂无");
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_TOTAL.getType(), jsonObject1);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_USER_TOTAL.getType(), jsonObject2);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_ADD_USER_TOTAL.getType(), jsonObject3);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_USER_DAY_RETENTION_RATE.getType(), jsonObject4);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.VISIT_TOTAL.getType(), jsonObject5);

        //图表
        /*
         *  1.开始时间和结束时间为空的时候，默认为近七天的维度
         *  2.有开始时间没有结束时间，结束时间默认为开始时间后七天
         *  3.没有开始时间有结束时间，开始时间默认为结束时间前七天
         *  4.判断开始时间和结束时间的差值
         *     4.1 最多的列数不超过10列，开始时间和结束时间的差值取模10，然后根据开始时间进行累加模值，直到小于或者等于结束时间
         *  5.根据type统计指定时间段的数据
         *
         **/
        //1 2 3
        initDefaultDate(applicationOverviewIndicatorsParam);
        List<String[]> dateStringsList = getDateStringsListByStartTimeAndEndTime(applicationOverviewIndicatorsParam, 10L);
        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        Map<String,Long>map=new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            Long count=0L;
            applicationOverviewIndicatorsParam.setType(String.valueOf(i));
            for(String[] strings:dateStringsList){
                ApplicationOverviewIndicatorsParam param = new ApplicationOverviewIndicatorsParam();
                BeanUtil.copyProperties(applicationOverviewIndicatorsParam, param);
                param.setStartTime(strings[0]);
                param.setEndTime(strings[1]);
                //5
                JSONObject jsonObject = getSwitchTypeShowCount(param);
                count=count+Long.parseLong(jsonObject.getStr("showCountPc"));
                jsonObjectList.add(jsonObject);
            }
            map.put("count"+i, count);
            /*dateStringsList.forEach(strings -> {
                ApplicationOverviewIndicatorsParam param = new ApplicationOverviewIndicatorsParam();
                BeanUtil.copyProperties(applicationOverviewIndicatorsParam, param);
                param.setStartTime(strings[0]);
                param.setEndTime(strings[1]);
                //5
                JSONObject jsonObject = getSwitchTypeShowCount(param);
                count=count+Long.parseLong(jsonObject.getString("showCountPc"));
                jsonObjectList.add(jsonObject);
            });*/
        }
        String endTime =null;
        String startTime =null;
        //生成word
        Long day=7L;
        if(StringUtils.isEmpty(applicationOverviewIndicatorsParam.getStartTime())&&StringUtils.isEmpty(applicationOverviewIndicatorsParam.getEndTime())){
            Long currentTimeMillis=System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            endTime = sdf.format(currentTimeMillis);
            startTime = sdf.format(currentTimeMillis-604800000);
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startTime= applicationOverviewIndicatorsParam.getStartTime();
            endTime = applicationOverviewIndicatorsParam.getEndTime();
            Date startDate= sdf.parse(startTime);
            Date endDate= sdf.parse(endTime);
            long time1 = startDate.getTime();
            long time2 = endDate.getTime();
            time2 = time2 - time1;
            day=time2/(60*60*24*1000);
        }
        StringBuilder sevenTime = new StringBuilder();
        sevenTime.append(startTime).append("至").append(endTime);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("XX应用", applicationOverviewIndicatorsParam.getApplicationName());
        replacements.put("sevenTime", String.valueOf(sevenTime));
        replacements.put("Y", String.valueOf(sevenTime));
        replacements.put("D", String.valueOf(day));
        replacements.put("x1", String.valueOf(thisTotal1));
        replacements.put("y1", Double.valueOf(lastMouthTotal1) != 0.0d ? df.format((Double.valueOf(thisMouthTotal1) - Double.valueOf(lastMouthTotal1)) / Double.valueOf(lastMouthTotal1) * 100) + "%": "暂无数据");
        replacements.put("x2", String.valueOf(thisTotal2));
        replacements.put("y2", Double.valueOf(lastMouthTotal2) != 0.0d ? df.format((Double.valueOf(thisMouthTotal2) - Double.valueOf(lastMouthTotal2)) / Double.valueOf(lastMouthTotal2) * 100) + "%" : "暂无数据");
        replacements.put("x3", String.valueOf(thisTotal3));
        replacements.put("y3", "暂无数据");
        replacements.put("x4", df.format(thisTotal4 * 100));
        replacements.put("y4", "暂无数据");
        replacements.put("x5", String.valueOf(map.get("count1")));
        replacements.put("x6", String.valueOf(map.get("count2")));
        replacements.put("x7", String.valueOf(map.get("count3")));
        replacements.put("x8", String.valueOf(map.get("count4")));
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:temp/applicationStatdocx.docx");
        XWPFDocument document = new XWPFDocument(resource.getInputStream());
        replacePlaceholder(document, replacements);
        insertImage(document,inputStreamList);
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("统计.docx", String.valueOf(StandardCharsets.UTF_8)));
        OutputStream fos = response.getOutputStream();
        document.write(fos);
        fos.close();
        for (InputStream inputStream1:inputStreamList){
            inputStream1.close();
        }
        return null;
    }

    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用概览指标获取
     * @author: caohaifeng
     * @date: 2024/8/12 14:03
     */
    @Override
    public Result applicationOverviewIndicators(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        Map<String, JSONObject> jsonObjectMap = new HashMap();
        // 创建一个DecimalFormat对象，并设置保留四位小数
        DecimalFormat df = new DecimalFormat("#.##");
        // 总使用量
        long thisTotal1 = getUsageTotal(applicationOverviewIndicatorsParam);
        ApplicationOverviewIndicatorsParam thisParam1 = new ApplicationOverviewIndicatorsParam();
        BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam1);
        final String[] thisMonthStrings1 = DateUtil.startDayOfThisMonth(LocalDate.now());
        final String[] lastMonthStrings1 = DateUtil.startDayOfLastMonth(LocalDate.now());
        thisParam1.setStartTime(thisMonthStrings1[0]);
        thisParam1.setEndTime(thisMonthStrings1[1]);
        long thisMouthTotal1 = getUsageTotal(thisParam1);
        thisParam1.setStartTime(lastMonthStrings1[0]);
        thisParam1.setEndTime(lastMonthStrings1[1]);
        long lastMouthTotal1 = getUsageTotal(thisParam1);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("total", thisTotal1);
        jsonObject1.put("unit", "次");
        jsonObject1.put("QOQ", Double.valueOf(lastMouthTotal1) != 0.0d ? df.format((Double.valueOf(thisMouthTotal1) - Double.valueOf(lastMouthTotal1)) / Double.valueOf(lastMouthTotal1) * 100) + "%" : "暂无");

        // 总使用用户量数量
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

        // 总新增使用用户数量
        ApplicationOverviewIndicatorsParam thisParam3 = new ApplicationOverviewIndicatorsParam();
        BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam3);
        final String[] thisDayStrings = DateUtil.startDayOfThisDay(LocalDate.now());
        thisParam3.setStartTime(thisDayStrings[0]);
        thisParam3.setEndTime(thisDayStrings[1]);
        long thisTotal3 = getUsageAddUserTotal(thisParam3);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("total", thisTotal3);
        jsonObject3.put("unit", "人");
        jsonObject3.put("QOQ", "暂无");


        // 使用用户平均日存率 -- 今日使用应用的用户数/绑定应用的用户数
        ApplicationOverviewIndicatorsParam thisParam4 = new ApplicationOverviewIndicatorsParam();
        BeanUtil.copyProperties(applicationOverviewIndicatorsParam, thisParam4);
        final String[] thisDayStrings4 = DateUtil.startDayOfThisDay(LocalDate.now());
        thisParam4.setStartTime(thisDayStrings4[0]);
        thisParam4.setEndTime(thisDayStrings4[1]);
        double thisTotal4 = getUsageUserDayRetentionRate(thisParam4);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("total", df.format(thisTotal4 * 100));
        jsonObject4.put("unit", "%");
        jsonObject4.put("QOQ", "暂无");

        // 访问量
        double thisTotal5 = statisticalVisits(applicationOverviewIndicatorsParam);
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("total", thisTotal5);
        jsonObject5.put("unit", "次");
        jsonObject5.put("QOQ", "暂无");

        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_TOTAL.getType(), jsonObject1);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_USER_TOTAL.getType(), jsonObject2);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_ADD_USER_TOTAL.getType(), jsonObject3);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.USAGE_USER_DAY_RETENTION_RATE.getType(), jsonObject4);
        jsonObjectMap.put(ApplicationAnalysisTypeEnum.VISIT_TOTAL.getType(), jsonObject5);
        return Result.success(jsonObjectMap);
    }

    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用概览指标-某一类详细指标获取-问答质量
     * @author: caohaifeng
     * @date: 2025/1/6 15:25
     */
    @Override
    public Result getApplicationOverviewQualityByType(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        List<JSONObject> results = new ArrayList<>();
        //获取所有的最低级部门列表
        oauthDeptService.getAllDept().stream().filter(item -> !item.getPid().equals("0")).forEach(item -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dept_id", item.getDeptId());
            jsonObject.put("dept_name", item.getDeptName());
            jsonObject.put("dept_dept", item.getDeptCode());
            jsonObject.put("count", -1L);
            results.add(jsonObject);
        });
        if ("3".equals(applicationOverviewIndicatorsParam.getType())) { //知识库问答添加数排名
            Wrappers<Object> wrapper = Wrappers.init()
                    .leftJoin(APPLICATION_KNOWLEDGE, StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getApplicationId())).on(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
                    .where(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getApplicationId()), APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationOverviewIndicatorsParam.getApplicationId()))
                    .orderBy(KNOWLEDGE_INFO.CREATE_TIME.desc());
            List<KnowledgeInfo> records = knowledgeInfoService.list(wrapper);
            List<String> knowledgeInfoIds = records.stream().map(KnowledgeInfo::getKnowledgeId).collect(Collectors.toList());
            if (knowledgeInfoIds == null || knowledgeInfoIds.size() < 1) {
                knowledgeInfoIds = new ArrayList<>();
                knowledgeInfoIds.add("zhishiku");
            }
            applicationOverviewIndicatorsParam.setKnowledgeIds(knowledgeInfoIds);
            final Map<String,Long> countGroupType = getCountKnowledgeGroupType(applicationOverviewIndicatorsParam);
            results.stream().forEach(item -> {
                item.put("count", countGroupType.getOrDefault(item.get("dept_id"), 0L));
            });
        }else if("1".equals(applicationOverviewIndicatorsParam.getType()) || "2".equals(applicationOverviewIndicatorsParam.getType()) || "4".equals(applicationOverviewIndicatorsParam.getType())){
            final Map<String,Long> countGroupType = getCountGroupType(applicationOverviewIndicatorsParam);
            results.stream().forEach(item -> {
                item.put("count", countGroupType.getOrDefault(item.get("dept_id"), 0L));
            });
        }
        results.sort((o2, o1) -> Long.compare(o1.getLong("count"), o2.getLong("count")));
        return Result.success(results);
    }

    @Override
    public void getApplicationOverviewQualityByTypeExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
        final Result result = this.getApplicationOverviewQualityByType(applicationOverviewIndicatorsParam);
        if (!result.getCode().equals("000000")) {
            log.error("导出异常，请检查！！！");
            return;
        }
        Object object = result.getData();
        List<JSONObject> list = new ArrayList<>();
        if (object != null) {
            list = (List<JSONObject>) object;
        }
        String fileName = "";
        if (applicationOverviewIndicatorsParam.getType().equals("1")) {
            fileName = "点赞数排名_";
        }else if(applicationOverviewIndicatorsParam.getType().equals("2")){
            fileName = "点踩数排名_";
        }else if(applicationOverviewIndicatorsParam.getType().equals("3")){
            fileName = "知识库问答添加数排名_";
        }else if(applicationOverviewIndicatorsParam.getType().equals("4")){
            fileName = "问答审核排名_";
        }
        if(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())){
            fileName += "_";
            exportData2(list, fileName, Arrays.asList("用户", "数量"), response);
        }
        exportData2(list, fileName, Arrays.asList("部门", "数量"), response);
    }

    /**
     * @description: 统计访问量
     * @param param
     * @return
     */
    private Long statisticalVisits(ApplicationOverviewIndicatorsParam param) {
        ApplicationInfo byAppId = applicationInfoService.getByAppId(param.getApplicationId());
        if (null == byAppId) {
            return (long) NumberConstants.ZERO;
        }
        OperaLog operaLog = new OperaLog();

        operaLog.setOperUrl("/applicationInfo/getApplicationDetail/{appCode}".replace("{appCode}", byAppId.getApplicationCode()));
        return operaLogService.getOperaLogCount(operaLog);
    }


    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用概览指标-某一类详细指标获取
     * @author: caohaifeng
     * @date: 2024/8/12 14:03
     */
    @Override
    public Result getApplicationOverviewIndicatorsByType(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        /*
         *  1.开始时间和结束时间为空的时候，默认为近七天的维度
         *  2.有开始时间没有结束时间，结束时间默认为开始时间后七天
         *  3.没有开始时间有结束时间，开始时间默认为结束时间前七天
         *  4.判断开始时间和结束时间的差值
         *     4.1 最多的列数不超过10列，开始时间和结束时间的差值取模10，然后根据开始时间进行累加模值，直到小于或者等于结束时间
         *  5.根据type统计指定时间段的数据
         *
         **/
        //1 2 3
        initDefaultDate(applicationOverviewIndicatorsParam);
        //4
        List<String[]> dateStringsList = getDateStringsListByStartTimeAndEndTime(applicationOverviewIndicatorsParam, 10L);
//        System.out.println(dateStringsList.size());
        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        dateStringsList.forEach(strings -> {
            ApplicationOverviewIndicatorsParam param = new ApplicationOverviewIndicatorsParam();
            BeanUtil.copyProperties(applicationOverviewIndicatorsParam, param);
            param.setStartTime(strings[0]);
            param.setEndTime(strings[1]);
            //5
            JSONObject jsonObject = getSwitchTypeShowCount(param);
            jsonObjectList.add(jsonObject);
        });
        return Result.success(jsonObjectList);
    }

    /**
     * @description: 处理默认时间
     * @author: caohaifeng
     * @date: 2024/8/13 10:41
     **/
    private void initDefaultDate(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        // 创建一个只包含年、月、日的 DateTimeFormatter
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (StringUtils.isBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            final String[] strings = DateUtil.startDayOfThisDay(LocalDate.now());
            applicationOverviewIndicatorsParam.setEndTime(strings[1]); //结束时间
            String endDay = strings[1].substring(0, 10); // 截取前10个字符，即 yyyy-MM-dd
            // 使用 DateTimeFormatter 解析日期字符串
            LocalDate date = LocalDate.parse(endDay, dateFormatter);
            final String[] stringsStart = DateUtil.startDayOfAddDaySize(date, -6L);
            applicationOverviewIndicatorsParam.setStartTime(stringsStart[0]);
        }else if(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isBlank(applicationOverviewIndicatorsParam.getEndTime())){
            String dateOnlyStr = applicationOverviewIndicatorsParam.getStartTime().substring(0, 10); // 截取前10个字符，即 yyyy-MM-dd
            // 使用 DateTimeFormatter 解析日期字符串
            LocalDate date = LocalDate.parse(dateOnlyStr, dateFormatter);
            final String[] strings = DateUtil.startDayOfAddDaySize(date, 6L);
            applicationOverviewIndicatorsParam.setEndTime(strings[1]);
        }else if(StringUtils.isBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())){
            String dateOnlyStr = applicationOverviewIndicatorsParam.getEndTime().substring(0, 10); // 截取前10个字符，即 yyyy-MM-dd
            // 使用 DateTimeFormatter 解析日期字符串
            LocalDate date = LocalDate.parse(dateOnlyStr, dateFormatter);
            final String[] strings = DateUtil.startDayOfAddDaySize(date, -6L);
            applicationOverviewIndicatorsParam.setStartTime(strings[0]);
        }
    }

    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 问题排行榜Top50/topN
     * @author: caohaifeng
     * @date: 2024/8/12 18:15
     */
    @Override
    public Result questionChartsTop50(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        Integer count = applicationOverviewIndicatorsParam.getCount();
        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        if ("count".equals(applicationOverviewIndicatorsParam.getTop50Type())) {
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
                boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
            }
            if(StringUtils.isNotEmpty(applicationOverviewIndicatorsParam.getQuestion())){
                boolQuery.must(QueryBuilders.queryStringQuery(applicationOverviewIndicatorsParam.getQuestion()).field("question"));
            }

            if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getSourceType())) {
                if(applicationOverviewIndicatorsParam.getSourceType().equals("APP")){
                    BoolQueryBuilder boolQueryshould = QueryBuilders.boolQuery();
                    boolQueryshould.should(QueryBuilders.termQuery("clientType", "APP"));
                    boolQueryshould.should(QueryBuilders.boolQuery()
                            .mustNot(QueryBuilders.existsQuery("clientType")));
                    boolQuery.must(boolQueryshould);
                }else {
                    boolQuery.must(QueryBuilders.termQuery("clientType", applicationOverviewIndicatorsParam.getSourceType()));
                }
            }

            // 创建聚合查询
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_field").size(count)
                    .field("question.keyword") // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                    .order(BucketOrder.count(false)); // 按计数降序排列

            searchSourceBuilder.aggregation(aggregation);
            searchSourceBuilder.size(0).query(boolQuery);
            searchSourceBuilder.trackTotalHits(true);
            log.info("getUsageTotal ---- DSL：{}", searchSourceBuilder.toString());
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                // 解析聚合结果
                Terms terms = searchResponse.getAggregations().get("group_by_field");
                terms.getBuckets().forEach(bucket -> {
//                System.out.println(bucket.getKeyAsString() + ": " + bucket.getDocCount());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("question", bucket.getKeyAsString());
                    jsonObject.put("count", bucket.getDocCount());
                    jsonObjectList.add(jsonObject);
                });
            } catch (IOException e) {
                e.printStackTrace();
                return Result.fail("查询失败");
            }
        } else if ("time".equals(applicationOverviewIndicatorsParam.getTop50Type())) {
            SearchRequest searchRequest = new SearchRequest(dialogueIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if(CollectionUtils.isNotEmpty(applicationOverviewIndicatorsParam.getApplicationIds())){
                boolQuery.must(QueryBuilders.termsQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationIds()));
            }
            if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
                boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
            }
            if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
                boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
            }
            if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getSourceType())) {
                if(applicationOverviewIndicatorsParam.getSourceType().equals("APP")){
                    BoolQueryBuilder boolQueryshould = QueryBuilders.boolQuery();
                    boolQueryshould.should(QueryBuilders.termQuery("clientType", "APP"));
                    boolQueryshould.should(QueryBuilders.boolQuery()
                            .mustNot(QueryBuilders.existsQuery("clientType")));
                    boolQuery.must(boolQueryshould);
                }else {
                    boolQuery.must(QueryBuilders.termQuery("clientType", applicationOverviewIndicatorsParam.getSourceType()));
                }
            }
            if(StringUtils.isNotEmpty(applicationOverviewIndicatorsParam.getQuestion())){
                boolQuery.must(QueryBuilders.queryStringQuery(applicationOverviewIndicatorsParam.getQuestion()).field("question"));            }
            searchSourceBuilder.size(count).query(boolQuery);
            searchSourceBuilder.sort("createTime", SortOrder.DESC);
            searchSourceBuilder.trackTotalHits(true);
            log.info("getUsageTotal ---- DSL：{}", searchSourceBuilder.toString());
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    String sourceAsString = hit.getSourceAsString(); // 获取文档的原始JSON字符串
                    final JSONObject jsonObject = JSONUtil.parseObj(sourceAsString);
                    jsonObjectList.add(jsonObject);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Result.fail("查询失败");
            }
        }
        return Result.success(jsonObjectList);
    }

    @Override
    public Result questionRealtimeRecommendTopN(ApplicationQuestionRecommendParam param) {
        String question = param.getQuestion();
        if (StringUtils.isEmpty(question)) {
            return Result.success(new CopyOnWriteArrayList<>());
        }

        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();

        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("applicationId", param.getApplicationId()))
                .must(QueryBuilders.matchPhrasePrefixQuery("question", question));

        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_field")
                .size(param.getCount())
                .field("question.keyword")
                .order(BucketOrder.count(false));

        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            terms.getBuckets().forEach(bucket -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("question", bucket.getKeyAsString());
                jsonObject.put("count", bucket.getDocCount());
                jsonObjectList.add(jsonObject);
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }

        return Result.success(jsonObjectList);
    }

    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 分析应用回复的成功率、失败率、点赞率
     * @author: caohaifeng
     * @date: 2024/8/27 16:21
     * @param:
     * @return:
     */
    @Override
    public Result getApplicationUsageRate(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        DecimalFormat df = new DecimalFormat("#.##");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unit", "%");
        jsonObject.put("successRate", "0");
        jsonObject.put("failRate", "0");
        jsonObject.put("goodRate", "0");
        jsonObject.put("stepRate", "0");
        initDefaultDate(applicationOverviewIndicatorsParam);
        //总数
        long total = getSelectCount(applicationOverviewIndicatorsParam, null, null);
        //回答成功数
        long totalSuccess = getSelectCount(applicationOverviewIndicatorsParam, null, "no-NULL");
        //点赞
        long totalGood = getSelectCount(applicationOverviewIndicatorsParam, "1", null);
        //点踩
        long totalStep = getSelectCount(applicationOverviewIndicatorsParam, "0", null);

        jsonObject.put("successRate", Double.valueOf(total) != 0.0d ? df.format(Double.valueOf(totalSuccess) / Double.valueOf(total) * 100) + "" : "0.0");
        jsonObject.put("failRate", Double.valueOf(total) != 0.0d ? df.format(Double.valueOf(total - totalSuccess) / Double.valueOf(total) * 100) + "" : "0.0");
        jsonObject.put("goodRate", Double.valueOf(total) != 0.0d ? df.format(Double.valueOf(totalGood) / Double.valueOf(total) * 100) + "" : "0.0");
        jsonObject.put("stepRate", Double.valueOf(total) != 0.0d ? df.format(Double.valueOf(totalStep) / Double.valueOf(total) * 100) + "" : "0.0");
        return Result.success(jsonObject);
    }



    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用分析-查阅点赞点踩数排名（部门维度）
     * @author: caohaifeng
     * @date: 2024/9/18 14:04
     */
    @Override
    public Result getApplicationLikeStepRanking(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {

        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        boolQuery.must(QueryBuilders.termQuery("feedbackType", applicationOverviewIndicatorsParam.getFeedbackType()));
        boolQuery.must(QueryBuilders.existsQuery("feedbackUserId")); //这里必须存在用户id的才能列入统计
        boolQuery.must(QueryBuilders.existsQuery("feedbackDeptId")); //这里必须存在部门id的才能列入统计

        String orderField = "feedbackDeptId.keyword";
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())) {
            boolQuery.must(QueryBuilders.termQuery(orderField, applicationOverviewIndicatorsParam.getDeptId()));
            orderField = "feedbackUserId";
        }

            // 创建聚合查询
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_field").size(10000)
                .field(orderField) // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                .order(BucketOrder.count(false)); // 按计数降序排列

        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getApplicationLikeStepRanking ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            String finalOrderField = orderField;
            terms.getBuckets().forEach(bucket -> {
                System.out.println(bucket.getKeyAsString() + ": " + bucket.getDocCount());
                JSONObject jsonObject = new JSONObject();
                String keyId = bucket.getKeyAsString();
                jsonObject.put("keyId", keyId);
                if(finalOrderField.contains("feedbackDeptId")){
                    if(keyId.equals("-1")){
                        jsonObject.put("showName", "其他");
                    }else {
                        OauthDept deptInfo = null;
                        try {
                            deptInfo = redisService.get(APPLICATIONANALYSIS_DEPTINFO_KEY_ID + keyId, OauthDept.class);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(deptInfo == null){
                            deptInfo = oauthDeptService.getDeptDetail(keyId);
                            redisService.setExpire(APPLICATIONANALYSIS_DEPTINFO_KEY_ID + keyId, deptInfo, 60 * 10 * 1, TimeUnit.SECONDS);
                        }
                        jsonObject.put("showName", deptInfo != null ? deptInfo.getDeptName() : "未知");
                    }
                }
                if(finalOrderField.contains("feedbackUserId")){
                    OauthUser oauthUser = null;
                    try {
                        oauthUser = redisService.get(APPLICATIONANALYSIS_USERINFO_KEY_ID + keyId, OauthUser.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(oauthUser == null){
                        oauthUser = userMapper.selectOneById(keyId);
                        redisService.setExpire(APPLICATIONANALYSIS_USERINFO_KEY_ID + keyId, oauthUser, 60 * 10 * 1, TimeUnit.SECONDS);
                    }
                    jsonObject.put("showName", oauthUser != null ? oauthUser.getUsername() : "未知");
                }
                jsonObject.put("count", bucket.getDocCount());
                jsonObjectList.add(jsonObject);
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
        return Result.success(jsonObjectList);
    }

    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用分析-查阅对话日志审核数排名（部门维度）
     * @author: caohaifeng
     * @date: 2024/9/18 14:04
     */
    @Override
    public Result getApplicationDialogueReviewRanking(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        boolQuery.must(QueryBuilders.existsQuery("verifyUserId")); //这里必须存在用户id的才能列入统计
        boolQuery.must(QueryBuilders.existsQuery("verifyDeptId")); //这里必须存在部门id的才能列入统计
        boolQuery.mustNot(QueryBuilders.termQuery("verifyUserId", SystemVerifyContant.SYSTEM_VERIFY_USER_ID)); //系统审核的除外
        boolQuery.mustNot(QueryBuilders.termQuery("verifyStatus", DialogueVerifyStatusEnum.WAIT_VERIFY.getCode()));

        String orderField = "verifyDeptId";
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())) {
            boolQuery.must(QueryBuilders.termQuery("verifyDeptId", applicationOverviewIndicatorsParam.getDeptId()));
            orderField = "verifyUserId";
        }

        // 创建聚合查询
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_field").size(10000)
                .field(orderField) // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                .order(BucketOrder.count(false)); // 按计数降序排列

        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getApplicationDialogueReviewRanking ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            String finalOrderField = orderField;
            terms.getBuckets().forEach(bucket -> {
                System.out.println(bucket.getKeyAsString() + ": " + bucket.getDocCount());
                JSONObject jsonObject = new JSONObject();
                String keyId = bucket.getKeyAsString();
                jsonObject.put("keyId", keyId);
                if(finalOrderField.contains("verifyDeptId")){
                    if(keyId.equals("-1")){
                        jsonObject.put("showName", "其他");
                    }else {
                        OauthDept deptInfo = null;
                        try {
                            deptInfo = redisService.get(APPLICATIONANALYSIS_DEPTINFO_KEY_ID + keyId, OauthDept.class);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(deptInfo == null){
                            deptInfo = oauthDeptService.getDeptDetail(keyId);
                            redisService.setExpire(APPLICATIONANALYSIS_DEPTINFO_KEY_ID + keyId, deptInfo, 60 * 10 * 1, TimeUnit.SECONDS);
                        }
                        jsonObject.put("showName", deptInfo != null ? deptInfo.getDeptName() : "未知");
                    }
                }
                if(finalOrderField.contains("verifyUserId")){
                    OauthUser oauthUser = null;
                    try {
                        oauthUser = redisService.get(APPLICATIONANALYSIS_USERINFO_KEY_ID + keyId, OauthUser.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(oauthUser == null){
                        oauthUser = userMapper.selectOneById(keyId);
                        redisService.setExpire(APPLICATIONANALYSIS_USERINFO_KEY_ID + keyId, oauthUser, 60 * 10 * 1, TimeUnit.SECONDS);
                    }
                    jsonObject.put("showName", oauthUser != null ? oauthUser.getUsername() : "未知");
                }
                jsonObject.put("count", bucket.getDocCount());
                jsonObjectList.add(jsonObject);
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
        return Result.success(jsonObjectList);
    }

    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用分析-查阅知识库问答QA对添加数排名（部门维度）
     * @author: caohaifeng
     * @date: 2024/9/18 14:04
     */
    @Override
    public Result getApplicationAddQARanking(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {

        List<String> knowledgeIds = new ArrayList<>();
        //应用id查询所有绑定的知识库ids
        final List<KnowledgeInfo>  knowledgeInfos = knowledgeInfoService.effectiveKnowledge(applicationOverviewIndicatorsParam.getApplicationId());
        if (CollectionUtils.isNotEmpty(knowledgeInfos)) {
            for (KnowledgeInfo knowledgeInfo : knowledgeInfos) {
                knowledgeIds.add(knowledgeInfo.getKnowledgeId());
            }
        }
        if(knowledgeIds == null || knowledgeIds.size() == 0){
            return Result.fail("该应用没有绑定知识库");
        }
        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        SearchRequest searchRequest = new SearchRequest(knowledgeDataIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termsQuery("knowledgeId", knowledgeIds));
        boolQuery.must(QueryBuilders.termQuery("delStatus", "0"));
        List<String> dataSourceList = new ArrayList<>();
        dataSourceList.add("1");dataSourceList.add("2");dataSourceList.add("3");dataSourceList.add("5");
        boolQuery.must(QueryBuilders.termsQuery("dataSource", dataSourceList));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("updateTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        boolQuery.must(QueryBuilders.existsQuery("userId")); //这里必须存在用户id的才能列入统计
        boolQuery.must(QueryBuilders.existsQuery("deptId")); //这里必须存在部门id的才能列入统计

        String orderField = "deptId.keyword";
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())) {
            boolQuery.must(QueryBuilders.termQuery(orderField, applicationOverviewIndicatorsParam.getDeptId()));
            orderField = "userId.keyword";
        }

        // 创建聚合查询
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_field").size(10000)
                .field(orderField) // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                .order(BucketOrder.count(false)); // 按计数降序排列

        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getApplicationAddQARanking ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            String finalOrderField = orderField;
            terms.getBuckets().forEach(bucket -> {
                System.out.println(bucket.getKeyAsString() + ": " + bucket.getDocCount());
                JSONObject jsonObject = new JSONObject();
                String keyId = bucket.getKeyAsString();
                jsonObject.put("keyId", keyId);
                if(finalOrderField.contains("deptId")){
                    if(keyId.equals("-1")){
                        jsonObject.put("showName", "其他");
                    }else {
                        OauthDept deptInfo = null;
                        try {
                            deptInfo = redisService.get(APPLICATIONANALYSIS_DEPTINFO_KEY_ID + keyId, OauthDept.class);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(deptInfo == null){
                            deptInfo = oauthDeptService.getDeptDetail(keyId);
                            redisService.setExpire(APPLICATIONANALYSIS_DEPTINFO_KEY_ID + keyId, deptInfo, 60 * 10 * 1, TimeUnit.SECONDS);
                        }
                        jsonObject.put("showName", deptInfo != null ? deptInfo.getDeptName() : "未知");
                    }
                }
                if(finalOrderField.contains("userId")){
                    OauthUser oauthUser = null;
                    try {
                        oauthUser = redisService.get(APPLICATIONANALYSIS_USERINFO_KEY_ID + keyId, OauthUser.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(oauthUser == null){
                        oauthUser = userMapper.selectOneById(keyId);
                        redisService.setExpire(APPLICATIONANALYSIS_USERINFO_KEY_ID + keyId, oauthUser, 60 * 10 * 1, TimeUnit.SECONDS);
                    }
                    jsonObject.put("showName", oauthUser != null ? oauthUser.getUsername() : "未知");
                }
                jsonObject.put("count", bucket.getDocCount());
                jsonObjectList.add(jsonObject);
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
        return Result.success(jsonObjectList);
    }

    /**
     * 三个导出接口
     *
     * @param applicationOverviewIndicatorsParam
     * @param response
     */
    @Override
    public void getApplicationLikeStepRankingExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
        final Result result = this.getApplicationLikeStepRanking(applicationOverviewIndicatorsParam);
        if (!result.getCode().equals("000000")) {
            log.error("导出异常，请检查！！！");
            return;
        }
        Object object = result.getData();
        List<JSONObject> list = new ArrayList<>();
        if (object != null) {
            list = (List<JSONObject>) object;
        }
        String fileName = "点赞数排名_";
        if (applicationOverviewIndicatorsParam.getFeedbackType().equals("0")) {
            fileName = "点踩数排名_";
        }
        if(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())){
            fileName += "_";
            exportData(list, fileName, Arrays.asList("用户", "数量"), response);
        }
        exportData(list, fileName, Arrays.asList("部门", "数量"), response);
    }



    @Override
    public void getApplicationDialogueReviewRankingExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
        final Result result = this.getApplicationDialogueReviewRanking(applicationOverviewIndicatorsParam);
        if (!result.getCode().equals("000000")) {
            log.error("导出异常，请检查！！！");
            return;
        }
        Object object = result.getData();
        List<JSONObject> list = new ArrayList<>();
        if (object != null) {
            list = (List<JSONObject>) object;
        }
        String fileName = "问答审核数排名_";
        if(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())){
            fileName += "_";
            exportData(list, fileName, Arrays.asList("用户", "数量"), response);
        }
        exportData(list, fileName, Arrays.asList("部门", "数量"), response);
    }

    @Override
    public void getApplicationAddQARankingExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response) {
        final Result result = this.getApplicationAddQARanking(applicationOverviewIndicatorsParam);
        if (!result.getCode().equals("000000")) {
            log.error("导出异常，请检查！！！");
            return;
        }
        Object object = result.getData();
        List<JSONObject> list = new ArrayList<>();
        if (object != null) {
            list = (List<JSONObject>) object;
        }
        String fileName = "知识库问答添加数排行_";
        if(StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getDeptId())){
            fileName += "_";
            exportData(list, fileName, Arrays.asList("用户", "数量"), response);
        }
        exportData(list, fileName, Arrays.asList("部门", "数量"), response);
    }

    private void exportData(List<JSONObject> list, String fileName,List<String> titles, HttpServletResponse response) {
        java.io.File file = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");
        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(dateFormatter);
        try {
            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();
            List<List<String>> rows = Lists.newArrayList();
            rows.add(titles);
            list.forEach(data -> {
                List<String> stringList = new ArrayList<>();
                stringList.add(data.get("showName") + "");
                stringList.add(data.get("count") + "");
                rows.add(stringList);
            });
            EasyExcelUtil.export(rows, outputStream);
            file = new File(fileName + formattedDateTime + ".xls");
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        } finally {
            if (null != file) {
                FileUtil.del(file);
            }
        }
    }

    private void exportData2(List<JSONObject> list, String fileName,List<String> titles, HttpServletResponse response) {
        java.io.File file = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");
        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(dateFormatter);
        try {
            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();
            List<List<String>> rows = Lists.newArrayList();
            rows.add(titles);
            list.forEach(data -> {
                List<String> stringList = new ArrayList<>();
                stringList.add(data.get("dept_name") + "");
                stringList.add(data.get("count") + "");
                rows.add(stringList);
            });
            EasyExcelUtil.export(rows, outputStream);
            file = new File(fileName + formattedDateTime + ".xls");
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        } finally {
            if (null != file) {
                FileUtil.del(file);
            }
        }
    }
    /**
     * @param applicationOverviewIndicatorsParam
     * @description: 应用分析-查阅共计点踩数、推送公开知识库数、推送私有知识库数
     * 查阅共计审核数（系统审核除外）、其中修正后的审核且推送公开知识库数、推送私有知识库数
     * @author: caohaifeng
     * @date: 2024/9/18 15:06
     */
    @Override
    public Result getApplicationLikesAndReviewsCount(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        JSONObject resJsonObject = new JSONObject();
        //赞踩数
        resJsonObject.put("stepTotal", 0);
        resJsonObject.put("stepToPublicTotal", 0);
        resJsonObject.put("stepToPrivateTotal", 0);

        //审核数（审核通过且非系统审核）
        resJsonObject.put("auditTotal", 0);
        resJsonObject.put("auditToPublicTotal", 0);
        resJsonObject.put("auditToPrivateTotal", 0);






        return Result.success(resJsonObject);
    }

    @Override
    public Result getApplicationOverviewIndicatorsByTypeSuccess(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        //1 2 3
        initDefaultDate(applicationOverviewIndicatorsParam);
        // 查询成功率和失败率
        List<Dialogue> dialogueList = getDialogueList(applicationOverviewIndicatorsParam, null, null);
        // 根据 answerStatus 分成两个集合
        List<Dialogue> answeredList = dialogueList.stream()
                .filter(dialogue -> "0".equals(dialogue.getAnswerFlag()))
                .collect(Collectors.toList());
        Map<String, Long> answeredMap = answeredList.stream()
                .collect(Collectors.groupingBy(
                        dialogue -> dialogue.getCreateTime().substring(0, 10), // 将字符串解析为 LocalDate
                        Collectors.counting()
                ));
        Map<String, Long> allAnsweredMap = dialogueList.stream()
                .collect(Collectors.groupingBy(
                        dialogue -> dialogue.getCreateTime().substring(0, 10), // 将字符串解析为 LocalDate
                        Collectors.counting()
                ));
        //获取两个时间之间有多少个日期 列表
        List<String> dates = DateRangeExample.getDatesBetween(applicationOverviewIndicatorsParam.getStartTime(),
                applicationOverviewIndicatorsParam.getEndTime());
        List<JSONObject> resultCount = new ArrayList<>();
        for (String date : dates) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", date);

            long answeredCount = answeredMap.getOrDefault(date, 0L);
            long allAnsweredCount = allAnsweredMap.getOrDefault(date, 0L);
            // 转换为 BigDecimal
            BigDecimal answered = BigDecimal.valueOf(answeredCount);
            BigDecimal allAnswered = BigDecimal.valueOf(allAnsweredCount);
            BigDecimal successRate = new BigDecimal(0);
            BigDecimal failRate = new BigDecimal(0);
            if (allAnsweredCount != 0) {
                successRate = answered.divide(allAnswered, 4, RoundingMode.HALF_UP)
                        .setScale(4, RoundingMode.HALF_UP);
                failRate = BigDecimal.ONE.subtract(successRate)
                        .setScale(4, RoundingMode.HALF_UP);
            }
            jsonObject.put("successRate", successRate);
            jsonObject.put("failRate", failRate);
            resultCount.add(jsonObject);
        }
        return Result.success(resultCount);
    }

    @Override
    public Result getApplicationOverviewIndicatorsBySelect(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        initDefaultDate(applicationOverviewIndicatorsParam);
        // 查询点赞率和点踩率
        List<Dialogue> dialogueList = getDialogueList(applicationOverviewIndicatorsParam, null, null);
        // 根据 feedbackType 反馈类型：1 赞；0 踩
        List<Dialogue> likeList = dialogueList.stream()
                .filter(dialogue -> "1".equals(dialogue.getFeedbackType()))
                .collect(Collectors.toList());
        Map<String, Long> likeMap = likeList.stream()
                .collect(Collectors.groupingBy(
                        dialogue -> dialogue.getCreateTime().substring(0, 10), // 将字符串解析为 LocalDate
                        Collectors.counting()
                ));
        List<Dialogue> stepList = dialogueList.stream()
                .filter(dialogue -> "1".equals(dialogue.getFeedbackType()))
                .collect(Collectors.toList());
        Map<String, Long> stepdMap = stepList.stream()
                .collect(Collectors.groupingBy(
                        dialogue -> dialogue.getCreateTime().substring(0, 10), // 将字符串解析为 LocalDate
                        Collectors.counting()
                ));
        //获取两个时间之间有多少个日期 列表
        List<String> dates = DateRangeExample.getDatesBetween(applicationOverviewIndicatorsParam.getStartTime(),
                applicationOverviewIndicatorsParam.getEndTime());
        List<JSONObject> resultCount = new ArrayList<>();
        for (String date : dates) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", date);
            long likeCount = likeMap.getOrDefault(date, 0L);
            long stepCount = stepdMap.getOrDefault(date, 0L);
            // 转换为 BigDecimal
            BigDecimal liked = BigDecimal.valueOf(likeCount);
            BigDecimal steped = BigDecimal.valueOf(stepCount);
            BigDecimal allCount = liked.add(steped);
            BigDecimal likeRate = new BigDecimal(0);
            BigDecimal stepRate = new BigDecimal(0);
            if (!allCount.equals(BigDecimal.ZERO)) {
                likeRate = liked.divide(allCount, 4, RoundingMode.HALF_UP)
                        .setScale(4, RoundingMode.HALF_UP);
                stepRate = stepRate.divide(allCount, 4, RoundingMode.HALF_UP)
                        .setScale(4, RoundingMode.HALF_UP);
            }
            jsonObject.put("likeRate", likeRate);
            jsonObject.put("stepRate", stepRate);
            resultCount.add(jsonObject);
        }
        return Result.success(resultCount);
    }

    private List<Dialogue> getDialogueList(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam,
                                           String feedbackType, String answerStatus) {
        try {
            //根据应用id获取所有的值
            LambdaEsQueryWrapper<Dialogue> wrapper = new LambdaEsQueryWrapper<>();
            wrapper.size(9999);
            wrapper.eq(Dialogue::getApplicationId, applicationOverviewIndicatorsParam.getApplicationId());
            if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
                wrapper.between(Dialogue::getCreateTime, applicationOverviewIndicatorsParam.getStartTime(), applicationOverviewIndicatorsParam.getEndTime());
            }
            if (StringUtils.isNotBlank(feedbackType)) {
                wrapper.eq(Dialogue::getFeedbackType, feedbackType);
            }
            if (StringUtils.isNotBlank(answerStatus)) {
                if ("NULL".equals(answerStatus)) {
                    wrapper.not().exists(Dialogue::getAnswer);
                } else {
                    wrapper.exists(Dialogue::getAnswer);
                }
            }
           return dialogueMapper.selectList(wrapper);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("selectList 查询异常，请检查");
        }
        return Lists.newArrayList();
    }


    /**
     * @description: 获取问题总数量-根据条件
     * @author: caohaifeng
     * @date: 2024/8/27 17:35
     * @Param feedbackType反馈类型：1 赞；0 踩  no-answer 回答失败
     * @Param answerStatus失败或者成功状态：NULL 回答失败 no-NULL 回答成功
     **/
    private long getSelectCount(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, String feedbackType, String answerStatus) {
        try {
            //根据应用id获取所有的值
            LambdaEsQueryWrapper<Dialogue> wrapper = new LambdaEsQueryWrapper<>();
            wrapper.eq(Dialogue::getApplicationId, applicationOverviewIndicatorsParam.getApplicationId());
            if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
                wrapper.between(Dialogue::getCreateTime, applicationOverviewIndicatorsParam.getStartTime(), applicationOverviewIndicatorsParam.getEndTime());
            }
            if (StringUtils.isNotBlank(feedbackType)) {
                wrapper.eq(Dialogue::getFeedbackType, feedbackType);
            }
            if (StringUtils.isNotBlank(answerStatus)) {
                if ("NULL".equals(answerStatus)) {
                    wrapper.not().exists(Dialogue::getAnswer);
                } else {
                    wrapper.exists(Dialogue::getAnswer);
                }
            }
            return dialogueMapper.selectCount(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getSelectCount 查询异常，请检查");
        }
        return 0L;
    }

    @Override
    public Result getApplicationActiveUser(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        initDefaultDate(applicationOverviewIndicatorsParam);
        List<JSONObject> resultCount = new ArrayList<>();
        try {
            Map<String, Long> stringLongMap = statisticalUserCountByDate(applicationOverviewIndicatorsParam);

            //获取两个时间之间有多少个日期 列表
            List<String> dates = DateRangeExample.getDatesBetween(applicationOverviewIndicatorsParam.getStartTime(),
                    applicationOverviewIndicatorsParam.getEndTime());
            for (String date : dates) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("date", date);
                long activeUserCount = stringLongMap.getOrDefault(date, 0L);
                jsonObject.set("activeUserCount", activeUserCount);
                resultCount.add(jsonObject);
            }
        } catch (IOException e) {
            log.error("selectList 查询异常，请检查", e);
        }
        return Result.success(resultCount);
    }

    /**
     * @description 从es统计人数
     * @param applicationOverviewIndicatorsParam

     * @return
     */
    private Map<String, Long> statisticalUserTotal(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) throws IOException {
        Map<String, Long> userCountMap = Maps.newHashMap();
        // 创建一个聚合请求
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()))
                .mustNot(QueryBuilders.boolQuery()
                        .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("userName")))
                        .should(QueryBuilders.termQuery("userName", "")));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.size(0);

        // 创建去重聚合
        TermsAggregationBuilder aggregation = AggregationBuilders
                .terms("group_by_userName")
                .field("userName.keyword")
                .size(100000); // 设置桶的数量为100000
        searchSourceBuilder.aggregation(aggregation);

        // 执行搜索请求
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 处理响应以获取去重结果
        // 获取聚合结果
        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            // 获取名为 "group_by_userName" 的 terms 聚合
            Terms userNameTerms = aggregations.get("group_by_userName");
            for (Terms.Bucket entry : userNameTerms.getBuckets()) {
                userCountMap.put(entry.getKey().toString(), entry.getDocCount());
            }
        }
        return userCountMap;
    }

    private Map<String, Long> statisticalUserCountByDate(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) throws IOException {
        Map<String, Long> result = new HashMap<>();

        // 创建查询构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 构建基础查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()))
                .mustNot(QueryBuilders.boolQuery()
                        .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("userName")))
                        .should(QueryBuilders.termQuery("userName", "")));

        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }

        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.size(0);

        // 使用 script 提取日期格式为 "yyyy-MM-dd"
        Script dateScript = new Script(ScriptType.INLINE, "painless",
                "Instant.ofEpochMilli(doc['createTime'].value.millis).atZone(ZoneId.of('UTC')).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)",
                Collections.emptyMap());

        // 第一层聚合：按日期字符串分组
        TermsAggregationBuilder dateAgg = AggregationBuilders.terms("group_by_date")
                .script(dateScript)
                .size(31); // 最多统计31天的数据

        // 第二层聚合：在每个日期中统计独立用户名数量
        TermsAggregationBuilder userAgg = AggregationBuilders.terms("distinct_users")
                .field("userName.keyword")
                .size(10000); // 支持最多 10000 个不同用户名

        dateAgg.subAggregation(userAgg);

        // 添加聚合到请求中
        searchSourceBuilder.aggregation(dateAgg);

        // 执行搜索请求
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 处理响应数据
        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            Terms dateTerms = aggregations.get("group_by_date");
            for (Terms.Bucket dateBucket : dateTerms.getBuckets()) {
                String dateKey = dateBucket.getKey().toString(); // 格式已经是 yyyy-MM-dd

                // 获取该日期下的用户名桶
                Terms userTerms = dateBucket.getAggregations().get("distinct_users");

                // 统计桶的数量即为该天的独立用户数
                long distinctUserCount = userTerms.getBuckets().size();

                result.put(dateKey, distinctUserCount);
            }
        }

        return result;
    }

    /**
     * @description: 总使用量
     * @author: caohaifeng
     * @date: 2024/8/12 14:25
     **/
    private long getUsageTotal(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
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
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getSourceType())) {
            if(applicationOverviewIndicatorsParam.getSourceType().equals("APP")){
                BoolQueryBuilder boolQueryshould = QueryBuilders.boolQuery();
                boolQueryshould.should(QueryBuilders.termQuery("clientType", "APP"));
                boolQueryshould.should(QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.existsQuery("clientType")));
                boolQuery.must(boolQueryshould);
            }else {
                boolQuery.must(QueryBuilders.termQuery("clientType", applicationOverviewIndicatorsParam.getSourceType()));
            }
        }
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getUsageTotal ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 获取命中总数
           return searchResponse.getHits().getTotalHits().value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * @description: 总使用用户量
     * @author: caohaifeng
     * @date: 2024/8/12 14:25
     **/
    private long getUsageUserTotal(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        return applicationUserMapper.getApplicationUserCount(applicationOverviewIndicatorsParam.getStartTime(),
                applicationOverviewIndicatorsParam.getEndTime(), applicationOverviewIndicatorsParam.getApplicationId(),applicationOverviewIndicatorsParam.getApplicationIds());
    }

    /**
     * @description: 总新增用户使用量
     * @author: caohaifeng
     * @date: 2024/8/12 14:25
     **/
    private long getUsageAddUserTotal(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        return applicationUserMapper.getApplicationUserCount(applicationOverviewIndicatorsParam.getStartTime(),
                applicationOverviewIndicatorsParam.getEndTime(), applicationOverviewIndicatorsParam.getApplicationId(),applicationOverviewIndicatorsParam.getApplicationIds());
    }

    /**
     * @description: 使用用户日均存活率 -- 今日使用应用的用户数/绑定应用的用户数
     * @author: caohaifeng
     * @date: 2024/8/12 14:25
     **/
    private double getUsageUserDayRetentionRate(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        //应用绑定用户的总数量
        final long applicationBindUserCount = applicationUserMapper.getApplicationBindUserCount(applicationOverviewIndicatorsParam.getApplicationId());
        long applicationTodayUsingUserCount = 0L;

        //应用今日使用的用户数量
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getSourceType())) {
            if(applicationOverviewIndicatorsParam.getSourceType().equals("APP")){
                BoolQueryBuilder boolQueryshould = QueryBuilders.boolQuery();
                boolQueryshould.should(QueryBuilders.termQuery("clientType", "APP"));
                boolQueryshould.should(QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.existsQuery("clientType")));
                boolQuery.must(boolQueryshould);
            }else {
                boolQuery.must(QueryBuilders.termQuery("clientType", applicationOverviewIndicatorsParam.getSourceType()));
            }
        }

        // 创建聚合查询
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_field").size(100000)
                .field("userName.keyword") // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                .order(BucketOrder.count(true)); // 按计数降序排列
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getUsageTotal ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            applicationTodayUsingUserCount = terms.getBuckets().size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (applicationBindUserCount == 0L) {
            return 0.00d;
        }

        //计算一下 今日访问数/总绑定数
        final double rate = Double.valueOf(applicationTodayUsingUserCount) / Double.valueOf(applicationBindUserCount);
        return rate;
    }


    /**
     * @description: 根据开始时间和结束时间获取统计图横坐标维度列表
     * @author: caohaifeng
     * @date: 2024/8/13 10:37
     **/
    List<String[]> getDateStringsListByStartTimeAndEndTime(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, long modelSize) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String[]> resList = new ArrayList<>();
        if (StringUtils.isBlank(applicationOverviewIndicatorsParam.getStartTime()) || StringUtils.isBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            log.error("getDateStringsListByStartTimeAndEndTime -- 开始时间和结束时间不能为空");
            return resList;
        }
        //开始时间和结束时间的天数差值
        final long differentDays = DateUtil.differentDays(applicationOverviewIndicatorsParam.getStartTime(), applicationOverviewIndicatorsParam.getEndTime());
        long gapDays = (differentDays / modelSize) + 1;
        String startTime = applicationOverviewIndicatorsParam.getStartTime();
//        String endTime = applicationOverviewIndicatorsParam.getEndTime();
        for (long l = -1; l <= (differentDays / gapDays)-1; l++) {
            // 使用 DateTimeFormatter 解析日期字符串
            LocalDate date = LocalDate.parse(startTime.split(" ")[0], dateFormatter);
            final String[] stringsAfter = DateUtil.startDayOfAddDaySize(date, gapDays * l);
            final String[] stringsBefor = DateUtil.startDayOfAddDaySize(date, gapDays * (l+1));
            stringsAfter[1] = stringsBefor[0];
            resList.add(stringsAfter);
        }
        return resList;
    }


    private JSONObject getSwitchTypeShowCount(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam){
        // 创建一个DecimalFormat对象，并设置保留四位小数
        DecimalFormat df = new DecimalFormat("#.##");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startTime", applicationOverviewIndicatorsParam.getEndTime().split(" ")[0]);
        jsonObject.put("endTime", applicationOverviewIndicatorsParam.getEndTime().split(" ")[0]);
        jsonObject.put("originalStartTime", applicationOverviewIndicatorsParam.getStartTime());
        jsonObject.put("originalEndTime", applicationOverviewIndicatorsParam.getEndTime());
//        jsonObject.put("unit", "天");
        jsonObject.put("showCount", 0L);
        switch (applicationOverviewIndicatorsParam.getType()) {
            case "1":
//                applicationOverviewIndicatorsParam.setSourceType("APP");
                Object object1 =  getUsageTotal(applicationOverviewIndicatorsParam);
                jsonObject.put("showCount", object1);
//                applicationOverviewIndicatorsParam.setSourceType("PC");
                jsonObject.put("showCountPc", object1);
                break;
            case "2":
//                applicationOverviewIndicatorsParam.setSourceType("APP");
                Object object2 =  getUsageUserTotal(applicationOverviewIndicatorsParam);
                jsonObject.put("showCount", object2);
//                applicationOverviewIndicatorsParam.setSourceType("PC");
                jsonObject.put("showCountPc", object2);
                break;
            case "3":
//                applicationOverviewIndicatorsParam.setSourceType("APP");
                Object object3 =  getUsageAddUserTotal(applicationOverviewIndicatorsParam);
                jsonObject.put("showCount", object3);
//                applicationOverviewIndicatorsParam.setSourceType("PC");
                jsonObject.put("showCountPc", object3);
                break;
            case "4":
//                applicationOverviewIndicatorsParam.setSourceType("APP");
                Object object4 =  df.format(getUsageUserDayRetentionRate(applicationOverviewIndicatorsParam) * 100) + "";
                jsonObject.put("showCount", object4);
//                applicationOverviewIndicatorsParam.setSourceType("PC");
                jsonObject.put("showCountPc", object4);
                break;
            default:
                break;
        }
        return jsonObject;
    }

    /**
     * @description: 根据应用id获取 审核数 赞踩数 的分组count数组
     * @author: caohaifeng
     * @date: 2025/1/6 16:25
     **/
    private Map<String, Long> getCountGroupType(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        Map<String, Long> resultMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("applicationId", applicationOverviewIndicatorsParam.getApplicationId()));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        TermsAggregationBuilder aggregation = null;
        if ("1".equals(applicationOverviewIndicatorsParam.getType())) { //赞
            boolQuery.must(QueryBuilders.termQuery("feedbackType", "1"));
            // 创建聚合查询
            aggregation = AggregationBuilders.terms("group_by_field").size(100000)
                    .field("feedbackDeptId.keyword") // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                    .order(BucketOrder.count(true)); // 按计数降序排列
        } else if ("2".equals(applicationOverviewIndicatorsParam.getType())) { //踩
            boolQuery.must(QueryBuilders.termQuery("feedbackType", "0"));
            // 创建聚合查询
            aggregation = AggregationBuilders.terms("group_by_field").size(100000)
                    .field("feedbackDeptId.keyword") // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                    .order(BucketOrder.count(true)); // 按计数降序排列
        } else if ("4".equals(applicationOverviewIndicatorsParam.getType())) { //问答审核
            boolQuery.must(QueryBuilders.existsQuery("verifyDeptId"));
            // 创建聚合查询
            aggregation = AggregationBuilders.terms("group_by_field").size(100000)
                    .field("verifyDeptId") // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                    .order(BucketOrder.count(true)); // 按计数降序排列
        }
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getCountGroupType ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            terms.getBuckets().forEach(bucket -> {
                System.out.println(bucket.getKeyAsString() + ": " + bucket.getDocCount());
                resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * @description: 根据应用id获取 审核数 赞踩数 的分组count数组
     * @author: caohaifeng
     * @date: 2025/1/6 16:25
     **/
    private Map<String, Long> getCountKnowledgeGroupType(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
        Map<String, Long> resultMap = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(dialogueIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termsQuery("knowledgeId", applicationOverviewIndicatorsParam.getKnowledgeIds()));
        if (StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getStartTime()) && StringUtils.isNotBlank(applicationOverviewIndicatorsParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("updateTime").gte(applicationOverviewIndicatorsParam.getStartTime()).lte(applicationOverviewIndicatorsParam.getEndTime()));
        }
        TermsAggregationBuilder aggregation = null;
        boolQuery.must(QueryBuilders.existsQuery("deptId.keyword"));
        // 创建聚合查询
        aggregation = AggregationBuilders.terms("group_by_field").size(100000)
                .field("deptId.keyword") // 替换为你的字段名，注意使用.keyword后缀以使用精确匹配
                .order(BucketOrder.count(true)); // 按计数降序排列
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        log.info("getCountKnowledgeGroupType ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            terms.getBuckets().forEach(bucket -> {
                System.out.println(bucket.getKeyAsString() + ": " + bucket.getDocCount());
                resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
    public void downloadInterceptWordDataTemp() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

            // 从类路径加载资源
            //Resource classpathResource = loader.getResource("classpath:application.properties");
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:temp/applicationStatdocx.docx");
            org.springframework.core.io.Resource resource1 = resourceLoader.getResource("classpath:temp/ai1.jpg");
            org.springframework.core.io.Resource resource2 = resourceLoader.getResource("classpath:temp/ai2.jpg");
            org.springframework.core.io.Resource resource3 = resourceLoader.getResource("classpath:temp/ai3.jpg");
            org.springframework.core.io.Resource resource4 = resourceLoader.getResource("classpath:temp/ai4.jpg");
            XWPFDocument document = new XWPFDocument(resource.getInputStream());
            // 替换占位符
            Map<String, String> replacements = new HashMap<>();
            replacements.put("x1", "31");
            replacements.put("y1", "351");
            replacements.put("x2", "352");
            replacements.put("y2", "352");
            replacePlaceholder(document, replacements);
            List<InputStream>inputStreamList=new ArrayList<>();
            inputStreamList.add(resource1.getInputStream());
            inputStreamList.add(resource2.getInputStream());
            inputStreamList.add(resource3.getInputStream());
            inputStreamList.add(resource4.getInputStream());
            insertImage(document,inputStreamList);
            FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\jjj\\example2.docx");
            document.write(fos);
            fos.close();
            /*// 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("测评模板下载.xlsx", StandardCharsets.UTF_8.toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            // 将文件写入响应输出流
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void replacePlaceholder(XWPFDocument document, Map<String, String> replacements) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        text = text.replace(entry.getKey(), entry.getValue());
                    }
                    run.setText(text, 0);
                }
            }
        }
    }
    private void insertImage(XWPFDocument document,List<InputStream> inputStream) throws IOException, InvalidFormatException {
        List<String>images=new ArrayList();
        images.add("image1.png");
        images.add("image2.png");
        images.add("image3.png");
        images.add("image4.png");
        for (XWPFParagraph itemParagraph : document.getParagraphs()) {
            for (XWPFRun itemRun : itemParagraph.getRuns()) {
                for (XWPFPicture itemEmbeddedPicture : itemRun.getEmbeddedPictures()) {
                    // 确定某个图片（这里的图片之所以有自定义的描述，是因为图片是通过代码插入的）
                    String string=itemEmbeddedPicture.getPictureData().getFileName();
                    for (int i=0;i<inputStream.size();i++) {
                        if (com.alibaba.nacos.api.utils.StringUtils.equals(images.get(i), itemEmbeddedPicture.getPictureData().getFileName())) {
                            // 待替换的图片文件
                            //File toReplaceImage = new File("xxx.jpg");
                            try{
                                // 替换图片
                                itemEmbeddedPicture.getCTPicture().getBlipFill().getBlip().setEmbed(document.addPictureData(
                                inputStream.get(i),
                                XWPFDocument.PICTURE_TYPE_JPEG));
                                // 根据图片修改大小
                                //BufferedImage bufferedImage = ImageIO.read(inputStream);
                                //itemRun.getCTR().getDrawingArray(0).getInlineList().get(0).getExtent().setCx(5000000L);
                                //itemRun.getCTR().getDrawingArray(0).getInlineList().get(0).getExtent().setCy(5000000L * bufferedImage.getHeight() / bufferedImage.getWidth());
                                //itemEmbeddedPicture.getCTPicture().getSpPr().getXfrm().getExt().setCx(5000000L);
                                //itemEmbeddedPicture.getCTPicture().getSpPr().getXfrm().getExt().setCy(5000000L * bufferedImage.getHeight() / bufferedImage.getWidth());
                            } catch (InvalidFormatException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }
}
