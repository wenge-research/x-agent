package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.StringUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ModelPluginApiPageParam;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.ModelPluginApiAuthUser;
import com.wenge.model.entity.ModelPluginApiManage;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.mapper.ModelPluginApiManageMapper;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.ModelPluginApiAuthUserService;
import com.wenge.model.service.ModelPluginApiService;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.workflow.component.file.File;
import com.wenge.model.workflow.component.file.FileTransferMethod;
import com.wenge.model.workflow.component.file.FileType;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.ModelPluginApiAuthUserTableDef.MODEL_PLUGIN_API_AUTH_USER;
import static com.wenge.model.entity.table.ModelPluginApiManageTableDef.MODEL_PLUGIN_API_MANAGE;

@Slf4j
@Service
public class ModelPluginApiServiceImpl extends ServiceImpl<ModelPluginApiManageMapper, ModelPluginApiManage> implements ModelPluginApiService {

    @Autowired
    private Map<String, LlmStrategy> llmStrategyMap;

    @Autowired
    private LlmInfoService llmInfoService;

    @Autowired
    private ModelPluginApiAuthUserService modelPluginApiAuthUserService;

    @Override
    public Result addModelPluginApi(ModelPluginApiManage param) {
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        if (StringUtils.isBlank(param.getModelPluginApiId())) {
            param.setModelPluginApiId(IdUtil.simpleUUID());
        }
        //名称重复校验
        boolean exists = Wrappers.of(mapper)
                .where(MODEL_PLUGIN_API_MANAGE.NAME.eq(param.getName()))
                .where(MODEL_PLUGIN_API_MANAGE.TYPE.eq(param.getType()))
                .and(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.ne(param.getModelPluginApiId()).when(StringUtils.isNotBlank(param.getModelPluginApiId())))
                .exists();
        // final ModelPluginApiManage modelPluginApiManage = getOne(MODEL_PLUGIN_API_MANAGE.NAME.eq(param.getName()));
        if (exists) {
            return Result.fail("名称重复，请重新输入");
        }
        if (StringUtils.isBlank(param.getCreateUser())) {
            param.setCreateUser(tokenUserInfo.getId() + "");
        }
        if (StringUtils.isBlank(param.getModelId())) {
            param.setCreateTime(DateUtil.getCurrentTime());
        }
        ModelPluginApiManage one = Wrappers.of(mapper)
                .and(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.eq(param.getModelPluginApiId()))
                .one();
        if (null != one) {
            param.setId(one.getId());
        }
        param.setUpdateUser(tokenUserInfo.getId() + "");
        param.setUpdateTime(param.getCreateTime());

        param.setPathApi("/modelPluginApi/capability/" + param.getModelPluginApiId());
        saveOrUpdate(param);
        return Result.success(param);
    }

