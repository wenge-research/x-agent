package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.FormConstant;
import com.wenge.model.dto.param.MatterFiledInfoParam;
import com.wenge.model.dto.result.AllFieldsResult;
import com.wenge.model.dto.result.MatterGuideFormResult;
import com.wenge.model.entity.MatterGuide;
import com.wenge.model.entity.MatterGuideFiled;
import com.wenge.model.entity.MatterGuideGroup;
import com.wenge.model.entity.MatterGuideOption;
import com.wenge.model.mapper.MatterGuideFiledMapper;
import com.wenge.model.mapper.MatterGuideGroupMapper;
import com.wenge.model.mapper.MatterGuideOptionMapper;
import com.wenge.model.service.MatterGuideFiledService;
import com.wenge.model.service.MatterGuideGroupService;
import com.wenge.model.service.MatterGuideService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.MatterGuideFiledTableDef.MATTER_GUIDE_FILED;
import static com.wenge.model.entity.table.MatterGuideGroupTableDef.MATTER_GUIDE_GROUP;
import static com.wenge.model.entity.table.MatterGuideOptionTableDef.MATTER_GUIDE_OPTION;
import static com.wenge.model.entity.table.MatterGuideTableDef.MATTER_GUIDE;


@Service
@Slf4j
public class MatterGuideFiledServiceImpl extends ServiceImpl<MatterGuideFiledMapper, MatterGuideFiled> implements MatterGuideFiledService {

    @Autowired
    private MatterGuideFiledMapper matterGuideFiledMapper;

    @Autowired
    private MatterGuideOptionMapper matterGuideOptionMapper;

    @Autowired
    private MatterGuideGroupMapper matterGuideGroupMapper;

    @Autowired
    private MatterGuideService matterGuideService;

    @Autowired
    private MatterGuideGroupService matterGuideGroupService;

