package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Maps;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.FormConstant;
import com.wenge.model.dto.param.MatterInfoPageParam;
import com.wenge.model.dto.param.MatterInfoUpdateParam;
import com.wenge.model.entity.*;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.enums.MatterInfoStatusEnum;
import com.wenge.model.mapper.*;
import com.wenge.model.mapper.es.MatterDataMapper;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.MatterGuideInfoService;
import com.wenge.model.service.MatterGuideService;
import com.wenge.model.utils.*;
import com.wenge.model.vo.MatterGuideInfoPageVo;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.core.Wrappers;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.MatterGuideFiledTableDef.MATTER_GUIDE_FILED;
import static com.wenge.model.entity.table.MatterGuideGroupTableDef.MATTER_GUIDE_GROUP;
import static com.wenge.model.entity.table.MatterGuideInfoTableDef.MATTER_GUIDE_INFO;
import static com.wenge.model.entity.table.MatterGuideOptionTableDef.MATTER_GUIDE_OPTION;
import static com.wenge.model.entity.table.MatterGuideTableDef.MATTER_GUIDE;


@Service
@Slf4j
public class MatterGuideInfoServiceImpl extends ServiceImpl<MatterGuideInfoMapper, MatterGuideInfo> implements MatterGuideInfoService {

    @Autowired
    MatterGuideInfoMapper matterGuideInfoMapper;
    @Autowired
    MatterGuideDataMapper matterGuideDataMapper;
    @Autowired
    MatterGuideFiledMapper matterGuideFiledMapper;
    @Autowired
    MatterGuideOptionMapper matterGuideOptionMapper;
    @Autowired
    MatterGuideMapper matterGuideMapper;

    private static final Integer EXPORT_MAX_SIZE = 10000;
    private static final List<String> EXCEL_TITLE = ListUtil.toList("事项类型", "姓名", "身份证号", "联系电话", "进度", "提交时间");

    @Autowired
    private MatterDataMapper matterDataMapper;

    @Autowired
    private MatterGuideGroupMapper matterGuideGroupMapper;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private MatterGuideService matterGuideService;

