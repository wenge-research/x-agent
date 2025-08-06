package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.DataCollectPageParam;
import com.wenge.model.entity.DataCollectInfo;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 数据集信息表服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 18:05:32
 *
 */
public interface DataCollectInfoService extends IService<DataCollectInfo> {

    Result<DataCollectInfo> addDataCollectInfo(DataCollectInfo dataCollectInfo);

    Result<Page<DataCollectInfo>> getDataCollectInfoList(DataCollectPageParam param);

    Result deleteDataCollectInfo(List<String> idList);

}