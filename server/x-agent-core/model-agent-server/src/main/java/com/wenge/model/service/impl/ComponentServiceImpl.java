package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.dto.result.ComponentNodeDto;
import com.wenge.model.entity.*;
import com.wenge.model.enums.ApplicationTypeEnum;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.enums.VariableStatusEnum;
import com.wenge.model.event.TaskManageEvent;
import com.wenge.model.mapper.*;
import com.wenge.model.mapper.es.ApplicationInfoVersionIndexMapper;
import com.wenge.model.service.*;
import com.wenge.model.utils.SseEmitterUtils;
import com.wenge.model.workflow.entity.*;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.model.workflow.enums.ComponentTypeEnum;
import com.wenge.model.workflow.enums.DirectionEnum;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wenge.oauth.entity.GrantData;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.holder.ContextHolders;
import com.wenge.oauth.mapper.GrantDataMapper;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wenge.oauth.util.RequestUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.core.utils.BeanUtil;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;
import static com.wenge.model.workflow.constants.SettingConstants.TRIGGER_CONFIG;
import static com.wenge.model.workflow.entity.table.ComponentNodeRelTableDef.COMPONENT_NODE_REL;
import static com.wenge.model.workflow.entity.table.ComponentNodeTableDef.COMPONENT_NODE;
import static com.wenge.model.workflow.entity.table.ComponentTableDef.COMPONENT;
import static com.wenge.model.workflow.entity.table.MetaParamTableDef.META_PARAM;
import static com.wenge.oauth.entity.table.GrantDataTableDef.GRANT_DATA;
import static com.wenge.oauth.enums.OwnerTypeEnum.*;

@Service
@Slf4j
public class ComponentServiceImpl implements ComponentService {
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR =
            new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ComponentNodeMapper componentNodeMapper;

    @Autowired
    private MetaParamMapper metaParamMapper;

    @Autowired
    private ComponentNodeRelMapper componentNodeRelMapper;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private DialogueService dialogueService;

    @Autowired
    private PresetQuestionService presetQuestionService;


    @Autowired
    private ServerPublishAuditService serverPublishAuditService;

    @Autowired
    private ApplicationPublishRecordService applicationPublishRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private TriggerConfigService triggerConfigService;

    @Value("classpath:plugin/code/init_data.json")
    private Resource dataResource;
    @Autowired
    private ApplicationInfoVersionMapper applicationInfoVersionMapper;
    @Autowired
    private ApplicationInfoVersionIndexMapper applicationInfoVersionIndexMapper;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private GrantDataMapper grantDataMapper;

    @Value("${agentX.workFlowApi}")
    private String workFlowApi;

    @Value("${agentX.runNodeApi}")
    private String runNodeApi;

    private static final ConcurrentHashMap<String, ComponentDto> COMPONENT_MAP = new ConcurrentHashMap<>();

