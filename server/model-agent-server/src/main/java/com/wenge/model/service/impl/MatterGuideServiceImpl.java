package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.MatterGuideListParam;
import com.wenge.model.dto.param.MatterGuideParam;
import com.wenge.model.entity.*;
import com.wenge.model.enums.BusinessPermissionEnum;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.mapper.MatterGuideFiledMapper;
import com.wenge.model.mapper.MatterGuideMapper;
import com.wenge.model.mapper.MatterGuideOptionMapper;
import com.wenge.model.mapper.MatterGuideTypeMapper;
import com.wenge.model.service.*;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.vo.MatterGuideListVo;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.util.PermissionControlUtils;
import com.wenge.oauth.mapper.UserMapper;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.utils.BeanUtil;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.MatterGuideFiledTableDef.MATTER_GUIDE_FILED;
import static com.wenge.model.entity.table.MatterGuideGroupTableDef.MATTER_GUIDE_GROUP;
import static com.wenge.model.entity.table.MatterGuideOptionTableDef.MATTER_GUIDE_OPTION;
import static com.wenge.model.entity.table.MatterGuideTableDef.MATTER_GUIDE;
import static com.wenge.model.entity.table.MatterGuideTypeTableDef.MATTER_GUIDE_TYPE;
import static com.wenge.model.entity.table.SceneApplicationRefTableDef.SCENE_APPLICATION_REF;
import static com.wenge.model.entity.table.SceneMatterGroupRefTableDef.SCENE_MATTER_GROUP_REF;


