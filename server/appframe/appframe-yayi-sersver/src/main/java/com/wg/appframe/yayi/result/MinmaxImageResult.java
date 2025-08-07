package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MinmaxImageResult implements Serializable {

    private static final long serialVersionUID = -579729328079226098L;

    private Data data;
    private Metadata metadata;
    private MinmaxCompletionResult.BaseResp base_resp; //	String	固定为 chat.completion	chat.completion

    @lombok.Data
    public static class Data implements Serializable {

        private static final long serialVersionUID = 2509398545374047724L;

        private List<String> image_urls;
        private List<String> image_base64;
    }


    @lombok.Data
    public static class Metadata implements Serializable {

        private static final long serialVersionUID = 3944063501217151214L;
        private String success_count;
        private String failed_count;
    }
}
