package com.wenge.model.config.stt;

import com.wenge.model.config.stt.message.AudioMessage;
import com.wenge.model.config.stt.message.MessageDecoder;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.service.SpeechToTextService;
import com.wenge.model.service.VoiceComponentInfoService;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;

@Component
@ServerEndpoint(
        value = "/stt",
//        encoders = {MessageEncoder.class},  // 自定义消息编码器
        decoders = {MessageDecoder.class} // 自定义消息解码器
)
@Slf4j
public class STTWebSocketEndpoint {
    private SpeechToTextService sttService;

    private String sessionId;

    /**
     * 建立ws链接的时候，需要初始化策略，保证后续前端推送音频数据时，可以直接识别
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.sessionId = session.getId();
        this.sttService = CoreContextProvider.getContext().getBean(SpeechToTextService.class);
        VoiceComponentInfoService voiceComponentInfoService = CoreContextProvider.getContext().getBean(VoiceComponentInfoService.class);
        // 获取参数
        String queryString = session.getQueryString();
        String[] split = StringUtils.split(queryString, "&");
        Map<String, String> parameters = new HashMap<>();
        for (String pair : split) {
            String[] keyValue = StringUtils.split(pair, "=", 2);
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                parameters.put(keyValue[0], "");
            }
        }
        String ttsId = parameters.get("ttsId");
        String applicationId = parameters.get("applicationId");
        // 初始化语音识别会话，分发不同的厂商策略
        Wrappers wrappers = null;
        if (StringUtils.isNotBlank(ttsId)) {
            wrappers = Wrappers.init()
                    .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                    .where(VOICE_COMPONENT_INFO.ID.eq(ttsId));
        } else if (StringUtils.isNotBlank(applicationId)) {
            wrappers = Wrappers.init()
                    .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                    .from(APPLICATION_INFO)
                    .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.TTS_ID.eq(VOICE_COMPONENT_INFO.ID))
                    .where(APPLICATION_INFO.APPLICATION_ID.eq(applicationId))
                    .limit(1);
        }
        if (null == wrappers) {
            return;
        }
        VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);
        if (null == voiceComponentInfo) {
            log.info("没有找到语音配置");
            return;
        }

        sttService.onOpen(session, voiceComponentInfo);
    }

    // 初始化缓冲区：分块读取（每次4KB），动态扩展主缓冲区
    byte[] chunkBuffer = new byte[4 * 1024]; // 临时存储每次读取的块
    byte[] mainBuffer = new byte[0];         // 主缓冲区，初始为空
    int totalBytesRead = 0;                  // 记录已读取的总字节数

    //    @OnMessage   // todo 火山语音识别，未测通
    public void onMessage(Session session, AudioMessage audioMessage) {
        AudioMessage.Data data = audioMessage.getData();
        String audio = data.getAudio();
        if (StringUtils.isBlank(audio)) {
            return;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(audio);
        log.info("接收到音频分片，长度：{}", decode.length);

        chunkBuffer = decode;
        // 1. 计算新缓冲区大小并创建新数组
        int newTotalLength = totalBytesRead + decode.length;
        byte[] newMainBuffer = new byte[newTotalLength];

        // 2. 将旧数据复制到新缓冲区
        System.arraycopy(mainBuffer, 0, newMainBuffer, 0, totalBytesRead);

        // 3. 追加新读取的块数据
        System.arraycopy(chunkBuffer, 0, newMainBuffer, totalBytesRead, decode.length);

        // 4. 更新主缓冲区和总长度
        mainBuffer = newMainBuffer;
        totalBytesRead = newTotalLength;

        sttService.processChunk(sessionId, decode, data.getStatus() == 2);

    }

    /**
     * 接收前端发送的二进制数据
     * @param session
     * @param buffer
     */
    @OnMessage
    public void onMessage(Session session, ByteBuffer buffer) {
        sttService.processChunk(sessionId, buffer.array(), false);
    }

    @OnClose
    public void onClose() {
        log.info("连接关闭");
        sttService.closeSession(sessionId); // 释放资源
        // 转录音频文件，调试代码
//        saveAsWav(mainBuffer, "C:\\Users\\54984\\Desktop\\output\\output.wav", 16000, 1, 16);
    }

//    public static void main(String[] args) throws IOException {
//        // 读取文件全部字节
//        byte[] fileBytes = Files.readAllBytes(Paths.get("C:\\Users\\WJL\\Desktop\\asr_example.wav"));
//        // 编码为 Base64 字符串
//        System.out.println(Base64.getEncoder().encodeToString(fileBytes));
//        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
//
//            int bytesRead;
//
//            STTWebSocketEndpoint endpoint = new STTWebSocketEndpoint();
//            byte[] bytes = new byte[4096];
//            while ((bytesRead = inputStream.read(bytes)) != -1) {
////                endpoint.onMessage(null, ByteBuffer.wrap(bytes, 0, bytesRead));
//            }
//
//            if (endpoint.totalBytesRead <= 0) {
//                System.out.println("===>read len <= 0, exit");
//                return;
//            }
//
//            saveAsWav(endpoint.mainBuffer, "C:\\Users\\WJL\\Desktop\\output\\output.wav", 16000, 1, 16);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void saveAsWav(byte[] audioData, String filePath,
                                 int sampleRate, int channels, int bitsPerSample) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            byte[] header = generateWavHeader(audioData.length, sampleRate, channels, bitsPerSample);
            out.write(header);
            out.write(audioData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] generateWavHeader(int totalAudioLen, int sampleRate,
                                           int channels, int bitsPerSample) {
        ByteBuffer header = ByteBuffer.allocate(44);
        header.order(ByteOrder.LITTLE_ENDIAN);

        // RIFF块
        header.put("RIFF".getBytes());
        header.putInt(totalAudioLen + 36); // 文件总长度-8
        header.put("WAVE".getBytes());

        // fmt子块
        header.put("fmt ".getBytes());
        header.putInt(16);                // fmt块长度
        header.putShort((short) 1);        // PCM格式标识
        header.putShort((short) channels);
        header.putInt(sampleRate);
        header.putInt(sampleRate * channels * bitsPerSample / 8); // 字节率
        header.putShort((short) (channels * bitsPerSample / 8));    // 块对齐
        header.putShort((short) bitsPerSample);

        // data子块
        header.put("data".getBytes());
        header.putInt(totalAudioLen);

        return header.array();
    }
}