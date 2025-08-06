package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.OauthLoginLog;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 15:48:49
 *
 */
public interface OauthLoginLogService extends IService<OauthLoginLog> {

    Result addOauthLoginLog(OauthLoginLog oauthLoginLog);

    Result getOauthLoginLogList(OauthLoginLog oauthLoginLog);

    Result updateOauthLoginLog(OauthLoginLog oauthLoginLog);

    Result deleteOauthLoginLog(List<String> idList);

    Object analyzePortalToken(String portaltoken);

}