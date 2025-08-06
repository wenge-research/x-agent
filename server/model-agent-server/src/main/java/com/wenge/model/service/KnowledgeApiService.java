package com.wenge.model.service;

import cn.hutool.json.JSONObject;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.KnowledgeApiBatchDeleteParamParam;
import com.wenge.model.dto.param.KnowledgeApiPageQueryParam;
import com.wenge.model.entity.KnowledgeApi;
import com.wg.appframe.core.bean.Result;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Description: 应用配置服务类
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-05-10 10:20
 *
 */
public interface KnowledgeApiService extends IService<KnowledgeApi> {

    /**
     * 新增或修改
     * @param knowledgeApi
     * @return
     */
    Result addOrUpdate(KnowledgeApi knowledgeApi);


    /**
     * 分页查询
     * @param param
     * @return
     */
    Result pageQuery(KnowledgeApiPageQueryParam param);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    Result queryDetailById(Long id);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    KnowledgeApi queryDetail(Long id);

    /**
     * 根据id列表批量删除
     * @param param
     * @return
     */
    void deleteByIdList(KnowledgeApiBatchDeleteParamParam param);

    JSONObject request(KnowledgeApi knowledgeApi) throws NoSuchAlgorithmException, KeyManagementException;

    /**
     *
     * @param knowledgeApi
     * @return
     */
    Result runApi(KnowledgeApi knowledgeApi);

    public void updateKnowledgeApi(KnowledgeApi knowledgeApi);

    /**
     * 请求任务
     * @param knowledgeApi
     */
    public void requestTask(KnowledgeApi knowledgeApi);
}