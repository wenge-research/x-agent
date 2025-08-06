package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.*;
import com.wenge.oauth.entity.OauthUser;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

/**
 * Description: 用户表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-05 14:13:29
 *
 */
public interface UserService extends IService<OauthUser> {

    Result updateUserList(List<OauthUser> oauthUsers);

    Result addUser(OauthUser oauthUser);

    Result getUserList(UserParam user);

    Result updateUser(OauthUser oauthUser);

    Result deleteUser(ListStringParam idList);

    Result updateTenant(UpdateTenantParam param);

    Result bindingRole(UserBindingRoleParam param);

    Result deregisterUser(ListStringParam userIdList);

    Result unlock(ListStringParam userIdList);

    Result getManageUserByTenant(StringParam tenantId);

    Result getManageUser(StringParam param);

    List<OauthUser> getSuperManageUser();

    Result checkStaff(StaffCheckParam param);

    Result fillInPersonal(PersonalFillInParam param);

    Result<OauthUser> getUserDetail(StringParam userId);

    /**
     * 根据角色code绑定角色
     * @param param
     */
    void bindingRoleByCode(UserBindingRoleParam param);

    Result changPw(PwChangParam param);

    List<OauthUser> getUserByTenant();

    Result retrievePassword(RetrievePasswordParam param);

    List<OauthUser> getUserDetailByAccountNameList(List<String> accountNameList);

    void syncWorker(OauthUser oauthUser);

    /**
     * 根据租户id获取用户列表
     * @return
     */
    List<OauthUser> getUserByTenantId(String tenantId);
    Result currentAccount();
    Result updateCurrentAccount(OauthUser oauthUser);
    Result updatePhone(RetrievePasswordParam param);

    /**
     * 根据用户id查询用户列表
     * @param userIds
     * @return
     */
    List<OauthUser> getUsersByUserIds(List<String> userIds);

    Result getAllUserByTenant(StringParam tenantId);

    OauthUser getUserByAccount(String accountName);

    /**
     * 管理端修改密码
     * @param param
     * @return
     */
    Result updatePassword(RetrievePasswordParam param);
}
