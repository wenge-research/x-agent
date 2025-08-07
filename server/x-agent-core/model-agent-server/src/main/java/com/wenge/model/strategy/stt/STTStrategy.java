package com.wenge.model.strategy.stt;

import com.wenge.model.entity.VoiceComponentInfo;

import javax.websocket.Session;

public interface STTStrategy {
    /**
     * 和第三方接口建立ws链接
     *
     * @param session
     * @param componentInfo
     */
    void onOpen(Session session, VoiceComponentInfo componentInfo);

    /**
     * 处理分片音频数据
     * @param sessionId
     * @param audioChunk
     * @param isEnd
     * @return
     */
    void processChunk(String sessionId, byte[] audioChunk, boolean isEnd);

    /**
     * 关闭第三方ws链接
     */
    void closeSession(String sessionId);

}
