package com.wenge.model.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ApplicationInfoDialogueParam;
import com.wenge.model.entity.ApplicationInfoDialogueRecord;
import com.wenge.model.mapper.ApplicationInfoDialogueRecordMapper;
import com.wenge.model.service.ApplicationInfoDialogueRecordService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
@Service
@Slf4j
public class ApplicationInfoDialogueRecordImpl extends ServiceImpl<ApplicationInfoDialogueRecordMapper, ApplicationInfoDialogueRecord> implements ApplicationInfoDialogueRecordService {
    @Resource
    private UserService userService;
    @Override
    public Result saveApplicationInfoDialogue(ApplicationInfoDialogueParam applicationInfoDialogueParam) {
        ApplicationInfoDialogueRecord applicationInfoDialogueRecord = new ApplicationInfoDialogueRecord();
        BeanUtils.copyProperties(applicationInfoDialogueParam, applicationInfoDialogueRecord);
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        Long currentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用Date构造函数，传入时间戳
        Date date = new Date(currentTimeMillis);
        String formattedDate = sdf.format(date);
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            applicationInfoDialogueRecord.setCreateTime(formattedDate);
            applicationInfoDialogueRecord.setUpdateTime(formattedDate);
            applicationInfoDialogueRecord.setCreateUser(oauthUser.getCreateUser());
            applicationInfoDialogueRecord.setUpdateUser(oauthUser.getUpdateUser());
            applicationInfoDialogueRecord.setTenantId(oauthUser.getTenantId());
            //applicationInfoDialogue.setDeptId(oauthUser.getDeptId());
            applicationInfoDialogueRecord.setUserId(String.valueOf(oauthUser.getId()));
            if(StringUtils.isEmpty(oauthUser.getTenantId())){
                applicationInfoDialogueRecord.setTenantId(tokenUserInfo.getTenantId());
            }
        }
        applicationInfoDialogueRecord.setAiQuestionId(String.valueOf(applicationInfoDialogueParam.getId()));
        mapper.insert(applicationInfoDialogueRecord,false);
        return Result.success();
    }
}
