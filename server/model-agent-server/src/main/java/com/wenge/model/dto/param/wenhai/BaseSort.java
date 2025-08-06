package com.wenge.model.dto.param.wenhai;

/**
 * BaseSort, 排序信息
 */
@lombok.Data
public class BaseSort {
    /**
     *
     * 排序类型【默认发布时间排序,1-发布时间、2-阅读量、3-转发量、4-评论量、5-点赞量、6-播放量、7-热度、8-在看量、9-采集时间、10-相关度排序、11-Gid排序、12-综合排序、13-入ES时间、14-累加互动量、15-收藏量、16-弹幕量、17-投币量、18-加权互动量、19-表情_总表情数】
     */
    private Long sortField;
    /**
     * 排序方式【默认倒序,asc-正序、desc-倒序】
     */
    private String sortWay;
}
