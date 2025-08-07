package com.wenge.model.strategy.tts;

import com.google.gson.Gson;
import com.wenge.model.utils.SseEmitterUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
public class XunFeiWebSocketListener extends WebSocketListener {
    // 音频输入流
    private InputStream inputStream;
    public static final int StatusFirstFrame = 0;
    public static final int StatusContinueFrame = 1;
    public static final int StatusLastFrame = 2;
    public static final Gson gson = new Gson();
    // 开始时间
    private static Date dateBegin = new Date();
    // 结束时间
    private static Date dateEnd = new Date();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");

    /**
     * 推送客户端id
     */
    private String clientId;

    /**
     * appid
     */
    private String appid;

    /**
     * 推送文本内容
     */
    private StringBuilder pushText = new StringBuilder();

    public XunFeiWebSocketListener(String clientId, InputStream inputStream, String appid) {
        this.clientId = clientId;
        this.inputStream = inputStream;
        this.appid = appid;
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        new Thread(() -> {
            //连接成功，开始发送数据
            int frameSize = 1280; //每一帧音频的大小,建议每 40ms 发送 122B
            int intervel = 40;
            int status = 0;  // 音频的状态
            int seq = 0; //数据序号
            try (InputStream fs = inputStream) {
                byte[] buffer = new byte[frameSize];
                // 发送音频
                end:
                while (true) {
                    seq++; // 每次循环更新下seq
                    int len = fs.read(buffer);
                    if (len == -1) {
                        status = StatusLastFrame;  //文件读完，改变status 为 2
                    }
                    switch (status) {
                        case StatusFirstFrame:   // 第一帧音频status = 0
                            String json = "{\n" +
                                    "  \"header\": {\n" +
                                    "    \"app_id\": \"" + appid + "\",\n" +
                                    "    \"status\": " + StatusFirstFrame + "\n" +
                                    "  },\n" +
                                    "  \"parameter\": {\n" +
                                    "    \"iat\": {\n" +
                                    "      \"domain\": \"slm\",\n" +
                                    "      \"language\": \"zh_cn\",\n" +
                                    "      \"accent\": \"mulacc\",\n" +
                                    "      \"eos\": 6000,\n" +
                                    "      \"vinfo\": 1,\n" +
                                    "      \"result\": {\n" +
                                    "        \"encoding\": \"utf8\",\n" +
                                    "        \"compress\": \"raw\",\n" +
                                    "        \"format\": \"json\"\n" +
                                    "      }\n" +
                                    "    }\n" +
                                    "  },\n" +
                                    "  \"payload\": {\n" +
                                    "    \"audio\": {\n" +
                                    "      \"encoding\": \"raw\",\n" +
                                    "      \"sample_rate\": 16000,\n" +
                                    "      \"channels\": 1,\n" +
                                    "      \"bit_depth\": 16,\n" +
                                    "      \"seq\": " + seq + ",\n" +
                                    "      \"status\": 0,\n" +
                                    "      \"audio\": \"" + Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)) + "\"\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "}";
                            webSocket.send(json);
                            // System.err.println(json);
                            System.out.println("第一帧音频发送完毕...");
                            System.out.println("中间音频将持续发送...");
                            status = StatusContinueFrame;  // 发送完第一帧改变status 为 1
                            break;
                        case StatusContinueFrame:  //中间帧status = 1
                            json = "{\n" +
                                    "  \"header\": {\n" +
                                    "    \"app_id\": \"" + appid + "\",\n" +
                                    "    \"status\": 1\n" +
                                    "  },\n" +
                                    "  \"payload\": {\n" +
                                    "    \"audio\": {\n" +
                                    "      \"encoding\": \"raw\",\n" +
                                    "      \"sample_rate\": 16000,\n" +
                                    "      \"channels\": 1,\n" +
                                    "      \"bit_depth\": 16,\n" +
                                    "      \"seq\": " + seq + ",\n" +
                                    "      \"status\": 1,\n" +
                                    "      \"audio\": \"" + Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)) + "\"\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "}";
                            webSocket.send(json);
                            // System.err.println(json);
                            // System.out.println("中间帧音频发送中...");
                            break;
                        case StatusLastFrame:    // 最后一帧音频status = 2 ，标志音频发送结束
                            json = "{\n" +
                                    "  \"header\": {\n" +
                                    "    \"app_id\": \"" + appid + "\",\n" +
                                    "    \"status\": 2\n" +
                                    "  },\n" +
                                    "  \"payload\": {\n" +
                                    "    \"audio\": {\n" +
                                    "      \"encoding\": \"raw\",\n" +
                                    "      \"sample_rate\": 16000,\n" +
                                    "      \"channels\": 1,\n" +
                                    "      \"bit_depth\": 16,\n" +
                                    "      \"seq\": " + seq + ",\n" +
                                    "      \"status\": 2,\n" +
                                    "      \"audio\": \"\"\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "}";
                            webSocket.send(json);
                            // System.err.println(json);
                            System.out.println("最后一帧音频发送完毕...");
                            break end;
                    }
                    Thread.sleep(intervel); //模拟音频采样延时
                }
                System.out.println("所有音频发送完毕...");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        // System.out.println(text);
        JsonParse jsonParse = gson.fromJson(text, JsonParse.class);
        if (jsonParse != null) {
            if (jsonParse.header.code != 0) {
                log.info("识别失败，错误码:{} error=>{} sid={}", jsonParse.header.code, jsonParse.header.message, jsonParse.header.sid);
                System.out.println("错误码查询链接：https://www.xfyun.cn/document/error-code");
                return;
            }
            if (jsonParse.payload != null) {
                if (jsonParse.payload.result.text != null) { // 中间结果
                    byte[] decodedBytes = Base64.getDecoder().decode(jsonParse.payload.result.text);
                    String decodeRes = new String(decodedBytes, StandardCharsets.UTF_8);
                    JsonParseText jsonParseText = gson.fromJson(decodeRes, JsonParseText.class);
                    List<Ws> wsList = jsonParseText.ws;
                    for (Ws ws : wsList) {
                        List<Cw> cwList = ws.cw;
                        for (Cw cw : cwList) {
                            log.debug("音频文件中间识别结果：{}", cw.w);
                            pushText.append(cw.w);
                            try {
                                SseEmitterUtils.sendMsg(clientId, pushText.toString());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                if (jsonParse.payload.result.status == 2) { // 最终结果  说明数据全部返回完毕，可以关闭连接，释放资源
                    System.out.println("session end ");
                    dateEnd = new Date();
                    log.info("识别结束, 本次识别开始时间{}，结束时间{}，耗时{}ms，本次识别sid==>{}", sdf.format(dateBegin),
                            sdf.format(dateEnd), dateEnd.getTime() - dateBegin.getTime(), jsonParse.header.sid);
                    webSocket.close(1000, "");
                }
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 返回结果拆分与展示，仅供参考
    // 返回结果拆分与展示，仅供参考
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        String message;
        String sid;
        int status;
    }

    class Payload {
        Result result;
    }

    class Result {
        String text;
        int status;
    }

    class JsonParseText {
        List<Ws> ws;
        String pgs;
        List<Integer> rg;
    }

    class Ws {
        List<Cw> cw;
    }

    class Cw {
        String w;
    }
}
