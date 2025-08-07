package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxVideoResult implements Serializable {

    private static final long serialVersionUID = -2679664349594998219L;

    private String task_id;
    private String status;
    private String download_url;
    private String file_id;
    private Integer video_width;
    private Integer video_height;
    private MinmaxCompletionResult.BaseResp base_resp; //	String	固定为 chat.completion	chat.completion

}
