package com.wenge.model.service.impl;

import com.google.common.collect.Lists;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.ApplicationKnowledge;
import com.wenge.model.mapper.ApplicationKnowledgeMapper;
import com.wenge.model.service.ApplicationKnowledgeService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

/**
 * Description: 应用-知识库关联表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 19:57:12
 *
 */
@Service
@Slf4j
public class ApplicationKnowledgeServiceImpl extends ServiceImpl<ApplicationKnowledgeMapper, ApplicationKnowledge> implements ApplicationKnowledgeService {
	/**
	 * 	应用-知识库关联表数据库处理类
	 */
	@Autowired
	private ApplicationKnowledgeMapper applicationKnowledgeMapper;

	@Override
	public Result addApplicationKnowledge(ApplicationKnowledge applicationKnowledge){
		save(applicationKnowledge);
		return Result.success();
	}

	@Override
	public Result getApplicationKnowledgeList(ApplicationKnowledge applicationKnowledge){
		return Result.success();
	}

	@Override
	public Result updateApplicationKnowledge(ApplicationKnowledge applicationKnowledge){
		updateById(applicationKnowledge);
		return Result.success();
	}

	@Override
	public Result deleteApplicationKnowledge(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<String> getAppNameByKnowledgeId(String knowledgeId) {
		if (StringUtils.isBlank(knowledgeId)) {
			return Lists.newArrayList();
		}

		List<ApplicationKnowledge> list = Wrappers.of(mapper)
				.select(APPLICATION_INFO.APPLICATION_NAME)
				.innerJoin(APPLICATION_INFO).on(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(APPLICATION_INFO.APPLICATION_ID))
				.innerJoin(KNOWLEDGE_INFO).on(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID))
				.where(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeId))
				.list();

		return list.stream().map(ApplicationKnowledge::getApplicationName).distinct().collect(Collectors.toList());
	}

	@Override
	public List<String> getListByApplicationId(String applicationId, String type) {
		if (StringUtils.isBlank(applicationId)) {
			return Lists.newArrayList();
		}
		List<ApplicationKnowledge> list = Wrappers.of(mapper)
				.where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(applicationId))
				.and(APPLICATION_KNOWLEDGE.TYPE.eq(type))
				.list();
		return list.stream().map(ApplicationKnowledge::getKnowledgeId).distinct().collect(Collectors.toList());
	}



	@Override
	public Map<String, Integer> getAppCount() {
		QueryWrapper queryWrapper = Wrappers.create()
				.select("knowledge_id", "count(1) associatedApp")
				.from(APPLICATION_KNOWLEDGE)
				.groupBy(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID);
		List<ApplicationKnowledge> list = list(queryWrapper);
		return list.stream().collect(Collectors.toMap(ApplicationKnowledge::getKnowledgeId, ApplicationKnowledge::getAssociatedApp, (v1, v2) -> v1));
	}

	@Override
	public List<ApplicationKnowledge> list() {
		Wrappers<ApplicationKnowledge> on = Wrappers.of(mapper)
//				.select(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID, KNOWLEDGE_INFO.KNOWLEDGE_NAME)
				.select(APPLICATION_KNOWLEDGE.ALL_COLUMNS, KNOWLEDGE_INFO.KNOWLEDGE_NAME)
				.innerJoin(KNOWLEDGE_INFO).on(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(APPLICATION_KNOWLEDGE.KNOWLEDGE_ID));

		List<ApplicationKnowledge> list = list(on);
		return list;
	}
}