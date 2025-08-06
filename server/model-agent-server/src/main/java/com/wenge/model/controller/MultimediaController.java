package com.wenge.model.controller;

import com.wenge.model.dto.param.FileAddParam;
import com.wenge.model.dto.param.ImageAnalysisParam;
import com.wenge.model.entity.File;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.service.MultimediaService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "多媒体接口")
@RestController
@RequestMapping("/multimedia")
@Slf4j
public class MultimediaController {

    @Autowired
    private Map<String, MultimediaService> multimediaServiceMap;


    /* 图片 */
    @ApiOperation(value = "新增图片文件",tags = "新增图片文件", notes = "新增图片文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addImage")
    @OperaLogs
    public Result addImage(FileAddParam file) {
        MultimediaService multimediaService = multimediaServiceMap.get(MultimediaEnum.IMAGE.getName());
        return multimediaService.addFile(file);
    }

    /**
     * 查询图片详情
     */
    @ApiOperation(value = "查询文件详情",tags = "查询文件详情", notes = "查询文件详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getImage/{fileId}")
    public Result<File> getFile(@PathVariable String fileId) {
        MultimediaService multimediaService = multimediaServiceMap.get(MultimediaEnum.IMAGE.getName());
        return multimediaService.getByFileId(fileId);
    }




    /* 音频 */
    @ApiOperation(value = "新增音频文件",tags = "新增音频文件", notes = "新增音频文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addAudio")
    @OperaLogs
    public Result addAudio(FileAddParam file) {
        MultimediaService multimediaService = multimediaServiceMap.get(MultimediaEnum.AUDIO.getName());
        return multimediaService.addFile(file);
    }

    /* 视频 */
    @ApiOperation(value = "新增视频文件（仅限mp4格式）", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addVideo")
    @OperaLogs
    public Result addVideo(FileAddParam file) {
        MultimediaService multimediaService = multimediaServiceMap.get(MultimediaEnum.VIDEO.getName());
        return multimediaService.addFile(file);
    }

    /**
     * 图片解析
     */
    @ApiOperation(value = "图片解析", tags = "图片解析", notes = "图片解析", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/analysisImage")
    @OperaLogs
    public Result analysisImage(@RequestBody ImageAnalysisParam param) {
        MultimediaService multimediaService = multimediaServiceMap.get(MultimediaEnum.IMAGE.getName());
        return multimediaService.analysisImage(param);
    }

}