    @Override
    public List<ComponentDto> selectById(List<String> componentIds) {
        componentIds = componentIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<ApplicationInfo> activeAppList = applicationInfoService.getActiveApp(componentIds);
        Map<String, ApplicationInfo> appMap = activeAppList.stream().collect(Collectors.toMap(
                ApplicationInfo::getApplicationId,
                p -> p,
                (k1, k2) -> k1
        ));

        componentIds = componentIds.stream().map(p -> {
            if (!p.contains("_")) {
                ApplicationInfo applicationInfo = appMap.get(p);
                if (null != applicationInfo) {
                    String type = applicationInfo.getType();
                    if (ApplicationTypeEnum.WORKFLOW.getName().equals(type)) {
                        return p + "_" + ComponentTypeEnum.APPLICATION.getCode();
                    } else if (ApplicationTypeEnum.DIALOGUE.getName().equals(type)) {
                        return p + "_" + ComponentTypeEnum.APPLICATION_FLOW.getCode();
                    }
                }
            }
            return p;
        }).collect(Collectors.toList());
        if (componentIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper where = QueryWrapper.create().where(COMPONENT.COMPONENT_ID.in(componentIds));
        List<Component> components = componentMapper.selectListByQuery(where);
        if (CollUtil.isEmpty(components)) {
            //throw new RuntimeException("未找到组件" + componentIds);
            return null;
        }

        List<ComponentNodeRel> allRel = componentNodeRelMapper.selectListByQuery(QueryWrapper.create().where(COMPONENT_NODE_REL.COMPONENT_ID.in(componentIds)));
        Map<String, List<ComponentNodeRel>> relMap = allRel.stream().collect(Collectors.groupingBy(ComponentNodeRel::getComponentId));

        List<ComponentNode> allComponentNodes = componentNodeMapper.selectListByQuery(QueryWrapper.create().where(COMPONENT_NODE.COMPONENT_ID.in(componentIds)));
        Map<String, List<ComponentNode>> componentNodeMap = allComponentNodes.stream().collect(Collectors.groupingBy(ComponentNode::getComponentId));

        List<String> allNodeIdList = allComponentNodes.stream().map(ComponentNode::getNodeId).distinct().collect(Collectors.toList());
        List<MetaParam> allMetaParams = metaParamMapper.selectListByQuery(QueryWrapper.create().where(META_PARAM.NODE_ID.in(allNodeIdList)));

        // List<ApplicationInfo> activeAppList = applicationInfoService.getActiveApp(componentIds);
        List<ComponentDto> componentDtoList = Lists.newArrayList();
        for (Component component : components) {
            ComponentDto dto = BeanUtil.copy(component, ComponentDto.class);
            ComponentTypeEnum type = ComponentTypeEnum.getByCode(component.getType());
            dto.setType(type.getCode());
            componentDtoList.add(dto);
            dto.setNodeRel(relMap.get(component.getComponentId()));
            // List<ComponentNode> componentNodes = componentNodeMapper.selectListByQuery(QueryWrapper.create().where(COMPONENT_NODE.COMPONENT_ID.eq(componentId)));
            List<ComponentNode> componentNodes = componentNodeMap.getOrDefault(component.getComponentId(), Lists.newArrayList());
            List<ComponentNodeDto> nodes = componentNodes.stream().map(node -> BeanUtil.copy(node, ComponentNodeDto.class)).collect(Collectors.toList());
            HashMap<String, ComponentNode> nodeMap = componentNodes.stream().collect(Collectors.toMap(
                    ComponentNode::getNodeId,
                    p -> p,
                    (k1, k2) -> k1,
                    Maps::newHashMap
            ));
            // List<MetaParam> metaParams = metaParamMapper.selectListByQuery(QueryWrapper.create().where(META_PARAM.NODE_ID.in(componentNodes.stream().map(ComponentNode::getNodeId).collect(Collectors.toList()))));
            List<String> nodeIdList = componentNodes.stream().map(ComponentNode::getNodeId).distinct().collect(Collectors.toList());
            List<MetaParam> metaParams = allMetaParams.stream().filter(p -> nodeIdList.contains(p.getNodeId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(metaParams)) {
                metaParams.forEach(item -> {
                    ComponentNode componentNode = nodeMap.get(item.getReferenceNodeId());
                    if (null != componentNode) {
                        Integer nodeType = componentNode.getNodeType();
                        ComponentNodeEnum byCode = ComponentNodeEnum.getByCode(nodeType);
                        item.setNodeType(byCode.getCode());
                    }
                    item.setStatus(VariableStatusEnum.UNINITIALIZED.getCode());

                    // 输入参数为非应用的情况下，默认是有初始值的，所以这里设置初始化完成状态
                    if (DirectionEnum.INPUT.getValue().equals(item.getDirection()) && !MetaParamEnum.REFERENCE.getName().equals(item.getValueType())) {
                        item.setStatus(VariableStatusEnum.COMPLETE.getCode());
                    }
                });
            }
            nodes.forEach(node -> {
                node.setInput(metaParams.stream().filter(param -> Objects.equals(param.getDirection(), DirectionEnum.INPUT.getValue()) && node.getNodeId().equals(param.getNodeId())).collect(Collectors.toList()));
                node.setOutput(metaParams.stream().filter(param -> Objects.equals(param.getDirection(), DirectionEnum.OUTPUT.getValue()) && node.getNodeId().equals(param.getNodeId())).collect(Collectors.toList()));
            });
            dto.setNodes(nodes);
            dto.setMetaParams(metaParams);
            String componentId = component.getComponentId();
            if (componentId.contains("_")) {
                componentId = componentId.substring(0, component.getComponentId().indexOf("_"));
            }
            // ApplicationInfo activeApp = applicationInfoService.getActiveApp(component.getComponentId(), StringConstant.BLANK);
            ApplicationInfo activeApp = appMap.get(componentId);
            dto.setApplicationInfo(activeApp);
        }

        return componentDtoList;
    }

    @Override
    public Result<Component> save(Component component) {

        if (StrUtil.isBlank(component.getComponentId())) {
            String componentId = ComponentServiceImpl.getComponentId(component);
            component.setComponentId(componentId);
        }
        if (component.getStatus() == null) {
            component.setStatus(0);
        }
        TokenUser info = AppContextHolder.getTokenUserInfo();
        Assert.notNull(info, "用户未登录");
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
        componentMapper.insertOrUpdateSelective(component);

        component.setApplicationInfo(applicationInfoService.getByAppId(component.getComponentId()));
        return Result.success(component);
    }

    @Override
    @Transactional
    public Result draft(ComponentDto component) {
        Component copy = BeanUtil.copy(component, Component.class);
        save(copy);
        List<ComponentNodeDto> nodes = component.getNodes();
        boolean allMatch = nodes.stream().allMatch(p -> StringUtils.isNotBlank(p.getNodeId()));
        if (!allMatch) {
            return Result.success();
        }
        ApplicationInfo applicationInfo = component.getApplicationInfo();
        if (CollUtil.isNotEmpty(nodes)) {
            componentNodeMapper.deleteByCondition(COMPONENT_NODE.COMPONENT_ID.eq(copy.getComponentId()));
            metaParamMapper.deleteByCondition(META_PARAM.NODE_ID.in(nodes.stream().map(ComponentNodeDto::getNodeId).collect(Collectors.toList())));
            // 保存组件节点信息
            nodes.forEach(node -> {
                node.setId(null);
                node.setComponentId(copy.getComponentId());
                componentNodeMapper.insertOrUpdate(BeanUtil.copy(node, ComponentNode.class));
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

                // 保存触发器配置
                JSONObject settings = JSONUtil.parseObj(node.getSettings());
                if (!JSONUtil.isNull(settings)) {
                    TriggerConfig triggerConfig = JSONUtil.toBean(settings.getJSONObject(TRIGGER_CONFIG), TriggerConfig.class);
                    if (ObjectUtil.isNotEmpty(triggerConfig)) {
                        TriggerConfigParam triggerConfigParam = new TriggerConfigParam();
                        triggerConfigParam.setComponentId(copy.getComponentId());
                        String applicationId = StringUtils.EMPTY;
                        if (ObjectUtil.isNotEmpty(applicationInfo)) {
                            applicationId = applicationInfo.getApplicationId();
                        }
                        triggerConfigParam.setApplicationId(applicationId);
                        TriggerConfig triggerConfigOld = (TriggerConfig) triggerConfigService.queryDetails(triggerConfigParam).getData();
                        String currentDateString = DateUtil.getCurrentDateString();
                        boolean isNew = false;
                        if (ObjectUtil.isNotEmpty(triggerConfigOld)) {
                            triggerConfig.setId(triggerConfigOld.getId());
                            triggerConfig.setTriggerId(triggerConfigOld.getTriggerId());
                        } else {
                            isNew = true;
                            triggerConfig.setTriggerId(IdUtil.simpleUUID());
                            triggerConfig.setCreatedTime(currentDateString);
                        }
                        triggerConfig.setComponentId(node.getComponentId());
                        triggerConfig.setApplicationId(applicationId);
                        triggerConfig.setComponentId(copy.getComponentId());
                        triggerConfig.setCreateUser(copy.getCreateUser());
                        triggerConfig.setUpdateUser(copy.getUpdateUser());
                        triggerConfig.setUpdatedTime(currentDateString);

                        triggerConfig = (TriggerConfig)triggerConfigService.addOrUpdate(triggerConfig).getData();
                        // 发布事件
                        TaskManageEvent.ChangeType type = isNew ? TaskManageEvent.ChangeType.CREATE : TaskManageEvent.ChangeType.UPDATE;
                        publisher.publishEvent(new TaskManageEvent(this, triggerConfig, type));
                    }
                    settings.set(TRIGGER_CONFIG, triggerConfig);
                    node.setSettings(JSONUtil.toJsonStr(settings));
                }

            });
        }
        // 保存组件节点关系信息
        componentNodeRelMapper.deleteByCondition(COMPONENT_NODE_REL.COMPONENT_ID.eq(copy.getComponentId()));
        List<ComponentNodeRel> nodeRel = component.getNodeRel();
        if (CollUtil.isNotEmpty(nodeRel)) {
            List<ComponentNodeRel> collect = nodeRel.stream().map(rel -> BeanUtil.copy(rel, ComponentNodeRel.class)).collect(Collectors.toList());
            collect.forEach(rel -> rel.setComponentId(copy.getComponentId()));
            componentNodeRelMapper.insertBatch(collect);
        }

        Optional.ofNullable(applicationInfo).ifPresent(app -> applicationInfoService.addApplicationInfo(app));

        //工作流版本节点数据备份
        if(ObjectUtil.isNotEmpty(applicationInfo.getMakeType())&&applicationInfo.getMakeType()!=""&&applicationInfo.getMakeType()!=null&&applicationInfo.getMakeType().equals("1")) {
            LambdaEsQueryWrapper<ApplicationInfoVersionIndex> esWrapper = EsWrappers.lambdaQuery(ApplicationInfoVersionIndex.class)
                    .eq(ApplicationInfoVersionIndex::getVersionId,applicationInfo.getRemark());
            ApplicationInfoVersionIndex applicationInfoVersion= applicationInfoVersionIndexMapper.selectOne(esWrapper);
            if(ObjectUtil.isEmpty(applicationInfoVersion)) {
                Result.fail("发布失败");
            }
            Gson gson = new Gson();
            if(ObjectUtil.isNotEmpty(component.getNodes())&&ObjectUtil.isNotEmpty(applicationInfoVersion)){
                applicationInfoVersion.setNodesJson(gson.toJson(component.getNodes()));
            }
            if(ObjectUtil.isNotEmpty(component.getNodeRel())&&ObjectUtil.isNotEmpty(applicationInfoVersion)){
                applicationInfoVersion.setNodeRelJson(gson.toJson(component.getNodeRel()));
            }
            if(ObjectUtil.isNotEmpty(component)&&ObjectUtil.isNotEmpty(applicationInfoVersion)){
                applicationInfoVersion.setComponentJson(gson.toJson(component));
            }
            if(ObjectUtil.isNotEmpty(applicationInfoVersion)){
                applicationInfoVersionIndexMapper.updateById(applicationInfoVersion);
            }
        }
        // 删除缓存
        StringParam param = new StringParam();
        param.setParam(component.getComponentId());
        clearFlowCache(param);
        param.setParam(copy.getComponentId());
        clearFlowCache(param);
        return Result.success();
    }

    @Override
    public Result<Page<ComponentDto>> page(WorkFlowPageParam pageParam) {
        // 调用处：菜单工作流页面【personalGrantTenant】、应用或流中添加工作流时的页面【推荐official，我的（个人、授权）personalGrant】
        String accountName = AppContextHolder.getAccountName();
        //PermissionControlUtils.buildPermission(queryWrapper, BusinessPermissionEnum.WORKFLOW);
        ComponentTypeEnum type = pageParam.getType();
        Wrappers<Object> wrappers = Wrappers.init()
                .select(COMPONENT.ID, COMPONENT.COMPONENT_ID, COMPONENT.COMPONENT_NAME, COMPONENT.COMPONENT_DESC, COMPONENT.STATUS,
                        COMPONENT.TYPE, COMPONENT.ICON, COMPONENT.CREATE_USER, COMPONENT.CREATE_TIME, COMPONENT.UPDATE_TIME, COMPONENT.UPDATE_USER,
                        COMPONENT.TENANT_ID, COMPONENT.LABELS, COMPONENT.SYSTEM_PLUGIN, COMPONENT.PUBLISH_APP_STORE,
                        COMPONENT.PUBLISH_DESC, COMPONENT.PUBLISH_TIME, COMPONENT.OWNER_TYPE)
                .where(type != null, COMPONENT.TYPE.eq(type))
                .and(type == null, COMPONENT.TYPE.in(ComponentTypeEnum.WORKFLOW, ComponentTypeEnum.ITERATION))
                .and(StrUtil.isNotBlank(pageParam.getApplicationName()), COMPONENT.COMPONENT_NAME.like(pageParam.getApplicationName()))
                .and(StrUtil.isNotBlank(pageParam.getCreateUser()), COMPONENT.CREATE_USER.eq(accountName))
                .and(ObjectUtil.isNotEmpty(pageParam.getPublishAppStore()), COMPONENT.PUBLISH_APP_STORE.eq(pageParam.getPublishAppStore())) // 0-未发布 1-已发布
                .and(ObjectUtil.isNotEmpty(pageParam.getStatus()), COMPONENT.STATUS.eq(pageParam.getStatus())); // 0-未启用 1-已启用

        if (pageParam.getSort() != null && StrUtil.isNotBlank(pageParam.getOrder())) {
            wrappers.orderBy(pageParam.getOrder() + " " + pageParam.getSort());
        }else {
            wrappers.orderBy(COMPONENT.CREATE_TIME.desc()); // 无参默认按创建时间排序
        }

        String ownerType = pageParam.getOwnerType();

        // 根据权限查询
        PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.WORKFLOW, OwnerTypeEnum.getByCode(ownerType));

        Page<Component> paginate = componentMapper.paginate(new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), wrappers);
        List<String> componentIds = paginate.getRecords().stream().map(Component::getComponentId).collect(Collectors.toList());
        if (componentIds.isEmpty()) { // 如果前面的查询没有满足条件的，直接返回空防止下面全量查询ComponentNode
            return Result.success(new Page<ComponentDto>());
        }
        List<ComponentNode> componentNodes = componentNodeMapper.selectListByQuery(QueryWrapper.create().where(COMPONENT_NODE.COMPONENT_ID.in(componentIds)));
        Map<String, List<ComponentNode>> nodeMap = componentNodes.stream().collect(Collectors.groupingBy(ComponentNode::getComponentId));
        Map<String, List<MetaParam>> paramMap = metaParamMapper.selectListByQuery(QueryWrapper.create()
                        .where(META_PARAM.NODE_ID.in(componentNodes.stream().map(ComponentNode::getNodeId).collect(Collectors.toList()))))
                .stream().collect(Collectors.groupingBy(MetaParam::getNodeId));

        if (CollUtil.isEmpty(componentIds)) {
            return Result.success(Page.of(paginate.getPageNumber(), paginate.getPageSize()));
        }

        // 去掉componentId中的标识
        componentIds = componentIds.stream().map(id -> id.split("_")[0]).collect(Collectors.toList());

        // 应用信息
        List<ApplicationInfo> allApp = applicationInfoService.getByAppId(componentIds);
        Map<String, ApplicationInfo> appMap = allApp.stream().collect(Collectors.toMap(
                ApplicationInfo::getApplicationId,
                p -> p,
                (k1, k2) -> k1
        ));

        // 预设问题
        List<PresetQuestion> byAppIds = presetQuestionService.getByAppIds(componentIds);
        Map<String, List<PresetQuestion>> presetQuestionMap = byAppIds.stream().collect(Collectors.groupingBy(PresetQuestion::getApplicationId));

        // 用户信息
        List<Component> records = paginate.getRecords();
        List<String> accountNameList = records.stream().map(Component::getCreateUser).collect(Collectors.toList());
        List<OauthUser> users = userService.getUserDetailByAccountNameList(accountNameList);
        // 账户名->真实姓名
        Map<String, String> userMap = users.stream().filter(Objects::nonNull).collect(Collectors.toMap(OauthUser::getAccountName, OauthUser::getUsername));

        List<ComponentDto> collect = records.stream().map(record -> {
            ComponentDto dto = BeanUtil.copy(record, ComponentDto.class);
            ComponentTypeEnum typeEnum = ComponentTypeEnum.getByCode(record.getType());
            dto.setType(typeEnum.getCode());
            // ApplicationInfo info = applicationInfoService.getByAppId(record.getComponentId());
            ApplicationInfo info = appMap.get(record.getComponentId().split("_")[0]);
            List<PresetQuestion> list = presetQuestionMap.getOrDefault(record.getComponentId(), Lists.newArrayList());
            // List<PresetQuestion> list = presetQuestionService.list(
            //         QueryWrapper.create().where(
            //                         PRESET_QUESTION.APPLICATION_ID.eq(record.getComponentId()))
            //                 .and(PRESET_QUESTION.STATUS.eq(YesNoEnum.YES.getCode())));
            if (info != null) {
                info.setPresetQuestionList(list.stream().map(PresetQuestion::getQuestion).distinct().collect(Collectors.toList()));
            }
            dto.setApplicationInfo(info);
            // 获取节点信息，构造节点参数
            List<ComponentNode> nodeList = nodeMap.get(record.getComponentId());
            if (CollectionUtil.isEmpty(nodeList)) {
                return dto;
            }
            List<ComponentNodeDto> nodes = nodeList.stream().map(node -> BeanUtil.copy(node, ComponentNodeDto.class)).peek(node -> {
                List<MetaParam> metaParams = paramMap.get(node.getNodeId());
                if (CollUtil.isNotEmpty(metaParams)) {
                    node.setInput(metaParams.stream().filter(param -> param.getDirection().equals(DirectionEnum.INPUT.getValue())).collect(Collectors.toList()));
                    node.setOutput(metaParams.stream().filter(param -> param.getDirection().equals(DirectionEnum.OUTPUT.getValue())).collect(Collectors.toList()));
                }
            }).collect(Collectors.toList());
            dto.setNodes(nodes);
            // 设置用户真实姓名
            dto.setUsername(userMap.getOrDefault(record.getCreateUser(), StringConstant.BLANK));
            return dto;
        }).collect(Collectors.toList());

        Page<ComponentDto> result = Page.of(paginate.getPageNumber(), paginate.getPageSize(), paginate.getTotalRow());
        result.setRecords(collect);

        // 在授权相关场景下，如果是普通用户，经过权限查询后，如果不是当前用户的知识库，就是授权的，区分做一下标记
        String powerType = AppContextHolder.getTokenUserInfo().getPowerType();
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        List<ComponentDto> dtoRecords = result.getRecords();

        if (null != ownerType && ownerType.toLowerCase().contains(GRANT.getCode()) && !powerType.equals(PowerTypeEnum.SYSTEM_ADMIN.getCode())) {
            dtoRecords.forEach(record -> {
                Boolean isMe = accountName.equals(record.getCreateUser());
                record.setIsMe(isMe);
                record.setGranted(!isMe);
            });
        }
        return Result.success(result);
    }

