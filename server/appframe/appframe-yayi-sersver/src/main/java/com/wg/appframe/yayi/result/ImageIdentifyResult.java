package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageIdentifyResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -3342134440879873042L;

    private ImageIdentifyData data;

    @Data
    public static class ImageIdentifyData implements Serializable {

        private static final long serialVersionUID = 3852669622339262680L;

        private FileContent file_content;
    }

    @Data
    public static class FileContent implements Serializable {

        private static final long serialVersionUID = 6921497400541514435L;
        private String type;
        private String title;
        private String url;
        private ContentValue content;
        private String pubtime;
    }

    @Data
    public static class ContentValue implements Serializable {
        private String OCR;
        private String Caption;
    }

}
