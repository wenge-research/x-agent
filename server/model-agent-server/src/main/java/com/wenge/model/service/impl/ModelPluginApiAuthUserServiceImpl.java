package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.StringUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ModelPluginApiAuthUserPageParam;
import com.wenge.model.entity.ModelPluginApiAuthUser;
import com.wenge.model.mapper.ModelPluginApiAuthUserMapper;
import com.wenge.model.service.ModelPluginApiAuthUserService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.ModelPluginApiAuthUserTableDef.MODEL_PLUGIN_API_AUTH_USER;

@Slf4j
@Service
public class ModelPluginApiAuthUserServiceImpl extends ServiceImpl<ModelPluginApiAuthUserMapper, ModelPluginApiAuthUser> implements ModelPluginApiAuthUserService {


    @Override
    public Result addModelPluginApiAuthUser(ModelPluginApiAuthUser param) {
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // if (StringUtils.isBlank(param.getModelPluginApiId())) {
        //     return Result.fail("模型算法ID不能为空");
        // }

        //名称重复校验
        Wrappers<ModelPluginApiAuthUser> wrappers = Wrappers.of(mapper)
                .where(MODEL_PLUGIN_API_AUTH_USER.NAME.eq(param.getName()))
                .and(MODEL_PLUGIN_API_AUTH_USER.MOBILE.eq(param.getMobile()))
                .and(MODEL_PLUGIN_API_AUTH_USER.ID.ne(param.getId()).when(param.getId() != null))
                .and(MODEL_PLUGIN_API_AUTH_USER.MODEL_PLUGIN_API_ID.eq(param.getModelPluginApiId()));

        final ModelPluginApiAuthUser modelPluginApiManage = getOne(wrappers);
        if (modelPluginApiManage != null) {
            return Result.fail("姓名＋电话已经存在，请修改");
        }

        param.setCreateUser(tokenUserInfo.getId() + "");
        param.setUpdateUser(tokenUserInfo.getId() + "");
        param.setCreateTime(DateUtil.getCurrentTime());
        param.setUpdateTime(param.getCreateTime());

        //系统生成秘钥
        try {
            if (StringUtils.isBlank(param.getSecretKey())) {
                param.setSecretKey(IdUtil.simpleUUID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveOrUpdate(param);
        return Result.success(param);
    }

    @Override
    public Result getModelPluginApiAuthUserList(ModelPluginApiAuthUserPageParam param) {
        // if(StringUtil.isBlank(param.getModelPluginApiId())){
        //     return Result.fail("模型算法ID不能为空");
        // }
        // TokenUser tokenUserInfo = ContextHolders.getTokenUserInfo();
        Wrappers wrapper = Wrappers.of(mapper);
        wrapper.and(StringUtil.isNotBlank(param.getModelPluginApiId()),MODEL_PLUGIN_API_AUTH_USER.MODEL_PLUGIN_API_ID.eq(param.getModelPluginApiId()));
        wrapper.and(StringUtil.isNotBlank(param.getEnableFlag()), MODEL_PLUGIN_API_AUTH_USER.ENABLE_FLAG.eq(param.getEnableFlag()));
        wrapper.and(StringUtil.isNotBlank(param.getName()), MODEL_PLUGIN_API_AUTH_USER.NAME.like(param.getName()));
        wrapper.and(StringUtil.isNotBlank(param.getType()), MODEL_PLUGIN_API_AUTH_USER.TYPE.eq(param.getType()));
        // wrapper.and(null != tokenUserInfo, MODEL_PLUGIN_API_AUTH_USER.CREATE_USER.eq(tokenUserInfo.getId()));

        PermissionControlUtils.buildPermission(wrapper, null);
        final Page<ModelPluginApiAuthUser> pluginApiManagePage = this.page(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
        return Result.success(pluginApiManagePage);
    }

    @Override
    public Result updateModelPluginApiAuthUser(ModelPluginApiAuthUser modelPluginApiAuthUser) {
        // if(StringUtil.isBlank(modelPluginApiAuthUser.getModelPluginApiId())){
        //     return Result.fail("模型算法ID不能为空");
        // }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if(StringUtils.isNotBlank(modelPluginApiAuthUser.getName())){
            boolean exists = ModelPluginApiAuthUser.create()
                    .where(MODEL_PLUGIN_API_AUTH_USER.NAME.eq(modelPluginApiAuthUser.getName()))
                    .and(MODEL_PLUGIN_API_AUTH_USER.DELETE_FLAG.eq("0"))
                    .and(MODEL_PLUGIN_API_AUTH_USER.MOBILE.eq(modelPluginApiAuthUser.getMobile()))
                    .and(MODEL_PLUGIN_API_AUTH_USER.ID.ne(modelPluginApiAuthUser.getId()))
                    .exists();
            if (exists) {
                return Result.fail("姓名＋电话已经存在，请修改");
            }
//            //系统生成秘钥
//            try {
//                modelPluginApiAuthUser.setSecretKey(TokenUtil.generateToken(modelPluginApiAuthUser.getModelPluginApiId(), modelPluginApiAuthUser.getId() + "", modelPluginApiAuthUser.getName(), modelPluginApiAuthUser.getMobile(), modelPluginApiAuthUser.getExpireTime()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        modelPluginApiAuthUser.setUpdateUser(tokenUserInfo.getId() + "");
        modelPluginApiAuthUser.setUpdateTime(DateUtil.getCurrentTime());
        updateById(modelPluginApiAuthUser);
        return Result.success();
    }

    @Override
    public Result deleteModelPluginApiAuthUser(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return Result.success();
        }
        UpdateChain.create(mapper)
                .where(MODEL_PLUGIN_API_AUTH_USER.ID.in(idList))
                .remove();
        return Result.success();
    }

    @Override
    public Result getModelPluginApiAuthUserById(String id) {
        Wrappers<ModelPluginApiAuthUser> wrapper = new Wrappers<>();
        wrapper.and(MODEL_PLUGIN_API_AUTH_USER.ID.eq(id));
        final ModelPluginApiAuthUser modelPluginApiAuthUser = this.getOne(wrapper);
        return Result.success(modelPluginApiAuthUser);
    }

    @Override
    public Result updateStatus(ModelPluginApiAuthUser modelPluginApiAuthUser) {
        if (null == modelPluginApiAuthUser.getId()) {
            return Result.fail("id不能为空");
        }

        UpdateChain.create(mapper)
                .set(MODEL_PLUGIN_API_AUTH_USER.ENABLE_FLAG, modelPluginApiAuthUser.getEnableFlag())
                .where(MODEL_PLUGIN_API_AUTH_USER.ID.eq(modelPluginApiAuthUser.getId()))
                .update();

        return Result.success();
    }
}