package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.AddMatterGuideDataParam;
import com.wenge.model.entity.LabelType;
import com.wenge.model.entity.MatterGuide;
import com.wenge.model.entity.MatterGuideData;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface LabelTypeService extends IService<LabelType> {


    List<LabelType> getLabelTypes(Integer type);
}