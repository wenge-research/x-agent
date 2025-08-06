package com.wenge.model.service;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.FileAddParam;
import com.wenge.model.dto.param.FileDeleteParam;
import com.wenge.model.dto.param.FilePageParam;
import com.wenge.model.dto.param.ImageAnalysisParam;
import com.wenge.model.entity.File;
import com.wg.appframe.core.bean.Result;

import java.io.IOException;

public interface MultimediaService extends IService<File> {

    /**
     * 新增多媒体文件: 音频 视频 图片
     */
    Result addFile(FileAddParam param);

    /**
     * 删除文件
     */
    Result deleteFile(FileDeleteParam param);

    /**
     * 获取文件列表
     */
    Result<Page<File>> getFileList(FilePageParam file);

    /**
     * 更新文件
     */
    Result updateFile(File file);

    /**
     * 分析文件
     */
    String analysis(File file) throws IOException;

    /**
     * 查询文件详情
     */
    Result<File> getByFileId(String fileId);

    /**
     * 图片解析
     */
    Result analysisImage(ImageAnalysisParam param);
}
