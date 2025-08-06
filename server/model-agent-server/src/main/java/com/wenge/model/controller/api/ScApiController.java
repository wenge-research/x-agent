package com.wenge.model.controller.api;

import cn.hutool.json.JSONObject;
import com.wenge.model.dto.param.wenhai.ScApiParam;
import com.wenge.model.service.ScApiService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 长沙 api
 */
@RestController
@RequestMapping("/smartApi/cs")
@Slf4j
@Api(tags = "对外提供智能服务")
public class ScApiController {

    @Autowired
    private ScApiService scApiService;

    /**
     * 长沙服务通用接口
     *
     * @param param
     * @return
     */
    @PostMapping("/commonApi")
    public Result<JSONObject> commonApi(@RequestBody ScApiParam param, HttpServletRequest request) {
        return scApiService.commonApi(param, request);
    }
}