    @Override
    public Result getModelPluginApiList(ModelPluginApiPageParam param){
        Wrappers<ModelPluginApiManage> wrapper = new Wrappers<>();
        wrapper.and(StringUtil.isNotBlank(param.getModelPluginApiId()),MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.eq(param.getModelPluginApiId()));
        wrapper.and(StringUtil.isNotBlank(param.getCategory()),MODEL_PLUGIN_API_MANAGE.CATEGORY.eq(param.getCategory()));
        wrapper.and(StringUtil.isNotBlank(param.getEnableFlag()), MODEL_PLUGIN_API_MANAGE.ENABLE_FLAG.eq(param.getEnableFlag()));
        wrapper.and(StringUtil.isNotBlank(param.getName()), MODEL_PLUGIN_API_MANAGE.NAME.like(param.getName()));
        wrapper.and(StringUtil.isNotBlank(param.getType()), MODEL_PLUGIN_API_MANAGE.TYPE.eq(param.getType()));
        wrapper.orderBy(MODEL_PLUGIN_API_MANAGE.CREATE_TIME.desc());
        final Page<ModelPluginApiManage> pluginApiManagePage = this.page(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
        return Result.success(pluginApiManagePage);
    }

    @Override
    public Result updateModelPluginApi(ModelPluginApiManage modelPluginApiManage){

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (StringUtils.isNotBlank(modelPluginApiManage.getName())) {
            boolean exists = ModelPluginApiManage.create()
                    .where(StringUtils.isNotBlank(modelPluginApiManage.getName()), MODEL_PLUGIN_API_MANAGE.NAME.eq(modelPluginApiManage.getName()))
                    .and(MODEL_PLUGIN_API_MANAGE.DELETE_FLAG.eq("0"))
                    .and(MODEL_PLUGIN_API_MANAGE.ID.ne(modelPluginApiManage.getId()))
                    .exists();
            if (exists) {
                return Result.fail("名称重复，请重新输入");
            }
        }
        modelPluginApiManage.setUpdateUser(tokenUserInfo.getId() + "");
        modelPluginApiManage.setUpdateTime(DateUtil.getCurrentTime());
        updateById(modelPluginApiManage);
        return Result.success();
    }

    @Override
    public Result deleteModelPluginApi(List<String> idList){
        if (CollectionUtil.isEmpty(idList)) {
            return Result.success();
        }
        String id = idList.get(0);
        // 通过自增id删除
        if (NumberUtil.isNumber(id)) {
            UpdateChain.create(mapper)
                    .where(MODEL_PLUGIN_API_MANAGE.ID.in(idList))
                    .remove();
        } else {
            // 通过业务id删除
            UpdateChain.create(mapper)
                    .where(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.in(idList))
                    .remove();
        }

        return Result.success();
    }


    @Override
    public Result getModelPluginApiById(String id) {
        Wrappers<ModelPluginApiManage> wrapper = new Wrappers<>();
        wrapper.and(MODEL_PLUGIN_API_MANAGE.ID.eq(id));
        final ModelPluginApiManage modelPluginApiManage = this.getOne(wrapper);
        return Result.success(modelPluginApiManage);
    }


    public ModelPluginApiManage getModelPluginApiByModelPluginApiId(String modelPluginApiId){
        if (modelPluginApiId == null) {
            return null;
        }
        Wrappers<ModelPluginApiManage> wrapper = new Wrappers<>();
        wrapper.and(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.eq(modelPluginApiId));
//        wrapper.and(MODEL_PLUGIN_API_MANAGE.ENABLE_FLAG.eq("0"));
        final ModelPluginApiManage modelPluginApiManage = this.getOne(wrapper);
        return modelPluginApiManage;
    }


    /**
     * @param modelPluginApiId
     * @param jsonObjectParam
     * @author: caohaifeng
     * @date: 2024/11/5 16:05
     * @Description: 模型插件调用API-通用
     * 根基modelPluginApiId找到对应的插件模型指令，调用指定的模型进行结果响应
     * @Version:1.0
     */
    @Override
    public Result handleCapability(HttpServletRequest request, String modelPluginApiId, com.alibaba.fastjson.JSONObject jsonObjectParam) {
        boolean authStatus = true;
//         获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo != null && tokenUserInfo.getId() != null) {
            authStatus = false;
        }

        final String authorization = request.getHeader("Authorization");
        final ModelPluginApiManage modelPluginApiManage = getModelPluginApiByModelPluginApiId(modelPluginApiId);
        String modelInstruction = modelPluginApiManage.getModelInstruction();
        StringBuffer sb = new StringBuffer(modelInstruction);
        if (!authStatus) {
            modelPluginApiManage.setAuthMethod("0"); //系统页面调用不鉴权
        }

        Result resultAuth = checkAuth(modelPluginApiManage, authorization);
        if (!resultAuth.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            return resultAuth;
        }

        //入参不为空 进行指令替换
        Result result = checkParameters(modelPluginApiManage, jsonObjectParam, modelInstruction, sb);
        if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            return result;
        } else {
            modelInstruction = result.getData().toString();
        }

        final LlmInfo byModelId = llmInfoService.getByModelId(modelPluginApiManage.getModelId());
        LlmStrategy llmStrategy = llmStrategyMap.get(byModelId.getModelCode());
        if (null == llmStrategy) {
            return Result.error("404", "模型未找到，请联系管理员");
        }
        String resultJson = "";
        try {
            StepEntity changeStep = new StepEntity();
            cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
            stepParam.set(LLM_PARAM_MODEL_ID, byModelId.getModelId());
            // 大模型是否需要json格式化
            stepParam.set(LLM_JSON_FLAG, StringConstant.BLANK);
            stepParam.set(LLM_PARAM_OBJECT, JSON.parseObject(JSON.toJSONString(byModelId)));
            changeStep.setParam(stepParam);

            log.info(modelInstruction);
            resultJson = llmStrategy.generate(sb.toString(), null, changeStep, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //记录操作记录 用于统计使用
        }
        try {
            return Result.success(JSON.parse(resultJson.replaceAll("```", "").replace("json", "")));
        }catch (Exception e){
            return Result.success(resultJson);
        }
    }

