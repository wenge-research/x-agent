package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.BindInterceptWordHouseParam;
import com.wenge.model.dto.param.InterceptWordHouseApplicationRelPageParam;
import com.wenge.model.entity.InterceptWordHouseApplicationRel;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;


/**
 * @author: caohaifeng
 * @date: 2024/8/22 16:47
 * @Description: 拦截词库服务类
 * @Version:1.0
 **/
public interface InterceptWordHouseApplicationRelService extends IService<InterceptWordHouseApplicationRel> {

    Result addInterceptWordHouseApplicationRel(InterceptWordHouseApplicationRel interceptWordHouse);

    Result getInterceptWordHouseApplicationRelList(InterceptWordHouseApplicationRelPageParam interceptWordHouse);

    Result updateInterceptWordHouseApplicationRel(InterceptWordHouseApplicationRel interceptWordHouse);

    Result deleteInterceptWordHouseApplicationRel(List<String> idList);

    List<Long> getHouseIdByAppId(String appId);

    Map<String, List<Long>> getHouseIdByAppId(List<String> appIds);

    Result batchBindInterceptWordHouse(BindInterceptWordHouseParam param);
}