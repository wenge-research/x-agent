package com.wenge.model.constants;


/**
 * 龙华办事指南常量
 * 注： 请务必以single、multi结尾，value都是在数据库维护
 */
public interface LHGuideConstant {

    // 解析url为单个文件标识
    String GUIDE_SINGLE = "lh_guide_single";

    // 解析url为多个文件的标识
    String GUIDE_MULTI = "lh_guide_multi";

    // 一件事一次办解析url为单个文件的标识
    String GUIDE_ONE_THING_ONE_TIME = "lh_guide_one_thing_one_time_single";

}
