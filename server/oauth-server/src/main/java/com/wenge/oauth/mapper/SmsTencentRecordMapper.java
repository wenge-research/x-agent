package com.wenge.oauth.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.SmsTencentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 腾讯云短信记录表数据库处理类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-02-08 18:11:08
 *
 */
@Mapper
public interface SmsTencentRecordMapper extends BaseMapper<SmsTencentRecord> {
}