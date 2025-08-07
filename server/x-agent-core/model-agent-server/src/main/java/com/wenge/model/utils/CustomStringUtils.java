package com.wenge.model.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yjadmin
 */
@Slf4j
public class CustomStringUtils {

    private static final String EMPTY_STR_ = "";

    private static final String DEFAULT_SPLITTER_ = ",";

    /**
     * 方法描述:集合转字符串
     *
     * @param splitter 集合变为字符串的连接符号
     * @param: * @param list 目标集合
     */
    public static String list2String(List<String> list, String splitter) {
        if (CustomCollectionUtils.isEmpty(list)){
            return EMPTY_STR_;
        }
        return list.stream().map(String::valueOf).collect(Collectors.joining(splitter));
    }

    public static String list2Str(List<String> list, String splitter) {
        if (CustomCollectionUtils.isEmpty(list)) {
            return EMPTY_STR_;
        }
        return String.join(splitter, list);
    }

    public static String list2Str(List<String> list) {
        return list2Str(list,DEFAULT_SPLITTER_);
    }

    public static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(@Nullable Object str) {
        return str != null && !"".equals(str);
    }
}
