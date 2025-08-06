package com.wenge.model.service;

import com.wenge.model.dto.param.ReviewResultsParam;
import com.wenge.model.entity.ReviewResults;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface ReviewResultsService {
    Result batchSave(ReviewResultsParam reviewResults);

    Result getList(ReviewResults reviewResults);

    Result deleteByIds(List<String> idList);

    Result update(ReviewResultsParam reviewResults);
}
