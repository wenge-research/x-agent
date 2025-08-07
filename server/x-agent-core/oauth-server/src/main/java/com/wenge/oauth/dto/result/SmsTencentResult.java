package com.wenge.oauth.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 腾讯短信平台,接口文档：https://cloud.tencent.com/document/api/382/52077
 */
@Data
public class SmsTencentResult implements Serializable {

    private static final long serialVersionUID = 1663028119530869317L;

    private Response Response;


    @Data
    public static class Response implements Serializable {

        private static final long serialVersionUID = 51559516678441665L;

        private List<SendStatus> SendStatusSet;
        private Error Error;

    }

    @Data
    public static class Error implements Serializable {
        private String Code;
        private String Message;
    }

    @Data
    public static class SendStatus implements Serializable {

        private static final long serialVersionUID = -4834202403356216598L;

        private String SerialNo;
        private String PhoneNumber;
        private String Fee;
        private String SessionContext;
        private String Code;
        private String Message;
        private String IsoCode;
    }

}
