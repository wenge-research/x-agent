package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 文件数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 *
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {
}