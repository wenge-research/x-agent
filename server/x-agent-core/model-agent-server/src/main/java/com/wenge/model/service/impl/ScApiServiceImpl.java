package com.wenge.model.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.param.wenhai.ScApiParam;
import com.wenge.model.service.ScApiService;
import com.wenge.model.utils.CsSignUtil;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class ScApiServiceImpl implements ScApiService {

    @Override
    public Result<JSONObject> commonApi(ScApiParam param, HttpServletRequest request) {
        if (StringUtils.isBlank(param.getUri()) || StringUtils.isBlank(param.getAppKey()) || StringUtils.isBlank(param.getAppSecret())) {
            return Result.fail("参数不合法");
        }

        try {
            JSONObject headers = CsSignUtil.buildHeader(param.getAppKey(), param.getAppSecret(), param.getParam());
            HttpRequest post = HttpUtil.createPost(param.getUri());
            headers.forEach((k, v) -> {
                post.header(k, v.toString());
            });
            post.timeout(1000 * 60 * 3);
            post.body(JSONUtil.toJsonStr(param.getParam()));
            log.info("请求地址,{},请求参数:{}", param.getUri(), JSONUtil.toJsonStr(param.getParam()));
            String body = post.execute().body();
            log.info("请求结果:{}", body);
            JSONObject entries = JSONUtil.parseObj(body);
            return Result.success(entries);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(null);
    }

}
