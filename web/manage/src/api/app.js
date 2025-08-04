import request from "@/utils/request";

//更改聊天模板状态
export function updateTemplateStatus(data) {
    return request({
        url: "/dialogTemplate/updateStatus",
        method: "post",
        data,
    });
}

// 应用列表
export function appList(data) {
    return request({
        url: "/applicationInfo/getApplicationInfoList",
        method: "post",
        data,
    });
}

// 大模型列表
export function modelList(data) {
    return request({
        url: "/llmInfo/getLlmInfoList",
        method: "post",
        data,
    });
}
// 微信认证
export function getAuthChannels(data) {
    return request({
        url: "/authChannel/getAuthChannels",
        method: "post",
        data,
    });
}

// 应用列表
export function addApplication(data) {
    return request({
        url: "/applicationInfo/addApplicationInfo",
        method: "post",
        data,
    });
}

// 应用列表
export function deleteApplication(data) {
    return request({
        url: "/applicationInfo/deleteApplicationInfo",
        method: "post",
        data,
    });
}

// 应用列表
export function dialogRecord(data) {
    return request({
        url: "/record/getRecord",
        method: "post",
        data,
    });
}
// 核实对话记录
export function verifyRecord(data) {
    return request({
        url: "/record/verifyRecord",
        method: "post",
        data,
    });
}

// 查询答案审核列表
export function getCheckRecord(data) {
    return request({
        url: "/record/getCheckRecord",
        method: "post",
        data,
    });
}

// 审核对话记录
export function recordCheckRecord(data) {
    return request({
        url: "/record/checkRecord",
        method: "post",
        data,
    });
}

// 溯源列表
export function sourceList(data) {
    return request({
        url: "/dialogue/sourceAnswer",
        method: 'post',
        data
    });
}

// 模板列表
export function templateList(data) {
    return request({
        url: "/dialogTemplate/getDialogTemplateList",
        method: 'post',
        data
    });
}

// 语音列表
export function ttsList(data) {
    return request({
        url: "/voiceComponentInfo/getList",
        method: 'post',
        data
    });
}

// 用户列表
export function userList(data) {
    return request({
        url: "/user/getUserList",
        method: 'post',
        data
    });
}
// 用户列表 - 树状图
export function getDeptStreet(data) {
    return request({
        url: "/oauthDept/getDeptStreet",
        method: 'post',
        data
    });
}
// 部门列表
export function deptTree(data) {
    return request({
        url: "/oauthDept/getDeptStreet",
        method: 'post',
        data
    });
}
// 新增部门信息
export function addOauthDept(data) {
    return request({
        url: "/oauthDept/addOauthDept",
        method: 'post',
        data
    });
}
// 删除部门信息
export function deleteOauthDept(data) {
    return request({
        url: "/oauthDept/deleteOauthDept",
        method: 'post',
        data
    });
}
// 角色列表
export function roleList(data) {
    return request({
        url: "/role/getRoleList",
        method: 'post',
        data
    });
}

// 用户绑定角色
export function bindRole(data) {
    return request({
        url: "/user/bindingRole",
        method: 'post',
        data
    });
}

// 用户绑定角色查询
export function userSearchRole(data) {
    return request({
        url: "/role/getRole",
        method: 'post',
        data
    });
}

// 新增用户
export function addUser(data) {
    return request({
        url: "/user/addUser",
        method: 'post',
        data
    });
}
// 注销用户
export function deregisterUser(data) {
    return request({
        url: "/user/deregisterUser",
        method: 'post',
        data
    });
}

// 解锁用户
export function unlockUser(data) {
    return request({
        url: "/user/unlock",
        method: 'post',
        data
    });
}
// 删除用户
export function deleteUser(data) {
    return request({
        url: "/user/deleteUser",
        method: 'post',
        data
    });
}

// 新增角色
export function addRole(data) {
    return request({
        url: "/role/addRole",
        method: 'post',
        data
    });
}

// 角色菜单列表obj
export function menulist(data) {
    return request({
        url: "/menu/getPermission",
        method: 'post',
        data
    });
}
// 角色菜单列表id
export function menulists(data) {
    return request({
        url: "/menu/getMenuIdList",
        method: 'post',
        data
    });
}
// 菜单列表
export function allmenulist(data) {
    return request({
        url: "/menu/getAllMenuTree",
        method: 'post',
        data
    });
}

