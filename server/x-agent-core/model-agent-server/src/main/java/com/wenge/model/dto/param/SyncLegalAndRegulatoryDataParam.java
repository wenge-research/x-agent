package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SyncLegalAndRegulatoryDataParam extends WgPageInfo {
    private static final long serialVersionUID = 3792411984799843035L;
    private String companyName;
    private Integer type;
    private String tab;

    private String start_url; //爬取页面路径
    private Integer tar_page = 1; //需要爬取的目标页数（一次只爬一页）
}
