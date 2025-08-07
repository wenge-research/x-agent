package com.wg.appframe.wos.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * minio常用操作
 */
@Slf4j
public class WosUtil {

    @Autowired
    private MinioClient minioClient;

    @Value("${appframe.minio.gateway}")
    private String gateway;

    @Value("${spring.application.name}")
    private String appName;

    private static final String URL_PREFIX = "/wos/file/download?fileKey=";
    private static final String PATH_PREFIX = "/wos/file/download/";

    /**
     * 目前可支持加水印的文件类型
     */
    private static final List<String> FILE_TYPE_WATER_LIST = Arrays.asList(
            "jpg", "jpeg", "png", "bmp", "JPG", "PNG", ".PEG", "BMP", "docx", "xlsx", "pptx", "doc", "xls", "ppt", "pdf"
    );


    //获取文件流
    public InputStream getInput(String bucketName, String filename) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(filename).build());
    }

    //获取minio指定文件对象信息
    public StatObjectResponse getStatObject(String bucketName, String filename) {
        StatObjectResponse statObject = null;

        try {
            statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statObject;
    }

    //删除桶 --(不是空桶也删)
    public boolean removeBucket(String bucketName) {
        try {
            List<Object> folderList = this.getFolderList(bucketName);
            List<String> fileNames = new ArrayList<>();
            if (folderList != null && !folderList.isEmpty()) {
                for (int i = 0; i < folderList.size(); i++) {
                    Map o = (Map) folderList.get(i);
                    String name = (String) o.get("fileName");
                    fileNames.add(name);
                }
            }
            if (!fileNames.isEmpty()) {
                for (String fileName : fileNames) {
                    this.delete(bucketName, fileName);
                }
            }
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }


    /**
     * 复制一个附件到新位置
     *
     * @param bucketName     桶名称
     * @param sourceFilename 源文件
     * @return 文件路径名或者fail
     */
    public MinioInfoResult cpFile(String bucketName, String sourceFilename) {
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(sourceFilename).build());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            byte[] fileByte = bos.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileByte);
            //保留原文件的文件夹
            String objectName = sourceFilename.lastIndexOf("/") != -1 ? sourceFilename.substring(0, sourceFilename.lastIndexOf("/")) : "";
            return upload(bucketName, objectName, byteArrayInputStream, sourceFilename, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new MinioInfoResult();
    }

    //文件上传
    //public MinioInfoResult upload(String bucketName, InputStream inputStream, String fileName) {
    //    return upload(bucketName, "", inputStream, fileName);
    //}
    public MinioInfoResult upload(String bucketName, String filePath, InputStream inputStream, String fileName, boolean renameFlag) throws WosException {
        // 调整文件路径
        filePath = checkPath(filePath);
        MinioInfoResult minioInfoResult = new MinioInfoResult();
        minioInfoResult.setId(IdUtil.simpleUUID());
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        minioInfoResult.setFileType(suffix);
        if (renameFlag) {
            fileName = System.currentTimeMillis() + suffix;
            // filePath = filePath + "/" + System.currentTimeMillis() + suffix;
        }
        minioInfoResult.setFileStoreKey(filePath);

        if (StringUtils.isNotBlank(filePath)) {
            filePath = filePath + "/";
        }
        minioInfoResult.setFileName(fileName);

        //返回客户端文件系统中的原始文件名
        try {
            minioInfoResult.setFileSize(BigDecimal.valueOf(inputStream.available()).longValue());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath + fileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            String minioUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).method(Method.GET).build());
            minioInfoResult.setMinioUrl(minioUrl);
            String serverGateway = "";
            if (StringUtils.isNotBlank(gateway)) {
                serverGateway = gateway;
            } else if (StringUtils.isNotBlank(appName)) {
                serverGateway = "/" + appName;
            }
            minioInfoResult.setUrl(serverGateway + URL_PREFIX + filePath + fileName);
            minioInfoResult.setUrlPath(serverGateway + PATH_PREFIX + filePath + fileName);
            return minioInfoResult;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return minioInfoResult;
    }

    //文件下载
    public void download(HttpServletResponse response, String bucketName, String fileName) {
        InputStream inputStream = null;
        try {
            //获取文件源信息
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            //设置响应的内容类型 --浏览器对应不同类型做不同处理
            response.setContentType(statObject.contentType());
            //设置响应头
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName, "UTF-8"));
            //文件下载
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName)
                    .object(fileName).build());
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("download error : " + JSONUtil.toJsonStr(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String downloadBase64(String bucketName, String fileName) {
        InputStream inputStream = null;
        String encoded = "";
        try {
            //文件下载
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName)
                    .object(fileName).build());

            byte[] bytes = IoUtil.readBytes(inputStream);
            encoded = Base64.getEncoder().encodeToString(bytes);

        } catch (Exception e) {
            log.error("download error : " + JSONUtil.toJsonStr(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return encoded;
    }

    //文件删除
    public boolean delete(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName)
                    .object(fileName).build());
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    //桶是否存在
    public boolean bucketExists(String bucketName) {
        boolean b = false;
        try {
            b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (b) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    //创建桶
    public boolean createBucket(String bucketName) {
        try {
            boolean b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!b) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    //获取桶列表
    public List getBucketList() throws Exception {
        List<Bucket> buckets = minioClient.listBuckets();
        List list = new ArrayList();
        for (Bucket bucket : buckets) {
            String name = bucket.name();
            list.add(name);
        }
        return list;
    }

    //获取指定bucketName下所有文件 文件名+大小
    public List<Object> getFolderList(String bucketName) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        Iterator<Result<Item>> iterator = results.iterator();
        List<Object> items = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            items.add(JSONUtil.parseObj((String.format(format, item.objectName(),
                    formatFileSize(item.size())))));
        }
        return items;
    }

    /**
     * 讲快文件合并到新桶   块文件必须满足 名字是 0 1  2 3 5....
     *
     * @param bucketName  存块文件的桶
     * @param bucketName1 存新文件的桶
     * @param fileName1   存到新桶中的文件名称
     * @return
     */
    public boolean merge(String bucketName, String bucketName1, String fileName1) {
        try {
            List<ComposeSource> sourceObjectList = new ArrayList<ComposeSource>();
            List<Object> folderList = this.getFolderList(bucketName);
            List<String> fileNames = new ArrayList<>();
            if (folderList != null && !folderList.isEmpty()) {
                for (int i = 0; i < folderList.size(); i++) {
                    Map o = (Map) folderList.get(i);
                    String name = (String) o.get("fileName");
                    fileNames.add(name);
                }
            }
            if (!fileNames.isEmpty()) {
                Collections.sort(fileNames, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        if (Integer.parseInt(o2) > Integer.parseInt(o1)) {
                            return -1;
                        }
                        return 1;
                    }
                });
                for (String name : fileNames) {
                    sourceObjectList.add(ComposeSource.builder().bucket(bucketName).object(name).build());
                }
            }

            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName1)
                            .object(fileName1)
                            .sources(sourceObjectList)
                            .build());
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    //预览
    public void preview(HttpServletResponse response, String bucketName, String filename) {
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(filename).build());
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBase64(String bucketName, String filename) {
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(filename).build());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            byte[] fileByte = bos.toByteArray();
            //进行base64位加密
//            BASE64Encoder encoder = new BASE64Encoder();
            //拼接base64 图片加密后前缀

            String data = ImgUtil.toBase64DataUri(ImgUtil.toImage(fileByte), filename.substring(filename.lastIndexOf(".") + 1));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String imageChangeBase64(String bucketName, String filename) {
        InputStream inputStream = null;
        byte[] fileByte = null;
        try {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());
            //设置响应的内容类型 --浏览器对应不同类型做不同处理
            String s = statObject.contentType();

            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(filename).build());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            fileByte = bos.toByteArray();
            inputStream.close();
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            e.printStackTrace();
        }
        String data = ImgUtil.toBase64DataUri(ImgUtil.toImage(fileByte), filename.substring(filename.lastIndexOf(".") + 1));
        return data;
    }

    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
        }
        return fileSizeString;
    }

