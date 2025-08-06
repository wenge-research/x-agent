package com.wenge.model.service;

import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wg.appframe.core.bean.Result;

/**
 * @author: caohaifeng
 * @date: 2024/12/30 15:50
 * @Description: 运营管理接口层
 * @Version:1.0
 **/

public interface OperationManagementService {

    Result applicationCount();


    Result knowledgeCount();


    Result getUsingInfo();


    Result getQaCount();

    Result questionChartsTop50(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);

    Result getTokenConsumption(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);








}
