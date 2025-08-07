package com.wenge.model.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.GroupMatterRefAddParam;
import com.wenge.model.dto.param.SceneMatterGroupRefListParam;
import com.wenge.model.entity.MatterGuide;
import com.wenge.model.entity.SceneManagement;
import com.wenge.model.entity.SceneMatterGroupRef;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.mapper.SceneMatterGroupRefMapper;
import com.wenge.model.service.MatterGuideService;
import com.wenge.model.service.SceneManagementService;
import com.wenge.model.service.SceneMatterGroupRefService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.SUBJECT_SCENE_ENTRANCE;
import static com.wenge.model.entity.table.SceneMatterGroupRefTableDef.SCENE_MATTER_GROUP_REF;

/**
 * Description: 服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:20:59
 *
 */
@Service
@Slf4j
public class SceneMatterGroupRefServiceImpl extends ServiceImpl<SceneMatterGroupRefMapper, SceneMatterGroupRef> implements SceneMatterGroupRefService {
	/**
	 * 	数据库处理类
	 */
	@Autowired
	private SceneMatterGroupRefMapper sceneMatterGroupRefMapper;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private SceneManagementService sceneManagementService;

	@Autowired
	private MatterGuideService matterGuideService;

	@Override
	public Result addSceneMatterGroupRef(GroupMatterRefAddParam param){
		if (StringUtils.isBlank(param.getSceneId())) {
			return Result.fail("场景id不能为空");
		}

		// 批量插入
		StringParam sceneIdParam = new StringParam();
		sceneIdParam.setParam(param.getSceneId());
		Result<SceneManagement> sceneDetail = sceneManagementService.getSceneDetail(sceneIdParam);
		SceneManagement data = sceneDetail.getData();
		if (StringUtils.isBlank(data.getProcessing())) {
			return Result.fail("处理方式不能为空");
		}
		if (StringUtils.isNotBlank(data.getProcessing())) {
			JSONObject process = JSON.parseObject(data.getProcessing());
			String way = process.getString("way");
			if ("事项".equals(way)) {
				List<GroupMatterRefAddParam.GroupMatter> groupMatterList = param.getGroupMatterList();
				boolean groupEmpty = groupMatterList.stream().anyMatch(p -> StringUtils.isBlank(p.getGroupId()));
				if (groupEmpty) {
					return Result.fail("当前场景处理方式为事项，分组不能为空");
				}
				List<SceneMatterGroupRef> matterGroupRefList = param.getGroupMatterList().stream().flatMap(p -> p.getMatterList().stream()).collect(Collectors.toList());
				if (CollectionUtils.isEmpty(matterGroupRefList)) {
					return Result.fail("当前场景处理方式为事项，事项不能为空");
				}

				List<String> matterIdList = groupMatterList.stream().flatMap(p -> p.getMatterList().stream()).map(SceneMatterGroupRef::getMatterId).distinct().collect(Collectors.toList());
				List<MatterGuide> matterGuideList = matterGuideService.getMatterByMatterIds(matterIdList);
				if (CollectionUtils.isEmpty(matterGuideList)) {
					return Result.fail("事项已不存在");
				}
				boolean anyMatch = matterGuideList.stream().anyMatch(p -> p.getMatterType().equals(SUBJECT_SCENE_ENTRANCE));
				if (!anyMatch) {
					int size = matterGuideList.size();
					if (1 == size) {
						MatterGuide matterGuide = matterGuideList.get(0);
						if (StringUtils.isBlank(matterGuide.getExtraSystemPrompt()) || StringUtils.isBlank(matterGuide.getPrompt())) {
							return Result.fail("事项【" + matterGuide.getMatterName() + "】的系统提示词和开场白不能为空");
						}
					} else {
						return Result.fail("当前场景处理方式为事项，不能缺少【事项-场景入口】的事项");
					}
				}
			}
		}

		// 根据场景id，先清空事项和分组的关联数据
		UpdateChain.create(mapper)
				.where(SCENE_MATTER_GROUP_REF.SCENE_ID.eq(param.getSceneId()))
				.remove();

		if (CollectionUtils.isNotEmpty(param.getGroupMatterList())) {
			List<GroupMatterRefAddParam.GroupMatter> groupMatterList = param.getGroupMatterList();
			List<SceneMatterGroupRef> sceneMatterGroupRefs = groupMatterList.stream().flatMap(item -> {
				List<SceneMatterGroupRef> refList = item.getMatterList();
				if (CollectionUtils.isNotEmpty(refList)) {
					refList.forEach(ref -> {
						ref.setSceneId(param.getSceneId());
						ref.setGroupId(item.getGroupId());
					});
				}
				return refList.stream();
			}).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(sceneMatterGroupRefs)) {
				for (SceneMatterGroupRef sceneMatterGroupRef : sceneMatterGroupRefs) {
					save(sceneMatterGroupRef);
				}
			}
		}
		publisher.publishEvent(new SceneEvent(null));
		return Result.success();
	}

	@Override
	public Result<List<SceneMatterGroupRef>> getSceneMatterGroupRefList(SceneMatterGroupRefListParam param){
		if (StringUtils.isBlank(param.getSceneId())) {
			return Result.success(Lists.newArrayList());
		}

		List<SceneMatterGroupRef> list = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(param.getSceneId()), SCENE_MATTER_GROUP_REF.SCENE_ID.eq(param.getSceneId()))
				.list();
		return Result.success(list);
	}

	@Override
	public Result updateSceneMatterGroupRef(SceneMatterGroupRef sceneMatterGroupRef){
		updateById(sceneMatterGroupRef);
		return Result.success();
	}

	@Override
	public Result deleteSceneMatterGroupRef(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<String> getMatterIdList(List<String> groupIdList) {
		if (CollectionUtils.isEmpty(groupIdList)) {
			return Lists.newArrayList();
		}

		List<SceneMatterGroupRef> groupRefs = Wrappers.of(mapper)
				.where(SCENE_MATTER_GROUP_REF.GROUP_ID.in(groupIdList))
				.list();
		return groupRefs.stream().map(SceneMatterGroupRef::getGroupId).distinct().collect(Collectors.toList());
	}

	@Override
	public List<String> getGroupIdListByMatterId(List<String> matterIds) {
		if (CollectionUtils.isEmpty(matterIds)) {
			return Lists.newArrayList();
		}

		List<SceneMatterGroupRef> list = Wrappers.of(mapper)
				.where(SCENE_MATTER_GROUP_REF.MATTER_ID.in(matterIds))
				.list();

		return list.stream().map(SceneMatterGroupRef::getGroupId).distinct().collect(Collectors.toList());
	}
}