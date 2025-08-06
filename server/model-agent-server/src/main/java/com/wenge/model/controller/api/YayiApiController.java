package com.wenge.model.controller.api;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.YayiApiParam;
import com.wenge.model.utils.SseEmitterUtils;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.Generate30BParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/yayi")
public class YayiApiController {

    @Autowired
    private YayiServer yayiServer;

    @PostMapping(value = "/generate", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter dialogueByStream(@RequestBody YayiApiParam param) {
        String clientId = param.getClientId();
        SseEmitter emitter = SseEmitterUtils.getConnection(clientId);
        new Thread(() -> {
            try {
                if (StringUtils.isBlank(param.getQuestion())) {
                    return;
                }
                Generate30BParam generate30BParam = null;
                if (JSONUtil.isTypeJSONObject(param.getQuestion())) {
                    generate30BParam = JSON.parseObject(param.getQuestion(), Generate30BParam.class);
                    YayiMessage remove = generate30BParam.getMessages().remove(0);
                    param.setQuestion(remove.getContent());
                    if (CollectionUtils.isEmpty(generate30BParam.getMessages())) {
                        generate30BParam.setMessages(null);
                    }
                }

                JSONObject reuslt = new JSONObject();
                yayiServer.generate30BStreamStr(param.getQuestion(), generate30BParam, result -> {
                    try {
                        reuslt.put("answer", result);
                        SseEmitterUtils.send(clientId, JSON.toJSONString(reuslt));
                        System.out.println("==>" + result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SseEmitterUtils.complete(clientId);
            }
        }).start();
        return emitter;
    }
}
