package com.wenge.model.controller;

import com.wenge.model.dto.param.AddKnowledgeUrlDataParam;
import com.wenge.model.dto.param.DeleteKnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeUrlDataParam;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wenge.model.service.KnowledgeUrlDataService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 知识库-知识库详细数据
 */
@RestController
@RequestMapping("/knowledgeUrl")
@Slf4j
public class KnowledgeUrlDataController {

    @Autowired
    private KnowledgeUrlDataService knowledgeUrlDataService;

    /**
     * 新增知识库数据
     */
    @PostMapping("/addKnowledgeUrlData")
    public Result addKnowledgeUrlData(@RequestBody AddKnowledgeUrlDataParam param) {
        return knowledgeUrlDataService.addKnowledgeUrlData(param);
    }

    // 链接内新增
    @PostMapping("/addKnowledgeData")
    public Result addKnowledgeData(@RequestBody AddKnowledgeUrlDataParam param) {
        return knowledgeUrlDataService.addKnowledgeData(param);
    }

    // 链接内编辑
    @PostMapping("/updateKnowledgeData")
    public Result updateKnowledgeData(@RequestBody KnowledgeUrlData param) {
        return knowledgeUrlDataService.updateKnowledgeData(param);
    }

    @PostMapping("/getKnowledgeUrlDataDetail")
    public Result getKnowledgeUrlDataDetail(@RequestBody KnowledgeUrlData param) {
        return knowledgeUrlDataService.getKnowledgeUrlDataDetail(param);
    }

    @PostMapping("/getKnowledgeUrlDataListByParentId")
    public Result getKnowledgeUrlDataListByParentId(@RequestBody KnowledgeUrlData param) {
        try {
            return knowledgeUrlDataService.getKnowledgeUrlDataListByParentId(param);
        } catch (IOException e) {
            return Result.fail();
        }
    }

    /**
     * 删除知识库数据
     */
    @PostMapping("/deleteKnowledgeUrlData")
    public Result deleteKnowledgeUrlData(@RequestBody DeleteKnowledgeDataParam param) {
        return knowledgeUrlDataService.deleteKnowledgeUrlData(param);
    }

    @PostMapping("/getUrlDataList")
    public Result getUrlDataList(@RequestBody KnowledgeUrlDataParam param) {
        return knowledgeUrlDataService.getUrlDataList(param);
    }
}
