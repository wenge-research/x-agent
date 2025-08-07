package com.wenge.model.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ApplicationInfoDialogueParam;
import com.wenge.model.entity.ApplicationInfoDialogue;
import com.wenge.model.mapper.ApplicationInfoDialogueMapper;
import com.wenge.model.service.ApplicationInfoDialogueService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.wenge.model.entity.table.ApplicationEvaluationTableDef.APPLICATION_EVALUATION;
import static com.wenge.model.entity.table.ApplicationInfoDialogueTableDef.APPLICATION_INFO_DIALOGUE;
import static com.wenge.model.entity.table.ApplicationInfoVersionTableDef.APPLICATION_INFO_VERSION;

@Service
@Slf4j
public class ApplicationInfoDialogueServiceImpl extends ServiceImpl<ApplicationInfoDialogueMapper, ApplicationInfoDialogue> implements ApplicationInfoDialogueService {
    @Resource
    private UserService userService;
    @Override
    public Result<Page<ApplicationInfoDialogue>> getApplicationInfoDialogueList(ApplicationInfoDialogueParam applicationInfoDialogueParam) {
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String tenantId =null;
        String userid=null;
        String dept_id=null;
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            tenantId = oauthUser.getTenantId();
            userid= String.valueOf(oauthUser.getId());
            dept_id=oauthUser.getDeptId();
            if(com.alibaba.cloud.commons.lang.StringUtils.isEmpty(oauthUser.getTenantId())){
                tenantId=tokenUserInfo.getTenantId();
            }
        }
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(com.alibaba.cloud.commons.lang.StringUtils.isNotEmpty(tenantId)&&!tenantId.equals(""),APPLICATION_INFO_DIALOGUE.TENANT_ID.eq(tenantId))
                .and(com.alibaba.cloud.commons.lang.StringUtils.isNotEmpty(userid)&&!userid.equals(""),APPLICATION_INFO_DIALOGUE.USER_ID.eq(userid))
                .and(com.alibaba.cloud.commons.lang.StringUtils.isNotEmpty(dept_id)&&!dept_id.equals(""),APPLICATION_INFO_DIALOGUE.DEPT_ID.eq(dept_id))
                .and(applicationInfoDialogueParam.getApplicationId()!=null,APPLICATION_INFO_DIALOGUE.APPLICATION_ID.eq(applicationInfoDialogueParam.getApplicationId()))
                .and(applicationInfoDialogueParam.getApplicationInfoId()!=null,APPLICATION_INFO_DIALOGUE.APPLICATION_INFO_ID.eq(applicationInfoDialogueParam.getApplicationInfoId()))
                .and(applicationInfoDialogueParam.getDialogueId()!=null,APPLICATION_INFO_DIALOGUE.DIALOGUE_ID.eq(applicationInfoDialogueParam.getDialogueId()))
                .and(applicationInfoDialogueParam.getClientId()!=null,APPLICATION_INFO_DIALOGUE.CLIENT_ID.eq(applicationInfoDialogueParam.getClientId()))
                .and(applicationInfoDialogueParam.getQuestion()!=null,APPLICATION_INFO_DIALOGUE.QUESTION.eq(applicationInfoDialogueParam.getQuestion()))
                .orderBy(APPLICATION_INFO_DIALOGUE.CREATE_TIME.desc());
        Page<ApplicationInfoDialogue> page = page(Page.of(applicationInfoDialogueParam.getPageNo(), applicationInfoDialogueParam.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public Result insertApplicationInfoDialogue(ApplicationInfoDialogueParam applicationInfoDialogueParam) {
        if(applicationInfoDialogueParam!=null&&StringUtils.isEmpty(applicationInfoDialogueParam.getDialogueId())){
            return Result.fail("组件ID不能为空");
        }
        if(applicationInfoDialogueParam!=null&&StringUtils.isEmpty(applicationInfoDialogueParam.getQuestion())){
            return Result.fail("问题内容不能为空");
        }
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(applicationInfoDialogueParam.getDialogueId()!=null,APPLICATION_INFO_DIALOGUE.DIALOGUE_ID.eq(applicationInfoDialogueParam.getDialogueId()))
                .and(applicationInfoDialogueParam.getQuestion()!=null,APPLICATION_INFO_DIALOGUE.QUESTION.eq(applicationInfoDialogueParam.getQuestion()));
        ApplicationInfoDialogue applicationInfoDialogue1 = mapper.selectOneByQuery(wrappers);
        if(applicationInfoDialogue1!=null&&ObjectUtil.isNotEmpty(applicationInfoDialogue1)){
            return Result.success();
        }else{
            ApplicationInfoDialogue applicationInfoDialogue = new ApplicationInfoDialogue();
            BeanUtils.copyProperties(applicationInfoDialogueParam, applicationInfoDialogue);
            TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
            Long currentTimeMillis=System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 使用Date构造函数，传入时间戳
            Date date = new Date(currentTimeMillis);
            String formattedDate = sdf.format(date);
            if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                applicationInfoDialogue.setCreateTime(formattedDate);
                applicationInfoDialogue.setUpdateTime(formattedDate);
                applicationInfoDialogue.setCreateUser(oauthUser.getCreateUser());
                applicationInfoDialogue.setUpdateUser(oauthUser.getUpdateUser());
                applicationInfoDialogue.setTenantId(oauthUser.getTenantId());
                applicationInfoDialogue.setDeptId(oauthUser.getDeptId());
                applicationInfoDialogue.setUserId(String.valueOf(oauthUser.getId()));
                if(StringUtils.isEmpty(oauthUser.getTenantId())){
                    applicationInfoDialogue.setTenantId(tokenUserInfo.getTenantId());
                }
            }
            mapper.insert(applicationInfoDialogue,false);
            return Result.success();
        }
    }

    @Override
    public Result deleteApplicationInfoDialogue(Integer id) {
        return null;
    }

    @Override
    public Result saveApplicationInfoDialogue(ApplicationInfoDialogueParam applicationInfoDialogueParam) {
        ApplicationInfoDialogue applicationInfoDialogue = new ApplicationInfoDialogue();
        BeanUtils.copyProperties(applicationInfoDialogueParam, applicationInfoDialogue);
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        Long currentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用Date构造函数，传入时间戳
        Date date = new Date(currentTimeMillis);
        String formattedDate = sdf.format(date);
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            applicationInfoDialogue.setUpdateTime(formattedDate);
            applicationInfoDialogue.setUpdateUser(oauthUser.getUpdateUser());
            if(StringUtils.isEmpty(oauthUser.getTenantId())){
                applicationInfoDialogue.setTenantId(tokenUserInfo.getTenantId());
            }
        }
        mapper.insert(applicationInfoDialogue,false);
        return Result.success();
    }
}
