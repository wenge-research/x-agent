package com.wenge.model.service;

import com.wenge.model.dto.result.GkColumn;
import com.wenge.model.entity.GkStoryData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

public interface GkStoryService {

    Result<List<GkColumn>> getColumnList(EmptyParam param);

    Result<List<GkStoryData>> getStoryByColumnId(StringParam param);

    Result<GkStoryData> getStoryById(StringParam param);
}
