package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.LifeServeGuideInfo;

import java.util.List;

public interface LifeServeGuideInfoService extends IService<LifeServeGuideInfo> {


    // 通过事项id获取生活服务关联的信息
    List<LifeServeGuideInfo> getListByHandleMethodIdAndMatterGuideId(String matterGuideId);

    // 通过敏感词id和敏感词处理方式id获取生活服务关联的信息
    List<LifeServeGuideInfo> getListByHandleMethodIdAndInterceptWordId(Long handleMethodId, Long interceptWordId);

}