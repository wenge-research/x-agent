package com.wenge.model.strategy.tts;

import com.wenge.model.constants.TtsConstants;
import com.wenge.model.dto.param.TtsParam;
import com.wenge.model.entity.VoiceComponentInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service(TtsConstants.VOLCENGINE)
public class VolcengineStrategy implements TtsStrategy {
    @Override
    public void textToVoice(TtsParam ttsParam, HttpServletResponse response) {

    }


//    @Override
    public void voiceToTextStream(VoiceComponentInfo componentInfo, InputStream inputStream, String clientId) {

        final String url = componentInfo.getVoiceLink();
        final String appId = componentInfo.getAppId();
        final String token = componentInfo.getApiSecret();

        try {
            AudioInputStream ins = AudioSystem.getAudioInputStream(inputStream);

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
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());


            final OkHttpClient okHttpClient = new OkHttpClient.Builder().pingInterval(50, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts[0]).build();
//            okHttpClient.newWebSocket(request, new VolcengineWebSocketListener(ins, clientId));

        } catch (UnsupportedAudioFileException | IOException | KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
