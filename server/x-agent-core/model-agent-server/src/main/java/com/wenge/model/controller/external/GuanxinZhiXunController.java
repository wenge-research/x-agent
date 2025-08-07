package com.wenge.model.controller.external;

import com.wenge.model.dto.param.GuanxinPhoneCheckParam;
import com.wenge.model.dto.result.GuanxinPhoneCheckResult;
import com.wenge.model.service.GuanxinZhiXunService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求关芯智巡的接口
 */
@Api(tags = "请求关芯智巡的接口")
@RestController
@RequestMapping("/guanxin")
public class GuanxinZhiXunController {

    @Autowired
    private GuanxinZhiXunService guanxinZhiXunService;

    /**
     * 验证【关芯智巡】系统的手机号是否可用
     */
	@ApiOperation(value = "验证【关芯智巡】系统的手机号是否可用", tags = "验证【关芯智巡】系统的手机号是否可用", notes = "验证【关芯智巡】系统的手机号是否可用", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/checkPhone")
    public Result<GuanxinPhoneCheckResult> checkPhone(@RequestBody GuanxinPhoneCheckParam param) {
        return guanxinZhiXunService.checkPhone(param);
    }

    /**
     * 查询【关芯智巡】未登录用户
     */
	@ApiOperation(value = "未登录用户查询【关芯智巡】未登录用户", tags = "查询【关芯智巡】未登录用户", notes = "查询【关芯智巡】未登录用户", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/todayNoLogin")
    public Result<GuanxinPhoneCheckResult> todayNoLogin(@RequestBody EmptyParam param) {
        return guanxinZhiXunService.todayNoLogin(param);
    }
}
