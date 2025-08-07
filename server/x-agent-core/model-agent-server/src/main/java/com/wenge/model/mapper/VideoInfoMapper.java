package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.entity.VideoInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频信息Mapper接口
 */
@Mapper
public interface VideoInfoMapper extends BaseMapper<VideoInfo> {
}