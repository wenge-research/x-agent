package com.wenge.model.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.param.ImageAnalysisParam;
import com.wenge.model.entity.File;
import com.wenge.model.entity.FileData;
import com.wenge.model.entity.Folders;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.service.FoldersService;
import com.wenge.model.service.MultimediaService;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.result.ImageIdentifyResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 图片文件实现类
 */
@Service("image")
@Slf4j
public class ImageMultimediaServiceImpl extends AbstractMultimediaService {

    public ImageMultimediaServiceImpl() {
        super.FILE_TYPE_LIST = ListUtil.toList(".jpg", ".jpeg", ".png");
    }

    @Value("${third.image.url}")
    private String url;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private YayiServer yayiServer;

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
        fileDetail.setType(MultimediaEnum.IMAGE.getCode());
        return fileDetail;
    }

    @Override
    public String analysis(File file) throws IOException {
//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(7200, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//                .build();

//        Request.Builder builder = new Request.Builder();
//        Map<Object, Object> param = new HashMap<>();
//        param.put("url", file.getFileUrl());
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONUtil.toJsonStr(param));
//        builder.url("").post(body);
////        builder.url(this.url).post(body);
//        Call call = httpClient.newCall(builder.build());
//        Response response = call.execute();
//        String string = response.body().string();
        ImageIdentifyResult.ContentValue contentValue = this.imageAnalysis(file.getFileUrl());
        JSONObject entries = JSONUtil.parseObj(contentValue);
//        String output = (String) entries.get("output");
        String output = (String) entries.get("Caption");
        if (StringUtil.isBlank(output)) {
            throw new RuntimeException("Failed to process image");
        }
        return output;
    }

    /**
     * 文档转文本
     */
    private ImageIdentifyResult.ContentValue imageAnalysis(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return null;
        }
        ImageIdentifyResult imageIdentifyResult = yayiServer.imageIdentify(imageUrl);
        if (null == imageIdentifyResult) {
            return null;
        }
        ImageIdentifyResult.ImageIdentifyData data = imageIdentifyResult.getData();
        if (null == data) {
            return null;
        }
        ImageIdentifyResult.FileContent fileContents = data.getFile_content();
        if (null == fileContents) {
            return null;
        }
        return fileContents.getContent();
    }

}
