package com.wenge.model.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.GuanxinPhoneCheckParam;
import com.wenge.model.dto.result.GuanxinPhoneCheckResult;
import com.wenge.model.service.GuanxinZhiXunService;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuanxinZhiXunServiceImpl implements GuanxinZhiXunService {

//    @Value("${appConfig.guanxin.zhixun.checkPhoneUri}")
//    private String checkPhoneUri;

    @Override
    public Result<GuanxinPhoneCheckResult> checkPhone(GuanxinPhoneCheckParam param) {
        GuanxinPhoneCheckResult result = new GuanxinPhoneCheckResult();
        if (StringUtils.isBlank(param.getPhone())) {
            return Result.success(result);
        }

        try {
            String response = HttpUtil.get(AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_GUANXIN_ZHIXUN_CHECK_PHONE_URI) + "?phone=" + param.getPhone());
            if (StringUtils.isBlank(response)) {
                return Result.success(result);
            }

            JSONObject responseResult = JSON.parseObject(response);
            if ("200".equals(responseResult.getString("code"))) {
                Object data = responseResult.get("data");
                result.setResult(data);
            }

        } catch (Exception e) {
            log.error("请求【关芯智巡】系统的手机号是否可用失败", e);
        }
        return Result.success(result);
    }

    @Override
    public Result<GuanxinPhoneCheckResult> todayNoLogin(EmptyParam param) {
        GuanxinPhoneCheckResult result = new GuanxinPhoneCheckResult();

        try {
            String response = HttpUtil.get(AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_GUANXIN_ZHIXUN_TODAY_NOLOGIN_URI));
            if (StringUtils.isBlank(response)) {
                return Result.success(result);
            }

            JSONObject responseResult = JSON.parseObject(response);
            if ("200".equals(responseResult.getString("code"))) {
                Object data = responseResult.get("data");
                result.setResult(data);
            }

        } catch (Exception e) {
            log.error("请求【关芯智巡】系统的手机号是否可用失败", e);
        }
        return Result.success(result);
    }
}
