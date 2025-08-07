package com.wg.appframe.mybatisflex.core;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.SqlUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Wrappers<T> extends QueryWrapperAdapter<Wrappers<T>> {

    private static final long serialVersionUID = -5260199832823195613L;

    private BaseMapper<T> baseMapper;

    public Wrappers(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }
    public Wrappers() {
    }

    public static <T> Wrappers<T> of(BaseMapper<T> baseMapper) {
        return new Wrappers<>(baseMapper);
    }
    public static <T> Wrappers<T> init() {
        return new Wrappers<>();
    }

    // --------------------------------------- and ------------------------------------------------------------------
    public Wrappers<T> and(Boolean condition, QueryCondition queryCondition) {
        if (null != condition && condition) {
            super.and(queryCondition);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, String sql) {
        if (null != condition && condition) {
            super.and(sql);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, String sql, Object... params) {
        if (null != condition && condition) {
            super.and(sql, params);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, LambdaGetter<T> fn) {
        if (null != condition && condition) {
            super.and(fn);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, Consumer<QueryWrapper> consumer) {
        if (null != condition && condition) {
            super.and(consumer);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, Map<String, Object> whereConditions) {
        if (null != condition && condition) {
            super.and(whereConditions);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        if (null != condition && condition) {
            super.and(whereConditions, operators);
        }
        return this;
    }
    public Wrappers<T> and(Boolean condition, Map<String, Object> whereConditions, Map<String, SqlOperator> operators, SqlConnector innerConnector) {
        if (null != condition && condition) {
            super.and(whereConditions, operators, innerConnector);
        }
        return this;
    }

    // --------------------------------------- or ------------------------------------------------------------------
    public Wrappers<T> or(Boolean condition, QueryCondition queryCondition) {
        if (null != condition && condition) {
            super.or(queryCondition);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, String sql) {
        if (null != condition && condition) {
            super.or(sql);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, String sql, Object... params) {
        if (null != condition && condition) {
            super.or(sql, params);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, LambdaGetter<T> fn) {
        if (null != condition && condition) {
            super.or(fn);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, Consumer<QueryWrapper> consumer) {
        if (null != condition && condition) {
            super.or(consumer);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, Map<String, Object> whereConditions) {
        if (null != condition && condition) {
            super.or(whereConditions);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        if (null != condition && condition) {
            super.or(whereConditions, operators);
        }
        return this;
    }
    public Wrappers<T> or(Boolean condition, Map<String, Object> whereConditions, Map<String, SqlOperator> operators, SqlConnector innerConnector) {
        if (null != condition && condition) {
            super.or(whereConditions, operators, innerConnector);
        }
        return this;
    }

    // --------------------------------------- where ------------------------------------------------------------------
    public Wrappers<T> where(Boolean condition, QueryCondition queryCondition) {
        if (null != condition && condition) {
            super.where(queryCondition);
        }
        return this;
    }
    public Wrappers<T> where(Boolean condition, String sql) {
        if (null != condition && condition) {
            super.where(sql);
        }
        return this;
    }
    public Wrappers<T> where(Boolean condition, String sql, Object... params) {
        if (null != condition && condition) {
            super.where(sql, params);
        }
        return this;
    }
    public Wrappers<T> where(Boolean condition, LambdaGetter<T> fn) {
        if (null != condition && condition) {
            super.where(fn);
        }
        return this;
    }
    public Wrappers<T> where(Boolean condition, Map<String, Object> whereConditions) {
        if (null != condition && condition) {
            super.where(whereConditions);
        }
        return this;
    }
    public Wrappers<T> where(Boolean condition, Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        if (null != condition && condition) {
            super.where(whereConditions, operators);
        }
        return this;
    }

    public Wrappers<T> orderBys(QueryOrderBy... orderBys) {
        super.orderBy(orderBys);
        return this;
    }


    public Wrappers<T> orderBys(String... orderBys) {
        super.orderBy(orderBys);
        return this;
    }


    public long count() {
        return this.baseMapper.selectCountByQuery(this);
    }

    public boolean exists() {
        return SqlUtil.toBool(this.count());
    }

    public T one() {
        return this.baseMapper.selectOneByQuery(this);
    }

    public <R> R oneAs(Class<R> asType) {
        return this.baseMapper.selectOneByQueryAs(this, asType);
    }

    public T oneWithRelations() {
        return this.baseMapper.selectOneWithRelationsByQuery(this);
    }

    public <R> R oneWithRelationsAs(Class<R> asType) {
        return this.baseMapper.selectOneWithRelationsByQueryAs(this, asType);
    }

    public Optional<T> oneOpt() {
        return Optional.ofNullable(this.baseMapper.selectOneByQuery(this));
    }

    public <R> Optional<R> oneAsOpt(Class<R> asType) {
        return Optional.ofNullable(this.baseMapper.selectOneByQueryAs(this, asType));
    }

    public Optional<T> oneWithRelationsOpt() {
        return Optional.ofNullable(this.baseMapper.selectOneWithRelationsByQuery(this));
    }

    public <R> Optional<R> oneWithRelationsAsOpt(Class<R> asType) {
        return Optional.ofNullable(this.baseMapper.selectOneWithRelationsByQueryAs(this, asType));
    }

    public Object obj() {
        return this.baseMapper.selectObjectByQuery(this);
    }

    public <R> R objAs(Class<R> asType) {
        return this.baseMapper.selectObjectByQueryAs(this, asType);
    }

    public Optional<Object> objOpt() {
        return Optional.ofNullable(this.baseMapper.selectObjectByQuery(this));
    }

    public <R> Optional<R> objAsOpt(Class<R> asType) {
        return Optional.ofNullable(this.baseMapper.selectObjectByQueryAs(this, asType));
    }

    public List<Object> objList() {
        return this.baseMapper.selectObjectListByQuery(this);
    }

    public <R> List<R> objListAs(Class<R> asType) {
        return this.baseMapper.selectObjectListByQueryAs(this, asType);
    }

    public List<T> list() {
        return this.baseMapper.selectListByQuery(this);
    }

    public List<T> listWithRelations() {
        return this.baseMapper.selectListWithRelationsByQuery(this);
    }

    public <R> List<R> listAs(Class<R> asType) {
        return this.baseMapper.selectListByQueryAs(this, asType);
    }

    public <R> List<R> listWithRelationsAs(Class<R> asType) {
        return this.baseMapper.selectListWithRelationsByQueryAs(this, asType);
    }

    public Page<T> page(Page<T> page) {
        return this.baseMapper.paginate(page, this);
    }

    public Page<T> pageWithRelations(Page<T> page) {
        return this.baseMapper.paginateWithRelations(page, this);
    }

    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        return this.baseMapper.paginateAs(page, this, asType);
    }

    public <R> Page<R> pageWithRelationsAs(Page<R> page, Class<R> asType) {
        return this.baseMapper.paginateWithRelationsAs(page, this, asType);
    }

}
