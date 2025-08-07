/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import com.wg.appframe.core.constant.NumberConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * <p>
 * 日期工具类
 * </p>
 *
 * @author yangyunjun
 * @since 2021-06-30
 */
@Slf4j
public class DateUtil {

    /**
     * 默认转换格式
     */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 到毫秒
     */
    public static final String TIME_MILLISECOND = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 日期转换格式
     */
    public static final String DATE_FORMAT_D = "yyyy-MM-dd";

    /**
     * 日期加时分转换格式
     */
    public static final String DATE_FORMAT_M = "yyyy-MM-dd HH:mm";

    /**
     * UTC转换格式
     */
    public static final String PATTERN_FOR_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * long转换成日期
     *
     * @param dateFormat 转换格式
     * @param millSecond 日期long值
     * @return 返回日期字符串
     */
    public static String transferLongToDate(String dateFormat, Long millSecond) {
        Date time = new Date(millSecond);
        SimpleDateFormat formats = new SimpleDateFormat(dateFormat);
        return formats.format(time);
    }

    /**
     * long转换成日期
     *
     * @param dateFormat 转换格式
     * @param millSecond 日期long值
     * @return 返回日期字符串
     */
    public static String longToDate(String dateFormat, Long millSecond) {
        Date time = new Date(millSecond);
        LocalDateTime localDate = toLocalDateTime(toZonedDateTime(time, TIMEZONE_GMT8), TIMEZONE_GMT8);
        return localDate.format(DateTimeFormatter.ofPattern(dateFormat));
    }


    /**
     * 获取某一时间是当年的第几周
     *
     * @param date 日期
     * @return 返回
     */
    public static int getWeekOfYear(Date date) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(date);

