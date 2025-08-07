package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ImageInfoParam;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wenge.model.entity.ImageInfo;
import com.wg.appframe.core.bean.Result;

public interface ImageInfoService extends IService<ImageInfo> {

    /**
     * 保存在首页用户生成的图片
     * @param param 生成的图片路径
     * @return
     */
    Result<ImageInfo> saveImageInfo(PromptGenerateParam param);

    /**
     * 根据ID获取图片信息
     * @param id 图片ID
     * @return 图片信息
     */
    Result<ImageInfo> getImageInfoById(Long id);

    /**
     * 查询发布过审的图片回显首页
     * @param imageInfoParam
     * @return 分页查询图片信息
     */
    Result<Page<ImageInfo>> getImageInfoList(ImageInfoParam imageInfoParam);

    /**
     * 更新图片信息的审核与发布状态
     * @param imageInfoParam
     * @return 修改状态是否成功
     */
    Result<ImageInfo> updateImageInfoStatusByImageId(ImageInfoParam imageInfoParam);

    /**
     * 通过图片id查询图片信息
     * @param imageId
     * @return 单张图片信息
     */
    Result<ImageInfo> getImageInfoByImageId(String imageId);
}