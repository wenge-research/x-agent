package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.JsonArray;
import com.alibaba.nacos.shaded.com.google.gson.JsonElement;
import com.alibaba.nacos.shaded.com.google.gson.JsonParser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.DialogueConstant;
import com.wenge.model.dto.param.ApplicationInfoVersionParam;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.dto.result.ComponentNodeDto;
import com.wenge.model.entity.*;
import com.wenge.model.entity.table.DialogTemplateTableDef;
import com.wenge.model.enums.ApplicationKnowledgeTypeEnum;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.mapper.*;
import com.wenge.model.mapper.es.ApplicationInfoVersionIndexMapper;
import com.wenge.model.service.*;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.ComponentNodeRel;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentTypeEnum;
import com.wenge.model.workflow.enums.DirectionEnum;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.holder.ContextHolders;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import jodd.util.Consumers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryChainWrapper;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationInfoVersionTableDef.APPLICATION_INFO_VERSION;
import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;
import static com.wenge.model.entity.table.InterceptWordHouseApplicationRelTableDef.INTERCEPT_WORD_HOUSE_APPLICATION_REL;
import static com.wenge.model.entity.table.PresetQuestionTableDef.PRESET_QUESTION;
import static com.wenge.model.workflow.entity.table.ComponentNodeRelTableDef.COMPONENT_NODE_REL;
import static com.wenge.model.workflow.entity.table.ComponentNodeTableDef.COMPONENT_NODE;
import static com.wenge.model.workflow.entity.table.MetaParamTableDef.META_PARAM;