        // 获得周数
        return g.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取当前时间离一天结束剩余秒数
     *
     * @param currentDate 当前时间
     * @return 返回
     */
    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }

    /**
     * 获取当天0点时间
     *
     * @return long 毫秒
     */
    public static Long getTimesMorning() {
        return getTimes(0);
    }

    /**
     * 获取当天24点时间
     *
     * @return long 毫秒
     */
    public static Long getTimesNight() {
        return getTimes(NumberConstants.TWENTY_FOUR);
    }

    private static Long getTimes(Integer hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    /**
     * 获取UTC time字符串
     *
     * @return 返回
     */
    public static String getUTCTimeStr() {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
        StringBuilder timeBuilder = new StringBuilder();

        // 取得本地时间
        Calendar cal = Calendar.getInstance();

        // 取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);

        // 取得夏令时差
        int dstOffset = cal.get(Calendar.DST_OFFSET);

        // 从本地时间里扣除这些差量，即可以取得UTC时间
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        timeBuilder.append(year).append("-").append(month).append("-").append(day);
        timeBuilder.append(" ").append(hour).append(":").append(minute).append(":").append(second);
        try {
            format.parse(timeBuilder.toString());
            return timeBuilder.toString();
        } catch (ParseException e) {
            log.error("==>get utc time found error.");
        }
        return null;
    }

    /**
     * timestamp 转String
     *
     * @param timestamp timestamp
     * @return 返回
     */
    public static String timeStamp2Date(Timestamp timestamp) {
        return new SimpleDateFormat(DEFAULT_FORMAT).format(timestamp);
    }


    /**
     * timestamp 转String
     *
     * @param timestamp timestamp
     * @param format    format
     * @return 返回
     */
    public static String timeStamp2Date(Timestamp timestamp, String format) {
        return new SimpleDateFormat(format).format(timestamp);
    }


    /**
     * 字符串转时间戳
     *
     * @param timeStr 时间字符串
     * @param format  format
     * @return 返回
     */
    public static long strToTimeMillis(String timeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : NumberConstants.MINUS_ONE;
    }

    /**
     * 比较时间差(秒)
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 返回
     */
    public static int countTimeDifference(long startTime, long endTime) {
        return (int) ((endTime - startTime) / NumberConstants.THOUSAND);
    }

    /**
     * 获取当前时间字符串
     *
     * @param pattern pattern
     * @return 返回
     */
    public static String getCurrentTime(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    /**
     * 时间相加
     *
     * @param pattern pattern
     * @param time    时间
     * @param amount  增加时间
     * @return 返回
     */
    public static String calculate(String pattern, String time, long amount) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date = format.parse(time);
            long dateLong = date.getTime() + amount;
            time = format.format(dateLong);
        } catch (ParseException e) {
            return time;
        }
        return time;
    }

    /**
     * 获取当前时间
     *
     * @return 返回
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前时间字符串
     *
     * @return 返回
     */
    public static String getCurrentDateString(String format) {
        return dateToString(getCurrentDate(), format);
    }

    /**
     * 获取当前时间字符串
     *
     * @return 返回
     */
    public static String getCurrentDateString() {
        return dateToString(getCurrentDate(), DEFAULT_FORMAT);
    }

    /**
     * 字符串转日期
     *
     * @param dateString 日期字符串
     * @param format     format
     * @return 返回
     * @throws Exception 异常
     */
    public static Date stringToDate(String dateString, String format) {
        if (StringUtils.isNotBlank(dateString)) {
            String dateFormat = StringUtils.isNotBlank(format) ? format : DEFAULT_FORMAT;
            DateFormat df = getDateFormat(dateFormat);
            try {
                return df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取SimpleDateFormat
     *
     * @param format format
     * @return 返回
     */
    public static DateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format);

    }

    /**
     * 字符串转日期
     *
     * @param dateString 日期字符串
     * @return 返回
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, DEFAULT_FORMAT);
    }


    /**
     * 日期转字符串
     *
     * @param date   日期
     * @return 返回
     */
    public static String dateToString(Date date) {
       return dateToString(date, null);
    }

    /**
     * 日期转字符串
     *
     * @param date   日期
     * @param format format
     * @return 返回
     */
    public static String dateToString(Date date, String format) {
        String dateString = "";
        if (date != null) {
            String dateFormat = StringUtils.isNotBlank(format) ? format : DEFAULT_FORMAT;
            DateFormat df = getDateFormat(dateFormat);
            dateString = df.format(date);
        }
        return dateString;
    }

    /**
     * 获取上一天
     *
     * @param date 日期
     * @return 返回
     */
    public static Date getPreDay(Date date) {
        return getPastDate(date, 1);
    }

    /**
     * 获取后几天
     *
     * @param date       日期
     * @param pastNumber 天数
     * @return 返回
     */
    public static Date getPastDate(Date date, int pastNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 小时
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, (-pastNumber));
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取下一天
     *
     * @param date 日期
     * @return 返回
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取上一年的时间
     *
     * @param date 日期
     * @return 返回
     */
    public static Date getPreYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, NumberConstants.MINUS_ONE);
        return calendar.getTime();
    }

    /**
     * 获取上个月时间
     *
     * @param date 日期
     * @return 返回
     */
    public static Date getPreMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, NumberConstants.MINUS_ONE);
        return calendar.getTime();
    }

    /**
     * 获取下个月时间
     *
     * @param date 日期
     * @return 返回
     */
    public static Date getNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, NumberConstants.ONE);
        return calendar.getTime();
    }

    /**
     * 获取零时的时间戳
     *
     * @param date 日期
     * @return 返回
     */
    public static long getZeroHourTime(Date date) {
        if (date == null) {
            return 0L;
        }
        String dateStr = dateToString(date, DATE_FORMAT_D) + " 00:00:00";
        Date d = null;
        try {
            d = stringToDate(dateStr);
        } catch (Exception e) {
            d = date;
        }
        return d.getTime();
    }

    /**
     * 获取上一天零时的时间戳
     *
     * @return 返回
     */
    public static long getPreZeroHourTime() {
        Date currentDate = new Date();
        String preDateStr = dateToString(getPreDay(currentDate), DATE_FORMAT_D) + " 00:00:00";
        Date preDate = null;
        try {
            preDate = stringToDate(preDateStr);
        } catch (Exception e) {
            preDate = getPreDay(currentDate);
        }
        return preDate.getTime();
    }

    /**
     * timeStamp转Date
     *
     * @param time time
     * @return 返回
     */
    public static Date timeStampToDate(long time) {
        return new Date(time);
    }

    /**
     * 日期转TimeStamp
     *
     * @param date 日期
     * @return 返回
     */
    public static long dateToTimeStamp(Date date) {
        return date.getTime();
    }

    /**
     * 获取当前时间戳
     *
     * @return 返回
     */
    public static Long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }


    /**
     * UTC字符串转日期
     *
     * @param text utc日期字符串
     * @return 返回
     */
    public static Date parseUTCText(String text) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (text.contains(".")) {
            String prefix = text.substring(0, text.indexOf("."));
            String suffix = text.substring(text.indexOf("."));
            if (suffix.length() >= NumberConstants.FIVE) {
                suffix = suffix.substring(0, NumberConstants.FOUR) + "Z";
            } else {
                int len = NumberConstants.FIVE - suffix.length();
                StringBuilder temp = new StringBuilder();
                temp.append(suffix.substring(0, suffix.length() - 1));
                for (int idx = 0; idx < len; idx++) {
                    temp.append("0");
                }
                suffix = temp + "Z";
            }
            text = prefix + suffix;
        } else {
            text = text.substring(0, text.length() - 1) + ".000Z";
        }
        Date date = sdf.parse(text);
        String ttt = dateToString(date, DEFAULT_FORMAT);
        return date;
    }

    /**
     * 根据时间算第几季度
     *
     * @param date 时间
     * @return 季度数
     */
    public static int getQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        System.out.println("===mouth=" + month);
        if (month <= NumberConstants.THREE) {
            return NumberConstants.ONE;
        }
        if (month <= NumberConstants.SIX) {
            return NumberConstants.TWO;
        }
        if (month <= NumberConstants.NINE) {
            return NumberConstants.THREE;
        }
        if (month <= NumberConstants.TWELVE) {
            return NumberConstants.FOUR;
        }
        return NumberConstants.MINUS_ONE;
    }

    /**
     * 根据实际获取年份
     *
     * @param date 时间
     * @return 返回
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 根据时间获取月份
     *
     * @param date 时间
     * @return 返回
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据时间获取Date
     *
     * @param date date
     * @return 返回
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    /**
     * 获取第一天
     *
     * @param year  年份
     * @param month 月份
     * @return 返回
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int firstDay = cal.getMinimum(Calendar.DATE);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_D);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取最后一天
     *
     * @param year  年份
     * @param month 月份
     * @return 返回
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_D);
        return sdf.format(cal.getTime());
    }

    /**
     * 根据年份和季度获取季度的第一天和最后一天
     *
     * @param year    年份
     * @param quarter 季度
     * @return 返回
     */
    public static String[] getFirstAndLastDayInQuarter(int year, int quarter) {
        String[] dateArray = new String[NumberConstants.TWO];
        if (quarter == NumberConstants.ONE) {
            dateArray[0] = getFirstDayOfMonth(year, NumberConstants.ONE) + " 00:00:00";
            dateArray[1] = getLastDayOfMonth(year, NumberConstants.THREE) + " 23:59:59";
            return dateArray;
        }
        if (quarter == NumberConstants.TWO) {
            dateArray[0] = getFirstDayOfMonth(year, NumberConstants.FOUR) + " 00:00:00";
            dateArray[1] = getLastDayOfMonth(year, NumberConstants.SIX) + " 23:59:59";
            return dateArray;
        }
        if (quarter == NumberConstants.THREE) {
            dateArray[0] = getFirstDayOfMonth(year, NumberConstants.SEVEN) + " 00:00:00";
            dateArray[1] = getLastDayOfMonth(year, NumberConstants.NINE) + " 23:59:59";
            return dateArray;
        }
        dateArray[0] = getFirstDayOfMonth(year, NumberConstants.TEN) + " 00:00:00";
        dateArray[1] = getLastDayOfMonth(year, NumberConstants.TWELVE) + " 23:59:59";
        return dateArray;
    }

    /**
     * 根据时间所在的季度第一天和最后一天
     *
     * @param date 时间
     * @return 返回
     */
    public static String[] getFirstAndLastDayInQuarter(Date date) {
        int year = getYear(date);
        int quarter = getQuarter(date);
        if (quarter > NumberConstants.FOUR || quarter < NumberConstants.ONE) {
            return null;
        }
        return getFirstAndLastDayInQuarter(year, quarter);
    }

    /**
     * 获取过去四个季度数组，返回[2021,2],[2021,1],[2020,4],[2020,3] 集合
     * 表示2021年第2季度、2021年第1季度，2020年第4季度，2020年第3季度
     *
     * @param date 日期
     * @return 返回
     */
    public static List<Integer[]> getLastFourQuarter(Date date) {
        List<Integer[]> quarterList = new ArrayList<>();
        int currentYear = getYear(date);
        int currentQuarter = getQuarter(date);
        while (quarterList.size() != NumberConstants.FOUR) {
            // 四个季度终止
            Integer[] array = new Integer[NumberConstants.TWO];
            array[0] = currentYear;
            array[1] = currentQuarter;
            quarterList.add(array);
            currentQuarter--;
            // 假如季度变成0，则年份减一
            if (currentQuarter == NumberConstants.ZERO) {
                currentQuarter = NumberConstants.FOUR;
                currentYear--;
            }
        }
        Collections.reverse(quarterList);
        return quarterList;
    }

    /**
     * 获取过去n个月数组，返回 [2020,8],[2020,9],...,[2021,8] 集合
     * 表示2020年8月、2020年9月，...，2021年8月
     *
     * @param date 日期
     * @return 返回
     */
    public static List<Integer[]> getLastSpecifiedQuantityMonth(Date date, int specifiedQuantity) {
        List<Integer[]> monthList = new ArrayList<>();
        int currentYear = getYear(date);
        int currentMonth = getMonth(date);
        while (monthList.size() != specifiedQuantity) {
            // specifiedQuantity个月终止
            Integer[] array = new Integer[NumberConstants.TWO];
            array[0] = currentYear;
            array[1] = currentMonth;
            monthList.add(array);
            currentMonth--;
            // 假如月份变成0，则年份减一
            if (currentMonth == NumberConstants.ZERO) {
                currentMonth = NumberConstants.TWELVE;
                currentYear--;
            }
        }
        Collections.reverse(monthList);
        return monthList;
    }

    /**
     * timeStamp字符串转日期
     *
     * @param date 日期字符串
     * @return 返回
     */
    public static Date timeStampFormat(String date) {
        date = date.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * UTC转日期
     *
     * @param dateStr utc日期字符串
     * @param pattern 转换格式
     * @return 返回
     */
    public static Date parseByTZUTC(String dateStr, String pattern) {
        return parseByTimeZone(dateStr, pattern, "UTC");
    }

    /**
     * 字符串转日期
     *
     * @param dateStr 日期字符串
     * @param pattern 转换格式
     * @param timeZone 时区
     * @return 返回
     */
    public static Date parseByTimeZone(String dateStr, String pattern, String timeZone) {
        if (StringUtils.isBlank(dateStr) || StringUtils.isBlank(pattern) || StringUtils.isBlank(timeZone)) {
            return null;
        }
        DateFormat df;
        try {
            df = new SimpleDateFormat(pattern);
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            return df.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 考虑港和美 采用GMT-1时区来确定报表日 即T日的报表包含北京时间T日9时至T+1日9时的数据
     */
    public static final ZoneId TIMEZONE_GMT_1 = ZoneId.of("GMT-1");

    /**
     * TIMEZONE_EST
     */
    public static final ZoneId TIMEZONE_EST = ZoneId.of("US/Eastern");

    /**
     * TIMEZONE_GMT8
     */
    public static final ZoneId TIMEZONE_GMT8 = ZoneId.of("GMT+8");

    /**
     * 常用时间转换格式
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 年月日
     */
    public static final String DATE_NO_GAP_FORMAT = "yyyyMMdd";

    /**
     * 年-月-日
     */
    public static final String DATE_GAP_FORMAT = "yyyy-MM-dd";

    /**
     * 时分
     */
    public static final String TIME_HH_MM_FORMAT = "HHmm";

    /**
     * DATE_TIME_FORMAT_MAP
     */
    public static final Map<String, DateTimeFormatter> DATE_TIME_FORMAT_MAP
            = new Hashtable<String, DateTimeFormatter>() {
                {
                    put(TIME_FORMAT, DateTimeFormatter.ofPattern(TIME_FORMAT));
                    put(DATE_NO_GAP_FORMAT, DateTimeFormatter.ofPattern(DATE_NO_GAP_FORMAT));
                    put(DATE_GAP_FORMAT, DateTimeFormatter.ofPattern(DATE_GAP_FORMAT));
                    put(TIME_HH_MM_FORMAT, DateTimeFormatter.ofPattern(TIME_HH_MM_FORMAT));
                }
            };

    /**
     * 获取当前微秒
     *
     * @return long
     */
    public static Long getmicTime() {
        Long cutime = System.currentTimeMillis() * 1000; // 微秒
        Long nanoTime = System.nanoTime(); // 纳秒
        return cutime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
    }

    /**
     * 根据format的格式获取相应的DateTimeFormatter对象
     *
     * @param format 时间转换格式字符串
     * @return
     */
    public static DateTimeFormatter getDateTimeFormatter(String format) {
        if (DATE_TIME_FORMAT_MAP.containsKey(format)) {
            return DATE_TIME_FORMAT_MAP.get(format);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            DATE_TIME_FORMAT_MAP.put(format, formatter);
            return formatter;
        }
    }


    /**
     * 获取当前日期的开始时间
     *
     * @param zoneId 时间偏移量
     * @return
     */
    public static LocalDateTime todayStart(ZoneId zoneId) {
        return startOfDay(0, zoneId);
    }

    /**
     * 获取当前的ZoneDateTime
     *
     * @param zoneId 时区偏移量
     * @return
     */
    public static ZonedDateTime now(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    /**
     * 获取当前日期的开始时间ZonedDateTime
     *
     * @param date   日期
     * @param zoneId 时区偏移量
     * @return
     */
    public static ZonedDateTime localDateToZoneDateTime(LocalDate date, ZoneId zoneId) {
        return date.atStartOfDay(zoneId);
    }

    /**
     * 获取当前日期的开始时间
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime startOfDay(ZonedDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
    }

    /**
     * 获取今天后的指定天数的开始时间
     *
     * @param plusDays 当前多少天后
     * @param zoneId   时区偏移量
     * @return
     */
    public static LocalDateTime startOfDay(int plusDays, ZoneId zoneId) {
        return startOfDay(now(zoneId).plusDays(plusDays));
    }

    /**
     * 获取指定日期的后几个工作日的时间LocalDate
     *
     * @param date 指定日期
     * @param days 工作日数
     * @return
     */
    public static LocalDate plusWeekdays(LocalDate date, int days) {
        if (days == 0) {
            return date;
        }
        if (Math.abs(days) > 50) {
            throw new IllegalArgumentException("days must be less than 50");
        }
        int i = 0;
        int delta = days > 0 ? 1 : -1;
        while (i < Math.abs(days)) {
            date = date.plusDays(delta);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                i += 1;
            }
        }
        return date;
    }

    /**
     * 获取指定日期的后几个工作日的时间ZoneDateTime
     *
     * @param date
     * @param days
     * @return
     */
    public static ZonedDateTime plusWeekdays(ZonedDateTime date, int days) {
        return plusWeekdays(date.toLocalDate(), days).atStartOfDay(date.getZone());
    }

    /**
     * 获取当前月份的第一天的时间ZoneDateTime
     *
     * @param zoneId
     * @return
     */
    public static ZonedDateTime firstDayOfMonth(ZoneId zoneId) {
        return now(zoneId).withDayOfMonth(1);
    }


    /**
     * 将Date转成指定时区的Date
     *
     * @param date
     * @return
     */
    public static Date dateToDate(Date date, ZoneId zoneId) {
        LocalDateTime dt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return toDate(ZonedDateTime.of(dt, zoneId));
    }

    /**
     * 将LocalDate转成Date
     *
     * @param date
     * @return
     */
    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * ZonedDateTime 转换成Date
     *
     * @param dateTime
     * @return
     */
    public static Date toDate(ZonedDateTime dateTime) {
        return Date.from(dateTime.toInstant());
    }

    /**
     * String 转换成 Date
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String date, String format, ZoneId zoneId) throws ParseException {
        DateTimeFormatter formatter = getDateTimeFormatter(format).withZone(zoneId);
        Instant instant = Instant.from(formatter.parse(date));
        return Date.from(instant);
    }

    /**
     * 将Date转成相应的时区的localDate
     *
     * @param date
     * @param zoneId
     * @return
     */
    public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    /**
     * 将Instant转成指定时区偏移量的localDate
     *
     * @param instant
     * @param zoneId
     * @return
     */
    public static LocalDate toLocalDate(Instant instant, ZoneId zoneId) {
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 将Instant转换成指定时区偏移量的localDateTime
     *
     * @param instant
     * @param zoneId
     * @return
     */
    public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId) {
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 将Instant转成系统默认时区偏移量的LocalDateTime
     *
     * @param instant
     * @return
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return toLocalDateTime(instant, ZoneId.systemDefault());
    }

    /**
     * 将ZoneDateTime 转成 指定时区偏移量的LocalDateTime
     *
     * @param zonedDateTime 时间
     * @param zoneId        指定时区偏移量
     * @return
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime, ZoneId zoneId) {
        return zonedDateTime.toInstant().atZone(zoneId).toLocalDateTime();
    }

    /**
     * 将ZoneDateTime 转成 LocalDateTime
     *
     * @param zonedDateTime
     * @return
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * String 转成 ZoneDateTime
     * 需要类似 yyyy-MM-dd HH:mm:ss 需要日期和时间信息完整信息
     *
     * @param date
     * @param format
     * @param zoneId
     * @return
     */
    public static ZonedDateTime stringToZoneDateTime(String date, String format, ZoneId zoneId) {
        DateTimeFormatter formatter = getDateTimeFormatter(format).withZone(zoneId);
        return ZonedDateTime.parse(date, formatter);
    }


    /**
     * 将时间戳long转成ZonedDateTime
     *
     * @param timeStamp
     * @param zoneId
     * @return
     */
    public static ZonedDateTime longToZoneDateTime(long timeStamp, ZoneId zoneId) {
        return ZonedDateTime.from(Instant.ofEpochMilli(timeStamp).atZone(zoneId));
    }

    /**
     * 两个时区的zoneDateTime相互转换
     *
     * @param zonedDateTime 需要转换的如期
     * @param zoneId        转换成的ZoneDateTime的时区偏移量
     * @return
     */
    public static ZonedDateTime zonedDateTimeToZoneDateTime(ZonedDateTime zonedDateTime, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(zonedDateTime.toInstant(), zoneId);
    }

    /**
     * Date 转成 指定时区偏移量的ZoneDateTime
     *
     * @param date
     * @param zoneId
     * @return
     */
    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId);
    }

    /**
     * LocaldateTime 转成 指定时区偏移量的ZonedDateTime
     *
     * @param localDateTime 本地时间
     * @param zoneId        转成ZonedDateTime的时区偏移量
     * @return
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId);
    }

    /**
     * Date装换成String
     *
     * @param date   时间
     * @param format 转化格式
     * @return
     */
    public static String dateToString(Date date, String format, ZoneId zoneId) {
        DateTimeFormatter formatter = getDateTimeFormatter(format).withZone(zoneId);
        return formatter.format(date.toInstant());
    }

    /**
     * ZoneDateTime 转换成 String
     *
     * @param dateTime
     * @param zoneId   localDateTime所属时区
     * @return
     */
    public static String zoneDateTimeToString(ZonedDateTime dateTime, String format, ZoneId zoneId) {
        DateTimeFormatter formatter = getDateTimeFormatter(format).withZone(zoneId);
        return dateTime.format(formatter);
    }

    /**
     * LocalDateTime 转成 String
     *
     * @param localDateTime
     * @param format
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = getDateTimeFormatter(format);
        return localDateTime.format(formatter);
    }

    /**
     * 将ZonedDateTime转成时间戳long
     *
     * @return
     * @parm zonedDateTime
     */
    public static long zoneDateTimeToLong(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 将LocalDateTime转成时间戳long
     *
     * @param localDateTime
     * @param zoneId
     * @return
     */
    public static long toLong(LocalDateTime localDateTime, ZoneId zoneId) {
        return zoneDateTimeToLong(localDateTime.atZone(zoneId));
    }

}
