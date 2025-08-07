package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiImageParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 2472789111271586402L;

    private String url;
    private Float controlnet_conditioning_scale;
    private Boolean return_outline;
    private Content content;

    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = 8519701832344182226L;

        private String mode;
        private String prompt;
        private String negPrompt;
        private Boolean autoStyle;
        private String style;
        private String artist;
        private List<String> magicWords;
        private Integer width;
        private Integer height;
        private Integer scale;
        private Float guidance_scale;
        private Integer picNum;
        private Boolean useFilter;
        private Integer steps;
        private Integer seed;
        private String picForm;
        private String initImageUrl;
        private Float strength;
        private String control;
        private String maskImageUrl;
        private String func;

    }

}
