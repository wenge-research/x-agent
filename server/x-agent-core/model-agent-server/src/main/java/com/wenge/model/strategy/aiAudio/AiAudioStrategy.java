package com.wenge.model.strategy.aiAudio;

import cn.hutool.http.HttpUtil;
import com.wenge.model.dto.param.AiAudioParam;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AiAudioStrategy {

    String getAiAudio(AiAudioParam param);

    /**
     * 上传图片到minio
     *
     * @param audioUrl
     * @return
     */
    default String uploadMinio(String audioUrl, String hexData, String fileName) {
        if (StringUtils.isBlank(audioUrl) && StringUtils.isBlank(hexData)) {
            return StringConstant.BLANK;
        }
        if (StringUtils.isBlank(fileName)) {
            fileName = StringConstant.BLANK;
        }
        InputStream inputStream = null;
        // String fileName = StringConstant.BLANK;
        String urlPath = StringConstant.BLANK;
        try {
            ApplicationContext context = CoreContextProvider.getContext();
            WosUtil wosUtil = context.getBean(WosUtil.class);
            Environment environment = context.getEnvironment();
            String bucket = environment.getProperty("appframe.minio.bucket");
            byte[] bytes;
            if (StringUtils.isNotBlank(audioUrl)) {
                bytes = HttpUtil.downloadBytes(audioUrl);
            } else {
                bytes = DatatypeConverter.parseHexBinary(hexData);
            }
            inputStream = new ByteArrayInputStream(bytes);
            MinioInfoResult aiImage = wosUtil.upload(bucket, "aiAudio", inputStream, fileName, true);
            urlPath = aiImage.getUrlPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                java.io.File file = new java.io.File(fileName);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return urlPath;
    }
}
