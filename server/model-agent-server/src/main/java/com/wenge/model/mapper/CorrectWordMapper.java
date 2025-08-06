package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.CorrectWord;
import com.wenge.model.entity.InterceptWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 纠错词数据库处理类
 */
@Mapper
public interface CorrectWordMapper extends BaseMapper<CorrectWord> {

}