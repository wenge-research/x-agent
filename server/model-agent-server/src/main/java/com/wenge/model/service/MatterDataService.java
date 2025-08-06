package com.wenge.model.service;

import com.wenge.model.dto.param.MatterDataParam;
import com.wenge.model.entity.MatterData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

public interface MatterDataService {

    Result getMatterDataList(MatterDataParam param);

    Result<MatterData> getMatterDataDetail(StringParam param);
}
