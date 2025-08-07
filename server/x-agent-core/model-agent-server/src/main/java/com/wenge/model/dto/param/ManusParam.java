package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManusParam extends WGParam {

    private static final long serialVersionUID = 8604237307546790284L;

    /**
     * 用户问题
     */
    private String question;

    /**
     * 客户端 id
     */
    private String clientId;

    /**
     * 应用 id
     */
    private String applicationId;
}
