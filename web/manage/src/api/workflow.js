import request from "@/utils/request";
// 应用列表
export function appList(data) {
    return request({
        url: "/workflow/list",
        method: "post",
        data
    });
}
// 查询单个详情
export function queryWorkflowDetail(data) {
    return request({
        url: "/workflow/queryDetail",
        method: "post",
        data
    });
}

// 工作流新增
export function saveWorkflow(data) {
  data.nodes.forEach(item => {
      item.settings = item.settings ? JSON.stringify(item.settings) : item.settings;
  })
  return request({
      url: "/component/draft",
      method: "post",
      data,
  });
}
export function saveWorkflowComponent(data) {
  return request({
      url: "/workflow/save",
      method: "post",
      data,
  });
}
export function updateWorkflowComponent(data) {
  return request({
      url: "/workflow/update",
      method: "post",
      data,
  });
}
// // 工作流新增
// export function saveWorkflowComponent(data) {
//   return request({
//       url: "/workflow/save",
//       method: "post",
//       data,
//   });
// }
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

// 工作流删除
export function deleteComponent(data) {
    return request({
        url: "/component/delete",
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
export function copyWorkflowApp(data) {
    return request({
        url: "/workflow/copy",
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

// 租户列表
export function tenantList(data) {
    return request({
        url: "/oauthTenant/getOauthTenantList",
        method: 'post',
        data
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
// 新增组件
export function saveComponent(data) {
    return request({
        url: "/plugin/api/create",
        method: 'post',
        data
    });
}
// api验证
export function apiValidate(data) {
    return request({
        url: "/plugin/api/validate",
        method: 'post',
        data
    });
}
// 组件列表
export function pluginList(data) {
    return request({
        url: "/plugin/api/query",
        method: 'post',
        data
    });
}
// 组件更新
export function updateComponent(data) {
    data.nodes.forEach(item => {
        item.settings = item.settings ? JSON.stringify(item.settings) : item.settings;
    })
    return request({
        url: "/plugin/api/update",
        method: 'post',
        data
    });
}
//更新代码插件
export function updateCodePlugin(data) {
    return request({
        url: "/plugin/code/update",
        method: 'post',
        data
    });
}
// 组件详情
export function pluginQueryDetail(data) {
  return request({
      url: "/plugin/api/queryDetail",
      method: 'post',
      data
  });
}
// 组件运行
export function runComponent(data) {
    return request({
        url: "/workflow/dialogueRun",
        method: 'post',
        data
    });
}


// 模型算法-更新插件方法
export function updateModelPluginApi(data) {
    return request({
        url: "/modelPluginApi/updateModelPluginApi",
        method: 'post',
        data
    });
}

// 模型算法-更新插件方法
export function addModelPluginApi(data) {
    return request({
        url: "/modelPluginApi/addModelPluginApi",
        method: 'post',
        data
    });
}
// 插件-代码
export function addCodePlugin(data) {
    return request({
        url: "/plugin/code/create",
        method: 'post',
        data
    });
}


// 模型算法-根据Id查询插件模型详情
export function getModelPluginApiById(data) {
    return request({
        url: `/modelPluginApi/getModelPluginApiById?id=${data.id}`,
        method: 'get',
    });
}

// 应用中文本生成模式获取内容
export function findByPluginId(data) {
    return request({
        url: `modelPluginApi/findByPluginId?pluginId=${data.id}`,
        method: 'get',
    });
}

// 模型算法-分页列表
export function getModelPluginApiList(data) {
    return request({
        url: "/modelPluginApi/getModelPluginApiList",
        method: 'post',
        data
    });
}

// 模型算法-删除插件方法
export function deleteModelPluginApi(data) {
    return request({
        url: "/modelPluginApi/deleteModelPluginApi",
        method: 'post',
        data
    });
}

// 添加秘钥
export function addModelPluginApiAuthUser(data) {
    return request({
        url: "/ModelPluginApiAuthUserAuthUser/addModelPluginApiAuthUser",
        method: 'post',
        data
    });
}

// 根据id查询用户详情
export function getModelPluginApiAuthUserById(data) {
    return request({
        url: `/ModelPluginApiAuthUserAuthUser/getModelPluginApiAuthUserById?id=${data.id}`,
        method: 'get',
    });
}

// 编辑用户
export function updateModelPluginApiAuthUser(data) {
    return request({
        url: "/ModelPluginApiAuthUserAuthUser/updateModelPluginApiAuthUser",
        method: 'post',
        data
    });
}

// 删除用户
export function deleteModelPluginApiAuthUser(data) {
    return request({
        url: "/ModelPluginApiAuthUserAuthUser/deleteModelPluginApiAuthUser",
        method: 'post',
        data
    });
}

// 用户列表
export function getModelPluginApiAuthUserList(data) {
    return request({
        url: "/ModelPluginApiAuthUserAuthUser/getModelPluginApiAuthUserList",
        method: 'post',
        data
    });
}

// 单节点运行
export function apiRunNode(data) {
    return request({
        url: "/component/runNode",
        method: 'post',
        data
    });
}

// 查询提示词配置列表
export function promptConfigGetPromptConfigList(data) {
    return request({
        url: "/promptConfig/getPromptConfigList",
        method: 'post',
        data
    });
}

// 新增提示词
export function promptConfigAddPromptConfig(data) {
    return request({
        url: "/promptConfig/addPromptConfig",
        method: 'post',
        data
    });
}

// 删除提示词
export function promptConfigDeletePromptConfig(data) {
    return request({
        url: "/promptConfig/deletePromptConfig",
        method: 'post',
        data
    });
}

//节点删除
export function deleteNode(id) {
    return request({
        url:  `/custom/node/delete?id=${id}`,
        method: 'post',
    });
}

//节点列表
export function nodeList(type) {
    return request({
        url: `/custom/node/list?type=${type}`,
        method: 'get',
    });
}

//新增节点
export function addNode(data) {
    return request({
        url: '/custom/node/save',
        method: 'post',
        data
    });
}

//更新节点
export function updateNode(data) {
    return request({
        url: '/custom/node/update',
        method: 'post',
        data
    });
}

//查询节点
export function queryNode(id) {
    return request({
        url: `/custom/node/getById?id=${id}`,
        method: 'get',
    });
}
//上下文溯源
export function recordGetRecord(data) {
	return request({
		url:"/record/getRecord",
		method: 'post',
		data
	});
}
export function closeDialogueConn(data){
    return request({
		url:'/yayi/closeSseEmitter',
		method: 'post',
		data
	});
}
// export function updateNameConversation(id){
// 	return request({
// 		url: `/conversation/updateName/${id}`,
// 		method: 'put',
// 	})
// }
export function apiGetText(data){
    return request({
        url: `/file/analysisFile`,
        method:'POST',
        data,
    })
    }