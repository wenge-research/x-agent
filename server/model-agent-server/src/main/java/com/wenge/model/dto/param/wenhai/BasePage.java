package com.wenge.model.dto.param.wenhai;

/**
 * BasePage, 分页信息【最大可查前一万条数据(页数*每页条数不可大于一万条)】
 */
@lombok.Data
public class BasePage {
    /**
     * 当前页【默认第一页】
     */
    private Long currentPage;
    /**
     * 每页条数【默认每页10条,最大200条】
     */
    private Long pageSize;
}
