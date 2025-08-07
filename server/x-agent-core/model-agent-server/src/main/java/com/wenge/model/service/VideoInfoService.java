package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.VideoInfoParam;
import com.wenge.model.dto.param.VideoUploadParam;
import com.wenge.model.dto.result.MediaResult;
import com.wenge.model.entity.VideoInfo;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * 视频信息服务接口
 */
public interface VideoInfoService extends IService<VideoInfo> {
    /**
     * 保存视频信息
     * 
     * @param videoInfo 视频信息
     * @return 是否保存成功
     */
    boolean saveVideoInfo(VideoInfo videoInfo);

    /**
     * 根据ID获取视频信息
     * 
     * @param id 视频ID
     * @return 视频信息
     */
    VideoInfo getVideoInfoById(Long id);

    /**
     * 更新视频信息
     * 
     * @param videoInfo 视频信息
     * @return 是否更新成功
     */
    boolean updateVideoInfo(VideoInfo videoInfo);

    /**
     * 删除视频信息
     * 
     * @param id 视频ID
     * @return 是否删除成功
     */
    boolean deleteVideoInfo(Long id);

    Result batchUploadByUrl(List<VideoUploadParam> params);

    /**
     * 通过视频标题返回视频列表
     * @param videoInfoParam
     * @return 分页查询视频信息
     */
    Result<Page<VideoInfo>> getVideoInfoList(VideoInfoParam videoInfoParam);

    /**
     * 根据视频ID修改审核状态与发布状态
     * @param videoInfoParam
     * return 修改是否成功
     */
    Result<VideoInfo> updateVideoInfoStatusByVideoId(VideoInfoParam videoInfoParam);

    /**
     * 根据视频ID获取视频信息
     * @param videoId 视频ID
     * @return 视频信息
     */
    Result<VideoInfo> getVideoInfoByVideoId(String videoId);


    /**
     * 获取视频和图片, displayScope=0展示首页，displayScope=1展示个人
     * @param videoInfoParam
     * @return
     */
    Result<Map<String, Page<MediaResult>>> getVideoAndImage(VideoInfoParam videoInfoParam);

    //Page<MediaResult> getPersonalHistory(VideoInfoParam videoInfoParam);

    //Page<MediaResult> getHomePageVideAndImage(VideoInfoParam videoInfoParam);

}