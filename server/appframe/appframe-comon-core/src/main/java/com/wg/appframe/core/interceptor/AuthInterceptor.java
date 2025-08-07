package com.wg.appframe.core.interceptor;


import com.wg.appframe.core.handler.PreHandle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Description: 拦截请求
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-04-27 14:04:33
 */
@Slf4j
@Data
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 注入拦截器
     */
    @Autowired
    private Map<String, PreHandle> handleMap;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 按顺序依次执行拦截器
        if (null != handleMap && !handleMap.isEmpty()) {
            // 按sort排序
            List<PreHandle> preHandleList = handleMap.values().stream().sorted(Comparator.comparingInt(PreHandle::getSort)).collect(Collectors.toList());
            for (PreHandle preHandle : preHandleList) {
                boolean isPass = preHandle.preHandle(request, response, handler);
                if (!isPass) {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        //ContextHolders.removeAccessToken();
        //ContextHolders.removeTokenUserinfo();
        MDC.clear();
    }
}

