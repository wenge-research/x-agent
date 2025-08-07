package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentResult extends YayiResult implements Serializable {
    private static final long serialVersionUID = -75954427063438215L;

    private DocumentData data;

    // Data类
    @Data
    public static class DocumentData implements Serializable {

        private static final long serialVersionUID = 5132157250255496625L;

        private String user_id;
        private String alltext;

        private List<FileContent> file_content;
    }

    // FileContent
    @Data
    public static class FileContent implements Serializable {
        private static final long serialVersionUID = -529905996273455977L;

        private String text;
        private String title;
        private String type;
        private String src;
        private List<List<Long>> bbox;
        private List<Float> shape;
        private Integer para_num;
        private Integer page_num;

        private Info info;
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
