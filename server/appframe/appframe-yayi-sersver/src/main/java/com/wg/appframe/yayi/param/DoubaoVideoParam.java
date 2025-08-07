package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.config.DoubaoVideoConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DoubaoVideoParam extends DoubaoVideoConfig {

    private static final long serialVersionUID = 7934388669301739831L;

    private String appKey;
    private List<Content> content;

    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = 4425498793510330164L;
        private String type;
        private String role;
        private String text;
        private ImageUrl image_url;
    }

    @Data
    public static class  ImageUrl implements Serializable {

        private static final long serialVersionUID = 2805754183611411365L;

        private String url;

    }
}
