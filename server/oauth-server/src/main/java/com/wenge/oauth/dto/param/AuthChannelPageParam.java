package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthChannelPageParam extends WgPageInfo {

    private static final long serialVersionUID = -643519520852339246L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 客户端类型
     */
    private String clientType;
}
