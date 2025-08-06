package com.wenge.model.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wenge.model.constants.MediaAnalysisConstant;
import com.wenge.model.dto.result.AudioAndVideoAnalysisResult;
import com.wenge.model.entity.File;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.service.FileDataService;
import com.wenge.model.service.FoldersService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.result.Generate30BResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 音频文件实现类
 */
@Service("audio")
@Slf4j
public class AudioMultimediaServiceImpl extends AbstractMultimediaService {

    public AudioMultimediaServiceImpl() {
        super.FILE_TYPE_LIST = ListUtil.toList(".mp3", ".wav");
    }

    @Value("${third.audio.url}")
    private String url;

    @Value("${third.audio.yayiRewrite:false}")
    private Boolean yayiEnable;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private FileDataService fileDataService;

    @Autowired
    private ServletWebServerApplicationContext applicationContext;

    @Override
    protected File getFile(MultipartFile file, String foldersId) {
        File fileDetail = new File();
        fileDetail.setFileId(IdUtil.simpleUUID());
        fileDetail.setFoldersId(foldersId);
        fileDetail.setFileName(file.getOriginalFilename());
        int indexOf = file.getOriginalFilename().lastIndexOf(".");
        String type = file.getOriginalFilename().substring(indexOf + 1);
        fileDetail.setFileType(type);
        fileDetail.setFileSize(file.getSize());
        fileDetail.setMultipartFile(file);
        fileDetail.setType(MultimediaEnum.AUDIO.getCode());
        return fileDetail;
    }

    @Override
    public String analysis(File file) throws IOException {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(7200, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Request.Builder builder = new Request.Builder();
        Map<Object, Object> param = new HashMap<>();
        param.put("url", file.getFileUrl());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONUtil.toJsonStr(param));
        builder.url(this.url).post(body);
        Call call = httpClient.newCall(builder.build());
        Response response = call.execute();

        if (Objects.equals(response.code(), MediaAnalysisConstant.QUEUE_CODE)) {
            log.info("任务繁忙，code:" + response.code());
            return MediaAnalysisConstant.QUEUE_FLAG;
        }

        String string = response.body().string();
        AudioAndVideoAnalysisResult result = JSONUtil.toBean(string, AudioAndVideoAnalysisResult.class);
        String transcription = result.getTranscription();
        // 雅意重写
        if (yayiEnable) {
            String yayiRewrite = yayiRewrite(transcription);
            if (StrUtil.isNotBlank(yayiRewrite)) {
                return yayiRewrite;
            }
        }
        return transcription;
    }

    /**
     * 调用雅意功能，修正语音识别出的文本可能产生的语法、语义错误
     */
    private String yayiRewrite(String transcription) {
        Generate30BResult result = yayiServer.generate30B("【"+transcription + "】 【】中的文本为语音识别后的文本，请检查上述文本是否有单词，语句错误的问题，如果存在修改为符合人类阅读习惯的语句，只输出修改后的文本。只输出修正后的文本格式，其他描述不要。");
        if (result.getCode() != 200) {
            return "";
        }
        Generate30BResult.GenerateData data = result.getData();
        return data.getChoices().get(0).getMessage().getContent();
    }
}
