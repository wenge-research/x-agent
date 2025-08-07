package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -7441104216830728841L;

    private DocumentData data;

    // Data类
    @Data
    public static class DocumentData implements Serializable {

        private static final long serialVersionUID = 5132157250255496625L;

        private String robot_id;
        private String status;
        private List<String> file_id_list;
        private String res;
        private List<ResInfo> res_info;
        private boolean find_label;
        private List<String> questions;
        private List<ChunkResult> chunk_result;
    }

    // FileContent
    @Data
    public static class ResInfo implements Serializable {

        private static final long serialVersionUID = -3913207726124871904L;

        private String context;
        private FilePagePara file_page_para;
        private String title;
    }

    // FileContent
    @Data
    public static class ChunkResult implements Serializable {

        private static final long serialVersionUID = 6203166074363418734L;

        private List<TitleContent> table_content;
        private List<TitleContent> title_content;
    }


    // FileContent
    @Data
    public static class FilePagePara implements Serializable {
        private static final long serialVersionUID = -4933190769658338264L;

        private String file_id;
        private List<List<Integer>> page_para_list;
    }
    

    // FileContent
    @Data
    public static class TitleContent implements Serializable {

        private static final long serialVersionUID = 3223804132751422520L;

        private FileInfo info;
        private String page_para_list_str;
        private String para_bbox_list_str;
        private String text;
        private String title;
        private String type;
        private Integer para_num;
    }


    /**
     * Represents information specific to a file.
     */
    @Data
    public static class FileInfo {
        private String file_type;
        private String file_id;
        private Integer page_num;
        private String file_name;
    }
}
