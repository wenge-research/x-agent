package com.wenge.oauth.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.OperaLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 操作日志数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 16:17:49
 *
 */
@Mapper
public interface OperaLogMapper extends BaseMapper<OperaLog> {
}