// 更改角色状态
export function updateRoleStatus(data) {
    return request({
        url: "/role/updateStatus",
        method: 'post',
        data
    });
}

// 删除角色
export function deleteRole(data) {
    return request({
        url: "/role/deleteRole",
        method: 'post',
        data
    });
}


// 复制应用
export function copyApp(data) {
    return request({
        url: "/applicationInfo/copyApp",
        method: 'post',
        data
    });
}


// 默认应用信息
export function defaultApp(data) {
    return request({
        url: "/applicationInfo/getDefaultApp",
        method: 'post',
        data
    });
}


// 对话体验AI生成
export function getQuestionByAI(data) {
    return request({
        url: "/applicationInfo/getQuestionByAI",
        method: 'post',
        data
    });
}

// 租户列表
export function tenantList(data) {
    return request({
        url: "/oauthTenant/getOauthTenantList",
        method: 'post',
        data
    });
}
// 获取租户详情
export function getOauthTenantInfoByTenantId(data) {
  return request({
      url: "/oauthTenant/getOauthTenantInfoByTenantId",
      method: 'get',
      params: data
  });
}

// 新增租户
export function addTenant(data) {
    return request({
        url: "/oauthTenant/addOauthTenant",
        method: 'post',
        data
    });
}
//查询用户信息
export function findTenant(data){
    return request({
        url:'oauthTenant/getOauthTenantInfoByTenantId',
        method:'get',
        data
    })
}
// 删除租户
export function deleteTenant(data) {
    return request({
        url: "/oauthTenant/deleteOauthTenant",
        method: 'post',
        data
    });
}

// 更新租户状态
export function updateTenantStatus(data) {
    return request({
        url: "/oauthTenant/updateStatus",
        method: 'post',
        data
    });
}

// 更新租户用户
export function updateTenantUser(data) {
    return request({
        url: "/user/updateTenant",
        method: 'post',
        data
    });
}

// 查询管理员用户
export function getmanageUser(data) {
    return request({
        url: "/user/getManageUser",
        method: 'post',
        data
    });
}

// 租户下绑定的用户
export function tenantByUser(data) {
    return request({
        url: "/user/getUserByTenant",
        method: 'post',
        data
    });
}
// 租户下绑定的用户
export function getAllUserByTenant(data) {
    return request({
        url: "/user/getAllUserByTenant",
        method: 'post',
        data
    });
}

// 菜单列表
export function getMenuList(data) {
    return request({
        url: "/menu/getMenuList",
        method: 'post',
        data
    });
}

// 菜单列表
export function addMenu(data) {
    return request({
        url: "/menu/addMenu",
        method: 'post',
        data
    });
}


// 删除菜单
export function deleteMenu(data) {
    return request({
        url: "/menu/deleteMenu",
        method: 'post',
        data
    });
}
// 更新菜单状态
export function updateMenuStatus(data) {
    return request({
        url: "/menu/updateStatus",
        method: 'post',
        data
    });
}
// 更新菜单隐藏状态
export function updateHidden(data) {
    return request({
        url: "/menu/updateHidden",
        method: 'post',
        data
    });
}
// 更新菜单隐藏状态
export function getAllMenuTree(data) {
    return request({
        url: "/menu/getAllMenuTree",
        method: 'post',
        data
    });
}

// 审核工作人员状态
export function checkStaff(data) {
    return request({
        url: "/user/checkStaff",
        method: 'post',
        data
    });
}

// 聊天模板列表
export function getDialogTemplateListApi(data) {
    return request({
        url: "/dialogTemplate/getDialogTemplateList",
        method: 'post',
        data
    });
}

// 新增模板列表
export function addDialogTemplateApi(data) {
    return request({
        url: "/dialogTemplate/addDialogTemplate",
        method: 'post',
        data
    });
}

// 删除模板列表
export function deleteDialogTemplateApi(data) {
    return request({
        url: "/dialogTemplate/deleteDialogTemplate",
        method: 'post',
        data
    });
}


// 添加授权数据
export function addGrantData(data) {
    return request({
        url: "/grantData/addGrantData",
        method: 'post',
        data
    });
}

