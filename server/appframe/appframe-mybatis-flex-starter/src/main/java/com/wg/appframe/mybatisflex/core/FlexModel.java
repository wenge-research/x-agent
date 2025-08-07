package com.wg.appframe.mybatisflex.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryOrderBy;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

public class FlexModel<T extends FlexModel<T>> extends Model<FlexModel<T>> {


    @Column(ignore = true)
    @JsonIgnore
    @Getter
    private QueryWrapper wrapperWg;

    public FlexModel() {
        this.wrapperWg = super.getQueryWrapper();
    }

    // public QueryWrapper getCurrentWrapper() {
    //     return queryWrapper = getQueryWrapper();
    // }

    public T where(boolean flag, QueryCondition queryCondition) {
        if (flag) {
           super.where(queryCondition);
        }
        return (T) this;
    }

    public T where(boolean flag, String sql) {
        if (flag) {
            return (T) super.where(sql);
        }
        return (T) this;
    }

    public T where(boolean flag, String sql, Object... params) {
        if (flag) {
            return (T) super.where(sql, params);
        }
        return (T) this;
    }

    public T and(boolean flag, QueryCondition queryCondition) {
        if (flag) {
            return (T) super.and(queryCondition);
        }
        return (T) this;
    }

    public T and(boolean flag, String sql) {
        if (flag) {
            return (T) super.and(sql);
        }
        return (T) this;
    }

    public T and(boolean flag, String sql, Object... params) {
        if (flag) {
            return (T) super.and(sql, params);
        }
        return (T) this;
    }


    public T or(boolean flag, QueryCondition queryCondition) {
        if (flag) {
            return (T) super.or(queryCondition);
        }
        return (T) this;
    }

    public T or(boolean flag, String sql) {
        if (flag) {
            return (T) super.or(sql);
        }
        return (T) this;
    }

    public T or(boolean flag, String sql, Object... params) {
        if (flag) {
            return (T) super.or(sql, params);
        }
        return (T) this;
    }

    public List<T> lists() {
        return (List<T>) this.baseMapper().selectListByQuery(this.getQueryWrapper());
    }

    public Page<T> pages(Page<T> page) {
        return (Page<T>) this.baseMapper().paginate(page.getPageNumber(), page.getPageSize(), this.getQueryWrapper());
    }

    public T orderBys(QueryOrderBy... orderBys) {
        return (T) super.orderBy(orderBys);
    }


    public T orderBys(String... orderBys) {
        return (T) super.orderBy(orderBys);
    }

}
