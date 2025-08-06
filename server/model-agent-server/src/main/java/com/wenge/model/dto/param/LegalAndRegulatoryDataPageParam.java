package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LegalAndRegulatoryDataPageParam extends WgPageInfo {
    private static final long serialVersionUID = 3792411984799843035L;
    private String companyName;
    private Integer type;
    private String tab;

    //标题关键字检索
    private String keyword;

    //发布时间
    private String startPushTime;
    private String endPushTime;
}
