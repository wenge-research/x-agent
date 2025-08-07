package com.wenge.model.dto.param;

import lombok.Data;

@Data
public class UserInfoParam {

    private static final long serialVersionUID = 7408849922010567565L;

    /**
     * 大学城用户id
     */
    private String userId;

    /**
     * 应用ID
     */
    private String applicationId;

}
