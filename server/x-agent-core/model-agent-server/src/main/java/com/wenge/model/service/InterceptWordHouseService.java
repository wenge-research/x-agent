package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.InterceptWordHousePageParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.entity.InterceptWord;
import com.wenge.model.entity.InterceptWordHouse;
import com.wg.appframe.core.bean.Result;

import java.util.List;


/**
 * @author: caohaifeng
 * @date: 2024/8/22 15:28
 * @Description: 拦截词库服务类
 * @Version:1.0
 **/
public interface InterceptWordHouseService extends IService<InterceptWordHouse> {

    Result addInterceptWordHouse(InterceptWordHouse interceptWordHouse);

    Result getInterceptWordHouseList(InterceptWordHousePageParam interceptWordHouse);

    Result getInterceptWordHouseListAll(InterceptWordHousePageParam interceptWordHouse);

    void updateHouseApplicationCount(Long interceptWordHouseId);

    Result updateInterceptWordHouse(InterceptWordHouse interceptWordHouse);

    Result deleteInterceptWordHouse(List<String> idList);

    Result updateStatus(InterceptWordHouse interceptWordHouse);


    Result setPreset(InterceptWordHousePageParam interceptWordHouse);
}