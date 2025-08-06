package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.OperaLog;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 操作日志服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 16:17:49
 *
 */
public interface OperaLogService extends IService<OperaLog> {

    Result addOperaLog(OperaLog operaLog);

    Result getOperaLogList(OperaLog operaLog);

    Result updateOperaLog(OperaLog operaLog);

    Result deleteOperaLog(List<String> idList);

    /**
     * 获取操作日志总数
     * @param operaLog
     * @return
     */
    Long getOperaLogCount(OperaLog operaLog);

}