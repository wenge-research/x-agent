package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.DenseVector;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 向量化模型数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-09-11 14:45:53
 *
 */
@Mapper
public interface DenseVectorMapper extends BaseMapper<DenseVector> {
}