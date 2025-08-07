package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.entity.InterceptWordHouseApplicationRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: caohaifeng
 * @date: 2024/8/22 15:03
 * @Description: 拦截词库与应用关联关系处理类
 * @Version:1.0
 **/
@Mapper
public interface InterceptWordHouseApplicationRelMapper extends BaseMapper<InterceptWordHouseApplicationRel> {
}