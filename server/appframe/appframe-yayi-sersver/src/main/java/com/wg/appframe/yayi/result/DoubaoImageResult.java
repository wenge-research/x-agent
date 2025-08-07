package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DoubaoImageResult implements Serializable {

    private static final long serialVersionUID = 1359789973376657244L;

    private String model;
    private Integer created;
    private List<Data> data;
    private Usage usage;
    private Error error;

    @lombok.Data
    public static class Data implements Serializable{

        private static final long serialVersionUID = 2822545562230024412L;

        private String url;
        private String b64_json;
    }

    @lombok.Data
    public static class Usage implements Serializable{

        private static final long serialVersionUID = -5252836220398393932L;

        private Integer generated_images;

    }

    @lombok.Data
    public static class Error implements Serializable{

        private static final long serialVersionUID = -8527800896128011589L;

        private String code;
        private String message;

    }
}
