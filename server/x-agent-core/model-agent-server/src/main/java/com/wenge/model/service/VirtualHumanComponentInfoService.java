package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.VirtualHumanComponentInfoPageParam;
import com.wenge.model.dto.param.VirtualHumanManageParam;
import com.wenge.model.entity.VirtualHumanComponentInfo;
import com.wg.appframe.core.bean.Result;

public interface VirtualHumanComponentInfoService extends IService<VirtualHumanComponentInfo> {

    /**
     * 获取分页列表
     *
     * @param param
     * @return
     */
    Result getInfoList(VirtualHumanComponentInfoPageParam param);

    /**
     * 添加信息
     *
     * @param param
     * @return
     */
    Result addInfo(VirtualHumanComponentInfo param);

    /**
     * 删除信息
     *
     * @param componentId
     * @return
     */
    Result delInfo(String componentId);

    /**
     * 更新信息
     *
     * @param param
     * @return
     */
    Result updateInfo(VirtualHumanComponentInfo param);

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    Result getInfo(String id);


    /**
     * @author: caohaifeng
     * @date: 2024/8/8 17:30
     * @Description: 获取所有订阅的企业subscriptionList
     * @Version:1.0
     **/
    Result getSubscriptionList();

    /**
     * @author: caohaifeng
     * @date: 2024/8/8 17:30
     * @Description: 生成小冰token
     * @Version:1.0
     **/
    Result getAuthTokenBySubscription(String subscription);

    /**
     * @description: 销毁小冰token
     * @author: caohaifeng
     * @date: 2024/8/9 11:36
     **/
    Result destroyAuthTokenBySubscription(VirtualHumanManageParam virtualHumanManageParam);


    /**
     * @description: 获取可用的数字人列表
     * @author: caohaifeng
     * @date: 2024/10/18 10:36
     **/
    Result getAvailableHumanList();
}