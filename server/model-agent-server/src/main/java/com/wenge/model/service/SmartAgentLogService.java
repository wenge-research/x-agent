package com.wenge.model.service;

import com.wenge.model.dto.param.SmartAgentLogParam;
import com.wenge.model.entity.SmartAgentLog;
import com.wg.appframe.core.bean.Result;

import java.io.IOException;
import java.util.List;

/**
 * Description: 应用信息服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
public interface SmartAgentLogService  {

    Result addSmartAgentLog(SmartAgentLog smartAgentLog);

    Result addSmartAgentLogBatch(List<SmartAgentLog> smartAgentLogs);

    Result smartAgentLogList(SmartAgentLogParam smartAgentLogParam) throws IOException;

    Result smartAgentLogListGroupTime(SmartAgentLogParam smartAgentLogParam) throws IOException;

}