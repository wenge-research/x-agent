package com.wenge.model.utils;


import cn.hutool.core.date.LocalDateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {
    public final static String PATTERN_1 = "yyyy-MM-dd HH:mm:ss";
    public final static String PATTERN_2 = "yyyy-MM-dd";
    public final static String PATTERN_3 = "yyyyMMddHHmmss";
    public final static String PATTERN_4 = "yyyy/M/d H:m:s";
    public final static String PATTERN_5 = "yyyy-MM";
    public final static String PATTERN_6 = "yyyy-MM-dd'T'HH:mm:ss";
    public final static String PATTERN_7 = "yyyy";
    public final static String PATTERN_8 = "MM";
    public final static String PATTERN_9 = "yyyy年MM月dd日";
    public final static String PATTERN_10 = "HH:mm:ss";

//    public static void main(String[] args) {
//        /*System.out.println(subTwoTime("2022-07-02"));*/
//        //System.out.println(format(DateUtil.parse("2023-02-01T03:01:00.000+0000", DateUtil.PATTERN_1),DateUtil.PATTERN_1));
//        /*System.out.println(format(startOfDate(new Date()), DateUtil.PATTERN_1));
//        System.out.println(format(endOfDate(new Date()), DateUtil.PATTERN_1));*/
//        System.out.println(format(parse("2023-06-08 00:12:12",DateUtil.PATTERN_1),DateUtil.PATTERN_7));
//        System.out.println(format(parse("2023-06-08 00:12:12",DateUtil.PATTERN_1),DateUtil.PATTERN_8));
//
//    }

    public static String format(Date date, String pattern) {
        if (date == null || pattern == "" || pattern == null) {
            return null;
        }
        DateFormat df = null;
        try {
            df = new SimpleDateFormat(pattern);
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date parse(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr) || StringUtils.isBlank(pattern)) {
            return null;
        }
        DateFormat df;
        try {
            df = new SimpleDateFormat(pattern);
            return df.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseByPattern1(String dateStr) {
        return parse(dateStr, PATTERN_1);
    }

    public static Date parseByPattern2(String dateStr) {
        return parse(dateStr, PATTERN_2);
    }

    public static Date parseByPattern5(String dateStr) {
        return parse(dateStr, PATTERN_5);
    }

    public static String formatByPattern2(Date date) {
        return format(date, PATTERN_2);
    }

    public static Date parseByPattern4(String dateStr) {
        return parse(dateStr, PATTERN_4);
    }

    public static String formatByPattern1(Date date) {
        return format(date, PATTERN_1);
    }

    public static String formatByPattern3(Date date) {
        return format(date, PATTERN_3);
    }

    public static String formatByPattern5(Date date) {
        return format(date, PATTERN_5);
    }

    public static Date adjustDate(Date date, long mills) {
        long time = date.getTime();
        Date newDate = new Date();
        newDate.setTime(time + mills);
        return newDate;
    }

    public static Date curDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date preDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
    }


    public static Date endOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return calendar.getTime();
    }

    public static Date startOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        return calendar.getTime();
    }

    public static Date startOfNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
    }

    public static Date startTimeOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        return calendar.getTime();
    }

    public static Date startDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }


    public static Date startDayOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        return calendar.getTime();
    }


    public static Date endDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
    }

    public static Date endDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
    }


    public static Date endTimeOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
    }

    public static int compareByPattern1(String a, String b) {
        return compare(parseByPattern1(a), parseByPattern1(b));
    }

    public static int compare(Date a, Date b) {
        if (a == null || b == null) {
            return 0;
        }
        return a.compareTo(b);
    }

    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }


    /**
     * 获取昨天开始时间
     * @return
     */
    public static Long getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTimeInMillis();
    }

    /**
     * 获取昨天的结束时间
     * @return
     */
    public static Long getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTimeInMillis();
    }

    public static LocalDate dateConvertLocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    public static LocalDateTime dateConvertLocalDateTime(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static String localDateTimeConvertString(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_1);
        return time.format(formatter);
    }

    public static LocalDateTime stringConvertLocalDateTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_1);
        return LocalDateTime.parse(time, formatter);
    }

    public static Date localDateConvertDate(LocalDate date){

        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.atStartOfDay().atZone(zone).toInstant();
       return Date.from(instant);

    }

    /**
     * 方法描述:指定时间距现在有几个月
     * @param:  * @param
     * @return: Date
     * @author: LWQ
     * @date: 2023/3/7
     */
    public static int subTwoTime(String thisTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_2);
        try {
            Date oldDate = sdf.parse(thisTime);
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.setTime(oldDate);
            int oldYear = calendar.get(Calendar.YEAR);
            int oldMonth = calendar.get(Calendar.MONTH) + 1;
            Date currentDate = new Date();
            calendar.clear();
            calendar.setTime(currentDate);
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            return Math.abs((currentYear - oldYear) * 12 + (currentMonth - oldMonth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    private static int differentDays(Date date1,Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        //同一年
        if(year1 != year2) {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++) {
                //闰年
                if(i % 4 == 0 && i % 100!=0 || i % 400==0) {
                    timeDistance += 366;
                }
                //不是闰年
                else {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        } else {// 不同年
            return day2-day1;
        }
    }

    public static String getCurrentTime() {
        return LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PATTERN_1);
    }

    public static String getCurrentDate() {
        return LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PATTERN_9);
    }

    public static String getTime() {
        return LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PATTERN_10);
    }

    public static Long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * @author: caohaifeng
     * @date: 2024/8/12 15:44
     * @Description: 上个月的第一天和最后一天
     * @Version:1.0
     **/
    public static String[] startDayOfLastMonth(LocalDate date) {
        String[] strings = new String[2];
        LocalDate lastMonthFirstDay1 = date.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        strings[0] = lastMonthFirstDay1 + " 00:00:00";
        LocalDate lastMonthFirstDa2 = date.with(TemporalAdjusters.lastDayOfMonth()).minusMonths(1);
        strings[1] = lastMonthFirstDa2 + " 23:59:59";
        return strings;
    }

    /**
     * @author: caohaifeng
     * @date: 2024/8/12 15:44
     * @Description: 本月的第一天和最后一天
     * @Version:1.0
     **/
    public static String[] startDayOfThisMonth(LocalDate date) {
        String[] strings = new String[2];
        LocalDate lastMonthFirstDay1 = date.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(0);
        strings[0] = lastMonthFirstDay1 + " 00:00:00";
        LocalDate lastMonthFirstDa2 = date.with(TemporalAdjusters.lastDayOfMonth()).minusMonths(0);
        strings[1] = lastMonthFirstDa2 + " 23:59:59";
        return strings;
    }

    /**
     * @author: caohaifeng
     * @date: 2024/8/12 16:08
     * @Description: 获今日的开始时间和结束时间
     * @Version:1.0
     **/
    public static String[] startDayOfThisDay(LocalDate date) {
        String[] strings = new String[2];
        strings[0] = date + " 00:00:00";
        strings[1] = date + " 23:59:59";
        return strings;
    }

    /**
     * @description: 当前传入时间前多少天 或者后多少天
     * @author: caohaifeng
     * @date: 2024/8/13 10:58
     **/
    public static String[] startDayOfAddDaySize(LocalDate localDate,long day) {
        String[] strings = new String[2];
        LocalDate resLocalDate = null;
        if (day >= 0) { //后多少天
            // 获取当前日期的后3天
            resLocalDate = localDate.plusDays(day);
        }else {
            // 获取当前日期的前5天
            resLocalDate = localDate.minusDays(-day);
        }
        strings[0] = resLocalDate + " 23:59:59";
        strings[1] = resLocalDate + " 00:00:00";
        return strings;
    }

    /**
     * @description: 计算两个时间的天数差值
     * @author: caohaifeng
     * @date: 2024/8/13 11:26
     **/
    public static long differentDays(String startDate,String endDate) {
        final String[] startStr = startDate.split(" ")[0].split("-");
        final String[] endStr = endDate.split(" ")[0].split("-");
        // 创建两个LocalDate实例
        LocalDate startDate2 = LocalDate.of(Integer.valueOf(startStr[0]), Integer.valueOf(startStr[1]),Integer.valueOf(startStr[2])); // 示例开始日期
        LocalDate endDate2 = LocalDate.of(Integer.valueOf(endStr[0]), Integer.valueOf(endStr[1]),Integer.valueOf(endStr[2])); // 示例结束日期
        // 使用ChronoUnit.DAYS.between()方法计算两个日期之间的天数差
        long daysBetween = ChronoUnit.DAYS.between(startDate2, endDate2);
        System.out.println(startDate + "--" + endDate + "--两个日期之间的天数差是：" + daysBetween + " 天");
        return daysBetween;
    }




    public static void main(String[] args) {
//        final String[] strings = startDayOfThisMonth(LocalDate.now());
//        for (int i = 0; i < strings.length; i++) {
//            System.out.println(strings[i]);
//        }

//        long gapDays = (300 / 10) + 1;
//        System.out.println(gapDays);
    }

    /**
     * 获取当前日期，中文版
     * @return
     */
    public static String getCurrentDateCn() {
        return LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PATTERN_9);
    }

    /**
     * 获取当前星期几，中文版
     */
    public static String getCurrentWeekCn() {
        LocalDateTime now = LocalDateTimeUtil.now();
        // 获取当前星期几，比如“星期六”
        return now.format(DateTimeFormatter.ofPattern("EEEE", Locale.CHINA));
    }

    /**
     * 获取当前日期，用于prompt
     * @return
     */
    public static String getDateForPrompt() {
        String dayOfWeek = DateUtil.getCurrentWeekCn();
        String time = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy年MM月dd日 HH:mm:ss");
        return time + " " + dayOfWeek;
    }

}
