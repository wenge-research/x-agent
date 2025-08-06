package com.wenge.oauth.controller;

import com.wenge.oauth.service.AuthService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/auth")
@Slf4j
@Api(tags = "认证相关服务")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/sendSmsAuthentication")
    @ApiOperation(value = "发送短信验证码", tags = "发送短信验证码", notes = "发送短信验证码", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result smsAuthentication(@RequestBody StringParam phoneNumber, HttpServletRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return authService.smsAuthentication(phoneNumber, request);
    }

}
