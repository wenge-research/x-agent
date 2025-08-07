package com.wg.appframe.core.dto.params;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JasyptParam implements Serializable {

    private static final long serialVersionUID = 7085818304393296545L;

    /**
     * 算法
     */
    private String algorithm;

    /**
     * 秘钥
     */
    private String password;

    /**
     * 内容
     */
    private String value;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 端口号
     */
    private String port;

    /**
     * 盐
     */
    private List<String> saltList;
}