@Service
@Slf4j
public class MatterGuideServiceImpl extends ServiceImpl<MatterGuideMapper, MatterGuide> implements MatterGuideService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MatterGuideMapper matterGuideMapper;

    @Autowired
    private UserMapper oauthUserMapper;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private MatterGuideFiledService matterGuideFiledService;

    @Autowired
    private MatterGuideFiledMapper matterGuideFiledMapper;

    @Autowired
    private MatterGuideOptionMapper matterGuideOptionMapper;

    @Autowired
    private MatterGuideTypeMapper matterGuideTypeMapper;

    @Autowired
    private MatterGuideGroupService matterGuideGroupService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private SceneMatterGroupRefService sceneMatterGroupRefService;

    @PostConstruct
    public void init() {
        redisService.batchDel(RedisKey.MATTER_DATA);
    }

    @Override
    public Result<List<MatterGuideListVo>> getList(MatterGuideListParam param) {
        if (StringUtils.isBlank(param.getApplicationId())) {
            return Result.success(Lists.newArrayList());
        }
        List<MatterGuide> matterNameList = getMatterName(param.getApplicationId(), StringConstant.BLANK);
        List<MatterGuideListVo> matterGuideListVos = BeanUtil.copyList(matterNameList, MatterGuideListVo.class);
        return Result.success(matterGuideListVos);
    }

    @Override
    public Result<List<MatterGuide>> getActiveList(MatterGuideListParam param) {
        Wrappers<Object> wrappers = Wrappers.init()
                .select(MATTER_GUIDE.ID, MATTER_GUIDE.MATTER_ID, MATTER_GUIDE.MATTER_NAME)
                .where(MATTER_GUIDE.APPLICATION_ID.eq(param.getApplicationId()));
        List<MatterGuide> matterGuideList = matterGuideMapper.selectListByQuery(wrappers);
        return Result.success(matterGuideList);
    }


    @Override
    public Result addMatter(MatterGuide matterGuide) {
        //if (StringUtils.isBlank(matterGuide.getApplicationId())) {
        //    return Result.fail("应用ID不能为空");
        //}

        if (StringUtils.isBlank(matterGuide.getMatterName())) {
            return Result.fail("事项名称不能为空");
        }
        //
        //final ApplicationInfo byId = applicationInfoService.getByAppId(matterGuide.getApplicationId());
        //if (byId == null) {
        //    return Result.fail("没有找到对应的应用信息");
        //}

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        matterGuide.setTenantId(tokenUserInfo.getTenantId());
        boolean exists = MatterGuide.create()
                .where(MATTER_GUIDE.APPLICATION_ID.eq(matterGuide.getApplicationId()))
                .and(MATTER_GUIDE.MATTER_NAME.eq(matterGuide.getMatterName()))
                .and(MATTER_GUIDE.TENANT_ID.eq(matterGuide.getTenantId()))
                .and(StringUtils.isNotBlank(matterGuide.getMatterId()), MATTER_GUIDE.MATTER_ID.ne(matterGuide.getMatterId()))
                .exists();
        if (exists) {
            return Result.fail("事项名称重复");
        }

        if (StringUtils.isBlank(matterGuide.getMatterId())) {
            matterGuide.setMatterId(IdUtil.simpleUUID());
        }
        matterGuide.setCreateUserId(tokenUserInfo.getId() + "");
        matterGuide.setCreateUserName(tokenUserInfo.getAccountName());
        //matterGuide.setApplicationName(byId.getApplicationName());
        matterGuide.setCreateTime(DateUtil.getCurrentTime());
        matterGuide.setUpdateTime(matterGuide.getCreateTime());
        matterGuide.setMatterTypeId(Long.parseLong(matterGuide.getMatterType()));
        final MatterGuideType matterGuideType = matterGuideTypeMapper.selectOneById(Long.parseLong(matterGuide.getMatterType()));
        matterGuide.setMatterType(matterGuideType != null ? matterGuideType.getName() : null);
        saveOrUpdate(matterGuide);
        publisher.publishEvent(new SceneEvent(null));
        return Result.success(matterGuide);
    }

    @Override
    @Transactional
    public Result editMatter(MatterGuide matterGuide) {
        if (matterGuide == null || matterGuide.getId() == null) {
            return Result.fail("事项ID不能为空");
        }
        final MatterGuide byId = getById(matterGuide.getId());
        if (byId == null) {
            return Result.fail("没有找到对应ID的事项记录");
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        //已经有的字段list
        List<MatterGuideFiled> matterGuideFileds = MatterGuideFiled.create().where(MATTER_GUIDE_FILED.MATTER_ID.eq(byId.getMatterId()))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP.isNotNull()).lists();
        //需要留下了的字段ID
        List<Long> filedIds = new ArrayList<>();
        //需要删除的字段ID
        List<Long> delFiledIds = new ArrayList<>();
        //字段list处理
        if (CollectionUtil.isNotEmpty(matterGuide.getMatterGuideFiledList())) {
            for (MatterGuideFiled matterGuideFiled : matterGuide.getMatterGuideFiledList()) {
                matterGuideFiled.setMatterId(byId.getMatterId());
                if (StringUtils.isBlank(matterGuideFiled.getFiledId())) {
                    matterGuideFiled.setFiledId(IdUtil.simpleUUID());
                }
                handleMatterGuideFiled(matterGuideFiled);
                if (matterGuideFiled.getId()!=null) {
                    filedIds.add(matterGuideFiled.getId());
                }

            }

            //删掉不在提交记录里面的id
            if (CollectionUtil.isNotEmpty(matterGuideFileds)) {
                for (MatterGuideFiled matterGuideFiled : matterGuideFileds) {
                    if (!filedIds.contains(matterGuideFiled.getId())) {
                        delFiledIds.add(matterGuideFiled.getId());
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(delFiledIds)) {
                MatterGuideFiled matterGuideFiledDel = MatterGuideFiled.create();
                matterGuideFiledDel.setDeletedFlag(1);
                matterGuideFiledMapper.updateByQuery(matterGuideFiledDel, QueryWrapper.create().where(MATTER_GUIDE_FILED.ID.in(delFiledIds)));
                log.info("需要删除的字段执行成功");
            }
        }
        matterGuide.setUpdateUserId(tokenUserInfo.getId() + "");
        matterGuide.setUpdateTime(DateUtil.getCurrentTime());
        //try {
        //    matterGuide.setMatterTypeId(Long.parseLong(matterGuide.getMatterType()));
        //    final MatterGuideType matterGuideType = matterGuideTypeMapper.selectOneById(Long.parseLong(matterGuide.getMatterType()));
        //    matterGuide.setMatterType(matterGuideType != null ? matterGuideType.getName() : null);
        //}catch (Exception e){
        //    e.printStackTrace();
        //}

        if(updateById(matterGuide)){
            publisher.publishEvent(new SceneEvent(null));
            return Result.success("保存成功");
        }
        publisher.publishEvent(new SceneEvent(null));
        return Result.fail("保存失败");
    }

    @Override
    public Result getMatterById(Long id) {
        final MatterGuide byId = getById(id);
        byId.setMatterTypeName(byId.getMatterType() + "");
        byId.setMatterType(byId.getMatterTypeId() + "");
        List<MatterGuideFiled> matterGuideFileds = MatterGuideFiled.create().where(MATTER_GUIDE_FILED.MATTER_ID.eq(byId.getMatterId()))
                .and(MATTER_GUIDE_FILED.DELETED_FLAG.eq(0))
                .and(MATTER_GUIDE_FILED.FILED_CODE_GROUP.isNotNull()).lists();
        if(CollectionUtil.isNotEmpty(matterGuideFileds)){
            for (MatterGuideFiled matterGuideFiled : matterGuideFileds) {
                matterGuideFiled.setFiledCodeGroup(matterGuideFiled.getFiledCodeGroupId() + "");
                List<MatterGuideOption> matterGuideOptions = MatterGuideOption.create().
                        where(MATTER_GUIDE_OPTION.DELETED_FLAG.eq(0)).and(MATTER_GUIDE_OPTION.FILED_ID.eq(matterGuideFiled.getFiledId())).lists();
                matterGuideFiled.setOptionList(matterGuideOptions);
            }
            matterGuideFileds = matterGuideFileds.stream().sorted((p1, p2) -> {
                Integer sorted = p1.getSorted();
                if (null == sorted) {
                    sorted = 0;
                }
                Integer sorted1 = p2.getSorted();
                if (null == sorted1) {
                    sorted1 = 0;
                }
                return sorted - sorted1;
            }).collect(Collectors.toList());
            byId.setMatterGuideFiledList(matterGuideFileds);
        }
        return Result.success(byId);
    }

    @Override
    public MatterGuide getMatterByMatterId(String matterId) {
        List<MatterGuide> matterGuides = MatterGuide.create().and(MATTER_GUIDE.MATTER_ID.eq(matterId)).lists();
        if(CollectionUtil.isNotEmpty(matterGuides)){
            return matterGuides.get(0);
        }
        return null;
    }

    //处理字段数据
    public void handleMatterGuideFiled(MatterGuideFiled matterGuideFiled) {
        //已经有的字段list
        List<MatterGuideOption> matterGuideOptions = MatterGuideOption.create().where(MATTER_GUIDE_OPTION.FILED_ID.eq(matterGuideFiled.getFiledId()))
                .and(MATTER_GUIDE_OPTION.DELETED_FLAG.eq(0)).lists();
        //需要留下了的字段ID
        List<Long> filedOptionIds = new ArrayList<>();
        //需要删除的字段ID
        List<Long> delFiledOptionIds = new ArrayList<>();
        //保存字段信息
        matterGuideFiled.setFiledCodeGroupId(matterGuideFiled.getFiledCodeGroup());
        final MatterGuideGroup byId = matterGuideGroupService.getById(Long.parseLong(matterGuideFiled.getFiledCodeGroup()));
        matterGuideFiled.setFiledCodeGroup(byId != null ? byId.getName() : null);
        matterGuideFiledService.saveOrUpdate(matterGuideFiled);
        //处理选项框
        if (CollectionUtil.isNotEmpty(matterGuideFiled.getOptionList())) {
            matterGuideFiled.getOptionList().forEach(matterGuideOption -> {
                matterGuideOption.setFiledId(matterGuideFiled.getFiledId());
                matterGuideOption.setDeletedFlag(0);
                matterGuideOptionMapper.insertOrUpdate(matterGuideOption);
                if (matterGuideOption.getId() != null) {
                    filedOptionIds.add(matterGuideOption.getId());
                }
            });
        }else {
            MatterGuideOption matterGuideFiledDel = MatterGuideOption.create();
            matterGuideFiledDel.setDeletedFlag(1);
            matterGuideOptionMapper.updateByQuery(matterGuideFiledDel, QueryWrapper.create().where(MATTER_GUIDE_OPTION.FILED_ID.eq(matterGuideFiled.getFiledId())));
        }
        //删掉不在提交记录里面的id
        if (CollectionUtil.isNotEmpty(matterGuideOptions)) {
            for (MatterGuideOption matterGuideOption : matterGuideOptions) {
                if (!filedOptionIds.contains(matterGuideOption.getId())) {
                    delFiledOptionIds.add(matterGuideOption.getId());
                }
            }
        }
        if (CollectionUtil.isNotEmpty(delFiledOptionIds)) {
            MatterGuideOption matterGuideFiledDel = MatterGuideOption.create();
            matterGuideFiledDel.setDeletedFlag(1);
            matterGuideOptionMapper.updateByQuery(matterGuideFiledDel, QueryWrapper.create().where(MATTER_GUIDE_OPTION.ID.in(delFiledOptionIds)));
            log.info("需要删除的字段选项执行成功");
        }

    }


    @Override
    public Result getMatterList(MatterGuideParam matterGuide) {
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        FlexModel<MatterGuide> matterGuideFlexModel = MatterGuide.create()
                .where(StringUtils.isNotBlank(matterGuide.getApplicationId()), MATTER_GUIDE.APPLICATION_ID.eq(matterGuide.getApplicationId()))
                .and(MATTER_GUIDE.DELETE_FLAG.eq(0))
                .and(StringUtils.isNotBlank(tokenUserInfo.getTenantId()), MATTER_GUIDE.TENANT_ID.eq(tokenUserInfo.getTenantId()))
                .orderBy(MATTER_GUIDE.CREATE_TIME.desc());

        // 根据权限查询
        PermissionControlUtils.buildPermission(matterGuideFlexModel, BusinessPermissionEnum.MATTER);
        // 模糊条件搜索
        matterGuideFlexModel.and(StringUtils.isNotBlank(matterGuide.getMatterName()), MATTER_GUIDE.MATTER_NAME.like(matterGuide.getMatterName()))
                            .and(StringUtils.isNotBlank(matterGuide.getKeyword()), MATTER_GUIDE.MATTER_NAME.like(matterGuide.getKeyword())
                            .or(MATTER_GUIDE.MATTER_TYPE.like(matterGuide.getKeyword())));
        Page<MatterGuide> pages = matterGuideFlexModel.pages(Page.of(matterGuide.getPageNo(), matterGuide.getPageSize()));
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_TYPE.STATUS.eq(0))
                .orderBy(MATTER_GUIDE_TYPE.CREATE_TIME.desc());
        Map<String, MatterGuideType> stringMap = new HashMap<>();
        final List<MatterGuideType> list = matterGuideTypeMapper.selectListByQuery(queryWrapper);
        list.forEach(item -> {
            stringMap.put(item.getId() + "", item);
        });
        pages.getRecords().forEach(matterGuideIndex -> {
//            if (matterGuideIndex.getCreateUserId()!=null) {
//                final OauthUser oauthUser = oauthUserMapper.selectOneById(matterGuideIndex.getCreateUserId());
//                matterGuideIndex.setCreateUserName(oauthUser != null ? oauthUser.getUsername() : "--");
//            }
//            if (matterGuideIndex.getApplicationId() != null) {
//                final ApplicationInfo applicationInfo = applicationInfoService.getByAppId(matterGuideIndex.getApplicationId());
//                matterGuideIndex.setApplicationName(applicationInfo != null ? applicationInfo.getApplicationName() : "--");
//            }
            MatterGuideType matterGuideType = stringMap.get(matterGuideIndex.getMatterTypeId() + "");
            matterGuideIndex.setMatterTypeName(matterGuideType != null ? matterGuideType.getName() : "--");
            matterGuideIndex.setMatterType(matterGuideIndex.getMatterTypeId() + "");
        });
        new Thread(()->{
            List<MatterGuide> matterGuideList = pages.getRecords();
            matterGuideList.forEach(matterGuideUpdate -> {
                if (matterGuideUpdate.getApplicationId() != null) {
                    final ApplicationInfo applicationInfo = applicationInfoService.getByAppId(matterGuideUpdate.getApplicationId());
                    if (applicationInfo != null && !applicationInfo.getApplicationName().equals(matterGuideUpdate.getApplicationName())) {
                        matterGuideUpdate.setApplicationName(applicationInfo.getApplicationName());
                        matterGuideMapper.update(matterGuideUpdate);
                    }
                }
            });
        }).start();
        return Result.success(pages);
    }

    @Override
    public Result deleteMatter(MatterGuideParam matterGuide) {
        if(CollectionUtil.isEmpty(matterGuide.getDelMatterIds())){
            return Result.fail("请选择要删除的记录");
        }
//        MatterGuide.create()
//                .where(MATTER_GUIDE.MATTER_ID.in(idList))
//                .remove();
        List<String> groupIdList = sceneMatterGroupRefService.getGroupIdListByMatterId(matterGuide.getDelMatterIds());
        if (CollectionUtil.isNotEmpty(groupIdList)) {
            return Result.fail("事项关联了场景，请先解除关联再删除");
        }
        MatterGuide matterGuideUpdate = MatterGuide.create();
        matterGuideUpdate.setDeleteFlag(1);
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.and(MATTER_GUIDE.MATTER_ID.in(matterGuide.getDelMatterIds()));
        final boolean update = update(matterGuideUpdate, queryWrapper);
        publisher.publishEvent(new SceneEvent(null));
        return Result.success(update);
    }

    @Override
    public Result updateShowFlag(MatterGuide matterGuide) {
        publisher.publishEvent(new SceneEvent(null));
        MatterGuide matterGuideUpdate = MatterGuide.create();
        matterGuideUpdate.setId(matterGuide.getId());
        matterGuideUpdate.setShowFlag(matterGuide.getShowFlag());
        return Result.success(updateById(matterGuideUpdate));
    }

    @Override
    public List<MatterGuide> getMatterName(String applicationId, String sceneId) {
        if (StringUtils.isBlank(applicationId)) {
            return Lists.newArrayList();
        }

        // 先从redis获取场景
        String redisKey = RedisKey.MATTER_DATA + applicationId + ":";
        List<MatterGuide> matterGuideListFromReis = null;
        if (redisService.hasKey(redisKey)) {
            String matterStr = redisService.getObject(redisKey, String.class);
            if (StringUtils.isNotBlank(matterStr) && JSONUtil.isTypeJSONArray(matterStr)) {
                matterGuideListFromReis = JSON.parseArray(matterStr, MatterGuide.class);
            }
        } else {
            matterGuideListFromReis = Wrappers.of(mapper)
                    .select(MATTER_GUIDE.ALL_COLUMNS, MATTER_GUIDE_GROUP.SCENE_ID, MATTER_GUIDE_GROUP.NAME.as("groupName"), SCENE_MATTER_GROUP_REF.SORTED, MATTER_GUIDE_TYPE.NAME.as("matterType"))
                    .innerJoin(SCENE_MATTER_GROUP_REF).on(SCENE_MATTER_GROUP_REF.MATTER_ID.eq(MATTER_GUIDE.MATTER_ID))
                    .innerJoin(MATTER_GUIDE_GROUP).on(MATTER_GUIDE_GROUP.GROUP_ID.eq(SCENE_MATTER_GROUP_REF.GROUP_ID))
                    .innerJoin(SCENE_APPLICATION_REF).on(SCENE_APPLICATION_REF.SCENE_ID.eq(MATTER_GUIDE_GROUP.SCENE_ID))
                    .innerJoin(MATTER_GUIDE_TYPE).on(MATTER_GUIDE_TYPE.ID.eq(MATTER_GUIDE.MATTER_TYPE_ID))
                    .where(MATTER_GUIDE.DELETE_FLAG.eq(0))
                    .and(MATTER_GUIDE.SHOW_FLAG.eq(YesNoEnum.YES.getName()))
                    .and(SCENE_MATTER_GROUP_REF.STATUS.eq(StringConstant.ONE))
                    .and(MATTER_GUIDE_GROUP.STATUS.eq(0))
                    .and(SCENE_APPLICATION_REF.APPLICATION_ID.eq(applicationId))
                    .list();
            redisService.set(redisKey, JSON.toJSONString(matterGuideListFromReis), 60 * 60 * 24 * 7);
        }

        // 获取场景
        if (StringUtils.isNotBlank(sceneId)) {
            if (CollectionUtil.isNotEmpty(matterGuideListFromReis)) {
                return matterGuideListFromReis.stream().filter(p -> p.getSceneId().equals(sceneId)).collect(Collectors.toList());
            } else {
                return Lists.newArrayList();
            }
        }

        return matterGuideListFromReis;
    }

    @Override
    public List<MatterGuide> getMatterByMatterIds(List<String> matterIds) {
        if (CollectionUtils.isEmpty(matterIds)) {
            return Lists.newArrayList();
        }
        return Wrappers.of(mapper)
                .where(MATTER_GUIDE.MATTER_ID.in(matterIds))
                .list();
    }

    @Override
    public List<MatterGuide> getMatterName(String sceneId) {
        if (StringUtils.isBlank(sceneId)) {
            return Lists.newArrayList();
        }
        // 先从redis获取场景
        String redisKey = RedisKey.MATTER_DETAIL + sceneId + ":";
        List<MatterGuide> matterGuideListFromReis = null;
        if (redisService.hasKey(redisKey)) {
            String matterStr = redisService.getObject(redisKey, String.class);
            if (StringUtils.isNotBlank(matterStr) && JSONUtil.isTypeJSONArray(matterStr)) {
                matterGuideListFromReis = JSON.parseArray(matterStr, MatterGuide.class);
            }
        } else {
            matterGuideListFromReis = Wrappers.of(mapper)
                    .select(MATTER_GUIDE.ALL_COLUMNS, MATTER_GUIDE_GROUP.SCENE_ID, MATTER_GUIDE_GROUP.NAME.as("groupName"), SCENE_MATTER_GROUP_REF.SORTED, MATTER_GUIDE_TYPE.NAME.as("matterType"))
                    .innerJoin(SCENE_MATTER_GROUP_REF).on(SCENE_MATTER_GROUP_REF.MATTER_ID.eq(MATTER_GUIDE.MATTER_ID))
                    .innerJoin(MATTER_GUIDE_GROUP).on(MATTER_GUIDE_GROUP.GROUP_ID.eq(SCENE_MATTER_GROUP_REF.GROUP_ID))
                    .innerJoin(MATTER_GUIDE_TYPE).on(MATTER_GUIDE_TYPE.ID.eq(MATTER_GUIDE.MATTER_TYPE_ID))
                    .where(MATTER_GUIDE.DELETE_FLAG.eq(0))
                    // .and(MATTER_GUIDE.SHOW_FLAG.eq(YesNoEnum.YES.getName()))
                    .and(SCENE_MATTER_GROUP_REF.STATUS.eq(StringConstant.ONE))
                    .and(MATTER_GUIDE_GROUP.STATUS.eq(0))
                    .and(SCENE_MATTER_GROUP_REF.SCENE_ID.eq(sceneId))
                    .list();
            redisService.set(redisKey, JSON.toJSONString(matterGuideListFromReis), 60 * 60 * 24 * 7);
        }

        // 获取场景
        if (CollectionUtil.isNotEmpty(matterGuideListFromReis)) {
            return matterGuideListFromReis.stream().filter(p -> p.getSceneId().equals(sceneId)).collect(Collectors.toList());
        } else {
            return Lists.newArrayList();
        }
    }
}