package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.entity.ApplicationPlugin;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 应用配置服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-28 10:38:16
 *
 */
public interface ApplicationPluginService extends IService<ApplicationPlugin> {

    Result addApplicationPlugin(ApplicationPlugin param);

    Result<List<ApplicationPlugin>> getApplicationPluginList(AppConfigListParam param);

    Result updateApplicationPlugin(ApplicationPlugin applicationPlugin);

    Result deleteApplicationPlugin(List<String> idList);

}