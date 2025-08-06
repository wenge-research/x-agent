package com.wenge.expand.service.impl;

import com.wenge.expand.dto.param.VideoInfoGetParam;
import com.wenge.expand.dto.result.VideoInfoResult;
import com.wenge.expand.service.VideoService;
import com.wenge.expand.utils.VideoUtil;
import com.wg.appframe.core.bean.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl implements VideoService {

    @Override
    public Result<VideoInfoResult> getVideoInfo(VideoInfoGetParam param) {
        if (StringUtils.isBlank(param.getVideoUrl())) {
            return Result.success(new VideoInfoResult());
        }
        VideoInfoResult videoInfo = null;
        try {
            videoInfo = VideoUtil.getVideoInfo(param.getVideoUrl(), param.getCoverUrl());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.success(videoInfo);
    }
}
