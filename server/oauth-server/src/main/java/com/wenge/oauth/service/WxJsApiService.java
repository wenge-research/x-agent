package com.wenge.oauth.service;

import com.wenge.oauth.dto.param.WxSignatureParam;
import com.wg.appframe.core.bean.Result;

public interface WxJsApiService {

    Result signatureByAccessToken(WxSignatureParam param);

}