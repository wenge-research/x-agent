package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.ApplicationUser;

/**
 * Description: 应用用户关联表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-12 13:41:05
 *
 */
public interface ApplicationUserService extends IService<ApplicationUser> {

    void addApplicationUser(ApplicationUser applicationUser);

}