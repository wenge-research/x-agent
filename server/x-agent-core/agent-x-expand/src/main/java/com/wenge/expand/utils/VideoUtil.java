package com.wenge.expand.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.wenge.expand.dto.result.VideoInfoResult;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class VideoUtil {

    /**
     * 获取视频信息
     * @param videoUrl
     * @param coverUrl
     * @return
     * @throws IOException
     */
    public static VideoInfoResult getVideoInfo(String videoUrl, String coverUrl) throws IOException {
        FFmpegFrameGrabber grabber = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        VideoInfoResult info = new VideoInfoResult();
        String filePath = StringConstant.BLANK;
        File videoFile = null;
        try {
            ApplicationContext context = CoreContextProvider.getContext();
            WosUtil wosUtil = context.getBean(WosUtil.class);
            Environment environment = context.getEnvironment();
            String bucket = environment.getProperty("appframe.minio.bucket");
            // grabber = FFmpegFrameGrabber.createDefault(videoUrl);
            // 创建帧抓取器并设置视频URL
            //grabber = new FFmpegFrameGrabber(videoUrl);
            String[] split = videoUrl.split("\\?");
            int indexOf = split[0].lastIndexOf("/");
            String fileName = split[0].substring(indexOf + 1);
            String[] split1 = fileName.split("\\.");
            info.setFormat(split1[split1.length - 1]);
            // 网络下载
            if (videoUrl.contains("http")) {
                String downloadPath = environment.getProperty("multimedia.video.downloadPath");
                if (StringUtils.isBlank(downloadPath)) {
                    return info;
                }
                File file = new File(downloadPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String separator = File.separator;
                filePath = downloadPath + separator + IdUtil.simpleUUID() + "." + split1[split1.length - 1];
                HttpUtil.downloadFile(videoUrl, filePath);
            } else {
                // 本地磁盘
                filePath = videoUrl;
            }
            if (StringUtils.isBlank(filePath)) {
                return info;
            }
            grabber = new FFmpegFrameGrabber(filePath);
            grabber.start();

            // 获取视频时长（秒）
            Double duration = grabber.getLengthInTime() / 1000000.0;
            info.setVideoUrl(videoUrl);
            info.setDuration(duration);

            // 2. 获取分辨率
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            info.setWidth(width);
            info.setHeight(height);

            // 3. 获取文件大小
            videoFile = new File(videoUrl);
            if (videoFile.exists() && videoFile.isFile()) {
                long fileSize = videoFile.length();
                info.setSize(fileSize);
            }

            // 4. 获取封面
            if (StringUtils.isBlank(coverUrl)) {
                int attempts = 0; // 第几帧
                int maxAttempts = 100; // 最大尝试帧数
                Frame frame = null; // 每一帧的对象
                while ((frame = grabber.grabImage()) != null) {
                    if (attempts >= maxAttempts) {
                        break;
                    }
                    attempts++;
                    if (frame.image != null) {
                        Java2DFrameConverter converter = new Java2DFrameConverter();
                        BufferedImage firstFrame = converter.getBufferedImage(frame);
                        baos = new ByteArrayOutputStream();
                        ImageIO.write(firstFrame, "jpg", baos);
                        byte[] bytes = baos.toByteArray();
                        inputStream = new ByteArrayInputStream(bytes);
                        MinioInfoResult aiImage = wosUtil.upload(bucket, "aiVideo_cover", inputStream, IdUtil.simpleUUID() + ".jpg", true);
                        info.setCoverUrl(aiImage.getUrlPath());
                        break;
                    }
                }
            } else {
                info.setCoverUrl(coverUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (grabber != null) {
                grabber.stop();
                grabber.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != baos) {
                baos.close();
            }
            if (StringUtils.isNotBlank(filePath)) {
                FileUtil.del(filePath);
            }
            if (videoFile != null){
                videoFile.delete();
            }

        }
        return info;
    }
}
