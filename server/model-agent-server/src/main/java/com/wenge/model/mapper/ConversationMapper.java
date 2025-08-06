package com.wenge.model.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: 会话数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-19 09:31:50
 *
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}