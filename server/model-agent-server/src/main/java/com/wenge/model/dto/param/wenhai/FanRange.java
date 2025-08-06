package com.wenge.model.dto.param.wenhai;

/**
 * FanRange, 粉丝数量范围入参【包头包尾】
 */
@lombok.Data
public class FanRange {
    /**
     * 起始粉丝数量【单位：个(包头)】
     */
    private Long fromFan;
    /**
     * 结束粉丝数量【单位：个(包尾)】
     */
    private Long toFan;
}
