package com.wg.appframe.mybatisflex.handler;

import com.mybatisflex.core.util.CollectionUtil;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlexListenerAssistant {

    public static void onFill(Object entity, Function<FieldFill, Boolean> filterFlag) {
        try {
            // 获取类的所有字段（属性）
            Field[] fields = entity.getClass().getDeclaredFields();
            if (null == fields || fields.length == 0) {
                return;
            }

            // 获取属性有OnFieldFill注解的字段，并且属性有INSERT或则INSERT_UPDATE
            List<Field> fieldList = Arrays.stream(fields).filter(field -> {
                OnFieldFill annotation = field.getAnnotation(OnFieldFill.class);
                if (null == annotation) {
                    return false;
                }
                FieldFill fill = annotation.fill();
                return filterFlag.apply(fill);
            }).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(fieldList)) {
                return;
            }

            for (Field field : fieldList) {
                // 提取要赋值的内容，1.优先提取注解中的value；2.如果为空，则从MDC中获取数据
                OnFieldFill OnFieldFill = field.getAnnotation(OnFieldFill.class);
                Class fieildClass = OnFieldFill.fieildClass();
                // 判断field的字段类型和aclass是否一致
                if (!field.getType().equals(fieildClass)) {
                    continue;
                }
                String value = OnFieldFill.value();
                if (StringUtils.isBlank(value)) {
                    String mdcKey = "";
                    // 优先从mdc中配置的key获取数据
                    mdcKey = OnFieldFill.mdcKey();

                    // 如果mdc配置的key为空，则取实体类属性名称
                    if (StringUtils.isBlank(mdcKey)) {
                        mdcKey = field.getName();
                    }
                    value = MDC.get(mdcKey);
                }
                if (StringUtils.isNotBlank(value)) {
                    setValue(field, entity, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置属性的值
     * @param fieldName
     * @param entity
     * @param accountName
     */
    public static void setValue(Field fieldName, Object entity, String accountName) {
        try {
            fieldName.setAccessible(true);
            Object object = fieldName.get(entity);
            // 如果属性值为空，则赋值
            if (null == object) {
                fieldName.set(entity, accountName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断实体类是否有某个属性
     * @param obj
     * @param fieldName
     * @return
     */
    public static boolean hasField(Object obj, String fieldName) {
        Class<?> cls = obj.getClass();
        try {
            cls.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * 驼峰命名法
     * @param input
     * @return
     */
    public static String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }
        return result.toString();
    }

}
