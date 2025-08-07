package com.wenge.oauth.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 用户-角色关联表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:12:35
 *
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}