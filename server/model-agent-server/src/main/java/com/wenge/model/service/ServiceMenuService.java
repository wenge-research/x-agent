package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ServiceMenuPageParam;
import com.wenge.model.entity.ServiceMenu;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 服务菜单服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-05 11:34:24
 *
 */
public interface ServiceMenuService extends IService<ServiceMenu> {

    Result addServiceMenu(ServiceMenu serviceMenu);

    Result getServiceMenuList(ServiceMenuPageParam param);

    Result updateServiceMenu(ServiceMenu serviceMenu);

    Result deleteServiceMenu(List<String> idList);

    Result<List<String>> getServiceTypeList(ServiceMenuPageParam param);

    /**
     * 根据应用id获取服务菜单
     * @param appId
     * @return
     */
    List<ServiceMenu> getServiceByAppId(String appId);

    /**
     * 根据应用id获取办事指南
     *
     * @param appId
     * @param type
     * @return
     */
    List<ServiceMenu> getGuideByAppId(String appId, String type);
}