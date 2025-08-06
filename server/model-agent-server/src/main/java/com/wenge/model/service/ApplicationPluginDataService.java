package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.dto.param.ApplicationConfigEditParam;
import com.wenge.model.entity.ApplicationPluginData;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 15:31:30
 *
 */
public interface ApplicationPluginDataService extends IService<ApplicationPluginData> {

    Result addApplicationPluginData(ApplicationConfigEditParam applicationPluginData);

    Result<List<ApplicationPluginData>> getApplicationPluginDataList(AppConfigListParam applicationPluginData);

    Result updateApplicationPluginData(ApplicationPluginData applicationPluginData);

    Result deleteApplicationPluginData(List<String> idList);

}