package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.MatterGuideGroupParam;
import com.wenge.model.entity.MatterGuideGroup;
import com.wenge.model.entity.MatterGuideType;
import com.wenge.model.mapper.MatterGuideGroupMapper;
import com.wenge.model.mapper.MatterGuideTypeMapper;
import com.wenge.model.service.MatterGuideGroupService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.wenge.model.entity.table.MatterGuideGroupTableDef.MATTER_GUIDE_GROUP;
import static com.wenge.model.entity.table.MatterGuideTypeTableDef.MATTER_GUIDE_TYPE;

@Service
@Slf4j
public class MatterGuideGroupServiceImpl extends ServiceImpl<MatterGuideGroupMapper, MatterGuideGroup> implements MatterGuideGroupService {

    @Autowired
    private MatterGuideTypeMapper matterGuideTypeMapper;

    /**
     * @param matterGuideGroup
     * @description: 添加字段分组信息
     * @author: caohaifeng
     * @date: 2024/9/4 14:08
     */
    @Override
    public Result addMatterGuideGroup(MatterGuideGroup matterGuideGroup) {
        if(checkGroupName(matterGuideGroup.getName())){
            return Result.fail("分组名称已存在");
        }
        matterGuideGroup.setId(null);
        matterGuideGroup.setStatus(0);
        matterGuideGroup.setCreateTime(new Date());
        if (StringUtils.isBlank(matterGuideGroup.getGroupId())) {
            matterGuideGroup.setGroupId(IdUtil.simpleUUID());
        }
        save(matterGuideGroup);
        return Result.success(matterGuideGroup);
    }

    @Override
    public Result addOrUpdateMatterGroupList(List<MatterGuideGroup> matterGuideGroupList) {
        if(CollectionUtil.isEmpty(matterGuideGroupList)){
            return Result.fail("没有需要保存的分组");
        }
        //设置排序
        AtomicReference<Integer> sorted = new AtomicReference<>(5);
        matterGuideGroupList.forEach(matterGuideGroup -> {
            matterGuideGroup.setSorted(sorted.get());
            sorted.getAndSet(sorted.get() + 1);

            if (matterGuideGroup.getId() != null) {
                //编辑
                updateById(matterGuideGroup);
                matterGuideGroup.setUpdateTime(new Date());
            }else {
                //新增
                matterGuideGroup.setCreateTime(new Date());
                matterGuideGroup.setStatus(0);
                if (StringUtils.isBlank(matterGuideGroup.getGroupId())) {
                    matterGuideGroup.setGroupId(IdUtil.simpleUUID());
                }
                save(matterGuideGroup);
            }
        });
        return Result.success(matterGuideGroupList);
    }



    //检查重复名称
    private boolean checkGroupName(String groupName) {
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_GROUP.STATUS.eq(0))
                .and(MATTER_GUIDE_GROUP.NAME.eq(groupName));
        if (count(queryWrapper)>0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * @param matterGuideGroupParam
     * @description: 获取所有的字段分组信息
     * @author: caohaifeng
     * @date: 2024/8/19 10:29
     */
    @Override
    public Result<List<MatterGuideGroup>> getMatterGuideGroupList(MatterGuideGroupParam matterGuideGroupParam) {
        Wrappers wrappers = Wrappers.init()
                .where(MATTER_GUIDE_GROUP.STATUS.eq(0))
                .and(StringUtils.isNotBlank(matterGuideGroupParam.getMatterId()), MATTER_GUIDE_GROUP.MATTER_ID.eq(matterGuideGroupParam.getMatterId()))
                .and(StringUtils.isNotBlank(matterGuideGroupParam.getSceneId()), MATTER_GUIDE_GROUP.SCENE_ID.eq(matterGuideGroupParam.getSceneId()))
                .orderBy(MATTER_GUIDE_GROUP.CREATE_TIME.asc());
        final List<MatterGuideGroup> list = list(wrappers);
        list.forEach(item -> item.setIdx(item.getId() + ""));
        return Result.success(list);
    }

    /**
     * @param id
     * @description: 获取所有的字段分组信息
     * @author: caohaifeng
     * @date: 2024/8/19 10:31
     */
    @Override
    public Result delMatterGuideGroupById(Long id) {
        // 根据 ID 获取 MatterGuideGroup 实例
        MatterGuideGroup matterGuideGroup = getById(id);

        // 检查对象是否为空
        if (matterGuideGroup == null) {
            return Result.fail("未找到指定的分组信息");
        }

        matterGuideGroup.setStatus(1);
        if (updateById(matterGuideGroup)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * @param matterGuideType
     * @description: 获取所有的事项类型信息
     * @author: caohaifeng
     * @date: 2024/8/22 10:52
     */
    @Override
    public Result getMatterGuideTypeList(MatterGuideType matterGuideType) {
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(MATTER_GUIDE_TYPE.STATUS.eq(0))
                .orderBy(MATTER_GUIDE_TYPE.CREATE_TIME.desc());
        final List<MatterGuideType> list = matterGuideTypeMapper.selectListByQuery(queryWrapper);
        list.forEach(item -> item.setIdx(item.getId() + ""));
        return Result.success(list);
    }

    @Override
    public List<MatterGuideGroup> getBySceneIId(String sceneId) {
        if (StringUtils.isBlank(sceneId)) {
            return Lists.newArrayList();
        }

        return Wrappers.of(mapper)
                .where(MATTER_GUIDE_GROUP.SCENE_ID.eq(sceneId))
                .and(MATTER_GUIDE_GROUP.STATUS.eq(StringConstant.ONE))
                .list();
    }

    @Override
    public List<MatterGuideGroup> getByMatterId(String matterId) {
        if (StringUtils.isBlank(matterId)) {
            return Lists.newArrayList();
        }

        return Wrappers.of(mapper)
                .where(MATTER_GUIDE_GROUP.MATTER_ID.eq(matterId))
                .and(MATTER_GUIDE_GROUP.STATUS.eq(NumberConstants.ZERO))
                .list();
    }

}