// 查询授权数据
export function getGrantDataList(data) {
    return request({
        url: "/grantData/getGrantDataList",
        method: 'post',
        data
    });
}

// 操作日志查询
export function apiUmsOperationList(data) {
    return request({
        url: "/umsOperation/umsOperationList",
        method: 'post',
        data
    });
}

// 操作类型
export function apiGetLogTypeList() {
    return request({
        url: "/umsOperation/getLogTypeList",
        method: 'get',
    });
}

// 查询应用插件列表
export function applicationPluginList(data) {
    return request({
        url: "/applicationPlugin/getApplicationPluginList",
        method: 'post',
        data
    });
}

// 查询列表应用插件关联表
export function applicationPluginDataList(data) {
    return request({
        url: "/applicationPluginData/getApplicationPluginDataList",
        method: 'post',
        data
    });
}

// 新增应用插件关联表
export function addApplicationPluginData(data) {
    return request({
        url: "/applicationPluginData/addApplicationPluginData",
        method: 'post',
        data
    });
}

// 改写结果
export function getReviseQuestion(data) {
    return request({
        url: "/dialogue/getReviseQuestion",
        method: 'post',
        data
    });
}


// 查看全流程
export function getAllStepByDialogId(data) {
    return request({
        url: "/dialogue/getStepByDialogId",
        method: 'post',
        data
    });
}


export function getDictItemByType(data) {
    return request({
        url: "/dictItem/getDictItemByType",
        method: "post",
        data,
    });
}
// AI生成图片
export function getAiImage(data) {
    return request({
        url: "/applicationInfo/aiImage",
        method: "post",
        data,
    });
}
// 发布 - 配置微信公众号
export function apiAddAuthChannel(data) {
    return request({
        url: "/authChannel/addAuthChannel",
        method: "post",
        data,
    });
}
// 发布 - 配置微信公众号
export function apiGetAuthChannelByApplicationId(data) {
    return request({
        url: "/authChannel/getAuthChannelByApplicationId",
        method: "get",
        params: data,
    });
}
// 发布 - 历史记录(插件-更新记录)
export function apiGetAppPublishRecordList(data) {
    return request({
        url: "/applicationInfo/getAppPublishRecordList",
        method: "post",
        data,
    });
}
// 发布 - 历史应用版本列表
export function apiGetApplicationVersionInfoList(data) {
    return request({
        url: "/applicationVersionInfo/getApplicationVersionInfoList",
        method: "post",
        data,
    });
}
// 发布 - 历史应用版本说明修改
export function apiUpdateApplicationVersionInfo(data) {
    return request({
        url: "/applicationVersionInfo/updateApplicationVersionInfo",
        method: "post",
        data,
    });
}
// 发布 - 历史应用版本回退
export function apiBackUpdateApplicationInfo(data) {
    return request({
        url: "/applicationVersionInfo/backUpdateApplicationInfo",
        method: "post",
        data,
    });
}
// 发布 - 应用版本删除
export function apiDeleteApplicationVersionInfo(data) {
    return request({
        url: "/applicationVersionInfo/deleteApplicationVersionInfo",
        method: "post",
        data,
    });
}
// 发布 - api调用说明
export function apiGetApplicationMarkdownApiDesc() {
    return request({
        url: "/applicationInfo/getApplicationMarkdownApiDesc",
        method: "get",
    });
}
// 商店 - 标签接口
export function apiGetLabelTypes(data) {
    return request({
        url: "/matterGuideGroup/getLabelTypes",
        method: "get",
        params: data
    });
}
// 上架审核 - 列表
export function apiGetListPage(data) {
    return request({
        url: "/serverPublishAudit/getListPage",
        method: "post",
        data
    });
}
// 上架审核 - 审核详情
export function apiGetDataById(data) {
    return request({
        url: "/serverPublishAudit/getDataById",
        method: "get",
        params: data
    });
}
// 上架审核 - 审核原因
export function apiGetListByPid(data) {
    return request({
        url: "/auditNoPassReasons/getListByPid",
        method: "get",
        params: data
    });
}
// 上架审核 - 审核原因
export function apiUpdateserverPublishAudit(data) {
    return request({
        url: "/serverPublishAudit/update",
        method: "post",
        data
    });
}
// 上架审核 - 上一条/下一条
export function apiServerPublishAuditGetDataById(data) {
    return request({
        url: "/serverPublishAudit/getDataById",
        method: "get",
        params: data
    });
}
// 应用 - 添加工作流/知识库
export function apiAddApplicationKnowledge(data) {
    return request({
        url: "/applicationKnowledge/addApplicationKnowledge",
        method: "post",
        data
    });
}
// 应用 - 移除工作流/知识库
export function apiDeleteApplicationKnowledge(data) {
    return request({
        url: "/applicationKnowledge/deleteApplicationKnowledge",
        method: "post",
        data
    });
}
// 商店 - 详情
export function apiGetStoreDataByApplicationId(data) {
    return request({
        url: "/serverPublishAudit/getStoreDataByApplicationId",
        method: "get",
        params: data
    });
}
// 建议反馈 - 列表
export function apiGetSuggestionFeedbackListPage(data) {
    return request({
        url: "/suggestionFeedback/getListPage",
        method: "post",
        data
    });
}
// 建议反馈 - 导出
export function apiSuggestionFeedbackExport(data) {
    return request({
        url: "/suggestionFeedback/export",
        method: "post",
        data,
        responseType: 'blob',
    });
}
// 应用商店 - 收藏
export function apiEditMyFavorite(data) {
    return request({
        url: "/myFavorite/editMyFavorite",
        method: "post",
        data,    
    });
}
// 应用商店 - 查询收藏
export function apiGetMyFavoriteByApplicationId(data) {
    return request({
        url: "/myFavorite/getMyFavoriteByApplicationId",
        method: "get",
        params: data,    
    });
}

