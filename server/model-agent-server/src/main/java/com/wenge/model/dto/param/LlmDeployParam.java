package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LlmDeployParam extends WGParam {

    private static final long serialVersionUID = -6774964609914108014L;

    /**
     * 平台，window linux macos
     */
    private String platform;

    /**
     * 厂商
     */
    private String manufacturer;

    /**
     * 模型型号,
     * 1.5b:1_5b 7b:7b 8b:8b
     */
    private String modelVersion;

    /**
     * 服务器 ip
     */
    private String hostIp;

    /**
     * 服务器用户名
     */
    private String hostUser;

    /**
     * 服务器密码
     */
    private String hostPw;

    /**
     * 服务器端口,默认22
     */
    private Integer hostPort;

}
