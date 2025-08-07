package com.wg.appframe.core.filter;


import cn.hutool.core.util.StrUtil;
import com.wg.appframe.core.handler.PreHandle;
import com.wg.appframe.core.utils.MDCTraceUtils;
import com.wg.appframe.core.utils.UrlMatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用过滤器
 */
@Component
@Slf4j
public class CoreFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private Map<String, CoreFilterHandle> filterHandleMap;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = request.getHeader(MDCTraceUtils.TRACE_ID_HEADER);
            String spanId = request.getHeader(MDCTraceUtils.SPAN_ID_HEADER);
            if (StrUtil.isBlank(traceId)) {
                MDCTraceUtils.addTrace();
            } else {
                MDCTraceUtils.putTrace(traceId, spanId);
            }
            boolean passFlag = true;
            if (null != filterHandleMap && !filterHandleMap.isEmpty()) {
                // 按sort排序
                List<CoreFilterHandle> filterHandleList = filterHandleMap.values().stream().collect(Collectors.toList());
                for (CoreFilterHandle filterHandle : filterHandleList) {
                    boolean matchFlag = filterHandle.doFilterInternal(request, response, filterChain);
                    passFlag = passFlag && matchFlag;
                }
            }
            // 放行
            if (passFlag) {
                filterChain.doFilter(request, response);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
