package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.LhOnline;

import java.util.List;

/**
 * Description: 龙华政府在线服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-20 15:57:11
 *
 */
public interface LhOnlineService extends IService<LhOnline> {

    List<LhOnline> getActiveList();
}