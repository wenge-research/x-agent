package com.wenge.oauth.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 租户表数据库处理类
 * @Author: author
 * Version: 1.0
 * Create Date Time: 2024-06-28 14:31:45
 *
 */
@Mapper
public interface OauthTenantMapper extends BaseMapper<Tenant> {
}