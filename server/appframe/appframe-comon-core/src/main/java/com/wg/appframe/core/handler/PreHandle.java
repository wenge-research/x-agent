package com.wg.appframe.core.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
public interface PreHandle {

    /**
     * 拦截器实现
     * @param request
     * @param response
     * @param handler
     * @return
     */
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);

    /**
     * 拦截器排序
     * @return
     */
    int getSort();
}
