package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.ApplicationKnowledge;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * Description: 应用-知识库关联表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 19:57:12
 *
 */
public interface ApplicationKnowledgeService extends IService<ApplicationKnowledge> {

    Result addApplicationKnowledge(ApplicationKnowledge applicationKnowledge);

    Result getApplicationKnowledgeList(ApplicationKnowledge applicationKnowledge);

    Result updateApplicationKnowledge(ApplicationKnowledge applicationKnowledge);

    Result deleteApplicationKnowledge(List<String> idList);

    List<String> getAppNameByKnowledgeId(String knowledgeId);


    List<String> getListByApplicationId(String applicationId,String type);

    /**
     * 获取知识库关联的应用数量
     * @return
     */
    Map<String, Integer> getAppCount();
}