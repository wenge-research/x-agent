package com.wenge.oauth.service.impl;

import cn.hutool.json.JSONObject;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wenge.oauth.dto.param.UmsOperationParam;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.entity.UmsOperation;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.es.UmsOperationMapper;
import com.wenge.oauth.service.UmsOperationService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DELL
 */
@Service
@Slf4j
public class UmsOperationServiceImpl implements UmsOperationService {

    @Autowired
    private UmsOperationMapper umsOperationMapper;

    @Override
    public Result addUmsOperationg(UmsOperation umsOperation) {
        try {
            return Result.success(umsOperationMapper.insert(umsOperation));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail();
        }
    }

    @Override
    public Result addUmsOperationBatch(List<UmsOperation> umsOperationList) {
        try {
            return Result.success(umsOperationMapper.insertBatch(umsOperationList));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail();
        }
    }

    @Override
    public Result umsOperationList(UmsOperationParam param) throws IOException {

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        LambdaEsQueryWrapper<UmsOperation> wrapper = EsWrappers.lambdaQuery(UmsOperation.class);
        List<String> operatorSources = new ArrayList<>();
        operatorSources.add("annotate-UmsOperationLog");
        operatorSources.add("annotate-no");

        // 判断是否为超级管理员
        String powerType = tokenUserInfo.getPowerType();

        // 超级管理员可以看所有操作日志
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(powerType)) {
            wrapper.and(and -> {
                // 普通管理员只能看自己的操作日志
                and.or().eq(UmsOperation::getOperatorTagUserId, tokenUserInfo.getId());
                if (PowerTypeEnum.NORMAL_ADMIN.getCode().equals(powerType)) {
                    // 普通管理可以查看租户下的所有用户的操作日志
                    String tenantId = tokenUserInfo.getTenantId();
                    if (StringUtils.isBlank(tenantId)) {
                        // 如果没有住户
                        tenantId = "-1";
                    }
                    and.or().eq(UmsOperation::getOperatorTagTenantId, tenantId);
                }
            });
        }

        wrapper.in(UmsOperation::getOperatorSource, operatorSources);

        if (StringUtils.isNotBlank(param.getStartAccessTime()) && StringUtils.isNotBlank(param.getEndAccessTime())) {
            wrapper.between(UmsOperation::getAccessTime, param.getStartAccessTime(), param.getEndAccessTime());
        }
        if (StringUtils.isNotBlank(param.getUri())) {
            wrapper.like(UmsOperation::getUri, param.getUri());
        }
        if (StringUtils.isNotBlank(param.getSystemName())) {
            wrapper.like(UmsOperation::getSystemName, param.getSystemName());
        }
        if (StringUtils.isNotBlank(param.getBrowserName())) {
            wrapper.like(UmsOperation::getBrowserName, param.getBrowserName());
        }
        if (StringUtils.isNotBlank(param.getDescription())) {
            wrapper.like(UmsOperation::getDescription, param.getDescription());
        }
        if (StringUtils.isNotBlank(param.getIp())) {
            wrapper.like(UmsOperation::getIp, param.getIp());
        }
        if (StringUtils.isNotBlank(param.getMethod())) {
            wrapper.like(UmsOperation::getMethod, param.getMethod());
        }
        if (param.getLogType() != null) {
            wrapper.eq(UmsOperation::getLogType, param.getLogType());
        }
        if (StringUtils.isNotBlank(param.getOperatorTagAccountName())) {
            wrapper.like(UmsOperation::getOperatorTagAccountName, param.getOperatorTagAccountName())
                    .or().like(UmsOperation::getOperatorTagUserName, param.getOperatorTagAccountName());

        }
        if (StringUtils.isNotBlank(param.getKeyword().trim())) {
            wrapper.multiMatchQuery(param.getKeyword().trim(),
                    UmsOperation::getOperatorTagAccountName,
                    UmsOperation::getObjectName,
                    UmsOperation::getBelongModuleName,
                    UmsOperation::getDescription,
                    UmsOperation::getOperatorTagUserName
            );
        }
        wrapper.orderByDesc(UmsOperation::getAccessTime);
        EsPageInfo<UmsOperation> pageInfo = umsOperationMapper.pageQuery(wrapper, param.getPageNo(),
                param.getPageSize());

        Map<Integer, String> logTypeMap = new HashMap<>();
        for (String logType : UmsOperationLog.logTypes) {
            final String[] splitStr = logType.split("-");
            logTypeMap.put(Integer.parseInt(splitStr[1] + ""), splitStr[0]);
        }
        pageInfo.getList().forEach(umsOperation -> {
            if (umsOperation.getLogType()!=null) {
                umsOperation.setLogTypeName(logTypeMap.get(umsOperation.getLogType()));
            }
        });
        return Result.success(pageInfo);
    }

    @Override
    public Result getLogTypeList() {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (String logType : UmsOperationLog.logTypes) {
            final String[] split = logType.split("-");
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("value", split[0]);
            jsonObject.set("key", split[1]);
            jsonObjectList.add(jsonObject);
        }
        return Result.success(jsonObjectList);
    }
}

