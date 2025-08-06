package com.wenge.model.strategy.stt;

import com.google.gson.Gson;
import com.wenge.model.config.stt.message.AliyunMessage;
import com.wenge.model.entity.VoiceComponentInfo;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import javax.annotation.Nullable;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.UUID;

@Slf4j
public class AliyunWebSocketListener extends WebSocketListener {
    String appKey; // key
    String messageId = UUID.randomUUID().toString().replace("-", "");
    String taskId = UUID.randomUUID().toString().replace("-", "");

    private Session session;

    private Integer sampleRate = 16000;

    @Getter
    private WebSocket webSocket;

    public AliyunWebSocketListener(Session session, VoiceComponentInfo componentInfo) {
        this.session = session;
        this.appKey = componentInfo.getApiKey();
        Integer audioSampleRate = componentInfo.getAudioSampleRate();
        if (audioSampleRate != null) {
            this.sampleRate = audioSampleRate;
        }
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        // 建立连接的时候发送一次
        AliyunMessage aliyunMessage = new AliyunMessage();
        AliyunMessage.Header header = new AliyunMessage.Header();
        header.setAppkey(appKey);
        header.setName("StartTranscription");
        header.setNamespace("SpeechTranscriber");
        header.setTask_id(taskId);
        header.setMessage_id(messageId);
        AliyunMessage.Payload payload = new AliyunMessage.Payload();
        payload.setFormat("pcm"); // PCM、WAV、OPUS、SPEEX、AMR、MP3、AAC
        payload.setSample_rate(sampleRate); // 音频采样率，默认是16000 Hz
        payload.setEnable_intermediate_result(true); //是否返回中间识别结果
        payload.setEnable_inverse_text_normalization(true); // ITN（逆文本inverse text normalization）中文数字转换阿拉伯数字。设置为True时，中文数字将转为阿拉伯数字输出，默认值：False。
        payload.setEnable_punctuation_prediction(true); // 是否在后处理中添加标点
        aliyunMessage.setPayload(payload);
        aliyunMessage.setHeader(header);
        Gson gson = new Gson();
        String json = gson.toJson(aliyunMessage);
        webSocket.send(json);
        log.info("阿里语音服务发送消息: {}", json);
    }

    @SneakyThrows
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Gson gson = new Gson();
        AliyunMessage aliyunMessage = gson.fromJson(text, AliyunMessage.class);
        // 返回的消息推送给前端
        log.info("阿里语音服务返回消息: {}", text);
        session.getBasicRemote().sendText(text);
    }

    @SneakyThrows
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        log.info("阿里语音服务连接已关闭 WebSocket connection closed, code: {}，reason {}", code, reason);
        // 主动关闭前端websocket
        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, reason));
    }

    @SneakyThrows
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        String s = response == null ? "null" : response.toString();
        log.error("阿里语音服务连接错误 WebSocket connection failed, response: {}, error: ", s, t);
        // 主动关闭前端websocket
        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, s));
    }
}
