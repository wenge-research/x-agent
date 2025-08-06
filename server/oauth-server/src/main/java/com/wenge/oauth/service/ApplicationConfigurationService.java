package com.wenge.oauth.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.ApplicationConfigurationParam;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * @author: caohaifeng
 * @date: 2024/8/30 11:30
 * @Description: 应用nacos配置service
 * @Version:1.0
 **/
public interface ApplicationConfigurationService extends IService<ApplicationConfiguration> {

    Result addApplicationConfiguration(ApplicationConfiguration param);

    Result<Page<ApplicationConfiguration>>  getApplicationConfigurationList(ApplicationConfigurationParam param);

    Result updateApplicationConfiguration(ApplicationConfiguration applicationConfiguration);

    Result deleteApplicationConfiguration(ApplicationConfigurationParam applicationConfigurationParam);

    List<ApplicationConfiguration> getConfigByAppId(String appId);

    void updateAppConfig(List<ApplicationConfiguration> configurationList, String appId);

    Map<String, List<ApplicationConfiguration>> getConfigByAppIds(List<String> appId);
}