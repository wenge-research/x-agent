package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.AiSearchFileSubHistory;
import com.wg.appframe.core.bean.Result;

public interface AiSearchFileSubHistoryService extends IService<AiSearchFileSubHistory> {

    /**
     * 订阅/取消订阅
     * @param aiSearchFileSubHistory
     */
    Result subOrUnSub(AiSearchFileSubHistory aiSearchFileSubHistory);


}
