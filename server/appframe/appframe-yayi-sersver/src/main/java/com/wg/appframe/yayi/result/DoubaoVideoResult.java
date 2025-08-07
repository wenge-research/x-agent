package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoubaoVideoResult implements Serializable {

    private static final long serialVersionUID = 193898257783005888L;

    private String id;
    private String model;
    private String status;
    private Content content;
    private Long created_at;
    private Long updated_at;

    @Data
    public static class Content implements Serializable {
        private static final long serialVersionUID = -7762220159443775255L;
        private String video_url;
    }
    @Data
    public static class Usage implements Serializable {
        private static final long serialVersionUID = -5429581187844465667L;
        private Long completion_tokens;
        private Long total_tokens;
    }
}
