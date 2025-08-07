package com.wenge.model.utils;

import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @Description:
 * @Author: LWQ
 * @Date: 2023/4/2 15:00
 */
public class CustomCollectionUtils {

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return collection != null && collection.size() > 0;
    }
}