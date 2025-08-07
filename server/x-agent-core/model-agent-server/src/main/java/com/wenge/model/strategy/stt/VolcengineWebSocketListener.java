package com.wenge.model.strategy.stt;

import com.alibaba.fastjson.JSONPath;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wenge.model.entity.VoiceComponentInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class VolcengineWebSocketListener extends WebSocketListener {

    private static final byte PROTOCOL_VERSION = 0b0001;
    private static final byte DEFAULT_HEADER_SIZE = 0b0001;
    // Message Type:
    private static final byte FULL_CLIENT_REQUEST = 0b0001;
    private static final byte AUDIO_ONLY_REQUEST = 0b0010;
    private static final byte FULL_SERVER_RESPONSE = 0b1001;
    private static final byte SERVER_ACK = 0b1011;
    private static final byte SERVER_ERROR_RESPONSE = 0b1111;
    // Message Type Specific Flags
    private static final byte NO_SEQUENCE = 0b0000;// no check sequence
    private static final byte POS_SEQUENCE = 0b0001;
    private static final byte NEG_SEQUENCE = 0b0010;
    private static final byte NEG_WITH_SEQUENCE = 0b0011;
    private static final byte NEG_SEQUENCE_1 = 0b0011;
    // Message Serialization
    private static final byte NO_SERIALIZATION = 0b0000;
    private static final byte JSON = 0b0001;

    // Message Compression
    private static final byte NO_COMPRESSION = 0b0000;
    private static final byte GZIP = 0b0001;


    @Getter
    private WebSocket webSocket;

    private Session session;
    private int seq;
    private int rate = 16000;
    private int bits = 16;
    private int channel = 1;

    public VolcengineWebSocketListener(Session session, VoiceComponentInfo voiceComponentInfo) {
        this.session = session;
        Integer audioSampleRate = voiceComponentInfo.getAudioSampleRate();
        if (audioSampleRate != null) {
            rate = voiceComponentInfo.getAudioSampleRate();
        }
    }

    @SuppressWarnings("[ByDesign12.1]UsingRuntimeExec")
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        String logId = response.header("X-Tt-Logid");
        log.info("WebSocket connection opened, logId: {}", logId);

        // send full client request
        // step 1: append payload json string
        JsonObject user = new JsonObject();
        user.addProperty("uid", "test");

        JsonObject audio = new JsonObject();
        audio.addProperty("format", "pcm"); //
        audio.addProperty("sample_rate", rate);
        audio.addProperty("bits", bits);
        audio.addProperty("channel", channel);
        audio.addProperty("codec", "raw");

        JsonObject request = new JsonObject();
        request.addProperty("model_name", "bigmodel");
        request.addProperty("enable_punc", true);

        JsonObject payload = new JsonObject();
        payload.add("user", user);
        payload.add("audio", audio);
        payload.add("request", request);

        String payloadStr = payload.toString();
        log.info("Sending full client request, payload: {}", payloadStr);
        // step2: 压缩 payload 字段。
        final byte[] payloadBytes = gzipCompress(payloadStr.getBytes());
        // step3:组装 fullClientRequest；fullClientRequest= header+ sequence + payload
        byte[] header = getHeader(FULL_CLIENT_REQUEST, POS_SEQUENCE, JSON, GZIP, (byte) 0);
        final byte[] payloadSize = intToBytes(payloadBytes.length);
        seq = 1;
        byte[] seqBytes = generateBeforPayload(seq);
        final byte[] fullClientRequest = new byte[header.length + seqBytes.length + payloadSize.length
                + payloadBytes.length];
        int destPos = 0;
        System.arraycopy(header, 0, fullClientRequest, destPos, header.length);
        destPos += header.length;
        System.arraycopy(seqBytes, 0, fullClientRequest, destPos, seqBytes.length);
        destPos += seqBytes.length;
        System.arraycopy(payloadSize, 0, fullClientRequest, destPos, payloadSize.length);
        destPos += payloadSize.length;
        System.arraycopy(payloadBytes, 0, fullClientRequest, destPos, payloadBytes.length);
        boolean suc = webSocket.send(ByteString.of(fullClientRequest));
        log.debug("Full client request sent successfully: {}", suc);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        byte[] res = bytes.toByteArray();
        int sequence = parserResponse(res);
        boolean is_last_package = sequence < 0;
        if (is_last_package) {
            log.info("Recognition finished, closing WebSocket connection");
            webSocket.close(1000, "finished");
        }
    }

    boolean sendAudioOnlyRequest(byte[] buffer, int len, boolean isLast) {
        seq++;
        log.info("Sending audio only request, sequence: {}, isLast: {}", seq, isLast);
        if (isLast) {
            seq = -seq;
        }
        byte messageTypeSpecificFlags = isLast ? NEG_WITH_SEQUENCE : POS_SEQUENCE;
        // header
        byte[] header = getHeader(AUDIO_ONLY_REQUEST, messageTypeSpecificFlags, JSON, GZIP, (byte) 0);
        // sequence
        byte[] sequenceBytes = generateBeforPayload(seq);
        // payload size
        byte[] payloadBytes = gzipCompress(buffer, len);
        // payload
        byte[] payloadSize = intToBytes(payloadBytes.length);
        byte[] audio_only_request = new byte[header.length + sequenceBytes.length + payloadSize.length
                + payloadBytes.length];
        int destPos = 0;
        System.arraycopy(header, 0, audio_only_request, destPos, header.length);
        destPos += header.length;
        System.arraycopy(sequenceBytes, 0, audio_only_request, destPos, sequenceBytes.length);
        destPos += sequenceBytes.length;
        System.arraycopy(payloadSize, 0, audio_only_request, destPos, payloadSize.length);
        destPos += payloadSize.length;
        System.arraycopy(payloadBytes, 0, audio_only_request, destPos, payloadBytes.length);
        boolean result = webSocket.send(ByteString.of(audio_only_request));
        log.info("Audio only request sent successfully: {}", result);
        return result;
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        log.info("WebSocket connection closed, code: {}, reason: {}", code, reason);
        webSocket.close(1000, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        log.error("WebSocket connection failed, response: {}, error: ", 
            (response == null ? "null" : response.toString()), t);
        webSocket.close(1000, t.getMessage());
    }


    static byte[] getHeader(byte messageType, byte messageTypeSpecificFlags, byte serialMethod, byte compressionType,
                            byte reservedData) {
        final byte[] header = new byte[4];
        header[0] = (PROTOCOL_VERSION << 4) | DEFAULT_HEADER_SIZE; // Protocol version|header size
        header[1] = (byte) ((messageType << 4) | messageTypeSpecificFlags); // message type | messageTypeSpecificFlags
        header[2] = (byte) ((serialMethod << 4) | compressionType);
        header[3] = reservedData;
        return header;
    }

    static byte[] intToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)

        };
    }

    static int bytesToInt(byte[] src) {
        if (src == null || (src.length != 4)) {
            throw new IllegalArgumentException("");
        }
        return ((src[0] & 0xFF) << 24)
                | ((src[1] & 0xff) << 16)
                | ((src[2] & 0xff) << 8)
                | ((src[3] & 0xff));
    }

    static byte[] generateBeforPayload(int seq) {
        return intToBytes(seq);
    }

    static byte[] gzipCompress(byte[] src) {
        return gzipCompress(src, src.length);
    }

    static byte[] gzipCompress(byte[] src, int len) {
        if (src == null || len == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(src, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }

    static byte[] gzipDecompress(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream ins = new ByteArrayInputStream(src);
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(ins);
            byte[] buffer = new byte[ins.available()];
            int len = 0;
            while ((len = gzip.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return out.toByteArray();
    }


    int parserResponse(byte[] res) {
        if (res == null || res.length == 0) {
            log.warn("Empty response received");
            return -1;
        }
        // 当符号位为1时进行 >> 运算后高位补1（预期是补0），导致结果错误，所以增加个数再与其& 运算，目的是确保高位是补0.
        final byte num = 0b00001111;
        Map<String, Object> result = new HashMap<>();
        // header 32 bit=4 byte
        int protocol_version = (res[0] >> 4) & num;
        result.put("protocol_version", protocol_version);
        int header_size = res[0] & 0x0f;
        result.put("header_size", header_size);

        int message_type = (res[1] >> 4) & num;
        result.put("message_type", message_type);
        int message_type_specific_flags = res[1] & 0x0f;
        result.put("message_type_specific_flags", message_type_specific_flags);
        int serialization_method = res[2] >> num;
        result.put("serialization_method", serialization_method);
        int message_compression = res[2] & 0x0f;
        result.put("message_compression", message_compression);
        int reserved = res[3];
        result.put("reserved", reserved);

        // sequence 4 byte
        byte[] temp = new byte[4];
        System.arraycopy(res, 4, temp, 0, temp.length);
        int sequence = bytesToInt(temp);// sequence 4 byte

        // payload size 4 byte
        String payloadStr = null;
        System.arraycopy(res, 8, temp, 0, temp.length);
        int payloadSize = bytesToInt(temp);
        byte[] payload = new byte[res.length - 12];
        System.arraycopy(res, 12, payload, 0, payload.length);
        // 正常Response
        if (message_type == FULL_SERVER_RESPONSE) {
            if (message_compression == GZIP) {
                payloadStr = new String(gzipDecompress(payload));
            } else {
                payloadStr = new String(payload);
            }
            log.info("Received full server response, payload: {}", payloadStr);
            result.put("payload_size", payloadSize);
            log.info("Full server response: {}", new Gson().toJson(result));

        } else if (message_type == SERVER_ACK) {
            payloadStr = new String(payload);
            log.info("Received server ACK, payload: {}", payloadStr);
            result.put("payload_size", payloadSize);
            log.info("Server ACK response: {}", new Gson().toJson(result));

        } else if (message_type == SERVER_ERROR_RESPONSE) {
            payloadStr = new String(payload);
            result.put("code", sequence);
            result.put("error msg", payloadStr);
            log.error("Server error response: {}", new Gson().toJson(result));
        }

        try {
            String text = JSONPath.read(payloadStr, "$result.text", String.class);
            log.info("Sent text to client: {}", text);
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            log.error("Failed to send text to client", e);
        }
        return sequence;
    }


}
