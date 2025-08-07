package com.wg.appframe.wos.service;

import com.wg.appframe.wos.dto.params.UploadParam;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.dto.result.StringResult;
import com.wg.appframe.wos.dto.result.WosResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author huangzhigang
 * @create 2022-12-08 21:34
 */
public interface FileInfoService {

    /**
     * 批量上传
     * @param param 文件集合
     * @return 返回
     */
    WosResult<List<MinioInfoResult>> uploadBatch(UploadParam param);

    WosResult<StringResult> download(String fileKey, HttpServletResponse response);

    WosResult<StringResult> deleteBatch(List<String> fileList);

    WosResult<MinioInfoResult> getFileInfo(String fileKey);
//
//    /**
//     * 下载文件
//     * @param fileName fileName
//     * @param response response
//     * @param bucketName bucketName
//     * @return 返回
//     */
//    void download(String fileName, HttpServletResponse response, String bucketName);
//
//    /**
//     * 查询
//     *
//     * @param bucketName bucketName
//     * @return 返回
//     */
//    List<BaseFileEntity> query(String bucketName);
//
//    /**
//     * 根据文件名查询
//     *
//     * @param bucketName bucketName
//     * @param fileName fileName
//     * @return 返回
//     */
//    BaseFileEntity queryByName(String bucketName, String fileName);
//
//    /**
//     * 批量删除
//     *
//     * @param fileNames fileNames
//     * @param bucketName bucketName
//     * @return 返回
//     */
//    boolean deleteBatch(List<String> fileNames, String bucketName);


}
