package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DialogueFilterResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -3235217653127705014L;
    private Data data;

    @lombok.Data
    public static class Data implements Serializable {

        private static final long serialVersionUID = -3734399052373345024L;

        private List<Result> result;
    }

    @lombok.Data
    public static class Result implements Serializable {

        private static final long serialVersionUID = -5546704108407174644L;

        private String role;
        private String content;
    }

}
