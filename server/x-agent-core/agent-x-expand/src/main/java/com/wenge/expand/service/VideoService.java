package com.wenge.expand.service;

import com.wenge.expand.dto.param.VideoInfoGetParam;
import com.wenge.expand.dto.result.VideoInfoResult;
import com.wg.appframe.core.bean.Result;

public interface VideoService {

    Result<VideoInfoResult> getVideoInfo(VideoInfoGetParam param);
}
