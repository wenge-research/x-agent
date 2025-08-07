package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.AiSearchDataAnalysis;
import com.wg.appframe.core.bean.Result;

public interface AiSearchDataAnalysisService extends IService<AiSearchDataAnalysis> {

    /**
     * 点赞
     * @param aiSearchDataAnalysis
     */
    Result like(AiSearchDataAnalysis aiSearchDataAnalysis);


    /**
     * 浏览
     * @param aiSearchDataAnalysis
     */
    Result read(AiSearchDataAnalysis aiSearchDataAnalysis);

    /**
     * 订阅或者取消
     * @param aiSearchDataAnalysis
     * @param status 0 取消/ 1 订阅
     */
    Result subOrUnSub(AiSearchDataAnalysis aiSearchDataAnalysis, String status);

}
