package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SynonymWordPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3792411984799843035L;

    private String type;

    //关键字
    private String keyword;

    //时间
    private String startUpdateTime;
    private String endUpdateTime;
    private String id;
}
