package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.PresetQuestionPageParam;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wenge.model.entity.PresetQuestion;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 预置问题服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 14:01:21
 *
 */
public interface PresetQuestionService extends IService<PresetQuestion> {

    Result addPresetQuestion(PresetQuestion presetQuestion);

    Result getPresetQuestionList(PresetQuestionPageParam presetQuestion);

    Result updatePresetQuestion(PresetQuestion presetQuestion);

    Result deletePresetQuestion(List<String> idList);

    List<PresetQuestion> getByAppIds(List<String> appIds);

    Result getPresetQuestionByAI(PromptGenerateParam param);
}