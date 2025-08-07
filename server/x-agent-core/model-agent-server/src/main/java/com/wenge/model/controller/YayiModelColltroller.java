package com.wenge.model.controller;

import com.wenge.model.dto.param.Generate13bParam;
import com.wenge.model.dto.param.SseEmitterParam;
import com.wenge.model.service.YayiModelService;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 雅意大模型接口
 */
@RestController
@RequestMapping("/yayi")
public class YayiModelColltroller {

    @Autowired
    private YayiModelService yayiModelService;

    /**
     * 13b接口
     *
     * @param generate13bParam
     * @return
     */
    @PostMapping("/generate13b")
    public SseEmitter generate13b(@RequestBody Generate13bParam generate13bParam) {
        return yayiModelService.generate13b(generate13bParam);
    }

    /**
     * 30b接口
     *
     * @param generate13bParam
     * @return
     */
    @PostMapping("/generate30b")
    public SseEmitter generate30b(@RequestBody Generate13bParam generate13bParam) {
        return yayiModelService.generate30b(generate13bParam);
    }

    /**
     * 关闭对话
     *
     * @param sseEmitterParam
     * @return
     */
    @PostMapping("/closeSseEmitter")
    public Result<String> closeDialogueConn(@RequestBody SseEmitterParam sseEmitterParam) {
        return yayiModelService.closeDialogueConn(sseEmitterParam);
    }

    /**
     * 初始化一个sseEmitter
     *
     * @param clientId
     * @return
     */
    @GetMapping("/initSseEmitter")
    public SseEmitter initSseEmitter(String clientId) {
        return yayiModelService.initSseEmitter(clientId);
    }

}