    @Override
    public SseEmitter run(RunComponentNodeParam param, String model) {
        SseEmitter connection = SseEmitterUtils.getConnection(param.getClientId(), 1000 * 60 * 60);
        String accessToken = ContextHolders.getAccessToken();
        param.setToken(accessToken);
        // 执行方式和问答一致
        new Thread(() -> runFlow(param, model, true), MDC.get(MDCTraceUtils.KEY_TRACE_ID)).start();
        return connection;
    }

    @Override
    public SseEmitter runParallel(RunComponentNodeParam param, String model) {
        SseEmitter connection = SseEmitterUtils.getConnection(param.getClientId());
        String accessToken = ContextHolders.getAccessToken();
        param.setToken(accessToken);
        // 执行方式和问答一致
        new Thread(() -> runFlowParallel(param, model), MDC.get("traceId")).start();
        return connection;
    }


    @Override
    @Transactional
    public Result delete(Component component) {
        String componentId = component.getComponentId();
        List<ComponentDto> componentDtoList = selectById(ListUtil.toList(componentId));
        if (CollUtil.isEmpty(componentDtoList)) {
            throw new RuntimeException("未找到该组件");
        }
        ComponentDto componentDto = componentDtoList.get(0);
        // 清除节点参数
        List<MetaParam> metaParams = componentDto.getMetaParams();
        if (CollUtil.isNotEmpty(metaParams)) {
            metaParamMapper.deleteByCondition(META_PARAM.ID.in(metaParams.stream().map(MetaParam::getId).collect(Collectors.toList())));
        }
        // 清除节点关系
        componentNodeRelMapper.deleteByCondition(COMPONENT_NODE_REL.COMPONENT_ID.eq(component.getComponentId()));
        // 清除节点信息
        componentNodeMapper.deleteByCondition(COMPONENT_NODE.COMPONENT_ID.eq(componentId));
        // 清除组件信息
        componentMapper.deleteByCondition(COMPONENT.COMPONENT_ID.eq(componentId));
        // 清除应用信息
        String stringParam = "";
        /**
         * 这里的规则需要和保存时的一致，否则无法删除
         * @see WorkFlowServiceImpl#save(Component)
         */
        if (componentId.contains("_")) {
            int indexOf = componentId.indexOf("_");
            stringParam = (componentId.substring(0, indexOf));
        }
        if (!"fromApp".equals(component.getDeleteFrom())) {
            applicationInfoService.deleteByApplicationId(stringParam);
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<ComponentDto> createAPI(Component component) {
        // 添加后是personal，预置后是official，群众没有权限进行操作
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
            component.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
        } else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
            return Result.fail("无权限操作");
        } else {
            component.setOwnerType(PERSONAL.getCode());
        }


        if (StrUtil.isBlank(component.getComponentId())) {
            component.setComponentId(IdUtil.simpleUUID());
        }
        component.setType(ComponentTypeEnum.PLUGIN.getCode());
        save(component);
        String componentId = component.getComponentId();
        // 1. 添加3个节点 开始 API 结束
        List<ComponentNode> nodes = new ArrayList<>();
        ComponentNode start = buildStartNode(componentId);
        ComponentNode api = buildAPINode(componentId);
        ComponentNode end = buildEndNode(componentId);
        nodes.add(start);
        nodes.add(api);
        nodes.add(end);
        componentNodeMapper.insertBatch(nodes);

        // 2. 构建默认节点参数
        List<MetaParam> metaParamList = new ArrayList<>();
        buildStartNodeParam(start, metaParamList);
        buildAPINodeParam(api, metaParamList, start);
        buildEndNodeParam(api.getNodeId(), end, metaParamList);
        metaParamMapper.insertBatch(metaParamList);

        // 3. 构建节点关系
        List<ComponentNodeRel> relation = new ArrayList<>();
        ComponentNodeRel rel1 = new ComponentNodeRel();
        rel1.setComponentId(componentId);
        rel1.setSourceNodeId(start.getNodeId());
        rel1.setTargetNodeId(api.getNodeId());
        relation.add(rel1);
        ComponentNodeRel rel2 = new ComponentNodeRel();
        rel2.setComponentId(componentId);
        rel2.setSourceNodeId(api.getNodeId());
        rel2.setTargetNodeId(end.getNodeId());
        relation.add(rel2);
        componentNodeRelMapper.insertBatch(relation);
        List<ComponentDto> componentDtoList = selectById(ListUtil.toList(componentId));
        return Result.success(componentDtoList.get(0));
    }

