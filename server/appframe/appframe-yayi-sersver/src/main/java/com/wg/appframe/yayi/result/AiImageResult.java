package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiImageResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -2278628249590665250L;

    private ImageData data;

    // Data类
    @Data
    public static class ImageData implements Serializable {

        private static final long serialVersionUID = 3852669622339262680L;

        private ImageContent content;
        private List<Boolean> dangerFlags;
        private List<String> images;
        private String outline;
    }

    // FileContent
    @Data
    public static class ImageContent implements Serializable {

        private static final long serialVersionUID = 8143622562771826468L;
        private String artist;
        private Integer height;
        private List<String> magicWords;
        private String picForm;
        private Integer picNum;
        private String prompt;
        private Long seed;
        private Integer steps;
        private String style;
        private Boolean useFilter;
        private Integer width;
        private String initImageUrl;
        private String mode;
        private String negPrompt;
        private Double strength;
        private String maskImageUrl;
        private String func;
    }
}
