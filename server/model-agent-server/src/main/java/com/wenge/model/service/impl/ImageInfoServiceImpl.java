package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ImageInfoParam;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wenge.model.entity.ImageInfo;
import com.wenge.model.mapper.ImageInfoMapper;
import com.wenge.model.service.ImageInfoService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.wenge.model.entity.table.ImageInfoTableDef.IMAGE_INFO;

@Service
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements ImageInfoService {

    @Autowired
    private UserService userService;
    @Autowired
    private ImageInfoMapper imageInfoMapper;


    // 图片类型正则表达式（忽略大小写）
    private static final Pattern IMAGE_TYPE_PATTERN = Pattern.compile("(?i)\\b(jpg|jpeg|png|gif|bmp|webp|svg|tiff|tif|ico|heic|avif)\\b");

    /**
     * 使用Hutool从URL中提取图片类型
     *
     * @param url 图片URL
     * @return 图片类型（小写），未找到时返回null
     */
    public static String extractImageType(String url) {
        if (url == null) return null;

        // 使用正则表达式查找第一个匹配的图片类型
        String match = ReUtil.get(IMAGE_TYPE_PATTERN, url, 0);
        return match != null ? match.toLowerCase() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ImageInfo> saveImageInfo(PromptGenerateParam param) {
        String urlPath = StringConstant.BLANK; // 图片路径
        Long fileSize = 0L;  // 图片大小
        String fileType = extractImageType(param.getImageUrl()); // 图片类型
        String fileName = IdUtil.simpleUUID() + "." + fileType; // 图片名称

        // 如果不是dibrain图片, 则下载图片
        if (!param.getImageUrl().contains("dibrain")) {
            InputStream inputStream = null;
            try {
                ApplicationContext context = CoreContextProvider.getContext();
                WosUtil wosUtil = context.getBean(WosUtil.class);
                Environment environment = context.getEnvironment();
                String bucket = environment.getProperty("appframe.minio.bucket");
                byte[] bytes = HttpUtil.downloadBytes(param.getImageUrl());
                inputStream = new ByteArrayInputStream(bytes);
                MinioInfoResult minioInfoResult = wosUtil.upload(bucket, "aiImage", inputStream, fileName, true);
                urlPath = minioInfoResult.getUrlPath();
                fileSize = minioInfoResult.getFileSize();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    java.io.File file = new java.io.File(param.getTopic());
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            byte[] bytes = HttpUtil.downloadBytes(param.getImageUrl());
            fileSize = (long) bytes.length;
            urlPath = param.getImageUrl();
        }

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageId(IdUtil.simpleUUID()); // 图片id
        imageInfo.setImageUrl(urlPath); // 图片地址
        imageInfo.setDescription(param.getDescription()); // 图片描述
        imageInfo.setWidth(param.getWidth()); // 图片宽度
        imageInfo.setHeight(param.getHeight()); // 图片高度
        imageInfo.setSize(fileSize); // 图片大小
        imageInfo.setTopic(param.getDescription()); // 图片主题
        imageInfo.setFormat(fileType); // 图片格式
        imageInfo.setRatio(param.getRatio()); // 图片比例
        save(imageInfo);
        return Result.success(imageInfo);
    }

    @Override
    public Result<ImageInfo> getImageInfoById(Long id) {
        ImageInfo imageInfo = getById(id);
        if (imageInfo != null && ObjectUtil.isNotEmpty(imageInfo)) {
            return Result.success(imageInfo);
        } else {
            return Result.fail("图片ID参数为空或错误");
        }
    }


    @Override
    public Result<Page<ImageInfo>> getImageInfoList(ImageInfoParam imageInfoParam) {
        // 参数校验
        if (imageInfoParam == null) {
            return Result.fail("请求参数为空");
        }
        if (imageInfoParam.getPageNo() == null || imageInfoParam.getPageNo() < 1) {
            return Result.fail("页面参数为空或错误");
        }
        if (imageInfoParam.getPageSize() == null || imageInfoParam.getPageSize() < 1) {
            return Result.fail("页面大小参数为空或错误");
        }

        // 分页参数
        int pageNo = imageInfoParam.getPageNo();
        int pageSize = imageInfoParam.getPageSize();

        // 查询条件
        QueryWrapper query = QueryWrapper.create()
                .and(IMAGE_INFO.PUBLISHED_STATUS.eq(1)) // 已发布
                .and(IMAGE_INFO.REVIEW_STATUS.eq(1))   // 审核通过
                .and(IMAGE_INFO.STATUS.eq(1))         // 启用
                .orderBy(IMAGE_INFO.CREATE_TIME.desc());   // 创建时间倒序排序


        // 标题模糊查询
        if (StringUtils.isNotBlank(imageInfoParam.getTopic())) {
            query.and(IMAGE_INFO.TOPIC.like(imageInfoParam.getTopic()));
        }

        // 分页查询
        Page<ImageInfo> page = imageInfoMapper.paginate(
                pageNo,
                pageSize,
                query
        );

        if (page != null && ObjectUtil.isNotEmpty(page)) {
            return Result.success(page);
        } else {
            return Result.fail("查询结果为空");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ImageInfo> updateImageInfoStatusByImageId(ImageInfoParam imageInfoParam) {
        // 参数校验
        if (imageInfoParam == null) {
            return Result.fail("请求参数为空");
        }

        // 校验图片是否存在
        Result<ImageInfo> updateImageInfoResult = getImageInfoByImageId(imageInfoParam.getImageId());
        ImageInfo updateImageInfo = updateImageInfoResult.getData();
        if (updateImageInfo == null || updateImageInfo.getDeletedFlag() == 1) {
            return Result.fail("图片不存在");
        }

        // 根据传入参数直接设置需要更新的字段
        if (imageInfoParam.getPublishedStatus() < 0 || imageInfoParam.getPublishedStatus() > 1) {
            return Result.fail("发布状态参数非法");
        }
        if (imageInfoParam.getReviewStatus() < 0 || imageInfoParam.getReviewStatus() > 2) {
            return Result.fail("审核状态参数非法");
        }

        // 修改图片发布与审核状态
        updateImageInfo.setReviewStatus(imageInfoParam.getReviewStatus());
        updateImageInfo.setPublishedStatus(imageInfoParam.getPublishedStatus());

        // 更新图片发布与审核状态
        int update = mapper.update(updateImageInfo);

        if (update == 0) {
            return Result.fail("更新状态失败");
        } else {
            return Result.success(updateImageInfo);
        }
    }

    @Override
    public Result<ImageInfo> getImageInfoByImageId(String imageId) {
        if (StringUtils.isBlank(imageId)) {
            return Result.fail("图片ID不能为空");
        }
        Wrappers<ImageInfo> wrappers = Wrappers.of(mapper)
                .where(IMAGE_INFO.IMAGE_ID.eq(imageId));
        ImageInfo imageInfo = getOne(wrappers);
        if (imageInfo != null && ObjectUtil.isNotEmpty(imageInfo)) {
            return Result.success(imageInfo);
        } else {
            return Result.fail("图片获取失败");
        }
    }
}
