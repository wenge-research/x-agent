package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.HotSearchWordParam;
import com.wenge.model.entity.HotSearchWord;

import java.util.List;

public interface HotSearchWordService extends IService<HotSearchWord> {

    public List<HotSearchWord> queryHotSearchWordTopN(HotSearchWordParam hotSearchWordParam);

    public List<String> addHotSearchWord(String context, String applicationId);
}
