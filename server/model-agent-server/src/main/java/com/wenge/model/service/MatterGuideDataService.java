package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.AddMatterGuideDataParam;
import com.wenge.model.entity.MatterGuideData;
import com.wenge.model.entity.MatterGuideFiled;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;

public interface MatterGuideDataService extends IService<MatterGuideData> {


    Result submitFormInfo(AddMatterGuideDataParam param);
}