/**
 * Description: 应用版本信息服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
@Service
@Slf4j
public class ApplicationInfoVersionIndexServiceImpl implements ApplicationInfoVersionIndexService {
    @Resource
    private ApplicationInfoService applicationInfoService;
    @Autowired
    private DialogTemplateService templateService;
    @Autowired
    private ApplicationKnowledgeService applicationKnowledgeService;
    @Autowired
    private PresetQuestionService presetQuestionService;
    @Autowired
    private InterceptWordHouseApplicationRelService relService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private ApplicationUserConfigurationService applicationUserConfigurationService;
    @Resource
    private UserService userService;
    @Autowired
    private ComponentNodeMapper componentNodeMapper;
    @Autowired
    private MetaParamMapper metaParamMapper;
    @Autowired
    private ComponentMapper componentMapper;
    @Autowired
    private ComponentNodeRelMapper componentNodeRelMapper;
    @Autowired
    private ApplicationInfoVersionIndexMapper applicationInfoVersionIndexMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<EsPageInfo<ApplicationInfoVersionIndex>>getApplicationInfoList(ApplicationInfoVersionParam applicationversionInfo) {
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String tenantId =null;
        String userid=null;
        //String dept_id=null;
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            tenantId = oauthUser.getTenantId();
            userid= String.valueOf(oauthUser.getId());
            //dept_id=oauthUser.getDeptId();
            if(com.alibaba.cloud.commons.lang.StringUtils.isEmpty(oauthUser.getTenantId())){
                tenantId=tokenUserInfo.getTenantId();
            }
        }
        LambdaEsQueryWrapper<ApplicationInfoVersionIndex> wrapper= EsWrappers.lambdaQuery(ApplicationInfoVersionIndex.class)
                .eq(BeanUtil.isNotEmpty(applicationversionInfo.getApplicationInfoId()),ApplicationInfoVersionIndex::getApplicationInfoId,applicationversionInfo.getApplicationInfoId())
                // 版本记录默认要查看所有的数据
//                .eq(StringUtils.isNotEmpty(userid),ApplicationInfoVersionIndex::getUserId,userid)
                .between(BeanUtil.isNotEmpty(applicationversionInfo.getStartTime())&&BeanUtil.isNotEmpty(applicationversionInfo.getEndTime()),ApplicationInfoVersionIndex::getCreateTime,applicationversionInfo.getStartTime(),applicationversionInfo.getEndTime())
                .like(BeanUtil.isNotEmpty(applicationversionInfo.getPublishDesc()),ApplicationInfoVersionIndex::getPublishDesc,applicationversionInfo.getPublishDesc())
                .orderByDesc("createTime");
        EsPageInfo<ApplicationInfoVersionIndex> page = applicationInfoVersionIndexMapper.pageQuery(wrapper, applicationversionInfo.getPageNo(),
                applicationversionInfo.getPageSize());
        return Result.success(page);
    }

    @Override
    public ApplicationInfoVersionIndex getApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo) {
        LambdaEsQueryWrapper<ApplicationInfoVersionIndex> esWrapper = EsWrappers.lambdaQuery(ApplicationInfoVersionIndex.class)
                .eq(ApplicationInfoVersionIndex::getId,applicationversionInfo.getId());
        ApplicationInfoVersionIndex applicationInfoVersionIndex = applicationInfoVersionIndexMapper.selectOne(esWrapper);
        return applicationInfoVersionIndex;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo) {
        ApplicationInfoVersionIndex applicationInfoVersionIndex = new ApplicationInfoVersionIndex();
        applicationInfoVersionIndex.setId(String.valueOf(applicationversionInfo.getId()));
        applicationInfoVersionIndex.setPublishDesc(applicationversionInfo.getPublishDesc());
        applicationInfoVersionIndexMapper.updateById(applicationInfoVersionIndex);
        return Result.success();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo) {
        applicationInfoVersionIndexMapper.deleteById(applicationversionInfo.getId());
        return Result.success();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result backUpdateApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo) {
        // 查询历史的应用信息
        ApplicationInfo currentApp = applicationInfoService.getById(applicationversionInfo.getApplicationId());

        ApplicationInfoVersionIndex applicationVersionInfoIndex = getApplicationVersionInfo(applicationversionInfo);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        BeanUtils.copyProperties(applicationVersionInfoIndex, applicationInfo);
        applicationInfo.setId(Long.valueOf(applicationVersionInfoIndex.getApplicationInfoId()));
        // 回退不能修改创建人和租户
        applicationInfo.setCreateUser(currentApp.getCreateUser());
        applicationInfo.setTenantId(currentApp.getTenantId());

        //回退知识库和工作流关联关系版本记录knowledgeIds
        Gson gson = new Gson();
        JsonArray jsonArray =new JsonArray();
        if(BeanUtil.isNotEmpty(applicationVersionInfoIndex.getBindingKnowledgeIdsJson())){
            jsonArray = JsonParser.parseString(applicationVersionInfoIndex.getBindingKnowledgeIdsJson()).getAsJsonArray();
            List<ApplicationKnowledge> knowledgeIds = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                knowledgeIds.add(gson.fromJson(element, ApplicationKnowledge.class));
            }
            applicationInfo.setKnowledgeIds(knowledgeIds);
        }
        if(BeanUtil.isNotEmpty(applicationVersionInfoIndex.getBindingWorkflowIdsJson())) {
            jsonArray = JsonParser.parseString(applicationVersionInfoIndex.getBindingWorkflowIdsJson()).getAsJsonArray();
            List<ApplicationKnowledge> workflowIds = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                workflowIds.add(gson.fromJson(element, ApplicationKnowledge.class));
            }
            applicationInfo.setWorkflowIds(workflowIds);
        }
        //回退关联预设问题关联关系备份
        if(BeanUtil.isNotEmpty(applicationVersionInfoIndex.getBindingPresetQuestionJson())) {
            jsonArray = JsonParser.parseString(applicationVersionInfoIndex.getBindingPresetQuestionJson()).getAsJsonArray();
            List<String> presetQuestionList = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                presetQuestionList.add(gson.fromJson(element, String.class));
            }
            applicationInfo.setPresetQuestionList(presetQuestionList);
        }
        //回退关联敏敢词关联关系备份
        if(BeanUtil.isNotEmpty(applicationVersionInfoIndex.getBindingInterceptWordhoseJson())) {
            jsonArray = JsonParser.parseString(applicationVersionInfoIndex.getBindingInterceptWordhoseJson()).getAsJsonArray();
            List<Long> interceptWordHousesList = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                interceptWordHousesList.add(gson.fromJson(element, Long.class));
            }
            applicationInfo.setInterceptWordHouses(interceptWordHousesList);
        }
        //回退关联个性化关联关系备份
        if(BeanUtil.isNotEmpty(applicationVersionInfoIndex.getBindingConfigrationJson())) {
            String streamVoice = gson.fromJson(applicationVersionInfoIndex.getBindingConfigrationJson(), String.class);
            applicationInfo.setStreamVoice(streamVoice);
        }
        //回退节点备份
        if(ObjectUtil.isNotEmpty(applicationVersionInfoIndex.getNodesJson())&&ObjectUtil.isNotEmpty(applicationVersionInfoIndex.getComponentJson())){
            Component copy = gson.fromJson(applicationVersionInfoIndex.getComponentJson(), Component.class);
            save(copy);
            jsonArray = JsonParser.parseString(applicationVersionInfoIndex.getNodesJson()).getAsJsonArray();
            List<ComponentNodeDto> nodes = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                nodes.add(gson.fromJson(element, ComponentNodeDto.class));
            }
            if (CollUtil.isNotEmpty(nodes)) {
                componentNodeMapper.deleteByCondition(COMPONENT_NODE.COMPONENT_ID.eq(copy.getComponentId()));
                metaParamMapper.deleteByCondition(META_PARAM.NODE_ID.in(nodes.stream().map(ComponentNodeDto::getNodeId).collect(Collectors.toList())));
                // 保存组件节点信息
                nodes.forEach(node -> {
                    node.setComponentId(copy.getComponentId());
                    componentNodeMapper.insertOrUpdate(com.wg.appframe.core.utils.BeanUtil.copy(node, ComponentNode.class));
                    List<MetaParam> input = node.getInput();
                    // 保存节点参数元数据
                    input.forEach(param -> {
                        if (StrUtil.isBlank(param.getParamId())) {
                            param.setParamId(IdUtil.simpleUUID());
                        }
                        param.setNodeId(node.getNodeId());
                        param.setDirection(DirectionEnum.INPUT.getValue());
                        metaParamMapper.insertOrUpdate(param);
                    });
                    List<MetaParam> output = node.getOutput();
                    output.forEach(param -> {
                        if (StrUtil.isBlank(param.getParamId())) {
                            param.setParamId(IdUtil.simpleUUID());
                        }
                        param.setNodeId(node.getNodeId());
                        param.setDirection(DirectionEnum.OUTPUT.getValue());
                        metaParamMapper.insertOrUpdate(param);
                    });
                });
            }
        }
        //回退节点关系备份
        if(ObjectUtil.isNotEmpty(applicationVersionInfoIndex.getNodeRelJson())&&ObjectUtil.isNotEmpty(applicationVersionInfoIndex.getComponentJson())){
            ComponentDto copy = gson.fromJson(applicationVersionInfoIndex.getComponentJson(), ComponentDto.class);
            componentNodeRelMapper.deleteByCondition(COMPONENT_NODE_REL.COMPONENT_ID.eq(copy.getComponentId()));
            List<ComponentNodeRel> nodeRel = copy.getNodeRel();
            if (CollUtil.isNotEmpty(nodeRel)) {
                List<ComponentNodeRel> collect = nodeRel.stream().map(rel -> com.wg.appframe.core.utils.BeanUtil.copy(rel, ComponentNodeRel.class)).collect(Collectors.toList());
                collect.forEach(rel -> rel.setComponentId(copy.getComponentId()));
                componentNodeRelMapper.insertBatch(collect);
            }
        }
        //回退关联关系开始
        // 设置聊天模板
        setDialogTemplate(applicationInfo);

        // 关联知识库和工作流
        bindingKnowledge(applicationInfo);

        // 关联预设问题
        bindingPresetQuestion(applicationInfo);

        // 关联敏感词库
        bindingInterceptWordHouse(applicationInfo);

        // 关联个性化配置
        bindingConfiguration(applicationInfo);

        // 设置api接口地址
        if (StringUtils.isBlank(applicationInfo.getApi())) {
            applicationInfo.setApi(AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_DEFAULT_RELEASE_API));
        }

        // 前端未设置 answerTimeout 值时，设置默认值
        if (Objects.isNull(applicationInfo.getAnswerTimeout())) {
            applicationInfo.setAnswerTimeout(DialogueConstant.ANSWER_TIMEOUT);
        }
        //回退关联关系结束
        applicationInfoService.updateApplicationInfo(applicationInfo);
        /*ApplicationInfoVersion applicationInfoVersion = new ApplicationInfoVersion();
        applicationInfoVersion.setId(applicationversionInfo.getId());
        applicationInfoVersion.setBackVersionRemark(applicationversionInfo.getBackVersionRemark());*/
        ApplicationInfoVersionIndex applicationInfoVersionIndex = new ApplicationInfoVersionIndex();
        applicationInfoVersionIndex.setId(String.valueOf(applicationversionInfo.getId()));
        applicationInfoVersionIndex.setBackVersionRemark(applicationversionInfo.getBackVersionRemark());
        applicationInfoVersionIndexMapper.updateById(applicationInfoVersionIndex);
        return Result.success();
    }
    /**
     * 设置模板
     * @param applicationInfo
     */
    private void setDialogTemplate(ApplicationInfo applicationInfo) {
        // web端模板
        Wrappers<Object> templateWrappers = Wrappers.init()
                .where(DialogTemplateTableDef.DIALOG_TEMPLATE.TEMPLATE_ID.eq(applicationInfo.getTemplateId()))
                .limit(1);

        DialogTemplate template = templateService.getOne(templateWrappers);
        if (template != null) {
            applicationInfo.setTemplateRoute(template.getTemplateRoute());
        }

        // 移动端模板
        Wrappers<Object> templateRouteWrappers = Wrappers.init()
                .where(DialogTemplateTableDef.DIALOG_TEMPLATE.TEMPLATE_ID.eq(applicationInfo.getMobileTemplateId()))
                .limit(1);
        template = templateService.getOne(templateRouteWrappers);
        if (template != null) {
            applicationInfo.setMobileTemplateRoute(template.getTemplateRoute());
        }
    }
    /**
     * 关联知识库
     */
    private void bindingKnowledge(ApplicationInfo applicationInfo) {
        if (StrUtil.isNotBlank(applicationInfo.getApplicationId())) {
            // 清空关联表
            Wrappers<Object> wrappers = Wrappers.init()
                    .where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
                    .and(APPLICATION_KNOWLEDGE.TYPE.eq(ApplicationKnowledgeTypeEnum.KNOWLEDGE));
            applicationKnowledgeService.remove(wrappers);
        }
        // 新增关联表
        if (CollectionUtils.isNotEmpty(applicationInfo.getKnowledgeIds())) {
            List<ApplicationKnowledge> list = applicationInfo.getKnowledgeIds().stream()
                    .map(p -> {
                        ApplicationKnowledge applicationKnowledge = new ApplicationKnowledge();
                        applicationKnowledge.setApplicationId(applicationInfo.getApplicationId());
                        applicationKnowledge.setKnowledgeId(p.getKnowledgeId());
                        applicationKnowledge.setType(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType());
                        return applicationKnowledge;
                    }).collect(Collectors.toList());
            applicationKnowledgeService.saveBatch(list);
        }
    }
    /**
     * 关联预设问题
     */
    private void bindingPresetQuestion(ApplicationInfo applicationInfo) {
        Wrappers<Object> wrappers = Wrappers.init()
                .where(PRESET_QUESTION.APPLICATION_ID.eq(applicationInfo.getApplicationId()));
        presetQuestionService.remove(wrappers);
        if (CollectionUtils.isNotEmpty(applicationInfo.getPresetQuestionList())) {
            List<PresetQuestion> presetQuestionList = applicationInfo.getPresetQuestionList()
                    .stream().map(p -> {
                        PresetQuestion presetQuestion = new PresetQuestion();
                        presetQuestion.setPresetId(IdUtil.simpleUUID());
                        presetQuestion.setQuestion(p);
                        presetQuestion.setType("推荐问题");
                        presetQuestion.setStatus(1);
                        presetQuestion.setApplicationId(applicationInfo.getApplicationId());
                        presetQuestion.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                        presetQuestion.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                        return presetQuestion;
                    }).collect(Collectors.toList());
            presetQuestionService.saveBatch(presetQuestionList);
        }
    }
    private void bindingInterceptWordHouse(ApplicationInfo applicationInfo) {
        String applicationId = applicationInfo.getApplicationId();
        List<Long> houseIds = applicationInfo.getInterceptWordHouses();
        Assert.isTrue(StringUtils.isNotBlank(applicationId), "应用ID不能为空");
        // 重新关联敏感词库
        relService.remove(Wrappers.init().where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(applicationId)));
        if (CollectionUtils.isEmpty(houseIds)) {
            return;
        }
        publisher.publishEvent(new SceneEvent(null));
        houseIds.stream().map(id -> {
            InterceptWordHouseApplicationRel rel = new InterceptWordHouseApplicationRel();
            rel.setApplicationId(applicationId);
            rel.setInterceptWordHouseId(id);
            return rel;
        }).forEach(relService::addInterceptWordHouseApplicationRel);
    }
    private void bindingConfiguration(ApplicationInfo applicationInfo) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        Long currentUserId = tokenOauthUserInfo.getId();
        Assert.notNull(currentUserId, "用户未登录");
        String streamVoice = applicationInfo.getStreamVoice();
        List<ApplicationUserConfiguration> configurations = applicationUserConfigurationService.selectByUserIdAndAppId(currentUserId, applicationInfo.getApplicationId());
        if (CollectionUtils.isEmpty(configurations)) {
            return;
        }
        ApplicationUserConfiguration configuration = configurations.get(0);
        if (StringUtils.equals(streamVoice, "是")) {
            configuration.setStreamVoice(1);
        } else {
            configuration.setStreamVoice(0);
        }
        applicationUserConfigurationService.saveOrUpdate(configuration);
    }
    public Result<Component> save(Component component) {

        if (StrUtil.isBlank(component.getComponentId())) {
            String componentId = ComponentServiceImpl.getComponentId(component);
            component.setComponentId(componentId);
        }
        if (component.getStatus() == null) {
            component.setStatus(0);
        }
        TokenUser info = ContextHolders.getTokenUserInfo();
        cn.hutool.core.lang.Assert.notNull(info, "用户未登录");
        if (StrUtil.isBlank(component.getCreateUser())) {
            component.setCreateUser(info.getAccountName());
        }
        if (StrUtil.isBlank(component.getCreateTime())) {
            component.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
        }
        if (StrUtil.isBlank(component.getTenantId())) {
            component.setTenantId(StrUtil.isBlank(info.getTenantId()) ? "" : info.getTenantId());
        }
        if (component.getType() == null) {
            component.setType(ComponentTypeEnum.WORKFLOW.getCode());
        }
        component.setUpdateTime(DateUtil.getCurrentTime(DateUtil.DEFAULT_FORMAT));
        component.setUpdateUser(info.getAccountName());
        componentMapper.insertOrUpdate(component);

        component.setApplicationInfo(applicationInfoService.getByAppId(component.getComponentId()));
        return Result.success(component);
    }
}