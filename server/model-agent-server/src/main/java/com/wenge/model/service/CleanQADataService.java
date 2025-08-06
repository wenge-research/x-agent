package com.wenge.model.service;

import com.wg.appframe.core.bean.Result;

public interface CleanQADataService {


    /**
     * @description: 使用对话日志中的userName字段补全userId字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    Result useinguserNameToUserId();


    /**
     * @description: 使用对话日志中的feedbackUserId字段补全deptId相关字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    Result useingFeedbackUserIdToDeptId();



    /**
     * @description: 补全QA问题中的userId deptId字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    Result useingQAuserIdDeptId();

}