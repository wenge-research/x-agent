package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationInfoPageParam;
import com.wenge.model.entity.ApplicationPublishRecord;
import com.wg.appframe.core.bean.Result;

public interface ApplicationPublishRecordService extends IService<ApplicationPublishRecord> {

    Result<Page<ApplicationPublishRecord>> getAppPublishRecordList(ApplicationInfoPageParam applicationInfo);
}