package com.wenge.model.service.impl;


import com.google.common.collect.Maps;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.SceneApplicationRef;
import com.wenge.model.entity.SceneManagement;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.mapper.SceneApplicationRefMapper;
import com.wenge.model.service.SceneApplicationRefService;
import com.wenge.model.service.SceneManagementService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.SceneApplicationRefTableDef.SCENE_APPLICATION_REF;

/**
 * Description: 业务场景与应用关联表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:45:38
 *
 */
@Service
@Slf4j
public class SceneApplicationRefServiceImpl extends ServiceImpl<SceneApplicationRefMapper, SceneApplicationRef> implements SceneApplicationRefService {
	/**
	 * 	业务场景与应用关联表数据库处理类
	 */
	@Autowired
	private SceneApplicationRefMapper sceneApplicationRefMapper;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private SceneManagementService sceneManagementService;

	@Override
	public Result addSceneApplicationRef(SceneApplicationRef sceneApplicationRef){
		if (StringUtils.isBlank(sceneApplicationRef.getSceneId()) || StringUtils.isBlank(sceneApplicationRef.getApplicationId())) {
			return Result.error();
		}

		boolean exists = Wrappers.of(mapper)
				.where(SCENE_APPLICATION_REF.SCENE_ID.eq(sceneApplicationRef.getSceneId()))
				.and(SCENE_APPLICATION_REF.APPLICATION_ID.eq(sceneApplicationRef.getApplicationId()))
				.exists();
		if (exists) {
			return Result.fail("该场景已关联");
		}

		save(sceneApplicationRef);
		publisher.publishEvent(new SceneEvent(null));
		return Result.success();
	}

	@Override
	public Result<List<SceneApplicationRef>> getSceneApplicationRefList(SceneApplicationRef sceneApplicationRef){
		if (StringUtils.isBlank(sceneApplicationRef.getApplicationId())) {
			return Result.success(Lists.newArrayList());
		}
		List<SceneApplicationRef> list = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(sceneApplicationRef.getSceneId()), SCENE_APPLICATION_REF.SCENE_ID.eq(sceneApplicationRef.getSceneId()))
				.and(StringUtils.isNotBlank(sceneApplicationRef.getApplicationId()), SCENE_APPLICATION_REF.APPLICATION_ID.eq(sceneApplicationRef.getApplicationId()))
				.list();
		return Result.success(list);
	}

	@Override
	public Result updateSceneApplicationRef(SceneApplicationRef sceneApplicationRef){
		updateById(sceneApplicationRef);
		return Result.success();
	}

	@Override
	public Result deleteSceneApplicationRef(SceneApplicationRef sceneApplicationRef){
		if (StringUtils.isBlank(sceneApplicationRef.getSceneId()) || StringUtils.isBlank(sceneApplicationRef.getApplicationId())) {
			return Result.error();
		}
		UpdateChain.create(mapper)
				.where(SCENE_APPLICATION_REF.SCENE_ID.eq(sceneApplicationRef.getSceneId()))
				.and(SCENE_APPLICATION_REF.APPLICATION_ID.eq(sceneApplicationRef.getApplicationId()))
				.remove();
		publisher.publishEvent(new SceneEvent(null));
		return Result.success();
	}

	@Override
	public List<SceneManagement> getSceneByAppId(String applicationId) {
		if (StringUtils.isBlank(applicationId)) {
			return Lists.newArrayList();
		}

		List<SceneApplicationRef> list = Wrappers.of(mapper)
				.where(SCENE_APPLICATION_REF.APPLICATION_ID.eq(applicationId))
				.list();
		if (CollectionUtils.isNotEmpty(list)) {
			List<String> scenceIdList = list.stream().map(SceneApplicationRef::getSceneId).distinct().collect(Collectors.toList());
            return sceneManagementService.getSceneManagementList(scenceIdList);
		}

		return Lists.newArrayList();
	}

	@Override
	public List<String> getSceneIdsByAppId(String applicationId) {
		if (StringUtils.isNotBlank(applicationId)) {
			List<SceneApplicationRef> list = Wrappers.of(mapper)
					.where(SCENE_APPLICATION_REF.APPLICATION_ID.eq(applicationId))
					.list();
			if (CollectionUtils.isNotEmpty(list)) {
				return list.stream().map(SceneApplicationRef::getSceneId).distinct().collect(Collectors.toList());
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public Map<String, Long> getAppRefNum(List<String> sceneIds) {
		if (CollectionUtils.isEmpty(sceneIds)) {
			return Maps.newHashMap();
		}

		List<SceneApplicationRef> list = Wrappers.of(mapper)
				.where(SCENE_APPLICATION_REF.SCENE_ID.in(sceneIds))
				.list();
		return list.stream().collect(Collectors.groupingBy(SceneApplicationRef::getSceneId, Collectors.counting()));
	}

	@Override
	public List<String> getAppIdsBySceneId(List<String> sceneIds) {
		if (CollectionUtils.isEmpty(sceneIds)) {
			return Lists.newArrayList();
		}

		List<SceneApplicationRef> list = Wrappers.of(mapper)
				.select(APPLICATION_INFO.APPLICATION_NAME)
				.from(APPLICATION_INFO)
				.innerJoin(SCENE_APPLICATION_REF).on(SCENE_APPLICATION_REF.APPLICATION_ID.eq(APPLICATION_INFO.APPLICATION_ID))
				.where(SCENE_APPLICATION_REF.SCENE_ID.in(sceneIds))
				.list();
		return list.stream().map(SceneApplicationRef::getApplicationName).distinct().collect(Collectors.toList());
	}
}