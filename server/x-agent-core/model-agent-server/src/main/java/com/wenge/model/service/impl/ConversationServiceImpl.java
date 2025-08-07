package com.wenge.model.service.impl;


import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ConversationPage;
import com.wenge.model.entity.Conversation;
import com.wenge.model.mapper.ConversationMapper;
import com.wenge.model.service.ConversationService;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.ConversationTableDef.CONVERSATION;

/**
 * Description: 会话服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-19 09:31:50
 *
 */
@Service
@Slf4j
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {
	/**
	 * 	会话数据库处理类
	 */
	@Autowired
	private ConversationMapper conversationMapper;

	@Override
	public Result addConversation(Conversation conversation){
		if (StringUtils.isBlank(conversation.getConversationId())) {
			conversation.setConversationId(IdUtil.simpleUUID());
		}
		saveOrUpdate(conversation);
		return Result.success(conversation);
	}

	@Override
	public Result getConversationList(ConversationPage conversation){
		TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
		Wrappers<Object> wrappers = Wrappers.init()
				.where(CONVERSATION.UPDATE_USER.eq(tokenOauthUserInfo.getAccountName()));
		Page<Conversation> page = page(Page.of(conversation.getPageNo(), conversation.getPageSize()), wrappers);
		return Result.success(page);
	}

	@Override
	public Result updateConversation(Conversation conversation){
		updateById(conversation);
		return Result.success();
	}

	@Override
	public Result deleteConversation(List<String> idList) {
		removeByIds(idList);
		return Result.success();
	}
}