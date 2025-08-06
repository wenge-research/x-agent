package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ConversationPage;
import com.wenge.model.entity.Conversation;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 会话服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-19 09:31:50
 *
 */
public interface ConversationService extends IService<Conversation> {

    Result addConversation(Conversation conversation);

    Result getConversationList(ConversationPage conversation);

    Result updateConversation(Conversation conversation);

    Result deleteConversation(List<String> idList);

}