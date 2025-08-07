package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.strategy.aiAudio.AiAudioStrategy;
import com.wenge.model.strategy.aiImage.AiImageStrategy;
import com.wenge.model.strategy.aiVideo.AiVideoStrategy;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.ListStringParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 大模型信息表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-14 14:53:29
 *
 */
public interface LlmInfoService extends IService<LlmInfo> {


    Result getLlmInfoList(LlmInfoListParam param);

    /**
     * @description: 分页查询
     * @author: caohaifeng
     * @date: 2024/8/14 14:55
     **/
    Result getLlmInfoPageList(LlmInfoPageParam llmInfoPageParam);


    /**
     * @description: 添加模型
     * @author: caohaifeng
     * @date: 2024/8/14 14:55
     **/
    Result addLlmInfo(LlmInfoAddUpdateParam llmInfo);

    /**
     * @description: 编辑模型
     * @author: caohaifeng
     * @date: 2024/8/14 14:55
     **/
    Result editLlmInfo(LlmInfoAddUpdateParam llmInfo);


    /**
     * @description: 删除模型
     * @author: caohaifeng
     * @date: 2024/8/14 15:55
     **/
    Result delLlmInfoById(Long id);

    /**
     * 获取激活的模型
     * @param modelId
     * @return
     */
    LlmStrategy getActiveLLm(String modelId);

    Result<List<String>> getLlmManufacturer(EmptyParam param);

    Result<Page<LlmInfo>> getLlmPageList(LlmPageListParam param);

    Result editLlm(LlmInfo llmInfo);

    Result deleteLlm(ListStringParam idList);

    /**
     * 通过模型名称获取模型信息
     * @param modelName
     * @return
     */
    LlmInfo getByModelName(String modelName);

    /**
     * 根据模型id查询模型信息
     * @param modelId
     * @return
     */
    LlmInfo selectModel(String modelId);


    LlmInfo getByModelId(String modelId);

    LlmInfo getByDefaultImageModelCode(String modelCode);

    LlmInfo getByDefaultVideoModelCode(String modelCode);

    Result deployLlm(LlmDeployParam llmDeployParam, HttpServletResponse response);

    Result testConnectLinux(LlmDeployParam llmDeployParam);

    AiImageStrategy getAiImageStrategy(String modelId);
    AiVideoStrategy getAiVedioStrategy(String modelId);
    AiAudioStrategy getAiAudioStrategy(String modelId);

    Result setPreset(LlmInfoPageParam llmInfoPageParam);
}