// package com.wg.appframe.core.handler;
//
// import cn.hutool.core.util.StrUtil;
// import com.wg.appframe.core.utils.MDCTraceUtils;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
// /**
//  * 接口参数验签拦截器
//  */
// @Service
// @Slf4j
// public class TraceIdPreHandle implements PreHandle {
//
//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//         String traceId = request.getHeader(MDCTraceUtils.TRACE_ID_HEADER);
//         String spanId = request.getHeader(MDCTraceUtils.SPAN_ID_HEADER);
//         if (StrUtil.isBlank(traceId)) {
//             MDCTraceUtils.addTrace();
//         } else {
//             MDCTraceUtils.putTrace(traceId, spanId);
//         }
//         return true;
//     }
//
//     @Override
//     public int getSort() {
//         return -1;
//     }
// }
