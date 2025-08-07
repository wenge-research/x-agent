package com.wg.appframe.core.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.bean.WgPageInfo;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.dto.params.SingleParam;
import com.wg.appframe.core.interceptor.RequestWrapper;
import com.wg.appframe.core.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

/**
 * 只允许POST请求拦截器
 */
@Service
@Slf4j
public class ParamPreHandle implements PreHandle {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            if (null != handler && handler instanceof HandlerMethod) {
                MethodParameter[] methodParameters = ((HandlerMethod) handler).getMethodParameters();
                for (MethodParameter methodParameter : methodParameters) {
                    if (null != methodParameter.getParameterAnnotation(RequestBody.class)) {
                        // 单参数时，转换参数key
                        if (null == methodParameter.getParameterType().getSuperclass()) {
                            continue;
                        }
                        // 单参数时，修改入参key为param
                        if (methodParameter.getParameterType().getSuperclass().getName().equals(SingleParam.class.getName())) {
                            JSONObject requestParam = JSON.parseObject(request.getReader().lines().collect(Collectors.joining()));
                            if (null == requestParam) {
                                log.error("========请求体没有参数====================");
                                response.setStatus(407);
                                ResponseUtil.resultMsg(response, ResultCodeEnum.BODY_IS_BLANK);
                                return false;
                            }
                            if (requestParam.size() > 1) {
                                log.error("========单参数只能接收一个字段====================");
                                response.setStatus(407);
                                ResponseUtil.resultMsg(response, ResultCodeEnum.ONLY_ONE_PARAM);
                                return false;
                            } else if (requestParam.size() > 0) {
                                // 单参数时，修改入参key为param
                                JSONObject param = new JSONObject();
                                requestParam.forEach((pram, value) -> {
                                    param.put("param", value);
                                });

                                if (request instanceof RequestWrapper) {
                                    RequestWrapper requestWrapper = (RequestWrapper) request;
                                    requestWrapper.setRequestBody(JSON.toJSONString(param).getBytes());
                                }
                            }
                        } else if (methodParameter.getParameterType().getSuperclass().getName().equals(WgPageInfo.class.getName())) {
                            try {
                                ApplicationContext context = CoreContextProvider.getContext();
                                String pageEnable = context.getEnvironment().getProperty("appframe.page.enable");
                                if ("true".equals(pageEnable)) {
                                    String maxPageSize = context.getEnvironment().getProperty("appframe.page.maxPageSize");
                                    if (StringUtils.isNotBlank(maxPageSize) && StringUtils.isNumeric(maxPageSize)) {
                                        long pageSize = Long.parseLong(maxPageSize);
                                        // 分页参数时，验证 pageSize
                                        JSONObject requestParam = JSON.parseObject(request.getReader().lines().collect(Collectors.joining()));
                                        WgPageInfo wgPageInfo = requestParam.toJavaObject(WgPageInfo.class);
                                        // pageSize最大值验证
                                        if (null != wgPageInfo.getPageSize() && wgPageInfo.getPageSize() > pageSize) {
                                            String maxPageSizeIgnoreApi = context.getEnvironment().getProperty("appframe.page.maxPageSizeIgnoreApi");
                                            if (StringUtils.isBlank(maxPageSizeIgnoreApi)) {
                                                log.error("========pageSize max is {} ,config appframe.page.maxPageSizeIgnoreApi to ignore ====================", pageSize);
                                                response.setStatus(407);
                                                ResponseUtil.resultMsg(response, ResultCodeEnum.MAX_PAGE_SIZE);
                                                return false;
                                            } else {
                                                RequestMapping methodAnnotation = methodParameter.getMethodAnnotation(RequestMapping.class);
                                                if (null != methodAnnotation) {
                                                    String[] value = methodAnnotation.value();
                                                    if (value.length > 0) {
                                                        String[] split = maxPageSizeIgnoreApi.split(",");
                                                        for (String igoreApi : split) {
                                                            for (String api : value) {
                                                                if (igoreApi.equals(api)) {
                                                                    log.error("========pageSize max is {} ====================", pageSize);
                                                                    response.setStatus(407);
                                                                    ResponseUtil.resultMsg(response, ResultCodeEnum.MAX_PAGE_SIZE);
                                                                    return false;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("AuthInterceptor.postPreHandle.error:{}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public int getSort() {
        return 3;
    }
}
