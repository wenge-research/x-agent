package com.wenge.model.dto.param;


import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;

@Data

public class MyFavoriteQueryParam extends WgPageInfo {

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 收藏标识 0：未收藏，1：已收藏
     */
    private Integer favoriteFlag;

    /**
     * 类型
     */
    private Integer type;

}
