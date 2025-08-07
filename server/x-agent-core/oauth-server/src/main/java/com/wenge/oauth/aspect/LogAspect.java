package com.wenge.oauth.aspect;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.entity.OperaLog;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.OperaLogService;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.redis.utils.RequestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 自定义操作日志aop
 *
 * @author wlp参值ruoyi
 * @since 2024年1月15日 11:51:45
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private OperaLogService operaLogService;

    @AfterReturning(pointcut = "@annotation(operaLogs)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperaLogs operaLogs, Object jsonResult) {
        try {
            saveLog(joinPoint, operaLogs);
        } catch (Exception e) {
            log.error("LogDiyAspect记录日志异常:", e);
        }
    }

    @AfterThrowing(value = "@annotation(operaLogs)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperaLogs operaLogs, Exception e) {
        saveLog(joinPoint, operaLogs);
    }

    /**
     * 保存日志 入参final是防止改入参被误改动
     *
     * @param joinPoint
     * @param logDiy
     * @param e
     * @param jsonResult
     */
    protected void saveLog(final JoinPoint joinPoint, OperaLogs logDiy) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();

        //组装1
        String ip = RequestUtils.getIpAddress();
        String operUrl = request.getRequestURI();
        String requestMethod = request.getMethod();
        //组装2
        String businessType = logDiy.businessType();
        String title = logDiy.title();
        if (StringUtils.isBlank(title)) {
            try {//如果注解没填title直接读api注解
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                title = method.getAnnotation(ApiOperation.class).value();
            } catch (Exception ignored) {

            }
        }

        if (StringUtils.isBlank(businessType)) {
            try {//如果注解没填title直接读api注解
                businessType = joinPoint.getTarget().getClass().getAnnotation(Api.class).tags()[0];
            } catch (Exception ignored) {
            }
        }

        String operatorType = logDiy.operatorType();
        String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        OperaLog userLogDiy = new OperaLog();
        userLogDiy.setTitle(title);
        userLogDiy.setBusinessType(businessType);
        userLogDiy.setRequestMethod(requestMethod);
        userLogDiy.setOperatorType(operatorType);
        userLogDiy.setOperName(tokenOauthUserInfo == null ? "" : tokenOauthUserInfo.getAccountName());
        userLogDiy.setOperUrl(operUrl);
        userLogDiy.setOperIp(ip);
        userLogDiy.setOperTime(format);
        operaLogService.save(userLogDiy);
    }


    /**
     * 根据JoinPoint获取post形式的入参
     */
    public String getRequestStrByJoinPoint(final JoinPoint joinPoint) {
        StringBuilder requestStr = new StringBuilder();
        Object[] paramsArray = joinPoint.getArgs();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                try {
                    if (o != null && !isFiterObject(o)) {
                        try {
                            requestStr.append(JSONObject.toJSONString(o)).append(" ");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return requestStr.toString().trim();
    }

    /**
     * 做入参拼接时是否需要跳过的对象,主要是文件类型的要跳过
     *
     * @param o 对象
     * @return 如果需要跳过返回true
     */
    public boolean isFiterObject(final Object o) {
        Class<?> aClass = o.getClass();
        if (aClass.isArray()) {
            return aClass.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(aClass)) {
            Collection collection = (Collection) o;
            for (Object o1 : collection) {
                if (o1 instanceof MultipartFile)
                    return true;
            }
            return false;
        } else if (JSONObject.class.isAssignableFrom(aClass)) {
            JSONObject jsonObject = (JSONObject) o;
            for (String s : jsonObject.keySet()) {
                if (jsonObject.get(s) instanceof MultipartFile) {
                    return true;
                }
            }
            return false;
        } else if (Map.class.isAssignableFrom(aClass)) {
            Map map = (Map) o;
            for (Object o1 : map.keySet()) {
                if (((Map.Entry) o1).getValue() instanceof MultipartFile) {
                    return true;
                }
            }
            return false;
        }
        return o instanceof MultipartFile
                || o instanceof HttpServletRequest
                || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }


    /**
     * 截取字符串,避免异常写法
     */
    public static String trySubString(String str, int end) {
        if (str == null) {
            return "";
        }
        return str.substring(0, Math.min(end, str.length()));
    }

}
