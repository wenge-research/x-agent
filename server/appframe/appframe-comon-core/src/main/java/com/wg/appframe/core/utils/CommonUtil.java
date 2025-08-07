/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import com.wg.appframe.core.constant.NumberConstants;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 常用基础工具类
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-14
 */
public class CommonUtil {

    /**
     * 线程休眠
     *
     * @param seconds 秒
     */
    public static void sleep(long seconds) {
        try {
            Thread.sleep(seconds * NumberConstants.THOUSAND);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }


    /**
     * 生成UUID
     *
     * @return 返回
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成自定义uuid
     *
     * @param custom 自动字符串
     * @return 返回
     */
    public static String generateUUID(String custom) {
        return UUID.randomUUID().toString().replaceAll("-", "") + custom;
    }

    /**
     * 判断集合是否非空
     *
     * @param c 集合
     * @return 返回
     */
    public static boolean isNotEmptyCollection(Collection<?> c) {
        return (c != null && c.size() > 0);
    }


    /**
     * 判断集合是否空
     *
     * @param c 集合
     * @return 返回
     */
    public static boolean isEmptyCollection(Collection<?> c) {
        return !isNotEmptyCollection(c);
    }

    /**
     * 判断Map是否非空
     *
     * @param map map
     * @return 返回
     */
    public static boolean isNotEmptyMap(Map<?, ?> map) {
        return (map != null && map.size() > 0);
    }

    /**
     * 判断Map是否空
     *
     * @param map map
     * @return 返回
     */
    public static boolean isEmptyMap(Map<?, ?> map) {
        return !isNotEmptyMap(map);
    }

    /**
     * long转换，异常默认返回-1
     *
     * @param text id
     * @return 返回
     */
    public static Long parseLong(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        long id = -1L;
        try {
            id = Long.parseLong(text);
        } catch (Exception ignored) {
        }
        return id;
    }

    /**
     * 判断数组是否非空
     *
     * @param array 数组
     * @return 返回
     */
    public static boolean isNotEmptyArray(Object[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组是否为空
     *
     * @param array 数组
     * @return 返回
     */
    public static boolean isEmptyArray(Object[] array) {
        return !isNotEmptyArray(array);
    }


    /**
     * 随机生成整数列表
     *
     * @param listSize
     * @param rCount
     * @return
     */
    public static List<Integer> randomInteger(int listSize, int rCount) {
        List<Integer> list = null;
        if (listSize <= rCount) {
            list = new ArrayList<Integer>();
            for (int idx = 0; idx < listSize; idx++) {
                list.add(idx);
            }
        } else {
            list = new ArrayList<Integer>(rCount);
            while (list.size() < rCount) {
                Integer next = (int) (Math.random() * listSize);
                if (list.contains(next)) {
                    continue;
                }
                list.add(next);
            }
            Collections.sort(list);
        }

        return list;
    }


    /**
     * @param maxNum max number
     * @param minNum min number
     * @param count  random list size
     * @return 返回
     */
    public static List<Integer> randomInteger(int maxNum, int minNum, int count) {
        List<Integer> list = null;
        if (maxNum <= count) {
            list = new ArrayList<Integer>();
            for (int i = minNum; i < maxNum; i++) {
                list.add(i);
            }
        } else {
            list = new ArrayList<Integer>(count);
            while (list.size() < count) {
                Integer next = (int) ((Math.random() * (maxNum - minNum)) + minNum);
                if (list.contains(next)) {
                    continue;
                }
                list.add(next);
            }
        }
        return list;
    }

    public static List<Integer> randomIntegerSoft(int maxNum, int minNum, int count) {
        List<Integer> list = randomInteger(maxNum, minNum, count);
        Collections.sort(list);
        return list;
    }

    public static void randomList(List<?> list) {
        Collections.shuffle(list);
    }

    /**
     * 含有unicode 的字符串转一般字符串
     *
     * @param unicodeStr 混有 Unicode 的字符串
     * @return 返回
     */
    public static String unicodeText2String(String unicodeStr) {
        int length = unicodeStr.length();
        int count = 0;

        // 正则匹配条件，可匹配"\\u"1到4位，一般是4位可直接使用 String regex = "\\\\u[a-f0-9A-F]{4}";
        String regex = "\\\\u[a-f0-9A-F]{1,4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unicodeStr);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            // 原本的Unicode字符
            String oldChar = matcher.group();

            // 转换为普通字符
            String newChar = unicode2String(oldChar);

            // 在遇见重复出现的unicode代码的时候会造成从源字符串获取非unicode编码字符的时候截取索引越界等
            int index = matcher.start();

            // 添加前面不是unicode的字符
            sb.append(unicodeStr.substring(count, index));

            // 添加转换后的字符
            sb.append(newChar);

            // 统计下标移动的位置
            count = index + oldChar.length();
        }

        // 添加末尾不是Unicode的字符
        sb.append(unicodeStr.substring(count, length));
        return sb.toString();
    }

    /**
     * 字符串转换unicode
     *
     * @param string
     * @return
     */
    public static String string2Unicode(String string) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < string.length(); idx++) {
            // 取出每一个字符
            char c = string.charAt(idx);

            // 转换为unicode
            sb.append("\\u").append(Integer.toHexString(c));
        }
        return sb.toString();
    }

