package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContentParsingNewVersionResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -3342134440879873042L;

    private ContentParsingNewVersionData data;

    @Data
    public static class ContentParsingNewVersionData implements Serializable {

        private static final long serialVersionUID = 3852669622339262680L;

        private FileContent file_content;
    }

    @Data
    public static class FileContent implements Serializable {

        private static final long serialVersionUID = 6921497400541514435L;
        private String type;
        private String title;
        private String url;
        /**
         * 具体解析内容
         * 注：非全文模式content输出类型为list-入參的mode=0
         */
        private List<ContentValue> content;
        private String pubtime;
    }

    @Data
    public static class ContentValue implements Serializable {
        private String text;
        private String type;
        private String title;
        private Integer para_num;
        private Integer page_num;
        private List<List<Long>> bbox;
    }

}
