package com.wenge.oauth.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.AuthChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 认证方式渠道配置表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-02 15:28:16
 *
 */
@Mapper
public interface AuthChannelMapper extends BaseMapper<AuthChannel> {
}