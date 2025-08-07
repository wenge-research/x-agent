package com.wenge.model.service;

import com.wenge.model.dto.param.GuanxinPhoneCheckParam;
import com.wenge.model.dto.result.GuanxinPhoneCheckResult;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;

public interface GuanxinZhiXunService {

    Result<GuanxinPhoneCheckResult> checkPhone(GuanxinPhoneCheckParam param);

    Result<GuanxinPhoneCheckResult> todayNoLogin(EmptyParam param);
}
