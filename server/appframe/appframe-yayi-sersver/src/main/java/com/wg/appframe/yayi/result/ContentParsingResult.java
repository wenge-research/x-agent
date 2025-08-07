package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContentParsingResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -3342134440879873042L;

    private ContentParsingData data;

    // Data类
    @Data
    public static class ContentParsingData implements Serializable {

        private static final long serialVersionUID = 3852669622339262680L;

        private FileContent file_content;
    }

    // FileContent
    @Data
    public static class FileContent implements Serializable {

        private static final long serialVersionUID = 6921497400541514435L;
        private String type;
        private String title;
        private String url;
        /**
         * 具体解析内容
         * 注：
         * 全文模式输出类型为string
         * 非全文模式输出类型为list
         */
        private Object content;
        private String pubtime;
    }


    // FileContent
    @Data
    public static class Info implements Serializable {

        private static final long serialVersionUID = 1701323719137514941L;

        private String file_type;
        private Integer page_num;
        private String file_name;
        private String src;
        private List<List<Long>> bbox;
        private List<Float> shape;
        private Integer para_num;
    }
}
