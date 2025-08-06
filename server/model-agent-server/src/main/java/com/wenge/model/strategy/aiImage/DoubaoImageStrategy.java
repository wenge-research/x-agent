package com.wenge.model.strategy.aiImage;

import com.mybatisflex.core.util.CollectionUtil;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.api.VolcengineServer;
import com.wg.appframe.yayi.param.DoubaoImageParam;
import com.wg.appframe.yayi.result.DoubaoImageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 火山引擎 ai 图片
 * https://www.volcengine.com/docs/82379/1541523
 */
@Service("dbImageStrategy")
@Slf4j
public class DoubaoImageStrategy implements AiImageStrategy{

    @Autowired(required = false)
    private VolcengineServer volcengineServer;

    @Override
    public List<String> getAiImage(PromptGenerateParam param) {
        if (null == volcengineServer) {
            log.error("VolcengineServer is null");
            return new ArrayList<>();
        }
        DoubaoImageParam doubaoImageParam = new DoubaoImageParam();
        if (null != param.getWidth() && null != param.getHeight()) {
            doubaoImageParam.setSize(param.getWidth() + "x" + param.getHeight());
        }
        if (null != param.getGuidanceScale()) {
            doubaoImageParam.setGuidance_scale(param.getGuidanceScale());
        }
        DoubaoImageResult doubaoImageResult = volcengineServer.aiImage(param.getTopic(), doubaoImageParam);
        if (null != doubaoImageResult.getError()) {
            log.error("VolcengineServer error:{}", doubaoImageResult.getError().getMessage());
            return new ArrayList<>();
        }
        List<String> urlPathList = new ArrayList<>();
        try {
            // 默认采用url 格式返回
            List<DoubaoImageResult.Data> datas = doubaoImageResult.getData();
            List<String> urls = datas.stream().map(DoubaoImageResult.Data::getUrl).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(urls)) {
                if (YesNoEnum.NO.getName().equals(param.getSaveFlag())) {
                    return urls;
                } else {
                    for (String url:  urls) {
                        String[] split = url.split("\\?");
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
