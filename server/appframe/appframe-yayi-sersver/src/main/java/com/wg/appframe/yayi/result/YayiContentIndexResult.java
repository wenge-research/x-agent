package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiContentIndexResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 5831409942423758364L;
    private IndexData data;

    @Data
    public static class IndexData implements Serializable {

        private static final long serialVersionUID = 6715753608148676225L;

        private String res;
        private Map<String, Object> res_info;
    }
}
