package com.wenge.model.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.result.DecisionStatusResult;
import com.wenge.model.utils.SseEmitterUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DecisionOneWBListen extends WebSocketListener {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    public static ConcurrentHashMap<String, DecisionOneWBListen> WEB_SOCKET_MAP = new ConcurrentHashMap<>();

    /**
     * 会话 id
     */
    private final String sessionId;

    /**
     * 客户端 id
     */
    @Setter
    private String clientId;

    public DecisionOneWBListen(String sessionId) {
        this.sessionId = sessionId;
    }

    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        WEB_SOCKET_MAP.put(sessionId, this);
        log.info("会话 {} 创建了", sessionId);
    }


    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        try {
            System.out.println("DecisionOneWBListen.onMessage:" + text);
            JSONObject entries = JSONUtil.parseObj(text);
            // 处理智能体交互信息
            switch (entries.getStr("type")) {
                // 最新的交互动作信息
                case "interaction":
                    dealInteraction(entries);
                    break;
                // 推演状态信息
                case "status":
                    dealStatus(entries);
                    break;
                // 结束推演
                case "end_of_round":
                    endOfRound(entries);
                    break;
                // 最新的交互发言
                case "action":
                    dealAction(entries);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        WEB_SOCKET_MAP.remove(sessionId);
        // 处理状态更新
        SseEmitterUtils.completeDelay(clientId);
        log.info("onClosing会话 {} 关闭了", sessionId);
    }

    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        WEB_SOCKET_MAP.remove(sessionId);
        SseEmitterUtils.completeDelay(clientId);
        log.info("onClosed会话 {} 关闭了", sessionId);
    }

    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        log.error("会话 {} 连接失败", sessionId);
        JSONObject msg = new JSONObject();
        msg.set("msg", "会话连接失败：" + sessionId);
        msg.set("type", "status");
        try {
            // 处理状态更新
            SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(msg));
        } catch (Exception e) {
            log.error("dealStatus会话 {} 推送状态失败", sessionId);
        } finally {
            SseEmitterUtils.completeDelay(clientId);
        }
    }

    /**
     * 推送状态
     * @param entries
     */
    private void dealStatus(JSONObject entries) {
        DecisionStatusResult data = entries.getJSONObject("data").toBean(DecisionStatusResult.class);
        JSONObject msg = new JSONObject();
        msg.set("detail", data);
        msg.set("type", "status");
        try {
            // 处理状态更新
            SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(msg));
        } catch (Exception e) {
            log.error("dealStatus会话 {} 推送状态失败", sessionId);
        }
    }

    /**
     * 智能体发言
     * @param entries
     */
    private void endOfRound(JSONObject entries) {
        JSONObject msg = new JSONObject();
        msg.set("detail", entries);
        msg.set("type", "endOfRound");
        try {
            // 处理状态更新
            SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(msg));
            // SseEmitterUtils.completeDelay(clientId);
        } catch (Exception e) {
            log.error("endOfRound会话 {} 推送状态失败", sessionId);
        }
    }


    /**
     * 推送最新交互内容
     * @param entries
     */
    private void dealAction(JSONObject entries) {
        JSONObject data = entries.getJSONObject("data");
        JSONObject msg = new JSONObject();
        msg.set("detail", data);
        msg.set("type", "action");
        try {
            // 处理状态更新
            SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(msg));
        } catch (Exception e) {
            log.error("dealAction会话 {} 推送状态失败", sessionId);
        }
    }

    /**
     * 处理智能体交互信息
     * @param entries
     */
    private void dealInteraction(JSONObject entries) {
        JSONObject data = entries.getJSONObject("data");
        JSONObject msg = new JSONObject();
        msg.set("detail", data);
        msg.set("type", "interaction");
        try {
            // 处理状态更新
            SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(msg));
        } catch (Exception e) {
            log.error("dealInteraction会话 {} 推送状态失败", sessionId);
        }
    }


}
