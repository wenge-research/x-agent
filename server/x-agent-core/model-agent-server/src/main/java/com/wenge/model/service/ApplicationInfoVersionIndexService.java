package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationInfoVersionParam;
import com.wenge.model.entity.ApplicationInfoVersionIndex;
import com.wg.appframe.core.bean.Result;
import org.dromara.easyes.core.biz.EsPageInfo;

public interface ApplicationInfoVersionIndexService{
    Result<EsPageInfo<ApplicationInfoVersionIndex>> getApplicationInfoList(ApplicationInfoVersionParam applicationversionInfo);
    ApplicationInfoVersionIndex getApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
    Result updateApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
    Result deleteApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
    Result backUpdateApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
}