//    public String uploadResponse(String bucketName, String objectName, MultipartFile file) {
//        //返回客户端文件系统中的原始文件名
//        String originalFilename = file.getOriginalFilename();
//        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String fileName = objectName + "/" + System.currentTimeMillis() + suffix;
//        InputStream inputStream = null;
//        try {
//            inputStream = file.getInputStream();
//            ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(fileName)
//                    .stream(inputStream, file.getSize(), -1)
//                    .build());

    /// /            System.out.println(JSONObject.toJSONString(objectWriteResponse));
//            return fileName;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
    public MinioInfoResult upload(String bucketName, String filePath, MultipartFile file, boolean renameFlag) throws WosException {
        // 调整文件路径
        filePath = checkPath(filePath);
        MinioInfoResult minioInfoResult = new MinioInfoResult();
        minioInfoResult.setId(IdUtil.simpleUUID());
        //返回客户端文件系统中的原始文件名
        String originalFilename = file.getOriginalFilename();

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        minioInfoResult.setFileType(suffix.replaceFirst("\\.", ""));
        String fileName = originalFilename;
        // 重命名
        if (renameFlag) {
            fileName = System.currentTimeMillis() + suffix;
        }
        minioInfoResult.setFileName(fileName);
        minioInfoResult.setFileStoreKey(filePath);
        if (StringUtils.isNotBlank(filePath)) {
            filePath = filePath + "/";
        }
        minioInfoResult.setFileSize(file.getSize());
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath + fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType("application/octet-stream")
                    .build());

            String minioUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).method(Method.GET).build());
            minioInfoResult.setMinioUrl(minioUrl);
            String serverGateway = "";
            if (StringUtils.isNotBlank(gateway)) {
                serverGateway = gateway;
            } else if (StringUtils.isNotBlank(appName)) {
                serverGateway = "/" + appName;
            }

            minioInfoResult.setUrl(serverGateway + URL_PREFIX + filePath + fileName);
            minioInfoResult.setUrlPath(serverGateway + PATH_PREFIX + filePath + fileName);
            return minioInfoResult;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return minioInfoResult;
    }

    /**
     * 上传文件
     * @param bucketName
     * @param filePath
     * @param file
     * @param renameFlag
     * @return
     */
    public MinioInfoResult upload(String bucketName, String filePath, File file, boolean renameFlag) throws WosException {
        // 调整文件路径
        filePath = checkPath(filePath);
        MinioInfoResult minioInfoResult = new MinioInfoResult();
        minioInfoResult.setId(IdUtil.simpleUUID());
        //返回客户端文件系统中的原始文件名
        String originalFilename = file.getName();

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        minioInfoResult.setFileType(suffix.replaceFirst("\\.", ""));
        String fileName = originalFilename;
        // 重命名
        if (renameFlag) {
            fileName = System.currentTimeMillis() + suffix;
        }
        minioInfoResult.setFileName(fileName);
        // 上级文件夹
        if (StringUtils.isNotBlank(filePath)) {
            fileName = filePath + "/" + fileName;
        }
        minioInfoResult.setFileStoreKey(fileName);
        minioInfoResult.setFileSize(file.length());
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(file.toPath());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.length(), -1)
                    .contentType("application/octet-stream")
                    .build());

            String minioUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).method(Method.GET).build());
            minioInfoResult.setMinioUrl(minioUrl);
            String serverGateway = "";
            if (StringUtils.isNotBlank(gateway)) {
                serverGateway = gateway;
            } else if (StringUtils.isNotBlank(appName)) {
                serverGateway = "/" + appName;
            }
            minioInfoResult.setUrl(serverGateway + URL_PREFIX + fileName);
            minioInfoResult.setUrlPath(serverGateway + PATH_PREFIX + fileName);
            return minioInfoResult;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return minioInfoResult;
    }

    /**
     * 获取文件名
     *
     * @param fileUrl
     * @return
     */
    public String getFileNameByUrl(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return "";
        }

        // 提取类似这个文件名：https://city.wengegroup.com/smart-agent-api-demo/wos/file/download?fileKey=基本信息.xlsx
        if (fileUrl.contains(URL_PREFIX)) {
            return fileUrl.substring(fileUrl.lastIndexOf(URL_PREFIX) + URL_PREFIX.length());
        }
        // 提取类似这个文件名：https://city.wengegroup.com/smart-agent-api-demo/wos/file/download/基本信息.xlsx
        if (fileUrl.contains(PATH_PREFIX)) {
            return fileUrl.substring(fileUrl.lastIndexOf(PATH_PREFIX) + PATH_PREFIX.length());
        }
        return "";
    }

    /**
     * 检测路径
     * @param filePath
     * @return
     * @throws WosException
     */
    private String checkPath(String filePath) throws WosException {
        if (StringUtils.isBlank(filePath)) {
            throw new WosException("上传失败，文件路径不能为空");
        } else {
            while (true) {
                if (filePath.startsWith("/")) {
                    filePath = filePath.substring(1);
                    continue;
                }
                if (filePath.endsWith("/")) {
                    filePath = filePath.substring(0, filePath.length() - 1);
                    continue;
                }
                break;
            }
        }
        return filePath;
    }

    /**
     * 获取文件元信息
     * @param bucketName
     * @param objectName
     * @return
     * @throws Exception
     */
    public MinioInfoResult getObjectMetadata(String bucketName, String objectName) {
        MinioInfoResult result = null;
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            result = new MinioInfoResult();
            result.setFileName(objectName);
            result.setFileSize(stat.size());
            result.setLastModified(stat.lastModified().toString());
            result.setFileType(stat.contentType());
            result.setFileStoreKey(objectName);
            String serverGateway = "";
            if (StringUtils.isNotBlank(gateway)) {
                serverGateway = gateway;
            } else if (StringUtils.isNotBlank(appName)) {
                serverGateway = "/" + appName;
            }

            result.setUrl(serverGateway + URL_PREFIX + objectName);
            result.setUrlPath(serverGateway + PATH_PREFIX + objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取文件key
     * @param url
     * @return
     */
    public static String getFileKey(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }
        if (url.contains(URL_PREFIX)) {
            return url.split(URL_PREFIX)[1];
        } else if (url.contains(PATH_PREFIX)) {
            return url.split(PATH_PREFIX)[1];
        }
        return "";
    }
}
