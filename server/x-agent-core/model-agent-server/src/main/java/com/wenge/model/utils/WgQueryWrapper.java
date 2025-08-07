package com.wenge.model.utils;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapperAdapter;

public class WgQueryWrapper<T> extends QueryWrapperAdapter<WgQueryWrapper<T>> {

    private static final long serialVersionUID = -5260199832823195683L;

    private final BaseMapper<T> baseMapper;

    public WgQueryWrapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public static <T> WgQueryWrapper<T> of(BaseMapper<T> baseMapper) {
        return new WgQueryWrapper<>(baseMapper);
    }

    public WgQueryWrapper<T> and(Boolean condition, QueryCondition queryCondition) {
        if (null != condition && condition) {
            super.and(queryCondition);
        }
        return this;
    }
}
