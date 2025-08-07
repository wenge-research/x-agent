package com.wg.appframe.core.utils;

import com.alibaba.fastjson2.JSON;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author zlt
 * @date 2018/12/20
 */
public class ResponseUtil {
    
    private ResponseUtil() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * 响应信息
     *
     * @param response 返回信息
     */
    public static void resultMsg(HttpServletResponse response, ResultCodeEnum resultCode) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out;
        try {
            Result result = Result.result(resultCode);
            out = response.getWriter();
            out.append(JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
