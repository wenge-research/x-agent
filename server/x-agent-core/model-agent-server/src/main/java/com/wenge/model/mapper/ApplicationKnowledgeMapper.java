package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.ApplicationKnowledge;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 应用-知识库关联表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 19:57:12
 *
 */
@Mapper
public interface ApplicationKnowledgeMapper extends BaseMapper<ApplicationKnowledge> {
}