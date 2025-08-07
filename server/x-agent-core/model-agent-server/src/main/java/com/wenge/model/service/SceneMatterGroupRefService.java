package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.GroupMatterRefAddParam;
import com.wenge.model.dto.param.SceneMatterGroupRefListParam;
import com.wenge.model.entity.SceneMatterGroupRef;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:20:59
 *
 */
public interface SceneMatterGroupRefService extends IService<SceneMatterGroupRef> {

    Result addSceneMatterGroupRef(GroupMatterRefAddParam param);

    Result<List<SceneMatterGroupRef>> getSceneMatterGroupRefList(SceneMatterGroupRefListParam param);

    Result updateSceneMatterGroupRef(SceneMatterGroupRef sceneMatterGroupRef);

    Result deleteSceneMatterGroupRef(List<String> idList);

    /**
     * 获取场景下的问题
     * @param groupIdList
     * @return
     */
    List<String> getMatterIdList(List<String> groupIdList);

    /**
     * 通过事项id查询分组
     */
    List<String> getGroupIdListByMatterId(List<String> matterIds);
}