    @Override
    public Result findByPluginId(String pluginId) {
        if (StringUtils.isBlank(pluginId)) {
            return Result.success();
        }
        Wrappers<ModelPluginApiManage> wrapper = new Wrappers<>();
        wrapper.and(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.eq(pluginId));
        final ModelPluginApiManage modelPluginApiManage = this.getOne(wrapper);
        return Result.success(modelPluginApiManage);
    }

    @Override
    public void copy(ModelPluginApiManage param) {
        if (StringUtils.isBlank(param.getModelPluginApiId())) {
           return;
        }

        ModelPluginApiManage one = Wrappers.of(mapper)
                .where(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.eq(param.getModelPluginApiId()))
                .one();

        if (null == one) {
            return;
        }

        one.setModelPluginApiId(param.getNewModelPluginApiId());
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        //名称重复校验
        boolean exists = Wrappers.of(mapper)
                .where(MODEL_PLUGIN_API_MANAGE.NAME.eq(param.getName()))
                .where(MODEL_PLUGIN_API_MANAGE.TYPE.eq(param.getType()))
                .and(MODEL_PLUGIN_API_MANAGE.MODEL_PLUGIN_API_ID.ne(param.getModelPluginApiId()).when(StringUtils.isNotBlank(param.getModelPluginApiId())))
                .exists();
        if (exists) {
            return;
        }
        one.setCreateUser(tokenUserInfo.getId() + "");
        one.setCreateTime(DateUtil.getCurrentTime());
        one.setUpdateUser(tokenUserInfo.getId() + "");
        one.setUpdateTime(DateUtil.getCurrentTime());

        one.setPathApi("/modelPluginApi/capability/" + one.getModelPluginApiId());
        saveOrUpdate(one);
    }


    /**
     * @author: caohaifeng
     * @date: 2024/11/14 15:09
     * @Description: 鉴权检查
     * @Version:1.0
     **/
    public Result checkAuth(ModelPluginApiManage modelPluginApiManage, String apiToken) {
        if (modelPluginApiManage == null) {
            return Result.error("402", "请求API不存在/未启用");
        }
        if (StringUtils.isBlank(modelPluginApiManage.getModelId())) {
            return Result.error("403", "模型不存在/未启用");
        }

        if (StringUtils.isBlank(modelPluginApiManage.getAuthMethod())) {
            return Result.error("403", "请求API未配置鉴权方式");
        } else if ("0".equals(modelPluginApiManage.getAuthMethod())) {
            log.info("当前API无需鉴权...");
        } else if ("1".equals(modelPluginApiManage.getAuthMethod())) {
            log.info("当前API 进行鉴权[API-KEY]...");

            if (StringUtils.isBlank(apiToken) || apiToken == null) {
                return Result.error("401", "缺失秘钥");
            }
            Wrappers<ModelPluginApiAuthUser> wrapper = new Wrappers<>();
            wrapper.and(MODEL_PLUGIN_API_AUTH_USER.MODEL_PLUGIN_API_ID.eq(modelPluginApiManage.getModelPluginApiId()));
            wrapper.and(MODEL_PLUGIN_API_AUTH_USER.SECRET_KEY.eq(apiToken));
            final ModelPluginApiAuthUser modelPluginApiAuthUser = modelPluginApiAuthUserService.getOne(wrapper);

            Result result = checkApiToken(apiToken, modelPluginApiAuthUser);
            if (!Result.success().getCode().equals(result.getCode())) {
                return result;
            }
            if (modelPluginApiAuthUser == null) {
                return Result.error("401", "秘钥无效");
            }
            if (modelPluginApiAuthUser.getEnableFlag().equals("0")) {
                return Result.error("401", "秘钥未启用");
            }
        }
        return Result.success();
    }

