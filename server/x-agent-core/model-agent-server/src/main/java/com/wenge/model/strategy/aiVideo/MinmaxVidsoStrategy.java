package com.wenge.model.strategy.aiVideo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.wenge.model.dto.param.AiVideoParam;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.api.MinimaxiServer;
import com.wg.appframe.yayi.param.MinmaxVideoParam;
import com.wg.appframe.yayi.result.MinmaxCompletionResult;
import com.wg.appframe.yayi.result.MinmaxFileResult;
import com.wg.appframe.yayi.result.MinmaxVideoResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述: minimax
 * https://platform.minimaxi.com/document/video_generation?key=66d1439376e52fcee2853049
 */
@Service("minmaxVideoStrategy")
@Slf4j
public class MinmaxVidsoStrategy implements AiVideoStrategy {

    @Autowired(required = false)
    private MinimaxiServer minimaxiServer;

    @Override
    public String getAiVideo(AiVideoParam param) {
        if (null == minimaxiServer) {
            log.error("MinimaxiServer is null");
            return StringConstant.BLANK;
        }
        MinmaxVideoParam minmaxVideoParam = new MinmaxVideoParam();
        if (StringUtils.isNotBlank(param.getResolution())) {
            minmaxVideoParam.setResolution(param.getResolution());
        }
        if (null != param.getDuration()) {
            minmaxVideoParam.setDuration(param.getDuration());
        }

        if (StringUtils.isNotBlank(param.getImageUrl())) {
            minmaxVideoParam.setFirst_frame_image(param.getImageUrl());
        }

        if (CollectionUtil.isNotEmpty(param.getSubjectImageUrl())) {
            MinmaxVideoParam.SubjectReference subjectReference = new MinmaxVideoParam.SubjectReference();
            subjectReference.setType("character");
            subjectReference.setImage(param.getSubjectImageUrl());
            minmaxVideoParam.setSubject_reference(subjectReference);
        }

        minmaxVideoParam.setPrompt_optimizer(true);
        MinmaxVideoResult minmaxVideoResult = minimaxiServer.aiVideo(param.getContent(), minmaxVideoParam);
        MinmaxCompletionResult.BaseResp baseResp = minmaxVideoResult.getBase_resp();
        if (null != baseResp && 0 != baseResp.getStatus_code()) {
            log.info("MinimaxiServer error:{}", baseResp.getStatus_msg());
            return StringConstant.BLANK;
        }
        String videoUrl = StringConstant.BLANK;
        try {
            // 默认采用url 格式返回
            String id = minmaxVideoResult.getTask_id();
            log.info("minmaxVideoResult.id:{}", id);
            if (StringUtil.isBlank(id)) {
                return StringConstant.BLANK;
            }

            MinmaxVideoResult videoResult = null;
            // 最长等待10分钟
            long maxTime = 1000 * 60 * 10;
            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > maxTime) {
                    log.info("生成视频超时了");
                    break;
                }
                videoResult = minimaxiServer.tasks(id, minmaxVideoParam.getAppKey());
                // status: running, succeeded
                if ("Success".equals(videoResult.getStatus())) {
                    String fileId = videoResult.getFile_id();
                    if (StringUtils.isNotBlank(fileId)) {
                        MinmaxFileResult download = minimaxiServer.download(fileId, minmaxVideoParam.getAppKey());
                        if (null != download) {
                            baseResp = download.getBase_resp();

                            if (0 == baseResp.getStatus_code() && StringUtils.isNotBlank(download.getFile().getDownload_url())) {
                                videoUrl = download.getFile().getDownload_url();
                                if (!YesNoEnum.NO.getName().equals(param.getSaveFlag())) {
                                    String[] split = videoUrl.split("\\?");
                                    int indexOf = split[0].lastIndexOf("/");
                                    String fileName = split[0].substring(indexOf + 1);
                                    videoUrl = uploadMinio(videoUrl, fileName);
                                }
                                break;
                            }
                        }
                    }
                }
                ThreadUtil.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoUrl;
    }
}
