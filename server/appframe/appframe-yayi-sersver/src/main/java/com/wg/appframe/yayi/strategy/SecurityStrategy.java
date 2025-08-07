package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.SecurityConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.SecurityParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.SecurityResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SecurityStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String contnet, YayiParam yayiParam) {
        SecurityResult result = new SecurityResult();
        if (yayiParam instanceof SecurityParam) {
            SecurityParam param = (SecurityParam) yayiParam;
            result = running(contnet, param);
        }

        return result;
    }

    public SecurityResult running(String fileUrl, SecurityParam yayiParam) {
        SecurityResult result = new SecurityResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, securityConfig.getUri(), yayiConfig, yayiParam);

            // 封装重排模型接口入参
            SecurityParam securityParam = param(fileUrl, yayiParam);

            // 请求重排模型接口
            log.info("===>document 违禁词 url:{}  ,  param: {}", securityConfig.getUri(), JSONUtil.toJsonStr(securityParam));
            String body = YayiApiUtils.invokeYaYiApi(builder, securityParam);
            log.info("===>Security 违禁词 body: {}", body);
            if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
                JSONObject entries = JSONUtil.parseObj(body);
                return entries.toBean(SecurityResult.class);
            }

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 初始化重排模型接口参数
     *
     * @param param
     * @return
     */
    private SecurityParam param(String content, SecurityParam param) {
        SecurityParam securityParam = new SecurityParam();
        securityParam.setId(IdUtil.simpleUUID());
        securityParam.setContent(content);
        if (StringUtils.isNotBlank(securityConfig.getMode())) {
            securityParam.setMode(securityConfig.getMode());
        }

        securityParam.setRetryTimes(1);
        if (null != securityConfig.getRetryTimes()) {
            securityParam.setRetryTimes(securityConfig.getRetryTimes());
        }
        securityParam.setRetryInterval(3000);
        if (null != securityConfig.getRetryInterval()) {
            securityParam.setRetryInterval(securityConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(securityParam.getUri())) {
            securityParam.setUri(securityConfig.getUri());
        }

        if (StringUtils.isBlank(securityParam.getAppKey())) {
            securityParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(securityParam.getAppSecret())) {
            securityParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, securityParam, copyOptions);
        BeanUtil.copyProperties(securityParam, param, copyOptions);
        return securityParam;
    }
}
