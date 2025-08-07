package com.wenge.oauth.service;

import com.wenge.oauth.dto.param.UmsOperationParam;
import com.wenge.oauth.entity.InterfaceCallLogRecording;
import com.wenge.oauth.entity.UmsOperation;
import com.wg.appframe.core.bean.Result;

import java.io.IOException;
import java.util.List;

/**
 * @description: 系统操作日志服务
 * @author: caohaifeng
 * @date: 2024/8/23 15:05
 **/
public interface InterfaceCallLogService {

    Result addInterfaceCallLog(InterfaceCallLogRecording interfaceCallLogRecording);

    Result addInterfaceCallLogBatch(List<InterfaceCallLogRecording> interfaceCallLogRecordingList);

    Result interfaceCallLogList(UmsOperationParam umsOperationParam) throws IOException;

    Result getLogTypeList();
}