    @Override
    public Result queryPlugin(PluginParam pageInfo) {
        // 调用处：菜单工具插件页面、流或应用中添加插件的页面【插件商店official，我的（个人、授权）personalGrant】
        Wrappers<Object> wrappers = Wrappers.init()
                .where(StringUtils.isNotBlank(pageInfo.getComponentName()), COMPONENT.COMPONENT_NAME.like(pageInfo.getComponentName()))
                .and(COMPONENT.TYPE.in(ComponentTypeEnum.PLUGIN, ComponentTypeEnum.CODE))
                .and(StringUtils.isNotBlank(pageInfo.getLabels()), COMPONENT.LABELS.like(pageInfo.getLabels()))
                .and(ObjectUtil.isNotEmpty(pageInfo.getPublishAppStore()), COMPONENT.PUBLISH_APP_STORE.eq(pageInfo.getPublishAppStore()))
                .and(ObjectUtil.isNotEmpty(pageInfo.getStatus()), COMPONENT.STATUS.eq(pageInfo.getStatus()));

        List<String> accountNames = new ArrayList<>();
        if (StringUtils.isBlank(pageInfo.getCreateUser())) { //页面type传参标记是在工作流中点击的  工作流中点击的把发布的插件一起查出来
            List<OauthUser> superManageUser = userService.getSuperManageUser();
            accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());
        }

