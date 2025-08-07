package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.LlmInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 大模型信息表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-14 14:53:29
 *
 */
@Mapper
public interface LlmInfoMapper extends BaseMapper<LlmInfo> {
}