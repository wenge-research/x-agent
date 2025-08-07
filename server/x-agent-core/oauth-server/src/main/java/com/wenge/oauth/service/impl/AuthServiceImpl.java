package com.wenge.oauth.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.dto.param.SmsTencentParam;
import com.wenge.oauth.dto.result.SmsTencentResult;
import com.wenge.oauth.service.AuthService;
import com.wenge.oauth.util.SmsTencentUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${sms.tencent.smsSdkAppId}")
    private String smsSdkAppId;

    @Value("${sms.tencent.loginTemplateId}")
    private String loginTemplateId;

    @Value("${sms.tencent.SignName}")
    private String SignName;
    @Value("${sms.tencent.version}")
    private String version;
    @Value("${sms.tencent.region}")
    private String region;

    @Autowired
    private RedisService redisService;

    @Override
    public Result smsAuthentication(StringParam phoneNumber, HttpServletRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (null == phoneNumber || StringUtils.isBlank(phoneNumber.getParam())) {
            return Result.fail("手机号不能为空");
        }
        // 同一个 ip 在5分钟内只能发送一条验证码
        String clientIP = ServletUtil.getClientIP(request);
        String ipKey = RedisConstant.LOGIN_SMS_IP + clientIP;
        Object o = redisService.get(ipKey);
        if (null != o) {
            log.error("ip【{}】验证码发送过于频繁", clientIP);
            // return Result.fail("请勿频繁发送验证码");
        }

        SmsTencentParam param = new SmsTencentParam();
        param.setPhoneNumberSet(ListUtil.toList(phoneNumber.getParam()));
        param.setSmsSdkAppId(smsSdkAppId);
        param.setTemplateId(loginTemplateId);
        param.setSignName(SignName);
        param.setVersion(version);
        param.setRegion(region);
        // 随机6位数字，使用hutool工具
        String code = RandomUtil.randomNumbers(6);
        param.setTemplateParamSet(ListUtil.toList(code));
        SmsTencentResult smsTencentResult = SmsTencentUtil.sendMsg(param);
        if (null == smsTencentResult) {
            return Result.fail("发送验证码失败");
        }
        SmsTencentResult.Response response = smsTencentResult.getResponse();
        if (null == response) {
            return Result.fail("发送验证码失败");
        }

        // 验证发送短信的状态
        List<SmsTencentResult.SendStatus> sendStatusSet = response.getSendStatusSet();
        if (CollectionUtil.isNotEmpty(sendStatusSet)) {
            if (sendStatusSet.stream().map(SmsTencentResult.SendStatus::getCode).collect(Collectors.toList()).contains("LimitExceeded.PhoneNumberOneHourLimit")) {
                return Result.fail("今日发送验证码次数已达上限");
            }
        }
        SmsTencentResult.Error error = response.getError();
        if (null != error) {
            if (error.getMessage().contains("PhoneNumberDailyLimit")) {
                return Result.fail("今日发送验证码次数已达上限");
            }
            return Result.fail("发送验证码失败");
        }
        String key = RedisConstant.LOGIN_SMS + IdUtil.simpleUUID();
        // 存储验证码到redis中, 5分钟过期
        redisService.set(key, code, 1000 * 60 * 5);
        redisService.set(ipKey, clientIP, 1000 * 60 * 5);
        return Result.success(key);
    }
}
