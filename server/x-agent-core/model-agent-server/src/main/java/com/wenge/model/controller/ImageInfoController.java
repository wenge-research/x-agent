package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ImageInfoParam;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wenge.model.entity.ImageInfo;
import com.wenge.model.service.ImageInfoService;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图片信息管理控制器
 * 提供图片信息的增删改查和批量上传功能
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/image")
public class ImageInfoController {

    @Autowired
    private ImageInfoService imageInfoService;

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
//    @PostMapping("/batchUploadByUrl")
//    public Result batchUploadByUrl(@RequestBody List<VideoUploadParam> params) {
//        return videoInfoService.batchUploadByUrl(params);
//    }

    /**
     * 查询图片信息列表
     */
    @PostMapping("/getImageInfoList")
    public Result<Page<ImageInfo>> getImageInfoList(@RequestBody ImageInfoParam imageInfoParam){
        return imageInfoService.getImageInfoList(imageInfoParam);
    }

    /**
     * 修改发布和审核状态
     */
    @PostMapping("/updateImageInfoStatus")
    public Result<ImageInfo> updateImageInfoStatusByImageId(@RequestBody ImageInfoParam imageInfoParam){
        return imageInfoService.updateImageInfoStatusByImageId(imageInfoParam);
    }

    /**
     * 保存图片到个人中心
     */
    @PostMapping("/saveImageInfo")
    public Result<ImageInfo> saveImageInfo(@RequestBody PromptGenerateParam param){
        return imageInfoService.saveImageInfo(param);
    }



} 