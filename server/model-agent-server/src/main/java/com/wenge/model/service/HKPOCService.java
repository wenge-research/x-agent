package com.wenge.model.service;

import com.wenge.model.dto.param.HKParam;
import com.wg.appframe.core.bean.Result;

public interface HKPOCService {

    Result queryRequestId(HKParam hkParam);

    Result questFilesByRequestId(HKParam hkParam);

    Result getFileListByFileIds(HKParam hkParam);
}
