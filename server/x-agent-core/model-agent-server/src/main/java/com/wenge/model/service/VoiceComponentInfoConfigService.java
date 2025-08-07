package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.VoiceComponentInfoConfigPageParam;
import com.wenge.model.dto.param.VoiceComponentInfoConfigUpdateParam;
import com.wenge.model.entity.VoiceComponentInfoConfig;
import com.wg.appframe.core.bean.Result;

public interface VoiceComponentInfoConfigService extends IService<VoiceComponentInfoConfig> {

    Result<Page<VoiceComponentInfoConfig>> getConfigByPage(VoiceComponentInfoConfigPageParam param);

    Result updateConfig(VoiceComponentInfoConfigUpdateParam param);

}