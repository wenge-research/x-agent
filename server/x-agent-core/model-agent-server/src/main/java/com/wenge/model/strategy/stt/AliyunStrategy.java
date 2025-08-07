package com.wenge.model.strategy.stt;

import com.wenge.model.constants.STTConstants;
import com.wenge.model.entity.VoiceComponentInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service(STTConstants.ALIYUN)
@Slf4j
public class AliyunStrategy implements STTStrategy {

    /**
     * 第三方ws链接
     */
    private final Map<String, AliyunWebSocketListener> listenerHashMap = new HashMap<>();


    @Override
    public void onOpen(Session session, VoiceComponentInfo componentInfo) {
        String url = componentInfo.getVoiceLink();
        String token = componentInfo.getApiSecret();
        url += "?token=" + token;
        final Request request = new Request.Builder().url(url).build();
        AliyunWebSocketListener listener = new AliyunWebSocketListener(session, componentInfo);
        listenerHashMap.put(session.getId(), listener);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .pingInterval(50, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        okHttpClient.newWebSocket(request, listener);
        log.info("与阿里语音服务建立连接");
    }

    @Override
    public void processChunk(String sessionId, byte[] audioChunk, boolean isEnd) {
        AliyunWebSocketListener listener = listenerHashMap.getOrDefault(sessionId, null);
        if (listener != null) {
            WebSocket webSocket = listener.getWebSocket();
            if (null != webSocket) {
                webSocket.send(ByteString.of(audioChunk));
            }
        }
    }

    @Override
    public void closeSession(String sessionId) {
        AliyunWebSocketListener listener = listenerHashMap.getOrDefault(sessionId, null);
        WebSocket webSocket = listener.getWebSocket();
        // 前端主动断开连接
        if (webSocket != null) {
            webSocket.close(1000, "客户端断开连接");
        }
    }
}
