package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.dto.param.RecordPageParam;
import com.wenge.model.dto.param.ServerPublishAuditPageParam;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.entity.*;
import com.wenge.model.mapper.ServerPublishAuditMapper;
import com.wenge.model.service.*;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.MyFavoriteTableDef.MY_FAVORITE;
import static com.wenge.model.entity.table.ServerPublishAuditTableDef.SERVER_PUBLISH_AUDIT;

@Service
@Slf4j
public class ServerPublishAuditServiceImpl extends ServiceImpl<ServerPublishAuditMapper, ServerPublishAudit> implements ServerPublishAuditService {

    @Autowired
    private ApplicationInfoService appInfoService;

    @Autowired
    private ApplicationPluginService applicationPluginService;

    @Autowired
    private ApplicationPluginDataService applicationPluginDataService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ApplicationKnowledgeService applicationKnowledgeService;

    @Autowired
    private LlmInfoService llmInfoService;

    @Autowired
    private DialogueRecordService dialogueRecordService;

    @Autowired
    private MyFavoriteService myFavoriteService;


    @Override
    public Result add(ServerPublishAudit serverPublishAudit) {
        if (serverPublishAudit == null) {
            return Result.fail("参数错误");
        }
        serverPublishAudit.setCreateTime(new Date());
        //删除之前待审核的记录，保证一个应用/插件只有一条审核记录
        if (this.remove(Wrappers.init()
                .where(StringUtils.isNotBlank(serverPublishAudit.getApplicationId()), SERVER_PUBLISH_AUDIT.APPLICATION_ID.eq(serverPublishAudit.getApplicationId()))
                .and(StringUtils.isNotBlank(serverPublishAudit.getPluginId()), SERVER_PUBLISH_AUDIT.PLUGIN_ID.eq(serverPublishAudit.getPluginId()))
                .and(SERVER_PUBLISH_AUDIT.MESSAGE_SOURCE.eq(serverPublishAudit.getMessageSource()))
                .and(SERVER_PUBLISH_AUDIT.AUDIT_STATUS.eq(2))
                .and(SERVER_PUBLISH_AUDIT.STATUS.eq(0)))) {
            log.info("删除待审核记录成功");
        }
        return Result.success(this.save(serverPublishAudit));
    }

    @Override
    public Result delete(ServerPublishAudit serverPublishAudit) {
        if (serverPublishAudit == null) {
            return Result.fail("参数错误");
        }
        if (this.removeByIds(serverPublishAudit.getDelIds())) {
            return Result.success();
        }
        return Result.fail();
    }

