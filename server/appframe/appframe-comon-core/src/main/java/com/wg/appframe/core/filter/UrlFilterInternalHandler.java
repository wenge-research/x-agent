package com.wg.appframe.core.filter;

import com.wg.appframe.core.utils.UrlMatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截符合规则的 url
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "appframe.vulnerability", name = "enable", havingValue = "true")
public class UrlFilterInternalHandler implements CoreFilterHandle {

    /**
     * 是否开启指定 url 拦截
     */
    @Value("${appframe.vulnerability.enable:true}")
    private boolean enable;

    /**
     * 拦截指定 url 拦截
     */
    @Value("${appframe.vulnerability.interceptorUrl:}")
    private String interceptorUrl;

    @Override
    public boolean doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!enable) {
            return true;
        }
        String url = request.getRequestURI();

        // 匹配拦截url
        boolean matchFlag = UrlMatchUtils.isPassFlag(interceptorUrl, url);
        // 没匹配上就放行
        if (!matchFlag) {
            return true;
        }
        // 拦截漏洞
        response.setStatus(403);
        return false;
    }

}
