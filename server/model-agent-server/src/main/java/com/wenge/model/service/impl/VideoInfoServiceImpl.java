package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.VideoInfoParam;
import com.wenge.model.dto.param.VideoUploadParam;
import com.wenge.model.dto.result.MediaResult;
import com.wenge.model.entity.VideoInfo;
import com.wenge.model.mapper.ImageInfoMapper;
import com.wenge.model.mapper.MediaResultMapper;
import com.wenge.model.mapper.VideoInfoMapper;
import com.wenge.model.service.ImageInfoService;
import com.wenge.model.service.VideoInfoService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


import static com.wenge.model.entity.table.ImageInfoTableDef.IMAGE_INFO;
import static com.wenge.model.entity.table.VideoInfoTableDef.VIDEO_INFO;

/**
 * 视频信息服务实现类
 */
@Slf4j
@Service
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo> implements VideoInfoService {


    @Autowired
    private WosUtil wosUtil;

    @Value("${appframe.minio.bucket:}")
    private String bucket;

    @Value("${multimedia.video.getVideoInfoApi:}")
    private String getVideoInfoApi;

    @Autowired
    private VideoInfoMapper videoInfoMapper;

    @Autowired
    private ImageInfoMapper imageInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageInfoService imageInfoService;

    @Autowired
    private MediaResultMapper mediaResultMapper;

    @Override
    public boolean saveVideoInfo(VideoInfo videoInfo) {
        return save(videoInfo);
    }

    @Override
    public VideoInfo getVideoInfoById(Long id) {
        return getById(id);
    }

    @Override
    public boolean updateVideoInfo(VideoInfo videoInfo) {
        return updateById(videoInfo);
    }

    @Override
    public boolean deleteVideoInfo(Long id) {
        return removeById(id);
    }

    @Override
    public Result batchUploadByUrl(List<VideoUploadParam> params) {
        if (CollectionUtil.isEmpty(params)) {
            return Result.fail("视频URL列表为空");
        }

        // 使用 hutool 下载文件
        List<VideoInfo> videoInfos = params.stream().map(info -> {
            InputStream inputStream = null;
            String fileName = StringConstant.BLANK;
            VideoInfo videoInfo = new VideoInfo();
            MinioInfoResult objectMetadata = null;
            try {
                // 如果是已经保存到智川平台，则不重复上传
                if (info.getVideoUrl().contains("wenge")) {
                    String fileKey = WosUtil.getFileKey(info.getVideoUrl());
                    objectMetadata = wosUtil.getObjectMetadata(bucket, fileKey);
                } else {
                    byte[] bytes = HttpUtil.downloadBytes(info.getVideoUrl());
                    inputStream = new ByteArrayInputStream(bytes);
                    // 这里可以进一步处理下载的字节流，例如保存 inputStream = new ByteArrayInputStream(bytes);
                    objectMetadata = wosUtil.upload(bucket, "aiAudio", inputStream, fileName, true);
                }

                videoInfo.setVideoUrl(info.getVideoUrl());
                videoInfo.setCoverUrl(info.getCoverUrl());
                // String post = HttpUtil.post(getVideoInfoApi, JSONUtil.toJsonStr(videoInfo));
                String post = HttpUtil.post(getVideoInfoApi, JSONUtil.toJsonStr(videoInfo));
                if (StringUtils.isNotBlank(post)) {
                    JSONObject entries = JSONUtil.parseObj(post);
                    if (entries.containsKey("data")) {
                        videoInfo = entries.getJSONObject("data").toBean(VideoInfo.class);
                    }
                }

                String[] split = info.getVideoUrl().split("\\?");
                int indexOf = split[0].lastIndexOf("/");
                fileName = split[0].substring(indexOf + 1);
                videoInfo.setVideoId(IdUtil.simpleUUID());
                videoInfo.setTitle(info.getDescription());
                videoInfo.setStatus(1);
                videoInfo.setContent(info.getDescription());
                videoInfo.setRatio(info.getRatio());
                if (null != objectMetadata) {
                    videoInfo.setSize(objectMetadata.getFileSize());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return videoInfo;
        }).collect(Collectors.toList());
        for(VideoInfo videoInfo : videoInfos){
            save(videoInfo);
        }
//        saveBatch(videoInfos);
        return Result.success();
    }

    @Override
    public Result<Page<VideoInfo>> getVideoInfoList(VideoInfoParam videoInfoParam) {

        // 参数校验
        if (videoInfoParam == null) {
            return Result.fail("参数不能为空");
        }
        if (videoInfoParam.getPageNo() == null || videoInfoParam.getPageNo() < 1){
            return Result.fail("页面参数为空或错误");
        }
        if (videoInfoParam.getPageSize() == null || videoInfoParam.getPageSize() < 1){
            return Result.fail("页面大小参数为空或错误");
        }

        int pageNo = videoInfoParam.getPageNo();
        int pageSize = videoInfoParam.getPageSize();

        // 视频搜索栏有输入->模糊查询->查询到会返回对应视频->查询不到会返回空列表; 视频搜索栏没有输入("")或null->返回全部
        Wrappers<VideoInfo> wrappers = Wrappers.of(mapper)
                .where(VIDEO_INFO.TITLE
                        .like(videoInfoParam.getTitle())
                        .when(StringUtils.isNotBlank(videoInfoParam.getTitle()))
                        .and(VIDEO_INFO.PUBLISHED_STATUS.eq(1))  // 已发布
                        .and(VIDEO_INFO.REVIEW_STATUS.eq(1))   // 审核通过
                        .and(VIDEO_INFO.STATUS.eq(1)))       // 启用
                .orderBy(VIDEO_INFO.CREATE_TIME.desc());  // 创建时间倒序排序

        Page<VideoInfo> page = page(Page.of(pageNo, pageSize), wrappers);
        if (page != null && ObjectUtil.isNotEmpty(page)) {
            return Result.success(page);
        } else {
            return Result.fail("查询结果为空");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<VideoInfo> updateVideoInfoStatusByVideoId(VideoInfoParam videoInfoParam) {

        // 参数校验
        if (videoInfoParam == null) {
            return Result.fail("参数不能为空");
        }

        // 校验视频是否存在
        Result<VideoInfo> videoInfoResult = getVideoInfoByVideoId(videoInfoParam.getVideoId());
        VideoInfo updateVideoInfo = videoInfoResult.getData();
        if (updateVideoInfo == null || updateVideoInfo.getDeletedFlag() == 1) {
            return Result.fail("视频不存在");
        }

        // 根据传入参数直接设置需要更新的字段
        if (videoInfoParam.getPublishedStatus() < 0 || videoInfoParam.getPublishedStatus() > 1) {
            return Result.fail("发布状态参数非法");
        }
        if (videoInfoParam.getReviewStatus() < 0 || videoInfoParam.getReviewStatus() > 2) {
            return Result.fail("审核状态参数非法");
        }
        updateVideoInfo.setReviewStatus(videoInfoParam.getReviewStatus());
        updateVideoInfo.setPublishedStatus(videoInfoParam.getPublishedStatus());

        // 更新视频发布状态
        int update = mapper.update(updateVideoInfo);
        if (update == 0) {
            return Result.fail("更新状态失败");
        } else {
            return Result.success(updateVideoInfo);
        }
    }


    @Override
    public Result<VideoInfo> getVideoInfoByVideoId(String videoId) {
        if (StringUtils.isBlank(videoId)) {
            throw new IllegalArgumentException("视频ID不能为空");
        }
        Wrappers<VideoInfo> wrappers = Wrappers.of(mapper)
                .where(VIDEO_INFO.VIDEO_ID.eq(videoId));
        VideoInfo videoInfo = getOne(wrappers);
        if (videoInfo != null) {
            return Result.success(videoInfo);
        } else {
            return Result.fail("视频获取失败");
        }
    }

    @Override
    public Result<Map<String, Page<MediaResult>>> getVideoAndImage(VideoInfoParam videoInfoParam) {
        if (videoInfoParam == null){
            return Result.fail("请求参数为空");
        }
        if (videoInfoParam.getPageNo() == null || videoInfoParam.getPageNo() < 1){
            return Result.fail("页面参数为空或错误");
        }
        if (videoInfoParam.getPageSize() == null || videoInfoParam.getPageSize() < 1){
            return Result.fail("页面大小参数为空或错误");
        }
        if (videoInfoParam.getDisplayScope() == null){
            return Result.fail("展示范围参数为空或错误");
        }
        int pageNo = videoInfoParam.getPageNo();
        int pageSize = videoInfoParam.getPageSize();
        Integer displayScope = videoInfoParam.getDisplayScope();
        if (displayScope == 0) { //展示首页图片视频
            long videoCount = count(
                    QueryWrapper.create()
                            .from(VIDEO_INFO)
                            .where(VIDEO_INFO.PUBLISHED_STATUS.eq(1))
                            .and(VIDEO_INFO.REVIEW_STATUS.eq(1))
                            .and(VIDEO_INFO.STATUS.eq(1))
            );
            long imageCount = count(
                    QueryWrapper.create()
                            .from(IMAGE_INFO)
                            .where(IMAGE_INFO.PUBLISHED_STATUS.eq(1))
                            .and(IMAGE_INFO.REVIEW_STATUS.eq(1))
                            .and(IMAGE_INFO.STATUS.eq(1))
            );
            long total = videoCount + imageCount;
            QueryWrapper videoAndImageQuery = QueryWrapper.create()
                    .select(
                            VIDEO_INFO.ID.as("id"),
                            VIDEO_INFO.TITLE.as("title"),
                            VIDEO_INFO.VIDEO_URL.as("url"),
                            VIDEO_INFO.SIZE.as("size"),
                            VIDEO_INFO.WIDTH.as("width"),
                            VIDEO_INFO.HEIGHT.as("height"),
                            VIDEO_INFO.DURATION.as("duration"),
                            VIDEO_INFO.CREATE_TIME.as("createTime"),
                            VIDEO_INFO.UPDATE_TIME.as("updateTime"),
                            VIDEO_INFO.PUBLISHED_STATUS.as("publishedStatus"),
                            VIDEO_INFO.REVIEW_STATUS.as("reviewStatus"),
                            VIDEO_INFO.RATIO.as("ratio"),
                            VIDEO_INFO.RESOLUTION.as("resolution"),
                            VIDEO_INFO.CONTENT.as("description"),
                            VIDEO_INFO.CREATE_USER_NAME.as("createUserName"),
                            VIDEO_INFO.MEDIA_TYPE.as("mediaType") // 1=视频
                    ).from(VIDEO_INFO)
                    .where(VIDEO_INFO.PUBLISHED_STATUS.eq(1))
                    .and(VIDEO_INFO.REVIEW_STATUS.eq(1))
                    .and(VIDEO_INFO.STATUS.eq(1))
                    .unionAll(
                            QueryWrapper.create()
                                    .select(
                                            IMAGE_INFO.ID.as("id"),
                                            IMAGE_INFO.TOPIC.as("title"),
                                            IMAGE_INFO.IMAGE_URL.as("url"),
                                            IMAGE_INFO.SIZE.as("size"),
                                            IMAGE_INFO.WIDTH.as("width"),
                                            IMAGE_INFO.HEIGHT.as("height"),
                                            IMAGE_INFO.DELETED_FLAG.as("duration"), // 图片没有时长,给个0
                                            IMAGE_INFO.CREATE_TIME.as("createTime"),
                                            IMAGE_INFO.UPDATE_TIME.as("updateTime"),
                                            IMAGE_INFO.PUBLISHED_STATUS.as("publishedStatus"),
                                            IMAGE_INFO.REVIEW_STATUS.as("reviewStatus"),
                                            IMAGE_INFO.RATIO.as("ratio"),
                                            IMAGE_INFO.RESOLUTION.as("resolution"),
                                            IMAGE_INFO.DESCRIPTION.as("description"),
                                            IMAGE_INFO.CREATE_USER_NAME.as("createUserName"),
                                            IMAGE_INFO.MEDIA_TYPE.as("mediaType")// 2=图片
                                    )
                                    .from(IMAGE_INFO)
                                    .where(IMAGE_INFO.PUBLISHED_STATUS.eq(1))
                                    .and(IMAGE_INFO.REVIEW_STATUS.eq(1))
                                    .and(IMAGE_INFO.STATUS.eq(1))
                    );
            List<MediaResult> mediaResults = mediaResultMapper.selectListByQuery(videoAndImageQuery);
            mediaResults.sort(Comparator.comparing(MediaResult::getCreateTime).reversed());
            // 古法分页
            Page<MediaResult> page = getPage(pageNo, pageSize, mediaResults, total);
            HashMap<String, Page<MediaResult>> result = new HashMap<>();
            result.put("all", page);
            if (ObjectUtil.isNotEmpty(result)) {
                return Result.success(result);
            } else {
                return Result.fail("查询结果为空");
            }
        } else if (videoInfoParam.getDisplayScope() == 1) { // 展示个人历史图片视频
            // 用户与id校验
            Long userId = AppContextHolder.getTokenUserInfo().getId();
            if (null == userId || ObjectUtil.isEmpty(userId)) {
                return Result.fail("用户id为空");
            }
            OauthUser user = userService.getById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }

//            long videoCount = count(
//                    QueryWrapper.create()
//                            .select().from(VIDEO_INFO)
//                            .and(VIDEO_INFO.CREATE_USER_ID.eq(userId)));
//            long imageCount = count(
//                    QueryWrapper.create()
//                            .select().from(IMAGE_INFO)
//                            .and(IMAGE_INFO.CREATE_USER_ID.eq(userId)));
//            long total = videoCount + imageCount;

            QueryWrapper videoAndImageQuery = QueryWrapper.create()
                    .select(
                            VIDEO_INFO.ID.as("id"),
                            VIDEO_INFO.TITLE.as("title"),
                            VIDEO_INFO.VIDEO_URL.as("url"),
                            VIDEO_INFO.SIZE.as("size"),
                            VIDEO_INFO.WIDTH.as("width"),
                            VIDEO_INFO.HEIGHT.as("height"),
                            VIDEO_INFO.DURATION.as("duration"),
                            VIDEO_INFO.CREATE_TIME.as("createTime"),
                            VIDEO_INFO.UPDATE_TIME.as("updateTime"),
                            VIDEO_INFO.PUBLISHED_STATUS.as("publishedStatus"),
                            VIDEO_INFO.REVIEW_STATUS.as("reviewStatus"),
                            VIDEO_INFO.RATIO.as("ratio"),
                            VIDEO_INFO.RESOLUTION.as("resolution"),
                            VIDEO_INFO.CONTENT.as("description"),
                            VIDEO_INFO.MEDIA_TYPE.as("mediaType") // 1=视频
                    ).from(VIDEO_INFO)
                    .and(VIDEO_INFO.CREATE_USER_ID.eq(userId))
                    .unionAll(
                            QueryWrapper.create()
                                    .select(
                                            IMAGE_INFO.ID.as("id"),
                                            IMAGE_INFO.TOPIC.as("title"),
                                            IMAGE_INFO.IMAGE_URL.as("url"),
                                            IMAGE_INFO.SIZE.as("size"),
                                            IMAGE_INFO.WIDTH.as("width"),
                                            IMAGE_INFO.HEIGHT.as("height"),
                                            IMAGE_INFO.DELETED_FLAG.as("duration"), // 图片没有时长,给个0
                                            IMAGE_INFO.CREATE_TIME.as("createTime"),
                                            IMAGE_INFO.UPDATE_TIME.as("updateTime"),
                                            IMAGE_INFO.PUBLISHED_STATUS.as("publishedStatus"),
                                            IMAGE_INFO.REVIEW_STATUS.as("reviewStatus"),
                                            IMAGE_INFO.RATIO.as("ratio"),
                                            IMAGE_INFO.RESOLUTION.as("resolution"),
                                            IMAGE_INFO.DESCRIPTION.as("description"),
                                            IMAGE_INFO.MEDIA_TYPE.as("mediaType")// 2=图片
                                    ).from(IMAGE_INFO)
                                    .and(IMAGE_INFO.CREATE_USER_ID.eq(userId))
                    );
            List<MediaResult> mediaResults = mediaResultMapper.selectListByQuery(videoAndImageQuery);
            mediaResults.sort(Comparator.comparing(MediaResult::getCreateTime).reversed());
            // 现在
            Date now = new Date();
            // 近一周时间点：当前时间减去7天
            Calendar weekAgo = Calendar.getInstance();
            weekAgo.add(Calendar.DAY_OF_MONTH, -7);
            Date oneWeekAgo = weekAgo.getTime();
            // 近一月时间点：当前时间减去30天
            Calendar monthAgo = Calendar.getInstance();
            monthAgo.add(Calendar.DAY_OF_MONTH, -30);
            Date oneMonthAgo = monthAgo.getTime();
            // 近一周数据
            List<MediaResult> recentWeekList = filterByCreateTime(mediaResults, oneWeekAgo, now);
            Page<MediaResult> weekPage = (Page<MediaResult>) getPage(pageNo, pageSize, recentWeekList, recentWeekList.size());
            // 近一月数据(不包括近一周)
            List<MediaResult> recentMonthList = filterByCreateTime(mediaResults, oneMonthAgo, oneWeekAgo);
            Page<MediaResult> monthPage = (Page<MediaResult>) getPage(pageNo, pageSize, recentMonthList, recentMonthList.size());
            // 数据打包
            Map<String, Page<MediaResult>> timeOrderResult = new HashMap<>();
            timeOrderResult.put("recentWeek", weekPage);
            timeOrderResult.put("recentMonth", monthPage);

            // 古法分页,返回全部审核发布数据
            // Page<MediaResult> page = getPage(pageNo, pageSize, mediaResults, total);

            if (ObjectUtil.isNotEmpty(timeOrderResult)) {
                return Result.success(timeOrderResult);
            } else {
                return Result.fail("查询结果为空");
            }
        } else {
            return Result.fail("展示范围参数错误");
        }
    }

    private List<MediaResult> filterByCreateTime(List<MediaResult> data, Date startTime, Date endTime) {
        return data.stream()
                .filter(item -> item.getCreateTime().after(startTime) && item.getCreateTime().before(endTime))
                .collect(Collectors.toList());
    }

    private static <T> Page<T> getPage(int pageNo, int pageSize, List<T> dataList, long total) {
        int start = (pageNo - 1) * pageSize;
        int end = Math.min(start + pageSize, dataList.size());
        List<T> curPageData = dataList.subList(start, end);
        long totalPage = (total + pageSize - 1) / pageSize;
        return new Page<>(curPageData, pageNo, pageSize, totalPage);
    }
}