// 应用导出
export function apiApplicationInfoExporte(data) {
    return request({
        url: "/applicationInfo/export",
        method: "post",
        data,
        responseType: 'blob',
    });
}

// 应用导入
export function apiApplicationInfoImportApp(data) {
    return request({
        url: "/applicationInfo/importApp",
        method: "post",
        data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

//分页查询语音组件配置信息
export function pageQueryVoice(data) {
    return request({
        url: "/voiceComponentInfoConfig/getConfigByPage",
        method: "post",
        data,
    });
}

//更新语音组件配置信息
export function updateVoiceInformation(data) {
    return request({
        url: "/voiceComponentInfoConfig/updateConfig",
        method: "post",
        data,
    });
}

//更新语音识别状态
export function updateVoiceRecognitionStatus(data) {
    return request({
        url: "/voiceComponentInfo/updateInfo",
        method: "post",
        data,
    });
}


//查询语音详情
export function voiceInfoDetail(id) {
    return request({
        url: `/voiceComponentInfo/getDetail/${id}`,
        method: 'get',
    });
}

export function textToVoice(params) {
  return request({
    url: "/tts/textToVoice",
    method: "post",
    data: params,
  });
}

// 预置

// 插件预置
export function pluginPreset(data){
    return request({
        url:"plugin/api/setPreset",
        method:"POST",
        data
    })
}

// 提示词预置
export function promptPreset(data){
    return request({
        url:"promptConfig/setPreset",
        method:"POST",
        data
    })
}

// 敏感词预置
export function interceptWordHousePreset(data){
    return request({
        url:"interceptWordHouse/setPreset",
        method:"POST",
        data
    })
}

// 大模型预置
export function llmInfoPreset(data){
    return request({
        url:"llmInfo/setPreset",
        method:"POST",
        data
    })
}

// 知识库预置
export function knowledgeInfoPreset(data){
    return request({
        url:"knowledgeInfo/setPreset",
        method:"POST",
        data
    })
}

// 工作流预置
export function workflowPreset(data){
    return request({
        url:"workflow/setPreset",
        method:"POST",
        data
    })
}

// MCP预置
export function mcpServePreset(data){
    return request({
        url:"mcpServer/setPreset",
        method:"POST",
        data
    })
}
//更新语音识别状态
export function updateApplicationPublishAppStoreState(data) {
    return request({
        url: "/applicationInfo/updateApplicationPublishAppStoreState",
        method: "post",
        data,
    });
}
// 审核对话记录
export function bindKnn(data) {
    return request({
        url: "/record/bindKnn",
        method: "post",
        data,
    });
}

// 审核对话记录
export function getBindKnn(data) {
    return request({
        url: "/record/getBindKnn",
        method: "post",
        data,
    });
}
