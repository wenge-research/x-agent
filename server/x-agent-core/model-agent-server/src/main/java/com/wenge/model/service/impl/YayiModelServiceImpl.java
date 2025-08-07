package com.wenge.model.service.impl;

import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.param.Generate13bParam;
import com.wenge.model.dto.param.SseEmitterParam;
import com.wenge.model.service.YayiModelService;
import com.wenge.model.utils.SseEmitterUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.api.YayiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class YayiModelServiceImpl implements YayiModelService {

    private static final Logger log = LoggerFactory.getLogger(YayiModelServiceImpl.class);
    @Autowired
    private YayiServer yayiServer;

    @Value("${agentX.cluster}")
    private String cluster;

    @Resource(name = "dialogueByStreamPool")
    private ThreadPoolExecutor poolExecutor;

    @Override
    public SseEmitter generate13b(Generate13bParam generate13bParam) {
        SseEmitter sseEmitter = SseEmitterUtils.getConnection(generate13bParam.getClientId());
        CompletableFuture.runAsync(() -> {
            yayiServer.generate13BStreamStr(generate13bParam.getQuestion(), result -> {
                try {
                    //System.out.println("==>>>>>>" + result);
                    boolean finishFlag = SseEmitterUtils.sendMsg(generate13bParam.getClientId(), result);
                    if (finishFlag) {
                        throw new RuntimeException("stop....");
                    }
                } catch (Exception e) {
                    log.error("error:{}", e.getMessage(), e);

                }
            });
            SseEmitterUtils.complete(generate13bParam.getClientId());
        }, poolExecutor);
        return sseEmitter;
    }

    @Override
    public SseEmitter generate30b(Generate13bParam generate13bParam) {
        SseEmitter sseEmitter = SseEmitterUtils.getConnection(generate13bParam.getClientId());
        CompletableFuture.runAsync(() -> {
            yayiServer.generate30BStream(generate13bParam.getQuestion(), result -> {
                try {
                    String content = result.getData().getChoices().get(0).getMessage().getContent();
                    //System.out.println("==>>>>>>" + content);
                    boolean finishFlag = SseEmitterUtils.sendMsg(generate13bParam.getClientId(), content);
                    if (finishFlag) {
                        throw new RuntimeException("stop....");
                    }
                } catch (Exception e) {
                    log.error("error:{}", e.getMessage(), e);

                }
            });
            SseEmitterUtils.complete(generate13bParam.getClientId());
        }, poolExecutor);
        return sseEmitter;
    }

    @Override
    public Result<String> closeDialogueConn(SseEmitterParam sseEmitterParam) {
        SseEmitterUtils.closeConnection(sseEmitterParam.getClientId());

        // 不仅要关闭当前节点，还要关闭其他节点
        if (!StringConstant.ONE.equals(sseEmitterParam.getFromNode())) {
            sseEmitterParam.setFromNode(StringConstant.ONE);
            CacheClearServiceImpl.clearComCache("/yayi/closeSseEmitter", JSONUtil.toJsonStr(sseEmitterParam));
        }
        return Result.success("连接已关闭");
    }

    @Override
    public SseEmitter initSseEmitter(String clientId) {
        SseEmitter connection = SseEmitterUtils.getConnection(clientId);
        return connection;
    }

}