    /**
     * @author: caohaifeng
     * @date: 2024/11/14 15:08
     * @Description: 参数校验  占位符替换
     * @Version:1.0
     **/
    private Result checkParameters(ModelPluginApiManage modelPluginApiManage, com.alibaba.fastjson.JSONObject jsonObjectParam, String modelInstruction, StringBuffer sb) {
        if (StringUtils.isNotEmpty(modelPluginApiManage.getDescParam())) {
            com.alibaba.fastjson2.JSONArray jsonArray = JSON.parseArray(modelPluginApiManage.getDescParam());
            for (Object object : jsonArray) {
                JSONObject jsonObject = JSON.parseObject(object.toString());
                String name_en = jsonObject.getString("name_en");
                String feildType = jsonObject.getString("feild_type");
                Long max_length = jsonObject.getLong("max_length");
                Boolean isRequired = jsonObject.getBoolean("isRequired");
                Object obj = jsonObjectParam.get(name_en);
                if (feildType.equals("file") && obj != null) {
                    try {
                        List<File> files = buildFile(ListUtil.toList(obj.toString()));
                        List<String> result = files.stream().map(this::getFileContent).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(result)) {
                            obj = result.stream().collect(Collectors.joining("\n"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 判断是否必传
                if (isRequired != null && isRequired) {
                    if (obj == null || StringUtils.isBlank(obj + "")) {
                        return Result.error("304", "参数：{" + name_en + "} 是必填字段");
                    }
                }
                // 长度不能越界
                if (max_length != null) {
                    if (obj != null && (obj + "").length() > max_length) {
                        return Result.error("304", "参数：{" + name_en + "} 是长度不能超过：" + max_length);
                    }
                }
                modelInstruction = modelInstruction.replaceAll("\\{" + name_en + "}", obj + "");
            }
        }
        sb.append(modelInstruction);
        return Result.success(modelInstruction);
    }

    /**
     * 构建文件
     * @param filePathList
     * @return
     */
    private List<File> buildFile(List<String> filePathList) {
        if (CollectionUtil.isNotEmpty(filePathList)) {
            return filePathList.stream().map(filePath -> {
                File file = new File();
                file.setType(FileType.DEFAULT);
                file.setTransferMethod(FileTransferMethod.REMOTE_URL);
                file.setRemoteUrl(filePath);
                int lastIndexOf = filePath.lastIndexOf("/");
                String fileName = filePath.substring(lastIndexOf + 1);
                file.setFilename(fileName);
                int indexOf = filePath.lastIndexOf(".");
                String extension = filePath.substring(indexOf + 1);
                file.setExtension(extension);
                return file;
            }).collect(Collectors.toList());
        }
        return ListUtil.toList();
    }


    /**
     * 获取文件内容的模板方法
     * @param file
     * @return
     */
    public String getFileContent(File file) {
        List<String> contentList = file.getTransferMethod().getHandler().apply(file.getRemoteUrl());
        return String.join("\n", contentList);
    }


    /**
     * 验证密钥
     *
     * @return
     */
    @Override
    public Result checkApiToken(String apiToken, ModelPluginApiAuthUser modelPluginApiAuthUser) {
        if (null == modelPluginApiAuthUser) {
            Wrappers<ModelPluginApiAuthUser> wrapper = new Wrappers<>();
            wrapper.and(MODEL_PLUGIN_API_AUTH_USER.SECRET_KEY.eq(apiToken))
                    .and(MODEL_PLUGIN_API_AUTH_USER.ENABLE_FLAG.eq("1"));
            modelPluginApiAuthUser = modelPluginApiAuthUserService.getOne(wrapper);
        }
        if (modelPluginApiAuthUser == null) {
            return Result.error("401", "秘钥无效");
        }
        if (modelPluginApiAuthUser.getEnableFlag().equals("0")) {
            return Result.error("401", "秘钥未启用");
        }
        // 定义日期时间格式
        String dateFormatStr = "yyyy-MM-dd HH:mm:ss";
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr, Locale.getDefault());
        // 将字符串时间转换为 Date 对象
        try {
            Date date = sdf.parse(modelPluginApiAuthUser.getExpireTime());
            if (date.getTime() <= new Date().getTime()) {
                return Result.error("401", "秘钥已过期");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Result.success(modelPluginApiAuthUser);
    }

}