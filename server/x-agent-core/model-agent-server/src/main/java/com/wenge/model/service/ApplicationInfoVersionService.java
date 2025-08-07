package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationInfoPageParam;
import com.wenge.model.dto.param.ApplicationInfoVersionParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.ApplicationInfoVersion;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface ApplicationInfoVersionService extends IService<ApplicationInfoVersion> {
    Result<Page<ApplicationInfoVersion>> getApplicationInfoList(ApplicationInfoVersionParam applicationversionInfo);
    ApplicationInfoVersion getApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
    Result updateApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
    Result deleteApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
    Result backUpdateApplicationVersionInfo(ApplicationInfoVersionParam applicationversionInfo);
}
