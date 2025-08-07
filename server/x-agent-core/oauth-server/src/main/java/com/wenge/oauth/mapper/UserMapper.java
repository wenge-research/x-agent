package com.wenge.oauth.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.OauthUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 用户表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-05 14:13:29
 *
 */
@Mapper
public interface UserMapper extends BaseMapper<OauthUser> {
}