package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InterceptWordPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3792411984799843035L;

    private String type;

    //关键字
    private String keyword;

    private String status;

    private Long interceptWordHouseId;

    //时间
    private String startCreateTime;
    private String endCreateTime;

    private String interceptWordId;

}
