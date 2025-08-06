package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.VideoInfoParam;
import com.wenge.model.dto.param.VideoUploadParam;
import com.wenge.model.dto.result.MediaResult;
import com.wenge.model.entity.VideoInfo;
import com.wenge.model.service.VideoInfoService;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 视频信息管理控制器
 * 提供视频信息的增删改查和批量上传功能
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/video")
public class VideoInfoController {

    @Autowired
    private VideoInfoService videoInfoService;

    /**
     * 批量通过视频链接上传并保存视频信息
     * 
     * 处理流程：
     * 1. 下载视频到本地临时文件
     * 2. 上传视频到腾讯云COS
     * 3. 调用腾讯云CI获取视频元信息（时长、格式、大小）
     * 4. 获取视频第一帧作为封面
     * 5. 将视频和封面上传到MinIO
     * 6. 保存视频信息到数据库
     * 
     * @param params 包含视频链接列表的请求参数
     * @return 处理结果，包含每个视频的处理状态和URL信息
     */
    @PostMapping("/batchUploadByUrl")
    public Result batchUploadByUrl(@RequestBody List<VideoUploadParam> params) {
        return videoInfoService.batchUploadByUrl(params);
    }

    /**
     * 查询视频信息列表
     */
    @PostMapping("/getVideoInfoList")
    public Result<Page<VideoInfo>> getVideoInfoList(@RequestBody VideoInfoParam videoInfoParam){
        return videoInfoService.getVideoInfoList(videoInfoParam);
    }

    /**
     * 修改发布和审核状态
     */
    @PostMapping("/updateVideoInfoStatus")
    public Result<VideoInfo> updateVideoInfoStatusByVideoId(@RequestBody VideoInfoParam videoInfoParam){
        return videoInfoService.updateVideoInfoStatusByVideoId(videoInfoParam);
    }

    /**
     * 首页/个人历史展示(包括图片和视频）
     */
    @PostMapping("/display")
    public Result<Map<String, Page<MediaResult>>> display(@RequestBody VideoInfoParam videoInfoParam){
        return videoInfoService.getVideoAndImage(videoInfoParam);
    }
} 