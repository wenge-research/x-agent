package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.McpFindParam;
import com.wenge.model.dto.param.McpServerListParam;
import com.wenge.model.dto.param.TestApiParam;
import com.wenge.model.entity.McpFunction;
import com.wenge.model.entity.McpParameter;
import com.wenge.model.entity.McpServer;
import com.wenge.model.entity.McpServerUser;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.mapper.McpServerMapper;
import com.wenge.model.mapper.McpServerUserMapper;
import com.wenge.model.service.McpFunctionService;
import com.wenge.model.service.McpParameterService;
import com.wenge.model.service.McpServerService;
import com.wenge.model.service.McpServerUserService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.McpFunctionTableDef.MCP_FUNCTION;
import static com.wenge.model.entity.table.McpServerTableDef.MCP_SERVER;
import static com.wenge.model.entity.table.McpServerUserTableDef.MCP_SERVER_USER;
import static com.wenge.oauth.enums.OwnerTypeEnum.*;

/**
 * Description: mcp 服务服务实现类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:32:48
 */
@Service
@Slf4j
public class McpServerServiceImpl extends ServiceImpl<McpServerMapper, McpServer> implements McpServerService {


    @Value("${mcp.serviceApi:}")
    private String serviceApi;

    @Autowired
    private McpFunctionService mcpFunctionService;

    /**
     * mcp 服务数据库处理类
     */
    @Autowired
    private McpServerMapper mcpServerMapper;

    @Autowired
    private McpParameterService mcpParameterService;

    @Autowired
    private McpServerUserService mcpServerUserService;

    @Value("${mcp.buildMcpApi:}")
    private String buildMcpApi;

    @Autowired
    private UserService userService;
    
