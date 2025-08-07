package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DoubaoTokenizationResult implements Serializable {

    private static final long serialVersionUID = 2647381761563331510L;

    private String id;
    private String model;
    private Integer created;
    private String object;
    private List<Data> data;

    @lombok.Data
    public static class  Data implements  Serializable{
        private static final long serialVersionUID = 2647381761563331510L;
        private String object;
        private Integer index;
        private Integer total_tokens;
        private List<Integer> token_ids;
        private List<List<Integer>> offset_mapping;
    }
}
