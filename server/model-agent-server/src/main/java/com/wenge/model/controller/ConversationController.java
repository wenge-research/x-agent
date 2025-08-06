package com.wenge.model.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ConversationPage;
import com.wenge.model.entity.Conversation;
import com.wenge.model.service.ConversationService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 会话接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-19 09:31:50
 *
 */
@RestController
@RequestMapping("/conversation")
@Slf4j
public class ConversationController {

	/**
	 * 	会话服务类
	 */
	@Autowired
	private ConversationService conversationService;

	/**
	 * 新增会话
	 */
	@PostMapping("/addConversation")
	public Result<Conversation> addConversation(@RequestBody Conversation conversation) {
		return conversationService.addConversation(conversation);
	}

	/**
	 * 查询会话列表
	 */
	@PostMapping("/getConversationList")
	public Result<Page<Conversation>> getConversationList(@RequestBody ConversationPage conversation) {
		return conversationService.getConversationList(conversation);
	}

	/**
	 * 更新会话
	 */
	@PostMapping("/updateConversation")
	public Result updateConversation(@RequestBody Conversation conversation) {
		return conversationService.updateConversation(conversation);
	}

	/**
	 * 删除会话
	 */
	@PostMapping("/deleteConversation")
	public Result deleteConversation(@RequestBody List<String> idList) {
		return conversationService.deleteConversation(idList);
	}

}