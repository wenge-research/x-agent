package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 7045245860746971588L;

    public static final String KNOWLEDGE_SPLIT_CONFIG = "knowledgeSplitConfig";
    public static final String KNOWLEDGE_CONFIG = "knowledgeConfig";

    private String url;
    /**
     * 模型，knowledgeConfig/knowledgeSplitConfig
     */
    private String model;
    private Content content;


    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = -3783603802533388479L;
        private String function;
        private String robot_id;
        private List<FileContent> file_content;
        private List<String> merge_split_approach;
        private RCTSConfig RCTS_config;
        private RCTSConfig CTS_config;
        //private RCTSConfig rcts_config;
        //private RCTSConfig cts_config;
        private Integer save_full_text_limit;
        private List<String> file_id_list;
        private List<String> file_id;
        private String user_message;
        private String file_text;
        private String file_title;
        private Integer top_n;
        private Integer prompt_max_tokens;
        private Integer n;
        private Boolean return_chunk;

    }


    /**
     * Represents the content of the file being uploaded.
     */
    @Data
    public static class FileContent {
        private String text;
        private String type;
        private Integer para_num;
        private Integer page_num;
        private String title;
        private FileInfo info;
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

    /**
     * Represents the configuration for the RCTS (Read, Chunk, Transform, Save) process.
     */
    @Data
    public static class RCTSConfig {

        private Integer chunk_size;
        private Integer chunk_overlap;
    }
}
