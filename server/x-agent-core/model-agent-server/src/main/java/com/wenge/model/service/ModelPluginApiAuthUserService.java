package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ModelPluginApiAuthUserPageParam;
import com.wenge.model.entity.ModelPluginApiAuthUser;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface ModelPluginApiAuthUserService extends IService<ModelPluginApiAuthUser> {

    Result addModelPluginApiAuthUser(ModelPluginApiAuthUser param);

    Result getModelPluginApiAuthUserList(ModelPluginApiAuthUserPageParam param);

    Result updateModelPluginApiAuthUser(ModelPluginApiAuthUser modelPluginApiAuthUser);

    Result deleteModelPluginApiAuthUser(List<String> idList);

    Result getModelPluginApiAuthUserById(String id);

    Result updateStatus(ModelPluginApiAuthUser modelPluginApiAuthUser);
}