        if (StringUtils.isNotBlank(pageInfo.getOrder()) && StringUtils.isNotBlank(pageInfo.getSort())) {
            wrappers.orderBy(pageInfo.getOrder() + " " + pageInfo.getSort());
        }else{
            wrappers.orderBy(COMPONENT.CREATE_TIME.desc()); // 默认按创建时间排序
        }

        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String ownerType = pageInfo.getOwnerType();


        // 普通用户引用插件时，如果查看插件商店，前端传的是all，此时不需要根据权限查询
        // 如果是其他情况则不传all，此时根据上方的if-else，在此处根据权限查询的基础上增加条件筛选
        if (null != ownerType && !ownerType.equals(ALL.getCode())){
            PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.PLUGIN, OwnerTypeEnum.getByCode(ownerType));
        }

        // 根据权限查询
        //PermissionControlUtils.buildPermission2(wrappers, BusinessPermissionEnum.PLUGIN, accountNames);

        // 所有人可见的插件(公共类型的插件)
        //wrappers.or(COMPONENT.ALL_VISIBLE.eq(1));
        Page<Component> paginate = componentMapper.paginate(new Page<>(pageInfo.getPageNo(), pageInfo.getPageSize()), wrappers);
        List<String> componentIds = paginate.getRecords().stream().map(Component::getComponentId).distinct().collect(Collectors.toList());
        List<ComponentDto> componentDtoList = selectById(componentIds);
        Map<String, ComponentDto> dtoMap = componentDtoList.stream().collect(Collectors.toMap(
                ComponentDto::getComponentId,
                p -> p,
                (k1, k2) -> k1
        ));
        Page<ComponentDto> page = paginate.map(p -> dtoMap.get(p.getComponentId()));
        String accountName = tokenUserInfo.getAccountName();
        String powerType = tokenUserInfo.getPowerType();
        // 在授权相关场景下，如果是普通用户，经过权限查询后，如果不是当前用户的知识库，就是授权的
        List<ComponentDto> records = page.getRecords();
        if (null != ownerType && ownerType.toLowerCase().contains(GRANT.getCode()) && !powerType.equals(PowerTypeEnum.SYSTEM_ADMIN.getCode())) {
            records.forEach(record -> {
                Boolean isMe = accountName.equals(record.getCreateUser());
                record.setIsMe(isMe);
                record.setGranted(!isMe);
            });
        }
        return Result.success(page);
    }

    @Override
    @Transactional
    public Result updateAPI(ComponentDto component) {
        if (!component.getClickPublish()) {
            component.setPublishAppStore(null);
        } else {
            //发布插件到商店 推送审核记录到插件审核表中
            component.setPublishTime(new Date());

            //每次发布都需要记录变更记录
            ApplicationPublishRecord applicationPublishRecord = ApplicationPublishRecord.create();
            applicationPublishRecord.setMessageSource(2);
            applicationPublishRecord.setApplicationId(component.getComponentId());
            applicationPublishRecord.setPublishDesc(StringUtils.isBlank(component.getPublishDesc()) ? "首次发布" : component.getPublishDesc());
            applicationPublishRecordService.save(applicationPublishRecord);
            log.info("添加 插件 发布记录成功");

            //推送审核记录到审核记录表中
            ServerPublishAudit serverPublishAudit = ServerPublishAudit.create();
            serverPublishAudit.setMessageSource(2);
            serverPublishAudit.setPluginId(component.getComponentId());
            serverPublishAudit.setPluginName(component.getComponentName());
            serverPublishAudit.setIntroduce(component.getComponentDesc());
            serverPublishAudit.setFacadeImageUrl(component.getIcon());
            serverPublishAudit.setCreateUserId(component.getCreateUser());
            serverPublishAudit.setCreateUserName(component.getCreateUser());
            serverPublishAudit.setPublishType(component.getLabels());
            serverPublishAuditService.add(serverPublishAudit);
            log.info("推送审核记录成功，插件ID:{}", component.getComponentId());

        }
        // 修改组件信息
        Component componentEntity = BeanUtil.copy(component, Component.class);
        save(componentEntity);
        // 修改组件节点信息
        List<ComponentNodeDto> nodes = component.getNodes();
        ArrayList<MetaParam> params = new ArrayList<>();
        component.setMetaParams(params);
        nodes.forEach(node -> {
            params.addAll(node.getInput());
            params.addAll(node.getOutput());
            componentNodeMapper.insertOrUpdate(BeanUtil.copy(node, ComponentNode.class));
            // 根据NodeId清除节点参数
            metaParamMapper.deleteByQuery(Wrappers.create().where(META_PARAM.NODE_ID.eq(node.getNodeId())));
        });
        // 修改参数信息
        params.forEach(param -> {
            if (StringUtils.isBlank(param.getParamId())) {
                param.setParamId(IdUtil.simpleUUID());
            }
            metaParamMapper.insert(param);
        });

        // 修改节点关系
        List<ComponentNodeRel> nodeRel = component.getNodeRel();
        nodeRel.forEach(rel -> componentNodeRelMapper.insertOrUpdate(rel));
        StringParam param = new StringParam();
        param.setParam(component.getComponentId());
        clearFlowCache(param);
        return Result.success(component);
    }

    // @Override
    // public Result validate(ApiNode apiNode) {
    //     return Result.success(completedEvent);
    // }

    @Override
    public Result runNode(RunNodeParam param) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 有token是需要认证
        String accessToken = RequestUtil.extractToken(request);
        String body = HttpUtil.createPost(runNodeApi)
                .header("Authorization", accessToken)
                .body(JSONUtil.toJsonStr(param))
                .execute().body();
        return JSONUtil.toBean(body, Result.class);
    }


    @Override
    public JSONObject runTool(RunComponentNodeParam param, List<StepEntity> stepEntityList) throws Exception {
        return JSONUtil.parseObj(null);
    }

    @Override
    public JSONObject runIteration(RunComponentNodeParam param) throws Exception {
        return JSONUtil.parseObj(null);
    }

    @Override
    public Result<Component> update(Component component) {
//        component.setComponentId(null);
//        component.setType(null);
        if (component.getId() == null) {
            return Result.fail("id不能为空");
        }
        if (StringUtils.isBlank(component.getComponentId())){
            return Result.fail("组件id为空");
        }
        component.setUpdateTime(DateUtil.getCurrentTime(DateUtil.DEFAULT_FORMAT));
        component.setUpdateUser(AppContextHolder.getAccountName());
        int update = componentMapper.update(component);
        if (update > 0) {
            return Result.success(component);
        }
        return Result.fail("更新失败");
    }

    @Override
    public Component selectOne(String componentId) {
        return componentMapper.selectOneByCondition(COMPONENT.COMPONENT_ID.eq(componentId));
    }

    private ComponentNode buildAPINode(String componentId) {
        ComponentNode api = new ComponentNode();
        api.setNodeName("api");
        api.setNodeId(IdUtil.simpleUUID());
        api.setComponentId(componentId);
        api.setNodeType(ComponentNodeEnum.API.getCode());
        String accessToken = ContextHolders.getAccessToken();
        String setting = "{\"url\": \"\", \"Accept\": \"*/*\", \"method\": \"POST\", \"headers\": [{\"key\": \"Authorization\", \"value\": \"Bearer " + accessToken + " \"}], \"contentType\": \"application/json\", \"requestBody\": \"{\\\"msg\\\":\\\"${msg}\\\"}\", \"responseBody\": {\"msg\": \"success\", \"code\": \"200\", \"data\": {}, \"time\": \"2024-09-26 14:30:00\", \"spanId\": \"\", \"traceId\": \"\"}, \"responseType\": false}";
        JSONObject entries = JSONUtil.parseObj(setting);
        entries.set("requestBody", StringConstant.BLANK);
        entries.set("responseBody", StringConstant.BLANK);
        entries.set("headers", StringConstant.BLANK);
        api.setSettings(JSONUtil.toJsonStr(entries));
        return api;
    }

    private void buildEndNodeParam(String nodeId, ComponentNode end, List<MetaParam> metaParamList) {
        MetaParam endParam = new MetaParam();
        endParam.setParamId(IdUtil.simpleUUID());
        endParam.setLabel("result");
        endParam.setVariable("result");
        endParam.setType(MetaParamEnum.STRING.getName());
        endParam.setValueType(MetaParamEnum.REFERENCE.getName());
        endParam.setDesc("结束节点的默认入参");
        endParam.setNodeId(end.getNodeId());
        endParam.setReferenceNodeId(nodeId);
        endParam.setValue("result");
        endParam.setMaxLength(20);
        endParam.setRequired(Boolean.TRUE);
        endParam.setDirection(DirectionEnum.INPUT.getValue());
        metaParamList.add(endParam);
    }

    private void buildAPINodeParam(ComponentNode api, List<MetaParam> metaParamList, ComponentNode start) {
        // 入参
        MetaParam startInParam = new MetaParam();
        startInParam.setParamId(IdUtil.simpleUUID());
        startInParam.setLabel("msg");
        startInParam.setVariable("rawQuery");
        startInParam.setType(MetaParamEnum.STRING.getName());
        startInParam.setValueType(MetaParamEnum.REFERENCE.getName());
        startInParam.setDesc("API节点的默认入参");
        startInParam.setNodeId(api.getNodeId());
        startInParam.setReferenceNodeId(start.getNodeId());
        startInParam.setValue("rawQuery");
        startInParam.setMaxLength(20);
        startInParam.setRequired(Boolean.TRUE);
        startInParam.setDirection(DirectionEnum.INPUT.getValue());
        metaParamList.add(startInParam);
        // 出参
        MetaParam startOutParam = new MetaParam();
        startOutParam.setParamId(IdUtil.simpleUUID());
        startOutParam.setLabel("result");
        startOutParam.setVariable("result");
        startOutParam.setType(MetaParamEnum.STRING.getName());
        startOutParam.setValueType(MetaParamEnum.REFERENCE.getName());
        startOutParam.setDesc("API节点的默认出参");
        startOutParam.setNodeId(api.getNodeId());
        startOutParam.setReferenceNodeId(api.getNodeName());
        // $.data
        startOutParam.setValue("data");
        startOutParam.setMaxLength(20);
        startOutParam.setRequired(Boolean.TRUE);
        startOutParam.setDirection(DirectionEnum.OUTPUT.getValue());
        metaParamList.add(startOutParam);
    }

    private void buildStartNodeParam(ComponentNode start, List<MetaParam> metaParamList) {
        MetaParam startParam = new MetaParam();
        startParam.setParamId(IdUtil.simpleUUID());
        startParam.setLabel("rawQuery");
        startParam.setVariable("rawQuery");
        startParam.setType(MetaParamEnum.STRING.getName());
//        startParam.setDesc();
        startParam.setNodeId(start.getNodeId());
        startParam.setReferenceNodeId(start.getNodeName());
//        startParam.setValue();
        startParam.setMaxLength(20);
        startParam.setRequired(Boolean.TRUE);
        startParam.setDirection(DirectionEnum.OUTPUT.getValue());
        metaParamList.add(startParam);
    }

    private ComponentNode buildStartNode(String componentId) {
        ComponentNode start = new ComponentNode();
        start.setNodeName("开始");
        start.setNodeId(IdUtil.simpleUUID());
        start.setComponentId(componentId);
        start.setNodeType(ComponentNodeEnum.START.getCode());
        return start;
    }

    private ComponentNode buildEndNode(String componentId) {
        ComponentNode end = new ComponentNode();
        end.setNodeName("结束");
        end.setNodeId(IdUtil.simpleUUID());
        end.setComponentId(componentId);
        end.setNodeType(ComponentNodeEnum.END.getCode());
        end.setSettings("{\"responseModel\": 0, \"responseTemplate\": \"本次请求响应result为:${result}\"}");
        return end;
    }

    /**
     * 执行流程
     *
     * @param param
     * @param model
     * @return
     */
    @Override
    public Map<String, Object> runFlow(RunComponentNodeParam param, String model, boolean stopFlag) {
        return DialogueServiceImpl.requestStreamApi(param, workFlowApi, param.getClientId());
    }

    /**
     * 执行并行流程
     * @param param
     * @param model
     * @return
     */
    @Override
    public Map<String, Object> runFlowParallel(RunComponentNodeParam param, String model) {
        return Maps.newHashMap();
    }

    @Override
    public List<Component> effectiveComponent(String applicationId) {
        // String period = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        Wrappers<Object> wrappers = Wrappers.init()
                .select(COMPONENT.COMPONENT_ID)
                .innerJoin(APPLICATION_KNOWLEDGE).on(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID.eq(COMPONENT.COMPONENT_ID))
                .where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationId));
        return componentMapper.selectListByQuery(wrappers);
    }

    @Override
    public Map<String, Object> runWorkFlowNode(RunComponentNodeParam nodeParam) throws Exception {

        return Maps.newHashMap();
    }

    @Override
    @Transactional
    public Result createCode(ComponentDto component) {

        // 1. 从json文件中读取数据
        ComponentDto plugin;
        try {
            InputStream inputStream = dataResource.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            plugin = mapper.readValue(inputStream, ComponentDto.class);
        } catch (IOException e) {
            log.info("读取插件数据失败", e);
            throw new RuntimeException(e);
        }
        Map<String, String> map = new HashMap<>();
        // 组件id
        map.put("6a54048ff5c34059a1e5da899187c3ee_2", IdUtil.simpleUUID() + "_" + ComponentTypeEnum.CODE.getCode());
        // 节点id
        map.put("7f494501-f107-489f-81d2-2475d385a441", IdUtil.simpleUUID());
        map.put("8a1858ef-00c4-4792-b58e-e66456a4edc1", IdUtil.simpleUUID());
        map.put("fa57ac75-fcb9-4d60-9280-26de2239b7f6", IdUtil.simpleUUID());

        // 2. 修改插件类型为代码工具
        plugin.setId(null);
        plugin.setType(ComponentTypeEnum.CODE.getCode());
        plugin.setComponentId(map.get(plugin.getComponentId()));
        String nowDate = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        plugin.setCreateTime(nowDate);
        plugin.setUpdateTime(nowDate);

        // 业务
        plugin.setComponentName(component.getComponentName());
        plugin.setComponentDesc(component.getComponentDesc());
        plugin.setIcon(component.getIcon());
        plugin.setStatus(component.getStatus());
        plugin.setLabels(component.getLabels());
        // 节点数据
        List<ComponentNode> nodes = Lists.newArrayList();
        for (ComponentNodeDto node : plugin.getNodes()) {
            node.setId(null);
            node.setNodeId(map.get(node.getNodeId()));
            node.setComponentId(map.get(node.getComponentId()));
            nodes.add(BeanUtil.copy(node, ComponentNode.class));
        }
        // 参数数据
        List<MetaParam> metaParams = plugin.getMetaParams();
        for (MetaParam param : metaParams) {
            param.setId(null);
            param.setParamId(IdUtil.simpleUUID());
            param.setNodeId(map.get(param.getNodeId()));
            param.setReferenceNodeId(map.get(param.getReferenceNodeId()));
        }
        // 关联数据
        List<ComponentNodeRel> nodeRel = plugin.getNodeRel();
        for (ComponentNodeRel rel : nodeRel) {
            rel.setId(null);
            rel.setComponentId(map.get(rel.getComponentId()));
            rel.setSourceNodeId(map.get(rel.getSourceNodeId()));
            rel.setTargetNodeId(map.get(rel.getTargetNodeId()));
        }
        // 3. 分表保存到数据库
        Component copy = BeanUtil.copy(plugin, Component.class);

        // 添加后是personal，预置后是official，群众没有权限进行操作
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
            copy.setOwnerType(PERSONAL.getCode());
        } else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
            return Result.fail("无权限操作");
        } else {
            copy.setOwnerType(PERSONAL.getCode());
        }

        save(copy);
        componentNodeMapper.insertBatch(nodes);
        componentNodeRelMapper.insertBatch(nodeRel);
        metaParamMapper.insertBatch(metaParams);

        String componentId = plugin.getComponentId();
        List<ComponentDto> componentDtos = selectById(com.google.common.collect.Lists.newArrayList(componentId));
        return Result.success(componentDtos.get(0));
    }

    @Override
    public Result updateCode(CodeToolUpdateParam param) {
        // 1. 查询代码工具数据
        List<ComponentDto> componentDtos = selectById(Lists.newArrayList(param.getComponentId()));
        ComponentDto componentDto = componentDtos.get(0);
        componentDto.setClickPublish(true);
        // 2. 更新代码工具入参
        componentDto.setStatus(param.getStatus());
        componentDto.setPublishAppStore(param.getPublishAppStore());

        List<ComponentNodeDto> nodes = componentDto.getNodes();
        ComponentNodeDto startNode = nodes.stream().filter(node -> Objects.equals(node.getNodeType(), ComponentNodeEnum.START.getCode())).findFirst().orElseThrow(() -> new RuntimeException("未找到开始节点"));
        ComponentNodeDto codeNode = nodes.stream().filter(node -> Objects.equals(node.getNodeType(), ComponentNodeEnum.CODE.getCode())).findFirst().orElseThrow(() -> new RuntimeException("未找到代码节点"));
        ComponentNodeDto endNode = nodes.stream().filter(node -> Objects.equals(node.getNodeType(), ComponentNodeEnum.END.getCode())).findFirst().orElseThrow(() -> new RuntimeException("未找到结束节点"));

        List<MetaParam> paramInputs = param.getInputs();

        // 2.1 更新开始节点参数
        startNode.setOutput(paramInputs);
        // 2.2 更新代码节点参数
        List<MetaParam> codeInput = new ArrayList<>();
        for (MetaParam metaParam : paramInputs) {
            metaParam.setNodeId(startNode.getNodeId());

            MetaParam newParam = new MetaParam();
            newParam.setLabel(metaParam.getLabel());
            newParam.setNodeId(codeNode.getNodeId());
            newParam.setParamId(IdUtil.simpleUUID());
            newParam.setReferenceNodeId(startNode.getNodeId());
            newParam.setType(metaParam.getType());
            newParam.setValueType(MetaParamEnum.REFERENCE.getName());
            newParam.setValue(metaParam.getVariable());
            newParam.setVariable(metaParam.getVariable());
            newParam.setRequired(Boolean.TRUE);
            newParam.setDesc("代码节点入参");
            newParam.setDirection(DirectionEnum.INPUT.getValue());
            codeInput.add(newParam);
        }
        codeNode.setInput(codeInput);

        Map<String, String> map = new HashMap<>();
        map.put("code", param.getCode());
        map.put("url", param.getUrl());
        map.put("funcName", "main");
        map.put("language", param.getLanguage());
        codeNode.setSettings(JSONUtil.toJsonStr(map));
        // 3. 更新代码工具出参
        // 3.1 更新代码节点出参
        List<MetaParam> codeOutputs = new ArrayList<>();
        for (MetaParam metaParam : param.getOutputs()) {
            MetaParam newParam = new MetaParam();
            newParam.setLabel(metaParam.getLabel());
            newParam.setNodeId(codeNode.getNodeId());
            newParam.setParamId(IdUtil.simpleUUID());
            newParam.setType(metaParam.getType());
            newParam.setValueType(metaParam.getType());
            newParam.setVariable(metaParam.getVariable());
            newParam.setRequired(Boolean.TRUE);
            newParam.setDesc("代码节点出参");
            newParam.setDirection(DirectionEnum.OUTPUT.getValue());
            codeOutputs.add(newParam);
        }
        codeNode.setOutput(codeOutputs);

        // 3.2 更新结束节点出参
        List<MetaParam> endOutputs = new ArrayList<>();
        for (MetaParam metaParam : param.getOutputs()) {
            MetaParam newParam = new MetaParam();
            newParam.setLabel(metaParam.getLabel());
            newParam.setNodeId(endNode.getNodeId());
            newParam.setParamId(IdUtil.simpleUUID());
            newParam.setReferenceNodeId(codeNode.getNodeId());
            newParam.setType(metaParam.getType());
            newParam.setValueType(MetaParamEnum.REFERENCE.getName());
            newParam.setValue(metaParam.getVariable());
            newParam.setVariable(metaParam.getVariable());
            newParam.setRequired(Boolean.TRUE);
            newParam.setDesc(metaParam.getDesc());
            newParam.setDirection(DirectionEnum.INPUT.getValue());
            endOutputs.add(newParam);
        }
        endNode.setInput(endOutputs);

        // 4. 更新
        updateAPI(componentDto);

        return Result.success(componentDto);
    }

    public static String getComponentId(Component component) {
        String componentId = component.getComponentId();
        if (StringUtils.isBlank(componentId)) {
            componentId = IdUtil.simpleUUID();
        }
        if (null != component.getType() && !componentId.contains("_")) {
            componentId = componentId + "_" + component.getType();
        }
        return componentId;
    }

    /**
     * @param pluginId
     * @param publishAppStore
     * @author: caohaifeng
     * @date: 2025/2/15 16:22
     * @Description: 审核通过 更新上架字段
     * @Version:1.0
     */
    @Override
    public int updateAppStoreByPluginId(String pluginId, Integer publishAppStore) {
        if (StringUtils.isNotBlank(pluginId)) {
            Component component = new Component();
            component.setPublishAppStore(publishAppStore);
            QueryWrapper queryWrapper = QueryWrapper.create();
            queryWrapper.where(COMPONENT.COMPONENT_ID.eq(pluginId));
            return componentMapper.updateByQuery(component, queryWrapper);
        }
        return -1;
    }

    @Override
    public Result clearFlowCache(StringParam param) {
        clear(param);
        CacheClearServiceImpl.clearComCache("/cacheClear/clearFlowCache", param);
        return Result.success();
    }

    /**
     * 清空缓存
     * @param param
     */
    public static void clear(StringParam param) {
        if (StringUtils.isNotBlank(param.getParam())) {
            COMPONENT_MAP.remove(param.getParam());
            COMPONENT_MAP.remove(param.getParam().split(":")[0]);
        } else {
            COMPONENT_MAP.clear();
        }
    }

    /**
     * 获取组件数据
     *
     * @param componentId
     * @param param
     * @return
     */
    @Override
    public ComponentDto getComponentDto(String componentId, RunComponentNodeParam param) {
        ComponentDto componentDto = COMPONENT_MAP.get(componentId);
        // 缓存中获取到全部数据，直接返回
        if (null == componentDto) {
            List<ComponentDto> componentDtos = selectById(ListUtil.toList(componentId));
            componentDto = componentDtos.get(0);
            componentDto.setCanvas(null);
            COMPONENT_MAP.put(componentId, componentDto);
        }
        String jsonStr = JSONUtil.toJsonStr(componentDto);
        componentDto = JSONUtil.parseObj(jsonStr).toBean(ComponentDto.class);
        componentDto.setSseClientId(param.getClientId());
        componentDto.setClientType(param.getClientType());

        return componentDto;
    }

    @Override
    public Result setPreset(PluginParam pluginParam) {
        if (pluginParam == null) {
            return Result.fail("请求参数为空");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // 只有超管才能操作
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
            return Result.fail("无操作权限");
        }

        // 获取传入的插件主键id
        Integer Id = pluginParam.getId();
        Component component = componentMapper.selectOneById(Id);
        if (component == null) {
            return Result.fail("插件不存在");
        }

        // 超级管理员集合
        List<OauthUser> superManageUser = userService.getSuperManageUser();
        List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

        // 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
        if (accountNames.contains(component.getCreateUser())){
            // 构造更新对象，已经是预置就取消，否则设置为预置,
            if (OFFICIAL.getCode().equals(component.getOwnerType())
            ) {
                component.setOwnerType(PERSONAL.getCode());
            } else {
                component.setOwnerType(OFFICIAL.getCode());
            }
        }else{
            return Result.fail("该组件非超级管理员创建，无法进行预置操作");
        }

        // 执行更新
        componentMapper.update(component,false);

        return Result.success("插件预置成功");
    }

    @Override
    public List<GrantData> queryGrantData(ComponentDto componentDto) {
        if (componentDto == null) {
            return Lists.newArrayList();
        }

        String targetType = StringUtils.EMPTY;
        String dataId = StringUtils.EMPTY;

        if (componentDto.getType() == 1) {
            targetType = MybatisFiledConstant.PLUGIN;
            dataId = componentDto.getComponentId();
        } else if (componentDto.getType() == 2 || componentDto.getType() == 3) {
            targetType = MybatisFiledConstant.WORKFLOW;
            dataId = componentDto.getComponentId();
        } else if (componentDto.getType() == 4 || componentDto.getType() == 5) {
            targetType = MybatisFiledConstant.APP;
            ApplicationInfo applicationInfo = componentDto.getApplicationInfo();
            dataId = applicationInfo.getApplicationId();
        }

        ApplicationInfo applicationInfo = componentDto.getApplicationInfo();
        if (ObjectUtil.isEmpty(applicationInfo)) {
            return Lists.newArrayList();
        }
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        String userId = String.valueOf(tokenOauthUserInfo.getId());
        List<String> targetIds = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            // 有的用户可能没有绑定租户
            targetIds.add(userId);
        }
        String tenantId = tokenOauthUserInfo.getTenantId();
        // 用户id加入列表
        targetIds.add(userId);
        if (StringUtils.isNotBlank(tenantId)) {
            // 有的用户可能没有绑定租户
            targetIds.add(tenantId);
        }
        if (CollectionUtil.isEmpty(targetIds) || StringUtils.isBlank(dataId)) {
            return Lists.newArrayList();
        }
        if (StringUtils.isNotBlank(applicationInfo.getApplicationId()) && CollectionUtil.isNotEmpty(targetIds)) {
            Wrappers<Object> grantDataWrappers = Wrappers.init()
                    .where(GRANT_DATA.DATA_ID.eq(dataId))
                    .and(GRANT_DATA.DATA_TYPE.eq(targetType))
                    .and(GRANT_DATA.TARGET_TYPE.in(MybatisFiledConstant.USER, MybatisFiledConstant.TENANT))
                    .and(GRANT_DATA.TARGET_ID.in(targetIds));
            return grantDataMapper.selectListByQuery(grantDataWrappers);
        }
        return Lists.newArrayList();
    }

}