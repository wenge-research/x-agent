package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScreenshotResult implements Serializable {

    private static final long serialVersionUID = 7202259490421963758L;

    /**
     * 任务
     */
    private String taskId;

    /**
     * 截图详情
     */
    private List<Detail> detailList;

    @Data
    public static class Detail implements Serializable {

        private static final long serialVersionUID = -3106505325586156257L;

        /**
         * 截图图片
         */
        private String imageUrl;

        /**
         * 原始网页链接
         */
        private String originalUrl;
    }
}
