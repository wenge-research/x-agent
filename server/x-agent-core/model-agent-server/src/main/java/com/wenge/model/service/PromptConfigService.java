package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.PromptConfigListParam;
import com.wenge.model.entity.PromptConfig;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 提示词配置服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-12-18 11:02:37
 *
 */
public interface PromptConfigService extends IService<PromptConfig> {

    Result addPromptConfig(PromptConfig promptConfig);

    Result<Page<PromptConfig>> getPromptConfigList(PromptConfigListParam promptConfig);

    Result deletePromptConfig(List<String> idList);

    List<PromptConfig> getBuiltInPrompt(PromptConfigListParam promptConfigListParam);

    Result setPreset(PromptConfigListParam promptConfigListParam);
}