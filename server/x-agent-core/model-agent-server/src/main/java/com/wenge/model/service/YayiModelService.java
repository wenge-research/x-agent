package com.wenge.model.service;

import com.wenge.model.dto.param.Generate13bParam;
import com.wenge.model.dto.param.SseEmitterParam;
import com.wg.appframe.core.bean.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface YayiModelService {

    SseEmitter generate13b(Generate13bParam generate13bParam);
    SseEmitter generate30b(Generate13bParam generate13bParam);

    Result<String> closeDialogueConn(SseEmitterParam sseEmitterParam);

    SseEmitter initSseEmitter(String clientId);
}
