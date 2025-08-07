package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxMusicResult implements Serializable {

    private static final long serialVersionUID = 8245051801181413287L;

    private Data data;
    private MinmaxCompletionResult.BaseResp base_resp; //	String	固定为 chat.completion	chat.completion

    @lombok.Data
    public static class Data implements Serializable {

        private static final long serialVersionUID = 1198073355825906093L;

        private Integer status;
        private String audio;
    }

}
