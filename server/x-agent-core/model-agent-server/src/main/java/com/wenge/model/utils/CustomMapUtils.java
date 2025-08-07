package com.wenge.model.utils;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @Description:
 * @Author: LWQ
 */
public class CustomMapUtils {

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return map != null && map.size() > 0;
    }
}