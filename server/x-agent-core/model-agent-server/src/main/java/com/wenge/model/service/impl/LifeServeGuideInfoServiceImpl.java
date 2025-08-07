package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.LifeServeGuideInfo;
import com.wenge.model.mapper.LifeServeGuideInfoMapper;
import com.wenge.model.service.LifeServeGuideInfoService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wenge.model.entity.table.LifeServeGuideInfoTableDef.LIFE_SERVE_GUIDE_INFO;


@Service
@Slf4j
public class LifeServeGuideInfoServiceImpl extends ServiceImpl<LifeServeGuideInfoMapper, LifeServeGuideInfo> implements LifeServeGuideInfoService {

    @Autowired
    private LifeServeGuideInfoMapper lifeServeGuideInfoMapper;

    @Override
    public List<LifeServeGuideInfo> getListByHandleMethodIdAndInterceptWordId(Long handleMethodId, Long interceptWordId) {
        if (Objects.isNull(handleMethodId) || Objects.isNull(interceptWordId)) {
            return new ArrayList<>();
        }

        Wrappers<Object> wrappers = Wrappers.init()
                .and(LIFE_SERVE_GUIDE_INFO.HANDLE_METHOD_ID.eq(handleMethodId))
                .and(LIFE_SERVE_GUIDE_INFO.INTERCEPT_WORD_ID.eq(interceptWordId))
                .and(LIFE_SERVE_GUIDE_INFO.DELETE_FLAG.eq(0));
        return lifeServeGuideInfoMapper.selectListByQuery(wrappers);
    }

    @Override
    public List<LifeServeGuideInfo> getListByHandleMethodIdAndMatterGuideId(String matterGuideId) {
        if (StringUtils.isEmpty(matterGuideId)) {
            return new ArrayList<>();
        }

        Wrappers<Object> wrappers = Wrappers.init()
                .and(LIFE_SERVE_GUIDE_INFO.MATTER_GUIDE_ID.eq(matterGuideId))
                .and(LIFE_SERVE_GUIDE_INFO.DELETE_FLAG.eq(0));
        return lifeServeGuideInfoMapper.selectListByQuery(wrappers);
    }

}