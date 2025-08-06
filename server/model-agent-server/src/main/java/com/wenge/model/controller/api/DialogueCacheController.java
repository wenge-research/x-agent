package com.wenge.model.controller.api;


import com.wenge.model.dto.param.DialogueCacheAddParam;
import com.wenge.model.entity.DialogueCache;
import com.wenge.model.service.DialogueCacheService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 获取对话缓存
 */
@RestController
@RequestMapping("/DialogueCache")
@Slf4j
@Api(tags = "获取对话缓存")
public class DialogueCacheController {

    @Autowired
    private DialogueCacheService dialogueCacheService;

    @PostMapping("/addDialogueCache")
    @ApiOperation(value = "新增对话缓存", tags = "新增对话缓存", notes = "新增对话缓存", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result<String> addDialogueCache(@RequestBody DialogueCacheAddParam param) {
        return dialogueCacheService.addDialogueCache(param);
    }

    @GetMapping("/getDialogueCache")
    @ApiOperation(value = "获取对话缓存", tags = "获取对话缓存", notes = "获取对话缓存", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
    public Result<List<DialogueCache>> getDialogueCache(@RequestParam(value = "key") String param) {
        return dialogueCacheService.getDialogueCache(param);
    }

}