    /**
     * unicode 转字符串
     *
     * @param unicode 全为 Unicode 的字符串
     * @return 返回
     */
    public static String unicode2String(String unicode) {
        StringBuilder sb = new StringBuilder();
        String[] hex = unicode.split("\\\\u");
        for (int idx = 1; idx < hex.length; idx++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[idx], 16);
            sb.append((char) data);
        }
        return sb.toString();
    }

    /**
     * base64字符串转字符串
     *
     * @param base64Text base64Text
     * @return 返回
     * @throws IOException 抛出异常
     */
    public static String decodeBase64(String base64Text) throws IOException {
        if (StringUtils.isBlank(base64Text)) {
            return StringUtils.EMPTY;
        }
        return new String(cn.hutool.core.codec.Base64.decode(base64Text), StandardCharsets.UTF_8);
    }

    /**
     * 字符串转Base64
     *
     * @param text 字符串
     * @return 返回
     */
    public static String encodeBase64(String text) {
        if (StringUtils.isBlank(text)) {
            return StringUtils.EMPTY;
        }
        return cn.hutool.core.codec.Base64.encode(text.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        String text = "\\u003cp data-sourcepos=\\\"1:1-1:19\\\" dir=\\\"auto\\\"\\u003eHello world! \\u003cgl-emoji title=\\\"party popper\\\" data-name=\\\"tada\\\" data-unicode-version=\\\"6.0\\\"\\u003e\uD83C\uDF89\\u003c/gl-emoji\\u003e\\u003c/p\\u003e\n";
        System.out.println(unicodeText2String(text));
    }


    // 48 -> 126   range 78
    public static String createRandomPassward(int range) {
        if (range <= 0) {
            return null;
        }

        Random random = new Random();
        char[] arr = new char[range];
        for (int i = 0; i < range; i++) {
            char temp = 74;
            if (i == 0) {
                // 数字 48-57
                temp = createRandomChar(48, 57);
            } else if (i == 1) {
                // 特殊符号 35-38
                temp = createRandomChar(42, 47);
            } else if (i == 2) {
                // 大写字母 65-90
                temp = createRandomChar(65, 90);
            } else if (i == 3) {
                // 小写字母 97-122
                temp = createRandomChar(97, 122);
            } else {
                // 64-90
                temp = createRandomChar(42, 122);
            }
            arr[i] = temp;
        }
        return new String(arr);
    }

    private static char createRandomChar(int left, int right) {
        int range = right - left;
        Random random = new Random();
        int charInt = random.nextInt(range + 1) + left;
        return (char) charInt;
    }

    public static String[] toStringArray(List<String> taskIds) {

        String[] result = new String[taskIds.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = taskIds.get(i);
        }
        return result;
    }

    public static String arrToString(Collection<String> taskIds, String str) {

        StringJoiner sj = new StringJoiner(str);
        taskIds.forEach(sj::add);
        return sj.toString();
    }
}
