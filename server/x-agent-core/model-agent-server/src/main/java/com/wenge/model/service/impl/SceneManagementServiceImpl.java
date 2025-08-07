package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.SceneManagementPageParam;
import com.wenge.model.entity.SceneManagement;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.mapper.SceneManagementMapper;
import com.wenge.model.service.SceneApplicationRefService;
import com.wenge.model.service.SceneManagementService;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.SceneManagementTableDef.SCENE_MANAGEMENT;

/**
 * Description: 业务场景表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 10:12:42
 *
 */
@Service
@Slf4j
public class SceneManagementServiceImpl extends ServiceImpl<SceneManagementMapper, SceneManagement> implements SceneManagementService {
	/**
	 * 	业务场景表数据库处理类
	 */
	@Autowired
	private SceneManagementMapper sceneManagementMapper;

	@Autowired
	private SceneApplicationRefService sceneApplicationRefService;

	@Override
	public Result<SceneManagement> addSceneManagement(SceneManagement sceneManagement){
		if (StringUtils.isNotBlank(sceneManagement.getSceneId()) && StringUtils.isBlank(sceneManagement.getSceneName())) {
			return Result.fail("场景名称不能为空");
		}
		if (StringUtils.isNotBlank(sceneManagement.getSceneId()) && StringUtils.isBlank(sceneManagement.getSystemPrompt())) {
			return Result.fail("提示词不能为空");
		}

		// 判断场景名称是否重复
		boolean exists = Wrappers.of(mapper)
				.where(SCENE_MANAGEMENT.SCENE_NAME.eq(sceneManagement.getSceneName()))
				.and(SCENE_MANAGEMENT.TENANT_ID.eq(sceneManagement.getTenantId()).when(StringUtils.isNotBlank(sceneManagement.getTenantId())))
				.and(SCENE_MANAGEMENT.SCENE_ID.ne(sceneManagement.getSceneId()).when(StringUtils.isNotBlank(sceneManagement.getSceneId())))
				.exists();

		if (exists) {
			return Result.fail("场景名称已存在");
		}

		if (StringUtils.isBlank(sceneManagement.getSceneId())) {
			sceneManagement.setSceneId(IdUtil.simpleUUID());
		}
		saveOrUpdate(sceneManagement);
		return Result.success(sceneManagement);
	}

	@Override
	public Result<Page<SceneManagement>> getSceneManagementList(SceneManagementPageParam param){
		List<String> sceneByAppId = null;
		if (StringUtils.isNotBlank(param.getApplicationId())) {
			sceneByAppId = sceneApplicationRefService.getSceneIdsByAppId(param.getApplicationId());
		}
		Wrappers<Object> wrappers = Wrappers.init()
				.where(SCENE_MANAGEMENT.SCENE_ID.in(sceneByAppId).when(CollectionUtils.isNotEmpty(sceneByAppId)))
				.orderBy(SCENE_MANAGEMENT.CREATE_TIME.desc());
		PermissionControlUtils.buildPermission(wrappers, BusinessPermissionEnum.SCENE);
		// 条件模糊搜索
		wrappers.and(SCENE_MANAGEMENT.SCENE_NAME.like(param.getSceneName()).when(StringUtils.isNotBlank(param.getSceneName())));
		Page<SceneManagement> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
        List<SceneManagement> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<String> scenceIdList = records.stream().map(SceneManagement::getSceneId).distinct().collect(Collectors.toList());
            Map<String, Long> appRefNumMap = sceneApplicationRefService.getAppRefNum(scenceIdList);
            records.forEach(record -> record.setAppNum(appRefNumMap.getOrDefault(record.getSceneId(), 0L)));
        }
		return Result.success(page);
	}

	@Override
	public Result updateSceneManagement(SceneManagement sceneManagement){
		// updateById(sceneManagement);
		return Result.success();
	}

	@Override
	public Result deleteSceneManagement(List<String> idList){
		if (CollectionUtils.isEmpty(idList)) {
			return Result.success();
		}
		List<String> appNameList = sceneApplicationRefService.getAppIdsBySceneId(idList);
		if (CollectionUtils.isNotEmpty(appNameList)) {
			String names = appNameList.stream().collect(Collectors.joining("、"));
			return Result.fail("场景已关联应用【" + names + "】，不可删除");
		}
		Wrappers<SceneManagement> wrappers = Wrappers.of(mapper)
				.where(SCENE_MANAGEMENT.SCENE_ID.in(idList));

		remove(wrappers);

		return Result.success();
	}

	@Override
	public List<SceneManagement> getSceneManagementList(List<String> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Lists.newArrayList();
		}

        return Wrappers.of(mapper)
                .where(SCENE_MANAGEMENT.SCENE_ID.in(idList))
                .and(SCENE_MANAGEMENT.STATUS.eq(StringConstant.ONE))
                .list();
	}

	@Override
	public Result<SceneManagement> getSceneDetail(StringParam param) {
		if (StringUtils.isBlank(param.getParam())) {
			return Result.success(new SceneManagement());
		}

		SceneManagement one = Wrappers.of(mapper)
				.where(SCENE_MANAGEMENT.SCENE_ID.eq(param.getParam()))
				.limit(1)
				.one();
		return Result.success(one);
	}

}