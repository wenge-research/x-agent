package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.KnowledgeDataAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface KnowledgeDataAnalysisMapper  extends BaseMapper<KnowledgeDataAnalysis> {

    @Update("truncate table knowledge_data_analysis")
    public void truncate() ;

}