    @Autowired
    private WosUtil wosUtil;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Override
    public Result getFormInfo(MatterFiledInfoParam param) {
        String matterId = param.getMatterId();
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId))
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .orderBy(MATTER_GUIDE_FILED.SORTED.asc());
        List<MatterGuideFiled> guideFileds = matterGuideFiledMapper.selectListByQuery(queryWrapper);
        if (CollectionUtils.isEmpty(guideFileds)) {
            return Result.success(new InnerFormInfoVo());
        }

        Wrappers<Object> limit = Wrappers.init()
                .where(MATTER_GUIDE.MATTER_ID.eq(matterId))
                .limit(1);
        MatterGuide matterGuide = matterGuideService.getOne(limit);
        if (null == matterGuide) {
            return Result.success(new InnerFormInfoVo());
        }
        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();
        List<MatterGuideFiled> opList = new ArrayList<>();
        // 处理页面头部信息，并且移除走，后续不处理
        for (MatterGuideFiled guideFiled : guideFileds) {
            if("title".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setTitle(guideFiled.getTip());
            }else if("message".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setMessage(guideFiled.getTip());
            }else {
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
                if((FormConstant.FORM.equals(guideFiled.getFormType())
                        || FormConstant.ADD_FORM.equals(guideFiled.getFormType()))){
                    formItem.setBasicInfo(guideFiled.getTip());
                    formItem.setFiledName(guideFiled.getFiledCode());
                    formItem.setType(guideFiled.getFormType());

                    if(FormConstant.ADD_FORM.equals(guideFiled.getFormType())){
                        params.put(guideFiled.getFiledCode(),new JSONArray());
                    }
                    continue;
                }
                // 构建configs
                configs.add(buildConfigItem(guideFiled,params));
            }
            formItem.setConfigs(configs);
            forms.add(formItem);
        }
        innerFormInfoVo.setForms(forms);
        innerFormInfoVo.setParams(params);
        innerFormInfoVo.setMatterGuide(matterGuide);
        return Result.success(innerFormInfoVo);
    }

    @Override
    public Result getFormInfoNew(MatterFiledInfoParam param) {
        String matterId = param.getMatterId();
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId))
                //.and(MATTER_GUIDE_FILED.FORM_SHOW_FLAG.eq("是"))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP_ID.isNotNull())
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .orderBy(MATTER_GUIDE_FILED.SORTED.asc());
        List<MatterGuideFiled> guideFileds = matterGuideFiledMapper.selectListByQuery(queryWrapper);
        if (CollectionUtils.isEmpty(guideFileds)) {
            return Result.success(new InnerFormInfoVo());
        }


        Wrappers<Object> limit = Wrappers.init()
                .where(MATTER_GUIDE.MATTER_ID.eq(matterId))
                .limit(1);
        MatterGuide matterGuide = matterGuideService.getOne(limit);
        if (null == matterGuide) {
            return Result.success(new InnerFormInfoVo());
        }
        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();
        List<MatterGuideFiled> opList = new ArrayList<>();
        // 处理页面头部信息，并且移除走，后续不处理
        for (MatterGuideFiled guideFiled : guideFileds) {
            if("title".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setTitle(guideFiled.getTip());
            }else if("message".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setMessage(guideFiled.getTip());
            }else {
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
                List<ConfigItem> configs = new ArrayList<>();
                FormItem formItem = new FormItem();
                for (MatterGuideFiled matterGuideFiled  : matterGuideFiledList) {
                    configs.add(buildConfigItem(matterGuideFiled,params));
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
//                formItem.setType(matterGuideGroup.getFormType());
                forms.add(formItem);
            }
            innerFormInfoVo.setForms(forms);
            innerFormInfoVo.setParams(params);
            innerFormInfoVo.setMatterGuide(matterGuide);
        return Result.success(innerFormInfoVo);
    }


    @Override
    public Result uploadPic(MultipartFile file) throws WosException {
        MinioInfoResult infoResult = wosUtil.upload(bucket, "matterGuide", file, true);
        return Result.success(infoResult.getUrlPath());
    }

    @Override
    public Result uploadYYZZPic(MultipartFile file) throws WosException {
        MinioInfoResult infoResult = wosUtil.upload(bucket, "matterGuide", file, true);
        return Result.success(infoResult.getUrlPath());
    }

    @Override
    public MinioInfoResult uploadFile(MultipartFile file, String filName) {
        if (file.isEmpty()) {
            log.warn("上传文件为空");
            return null;
        }
        MinioInfoResult infoResult = null;
        try {
            infoResult = wosUtil.upload(bucket, "matterGuide", file.getInputStream(), filName, true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WosException e) {
            e.printStackTrace();
        }
        return infoResult;
    }

    @Override
    public Result getTableFields(StringParam matterId) {
        if (StringUtils.isBlank(matterId.getParam())) {
            return Result.success();
        }
        List<MatterGuideFiled> lists = MatterGuideFiled.create()
                .where(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId.getParam()))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .lists();
        List<MatterGuideFiled> guideFields = lists.stream()
                .filter(p -> p.getTableShowFlag().equals(YesNoEnum.YES.getName()))
                .sorted(Comparator.comparing(MatterGuideFiled::getSorted))
                .collect(Collectors.toList());
        return Result.success(guideFields);
    }

    @Override
    public Result getSearchFields(StringParam matterId) {
        List<MatterGuideFiled> lists = MatterGuideFiled.create()
                .where(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId.getParam()))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .lists();

        List<String> filedIdList = lists.stream().map(MatterGuideFiled::getFiledId).collect(Collectors.toList());

        // 根据字段id获取选项
        List<MatterGuideOption> guideOptions = MatterGuideOption.create()
                .where(MATTER_GUIDE_OPTION.FILED_ID.in(filedIdList))
                .and(MATTER_GUIDE_OPTION.DELETED_FLAG.eq(0))
                .lists();

        Map<String, List<MatterGuideOption>> optionGroup = guideOptions.stream().collect(Collectors.groupingBy(MatterGuideOption::getFiledId));

        // 筛选出检索的字段，封装字段的选项
        List<MatterGuideFiled> guideFields = lists.stream()
                .filter(p -> p.getSearchFlag().equals(YesNoEnum.YES.getName()))
                .map(p->p.setOptionList(optionGroup.get(p.getFiledId())))
                .sorted(Comparator.comparing(MatterGuideFiled::getSorted))
                .collect(Collectors.toList());
        return Result.success(guideFields);
    }

    @Override
    public Result<List<AllFieldsResult>> getAllFields(StringParam matterId) {
        // 根据事项id获取所有字段
        List<MatterGuideFiled> lists = MatterGuideFiled.create()
                .where(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId.getParam()))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .lists();

        List<String> filedIdList = lists.stream().map(MatterGuideFiled::getFiledId).collect(Collectors.toList());

        // 根据字段id获取选项
        List<MatterGuideOption> guideOptions = MatterGuideOption.create()
                .where(MATTER_GUIDE_OPTION.FILED_ID.in(filedIdList))
                .and(MATTER_GUIDE_OPTION.DELETED_FLAG.eq(0))
                .lists();

        Map<String, List<MatterGuideOption>> optionGroup = guideOptions.stream().collect(Collectors.groupingBy(MatterGuideOption::getFiledId));

        // 封装字段的选项
        Map<String, List<MatterGuideFiled>> groupBy = lists.stream()
                .map(p -> p.setOptionList(optionGroup.get(p.getFiledId())))
                .collect(Collectors.groupingBy(MatterGuideFiled::getFiledCodeGroup, Collectors.collectingAndThen(Collectors.toList(), list -> {
                    return list.stream().sorted(Comparator.comparing(MatterGuideFiled::getSorted)).collect(Collectors.toList());
                })));
        List<AllFieldsResult> allFieldsResults = new ArrayList<>();
        groupBy.forEach((group, list) -> {
            AllFieldsResult allFieldsResult = new AllFieldsResult();
            allFieldsResult.setGroup(group);
            allFieldsResult.setFiledList(list);
            allFieldsResults.add(allFieldsResult);
        });

        return Result.success(allFieldsResults);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addFields(List<MatterGuideFiled> filedList) {
        if (CollectionUtils.isEmpty(filedList)) {
            return Result.success();
        }

        boolean anyMatch = filedList.stream().anyMatch(p -> StringUtils.isBlank(p.getMatterId()));
        if (anyMatch) {
            return Result.fail("事项id不能为空");
        }

        String matterId = filedList.get(0).getMatterId();
        // 删除旧数据
        MatterGuideFiled.create()
                .where(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .remove();
        filedList.forEach(p -> p.setFiledId(IdUtil.simpleUUID()));
        // 保存新数据
        saveBatch(filedList);

        filedList.stream().forEach(item->{
            if (!CollectionUtils.isEmpty(item.getOptionList())) {
                item.getOptionList().forEach(option -> {
                    option.setFiledId(item.getFiledId());
                });
            }
        });
        List<MatterGuideOption> optionList = filedList.stream().flatMap(p -> {
            if (!CollectionUtils.isEmpty(p.getOptionList())) {
                p.getOptionList().forEach(option -> {
                    option.setFiledId(p.getFiledId());
                });
                return p.getOptionList().stream();
            }
            List<MatterGuideOption> list = Lists.newArrayList();
            return list.stream();
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(optionList)) {
            matterGuideOptionMapper.insertBatch(optionList);
        }
        return Result.success();
    }

    @Override
    public Result checkNameCodeExists(MatterFiledInfoParam param) {
        if(StringUtils.isBlank(param.getMatterId())){
            return Result.fail("事项ID不能为空");
        }
        if(StringUtils.isBlank(param.getFiledCode())){
            return Result.fail("filedCode不能为空");
        }

        //判断是否存在code
        final boolean exists = MatterGuideFiled.create()
                .where(MATTER_GUIDE_FILED.MATTER_ID.eq(param.getMatterId()))
                .and(MATTER_GUIDE_FILED.FILED_CODE.eq(param.getFiledCode())).and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0)).exists();
        if(exists){
            return Result.fail("字段名已存在");
        }
        return Result.success();
    }

    @Override
    public Result<MatterGuideFormResult> getMatterGuideForm(MatterFiledInfoParam param) {
        MatterGuideFormResult matterGuideFormResult = new MatterGuideFormResult();
        if (StringUtils.isBlank(param.getMatterId())) {
            return Result.success(matterGuideFormResult);
        }

        // 获取事项基本信息
        MatterGuide matterGuide = matterGuideService.getMatterByMatterId(param.getMatterId());
        if (null == matterGuide) {
            return Result.success(matterGuideFormResult);
        }
        matterGuideFormResult.setMatterGuide(matterGuide);

        // 获取字段的分组信息
        List<MatterGuideGroup> groupList = matterGuideGroupService.getByMatterId(param.getMatterId());
        List<Long> groupIdList = groupList.stream().map(MatterGuideGroup::getId).distinct().collect(Collectors.toList());

        // 这里添加1个随机id，防止为空时查询所有
        groupIdList.add(-1L);
        List<MatterGuideFiled> filedList = Wrappers.of(mapper)
                .where(MATTER_GUIDE_FILED.FILED_CODE_GROUP_ID.in(groupIdList))
                .list();
        Map<String, List<MatterGuideFiled>> groupCodeIdMap = filedList.stream().collect(Collectors.groupingBy(MatterGuideFiled::getFiledCodeGroupId));

        Map<String, List<MatterGuideOption>> optionMap = Maps.newHashMap();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(filedList)) {
            List<String> filedIdList = filedList.stream().map(MatterGuideFiled::getFiledId).distinct().collect(Collectors.toList());
            filedIdList.add("-1");
            List<MatterGuideOption> optionList = Wrappers.of(matterGuideOptionMapper)
                    .where(MATTER_GUIDE_OPTION.FILED_ID.in(filedIdList))
                    .list();
            optionMap.putAll(optionList.stream().collect(Collectors.groupingBy(MatterGuideOption::getFiledId)));
        }


        // 封装分组信息
        List<MatterGuideFormResult.FormConfig> configList = groupList.stream().map(group -> {
            MatterGuideFormResult.FormConfig formConfig = new MatterGuideFormResult.FormConfig();
            formConfig.setGroup(group);
            List<MatterGuideFiled> matterGuideFileds = groupCodeIdMap.get(group.getId() + StringConstant.BLANK);
            matterGuideFileds.forEach(filed -> {
                filed.setOptionList(optionMap.getOrDefault(filed.getFiledId(), Lists.newArrayList()));
            });
            formConfig.setFiledList(matterGuideFileds);
            return formConfig;
        }).collect(Collectors.toList());
        matterGuideFormResult.setFormConfigList(configList);
        return Result.success(matterGuideFormResult);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class InnerFormInfoVo implements Serializable {
        private String title;
        private String message;
        private MatterGuide matterGuide;
        private List<FormItem> forms;
        private JSONObject params;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FormItem{
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
    static class ConfigItem{
        private String filedId;
        private String label;
        private String type;
        private String field;
        private String placeholder;
        private String value;
        private String prompt;
        private String tip;
        private String webFormStatus;
        private String clientFormStatus;
        private String checkRuleCode;
        private List<Option> options;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Option{
        private String filedId;
        private String id;
        private String label;
    }

    private ConfigItem buildConfigItem(MatterGuideFiled guideFiled,JSONObject params) {
        ConfigItem configItem = new ConfigItem();
        configItem.setFiledId(guideFiled.getFiledId());
        configItem.setLabel(guideFiled.getFiledName());
        configItem.setType(guideFiled.getFormType());
        configItem.setField(guideFiled.getFiledCode());
        configItem.setPlaceholder(guideFiled.getPlaceholder());
        configItem.setTip(guideFiled.getTip());
        configItem.setCheckRuleCode(guideFiled.getCheckRuleCode());
        configItem.setPrompt(guideFiled.getRequiredTips());
        configItem.setWebFormStatus(guideFiled.getWebFormStatus());
        configItem.setClientFormStatus(guideFiled.getClientFormStatus());
        if(FormConstant.RADIO.equals(guideFiled.getFormType()) || FormConstant.SELECT.equals(guideFiled.getFormType())){
            List<Option> options = buildRadioOptions(guideFiled);
            configItem.setOptions(options);
            params.put(guideFiled.getFiledCode(),"");
        } else if(FormConstant.MUTIL_INPUT.equals(guideFiled.getFormType())){
            List<Option> options = buildMultilInputOptions(guideFiled,params);
            configItem.setOptions(options);
        }else {
            // 文件格式的前端要求一个数组参数
            if (FormConstant.FILE.equals(guideFiled.getFormType()) || FormConstant.IMAGE.equals(guideFiled.getFormType())) {
                params.put(guideFiled.getFiledCode(), new JSONArray());
            } else {
                params.put(guideFiled.getFiledCode(), "");
            }
        }
        return configItem;
    }

    private List<Option> buildMultilInputOptions(MatterGuideFiled guideFiled,JSONObject params) {
        QueryWrapper queryWrapper = new QueryWrapper().and(new QueryColumn("filed_id")
                .eq(guideFiled.getFiledId())).and(new QueryColumn("deleted_flag").eq(0));
        List<MatterGuideOption> optionList = matterGuideOptionMapper.selectListByQuery(queryWrapper);
        List<Option> options = new ArrayList<>();
        JSONArray items = new JSONArray();
        for (MatterGuideOption guideOption : optionList) {
            Option option = new Option();
            option.setFiledId(guideOption.getFiledId());
            option.setLabel(guideOption.getLable());
            options.add(option);
            JSONObject item = new JSONObject();
            item.put(guideFiled.getFiledCode(),"");
            items.add(item);
        }
        params.put(guideFiled.getFiledCode(),items);
        return options;
    }

    private List<Option> buildRadioOptions(MatterGuideFiled guideFiled) {
        QueryWrapper queryWrapper = new QueryWrapper().and(new QueryColumn("filed_id")
                .eq(guideFiled.getFiledId())).and(new QueryColumn("deleted_flag").eq(0));;
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

    private ArrayList<List<MatterGuideFiled>> getGroup(List<MatterGuideFiled> arrayList){
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