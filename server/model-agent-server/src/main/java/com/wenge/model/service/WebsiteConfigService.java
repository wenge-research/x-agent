package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.WebsiteConfig;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 网址置表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 15:33:14
 *
 */
public interface WebsiteConfigService extends IService<WebsiteConfig> {

    Result addWebsiteConfig(WebsiteConfig websiteConfig);

    Result getWebsiteConfigList(WebsiteConfig websiteConfig);

    Result updateWebsiteConfig(WebsiteConfig websiteConfig);

    Result deleteWebsiteConfig(List<String> idList);

    List<WebsiteConfig> getWebConfList(WebsiteConfig param);
}