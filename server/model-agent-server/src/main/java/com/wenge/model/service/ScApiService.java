package com.wenge.model.service;

import cn.hutool.json.JSONObject;
import com.wenge.model.dto.param.wenhai.ScApiParam;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletRequest;

public interface ScApiService {

    /**
     * 通用接口
     * @param param
     * @param request
     * @return
     */
    Result<JSONObject> commonApi(ScApiParam param, HttpServletRequest request);


}
