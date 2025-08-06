package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.McpServerUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * MCP关联用户
 */
@Mapper
public interface McpServerUserMapper extends BaseMapper<McpServerUser> {

}
