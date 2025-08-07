package com.wg.appframe.wos.controller;

import cn.hutool.core.util.URLUtil;
import com.wg.appframe.wos.dto.params.UploadParam;
import com.wg.appframe.wos.dto.result.FileKeyParam;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.dto.result.StringResult;
import com.wg.appframe.wos.dto.result.WosResult;
import com.wg.appframe.wos.service.FileInfoService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.K;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;


/**
 * @author ziqian
 * @create 2022-01-14 18:30
 */
@RestController
@RequestMapping("/wos/file")
@ConditionalOnProperty(prefix = "appframe.minio", name = "enable", havingValue = "true")
public class FileController {

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 上传文件
     *
     * @param param        文件
     * @return
     */
    @PostMapping("/upload")
    public WosResult<List<MinioInfoResult>> upload(UploadParam param) {
        List<MultipartFile> files = new LinkedList<>();
        files.add(param.getFile());
        param.setFileList(files);
        return fileInfoService.uploadBatch(param);
    }


    /**
     * 上传文件
     *
     * @param param        文件
     * @return
     */
    @PostMapping("/uploadBatch")
    public WosResult<List<MinioInfoResult>> uploadBatch(UploadParam param) {
        List<MultipartFile> files = new LinkedList<>();
        files.add(param.getFile());
        param.setFileList(files);
        return fileInfoService.uploadBatch(param);
    }

    /**
     * 文件下载
     *
     * @return 返回
     */
    @GetMapping("/download")
    public void download(String fileKey, HttpServletResponse response) {
        fileInfoService.download(fileKey, response);
        //return WosResult.success(new StringResult());
    }

    @PostMapping("/delete")
    public WosResult<StringResult> delete(@RequestBody FileKeyParam param) {
        List<String> fileList = new LinkedList<>();
        fileList.add(param.getFileKey());
        return fileInfoService.deleteBatch(fileList);
    }

    @PostMapping("/deleteBatch")
    public WosResult<StringResult> deleteBatch(@RequestBody FileKeyParam param) {
        return fileInfoService.deleteBatch(param.getFileKeyList());
    }


    @PostMapping("/getFileInfo")
    public WosResult<MinioInfoResult> getFileInfo(@RequestBody FileKeyParam param) {
        return fileInfoService.getFileInfo(param.getFileKey());
    }

    /**
     * 文件下载
     *
     * @return 返回
     */
    @GetMapping("/download/**")
    public void downloadPath(HttpServletResponse response, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (StringUtils.isBlank(requestURI)) {
            return;
        }
        String mappingFreePath = "/download/";

        int indexOf = requestURI.indexOf(mappingFreePath);
        if (indexOf > 0) {
            String filePath = requestURI.substring(indexOf + mappingFreePath.length());
            filePath = URLUtil.decode(filePath);
            fileInfoService.download(filePath, response);
        }
    }

}
