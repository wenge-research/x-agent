//package com.wenge.oauth.interceptor;
//
//import com.wenge.oauth.constants.MybatisFiledConstant;
//import com.wenge.oauth.entity.TokenUser;
//import com.wenge.oauth.holder.ContextHolders;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 处理MDC
// */
//@Slf4j
//@Data
//@Component
//public class LogHandlerInterceptor implements HandlerInterceptor {
//
//    /**
//     * 打印用户信息
//     */
//    @Value("${appframe.log.userKey:userId}")
//    private String userKey;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        try {
//            TokenUser tokenOauthUserInfo = ContextHolders.getTokenUserInfo();
//            if (null != tokenOauthUserInfo) {
//                // 打印日志的时候，把用户名打印出来，这里指定user对象的字段，是指打印用户的指定字段
//                MDC.put(userKey, tokenOauthUserInfo.getUserName());
//                // 租户id，只有设置MDC，配置tenantId才会自动配置租户id
//                MDC.put(MybatisFiledConstant.MDC_TENANT_ID, tokenOauthUserInfo.getTenantId());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        MDC.clear();
//        ContextHolders.removeTokenUserinfo();
//        ContextHolders.removeAccessToken();
//    }
//
//}
