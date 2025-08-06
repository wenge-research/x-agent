package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import com.wenge.model.entity.IntelligentTranslationFile;
import com.wenge.model.entity.IntelligentTranslationRecord;
import com.wenge.model.mapper.IntelligentTranslationFileMapper;
import com.wenge.model.mapper.es.IntelligentTranslationRecordMapper;
import com.wenge.model.service.IntelligentTranslationService;
import com.wenge.model.service.MatterGuideFiledService;
import com.wenge.model.utils.DateUtil;

import com.wenge.model.utils.translate.TranslateUtils;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.OauthDeptService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.result.YayiTranslationResult;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Random;

/**
 * @author: caohaifeng
 * @date: 2024/9/23 14:14
 * @Description: 智能翻译 实现类
 * @Version:1.0
 **/
@Service
@Slf4j
public class IntelligentTranslationServiceImpl implements IntelligentTranslationService {


    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private IntelligentTranslationFileMapper intelligentTranslationFileMapper;

    @Autowired
    private IntelligentTranslationRecordMapper intelligentTranslationRecordMapper;

    @Autowired
    private OauthDeptService oauthDeptService;

    @Autowired
    private MatterGuideFiledService matterGuideFiledService;

    @Autowired
    private WosUtil wosUtil;


    @Value("${appframe.minio.bucket}")
    private String bucket;

    /**
     * @param intelligentTranslationRecord
     * @description: 智能翻译接口
     * @author: caohaifeng
     * @date: 2024/9/23 13:52
     */
    @Override
    public Result translateTextOrFile(IntelligentTranslationRecord intelligentTranslationRecord) throws WosException {
        //文本翻译
        if("text".equals(intelligentTranslationRecord.getTranslateType())){
            return executeTranslateText(intelligentTranslationRecord);
        }else if("file".equals(intelligentTranslationRecord.getTranslateType())){
            return executeTranslateFile(intelligentTranslationRecord);
        }
        return Result.fail("暂不支持当前翻译类型");
    }

