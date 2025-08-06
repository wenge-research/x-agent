package com.wenge.model.controller;

import com.wenge.model.dto.param.ManusParam;
import com.wenge.model.service.ManusService;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.redis.annotation.RedisLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manus")
@Slf4j
@Api("manus过程")
public class ManusController {

    @Autowired
    private ManusService manusService;

    /**
     * manus对话
     * @param param
     * @return
     */
    @PostMapping(value = "/chat", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation(value = "manus对话")
    @RedisLimit(period = 10, count = 50)
    @UmsOperationLog(description = "manus对话", logType = 10, belongModule = "appmanage", belongModuleName = "应用对话", objectType = "应用", objectName = "--", objectUuid = "--")
    public Object chat(@RequestBody ManusParam param) {
        return manusService.dialogueByStream(param);
    }

}
