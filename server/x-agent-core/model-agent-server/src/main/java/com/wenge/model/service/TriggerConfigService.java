package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.TriggerConfigParam;
import com.wenge.model.workflow.entity.TriggerConfig;
import com.wg.appframe.core.bean.Result;

public interface TriggerConfigService extends IService<TriggerConfig> {
    Result addOrUpdate(TriggerConfig triggerConfig);

    Result<Page<TriggerConfig>> queryAll(TriggerConfigParam param);

    /**
     * 查询应用/工作流关联的触发器
     * @param param
     * @return
     */
    Result queryDetails(TriggerConfigParam param);
}
