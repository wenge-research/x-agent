package com.wenge.oauth.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 角色-菜单关联表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:12:25
 *
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}