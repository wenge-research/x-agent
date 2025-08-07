package com.wenge.oauth.holder;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.constant.ResultCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", exception.getMessage());
        jsonObject.put("code", ResultCode.USER_PSW_ERROR.getCode());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(jsonObject.toJSONString());
    }
}
