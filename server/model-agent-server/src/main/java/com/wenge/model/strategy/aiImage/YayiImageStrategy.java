package com.wenge.model.strategy.aiImage;

import com.wenge.model.dto.param.PromptGenerateParam;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.AiImageParam;
import com.wg.appframe.yayi.result.AiImageResult;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class YayiImageStrategy implements AiImageStrategy{

    @Autowired(required = false)
    private YayiServer yayiServer;

    @Override
    public List<String> getAiImage(PromptGenerateParam param) {
        if (yayiServer == null) {
            log.error("YayiServer is null");
            return new ArrayList<>();
        }
        List<String> urlPathList = new ArrayList<>();
        try {
            AiImageParam aiImageParam = new AiImageParam();
            AiImageParam.Content content = new AiImageParam.Content();
            aiImageParam.setContent(content);
            content.setWidth(128);
            content.setHeight(128);
            if (null != param.getWidth()) {
                content.setWidth(param.getWidth());
            }
            if (null != param.getHeight()) {
                content.setHeight(param.getHeight());
            }
            content.setStyle("插画");
            String prompt = "${topic}\n${description}";
            prompt = prompt.replace("${topic}", param.getTopic())
                    .replace("${description}", param.getDescription());
            content.setPrompt(prompt);
            AiImageResult aiImageResult = yayiServer.aiImage(aiImageParam);
            if (null != aiImageResult) {
                AiImageResult.ImageData data = aiImageResult.getData();
                if (null != data) {
                    List<String> imageUrls = data.getImages();
                    if (CollectionUtils.isNotEmpty(imageUrls)) {
                        if (YesNoEnum.NO.getName().equals(param.getSaveFlag())) {
                            return imageUrls;
                        } else {
                            for (String imageUrl: imageUrls) {
                                int indexOf = imageUrl.lastIndexOf("/");
                                String fileName = imageUrl.substring(indexOf + 1);
                                String urlPath = uploadMinio(imageUrl, fileName);
                                urlPathList.add(urlPath);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlPathList;
    }

}