    @Autowired
    private McpServerUserMapper mcpServerUserMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addMcpServer(McpServer mcpServer) {
        if (StringUtils.isBlank(mcpServer.getMcpName())) {
            return Result.fail("服务名称不能为空");
        }

        if (StringUtils.isBlank(mcpServer.getInstallWay())) {
            return Result.fail("服务安装方式不能为空");
        }

        if (StringUtils.equals(mcpServer.getInstallWay(), "sse") && StringUtils.isBlank(mcpServer.getUrl())) {
            return Result.fail("服务地址不能为空");
        }

        if (StrUtil.isBlank(mcpServer.getMcpType())) {
            return Result.fail("服务类型不能为空");
        }

        if (null == mcpServer.getId()) {
            mcpServer.setMcpId(IdUtil.simpleUUID());
        }

        // 添加后是personal，预置后是official，群众没有权限进行操作
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
            mcpServer.setOwnerType(PERSONAL.getCode());
        } else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
            return Result.fail("无权限操作");
        } else {
            mcpServer.setOwnerType(PERSONAL.getCode());
        }

        boolean exists = Wrappers.of(mapper)
                .where(MCP_SERVER.MCP_NAME.eq(mcpServer.getMcpName()))
                .and(MCP_SERVER.MCP_ID.ne(mcpServer.getMcpId()).when(StringUtils.isNotBlank(mcpServer.getMcpId())))
                .exists();
        if (exists) {
            return Result.fail("服务名称已存在");
        }

        // 自定义
        if ("custom".equals(mcpServer.getInstallWay())) {
            List<McpFunction> mcpFunctionList = mcpServer.getMcpFunctionList();
            if (CollectionUtil.isEmpty(mcpFunctionList)) {
                return Result.fail("请添加工具");
            }
            Result result = setServerUrl(mcpServer);
            if (!Result.success().getCode().equals(result.getCode())) {
                return result;
            }
        }

        try {
            // 获取工具
            saveMcpFunction(mcpServer);
            saveOrUpdate(mcpServer);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
        return Result.success();
    }

    @Override
    public Result getMcpServerList(McpServerListParam mcpServer) {
        // 调用处：菜单MCP服务页面【personalGrantTenant】、工作流或应用中引用MCP时页面【官方official，我的personal】
        // 参数校验
        if (mcpServer == null) {
            return Result.fail("请求参数为空");
        }
        if (mcpServer.getPageNo() == null || mcpServer.getPageNo() < 1) {
            return Result.fail("页面参数为空或错误");
        }
        if (mcpServer.getPageSize() == null || mcpServer.getPageSize() < 1) {
            return Result.fail("页面大小参数为空或错误");
        }

        // 返回当前用户的mcp服务
        //List<McpServerUser> mcpServerUsers = mcpServerUserService.myMcpServerUserList();
        // 获取当前用户的mcp服务id的不重复的已开通的集合
        // Set<String> mcpIds = mcpServerUsers.stream().map(McpServerUser::getMcpId).collect(Collectors.toSet());
        // 获取当前用户的mcp服务id和状态
        // Map<String, Integer> userMcpIdStatus = mcpServerUsers.stream()
        //        .collect(Collectors.toMap(McpServerUser::getMcpId, McpServerUser::getStatus));
//        Wrappers mcpServerWrappers = Wrappers.of(mapper)
//                .where(StringUtils.isNotBlank(mcpServer.getMcpName()), MCP_SERVER.MCP_NAME.like(mcpServer.getMcpName()))
//                .and(StringUtils.isNotBlank(mcpServer.getCreateBy()), MCP_SERVER.CREATE_USER_NAME.eq(mcpServer.getCreateBy()))
//                .and(CollectionUtil.isNotEmpty(mcpIds) && "2".equals(mcpServer.getMcpServerUserStatus()), MCP_SERVER.MCP_ID.in(mcpIds))
//                .and("0".equals(mcpServer.getMcpServerUserStatus()), MCP_SERVER.MCP_ID.notIn(mcpIds))
//                .and(StringUtils.isNotBlank(mcpServer.getType()), MCP_SERVER.MCP_TYPE.eq(mcpServer.getType()))
//                // 菜单mcp服务界面的时候能看到所有个人/租户/授权的mcp服务
//                //.and(null != ownerType && ownerType.equals(OwnerTypeEnum.PERSONAL_GRANT_TENANT.getCode()), MCP_SERVER.STATUS.eq(2).or(MCP_SERVER.STATUS.eq(1)))
//                // MCP服务引用查看自己的mcp服务时，只显示自己的而且开通的
//                //.and(null != ownerType && ownerType.equals(OwnerTypeEnum.PERSONAL.getCode()),
//                //        MCP_SERVER.STATUS.eq(2)
//                //        .and(MCP_SERVER.CREATE_USER_NAME.eq(AppContextHolder.getTokenUserInfo().getAccountName())))
//                .orderBy((MCP_SERVER.CREATE_TIME.desc()));
        //page(Page.of(mcpServer.getPageNo(), mcpServer.getPageSize()));

        String ownerType = mcpServer.getOwnerType();
        String mcpServerUserStatus = mcpServer.getMcpServerUserStatus();
        Wrappers mcpServerWrappers = Wrappers.of(mapper)
                .where("1".equals(mcpServerUserStatus), MCP_SERVER.STATUS.eq("2")) // 已开通,上架
                .and("0".equals(mcpServerUserStatus), MCP_SERVER.STATUS.eq("1")) // 未开通，暂存
                .and(StringUtils.isNotBlank(mcpServer.getMcpName()), MCP_SERVER.MCP_NAME.like(mcpServer.getMcpName())) // 模糊查询
                .and(StringUtils.isNotBlank(mcpServer.getType()), MCP_SERVER.MCP_TYPE.eq(mcpServer.getType())); // 分类查询


        if (StringUtils.isNotBlank(mcpServer.getOrder()) && StringUtils.isNotBlank(mcpServer.getSort())) {
            mcpServerWrappers.orderBy(mcpServer.getOrder() + " " + mcpServer.getSort());
        }else {
            mcpServerWrappers.orderBy(MCP_SERVER.CREATE_TIME.desc()); // 默认按更新时间排序
        }

        // 根据权限查询,ownerType=official时所有用户（超管、普通用户、路人）只能查到官方的，ownerType=personal时所有人只能查到个人
        // 其他情况，ownerType=personalGrantTenant、personalGrant时，只对普通用户有效，对超管无效
        PermissionControlUtils.buildPermission(mcpServerWrappers, BusinessPermissionEnum.MCP, OwnerTypeEnum.getByCode(mcpServer.getOwnerType()));

        Page<McpServer> page = mcpServerMapper.paginate(new Page<>(mcpServer.getPageNo(), mcpServer.getPageSize()), mcpServerWrappers);
        if (null != ownerType && !ownerType.equals(PERSONAL_GRANT_TENANT.getCode())) { // 菜单mcp界面不查询工具，流中引用mcp时下拉单可查看工具
            for (McpServer record : page.getRecords()) {
                StringParam param = new StringParam();
                param.setParam(record.getMcpId());
                Result result = getDetail(param);
                McpServer mcpServerWithFunctionList = (McpServer) result.getData();
                List<McpFunction> functionList = mcpServerWithFunctionList.getMcpFunctionList();
                record.setMcpFunctionList(functionList);
            }
        }

        return Result.success(page);
//        for (McpServer record : page.getRecords()) {
//            if (CollectionUtil.isNotEmpty(userMcpIdStatus)) {
//                record.setMcpServerUserStatus(userMcpIdStatus.getOrDefault(record.getMcpId(), 0)); // 目前通过mcp_server表的status字段判断是否开通
//            }
//        }

        // LambdaQueryWrapper<McpServer> wrapper = Wrappers.lambdaQuery(McpServer.class);
        // Page<McpServer> page = page(Page.of(mcpServer.getPageNo(), mcpServer.getPageSize()), wrapper);
    }

    @Override
    public Result getMyMcpServerList(McpServerListParam mcpServer) {
        // 查询关联的Mcp
        List<McpServerUser> mcpServerUsers = mcpServerUserService.myMcpServerUserList();
        Set<String> mcpIds = mcpServerUsers.stream().map(McpServerUser::getMcpId).collect(Collectors.toSet());
        List<McpServer> mcpServerList = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(mcpIds)) {
            mcpServerList = Wrappers.of(mapper)
                    .where(StringUtils.isNotBlank(mcpServer.getMcpName()), MCP_SERVER.MCP_NAME.like(mcpServer.getMcpName()))
                    .and(MCP_SERVER.STATUS.eq("2"))
                    .and(MCP_SERVER.MCP_ID.in(mcpIds))
                    .orderBy(MCP_SERVER.CREATE_TIME.desc())
                    .list();
            if (mcpServerList != null) {
                mcpServerList.forEach(server -> server.setMcpServerUserStatus(1));
            }
        }
        return Result.success(mcpServerList);
    }

    @Override
    public List<McpServer> getMcpServerByIds(List<String> mcpServerIdList) {
        if (CollectionUtil.isEmpty(mcpServerIdList)) {
            return Lists.newArrayList();
        }

        return Wrappers.of(mapper)
                .where(MCP_SERVER.MCP_ID.in(mcpServerIdList))
                .list();
    }

    @Override
    public List<McpServer> getActifList(McpFindParam param) {
        return Wrappers.of(mapper)
                .where(MCP_SERVER.STATUS.eq("2"))
                .and(StringUtils.isNotBlank(param.getMcpName()), MCP_SERVER.MCP_NAME.like(param.getMcpName()))
                .orderBy(MCP_SERVER.CREATE_TIME.desc())
                .list();
    }

    @Override
    public Result testApi(TestApiParam param) {
        if (StringUtils.isBlank(param.getUrl())) {
            return Result.fail("请填写API地址");
        }
        if (StringUtils.isBlank(param.getMethod())) {
            return Result.fail("请填写API请求方式");
        }

        // 获取API地址
        String url = param.getUrl();
        if (null != param.getPath() && !param.getPath().isEmpty()) {
            for (Map.Entry<String, Object> entry : param.getPath()) {
                if (entry.getKey() != null && StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().toString())) {
                    url = url.replace("${" + entry.getKey() + "}", entry.getValue().toString());
                }
            }
        }

        HttpRequest request = null;
        switch (param.getMethod()) {
            case "GET":
                request = HttpUtil.createGet(url);
                JSONObject query = param.getQuery();
                if (query != null) {
                    request.form(query);
                }
                break;
            case "POST":
                request = HttpUtil.createPost(url);
                JSONObject body = param.getBody();
                if (body != null) {
                    request.body(JSONUtil.toJsonStr(body));
                }
                break;
            default:
                return Result.fail("请填写正确的API请求方式");
        }

        // 设置header
        if (param.getHeader() != null && !param.getHeader().isEmpty()) {
            for (Map.Entry<String, Object> header : param.getHeader()) {
                request.header(header.getKey(), header.getValue().toString());
            }
        }

        String body = StringConstant.BLANK;
        try {
            request.timeout(1000 * 60 * 6);
            request.setReadTimeout(1000 * 60 * 5);
            request.setConnectionTimeout(1000 * 60 * 2);
            log.info("请求地址,{},请求体参数:{}，请求入参:{}", param.getUrl(), JSONUtil.toJsonStr(param.getBody()), JSONUtil.toJsonStr(param.getQuery()));
            HttpResponse execute = request.execute();
            body = execute.body();
            log.info("请求结果:{}", execute.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            if (JSONUtil.isTypeJSONObject(body)) {
                return Result.success(JSONUtil.parseObj(body));
            } else if (JSONUtil.isTypeJSONArray(body)) {
                return Result.success(JSONUtil.parseArray(body));
            }
        }
        return Result.success(body);
    }

    @Override
    public Result setPreset(McpServerListParam mcpServerListParam) {
        if (mcpServerListParam == null) {
            return Result.fail("请求参数为空");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // 只有超管才能操作
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
            return Result.fail("无操作权限");
        }

        // 获取传入的提示词主键id
        Long Id = mcpServerListParam.getId();
        McpServer updateMcpServer = getById(Id);
        if (updateMcpServer == null) {
            return Result.fail("MCP服务不存在");
        }

        // 超级管理员集合
        List<OauthUser> superManageUser = userService.getSuperManageUser();
        List<String> accountNames = superManageUser.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());

        // 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
        if (accountNames.contains(updateMcpServer.getCreateUserName())){
            // 构造更新对象，已经是预置就取消，否则设置为预置,
            if (OFFICIAL.getCode().equals(updateMcpServer.getOwnerType())
            ) {
                updateMcpServer.setOwnerType(PERSONAL.getCode());
            } else {
                updateMcpServer.setOwnerType(OFFICIAL.getCode());
            }
        }else{
            return Result.fail("该MCP服务非超级管理员创建，无法进行预置操作");
        }

        // 执行更新
        updateById(updateMcpServer,false);

        return Result.success("服务预置更改成功");
    }


    @Transactional
    public Result updateMcpStatus(McpServerListParam mcpServerParam) {
        // 参数校验
        if (mcpServerParam == null
                || StringUtils.isBlank(mcpServerParam.getMcpId())
                || StringUtils.isBlank(mcpServerParam.getStatus())
                || ObjectUtils.isEmpty(mcpServerParam.getId())){
            return Result.fail("参数不能为空");
        }

        Long id = mcpServerParam.getId();
        String mcpId = mcpServerParam.getMcpId();
        String status = mcpServerParam.getStatus();

        // 更新mcp_server_user表
        List<McpServerUser> mcpServerUsers = mcpServerUserMapper.selectListByQuery(QueryWrapper.create().select().from(MCP_SERVER_USER).where(MCP_SERVER_USER.MCP_ID.eq(mcpId)));
        if (CollectionUtil.isNotEmpty(mcpServerUsers)) {
            mcpServerUsers.forEach(mcpServerUser -> {
                mcpServerUser.setStatus(status.equals("1") ? 0 : 1); // mcp_server表中1未开通2开通，mcp_server_user表中0未开通1开通
                mcpServerUserMapper.update(mcpServerUser);
            });
        }
        McpServer mcpServer = new McpServer();
        mcpServer.setId(Long.valueOf(id));
        mcpServer.setMcpId(mcpId);
        mcpServer.setStatus(status);
        mcpServerMapper.update(mcpServer);

        return Result.success();
    }

    @Override
    public Result updateMcpServer(McpServer mcpServer) {
        updateById(mcpServer);
        return Result.success();
    }

    @Override
    public Result deleteMcpServer(List<String> idList) {
        removeByIds(idList);
        return Result.success();
    }

    @Override
    public Result checkMcp(McpServer mcpServer) {
        mcpServer.setMcpId(IdUtil.simpleUUID());
        if (StringUtils.equals(mcpServer.getInstallWay(), "npx") || StringUtils.equals(mcpServer.getInstallWay(), "uvx")) {
            if (StringUtils.isBlank(mcpServer.getNpxUvxService())) {
                return Result.fail("MCP服务配置格式不能为空");
            }
            if (!JSONUtil.isTypeJSONObject(mcpServer.getNpxUvxService())) {
                return Result.fail("MCP服务配置格式不合法");
            }
        }
        List<McpFunction> mcpFunctionList;
        try {
            mcpFunctionList = getFunction(mcpServer);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
        return Result.success(mcpFunctionList);
    }

    @Override
    public Result getDetail(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.success();
        }

        McpServer mcpServer = Wrappers.of(mapper)
                .where(MCP_SERVER.MCP_ID.eq(param.getParam()))
                .one();

        McpServerUser mcpServerUser = mcpServerUserService.queryMcpServerUser(mcpServer.getMcpId());
        if (ObjectUtils.isNotEmpty(mcpServerUser)) {
            mcpServer.setMcpServerUserStatus(mcpServerUser.getStatus());
        } else {
            mcpServer.setMcpServerUserStatus(0);
        }

        Wrappers<Object> where = Wrappers.init()
                .where(MCP_FUNCTION.MCP_ID.eq(param.getParam()));

        List<McpFunction> list = mcpFunctionService.list(where);

        if (CollectionUtil.isNotEmpty(list)) {
            List<String> funIdList = list.stream().map(McpFunction::getFunctionId).distinct().collect(Collectors.toList());
            Map<String, List<McpParameter>> parameterByFunIdList = mcpParameterService.getParameterByFunIdList(funIdList);
            for (McpFunction mcpFunction : list) {
                mcpFunction.setInstallWay(mcpServer.getInstallWay());
                mcpFunction.setParameterList(parameterByFunIdList.getOrDefault(mcpFunction.getFunctionId(), ListUtil.toList()));
            }
        }
        mcpServer.setMcpFunctionList(list);
        return Result.success(mcpServer);
    }

    @Override
    public Result getListAndDetail(ListStringParam param) {
        if (CollectionUtil.isEmpty(param.getParam())) {
            return Result.success();
        }

        List<McpServer> mcpServerList = Wrappers.of(mapper)
                .where(MCP_SERVER.MCP_ID.in(param.getParam()))
                .list();

        Wrappers<Object> where = Wrappers.init()
                .where(MCP_FUNCTION.MCP_ID.in(param.getParam()));
        List<McpFunction> list = mcpFunctionService.list(where);
        if (CollectionUtil.isNotEmpty(mcpServerList)) {
            Map<String, List<McpFunction>> listMap = list.stream().collect(Collectors.groupingBy(McpFunction::getMcpId));
            mcpServerList.forEach(item -> {
                item.setMcpFunctionList(listMap.getOrDefault(item.getMcpId(), ListUtil.toList()));
            });
        }
        return Result.success(mcpServerList);
    }

    /**
     * 获取工具
     *
     * @param mcpServer
     */
    private void saveMcpFunction(McpServer mcpServer) {
        // 获取工具
        if (StringUtils.equals(mcpServer.getInstallWay(), "sse") && StringUtils.isAnyBlank(serviceApi, mcpServer.getUrl())) {
            return;
        }
        // 获取工具
        List<McpFunction> mcpFunctionList = getFunction(mcpServer);
        if (CollectionUtil.isNotEmpty(mcpFunctionList)) {
            mcpFunctionService.deleteMcpFunctionByMcpId(mcpServer.getMcpId());
            mcpParameterService.deleteMcpParameterByMcpId(mcpServer.getMcpId());
            mcpFunctionService.addMcpFunction(mcpFunctionList);

            List<McpParameter> parameterList = mcpFunctionList.stream().flatMap(p -> p.getParameterList().stream()).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(parameterList)) {
                mcpParameterService.addMcpParameter(parameterList);
            }
        }
    }

    /**
     * 获取工具
     *
     * @param mcpServer
     * @return
     */
    private List<McpFunction> getFunction(McpServer mcpServer) {
        // 自定义，从对象中直接获取工具
        if ("custom".equals(mcpServer.getInstallWay())) {
            return getFunFromCustom(mcpServer);
        }
        // 获取工具
        if (StringUtils.isAnyBlank(serviceApi)) {
            return Lists.newArrayList();
        }
        JSONObject param = new JSONObject();
        param.set("id", mcpServer.getMcpId());
        param.set("url", mcpServer.getUrl());
        param.set("type", mcpServer.getInstallWay());
        // 自定义，这里写死 sse
        if ("custom".equals(mcpServer.getInstallWay())) {
            param.set("type", "sse");
        }

        if (StringUtils.equals(param.getStr("type"), "sse")) {
            JSONObject newArg = new JSONObject();
            param.set("arg", newArg);
            newArg.set("apiKey", StringUtils.defaultIfBlank(mcpServer.getApiKey(), ""));
        } else if (StringUtils.equals(mcpServer.getInstallWay(), "npx") || StringUtils.equals(mcpServer.getInstallWay(), "uvx")) {
            if (!JSONUtil.isTypeJSONObject(mcpServer.getNpxUvxService())) {
                return Lists.newArrayList();
            }
            JSONObject npxJson = JSONUtil.parseObj(mcpServer.getNpxUvxService());
            param.set("arg", npxJson);
        }
        log.info("获取工具url:{},请求参数：{}", serviceApi, JSONUtil.toJsonStr(param));
        String post = HttpUtil.post(serviceApi, JSONUtil.toJsonStr(param));
        log.info("获取工具返回结果：{}", post);
        List<McpFunction> functionList = Lists.newArrayList();
        if (StringUtils.isNotBlank(post)) {
            JSONObject entries = JSONUtil.parseObj(post);
            if (entries.containsKey("error:")) {
                log.error("获取工具失败：{}", entries.getStr("error:"));
                throw new RuntimeException(entries.getStr("error:"));
            }
            if (entries.containsKey("tools") && JSONUtil.isTypeJSONArray(entries.getStr("tools"))) {
                JSONArray tools = entries.getJSONArray("tools");
                if (CollectionUtils.isNotEmpty(tools)) {

                    List<JSONObject> list = tools.toList(JSONObject.class);
                    functionList = list.stream().map(p -> {
                        // 创建工具对象
                        JSONObject function = p.getJSONObject("function");
                        McpFunction mcpFunction = new McpFunction();
                        mcpFunction.setMcpId(mcpServer.getMcpId());
                        mcpFunction.setFunctionName(function.getStr("name"));
                        mcpFunction.setFunctionId(IdUtil.simpleUUID());
                        mcpFunction.setDescription(function.getStr("description"));

                        JSONObject parameters = function.getJSONObject("parameters");
                        List<McpParameter> parameterList = ListUtil.toList();
                        mcpFunction.setParameterList(parameterList);
                        if (null != parameters) {
                            JSONObject properties = parameters.getJSONObject("properties");
                            JSONArray required = parameters.getJSONArray("required");
                            List<String> requiredList = required != null ? required.toList(String.class) : new ArrayList<>();
                            if (null != properties) {
                                properties.forEach((k, v) -> {
                                    JSONObject value = (JSONObject) v;
                                    // 创建参数对象
                                    McpParameter mcpParameter = new McpParameter();
                                    mcpParameter.setParamId(IdUtil.simpleUUID());
                                    mcpParameter.setFunctionId(mcpFunction.getFunctionId());
                                    mcpParameter.setMcpId(mcpServer.getMcpId());
                                    mcpParameter.setDescription(value.getStr("description"));
                                    mcpParameter.setType(value.getStr("type"));
                                    mcpParameter.setParamName(k);
                                    if (requiredList.contains(k)) {
                                        mcpParameter.setRequired(YesNoEnum.YES.getCode());
                                    } else {
                                        mcpParameter.setRequired(YesNoEnum.NO.getCode());
                                    }
                                    parameterList.add(mcpParameter);
                                });
                            }
                        }
                        return mcpFunction;
                    }).collect(Collectors.toList());
                }
            }
        }
        return functionList;
    }

    /**
     * 获取服务 url
     * @param mcpServer
     * @return
     */
    private Result setServerUrl(McpServer mcpServer) {
        List<McpFunction> mcpFunctionList = mcpServer.getMcpFunctionList();
        List<JSONObject> tools = mcpFunctionList.stream().map(p -> {
            JSONObject entries = new JSONObject();
            entries.set("name", p.getFunctionName());
            entries.set("description", p.getDescription());
            List<McpParameter> parameterList = p.getParameterList();

            // form:header/query/body/path
            List<McpParameter> headerList = parameterList.stream().filter(params -> "header".equals(params.getForm())).collect(Collectors.toList());
            List<McpParameter> bodyList = parameterList.stream().filter(params -> !"header".equals(params.getForm())).collect(Collectors.toList());
            List<JSONObject> parameters = Lists.newArrayList();
            if (CollectionUtil.isNotEmpty(bodyList)) {
                parameters = bodyList.stream().map(parameter -> {
                    JSONObject entries1 = new JSONObject();
                    entries1.set("name", parameter.getParamName());
                    entries1.set("type", parameter.getType());
                    entries1.set("description", parameter.getDescription());
                    entries1.set("optional", StringConstant.ONE.equals(parameter.getRequired()));
                    entries1.set("default", parameter.getDefaults());
                    return entries1;
                }).collect(Collectors.toList());
            }

            entries.set("params", parameters);
            JSONObject apiConfig = new JSONObject();
            apiConfig.set("method", p.getMethod());
            apiConfig.set("url", p.getUrl());
            JSONObject headers = new JSONObject();
            if (CollectionUtil.isNotEmpty(headerList)) {
                for (McpParameter mcpParameter : headerList) {
                    headers.set(mcpParameter.getParamName(), mcpParameter.getDefaults());
                }
                apiConfig.set("headers", headers);
            }

            entries.set("api_config", apiConfig);
            return entries;
        }).collect(Collectors.toList());

        String mcpName = mcpServer.getMcpName();
        JSONObject data = new JSONObject();
        JSONObject entries = new JSONObject();
        entries.set("service_name", mcpName);
        entries.set("tools", tools);
        data.set("data", entries);

        log.info("mcp.url:{}, 获取工具参数：{}", buildMcpApi, JSONUtil.toJsonStr(data));
        String post = HttpUtil.post(buildMcpApi, JSONUtil.toJsonStr(data));
        log.info("封装获取工具返回结果：{}", post);
        if (StringUtils.isBlank(post)) {
            return Result.fail("获取工具失败");
        }
        JSONObject response = JSONUtil.parseObj(post);
        if (!"success".equals(response.getStr("status"))) {
            return Result.fail("获取工具失败");
        }
        mcpServer.setUrl(response.getStr("url"));
        ThreadUtil.sleep(10 * 1000);
        return Result.success();
    }


    /**
     * 获取自定义服务接口
     * @param mcpServer
     * @return
     */
    private List<McpFunction> getFunFromCustom(McpServer mcpServer) {
        List<McpFunction> mcpFunctionList = mcpServer.getMcpFunctionList();

        if (CollectionUtil.isNotEmpty(mcpFunctionList)) {
            mcpFunctionList.forEach(fun -> {
                fun.setMcpId(mcpServer.getMcpId());
                fun.setInstallWay(mcpServer.getInstallWay());
                if (StringUtils.isBlank(fun.getFunctionId())) {
                    fun.setFunctionId(IdUtil.simpleUUID());
                }
                List<McpParameter> parameterList = fun.getParameterList();
                if (CollectionUtil.isNotEmpty(parameterList)) {
                    parameterList.forEach(param -> {
                        if (StringUtils.isBlank(param.getParamId())) {
                            param.setParamId(IdUtil.simpleUUID());
                        }
                        param.setFunctionId(fun.getFunctionId());
                        param.setMcpId(mcpServer.getMcpId());
                    });
                }
            });
        }
        return mcpFunctionList;
    }
}