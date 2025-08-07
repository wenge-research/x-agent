package com.wenge.model.strategy.tts;

import com.wenge.model.constants.TtsConstants;
import com.wenge.model.dto.param.TtsParam;
import com.wenge.model.entity.VoiceComponentInfo;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Service(TtsConstants.XUN_FEI)
public class XunFeiStrategy implements TtsStrategy {
    @Override
    public void textToVoice(TtsParam ttsParam, HttpServletResponse response) {

    }



//    @Override
    public void voiceToTextStream(VoiceComponentInfo componentInfo, InputStream inputStream, String clientId) {
        // 构建鉴权url
        String authUrl = getAuthUrl(componentInfo.getVoiceLink(), componentInfo.getApiKey(), componentInfo.getApiSecret());
        OkHttpClient client = new OkHttpClient.Builder().build();
        //将url中的 schema http://和https://分别替换为ws:// 和 wss://
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, new XunFeiWebSocketListener(clientId, inputStream, componentInfo.getAppId()));
    }


    /**
     * 获取鉴权url
     *
     * @param hostUrl
     * @param apiKey
     * @param apiSecret
     * @return
     * @throws Exception
     */
    public String getAuthUrl(String hostUrl, String apiKey, String apiSecret) {
        HttpUrl httpUrl;
        try {
            URL url = new URL(hostUrl);
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());
            StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                    append("date: ").append(date).append("\n").//
                    append("GET ").append(url.getPath()).append(" HTTP/1.1");
            // System.out.println(builder);
            Charset charset = Charset.forName("UTF-8");
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
            mac.init(spec);
            byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
            String sha = Base64.getEncoder().encodeToString(hexDigits);
            //System.out.println(sha);
            String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
            //System.out.println(authorization);
            httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                    addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).//
                    addQueryParameter("date", date).//
                    addQueryParameter("host", url.getHost()).//
                    build();
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return httpUrl.toString();
    }
}
