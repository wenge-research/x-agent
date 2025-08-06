package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InterceptWordHousePageParam extends WgPageInfo {

    //关键字
    private String keyword;

    //时间
    private String startCreateTime;
    private String endCreateTime;

    private String applicationId;

    private String createUser;

    /**
     * 拦截词创建者类型
     */
    private String ownerType;

    private Integer id;
}
