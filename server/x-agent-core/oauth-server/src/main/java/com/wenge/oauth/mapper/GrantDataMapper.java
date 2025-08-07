package com.wenge.oauth.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.GrantData;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 授权数据数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-08 19:59:43
 *
 */
@Mapper
public interface GrantDataMapper extends BaseMapper<GrantData> {
}