package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.MyFavoriteQueryParam;
import com.wenge.model.entity.MyFavorite;
import com.wenge.model.mapper.MyFavoriteMapper;
import com.wenge.model.service.MyFavoriteService;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.yashandb.jdbc.StatementImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.wenge.model.entity.table.MyFavoriteTableDef.MY_FAVORITE;


@Service
@Slf4j
public class MyFavoriteServiceImpl extends ServiceImpl<MyFavoriteMapper, MyFavorite> implements MyFavoriteService {

    @Autowired
    private MyFavoriteMapper myFavoriteMapper;

    private static final int APPLICATION_TYPE = 1;
    private static final int PLUGIN_TYPE = 2;
    @Override
    public Result editMyFavorite(MyFavoriteQueryParam param) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null == tokenUserInfo) {
            return Result.fail("未获取到当前用户信息");
        }
        if (param.getType() == null) {
            return Result.fail("需要传入指定收藏类型，1：应用 2：插件");
        }
        if (param.getType() != 1 && param.getType() != 2){
            return Result.fail("收藏标识错误");
        }
        if (StringUtils.isBlank(param.getApplicationId())) {
            return Result.fail("应用为空");
        }

        MyFavorite favorite = new MyFavorite();
        Wrappers<Object> wrappers = Wrappers.init()
                .where(MY_FAVORITE.USER_ID.eq(tokenUserInfo.getId()))
                .and(MY_FAVORITE.APPLICATION_ID.eq(param.getApplicationId()).when(
                        StringUtils.isNotBlank(param.getApplicationId())&&param.getType()==APPLICATION_TYPE))
                .and(MY_FAVORITE.APPLICATION_ID.eq(param.getApplicationId()).when(
                        StringUtils.isNotBlank(param.getApplicationId())&&param.getType()==PLUGIN_TYPE));
        List<MyFavorite> existFavorites = myFavoriteMapper.selectListByQuery(wrappers);
        if (CollectionUtils.isEmpty(existFavorites)) { // 如果不存在收藏记录
            if (param.getFavoriteFlag()==0){ // 此时请求删除报错
                return Result.fail("无法取消未收藏的模块");
            }
            // 表中无数据，请求收藏则新增数据
            BeanUtil.copyProperties(param, favorite);
            favorite.setUserId(tokenUserInfo.getId());
            favorite.setFavoriteId(IdUtil.simpleUUID());
            favorite.setFavoriteFlag(1);
            myFavoriteMapper.insertOrUpdate(favorite);
            return Result.success(favorite);
        } else { // 如果存在收藏记录则先获取
            favorite = existFavorites.get(0);
            if (param.getFavoriteFlag() == 1) { // 用户想收藏
                if (favorite.getFavoriteFlag() == 1){ // 但是记录已存在且收藏，不允许重复收藏
                    return Result.fail("不能重复收藏");
                }
                favorite.setFavoriteFlag(1); // 记录不存在则收藏
                myFavoriteMapper.insertOrUpdate(favorite);// 更新收藏表
            } else { // 用户想取消收藏
                if (favorite.getFavoriteFlag() == 0){ // 但是记录本身不是收藏状态，不允许取消
                    return Result.fail("该应用未收藏，无法取消收藏");
                }
                myFavoriteMapper.deleteByQuery(wrappers);// 是收藏状态，用户想取消收藏，直接删除
                return Result.success("取消收藏成功");
            }
        }
        return Result.success(favorite);
    }


    @Override
    public Result<MyFavorite> getMyFavoriteByApplicationId(String applicationId) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null == tokenUserInfo) {
            throw new RuntimeException("未获取到当前用户信息");
        }
        if (StringUtils.isBlank(applicationId)) {
            throw new IllegalArgumentException("applicationId为空");
        }
        Wrappers<Object> wrappers = Wrappers.init()
                .where(MY_FAVORITE.APPLICATION_ID.eq(applicationId))
                .and(MY_FAVORITE.USER_ID.eq(tokenUserInfo.getId()));
        List<MyFavorite> myFavorites = myFavoriteMapper.selectListByQuery(wrappers);
        if (CollectionUtils.isEmpty(myFavorites)) {
            return Result.success(null);
        }

        return Result.success(myFavorites.get(0));
    }

    @Override
    public Result<Page<MyFavorite>> getAllMyFavorite(MyFavoriteQueryParam param) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        Wrappers<Object> wrappers = Wrappers.init()
                .where(MY_FAVORITE.FAVORITE_FLAG.eq(1))
                .and(MY_FAVORITE.USER_ID.eq(tokenUserInfo.getId()))
                .and(MY_FAVORITE.TYPE.eq(2));
        Page<MyFavorite> page = myFavoriteMapper.paginate(param.getPageNo(), param.getPageSize(), wrappers);
        return Result.success(page);
    }


}