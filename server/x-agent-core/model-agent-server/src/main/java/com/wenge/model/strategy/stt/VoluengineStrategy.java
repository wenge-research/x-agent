package com.wenge.model.strategy.stt;

import com.wenge.model.constants.STTConstants;
import com.wenge.model.entity.VoiceComponentInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.websocket.Session;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service(STTConstants.VOLCENGINE)
@Slf4j
public class VoluengineStrategy extends WebSocketListener implements STTStrategy {

    /**
     * 第三方ws链接
     */
    private final Map<String, VolcengineWebSocketListener> listenerHashMap = new HashMap<>();

    @Override
    public void onOpen(Session session, VoiceComponentInfo componentInfo) {
        CompletableFuture.runAsync(() -> {
            final String url = componentInfo.getVoiceLink();
            final String appId = componentInfo.getAppId();
            final String token = componentInfo.getApiSecret();
            final Request request = new Request.Builder()
                    .url(url)
                    .header("X-Api-App-Key", appId)
                    .header("X-Api-Access-Key", token)
                    .header("X-Api-Resource-Id", "volc.bigasr.sauc.duration")
                    .header("X-Api-Connect-Id", UUID.randomUUID().toString())
                    .build();
            // 创建信任所有证书的 TrustManager
            X509TrustManager[] trustAllCerts = new X509TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                    }
            };

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());


                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .pingInterval(50, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS)
                        .writeTimeout(100, TimeUnit.SECONDS)
                        .sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts[0])
                        .build();
                VolcengineWebSocketListener listener = new VolcengineWebSocketListener(session, componentInfo);
                listenerHashMap.put(session.getId(), listener);
                okHttpClient.newWebSocket(request, listener);
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void processChunk(String sessionId, byte[] audioChunk, boolean isEnd) {
        VolcengineWebSocketListener listener = listenerHashMap.getOrDefault(sessionId, null);
        // 向第三方发送音频二进制数据
        if (listener != null) {
            listener.sendAudioOnlyRequest(audioChunk, audioChunk.length, isEnd);
        }
    }

    @Override
    public void closeSession(String sessionId) {
        VolcengineWebSocketListener listener = listenerHashMap.getOrDefault(sessionId, null);
        WebSocket webSocket = listener.getWebSocket();
        // 前端主动断开连接
        if (webSocket != null) {
            webSocket.close(1000, "客户端断开连接");
        }
    }


}
