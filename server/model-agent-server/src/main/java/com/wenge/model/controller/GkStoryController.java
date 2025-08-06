package com.wenge.model.controller;

import com.wenge.model.dto.result.GkColumn;
import com.wenge.model.entity.GkStoryData;
import com.wenge.model.service.GkStoryService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 国科大稿件服务
 */
@RestController
@RequestMapping("/gkStory")
@Slf4j
public class GkStoryController {

    @Autowired
    private GkStoryService gkStoryService;

    /**
     *  获取栏目列表
     * @param param
     * @return
     */
    @PostMapping("/getColumnList")
    public Result<List<GkColumn>> getColumnList(@RequestBody EmptyParam param) {
        return gkStoryService.getColumnList(param);
    }

    /**
     *  获取稿件列表
     * @param param
     * @return
     */
    @PostMapping("/getStoryByColumnId")
    public Result<List<GkStoryData>> getStoryByColumnId(@RequestBody StringParam param) {
        return gkStoryService.getStoryByColumnId(param);
    }

    /**
     * 获取稿件详情
     * @param param
     * @return
     */
    @PostMapping("/getStoryById")
    public Result<GkStoryData> getStoryById(@RequestBody StringParam param) {
        return gkStoryService.getStoryById(param);
    }

}
