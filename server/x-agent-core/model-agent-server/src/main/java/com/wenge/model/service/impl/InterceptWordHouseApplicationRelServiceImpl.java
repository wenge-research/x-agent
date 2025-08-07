package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.BindInterceptWordHouseParam;
import com.wenge.model.dto.param.InterceptWordHouseApplicationRelPageParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.InterceptWordHouseApplicationRel;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.mapper.InterceptWordHouseApplicationRelMapper;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.InterceptWordHouseApplicationRelService;
import com.wenge.model.service.InterceptWordHouseService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.InterceptWordHouseApplicationRelTableDef.INTERCEPT_WORD_HOUSE_APPLICATION_REL;


/**
 * @author: caohaifeng
 * @date: 2024/8/22 16:35
 * @Description: 拦截词库与应用关联服务实现类
 * @Version:1.0
 **/

@Service
@Slf4j
public class InterceptWordHouseApplicationRelServiceImpl extends
        ServiceImpl<InterceptWordHouseApplicationRelMapper, InterceptWordHouseApplicationRel> implements InterceptWordHouseApplicationRelService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private InterceptWordHouseService interceptWordHouseService;

    final private String INTERCEPET_WORD_APPLICATION_INFO_KEY = "intercepet-word-application_info-key-id_";

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public Result addInterceptWordHouseApplicationRel(InterceptWordHouseApplicationRel interceptWordHouse){
        save(interceptWordHouse);
        //更新词库关联应用数量
        interceptWordHouseService.updateHouseApplicationCount(interceptWordHouse.getId());
        publisher.publishEvent(new SceneEvent(null));
        return Result.success();
    }

    @Override
    public Result getInterceptWordHouseApplicationRelList(InterceptWordHouseApplicationRelPageParam interceptWordHousePageParam){
        Wrappers wrappers = Wrappers.init().where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq("1"))
                .and(StringUtils.isNotBlank(interceptWordHousePageParam.getInterceptWordHouseId()),
                        INTERCEPT_WORD_HOUSE_APPLICATION_REL.INTERCEPT_WORD_HOUSE_ID.eq(interceptWordHousePageParam.getInterceptWordHouseId()));
        Page<InterceptWordHouseApplicationRel> page = page(Page.of(interceptWordHousePageParam.getPageNo(), interceptWordHousePageParam.getPageSize()), wrappers);
        page.getRecords().forEach(item -> {
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = redisService.get(INTERCEPET_WORD_APPLICATION_INFO_KEY + item.getApplicationId(), ApplicationInfo.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(applicationInfo == null){
                applicationInfo = applicationInfoService.getByAppId(item.getApplicationId());
                redisService.setExpire(INTERCEPET_WORD_APPLICATION_INFO_KEY + item.getApplicationId(), applicationInfo, 60 * 5 * 1, TimeUnit.SECONDS); //5分钟缓存
            }
            item.setApplicationInfo(applicationInfo);
        });
        return Result.success(page);
    }

    @Override
    public Result updateInterceptWordHouseApplicationRel(InterceptWordHouseApplicationRel interceptWordHouse){
        updateById(interceptWordHouse);

        //更新词库关联应用数量
        interceptWordHouseService.updateHouseApplicationCount(interceptWordHouse.getId());

        return Result.success();
    }

    @Override
    public Result deleteInterceptWordHouseApplicationRel(List<String> idList){
        if(CollectionUtil.isEmpty(idList)){
            return Result.fail("删除ids不能为空");
        }
        for (String s : idList) {
            InterceptWordHouseApplicationRel interceptWordHouse = new InterceptWordHouseApplicationRel();
            interceptWordHouse.setStatus("2");
            interceptWordHouse.setId(Long.parseLong(s));
            updateById(interceptWordHouse);
        }
        try {
            final InterceptWordHouseApplicationRel byId = getById(Long.parseLong(idList.get(0)));
            //更新词库关联应用数量
            interceptWordHouseService.updateHouseApplicationCount(byId.getInterceptWordHouseId());
            publisher.publishEvent(new SceneEvent(null));
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }

    @Override
    public List<Long> getHouseIdByAppId(String appId) {
        if(StringUtils.isBlank(appId)){
            return Lists.newArrayList();
        }

        List<InterceptWordHouseApplicationRel> list = Wrappers.of(mapper)
                .where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(appId))
                .and(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq(StringConstant.ONE))
                .list();
        return list.stream().map(InterceptWordHouseApplicationRel::getInterceptWordHouseId).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Long>> getHouseIdByAppId(List<String> appIds) {
        if (CollectionUtil.isEmpty(appIds)) {
            return Maps.newHashMap();
        }

        List<InterceptWordHouseApplicationRel> list = Wrappers.of(mapper)
                .where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.in(appIds))
                .and(INTERCEPT_WORD_HOUSE_APPLICATION_REL.STATUS.eq(StringConstant.ONE))
                .list();
         list.stream().map(InterceptWordHouseApplicationRel::getInterceptWordHouseId).distinct().collect(Collectors.toList());

        return list.stream().collect(Collectors.groupingBy(InterceptWordHouseApplicationRel::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), list1 -> {
            return list1.stream().map(InterceptWordHouseApplicationRel::getInterceptWordHouseId).collect(Collectors.toList());
        })));
    }

    @Override
    @Transactional
    public Result batchBindInterceptWordHouse(BindInterceptWordHouseParam param) {
        // 根据applicationId删除原有绑定的词库
        String applicationId = param.getApplicationId();
        InterceptWordHouseApplicationRel interceptWordHouse = new InterceptWordHouseApplicationRel();
        interceptWordHouse.setStatus("2");
        update(interceptWordHouse, Wrappers.of(mapper).where(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(applicationId)));
        // 添加新绑定的词库
        List<Long> ids = param.getIds();
        for (Long id : ids) {
            InterceptWordHouseApplicationRel save = new InterceptWordHouseApplicationRel();
            save.setApplicationId(applicationId);
            save.setInterceptWordHouseId(id);
            addInterceptWordHouseApplicationRel(save);
        }
        return Result.success();
    }

}