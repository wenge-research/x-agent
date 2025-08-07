package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.InterceptWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Description: 拦截词数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
@Mapper
public interface InterceptWordMapper extends BaseMapper<InterceptWord> {

    @Select("SELECT t.* FROM intercept_word_type t where t.status = 0 LIMIT 100")
    List<Map<String, Object>> getInterceptWordTypeList();

    @Select("SELECT t.* FROM intercept_word_handling_method t LIMIT 100 ")
    List<Map<String, Object>> getInterceptHandlingMethodList();
}