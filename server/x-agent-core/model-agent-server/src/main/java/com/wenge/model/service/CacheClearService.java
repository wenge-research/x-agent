package com.wenge.model.service;

import com.wenge.oauth.dto.param.CacheClearParam;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

public interface CacheClearService {

    Result cleanWord();

    Result clearFlowCache(StringParam param);

    Result clearDenseVector();

    Result clearAppConfig(CacheClearParam param);

}