    @Override
    public Result update(ServerPublishAudit serverPublishAudit) {
        if (serverPublishAudit == null || serverPublishAudit.getId() == null) {
            return Result.fail("参数错误");
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        serverPublishAudit.setAuditTime(new Date());
        serverPublishAudit.setAuditUserId(tokenUserInfo.getId() + "");
        serverPublishAudit.setAuditUserName(tokenUserInfo.getUserName());
        //如果是审核通过 需要更新应用的上架字段 publish_app_store
        if(serverPublishAudit.getAuditStatus() == 1){
            final ServerPublishAudit byId = getById(serverPublishAudit.getId());
            if(byId.getMessageSource() == 1){
                appInfoService.updateAppStoreByApplicationId(byId.getApplicationId(), 1);
            }else if(byId.getMessageSource() == 2){
                componentService.updateAppStoreByPluginId(byId.getPluginId(), 1);
            }
            log.info("应用审核记录ID：{} 上架应用商店通过...", serverPublishAudit.getId());
        }else {
            log.warn("应用审核记录ID：{} 上架应用商店被驳回...", serverPublishAudit.getId());
        }

        //todo: 消息通知业务
        return Result.success(this.updateById(serverPublishAudit));
    }

    @Override
    public Result<Page<ServerPublishAudit>> getListPage(ServerPublishAuditPageParam param) {
        if (param == null) {
            return Result.fail("参数错误");
        }
        if (param.getPageNo() == null) {
            param.setPageNo(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        Wrappers wrappers = Wrappers.init()
                .where(SERVER_PUBLISH_AUDIT.AUDIT_STATUS.in(param.getAuditStatusList()))
                .and(SERVER_PUBLISH_AUDIT.STATUS.eq(0))
                .and(SERVER_PUBLISH_AUDIT.MESSAGE_SOURCE.eq(param.getMessageSource()))
                .and(StringUtils.isNotBlank(param.getApplicationName()), SERVER_PUBLISH_AUDIT.APPLICATION_NAME.like("%" + param.getApplicationName() + "%"))
                .and(StringUtils.isNotBlank(param.getSubmitTimeStart()), SERVER_PUBLISH_AUDIT.CREATE_TIME.ge(param.getSubmitTimeStart()))
                .and(StringUtils.isNotBlank(param.getSubmitTimeEnd()), SERVER_PUBLISH_AUDIT.CREATE_TIME.le(param.getSubmitTimeEnd()))
                .and(StringUtils.isNotBlank(param.getPublishType()), SERVER_PUBLISH_AUDIT.PUBLISH_TYPE.le(param.getPublishType()))
                .orderBy(SERVER_PUBLISH_AUDIT.CREATE_TIME.desc());
        Page<ServerPublishAudit> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public Result getDataById(Long id, String upAnddown) {
        ServerPublishAudit byId = getById(id);
        if (byId == null) {
            return Result.fail("数据不存在");
        }
        Long middId = id;
        if (upAnddown != null) {
            //查询所有需要审核的数据
            Wrappers wrappers = Wrappers.init()
//                    .select("id")
                    .where(SERVER_PUBLISH_AUDIT.AUDIT_STATUS.eq(2))
                    .and(SERVER_PUBLISH_AUDIT.STATUS.eq(0))
                    .and(SERVER_PUBLISH_AUDIT.MESSAGE_SOURCE.eq(byId.getMessageSource()))
                    .or(SERVER_PUBLISH_AUDIT.ID.eq(id))
                    .orderBy(SERVER_PUBLISH_AUDIT.CREATE_TIME.desc());
            List<ServerPublishAudit> list = list(wrappers);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() - id == 0) {
                    if ("UP".equals(upAnddown)) {
                        if (i > 0) {
                            middId = list.get(i - 1).getId();
                            break;
                        } else {
                            return Result.fail("已经是第一条数据了");
                        }
                    } else if ("DOWN".equals(upAnddown)) {
                        if (i < list.size() - 1) {
                            middId = list.get(i + 1).getId();
                            break;
                        } else {
                            return Result.fail("已经是最后一条数据了");
                        }
                    }
                }
            }
            byId = getById(middId);
        }
        if (byId.getMessageSource() == 1) {  //应用
            final ApplicationInfo byAppId = appInfoService.getByAppId(byId.getApplicationId());
            //应用信息
            byId.setApplicationInfo(byAppId);

            //配置项信息
            //模型
            List<LlmInfo> llmInfoList = new ArrayList<>();
            llmInfoList.add(llmInfoService.getByModelId(byAppId.getModelId()));
            llmInfoList = llmInfoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            byId.setLlmInfoList(llmInfoList);

            //插件
            Set<String> pluginIds = new HashSet<>();
            AppConfigListParam appConfigListParam = new AppConfigListParam();
            appConfigListParam.setApplicationId(byId.getApplicationId());
            applicationPluginDataService.getApplicationPluginDataList(appConfigListParam).getData().stream().filter(p -> p.getStatus().equals("是")).forEach(p -> pluginIds.add(p.getPluginId()));
            List<ApplicationPlugin> listRes = new ArrayList<>();
            List<ApplicationPlugin> applicationPluginList = applicationPluginService.getApplicationPluginList(appConfigListParam).getData();
            applicationPluginList.stream().filter(p -> pluginIds.contains(p.getPluginId())).forEach(p -> listRes.add(p));
            byId.setApplicationPluginList(listRes);

            //知识库
            List<String> knowledgeIds = applicationKnowledgeService.getListByApplicationId(byId.getApplicationId(), "knowledge");
            if (CollectionUtils.isEmpty(knowledgeIds)) {
                byId.setKnowledgeInfoList(new ArrayList<>());
            } else {
                byId.setKnowledgeInfoList(knowledgeInfoService.getByIds(knowledgeIds));
            }
            //工作流
            List<String> knowledgeIds2 = applicationKnowledgeService.getListByApplicationId(byId.getApplicationId(), "workflow");
            if (CollectionUtils.isEmpty(knowledgeIds2)) {
                byId.setComponentInfoList(new ArrayList<>());
            } else {
                byId.setComponentInfoList(componentService.selectById(knowledgeIds2));
            }
        } else if (byId.getMessageSource() == 2) { //插件
            List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(byId.getPluginId()));
            if (CollectionUtil.isNotEmpty(componentDtoList)) {
                byId.setComponent(componentDtoList.get(0));
            } else {
                return Result.fail("插件记录不存在,请核实");
            }
        }
        return Result.success(byId);
    }

    @Override
    public Result getStoreDataByApplicationId(String applicationId) {
        if (StringUtils.isBlank(applicationId)) {
            return Result.fail("应用ID不能为空");
        }
        ServerPublishAudit byId = ServerPublishAudit.create();
        final ApplicationInfo byAppId = appInfoService.getByAppId(applicationId);
        if(byAppId == null){
            return Result.fail("应用记录不存在,请核实");
        }
        byId.setIntroduce(byAppId.getIntroduce());
        byId.setFacadeImageUrl(byAppId.getFacadeImageUrl());
        byId.setCreateUserName(byAppId.getCreateUser());
        byId.setApplicationType(byAppId.getType());
        byId.setPublishType(byAppId.getPublishType());
        byId.setApplicationName(byAppId.getApplicationName());
        byId.setApplicationId(byAppId.getApplicationId());

        //应用信息
        byId.setApplicationInfo(byAppId);

        //配置项信息
        //模型
        List<LlmInfo> llmInfoList = new ArrayList<>();
        llmInfoList.add(llmInfoService.getByModelId(byAppId.getModelId()));
        llmInfoList = llmInfoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        byId.setLlmInfoList(llmInfoList);

        //插件
        Set<String> pluginIds = new HashSet<>();
        AppConfigListParam appConfigListParam = new AppConfigListParam();
        appConfigListParam.setApplicationId(byId.getApplicationId());
        applicationPluginDataService.getApplicationPluginDataList(appConfigListParam).getData().stream().filter(p -> p.getStatus().equals("是")).forEach(p -> pluginIds.add(p.getPluginId()));
        List<ApplicationPlugin> listRes = new ArrayList<>();
        List<ApplicationPlugin> applicationPluginList = applicationPluginService.getApplicationPluginList(appConfigListParam).getData();
        applicationPluginList.stream().filter(p -> pluginIds.contains(p.getPluginId())).forEach(p -> listRes.add(p));
        byId.setApplicationPluginList(listRes);

        //知识库
        List<String> knowledgeIds = applicationKnowledgeService.getListByApplicationId(byId.getApplicationId(), "knowledge");
        if (CollectionUtils.isEmpty(knowledgeIds)) {
            byId.setKnowledgeInfoList(new ArrayList<>());
        } else {
            byId.setKnowledgeInfoList(knowledgeInfoService.getByIds(knowledgeIds));
        }
        //工作流
        List<String> knowledgeIds2 = applicationKnowledgeService.getListByApplicationId(byId.getApplicationId(), "workflow");
        if (CollectionUtils.isEmpty(knowledgeIds2)) {
            byId.setComponentInfoList(new ArrayList<>());
        } else {
            byId.setComponentInfoList(componentService.selectById(knowledgeIds2));
        }

        //对话数
        RecordPageParam param = new RecordPageParam();
        param.setApplicationId(byId.getApplicationId());
        param.setPageNo(1);
        param.setPageSize(1);
        Result<EsPageInfo<Dialogue>> result = dialogueRecordService.getRecord(param);
        byId.setDialogueCount(result.getData() != null ? result.getData().getTotal() : 0L);

        //收藏数  todo:
        long count = myFavoriteService.count(new QueryWrapper()
                .where(MY_FAVORITE.APPLICATION_ID.eq(applicationId))
                .and(MY_FAVORITE.FAVORITE_FLAG.eq(1)));
        byId.setCollectCount(count);

        return Result.success(byId);
    }
}