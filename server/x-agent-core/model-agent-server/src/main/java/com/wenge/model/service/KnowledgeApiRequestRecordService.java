package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.KnowledgeApiRequestRecordPageQueryParam;
import com.wenge.model.entity.KnowledgeApiRequestRecord;
import com.wg.appframe.core.bean.Result;

/**
 * Description: 应用配置服务类
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-05-10 10:20
 *
 */
public interface KnowledgeApiRequestRecordService extends IService<KnowledgeApiRequestRecord> {


    /**
     * 分页查询
     * @param param
     * @return
     */
    Result pageQuery(KnowledgeApiRequestRecordPageQueryParam param);

    /**
     * 插入执行请求记录
     * @param knowledgeApiRequestRecord
     * @return
     */
    Result saveRecord(KnowledgeApiRequestRecord knowledgeApiRequestRecord);

}