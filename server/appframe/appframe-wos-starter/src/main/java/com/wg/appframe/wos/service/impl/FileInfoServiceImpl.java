package com.wg.appframe.wos.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wg.appframe.wos.dto.params.UploadParam;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.dto.result.StringResult;
import com.wg.appframe.wos.dto.result.WosResult;
import com.wg.appframe.wos.service.FileInfoService;
import com.wg.appframe.wos.utils.WosUtil;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

/**
 * @author ziqian
 * @create 2022-01-12 21:35
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "appframe.minio", name = "enable", havingValue = "true")
public class FileInfoServiceImpl implements FileInfoService {

    @Value("${appframe.minio.bucket:}")
    private String bucket;

    @Autowired
    private WosUtil wosUtil;

    @Override
    public WosResult<List<MinioInfoResult>> uploadBatch(UploadParam param) {
        List<MinioInfoResult> resultList = ListUtil.toList();
        if (CollectionUtil.isEmpty(param.getFileList())) {
            return WosResult.success(resultList);
        }

        for (MultipartFile multipartFile : param.getFileList()) {
            try {
                MinioInfoResult MinioInfoResult = wosUtil.upload(bucket, param.getFilePath(), multipartFile, param.getRename());
                resultList.add(MinioInfoResult);
            } catch (Exception e) {
                log.error("batch upload file found error.", e);
            }
        }
        return WosResult.success(resultList);

    }

    @Override
    public WosResult<StringResult> download(String fileKey, HttpServletResponse response) {
        wosUtil.download(response, bucket, fileKey);
        return WosResult.success(new StringResult());
    }

    @Override
    public WosResult<StringResult> deleteBatch(List<String> fileList) {
        if (CollectionUtil.isNotEmpty(fileList)) {
            for (String fileName : fileList) {
                wosUtil.delete(bucket, fileName);
            }
        }
        return WosResult.success(new StringResult());
    }

    @Override
    public WosResult<MinioInfoResult> getFileInfo(String fileKey) {
        MinioInfoResult objectMetadata = wosUtil.getObjectMetadata(bucket, fileKey);
        return WosResult.success(objectMetadata);
    }


//    @Override
//    public void download(String fileName, HttpServletResponse response, String bucketName) {
//        if (StringUtils.isBlank(fileName)) {
//            throw new BizException(ResultCode.FILES_NAME_EMPTY);
//        }
//        try {
//            if (!wosClient.objectExists(bucketName, fileName)) {
//                throw new BizException(ResultCode.FILES_NOT_EXIST);
//            }
//            InputStream in = wosClient.getObject(bucketName, fileName);
//            FileUtils.downloadFile(response, in, fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GlobalException(ResultCode.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @Override
//    public List query(String bucketName) {
//        List<BaseFileEntity> resultFileEntity = new LinkedList<>();
//        try {
//            Iterable<Result<Item>> files = wosClient.listObjects(bucketName);
//            for (Result<Item> result : files) {
//                Item file = result.get();
//                String fileName = file.objectName();
//                Date lastModify = file.lastModified();
//                long fileSize = file.objectSize();
//                String url = wosClient.getObjectUrl(bucketName, fileName);
//                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
//                BaseFileEntity fileObject = convert2BaseFileEntity(fileName, url, fileSize, fileType, lastModify,
//                        bucketName, fileName);
//                resultFileEntity.add(fileObject);
//            }
//        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
//                 | NoResponseException | InternalException | InvalidResponseException | ErrorResponseException
//                 | XmlPullParserException | IOException e) {
//            e.printStackTrace();
//            throw new GlobalException(ResultCode.INTERNAL_SERVER_ERROR);
//        }
//        return resultFileEntity;
//        return null;
//    }
//
//    @Override
//    public BaseFileEntity queryByName(String bucketName, String fileName) {
//        if (StringUtils.isBlank(fileName)) {
//            throw new BizException(ResultCode.FILES_NAME_EMPTY);
//        }
//        List<BaseFileEntity> resultFileEntity = new LinkedList<>();
//        try {
//            Iterable<Result<Item>> files = wosClient.listObjects(bucketName, fileName);
//            for (Result<Item> result : files) {
//                Item file = result.get();
//                Date lastModify = file.lastModified();
//                long fileSize = file.objectSize();
//                String url = wosClient.getObjectUrl(bucketName, fileName);
//                String fileType = fileName.substring(fileName.lastIndexOf("."));
//                BaseFileEntity fileObject = convert2BaseFileEntity(fileName, url, fileSize, fileType, lastModify,
//                        bucketName, fileName);
//                resultFileEntity.add(fileObject);
//            }
//        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
//                 | NoResponseException | InternalException | InvalidResponseException | ErrorResponseException
//                 | XmlPullParserException | IOException e) {
//            e.printStackTrace();
//            throw new GlobalException(ResultCode.INTERNAL_SERVER_ERROR);
//        }
//        return resultFileEntity.get(0);
//    }
//
//    @Override
//    public boolean deleteBatch(List<String> fileNames, String bucketName) {
//        if (CollectionUtils.isEmpty(fileNames)) {
//            throw new BizException(ResultCode.FILES_NAME_EMPTY);
//        }
//        boolean success = true;
//        try {
//
//            if (!wosClient.objectExists(bucketName, fileNames.get(0))) {
//                throw new BizException(ResultCode.FILES_NOT_EXIST);
//            }
//
//            Iterable<Result<DeleteError>> ss = wosClient.removeObjects(bucketName, fileNames);
//            for (Result<DeleteError> result : ss) {
//                log.info(result.get().name);
//            }
//
//        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
//                 | NoResponseException | InternalException | InvalidResponseException | InvalidArgumentException
//                 | ErrorResponseException | XmlPullParserException | IOException e) {
//            e.printStackTrace();
//            success = false;
//            throw new GlobalException(ResultCode.INTERNAL_SERVER_ERROR);
//        }
//
//        return success;
//    }
//
//    private BaseFileEntity<Object> setGwUrl(String gwUrl, BaseFileEntity<Object> baseFileEntity) {
//        baseFileEntity.setGwUrl(gwUrl);
//        return baseFileEntity;
//    }
//
//    private BaseFileEntity<Object> convert2BaseFileEntity(String fileName, String url, long fileSize, String fileType,
//                                                          Date lastModify, String bucketName, String fileStoreKey) {
//        BaseFileEntity<Object> baseFileEntity = new BaseFileEntity<>();
//        baseFileEntity.setFileName(fileName);
//        baseFileEntity.setUrl(url);
//        baseFileEntity.setFileType(fileType);
//        baseFileEntity.setLastModified(lastModify);
//        baseFileEntity.setFileSize(fileSize);
//        baseFileEntity.setStoreBucketKey(bucketName);
//        baseFileEntity.setFileStoreKey(fileStoreKey);
//        return baseFileEntity;
//    }


    /**
     * 将文件资源转化为MD5字符串
     *
     * @param is 文件字节流
     * @return MD5 资源唯一标识字符串
     * @author dangwei
     */
    public static String getFileMD5(InputStream is) {
        String md5String = "";
        try {
            MessageDigest mMessageDigest = null;
            mMessageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = is.read(buffer, 0, 1024)) > 0) {
                mMessageDigest.update(buffer, 0, length);
            }
            md5String = new BigInteger(1, mMessageDigest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return md5String;
    }
}
