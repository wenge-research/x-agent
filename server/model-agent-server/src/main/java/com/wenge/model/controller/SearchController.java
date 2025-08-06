package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.AiSearchRequest;
import com.wenge.model.dto.param.FileResult;
import com.wenge.model.entity.File;
import com.wenge.model.service.AiSearchService;
import com.wenge.model.service.FileService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/search")
@Api(tags = "Description:AI搜索")
@Slf4j
public class SearchController {

    @Autowired
    private AiSearchService aiSearchService;

    /**
     * 文件服务类
     */
    @Autowired
    private FileService fileService;


    @PostMapping("/searchByWord")
    public Result searchByWord(@RequestBody AiSearchRequest aiSearchRequest) {

        return Result.success(aiSearchService.searchByWord(aiSearchRequest));
    }

    /**
     * 以结构化语句输出内容
     * @param aiSearchRequest
     * @return
     */
    @PostMapping("/byJgh")
    public Result searchByJgh(@RequestBody AiSearchRequest aiSearchRequest) {

        return Result.success(aiSearchService.searchByJgh(aiSearchRequest));
    }

    /**
     * 根据应用id查询知识库列表
     * @param aiSearchRequest
     * @return
     */
    @PostMapping("/queryKnowledgeList")
    public Result queryKnowledgeList(@RequestBody AiSearchRequest aiSearchRequest) {
        return aiSearchService.queryKnowledgeList(aiSearchRequest);
    }

    /**
     * 查询文件列表
     */
    @ApiOperation(value = "查询文件列表", tags = "查询文件列表", notes = "查询文件列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getFileList")
    public Result<Page<File>> getFileList(@RequestBody AiSearchRequest aiSearchRequest) {
        return aiSearchService.getFileList(aiSearchRequest);
    }

    /**
     * 查询热门推荐文档
     */
    @ApiOperation(value = "查询热门推荐文档", tags = "查询热门推荐文档", notes = "查询热门推荐文档", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getHotFileList")
    public Result<Page<FileResult>> getHotFileList(@RequestBody AiSearchRequest aiSearchRequest) {
        return aiSearchService.getHotFileList(aiSearchRequest);
    }

    /**
     * 查询订阅推荐文档
     */
    @ApiOperation(value = "查询订阅推荐文档", tags = "查询订阅推荐文档", notes = "查询订阅推荐文档", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getSubFileList")
    public Result<Page<FileResult>> getSubFileList(@RequestBody AiSearchRequest aiSearchRequest) {
        return aiSearchService.getSubFileList(aiSearchRequest);
    }

    /**
     * 查询我的订阅推荐文档
     */
    @ApiOperation(value = "查询我的订阅推荐文档", tags = "查询我的订阅推荐文档", notes = "查询我的订阅推荐文档", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getMySubFileList")
    public Result<Page<FileResult>> getMySubFileList(@RequestBody AiSearchRequest aiSearchRequest) {
        return aiSearchService.getMySubFileList(aiSearchRequest);
    }


}
