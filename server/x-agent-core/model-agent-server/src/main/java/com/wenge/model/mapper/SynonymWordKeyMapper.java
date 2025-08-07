package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.SynonymWordKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同义词库数据库处理类
 */
@Mapper
public interface SynonymWordKeyMapper extends BaseMapper<SynonymWordKey> {

}