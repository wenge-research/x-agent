package com.wenge.model.strategy.aiVideo;

import cn.hutool.core.thread.ThreadUtil;
import com.wenge.model.dto.param.AiVideoParam;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.api.VolcengineServer;
import com.wg.appframe.yayi.param.DoubaoVideoParam;
import com.wg.appframe.yayi.result.DoubaoVideoResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 火山引擎 ai 视频
 * https://www.volcengine.com/docs/82379/1520757
 */
@Service("dbVideoStrategy")
@Slf4j
public class DoubaoVideoStrategy implements AiVideoStrategy {

    @Autowired(required = false)
    private VolcengineServer volcengineServer;

    @Override
    public String getAiVideo(AiVideoParam param) {
        if (null == volcengineServer) {
            log.error("VolcengineServer is null");
            return StringConstant.BLANK;
        }
        DoubaoVideoParam doubaoVideoParam = new DoubaoVideoParam();
        if (StringUtils.isNotBlank(param.getResolution())) {
            doubaoVideoParam.setResolution(param.getResolution());
        }
        if (StringUtils.isNotBlank(param.getRatio())) {
            doubaoVideoParam.setRatio(param.getRatio());
        }
        if (param.getDuration() != null) {
            doubaoVideoParam.setDuration(param.getDuration());
        }

        // 参考图
        if (StringUtils.isNotBlank(param.getImageUrl())) {
            List<DoubaoVideoParam.Content> contentList = Lists.newArrayList();
            DoubaoVideoParam.Content content = new DoubaoVideoParam.Content();
            content.setType("text");
            content.setText(param.getContent());
            contentList.add(content);

            content = new DoubaoVideoParam.Content();
            content.setType("image_url");
            DoubaoVideoParam.ImageUrl imageUrl = new DoubaoVideoParam.ImageUrl();
            imageUrl.setUrl(param.getImageUrl());
            content.setImage_url(imageUrl);
            // 第一帧
            content.setRole("first_frame");
            contentList.add(content);

            doubaoVideoParam.setContent(contentList);
        }

        if (null != param.getFramepersecond()) {
            doubaoVideoParam.setFramepersecond(param.getFramepersecond());
        }
        if (null != param.getCamerafixed()) {
            doubaoVideoParam.setCamerafixed(param.getCamerafixed());
        }

        DoubaoVideoResult doubaoVideoResult = volcengineServer.aiVideo(param.getContent(), doubaoVideoParam);
        String videoUrl = StringConstant.BLANK;
        try {
            // 默认采用url 格式返回
            String id = doubaoVideoResult.getId();
            log.info("doubaoVideoResult.id:{}", id);
            if (StringUtil.isBlank(id)) {
                return StringConstant.BLANK;
            }
            DoubaoVideoResult videoResult = null;
            // 最长等待10分钟
            long maxTime = 1000 * 60 * 10;
            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > maxTime) {
                    log.info("生成视频超时了");
                    break;
                }
                videoResult = volcengineServer.tasks(id, doubaoVideoParam.getAppKey());
                // status: running, succeeded
                if ("succeeded".equals(videoResult.getStatus())) {
                    DoubaoVideoResult.Content resultContent = videoResult.getContent();
                    if (null != resultContent) {
                        videoUrl = resultContent.getVideo_url();
                        if (!YesNoEnum.NO.getName().equals(param.getSaveFlag())) {
                            String[] split = videoUrl.split("\\?");
                            int indexOf = split[0].lastIndexOf("/");
                            String fileName = split[0].substring(indexOf + 1);
                            videoUrl = uploadMinio(videoUrl, fileName);
                        }
                    }
                    break;
                }
                ThreadUtil.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoUrl;
    }
}
