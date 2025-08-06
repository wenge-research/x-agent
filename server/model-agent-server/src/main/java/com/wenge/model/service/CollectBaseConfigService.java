package com.wenge.model.service;


import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.CollectBaseConfig;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据集的表配置服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 20:14:11
 *
 */
public interface CollectBaseConfigService extends IService<CollectBaseConfig> {

    Result addCollectBaseConfig(CollectConfigAddParam collectBaseConfig);


    Result deleteCollectBaseConfig(List<String> idList);

    Result editCollectData(CollectDataEditParam editParam);

    Result editCollectConfig(CollectConfigEditParam param);

    Result getCollectBaseConfig(StringParam param);

    Result getCollectDataList(CollectDataListParam param);

    Result getWenShuConfig(WenShuConfigGetParam param);

    /**
     * 获取表数量
     * @param collectIds
     * @return
     */
    Map<String, Long> getTableNum(List<String> collectIds);

    /**
     * 根据数据集id获取配置id
     * @param collectIds
     * @return
     */
    List<String> getConfigIdListByCollectId(List<String> collectIds);
}