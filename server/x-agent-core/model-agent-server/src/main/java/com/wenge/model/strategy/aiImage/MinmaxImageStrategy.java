package com.wenge.model.strategy.aiImage;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.api.MinimaxiServer;
import com.wg.appframe.yayi.param.MinmaxImageParam;
import com.wg.appframe.yayi.result.MinmaxImageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：minmax
 * https://platform.minimaxi.com/document/vffrKguXhEQoELeH2hVECJnd?key=67b03bdcdd0f18b80647241a
 */
@Service("minmaxImageStrategy")
@Slf4j
public class MinmaxImageStrategy implements AiImageStrategy{

    @Autowired(required = false)
    private MinimaxiServer minimaxiServer;

    @Override
    public List<String> getAiImage(PromptGenerateParam param) {
        if (null == minimaxiServer) {
            log.error("MinimaxiServer is null");
            return new ArrayList<>();
        }
        MinmaxImageParam minmaxImageParam = new MinmaxImageParam();
        if (null != param.getWidth() && null != param.getHeight()) {
            minmaxImageParam.setAspect_ratio(param.getWidth() + ":" + param.getHeight());
        }
        if (null != param.getCount()) {
            minmaxImageParam.setN(param.getCount());
        }
        if (null != param.getPromptOptimizer()) {
            minmaxImageParam.setPrompt_optimizer(param.getPromptOptimizer());
        }
        if (CollectionUtil.isNotEmpty(param.getImageUrls())) {
            MinmaxImageParam.SubjectReference subjectReference = new MinmaxImageParam.SubjectReference();
            subjectReference.setType("character");
            subjectReference.setImage_file(param.getImageUrls());
            minmaxImageParam.setSubject_reference(subjectReference);
        }
        MinmaxImageParam.Style style = new MinmaxImageParam.Style();
        if (null != param.getStyleType()) {
            style.setStyle_type(param.getStyleType());
            minmaxImageParam.setStyle(style);
        }
        if (null != param.getStyleWeight()) {
            style.setStyle_weight(param.getStyleWeight());
            minmaxImageParam.setStyle(style);
        }
        minmaxImageParam.setAspect_ratio(param.getRatio());
        MinmaxImageResult minmaxImageResult = minimaxiServer.aiImage(param.getTopic(), minmaxImageParam);
        if (null != minmaxImageResult.getBase_resp() && 0 != minmaxImageResult.getBase_resp().getStatus_code()) {
            log.error("MinimaxiServer error:{}", minmaxImageResult.getBase_resp().getStatus_msg());
            return new ArrayList<>();
        }
        List<String> urlPathList = new ArrayList<>();
        try {
            // 默认采用url 格式返回
            List<String> urls = minmaxImageResult.getData().getImage_urls();
            if (CollectionUtil.isNotEmpty(urls)) {
                if (YesNoEnum.NO.getName().equals(param.getSaveFlag())) {
                    return urls;
                } else {
                    for (String url: urls) {
                        String decode = URLUtil.decode(url);
                        String[] split = decode.split("\\?");
                        int indexOf = split[0].lastIndexOf("/");
                        String fileName = split[0].substring(indexOf + 1);
                        String urlPath = uploadMinio(url, fileName);
                        urlPathList.add(urlPath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlPathList;
    }

}
