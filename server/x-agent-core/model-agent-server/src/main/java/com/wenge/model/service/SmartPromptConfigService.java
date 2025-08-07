package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.SmartPromptConfig;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 文档提示词配置表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-16 17:26:10
 *
 */
@Deprecated
public interface SmartPromptConfigService extends IService<SmartPromptConfig> {

    Result addSmartPromptConfig(SmartPromptConfig smartPromptConfig);

    Result getSmartPromptConfigList(SmartPromptConfig smartPromptConfig);

    Result updateSmartPromptConfig(SmartPromptConfig smartPromptConfig);

    Result deleteSmartPromptConfig(List<String> idList);

    List<SmartPromptConfig> getPromptConfigByAppId(String appId, String module);

    void updateSmartPromptConfigByAppId(SmartPromptConfig smartPromptConfig);
}