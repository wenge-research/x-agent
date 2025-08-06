package com.wenge.expand.controller;

import com.wenge.expand.dto.param.VideoInfoGetParam;
import com.wenge.expand.dto.result.VideoInfoResult;
import com.wenge.expand.service.VideoService;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 获取视频信息
     * @param param
     * @return
     */
    @PostMapping("/getVideoInfo")
    public Result<VideoInfoResult> getVideoInfo(@RequestBody VideoInfoGetParam param) {
        return videoService.getVideoInfo(param);
    }
}
