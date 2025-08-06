package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.SynonymWord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同义词数据库处理类
 */
@Mapper
public interface SynonymWordMapper extends BaseMapper<SynonymWord> {

}