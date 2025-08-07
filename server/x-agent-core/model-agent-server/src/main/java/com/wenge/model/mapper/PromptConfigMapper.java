package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.PromptConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 提示词配置数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-12-18 11:02:37
 *
 */
@Mapper
public interface PromptConfigMapper extends BaseMapper<PromptConfig> {
}