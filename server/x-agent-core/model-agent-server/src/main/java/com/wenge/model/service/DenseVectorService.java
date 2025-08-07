package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.DenseVectorPageParam;
import com.wenge.model.entity.DenseVector;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 向量化模型服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-09-11 14:45:53
 *
 */
public interface DenseVectorService extends IService<DenseVector> {

    Result addDenseVector(DenseVector denseVector);

    Result getDenseVectorList(DenseVectorPageParam denseVector);

    Result updateDenseVector(DenseVector denseVector);

    Result deleteDenseVector(List<String> idList);

    /**
     * 通过id获取向量化模型
     */
    DenseVector getDenseVectorById(String vectorId);

    /**
     * 通过id获取向量化模型
     * @param vectorId
     * @return
     */
    List<DenseVector> getDenseVectorByIds(List<String> vectorId);

    /**
     * 通过编码获取向量化模型
     *
     * @param vectorCode
     * @return
     */
    DenseVector getDenseVectorCode(String vectorCode);

    /**
     * 对文本进行向量化
     * @param query
     * @param knowledgeId
     * @param data
     * @param field
     * @return
     */
    List<Double> modelEncode(String query, String knowledgeId, Object data, String field);

    /**
     * 对文本进行向量化
     * @param query
     * @param knowledgeId
     * @return
     */
    List<Double> modelEncode(String query, String knowledgeId);

    /**
     * 获取所有的向量化模型
     */
    List<DenseVector> getAllDenseVector();

    /**
     * 获取默认的一个向量模型
     */
    DenseVector getOneDefaultDenseVector();
}

