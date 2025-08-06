package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ServerPublishAuditPageParam;
import com.wenge.model.entity.ServerPublishAudit;
import com.wenge.model.entity.ServerPublishAudit;
import com.wenge.model.utils.PageInfo;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface ServerPublishAuditService extends IService<ServerPublishAudit> {
    
    Result add(ServerPublishAudit serverPublishAudit);

    Result delete(ServerPublishAudit serverPublishAudit);

    Result update(ServerPublishAudit serverPublishAudit);

    Result<Page<ServerPublishAudit>> getListPage(ServerPublishAuditPageParam param);

    Result getDataById(Long id, String upAnddown); // UP-上翻 DOWN-下翻

    Result getStoreDataByApplicationId(String applicationId);

}