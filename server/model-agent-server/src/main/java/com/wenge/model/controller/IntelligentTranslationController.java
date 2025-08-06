package com.wenge.model.controller;


import com.wenge.model.dto.result.TranslateResult;
import com.wenge.model.entity.IntelligentTranslationRecord;
import com.wenge.model.service.IntelligentTranslationService;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.wos.exception.WosException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;



/**
 * @Author:caohaifeng
 * @createTime:2024-09-23 10:30:12
 * @Description: 智能翻译控制层
 * @Version:1.0
 */
@Api(tags = "智能翻译控制层")
@RestController
@RequestMapping("/intelligentTranslation")
@Slf4j
public class IntelligentTranslationController {

    @Autowired
    private IntelligentTranslationService intelligentTranslationService;

    @ApiOperation(value = "智能翻译-(文本、文件)", tags = "智能翻译")
    @PostMapping("/translateTextOrFile")
    @UmsOperationLog(description = "工具箱-智能翻译", logType = 1, belongModule = "toolsmanage", belongModuleName = "工具箱", objectType = "智能翻译", objectName = "--", objectUuid = "--")
    public Result<TranslateResult> translateTextOrFile(IntelligentTranslationRecord intelligentTranslationRecord) throws WosException {
        return intelligentTranslationService.translateTextOrFile(intelligentTranslationRecord);
    }

    @ApiOperation(value = "智能识别语种)", tags = "智能识别语种")
    @PostMapping("/identifyLanguage")
    @UmsOperationLog(description = "工具箱-智能识别语种", logType = 1, belongModule = "toolsmanage", belongModuleName = "工具箱", objectType = "智能识别语种", objectName = "--", objectUuid = "--")
    public Result identifyLanguage(@RequestBody IntelligentTranslationRecord intelligentTranslationRecord) {
        return intelligentTranslationService.identifyLanguage(intelligentTranslationRecord);
    }

    @ApiOperation(value = "[流式响应]闻歌智能翻译-(文本)", tags = "闻歌智能翻译")
    @PostMapping(value = "/executeTranslateTextWG", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @UmsOperationLog(description = "工具箱-闻歌智能翻译-(文本)", logType = 1, belongModule = "toolsmanage", belongModuleName = "工具箱", objectType = "智能翻译", objectName = "--", objectUuid = "--")
    public SseEmitter executeTranslateTextWG(@RequestBody  IntelligentTranslationRecord record, HttpServletRequest request) {

        return intelligentTranslationService.executeTranslateTextWG(record, request);
    }

}