    @Value("${esIndex.matterData}")
    private String matterData;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public Result<PageInfo<JSONObject>> getListNew(MatterInfoPageParam param) {

        FlexModel<MatterGuide> matterGuideFlexModel = MatterGuide.create();

        // 根据权限查询
        PermissionControlUtils.buildPermission(matterGuideFlexModel, BusinessPermissionEnum.MATTER);
        List<MatterGuide> lists = matterGuideFlexModel.lists();
        List<String> matterIdList = lists.stream().map(MatterGuide::getMatterId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(matterIdList)) {
            return Result.success(new PageInfo<>(param.getPageNo(), param.getPageSize(), NumberConstants.ZERO, ListUtil.toList()));
        }

        SearchRequest searchRequest = new SearchRequest(matterData);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 必须条件
        boolQuery.must(QueryBuilders.termsQuery("matterId", matterIdList));

        if (StringUtils.isNotBlank(param.getApplicationId())) {
            boolQuery.must(QueryBuilders.termQuery("applicationId", param.getApplicationId()));
        }
        if (StringUtils.isNotBlank(param.getMatterId())) {
            boolQuery.must(QueryBuilders.termQuery("matterId", param.getMatterId()));
        }
        if (param.getStatus() != null && param.getStatus() == 1) {
            boolQuery.must(QueryBuilders.termQuery("status.keyword", "已审批"));
        }
        if (param.getStatus() != null && param.getStatus() == 0) {
            boolQuery.must(QueryBuilders.termQuery("status.keyword", "未审批"));
        }

        if (StringUtils.isNotBlank(param.getStartTime()) && StringUtils.isNotBlank(param.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime.keyword").gte(param.getStartTime()).lte(param.getEndTime()));
        }

        if (!CollectionUtils.isEmpty(param.getSearchList())) {
            List<String> defaultValues = Arrays.asList("input", "textarea", "select", "radio", "checkbox", "email", "number", "tel", "text");
            List<String> defaultValues2 = Arrays.asList("date", "time", "datetime", "datePicker");
            for (MatterGuideFiled matterGuideFiled : param.getSearchList()) {
                BoolQueryBuilder boolQueryshould = QueryBuilders.boolQuery();
                if (defaultValues.contains(matterGuideFiled.getFormType()) || matterGuideFiled.getFormType().contains("addForm")) { //文本检索域
                    if (StringUtils.isBlank(matterGuideFiled.getSearchValue())) {
                        continue;
                    }
                    if (matterGuideFiled.getSearchType().equals("")
                            || matterGuideFiled.getSearchType() == null
                            || matterGuideFiled.getSearchType().equals("2")) {//模糊匹配
                        boolQueryshould.must(QueryBuilders.wildcardQuery(matterGuideFiled.getFiledCode() + ".keyword", "*" + matterGuideFiled.getSearchValue() + "*"));
                    } else if (matterGuideFiled.getSearchType().equals("1")) {//精确匹配
                        boolQueryshould.must(QueryBuilders.termQuery(matterGuideFiled.getFiledCode() + ".keyword", matterGuideFiled.getSearchValue()));
                    }
                } else if (defaultValues2.contains(matterGuideFiled.getFormType())) {
                    if (StringUtils.isBlank(matterGuideFiled.getSearchValue())) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(matterGuideFiled.getSearchValue()) && StringUtils.isNotBlank(matterGuideFiled.getSearchValue2())) {
                        boolQuery.must(QueryBuilders.rangeQuery(matterGuideFiled.getFiledCode()).gte(matterGuideFiled.getSearchValue()).lte(matterGuideFiled.getSearchValue2()));
                    }
                }
                boolQuery.must(boolQueryshould);
            }
        }

        //关键字模糊检索
        if (StringUtils.isNotBlank(param.getKeyword())) {
            BoolQueryBuilder boolQueryshould = QueryBuilders.boolQuery();
            for (MatterGuideFiled matterGuideFiled : param.getSearchList()) {
                boolQueryshould.should(QueryBuilders.termQuery(matterGuideFiled.getFiledCode(), matterGuideFiled.getSearchValue()));
            }
            boolQuery.must(boolQueryshould);
        }

        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        searchSourceBuilder.from((param.getPageNo() - 1) * param.getPageSize()).size(param.getPageSize()).query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("createTime.keyword", SortOrder.DESC);
        log.info("getListNew ---- DSL：{}", searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        Map<String, String> applicationNameMap = new HashMap<>();
        Map<String, String> matterNameMap = new HashMap<>();
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                String sourceAsString = hit.getSourceAsString(); // 获取文档的原始JSON字符串
                final JSONObject jsonObject = JSONObject.parseObject(sourceAsString);
                String status = jsonObject.get("status") != null ? jsonObject.get("status") + "" : "";
                if("未审批".equals(status)){
                    jsonObject.put("status", 0);
                }else if("已审批".equals(status)){
                    jsonObject.put("status", 1);
                }
                jsonObject.put("infoId", jsonObject.get("id"));
                if (jsonObject.get("applicationId") != null && StringUtils.isNotBlank(jsonObject.get("applicationId") + "")) {
                    if (applicationNameMap.get(jsonObject.get("applicationId")) != null) {
                        jsonObject.put("applicationName", applicationNameMap.get(jsonObject.get("applicationId")));
                    } else {
                        final ApplicationInfo applicationInfo = applicationInfoService.getByAppId(jsonObject.get("applicationId") + "");
                        if (null != applicationInfo) {
                            jsonObject.put("applicationName", applicationInfo.getApplicationName());
                        }
                    }
                }

                if (jsonObject.get("matterId") != null && StringUtils.isNotBlank(jsonObject.get("matterId") + "")) {
                    if (matterNameMap.get(jsonObject.get("matterId")) != null) {
                        jsonObject.put("matterName", matterNameMap.get(jsonObject.get("matterId")));
                    } else {
                        final MatterGuide matterGuide = matterGuideService.getMatterByMatterId(jsonObject.get("matterId") + "");
                        jsonObject.put("matterName", matterGuide.getMatterName());
                    }
                }

                jsonObjectList.add(jsonObject);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
        return Result.success(new PageInfo<>(param.getPageNo(), param.getPageSize(), searchResponse.getHits().getTotalHits().value, jsonObjectList));
    }

    @Override
    public Result<PageInfo<MatterGuideInfoPageVo>> getList(MatterInfoPageParam param) {
        LambdaEsQueryWrapper<MatterData> wrapper = EsWrappers.lambdaQuery(MatterData.class)
                .eq(StringUtils.isNotBlank(param.getMatterId()), MatterData::getMatterId, param.getMatterId())
                .eq(StringUtils.isNotBlank(param.getApplicationId()), MatterData::getApplicationId, param.getApplicationId())
                .eq(null != param.getStatus() && 1 == param.getStatus(), MatterData::getStatus, "未审批");
                wrapper.or(StringUtils.isNotBlank(param.getNameCardPhone()), or -> {
                            or.like("peopleName", param.getNameCardPhone());
                            or.like("idCard", param.getNameCardPhone());
                            or.like("phone", param.getNameCardPhone());
                        });
            if (!CollectionUtils.isEmpty(param.getSearchList())) {
            List<String> defaultValues = Arrays.asList("input", "textarea", "select", "radio", "checkbox", "email", "number", "tel", "text");
            List<String> defaultValues2 = Arrays.asList("date", "time", "datetime", "datePicker");
            for (MatterGuideFiled matterGuideFiled : param.getSearchList()) {
                if (defaultValues.contains(matterGuideFiled.getFormType()) || matterGuideFiled.getFormType().contains("addForm")) { //文本检索域
                    if (matterGuideFiled.getSearchType().equals("")
                            || matterGuideFiled.getSearchType() == null
                            || matterGuideFiled.getSearchType().equals("2")) {//模糊匹配
                        wrapper.like(StringUtils.isNotBlank(matterGuideFiled.getSearchValue()), matterGuideFiled.getFiledCode(), matterGuideFiled.getSearchValue());
                    } else if (matterGuideFiled.getSearchType().equals("1")) {//精确匹配
                        wrapper.eq(StringUtils.isNotBlank(matterGuideFiled.getSearchValue()), matterGuideFiled.getFiledCode(), matterGuideFiled.getSearchValue());
                    }
                } else if (defaultValues2.contains(matterGuideFiled.getFormType())) {
                    wrapper.ge(StringUtils.isNotBlank(matterGuideFiled.getSearchValue()), matterGuideFiled.getFiledCode(), matterGuideFiled.getSearchValue())
                            .le(StringUtils.isNotBlank(matterGuideFiled.getSearchValue2()), matterGuideFiled.getFiledCode(), matterGuideFiled.getSearchValue2());
                }
            }
        }

        //关键字模糊检索
        if (StringUtils.isNotBlank(param.getKeyword())) {
            wrapper.or(StringUtils.isNotBlank(param.getKeyword()), or -> {
                for (MatterGuideFiled matterGuideFiled : param.getSearchList()) {
                    or.like(matterGuideFiled.getFiledCode(), param.getKeyword());
                }
            });
        }
        wrapper.ge(StringUtils.isNotBlank(param.getStartTime()), "createTime.keyword", param.getStartTime())
                .le(StringUtils.isNotBlank(param.getEndTime()), "createTime.keyword", param.getEndTime());
        EsPageInfo<MatterData> matterDataEsPageInfo = matterDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        List<MatterGuide> matterGuideList = MatterGuide.create()
                .lists();
        HashMap<String, String> nameMap = matterGuideList.stream().collect(Collectors.toMap(
                MatterGuide::getMatterId,
                MatterGuide::getMatterName,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
        List<MatterData> list = matterDataEsPageInfo.getList();
        List<MatterGuideInfoPageVo> collect = list.stream().map(p -> {
            String status = p.getStatus();
            int statusInt = 0;
            switch (status) {
                case "未审批":
                    statusInt = 0;
                    break;
                case "已审批":
                    statusInt = 1;
                    break;
            }
                MatterGuideInfoPageVo vo = MatterGuideInfoPageVo.builder()
                        .id(p.getId())
                        .infoId(p.getId())
                        .matterId(p.getMatterId())
                        .matterName(nameMap.get(p.getMatterId()))
                        .userId(p.getId())
                        .userName(p.getString("peopleName"))
                        .updateTime(p.getCreateTime())
                        .createTime(p.getCreateTime())
                        .status(statusInt)
                        .idCard(p.getString("idCard"))
                        .phone(p.getString("phone"))
                        .build();

            return vo;
        }).collect(Collectors.toList());
        return Result.success(new PageInfo<>(param.getPageNo(), param.getPageSize(), matterDataEsPageInfo.getTotal(), collect));
    }

    @Override
    public Result getDetail(String infoId) {
        MatterData matterData = matterDataMapper.selectById(infoId);

        //MatterGuideInfo guideInfo = matterGuideInfoMapper
        //        .selectOneByQuery(new QueryWrapper().and(new QueryColumn("info_id").eq(infoId)));
        //String matterId = guideInfo.getMatterId();

        List<MatterGuideFiled> guideFiledList = matterGuideFiledMapper.selectListByQuery(new QueryWrapper()
                .and(new QueryColumn("matter_id").eq(matterData.getMatterId())));
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq("是")));

        //List<MatterGuideData> guideDataList = matterGuideDataMapper.selectListByQuery(new QueryWrapper()
        //        .and(new QueryColumn("matter_id").eq(matterId))
        //        .and(new QueryColumn("info_id").eq(infoId)));
        //Map<String, String> guideDataMap = guideDataList.stream().collect(Collectors.toMap(MatterGuideData::getFiledId,
        //        MatterGuideData::getDataValue));

        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();
        List<MatterGuideFiled> opList = new ArrayList<>();
        // 处理页面头部信息，并且移除走，后续不处理
        for (MatterGuideFiled guideFiled : guideFiledList) {
            if ("title".equals(guideFiled.getFiledCode())) {
                innerFormInfoVo.setTitle(guideFiled.getTip());
            } else if ("message".equals(guideFiled.getFiledCode())) {
                innerFormInfoVo.setMessage(guideFiled.getTip());
            } else {
                opList.add(guideFiled);
            }
        }

        List<FormItem> forms = new ArrayList<>();
        JSONObject params = new JSONObject();
        // 把配置按照表单分组。类型为form和addForm的就是表单头
        ArrayList<List<MatterGuideFiled>> groups = getGroup(opList);
        // 遍历每个表单
        for (List<MatterGuideFiled> groupList : groups) {
            // 构建forms
            List<ConfigItem> configs = new ArrayList<>();
            FormItem formItem = new FormItem();
            for (MatterGuideFiled guideFiled : groupList) {
                // 表单开始,构建表头
                if ((FormConstant.FORM.equals(guideFiled.getFormType())
                        || FormConstant.ADD_FORM.equals(guideFiled.getFormType()))) {
                    formItem.setBasicInfo(guideFiled.getTip());
                    formItem.setType(guideFiled.getFormType());
                    if (FormConstant.ADD_FORM.equals(guideFiled.getFormType())) {
                        String s = matterData.getString(guideFiled.getFiledCode());
                        ConfigItem configItem = new ConfigItem();
                        configItem.setField(guideFiled.getFiledName());
                        configItem.setType(guideFiled.getFormType());
                        configItem.setValue(s);
                        configs.add(configItem);
                    }
                    continue;
                }
                // 构建configs
                configs.add(buildConfigItem(guideFiled, params, matterData));
            }
            formItem.setConfigs(configs);
            forms.add(formItem);
        }

        innerFormInfoVo.setForms(forms);
        innerFormInfoVo.setCreateTime(matterData.getCreateTime());
        innerFormInfoVo.setRemark(matterData.getRemark());
        //innerFormInfoVo.setParams(params);
        return Result.success(innerFormInfoVo);
    }


    @Override
    public Result getDetailNew(String infoId) {
        MatterData matterData = matterDataMapper.selectById(infoId);
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_FILED.MATTER_ID.eq(matterData.getMatterId()))
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq(YesNoEnum.YES.getName()))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP_ID.isNotNull())
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .orderBy(MATTER_GUIDE_FILED.SORTED.asc());
        List<MatterGuideFiled> guideFiledList = matterGuideFiledMapper.selectListByQuery(queryWrapper);
        Assert.notEmpty(guideFiledList, "配置为空!");

        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();
        List<MatterGuideFiled> opList = new ArrayList<>();
        // 处理页面头部信息，并且移除走，后续不处理
        for (MatterGuideFiled guideFiled : guideFiledList) {
            if ("title".equals(guideFiled.getFiledCode())) {
                innerFormInfoVo.setTitle(guideFiled.getTip());
            } else if ("message".equals(guideFiled.getFiledCode())) {
                innerFormInfoVo.setMessage(guideFiled.getTip());
            } else {
                opList.add(guideFiled);
            }
        }

        // 使用fieldName进行分组
        Map<String, List<MatterGuideFiled>> groupedByFieldName = opList.stream().filter(guideFiled -> guideFiled.getFiledCodeGroupId() != null)
                .collect(Collectors.groupingBy(MatterGuideFiled::getFiledCodeGroupId));
        List<String> filedCodeGroupIds = new ArrayList<>();
        groupedByFieldName.forEach((key, value)->{
            filedCodeGroupIds.add(key);
        });

        List<FormItem> forms = new ArrayList<>();
        JSONObject params = new JSONObject();
        //查询分组表 排序
        List<MatterGuideGroup> matterGuideGroupList = matterGuideGroupMapper.selectListByQuery(
                QueryWrapper.create().where(MATTER_GUIDE_GROUP.ID.in(filedCodeGroupIds)).orderBy(MATTER_GUIDE_GROUP.SORTED.asc()));

        for (MatterGuideGroup matterGuideGroup : matterGuideGroupList) {
            List<MatterGuideFiled> matterGuideFiledList = groupedByFieldName.get(matterGuideGroup.getId() + "");
            // 构建forms
            List<MatterGuideInfoServiceImpl.ConfigItem> configs = new ArrayList<>();
            MatterGuideInfoServiceImpl.FormItem formItem = new MatterGuideInfoServiceImpl.FormItem();
            for (MatterGuideFiled matterGuideFiled  : matterGuideFiledList) {
                configs.add(buildConfigItem(matterGuideFiled, params, matterData));
            }
            //表单提交 分组类型标记为表单
            if(!CollectionUtils.isEmpty(configs)){
                if (configs.get(0).getType().startsWith("addForm")) {
                    formItem.setType(configs.get(0).getType());
                    formItem.setFiledName(configs.get(0).getField());
                }
            }
            formItem.setConfigs(configs);
            formItem.setBasicInfo(matterGuideGroup.getName());
            formItem.setRemark(matterGuideGroup.getRemark());
            formItem.setSorted(matterGuideGroup.getSorted());
//            formItem.setType(matterGuideGroup.getFormType());
            forms.add(formItem);
        }
        innerFormInfoVo.setForms(forms);
        innerFormInfoVo.setCreateTime(matterData.getCreateTime());
        innerFormInfoVo.setRemark(matterData.getRemark());
        return Result.success(innerFormInfoVo);
    }

    @Override
    public Result updateInfo(MatterInfoUpdateParam param) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String userName = tokenUserInfo.getUserName();

        String status = "未审批";
        switch (param.getStatus()) {
            case 1:
                status = "已审批";
                break;
            default:
                break;
        }
        LambdaEsUpdateWrapper<MatterData> updateWrapper = EsWrappers.lambdaUpdate(MatterData.class)
                .eq(MatterData::getId, param.getInfoId())
                .set(MatterData::getDealUser, userName)
                .set(MatterData::getDealTime, DateUtil.getCurrentTime())
                .set(MatterData::getStatus, status)
                .set(MatterData::getRemark, param.getRemark());
        matterDataMapper.update(null, updateWrapper);

        //Wrappers<Object> wrappers = Wrappers.init().and(MATTER_GUIDE_INFO.INFO_ID.in(param.getInfoId()));
        //MatterGuideInfo matterGuideInfo = matterGuideInfoMapper.selectOneByQuery(wrappers);
        //matterGuideInfo.setStatus(Objects.nonNull(param.getStatus()) ? param.getStatus() : matterGuideInfo.getStatus());
        //matterGuideInfo.setRemark(CustomStringUtils.isNotEmpty(param.getRemark()) ? param.getRemark() : matterGuideInfo.getRemark());
        //matterGuideInfo.setUpdateTime(new Date());
        //matterGuideInfoMapper.update(matterGuideInfo);
        return Result.success();
    }

    @Override
    public void export(MatterInfoPageParam param, HttpServletResponse response) throws IllegalAccessException, IOException {
        param.setPageNo(1);
        param.setPageSize(EXPORT_MAX_SIZE);
        List<MatterGuide> matterGuideList = matterGuideMapper.selectListByQuery(Wrappers.init().and(MATTER_GUIDE.SHOW_FLAG.eq("是")));
        Map<String, String> matterGuideMap = matterGuideList.stream().collect(Collectors.toMap(MatterGuide::getMatterId, MatterGuide::getMatterName));

        // 构建检索条件 发起检索
        Result<PageInfo<MatterGuideInfoPageVo>> list = getList(param);
        PageInfo<MatterGuideInfoPageVo> page = list.getData();
        List<MatterGuideInfoPageVo> records = page.getRecords();

        //Wrappers infoWrapper = buildQueryListWrapper(param);
        //Page<MatterGuideInfo> page = page(Page.of(param.getPageNo(), param.getPageSize()), infoWrapper);
        //List<MatterGuideInfo> records = page.getRecords();
        if (CustomCollectionUtils.isEmpty(records)) {
            throw new IllegalAccessException("导出结果集中没有数据");
        }
        try {
            // 构建excel导出数据
            List<List<String>> rows = buildExcelRows2(records, matterGuideMap);
            String fileName = "export_" + UUID.randomUUID() + ".xls";
            exportExcel(response, fileName, rows);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void exportNew(MatterInfoPageParam param, HttpServletResponse response) throws IllegalAccessException, IOException {
        param.setPageNo(1);
        param.setPageSize(EXPORT_MAX_SIZE);
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_FILED.MATTER_ID.eq(param.getMatterId()))
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.EXPORT_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP_ID.isNotNull())
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .orderBy(MATTER_GUIDE_FILED.SORTED.asc());
        List<MatterGuideFiled> matterGuideFiledList = matterGuideFiledMapper.selectListByQuery(queryWrapper);

        // 构建检索条件 发起检索
        Result<PageInfo<JSONObject>> list = getListNew(param);
        PageInfo<JSONObject> page = list.getData();
        List<JSONObject> records = page.getRecords();

        //Wrappers infoWrapper = buildQueryListWrapper(param);
        //Page<MatterGuideInfo> page = page(Page.of(param.getPageNo(), param.getPageSize()), infoWrapper);
        //List<MatterGuideInfo> records = page.getRecords();
        if (CustomCollectionUtils.isEmpty(records)) {
            throw new IllegalAccessException("导出结果集中没有数据");
        }
        try {
            // 构建excel导出数据
            List<List<String>> rows = buildExcelRows2New(records, matterGuideFiledList);
            String fileName = "export_" + UUID.randomUUID() + ".xls";
            exportExcel(response, fileName, rows);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param matterId
     * @description: 事项处置-根据事项id动态获取检索条件
     * @author: caohaifeng
     * @date: 2024/10/9 14:30
     * @param: [matterId]
     * @return: com.wg.appframe.core.bean.Result
     */
    @Override
    public Result getSearchFiledByMatterId(String matterId) {
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId))
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.SEARCH_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP_ID.isNotNull())
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .orderBy(MATTER_GUIDE_FILED.SORTED.asc());
        List<MatterGuideFiled> guideFiledList = matterGuideFiledMapper.selectListByQuery(queryWrapper);
        if(CollectionUtil.isNotEmpty(guideFiledList)){
            for (MatterGuideFiled matterGuideFiled : guideFiledList) {
                matterGuideFiled.setFiledCodeGroup(matterGuideFiled.getFiledCodeGroupId() + "");
                List<MatterGuideOption> matterGuideOptions = MatterGuideOption.create().
                        where(MATTER_GUIDE_OPTION.DELETED_FLAG.eq(0)).and(MATTER_GUIDE_OPTION.FILED_ID.eq(matterGuideFiled.getFiledId())).lists();
                matterGuideFiled.setOptionList(matterGuideOptions);
            }
        }
        return Result.success(guideFiledList);
    }

    /**
     * @param matterId
     * @description: 事项处置-根据事项id动态获取表头
     * @author: caohaifeng
     * @date: 2024/10/9 14:30
     * @param: [matterId]
     * @return: com.wg.appframe.core.bean.Result
     */
    @Override
    public Result getTableHeadFiledByMatterId(String matterId) {
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId))
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.TABLE_SHOW_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP_ID.isNotNull())
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .orderBy(MATTER_GUIDE_FILED.SORTED.asc());

        List<MatterGuideFiled> guideFiledList = matterGuideFiledMapper.selectListByQuery(queryWrapper);
        return Result.success(guideFiledList);
    }

    private Wrappers buildQueryListWrapper(MatterInfoPageParam param) {
        String matterId = param.getMatterId();
        Integer status = param.getStatus();
        String nameCardPhone = param.getNameCardPhone();
        String startTime = param.getStartTime();
        String endTime = param.getEndTime();
        return Wrappers.init()
                .where(Objects.nonNull(status), MATTER_GUIDE_INFO.STATUS.eq(status))
                .and(CustomStringUtils.isNotEmpty(matterId), MATTER_GUIDE_INFO.MATTER_ID.eq(matterId))
                .and(CustomStringUtils.isNotEmpty(startTime), MATTER_GUIDE_INFO.CREATE_TIME.ge(startTime))
                .and(CustomStringUtils.isNotEmpty(endTime), MATTER_GUIDE_INFO.CREATE_TIME.le(endTime))
                .and(CustomStringUtils.isNotEmpty(nameCardPhone),
                        MATTER_GUIDE_INFO.ID_CARD.like(nameCardPhone)
                                .or(MATTER_GUIDE_INFO.PHONE.like(nameCardPhone)
                                        .or(MATTER_GUIDE_INFO.USER_NAME.like(nameCardPhone)))
                )
                .orderBy(MATTER_GUIDE_INFO.UPDATE_TIME.desc());
    }


    private List<List<String>> buildExcelRows(List<MatterGuideInfo> matterGuideInfos, Map<String, String> matterGuideMap) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(EXCEL_TITLE);
        for (MatterGuideInfo info : matterGuideInfos) {
            List<String> dataList = ListUtil.toList(
                    CustomMapUtils.isNotEmpty(matterGuideMap) ? matterGuideMap.get(info.getMatterId()) : "",
                    info.getUserName(),
                    info.getIdCard(), info.getPhone(),
                    MatterInfoStatusEnum.APPROVE.getStatus().equals(info.getStatus()) ? MatterInfoStatusEnum.APPROVE.getDec() : MatterInfoStatusEnum.UN_APPROVE.getDec(),
                    DateUtil.formatByPattern1(info.getCreateTime()));
            rows.add(dataList);
        }
        return rows;
    }

    private List<List<String>> buildExcelRows2(List<MatterGuideInfoPageVo> matterGuideInfos, Map<String, String> matterGuideMap) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(EXCEL_TITLE);
        for (MatterGuideInfoPageVo info : matterGuideInfos) {
            List<String> dataList = ListUtil.toList(
                    info.getMatterName(),
                    info.getUserName(),
                    info.getIdCard(),
                    info.getPhone(),
                    MatterInfoStatusEnum.APPROVE.getStatus().equals(info.getStatus()) ? MatterInfoStatusEnum.APPROVE.getDec() : MatterInfoStatusEnum.UN_APPROVE.getDec(),
                    info.getCreateTime());
            rows.add(dataList);
        }
        return rows;
    }

    private List<List<String>> buildExcelRows2New(List<JSONObject> matterGuideInfos, List<MatterGuideFiled> guideFiledList) {
        List<List<String>> rows = new ArrayList<>();
//        private static final List<String> EXCEL_TITLE = ListUtil.toList("事项类型", "姓名", "身份证号", "联系电话", "进度", "提交时间");
        List<String> headList = new ArrayList<>();
        headList.add("事项类型");
        headList.add("应用名称");
        if (!CollectionUtils.isEmpty(guideFiledList)) {
            for (MatterGuideFiled matterGuideFiled : guideFiledList) {
                headList.add(matterGuideFiled.getFiledName());
            }
        }
        headList.add("进度");
        headList.add("提交时间");
        rows.add(headList);
        for (JSONObject info : matterGuideInfos) {
            List<String> dataList = new ArrayList<>();
            dataList.add(info.get("matterName") + "");
            dataList.add(info.get("applicationName") + "");
            if (!CollectionUtils.isEmpty(guideFiledList)) {
                for (MatterGuideFiled matterGuideFiled : guideFiledList) {
                    dataList.add(info.get(matterGuideFiled.getFiledCode() + "") != null ? (info.get(matterGuideFiled.getFiledCode() + "") + "") : "");
                }
            }
            dataList.add(MatterInfoStatusEnum.APPROVE.getStatus().equals(info.get("status")) ? MatterInfoStatusEnum.APPROVE.getDec() : MatterInfoStatusEnum.UN_APPROVE.getDec());
            dataList.add(info.get("createTime") + "");
            rows.add(dataList);
        }
        return rows;
    }

    private void exportExcel(HttpServletResponse response, String fileName, List<List<String>> rows) {
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE); // 设置为二进制流类型
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\""); // 添加引号以支持中文文件名
            EasyExcelUtil.export(rows, outputStream);
        } catch (IOException e) {
            log.error("Excel导出失败", e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class InnerFormInfoVo implements Serializable {
        private String title;
        private String message;
        private List<FormItem> forms;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String createTime;
        @ApiModelProperty(value = "审批描述信息")
        private String remark;
        //private JSONObject params;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FormItem {
        private String basicInfo;
        private String type = "form";
        private String filedName;
        private Integer sorted;
        private String remark;
        private List<ConfigItem> configs;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ConfigItem {
        private String filedId;
        private String label;
        private String type;
        private String field;
        private String placeholder;
        private String value;
        private String prompt;
        private String checkRuleCode;
        private List<Option> options;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Option {
        private String filedId;
        private String id;
        private String label;
    }

    private ConfigItem buildConfigItem(MatterGuideFiled guideFiled, JSONObject params, MatterData guideDataMap) {
        ConfigItem configItem = new ConfigItem();
        configItem.setFiledId(guideFiled.getFiledId());
        configItem.setLabel(guideFiled.getFiledName());
        configItem.setType(guideFiled.getFormType());
        configItem.setField(guideFiled.getFiledCode());
        String value = guideDataMap.getString(guideFiled.getFiledCode());
        if (value != null && value.equals("")) {
            configItem.setValue(null);
        } else {
            configItem.setValue(value);
        }
        configItem.setPlaceholder(guideFiled.getTip());
        configItem.setCheckRuleCode(guideFiled.getCheckRuleCode());
        configItem.setPrompt(guideFiled.getPlaceholder());
        if (FormConstant.RADIO.equals(guideFiled.getFormType()) || FormConstant.SELECT.equals(guideFiled.getFormType())) {
            List<Option> options = buildRadioOptions(guideFiled);
            configItem.setOptions(options);
            params.put(guideFiled.getFiledCode(), "");
        } else if (FormConstant.MUTIL_INPUT.equals(guideFiled.getFormType())) {
            List<Option> options = buildMultilInputOptions(guideFiled, params);
            configItem.setOptions(options);
        } else {
            // 文件格式的前端要求一个数组参数
            if (FormConstant.FILE.equals(guideFiled.getFormType()) || FormConstant.IMAGE.equals(guideFiled.getFormType())) {
                params.put(guideFiled.getFiledCode(), new JSONArray());
            } else {
                params.put(guideFiled.getFiledCode(), "");
            }
        }
        return configItem;
    }

    private List<Option> buildMultilInputOptions(MatterGuideFiled guideFiled, JSONObject params) {
        QueryWrapper queryWrapper = new QueryWrapper().and(new QueryColumn("filed_id")
                .eq(guideFiled.getFiledId()));
        List<MatterGuideOption> optionList = matterGuideOptionMapper.selectListByQuery(queryWrapper);
        List<Option> options = new ArrayList<>();
        JSONArray items = new JSONArray();
        for (MatterGuideOption guideOption : optionList) {
            Option option = new Option();
            option.setFiledId(guideOption.getFiledId());
            option.setLabel(guideOption.getLable());
            options.add(option);
            JSONObject item = new JSONObject();
            item.put(guideFiled.getFiledCode(), "");
            items.add(item);
        }
        params.put(guideFiled.getFiledCode(), items);
        return options;
    }

    private List<Option> buildRadioOptions(MatterGuideFiled guideFiled) {
        QueryWrapper queryWrapper = new QueryWrapper().and(new QueryColumn("filed_id")
                .eq(guideFiled.getFiledId()));
        List<MatterGuideOption> optionList = matterGuideOptionMapper.selectListByQuery(queryWrapper);
        List<Option> options = new ArrayList<>();
        for (MatterGuideOption guideOption : optionList) {
            Option option = new Option();
            option.setFiledId(guideOption.getFiledId());
            option.setLabel(guideOption.getLable());
            options.add(option);
        }
        return options;
    }

    private ArrayList<List<MatterGuideFiled>> getGroup(List<MatterGuideFiled> arrayList) {
        // 用于存储分组的集合
        ArrayList<List<MatterGuideFiled>> groups = new ArrayList<>();

        // 临时变量，用于存储当前分组的对象列表
        ArrayList<MatterGuideFiled> currentGroup = new ArrayList<>();

        // 遍历 ArrayList 中的对象
        for (int i = 0; i < arrayList.size(); i++) {
            MatterGuideFiled obj = arrayList.get(i);
            // 如果当前对象是 "form" 类型或 "addForm" 类型，则当前分组结束，存储当前分组并开始新的分组
            if (FormConstant.FORM.equals(obj.getFormType()) ||
                    FormConstant.ADD_FORM.equals(obj.getFormType())) {
                if (!currentGroup.isEmpty()) {
                    // 存储当前分组
                    groups.add(currentGroup);
                    // 创建新的空分组
                    currentGroup = new ArrayList<>();
                }
            }
            // 将当前对象添加到当前分组中
            currentGroup.add(obj);
        }

        // 最后，确保最后一个分组也被存储
        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }
        return groups;
    }
}