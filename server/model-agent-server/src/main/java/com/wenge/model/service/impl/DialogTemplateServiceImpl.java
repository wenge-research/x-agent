package com.wenge.model.service.impl;


import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.DialogTemplateParam;
import com.wenge.model.dto.param.TemplateStatusUpdateParam;
import com.wenge.model.entity.DialogTemplate;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.entity.table.DialogTemplateTableDef;
import com.wenge.model.mapper.DialogTemplateMapper;
import com.wenge.model.service.DialogTemplateService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.DialogTemplateTableDef.DIALOG_TEMPLATE;
import static com.wenge.oauth.enums.OwnerTypeEnum.OFFICIAL;
import static com.wenge.oauth.enums.OwnerTypeEnum.PERSONAL;

/**
 * Description: 聊天模板服务实现类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-25 17:16:27
 */
@Service
@Slf4j
public class DialogTemplateServiceImpl extends ServiceImpl<DialogTemplateMapper, DialogTemplate> implements DialogTemplateService {
    /**
     * 聊天模板数据库处理类
     */
    @Autowired
    private DialogTemplateMapper dialogTemplateMapper;
    @Autowired
    private UserService userService;

    @Override
    public Result addDialogTemplate(DialogTemplate dialogTemplate) {
        if (StringUtils.isBlank(dialogTemplate.getTemplateId())) {
            dialogTemplate.setTemplateId(IdUtil.simpleUUID());
        }
        if (StringUtils.isBlank(dialogTemplate.getTemplateRoute())) {
            return Result.fail("模板路由不能为空");
        }

        // 添加后是personal，预置后是official，群众没有权限进行操作
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if(PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())){
            dialogTemplate.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
        } else if (tokenUserInfo.getPowerType().equals(PowerTypeEnum.WECHAT_USER.getCode())) {
            return Result.fail("无权限操作");
        } else {
            dialogTemplate.setOwnerType(OwnerTypeEnum.PERSONAL.getCode());
        }

        saveOrUpdate(dialogTemplate);
        return Result.success();
    }

    @Override
    public Result getDialogTemplateList(DialogTemplateParam param) {
        //LambdaQueryWrapper<DialogTemplate> wrapper = Wrappers.lambdaQuery(DialogTemplate.class);
        //Page<DialogTemplate> page = page(Page.of(dialogTemplate.getPageNo(), dialogTemplate.getPageSize()), wrapper);
        if (null == param.getPageNo()) {
            param.setPageNo(1);
        }
        if (null == param.getPageSize()) {
            param.setPageSize(99);
        }
        Page<DialogTemplate> pages = DialogTemplate.create()
                .where(StringUtils.isNotBlank(param.getForm()), DIALOG_TEMPLATE.FORM.eq(param.getForm())) // PC/H5
                .and(!Objects.isNull(param.getStatus()), DIALOG_TEMPLATE.STATUS.eq(param.getStatus()))
                .and(StringUtils.isNotBlank(param.getDialogTemplateName()),DIALOG_TEMPLATE.TEMPLATE_NAME.like(param.getDialogTemplateName())) // 搜索
                .and(StringUtils.isNotBlank(param.getIntelligenceType()), DIALOG_TEMPLATE.INTELLIGENCE_TYPE.eq(param.getIntelligenceType())) // 模板类型
                .pages(Page.of(param.getPageNo(), param.getPageSize()));
        return Result.success(pages);
    }

    @Override
    public Result updateDialogTemplate(DialogTemplate dialogTemplate) {
        updateById(dialogTemplate);
        return Result.success();
    }

    @Override
    public Result deleteDialogTemplate(List<String> idList) {
        if (CollectionUtils.isNotEmpty(idList)) {
            Wrappers<Object> wrappers = Wrappers.init()
                    .where(DialogTemplateTableDef.DIALOG_TEMPLATE.TEMPLATE_ID.in(idList));
            remove(wrappers);
        }

        return Result.success();
    }

    @Override
    public Result updateStatus(TemplateStatusUpdateParam param) {

        if (StringUtils.isBlank(param.getTemplateId())) {
            return Result.fail("模板ID不能为空");
        }

        DialogTemplate.create()
                .setStatus(param.getStatus())
                .where(DIALOG_TEMPLATE.TEMPLATE_ID.eq(param.getTemplateId()))
                .update();

        return Result.success();
    }

    @Override
    public Result setPreset(TemplateStatusUpdateParam param) {
        if (param == null) {
            return Result.fail("请求参数为空");
        }

        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        // 只有超管才能操作
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
            return Result.fail("无操作权限");
        }

        // 获取传入的模板主键id
        long id = param.getId();
        DialogTemplate dialogTemplate = getById(id);
        if (dialogTemplate == null) {
            return Result.fail("模板不存在");
        }

        // 超级管理员id集合
        List<OauthUser> superManageUser = userService.getSuperManageUser();
        List<Long> superIds = superManageUser.stream().map(OauthUser::getId).distinct().collect(Collectors.toList());
        List<String> stringIds = new ArrayList<>();
        superIds.forEach(superId -> stringIds.add(superId + ""));
        // 只有超管才能进行预置操作且被预置的对象必须得是超管创建的才行
        if (stringIds.contains(dialogTemplate.getCreateUserId())){
            // 构造更新对象，已经是预置就取消，否则设置为预置
            dialogTemplate.setOwnerType(
                    OFFICIAL.getCode().equals(dialogTemplate.getOwnerType()) ? PERSONAL.getCode() : OFFICIAL.getCode());
        }else{
            return Result.fail("该模板非超级管理员创建，无法进行预置操作");
        }

        // 执行更新
        updateById(dialogTemplate,false);

        return Result.success("聊天模板预置更改成功");
    }
}