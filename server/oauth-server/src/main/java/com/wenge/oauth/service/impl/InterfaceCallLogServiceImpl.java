package com.wenge.oauth.service.impl;

import cn.hutool.json.JSONObject;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wenge.oauth.dto.param.UmsOperationParam;
import com.wenge.oauth.entity.InterfaceCallLogRecording;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.entity.UmsOperation;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.es.InterfaceCallLogRecordingMapper;
import com.wenge.oauth.mapper.es.UmsOperationMapper;
import com.wenge.oauth.service.InterfaceCallLogService;
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
import java.util.List;
/**
 * @author DELL
 */
@Service
@Slf4j
public class InterfaceCallLogServiceImpl implements InterfaceCallLogService {

    @Autowired
    private InterfaceCallLogRecordingMapper interfaceCallLogRecordingMapper;

    @Override
    public Result addInterfaceCallLog(InterfaceCallLogRecording interfaceCallLogRecording) {
        try {
            return Result.success(interfaceCallLogRecordingMapper.insert(interfaceCallLogRecording));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail();
        }
    }

    @Override
    public Result addInterfaceCallLogBatch(List<InterfaceCallLogRecording> interfaceCallLogRecordingList) {
        try {
            return Result.success(interfaceCallLogRecordingMapper.insertBatch(interfaceCallLogRecordingList));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail();
        }
    }

    @Override
    public Result interfaceCallLogList(UmsOperationParam param) throws IOException {

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        LambdaEsQueryWrapper<InterfaceCallLogRecording> wrapper = EsWrappers.lambdaQuery(InterfaceCallLogRecording.class);
        List<String> operatorSources = new ArrayList<>();
        operatorSources.add("annotate-UmsOperationLog");operatorSources.add("annotate-no");
        wrapper.eq("operatorTagUserId", tokenUserInfo.getId()).in("operatorSource", operatorSources);
        if (StringUtils.isNotBlank(param.getStartAccessTime()) && StringUtils.isNotBlank(param.getEndAccessTime())) {
            wrapper.between("accessTime", param.getStartAccessTime(), param.getEndAccessTime());
        }
        if (StringUtils.isNotBlank(param.getUri())) {
            wrapper.like("uri", param.getUri());
        }
        if (StringUtils.isNotBlank(param.getSystemName())) {
            wrapper.like("systemName", param.getSystemName());
        }
        if (StringUtils.isNotBlank(param.getBrowserName())) {
            wrapper.like("browserName", param.getBrowserName());
        }
        if (StringUtils.isNotBlank(param.getDescription())) {
            wrapper.like("description", param.getDescription());
        }
        if (StringUtils.isNotBlank(param.getIp())) {
            wrapper.like("ip", param.getIp());
        }
        if (StringUtils.isNotBlank(param.getMethod())) {
            wrapper.like("method", param.getMethod());
        }
        if (param.getLogType() != null) {
            wrapper.eq("logType", param.getLogType());
        }
        if (StringUtils.isNotBlank(param.getOperatorTagAccountName())) {
            wrapper.like("operatorTagAccountName", param.getOperatorTagAccountName())
                    .or().like("operatorTagUserName", param.getOperatorTagAccountName());
        }
        if (StringUtils.isNotBlank(param.getKeyword().trim())) {
            wrapper.multiMatchQuery(param.getKeyword().trim(),
                    InterfaceCallLogRecording::getOperatorTagAccountName, InterfaceCallLogRecording::getObjectName, InterfaceCallLogRecording::getOperatorTagUserName);
        }
        wrapper.orderByDesc("accessTime");
        EsPageInfo<InterfaceCallLogRecording> pageInfo = interfaceCallLogRecordingMapper.pageQuery(wrapper, param.getPageNo(),
                param.getPageSize());
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

