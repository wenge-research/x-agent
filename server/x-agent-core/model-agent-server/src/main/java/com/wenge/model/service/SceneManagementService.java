package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.SceneManagementPageParam;
import com.wenge.model.entity.SceneManagement;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Description: 业务场景表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 10:12:42
 *
 */
public interface SceneManagementService extends IService<SceneManagement> {

    Result<SceneManagement> addSceneManagement(SceneManagement sceneManagement);

    Result<Page<SceneManagement>> getSceneManagementList(SceneManagementPageParam param);

    Result updateSceneManagement(SceneManagement sceneManagement);

    Result deleteSceneManagement(List<String> idList);

    /**
     * 根据场景id查询
      * @param idList
     * @return
     */
    List<SceneManagement> getSceneManagementList(List<String> idList);


    Result<SceneManagement> getSceneDetail(@RequestBody StringParam param);
}