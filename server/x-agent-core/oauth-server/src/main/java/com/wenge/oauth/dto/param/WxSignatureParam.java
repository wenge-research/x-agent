package com.wenge.oauth.dto.param;

import lombok.Data;



@Data
public class WxSignatureParam {

    /**
     * url
     **/
    private String url;

    /**
     * 应用id
     **/
    private String applicationId;

}