    /**
     * @param intelligentTranslationRecord
     * @description: 智能识别语种
     * @author: caohaifeng
     * @date: 2024/9/23 17:52
     */
    @Override
    public Result identifyLanguage(IntelligentTranslationRecord intelligentTranslationRecord) {
        if (StringUtils.isBlank(intelligentTranslationRecord.getText())) {
            return Result.fail("识别内容不能为空");
        }
        try {
            YayiTranslationResult yayiTranslationResult = yayiServer.yayiTranslate(intelligentTranslationRecord.getText(), intelligentTranslationRecord.getSrcLang(), intelligentTranslationRecord.getTgtLang());
            if (StringConstans.CODE.equals(yayiTranslationResult.getCode())) {
                YayiTranslationResult.TranslationData data = yayiTranslationResult.getData();
                if (null != data) {
                    String tgt = data.getTgt();
                    return Result.success(tgt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(StringConstant.BLANK);
    }



    /**
     *  执行文本翻译 智语翻译
     **/
    public Result executeTranslateTextNew(IntelligentTranslationRecord intelligentTranslationRecord){
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        intelligentTranslationRecord.setUniqueId(IdUtil.simpleUUID());
        if (StringUtils.isBlank(intelligentTranslationRecord.getText())|| StringUtils.isBlank(intelligentTranslationRecord.getTgtLang()) || StringUtils.isBlank(intelligentTranslationRecord.getSrcLang())) {
            return Result.success(StringConstant.BLANK);
        }
        if(intelligentTranslationRecord.getTgtLang().equals(intelligentTranslationRecord.getSrcLang())){
            return Result.success(intelligentTranslationRecord.getText());
        }
        try {
            com.alibaba.fastjson2.JSONObject jsonObject = TranslateUtils.getWengeTranslateContent(intelligentTranslationRecord.getText(), intelligentTranslationRecord.getSrcLang(), intelligentTranslationRecord.getTgtLang(), null);
            intelligentTranslationRecord.setResponseBody(jsonObject.toJSONString());
            if (jsonObject != null && jsonObject.get("status").equals("success")) {
                final com.alibaba.fastjson2.JSONObject data = jsonObject.getJSONObject("data");
                intelligentTranslationRecord.setTextTranslate(data.getString("tgt"));
                return Result.success(jsonObject);
            }else {
                intelligentTranslationRecord.setTextTranslate("FAIL-Translation"); //翻译失败
                return Result.fail(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (tokenUserInfo != null) {
                intelligentTranslationRecord.setUserId(tokenUserInfo.getId()+"");
                intelligentTranslationRecord.setUserName(tokenUserInfo.getUserName()+"");
                intelligentTranslationRecord.setDeptId(tokenUserInfo.getDeptId() + "");
                if (intelligentTranslationRecord.getDeptId() != null) {
                    final OauthDept deptDetail = oauthDeptService.getDeptDetail(intelligentTranslationRecord.getDeptId());
                    if (deptDetail != null) {
                        intelligentTranslationRecord.setDeptName(deptDetail.getDeptName());
                    }
                }
            }
            intelligentTranslationRecord.setCreateTime(DateUtil.getCurrentTime());
            intelligentTranslationRecordMapper.insert(intelligentTranslationRecord);
            log.info("写入翻译记录成功");
        }
        return Result.success(StringConstant.BLANK);
    }

    /**
     *  执行文本翻译
     **/
    private Result executeTranslateText(IntelligentTranslationRecord intelligentTranslationRecord){
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        intelligentTranslationRecord.setUniqueId(IdUtil.simpleUUID());
        if (StringUtils.isBlank(intelligentTranslationRecord.getText())|| StringUtils.isBlank(intelligentTranslationRecord.getTgtLang()) || StringUtils.isBlank(intelligentTranslationRecord.getSrcLang())) {
            return Result.success(StringConstant.BLANK);
        }
        if(intelligentTranslationRecord.getTgtLang().equals(intelligentTranslationRecord.getSrcLang())){
            return Result.success(intelligentTranslationRecord.getText());
        }
        try {
            com.alibaba.fastjson2.JSONObject jsonObject = TranslateUtils.getAliyunTranslateContent(intelligentTranslationRecord.getText(), intelligentTranslationRecord.getSrcLang(), intelligentTranslationRecord.getTgtLang(), null);
            intelligentTranslationRecord.setResponseBody(jsonObject.toJSONString());
            if (jsonObject != null && jsonObject.get("status").equals("success")) {
                final com.alibaba.fastjson2.JSONObject data = jsonObject.getJSONObject("data");
                intelligentTranslationRecord.setTextTranslate(data.getString("tgt"));
                return Result.success(data.getString("tgt"));
            }else {
                intelligentTranslationRecord.setTextTranslate("FAIL-Translation"); //翻译失败
                return Result.fail(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (tokenUserInfo != null) {
                intelligentTranslationRecord.setUserId(tokenUserInfo.getId()+"");
                intelligentTranslationRecord.setUserName(tokenUserInfo.getUserName()+"");
                intelligentTranslationRecord.setDeptId(tokenUserInfo.getDeptId() + "");
                if (intelligentTranslationRecord.getDeptId() != null) {
                    final OauthDept deptDetail = oauthDeptService.getDeptDetail(intelligentTranslationRecord.getDeptId());
                    if (deptDetail != null) {
                        intelligentTranslationRecord.setDeptName(deptDetail.getDeptName());
                    }
                }
            }
            intelligentTranslationRecord.setCreateTime(DateUtil.getCurrentTime());
            intelligentTranslationRecordMapper.insert(intelligentTranslationRecord);
            log.info("写入翻译记录成功");
        }
        return Result.success(StringConstant.BLANK);
    }

    /**
     *  执行文件翻译
     **/
    private Result executeTranslateFile(IntelligentTranslationRecord intelligentTranslationRecord) throws WosException {
        //读取文件 并记录文档
        MultipartFile file = intelligentTranslationRecord.getFile();
        if (file == null) {
            return Result.fail("文件不能为空");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        final String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        Random random = new Random();
        //上传文件
//        final MinioInfoResult minioInfoResult = matterGuideFiledService.uploadFile(file,
//                System.currentTimeMillis() + suffix);

        MinioInfoResult minioInfoResult = wosUtil.upload(bucket, "intelligentTranslation", file, Boolean.TRUE);

        if (minioInfoResult == null) {
            return Result.fail("文件上传失败，请重新导入");
        }
        IntelligentTranslationFile intelligentTranslationFile = getIntelligentTranslationFile(file);
        intelligentTranslationFile.setFileUrl(minioInfoResult.getUrlPath());
        intelligentTranslationFile.setCreateUserId(tokenUserInfo.getId() + "");
        intelligentTranslationFile.setCreateTime(DateUtil.getCurrentTime());
        intelligentTranslationFileMapper.insert(intelligentTranslationFile);
        log.info("文件上传记录落库成功...开始解析文件...");
        intelligentTranslationRecord.setFileUrl(intelligentTranslationFile.getFileUrl());

        IntelligentTranslationFile intelligentTranslationFileUpdate = IntelligentTranslationFile.create();
        intelligentTranslationFileUpdate.setId(intelligentTranslationFile.getId());

        try {
            //调用阿里云翻译文件
            com.alibaba.fastjson2.JSONObject jsonObject = TranslateUtils.getAliyunTranslateContent(null, intelligentTranslationRecord.getSrcLang(), intelligentTranslationRecord.getTgtLang(), intelligentTranslationFile.getFileUrl());
            if (jsonObject != null && jsonObject.get("status").equals("success")) {
                intelligentTranslationFileUpdate.setStatus(4);
                intelligentTranslationFileUpdate.setFileUrlTranslate(jsonObject.get("download_file_url") + "");
            }else {
                intelligentTranslationFileUpdate.setStatus(5);
                intelligentTranslationFileUpdate.setFileUrlTranslate(jsonObject.get("download_file_url") + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件翻译失败...");
            intelligentTranslationFileUpdate.setStatus(5);
            return Result.fail("文件翻译失败，请检查重试");
        }finally {
            intelligentTranslationFileUpdate.setUpdateTime(DateUtil.getCurrentTime());
            intelligentTranslationFileUpdate.setUpdateUserId(tokenUserInfo.getId() + "");
            intelligentTranslationFileMapper.update(intelligentTranslationFileUpdate);

            if (tokenUserInfo != null) {
                intelligentTranslationRecord.setUserId(tokenUserInfo.getId()+"");
                intelligentTranslationRecord.setUserName(tokenUserInfo.getUserName()+"");
                intelligentTranslationRecord.setDeptId(tokenUserInfo.getDeptId() + "");
                if (intelligentTranslationRecord.getDeptId() != null) {
                    final OauthDept deptDetail = oauthDeptService.getDeptDetail(intelligentTranslationRecord.getDeptId());
                    if (deptDetail != null) {
                        intelligentTranslationRecord.setDeptName(deptDetail.getDeptName());
                    }
                }
            }
            intelligentTranslationRecord.setFileUrlTranslate(intelligentTranslationFileUpdate.getFileUrlTranslate());
            intelligentTranslationRecord.setCreateTime(DateUtil.getCurrentTime());
            intelligentTranslationRecord.setFile(null);
            intelligentTranslationRecordMapper.insert(intelligentTranslationRecord);
            log.info("写入翻译记录成功");
        }
        return Result.success(intelligentTranslationFileUpdate);
    }


    private IntelligentTranslationFile getIntelligentTranslationFile(MultipartFile file) {
        IntelligentTranslationFile intelligentTranslationFile = IntelligentTranslationFile.create();
        intelligentTranslationFile.setFileId(IdUtil.simpleUUID());
        intelligentTranslationFile.setFileName(file.getOriginalFilename());
        int indexOf = file.getOriginalFilename().lastIndexOf(".");
        String type = file.getOriginalFilename().substring(indexOf + 1);
        intelligentTranslationFile.setFileType(type);
        intelligentTranslationFile.setFileSize(file.getSize());
        intelligentTranslationFile.setMultipartFile(file);
        intelligentTranslationFile.setStatus(1);
        intelligentTranslationFile.setDeletedFlag(0 + "");
        return intelligentTranslationFile;
    }

    @Override
    public SseEmitter executeTranslateTextWG(IntelligentTranslationRecord record,
                                             HttpServletRequest request) {
        SseEmitter sseEmitter = null;

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        record.setUniqueId(IdUtil.simpleUUID());
        if (StringUtils.isBlank(record.getText())|| StringUtils.isBlank(record.getTgtLang())
                || StringUtils.isBlank(record.getSrcLang())) {
            return null;
        }
        try {
             sseEmitter = TranslateUtils.getWengeTranslate(record);
             return sseEmitter;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tokenUserInfo != null) {
                record.setUserId(tokenUserInfo.getId()+"");
                record.setUserName(tokenUserInfo.getUserName()+"");
                record.setDeptId(tokenUserInfo.getDeptId() + "");
                if (record.getDeptId() != null) {
                    final OauthDept deptDetail = oauthDeptService.getDeptDetail(record.getDeptId());
                    if (deptDetail != null) {
                        record.setDeptName(deptDetail.getDeptName());
                    }
                }
            }
            record.setCreateTime(DateUtil.getCurrentTime());
            intelligentTranslationRecordMapper.insert(record);
            log.info("写入翻译记录成功");
        }
        return null;
    }
}
