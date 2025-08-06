package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.SmsTencentRecord;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 腾讯云短信记录表服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-02-08 18:11:08
 *
 */
public interface SmsTencentRecordService extends IService<SmsTencentRecord> {

    void addSmsTencentRecord(List<SmsTencentRecord> smsTencentRecords);

}