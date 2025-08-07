package com.wenge.model.config.stt.message;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.socket.TextMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<TextMessage> {

    @Override
    public String encode(TextMessage message) throws EncodeException {
        return JSON.toJSONString(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}