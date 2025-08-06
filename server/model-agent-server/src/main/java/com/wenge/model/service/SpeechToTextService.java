package com.wenge.model.service;

import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.strategy.stt.STTStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SpeechToTextService {
    private final Map<String, STTStrategy> sessions = new ConcurrentHashMap<>();

    @Autowired
    private Map<String, STTStrategy> strategyMap;

    public void onOpen(Session session, VoiceComponentInfo componentInfo) {
        STTStrategy sttStrategy = strategyMap.getOrDefault(componentInfo.getStrategy(), null);
        // 初始化一个STT策略
        sessions.put(session.getId(), sttStrategy);
        // 初始化会话
        sttStrategy.onOpen(session, componentInfo);
    }

    public void processChunk(String sessionId, byte[] audioChunk, boolean isEnd) {
        STTStrategy strategy = sessions.get(sessionId);
        // 调用第三方API
        strategy.processChunk(sessionId, audioChunk, isEnd);
    }

    public void closeSession(String sessionId) {
        STTStrategy strategy = sessions.get(sessionId);
        strategy.closeSession(sessionId);
        sessions.remove(sessionId);  // 清理资源
    }

}