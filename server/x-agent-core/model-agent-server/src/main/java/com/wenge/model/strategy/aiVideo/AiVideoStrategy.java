package com.wenge.model.strategy.aiVideo;

import cn.hutool.http.HttpUtil;
import com.wenge.model.dto.param.AiVideoParam;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AiVideoStrategy {

    String getAiVideo(AiVideoParam param);


    /**
     * 上传图片到minio
     *
     * @param videoUrl
     * @return
     */
    default String uploadMinio(String videoUrl, String fileName) {
        if (StringUtils.isBlank(videoUrl) || StringUtils.isBlank(fileName)) {
            return StringConstant.BLANK;
        }
        InputStream inputStream = null;
        // String fileName = StringConstant.BLANK;
        String urlPath = StringConstant.BLANK;
        try {
            ApplicationContext context = CoreContextProvider.getContext();
            WosUtil wosUtil = context.getBean(WosUtil.class);
            Environment environment = context.getEnvironment();
            String bucket = environment.getProperty("appframe.minio.bucket");

            byte[] bytes = HttpUtil.downloadBytes(videoUrl);
            inputStream = new ByteArrayInputStream(bytes);
            MinioInfoResult aiImage = wosUtil.upload(bucket, "aiVideo", inputStream, fileName, true);

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
