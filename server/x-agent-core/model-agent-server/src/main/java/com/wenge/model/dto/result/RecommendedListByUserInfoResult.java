package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("APP智能问答根据关键词推荐列表响应参数")
public class RecommendedListByUserInfoResult {
    private String code;
    private String msg;

    private Detail data;

    @Data
    public static class Detail {
        /**
         * 主键
         */
        private String id;

        /**
         * 用户id
         */
        private String userId;

        /**
         * 性别 男/女
         */
        private String sex;

        /**
         * 年龄
         */
        private String age;

        /**
         * 学历
         */
        private String educationalBackground;

        /**
         * 专业技能
         */
        private String professionalSkill;

        /**
         * 已注册岗位
         */
        private String signUpPositionName;

        /**
         * 已注册岗位标签
         */
        private String signUpPositionTag;

        /**
         * 期望行业
         */
        private String desiredIndustry;

        /**
         * 可就业地点
         */
        private String employablePlace;
    }


}
