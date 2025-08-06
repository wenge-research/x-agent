package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class XinzhiWeather implements Serializable {

    private static final long serialVersionUID = 3648892483892025095L;

    /**
     * 地点信息
     */
    private Location location;

    /**
     * 天气预报信息
     */
    private List<Day> daily;

    /**
     * 实况天气信息
     */
    private Now now;

    /**
     * 更新时间
     */
    private String last_update;

    @Data
    public static class Location implements Serializable {

        private static final long serialVersionUID = -8969796612433154352L;

        /**
         * id
         */
        private String id;

        /**
         * 地点
         */
        private String name;

        /**
         * 国家
         */
        private String country;

        /**
         * 地区层级
         */
        private String path;

        /**
         * 时区
         */
        private String timezone;

        /**
         * 时区偏移量
         */
        private String timezoneOffset;
    }

    @Data
    public static class Day implements Serializable {

        private static final long serialVersionUID = -6010372617370812016L;

        /**
         * 日期（该城市的本地时间）。
         */
        private String date;

        /**
         * 白天天气现象的文字描述。
         */
        private String text_day;

        /**
         * 白天天气现象的代码。
         */
        private String code_day;

        /**
         * 晚间天气现象的文字描述。
         */
        private String text_night;

        /**
         * 晚间天气现象的代码。
         */
        private String code_night;

        /**
         * 当天的最高温度。
         */
        private String high;

        /**
         * 当天的最低温度。
         */
        private String low;

        /**
         * 降水概率，范围0~1，单位百分比（目前仅支持国外城市）。
         */
        private String precip;

        /**
         * 风向的文字描述。
         */
        private String wind_direction;

        /**
         * 风向的角度，范围0~360。
         */
        private String wind_direction_degree;

        /**
         * 风速，单位km/h（当unit=c时）、mph（当unit=f时）。
         */
        private String wind_speed;

        /**
         * 风力等级。
         */
        private String wind_scale;

        /**
         * 降水量，单位mm。
         */
        private String rainfall;

        /**
         * 相对湿度，0~100，单位为百分比。
         */
        private String humidity;
    }


    @Data
    public static class Now implements Serializable {

        private static final long serialVersionUID = -6010372617370812016L;

        /**
         * 白天天气现象的文字描述。
         */
        private String text;

        /**
         * 白天天气现象的代码。
         */
        private String code;

        /**
         * 温度
         */
        private String temperature;

        /**
         * 体感温度。
         */
        private String feels_like;

        /**
         * 大气压强，单位mb。
         */
        private String pressure;

        /**
         * 相对湿度，0~100，单位为百分比。
         */
        private String humidity;

        /**
         * 能见度，单位km。
         */
        private String visibility;

        /**
         * 风向的文字描述。
         */
        private String wind_direction;

        /**
         * 风向的角度，范围0~360。
         */
        private String wind_direction_degree;

        /**
         * 风速，单位km/h（当unit=c时）、mph（当unit=f时）。
         */
        private String wind_speed;

        /**
         * 风力等级。
         */
        private String wind_scale;

        /**
         * 降水概率，范围0~1，单位百分比（目前仅支持国外城市）。
         */
        private String precip;
    }
}
