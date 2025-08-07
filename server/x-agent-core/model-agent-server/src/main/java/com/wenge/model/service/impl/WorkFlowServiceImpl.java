package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import com.wenge.model.dto.param.FairAICheckParam;
import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.ScreenshotParam;
import com.wenge.model.dto.param.WorkFlowPageParam;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.dto.result.ComponentNodeDto;
import com.wenge.model.dto.result.ScreenshotResult;
import com.wenge.model.dto.result.WorkFlowDto;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.File;
import com.wenge.model.entity.FileData;
import com.wenge.model.enums.ApplicationTypeEnum;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.model.mapper.ComponentMapper;
import com.wenge.model.mapper.ComponentNodeMapper;
import com.wenge.model.mapper.ComponentNodeRelMapper;
import com.wenge.model.mapper.MetaParamMapper;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.ComponentService;
import com.wenge.model.service.WorkFlowService;
import com.wenge.model.task.FileDocumentTask;
import com.wenge.model.utils.ScreenshotUtils;
import com.wenge.model.workflow.component.dto.WorkflowRunStatus;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.ComponentNodeRel;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentTypeEnum;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentNodeMapper componentNodeMapper;

    @Autowired
    private ComponentNodeRelMapper nodeRelMapper;

    @Autowired
    private MetaParamMapper metaParamMapper;

    @Autowired
    private FileDocumentTask fileDocumentTask;

    @Autowired
    private ScreenshotUtils screenshotUtils;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Result<WorkFlowDto> save(Component component) {
        // 添加后是personal，预置后是official，群众没有权限进行操作
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
            component.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
        } else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
            return Result.fail("无权限操作");
        } else {
            component.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
        }
        // 唯一校验
        if (component.getComponentId() != null) {
            String componentId = ComponentServiceImpl.getComponentId(component);
            component.setComponentId(componentId);
            Component exist = componentService.selectOne(component.getComponentId());
            if (exist != null) {
                // 更新组件名称、图标、类型、组件描述
                exist.setComponentDesc(component.getComponentDesc());
                exist.setComponentName(component.getComponentName());
                exist.setIcon(component.getIcon());
                exist.setType(component.getType());
                componentService.update(exist);

                ApplicationInfo applicationInfo = new ApplicationInfo();
                applicationInfo.setApplicationId(component.getComponentId().split("_")[0]);
                Integer type = component.getType();
                String type1 = getType(type);
                applicationInfo.setType(type1);
                applicationInfoService.updateApp(applicationInfo);
                log.info("工作流{}已存在", component.getComponentId());
                return Result.success(new WorkFlowDto(exist, null));
            }
        }
        // 1. 创建一个工作流
        Result<Component> componentResult = componentService.save(component);
        Component data = componentResult.getData();
        if (data.getApplicationInfo() != null) {
            // 这里说明已经有应用, 将类型改成工作流
            ApplicationInfo applicationInfo = data.getApplicationInfo();
            if (null != data.getType()) {
                Integer type = data.getType();
                String type1 = getType(type);
                applicationInfo.setType(type1);

            }
            // 添加后是personal，预置后是official，群众没有权限进行操作
            if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
                applicationInfo.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
            } else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
                return Result.fail("无权限操作");
            } else {
                applicationInfo.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
            }
            applicationInfoService.addApplicationInfo(applicationInfo);
            return Result.success(new WorkFlowDto(data, data.getApplicationInfo()));
        }
        // 2. 获取默认应用配置
        Result<ApplicationInfo> defaultApp = applicationInfoService.getDefaultApp(new EmptyParam());
        ApplicationInfo defaultAppData = defaultApp.getData();
        defaultAppData.setApplicationId(data.getComponentId());
        if (data.getComponentId().contains("_")) {
            int indexOf = data.getComponentId().indexOf("_");
            defaultAppData.setApplicationId(data.getComponentId().substring(0, indexOf));
        }
        defaultAppData.setApplicationName(data.getComponentName());
        if (null != data.getType()) {
            Integer type = data.getType();
            ComponentTypeEnum byCode = ComponentTypeEnum.getByCode(type);
            if (null != byCode) {
                switch (byCode) {
                    case APPLICATION:
                        defaultAppData.setType(ApplicationTypeEnum.WORKFLOW.getName());
                        break;
                    case APPLICATION_FLOW:
                        defaultAppData.setType(ApplicationTypeEnum.DIALOGUE.getName());
                        break;
                    case WORKFLOW:
                        defaultAppData.setType(ApplicationTypeEnum.WORKFLOW_APPLICATION.getName());
                    default:
                        break;
                }
            }
        }
        defaultAppData.setApplicationCode(IdUtil.fastSimpleUUID());
        // 3. 工作流绑定默认应用
        Result<ApplicationInfo> result = applicationInfoService.addApplicationInfo(defaultAppData);

        return Result.success(new WorkFlowDto(data, result.getData()));
    }

    @Override
    public SseEmitter dialogueRun(RunComponentNodeParam param) {
        // 设置工作流为debug模式，执行过程中会推送日志
        return componentService.run(param, WorkflowRunStatus.DEBUG);
    }

    @Override
    @Transactional
    public Result<WorkFlowDto> copy(Component component, String from, String name) {
        String componentNewId = component.getComponentNewId();
        if (StringUtils.isBlank(componentNewId)) {
            componentNewId = IdUtil.simpleUUID();
        }
        try {
            return copy2Target(component, componentNewId, from, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success(null);
    }

    @Override
    public Result<WorkFlowDto> copy2Target(Component component, String targetId, String from, String name) {
        String componentId = component.getComponentId();
        Assert.notNull(componentId, "工作流id不能为空");
        // 1. 调用创建新工作流方法
        // ComponentDto componentDto = componentService.selectById(componentId);
        List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(componentId));
        if (CollectionUtil.isEmpty(componentDtoList)) {
            Assert.notEmpty(componentDtoList, "未找到工作流");
        }
        ComponentDto componentDto = componentDtoList.get(0);
        Assert.notNull(componentDto, "未找到工作流");
        Component componentCopy = BeanUtil.copy(componentDto, Component.class);
        componentCopy.setCreateTime(null);
        componentCopy.setUpdateTime(null);

        componentCopy.setId(null);
        if (!targetId.contains("_")) {
            componentCopy.setComponentId(targetId + "_" + componentCopy.getType());
        } else {
            componentCopy.setComponentId(targetId);
        }
        if (StringUtils.isNotBlank(name)) {
            componentCopy.setComponentName(name);
        } else {
            componentCopy.setComponentName(componentCopy.getComponentName() + "【复制】");
        }

        // 2. copy节点信息
        // 2.1 建立一份新旧节点id映射
        Map<String, String> map = new HashMap<>();
        List<ComponentNodeDto> nodes = componentDto.getNodes();
        List<ComponentNode> nodesCopy = nodes.stream().map(node -> BeanUtil.copy(node, ComponentNode.class)).peek(node -> {
            node.setId(null);
            String newNodeId = IdUtil.simpleUUID();
            map.put(node.getNodeId(), newNodeId);
            node.setNodeId(newNodeId);
            node.setComponentId(componentCopy.getComponentId());
        }).collect(Collectors.toList());
        if (!nodesCopy.isEmpty()) {
            componentNodeMapper.insertBatch(nodesCopy);
        }

        // 3. copy参数信息
        List<MetaParam> metaParams = componentDto.getMetaParams();
        metaParams.forEach(param -> {
            param.setId(null);
            param.setParamId(IdUtil.simpleUUID());
            param.setNodeId(map.get(param.getNodeId()));
            param.setReferenceNodeId(map.get(param.getReferenceNodeId()));
        });
        if (!metaParams.isEmpty()) {
            metaParamMapper.insertBatch(metaParams);
        }
        // 4. copy节点关系
        List<ComponentNodeRel> nodeRel = componentDto.getNodeRel();
        if (CollectionUtil.isNotEmpty(nodeRel)) {
            nodeRel.forEach(rel -> {
                rel.setId(null);
                rel.setComponentId(componentCopy.getComponentId());
                rel.setSourceNodeId(map.get(rel.getSourceNodeId()));
                rel.setTargetNodeId(map.get(rel.getTargetNodeId()));
            });
            nodeRelMapper.insertBatch(nodeRel);
        }
        // * 更新前端画布信息中的节点数据
        String canvas = componentCopy.getCanvas();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            canvas = canvas.replace(entry.getKey(), entry.getValue());
        }
        componentCopy.setCanvas(canvas);
        componentService.save(componentCopy);

        // 5. 调用创建新应用的方法
        if ("flowwork".equals(from)) {
            ApplicationInfo applicationInfo = componentDto.getApplicationInfo();
            if (null == applicationInfo) {
                applicationInfo = new ApplicationInfo();
            }
            // Assert.notNull(applicationInfo, "未找到工作流应用");
            clearProperties(applicationInfo, componentCopy);
            Result<ApplicationInfo> result = applicationInfoService.addApplicationInfo(applicationInfo);
        }

        return Result.success(new WorkFlowDto(componentCopy, componentDto.getApplicationInfo()));
    }

    @Override
    public SseEmitter fairAICheck(FairAICheckParam param) {
        File file = param.getFile();
        // 1. 调用yayi接口解析文档
        List<FileData> list = fileDocumentTask.parseOneFile(file);
        String fileName = list.get(0).getFileName();
        // 2. 执行工作流
        RunComponentNodeParam nodeParam = new RunComponentNodeParam();
        nodeParam.setComponentId(param.getComponentId());
        nodeParam.setClientId(param.getClientId());
        JSONObject entries = new JSONObject();
        // 固定参数名为fileArray
        entries.set("fileArray", list);
        entries.set("fileName", fileName);
        nodeParam.setInputs(entries);
        return dialogueRun(nodeParam);
    }

    @Override
    public Result<Component> update(Component component) {
        return componentService.update(component);
    }

    @Override
    public void deleteById(String id) {
        try {
            if (StringUtils.isBlank(id)) {
                return;
            }
            Component component = new Component();
            component.setComponentId(id);
            component.setDeleteFrom("fromApp");
            componentService.delete(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result<ScreenshotResult> screenshot(ScreenshotParam param) {
        if (CollectionUtil.isEmpty(param.getUrlList())) {
            return Result.success(new ScreenshotResult());
        }

        ScreenshotResult screenshot = screenshotUtils.screenshot(param.getTaskId(), param.getUrlList());
        return Result.success(screenshot);
    }

    @Override
    public Result setPreset(WorkFlowPageParam pageParam) {
        if (pageParam == null) {
            return Result.fail("请求参数为空");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // 只有超管才能操作
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
            return Result.fail("无操作权限");
        }

        // 获取传入的工作流
        Integer Id = pageParam.getId();
        Component component = componentMapper.selectOneById(Id);

        // 超级管理员集合
        List<OauthUser> superManageUser = userService.getSuperManageUser();
        List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

        // 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
        if (accountNames.contains(component.getCreateUser())){
            // 构造更新对象，已经是预置就取消，否则设置为预置,
            if (OwnerTypeEnum.OFFICIAL.getCode().equals(component.getOwnerType())
            ) {
                component.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
            } else {
                component.setOwnerType(OwnerTypeEnum.OFFICIAL.getCode());
            }
        }else{
            return Result.fail("该工作流非超级管理员创建，无法进行预置操作");
        }

        // 执行更新，允许插入null
        componentMapper.update(component,false);

        return Result.success("工作流预置更改成功");

    }

    /**
     * 这里和 {@link ApplicationInfoServiceImpl#clear(ApplicationInfo)} 逻辑应当保持一致
     * @param applicationInfo
     * @param componentCopy
     */
    private static void clearProperties(ApplicationInfo applicationInfo, Component componentCopy) {
        applicationInfo.setId(null);
        String applicationId = componentCopy.getComponentId();
        if (applicationId.contains("_")) {
            applicationId = applicationId.substring(0, componentCopy.getComponentId().indexOf("_"));
        }
        applicationInfo.setApplicationId(applicationId);
        applicationInfo.setApplicationName(componentCopy.getComponentName());
        applicationInfo.setApplicationCode(IdUtil.simpleUUID());
        applicationInfo.setClientLink(null);
        applicationInfo.setApi(null);
        applicationInfo.setApiKey(null);
        applicationInfo.setTenantId(null);
        applicationInfo.setPublishDesc(null);
        applicationInfo.setPublishStatus("4");
        applicationInfo.setPublishTime(null);
        applicationInfo.setPublishAppStore(0);
        applicationInfo.setCreateUser(null);
        applicationInfo.setUpdateUser(null);
        applicationInfo.setCreateTime(null);
        applicationInfo.setUpdateTime(null);
    }

    /**
     * 获取类型
     * @param type
     * @return
     */
    private String getType(Integer type) {
        ComponentTypeEnum byCode = ComponentTypeEnum.getByCode(type);
        String typeName = StringConstant.BLANK;
        if (null != byCode) {
            switch (byCode) {
                case APPLICATION:
                    typeName = ApplicationTypeEnum.WORKFLOW.getName();
                    break;
                case APPLICATION_FLOW:
                    typeName = ApplicationTypeEnum.DIALOGUE.getName();
                    break;
                case APPLICATION_MULTI_AGENT:
                    typeName = ApplicationTypeEnum.MULTI_AGENT.getName();
                    break;
                default:
                    break;
            }
        }
        return typeName;
    }

}
