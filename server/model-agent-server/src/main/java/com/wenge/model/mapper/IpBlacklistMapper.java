package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.IpBlacklist;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: IP黑名单数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 15:42:07
 *
 */
@Mapper
public interface IpBlacklistMapper extends BaseMapper<IpBlacklist> {
}