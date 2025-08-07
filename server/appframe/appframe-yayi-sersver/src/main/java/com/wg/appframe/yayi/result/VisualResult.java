package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class VisualResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 2139255862122926562L;

    private VisualData data;

    // Data类
    @Data
    public static class VisualData implements Serializable {

        private static final long serialVersionUID = 5132157250255496625L;

        private String content;
        private String role;
        private String type;
    }
}
