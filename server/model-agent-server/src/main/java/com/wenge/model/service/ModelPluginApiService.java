package com.wenge.model.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ModelPluginApiPageParam;
import com.wenge.model.entity.ModelPluginApiAuthUser;
import com.wenge.model.entity.ModelPluginApiManage;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ModelPluginApiService extends IService<ModelPluginApiManage> {

    Result addModelPluginApi(ModelPluginApiManage param);

    Result getModelPluginApiList(ModelPluginApiPageParam param);

    Result updateModelPluginApi(ModelPluginApiManage modelPluginApiManage);

    Result deleteModelPluginApi(List<String> idList);

    Result getModelPluginApiById(String id);

    /**
     * @author: caohaifeng
     * @date: 2024/11/5 16:05
     * @Description: 模型插件调用API-通用
     * 根基modelPluginApiId找到对应的插件模型指令，调用指定的模型进行结果响应
     * @Version:1.0
     **/
    Result handleCapability(HttpServletRequest request, String modelPluginApiId, JSONObject jsonObjectParam);

    Result findByPluginId(String pluginId);

    void copy(ModelPluginApiManage param);

    Result checkApiToken(String apiToken, ModelPluginApiAuthUser modelPluginApiAuthUser);
}