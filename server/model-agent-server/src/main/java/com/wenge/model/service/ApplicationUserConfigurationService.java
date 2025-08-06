package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.ApplicationUserConfiguration;

import java.util.List;

public interface ApplicationUserConfigurationService extends IService<ApplicationUserConfiguration> {


    List<ApplicationUserConfiguration> selectByUserId(Long currentUserId);

    List<ApplicationUserConfiguration> selectByAppId(String applicationId);

    List<ApplicationUserConfiguration> selectByUserIdAndAppId(Long currentUserId, String applicationId);
}
