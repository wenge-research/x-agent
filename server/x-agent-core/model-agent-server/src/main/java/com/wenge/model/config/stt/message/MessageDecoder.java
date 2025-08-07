package com.wenge.model.config.stt.message;

import com.alibaba.fastjson2.JSON;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<AudioMessage> {

    @Override
    public AudioMessage decode(String jsonStr) throws DecodeException {
        return JSON.parseObject(jsonStr, AudioMessage.class);
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}