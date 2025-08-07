package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.KnowledgeInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 知识库管理数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 14:10:23
 *
 */
@Mapper
public interface KnowledgeInfoMapper extends BaseMapper<KnowledgeInfo> {
}