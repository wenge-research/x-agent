package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxFileResult implements Serializable {

    private static final long serialVersionUID = 446499877268168607L;

    private File file;
    private MinmaxCompletionResult.BaseResp base_resp; //	String	固定为 chat.completion	chat.completion


    @Data
    public static class File implements Serializable {

        private static final long serialVersionUID = -7885105017521148222L;
        private String download_url;
        private String file_id;
        private Integer bytes;
        private String created_at;
        private String filename;
        private String purpose;
    }
}
