package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.SceneApplicationRef;
import com.wenge.model.entity.SceneManagement;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * Description: 业务场景与应用关联表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:45:38
 *
 */
public interface SceneApplicationRefService extends IService<SceneApplicationRef> {

    Result addSceneApplicationRef(SceneApplicationRef sceneApplicationRef);

    Result<List<SceneApplicationRef>> getSceneApplicationRefList(SceneApplicationRef sceneApplicationRef);

    Result updateSceneApplicationRef(SceneApplicationRef sceneApplicationRef);

    Result deleteSceneApplicationRef(SceneApplicationRef sceneApplicationRef);

    /**
     * 根据应用id获取场景列表
     * @param applicationId
     * @return
     */
    List<SceneManagement> getSceneByAppId(String applicationId);

    /**
     * 根据应用id获取场景id列表
     * @param applicationId
     * @return
     */
    List<String> getSceneIdsByAppId(String applicationId);

    /**
     * 获取场景关联应用数量
     * @param sceneIds
     * @return
     */
    Map<String, Long> getAppRefNum(List<String> sceneIds);

    /**
     * 根据场景id获取关联的应用id
     * @param sceneIds
     * @return
     */
    List<String> getAppIdsBySceneId(List<String> sceneIds);

}