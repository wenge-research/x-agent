package com.wg.appframe.core.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.annotation.GlobalLog;
import com.wg.appframe.core.utils.WitherUrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 接口参数验签拦截器
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "appframe.log", name = "enable", havingValue = "true")
public class LogHandle implements AopHandle {

    /**
     * 是否开启入参日志
     */
    @Value("${appframe.log.paramEnable:true}")
    private boolean enable;

    @Autowired
    private HttpServletRequest autowiredRequest;

    /**
     * 是否开启出参日志
     */
    @Value("${appframe.log.resultEnable:false}")
    private boolean resultEnable;

    /**
     * 打印用户信息
     */
    @Value("${appframe.log.userKey:userId}")
    private String userKey;

    /**
     * 忽略的接口
     */
    @Value("${appframe.log.ignore:}")
    private String ignores;

    private static final String IGNORES_URL = "/cicd/**,/wos/**";

    @Override
    public void before(ProceedingJoinPoint joinPoint, JSONObject param) {
        // 打印入参日志
        if (enable) {
            // 获取当前method对象
            String whiteStr = IGNORES_URL;
            if (StringUtils.isNotBlank(ignores)) {
                whiteStr = whiteStr + "," + ignores;
            }
            boolean passFlag = WitherUrlUtils.isPassFlag(whiteStr);
            if (passFlag) {
                return;
            }
            MethodSignature sign = (MethodSignature) joinPoint.getSignature();
            Method method = sign.getMethod();
            Object[] args = joinPoint.getArgs();
            String userId = getUserId();

            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (null != scheduled) {
                return;
            }
            GlobalLog annotation = method.getAnnotation(GlobalLog.class);
            // 如果有全局日志注解，则获取开关
            if (null == annotation || annotation.paramEnable()) {
                String[] parameterNames = sign.getParameterNames();
                JSONObject logJson = new JSONObject();
                int length = args.length;

                for (int i = 0; i < length; i++) {
                    // 不打印文件,ServletResponse,ServletRequest日志
                    if (args[i] instanceof InputStreamSource
                            || args[i] instanceof ServletResponse
                            || args[i] instanceof MultipartFile
                            || args[i] instanceof ServletRequest) {
                        logJson.put(parameterNames[i], parameterNames[i]);
                        continue;
                    }
                    logJson.put(parameterNames[i], args[i]);
                }
                String logJsonStr = "param";
                try {
                    logJsonStr = JSON.toJSONString(logJson);
                } catch (Exception e) {
                    e.getMessage();
                }
                log.info("〓〓〓{}〓〓〓   " +
                                "[uri] : {}   " +
                                "〓〓 [param] : {}",
                        userId, autowiredRequest.getRequestURI(), logJsonStr);
            }
        }
    }

    @Override
    public void after(ProceedingJoinPoint joinPoint, Object ret, JSONObject param) {
        // 打印出参日志
        if (resultEnable) {
            // 获取当前method对象
            String whiteStr = getWhiteStr();
            boolean passFlag = WitherUrlUtils.isPassFlag(whiteStr);
            if (passFlag) {
                return;
            }
            String userId = getUserId();

            MethodSignature sign = (MethodSignature) joinPoint.getSignature();
            Method method = sign.getMethod();
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (null != scheduled) {
                return;
            }
            GlobalLog annotation = method.getAnnotation(GlobalLog.class);
            // 如果有全局日志注解，则获取开关
            if (null == annotation || annotation.resultEnable()) {
                String retStr = "result";
                try {
                    retStr = JSON.toJSONString(ret);
                } catch (Exception e) {
                    e.getMessage();
                }
                log.info("■ ■ {} ■ ■   " +
                                "[uri] : {}   " +
                                "■ ■ [result] : {}",
                        userId, autowiredRequest.getRequestURI(), retStr);
            }
        }
    }

    @Override
    public int getSort() {
        return 2;
    }

    /**
     * 获取用户id
     * @return
     */
    private String getUserId() {
        String userId = MDC.get(userKey);
        if (StringUtils.isBlank(userId) || "null".equals(userId)) {
            userId = "";
        } else {
            userId = "【" + userId + "】";
        }
        return userId;
    }

    /**
     * 获取白名单
     * @return
     */
    private String getWhiteStr() {
        String whiteStr = IGNORES_URL;
        if (StringUtils.isNotBlank(ignores)) {
            whiteStr = whiteStr + "," + ignores;
        }
        return whiteStr;
    }
}
