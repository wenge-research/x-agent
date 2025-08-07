/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Bean拷贝工具类
 * </p>
 *
 * @author wang.y
 * @since 2020-05-13
 */
@Slf4j
public class BeanUtil {

    /**
     * 复制对象内容到另外一个对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <T> 泛型
     * @return 返回
     */
    public static <T> T copy(Object source, final Class<T> target) {
        if (Objects.isNull(source)) {
            return null;
        }
        T voObj = null;
        try {
            voObj = target.newInstance();
            BeanUtils.copyProperties(source, voObj);
        } catch (Exception e) {
            log.error("copy properties to :{} found error.", target.getName(), e);
        }
        return voObj;
    }

    /**
     * 复制对象列表
     *
     * @param sourceList 源对象列表
     * @param targetClass 目标对象类型
     * @param <T> 泛型
     * @return 返回
     */
    public static <T> List<T> copyList(List<?> sourceList, final Class<T> targetClass) {
        List<T> dataList = new ArrayList<>();
        for (Object source : sourceList) {
            dataList.add(copy(source, targetClass));
        }
        return dataList;
    }

    /**
     * From转化为Po，进行后续业务处理
     *
     * @param clazz
     * @return
     */
    public <T> T toPo(Class<T> clazz) {
        T t = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(this, t);
        return t;
    }

    private BeanUtil() {
    }
}
