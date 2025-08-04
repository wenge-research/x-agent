import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;

//新增用户管理
export function userSave(data: object) {
	return request({
		url: baseUrl + '/user/save',
		method: 'post',
		data,
	});
}
//填写个人资料
export function fillInPersonal(data: object) {
	return request({
		url: baseUrl + '/user/fillInPersonal',
		method: 'post',
		data,
	});
}
//查询部门信息树
export function getDeptStreet(data: object) {
	return request({
		url: baseUrl + '/oauthDept/getDeptStreetByTenant',
		method: 'post',
		data,
	});
}
//获取用户详情
export function getUserDetail(data: object) {
	return request({
		url: baseUrl + '/user/getUserDetail',
		method: 'post',
		data,
	});
}
//详情用户管理
export function userDetail(id: number | string) {
	return request({
		url: baseUrl + `/user/detail/${id}`,
		method: 'get',
	});
}
//删除用户管理
export function userDelete(id: Number | String) {
	return request({
		url: baseUrl + '/user/delete/' + id,
		method: 'DELETE',
	});
}
//清除 token
export function clearToken(id: Number | String) {
	return request({
		url: baseUrl + '/user/clearToken/' + id,
		method: 'put',
	});
}

//敏感词检测
export function userSensitiveCheck(data: object) {
	return request({
		url: baseUrl + `/user/enableSensitiveCheck/${data.id}/${data.enable}`,
		method: 'put',
	});
}
//应用
export function listApp() {
	return request({
		url: baseUrl + '/app/listApp',
		method: 'get',
	});
}
//获取问答列表
export function dialoguePage(data: object) {
	return request({
		url: baseUrl + '/dialogue/page',
		method: 'post',
		data,
	});
}
//导出问答列表
export function dialogueExport(data: object) {
	return request({
		url: baseUrl + '/dialogue/export',
		method: 'post',
		responseType: 'blob',
		data,
	});
}
//赞踩列表
export function feedbackPage(data: object) {
	return request({
		url: baseUrl + '/feedback/page',
		method: 'post',
		data,
	});
}
//导出赞踩列表
export function feedbackExport(data: object) {
	return request({
		url: baseUrl + '/feedback/export',
		method: 'post',
		responseType: 'blob',
		data,
	});
}
//赞踩审核
export function feedbackAudit(data: object) {
	return request({
		url: baseUrl + '/feedback/audit',
		method: 'put',
		params: data,
	});
}

//用户管理列表
export function userPage(data: object) {
	return request({
		url: baseUrl + '/user/page',
		method: 'get',
		params: data,
	});
}
//指令管理列表
export function pagePrompt(data: object) {
	return request({
		url: baseUrl + '/manage/app',
		method: 'get',
		params: data,
	});
}
//查询技能
export function getPromptById(id: string, type: string) {
	return request({
		url: baseUrl + `/manage/app/${id}`,
		method: type,
	});
}
//新增技能
export function addInstrctList(data: object) {
	return request({
		url: baseUrl + '/manage/app',
		method: 'post',
		data,
	});
}
//编辑技能
export function editInstrctList(data: object) {
	return request({
		url: baseUrl + '/manage/app',
		method: 'put',
		data,
	});
}
//指令类型
export function listIndustry() {
	return request({
		url: baseUrl + '/manage/app/category/allList',
		method: 'get',
	});
}
//指令新增、编辑
export function promptSava(data: object) {
	return request({
		url: baseUrl + '/prompt/sava',
		method: 'post',
		data,
	});
}

//指令下架
export function promptDown(id: string) {
	return request({
		url: baseUrl + `/manage/app/down/${id}`,
		method: 'post',
	});
}
//指令上架
export function promptUp(id: string) {
	return request({
		url: baseUrl + `/manage/app/up/${id}`,
		method: 'post',
	});
}

//指令删除
export function promptDelete(id: number | string) {
	return request({
		url: baseUrl + `/prompt/delete/${id}`,
		method: 'delete',
	});
}

//推荐指令
export function listPrompt(data: object) {
	return request({
		url: baseUrl + `/prompt/listPrompt`,
		method: 'get',
		params: data,
	});
}

//申请管理列表
export function consultPageList(data: object) {
	return request({
		url: baseUrl + `/consult/pageList`,
		method: 'get',
		params: data,
	});
}
//所属行业
export function consultListTrade(data: object) {
	return request({
		url: baseUrl + `/consult/listTrade`,
		method: 'get',
		params: data,
	});
}
//申请审核
export function consultAudit(data: object) {
	return request({
		url: baseUrl + `/consult/audit`,
		method: 'put',
		params: data,
	});
}
//申请导出
export function consultExport(data: object) {
	return request({
		url: baseUrl + `/consult/export`,
		method: 'post',
		responseType: 'blob',
		data,
	});
}
//服务调用统计
export function getServerCount(data: object) {
	return request({
		url: baseUrl + `/overview/getServerCount`,
		method: 'post',
		data,
	});
}
//统计总调用次数和失败次数
export function getTotalAmount(data: object) {
	return request({
		url: baseUrl + `/overview/getTotalAmount`,
		method: 'post',
		data,
	});
}
//技能调用情况
export function getSkillDetailCountAndToken(data: object) {
	return request({
		url: baseUrl + `/overview/getSkillDetailCountAndToken`,
		method: 'post',
		data,
	});
}
//数据统计-充值情况
export function rechargeSituation(data: object) {
	return request({
		url: baseUrl + `/overview/rechargeSituation`,
		method: 'post',
		data,
	});
}
//获取用户数量
export function userCount(data: object) {
	return request({
		url: baseUrl + `/user/userCount`,
		method: 'get',
		params: data,
	});
}

//获取技能分类
export function getCategoryList(data: object) {
	return request({
		url: baseUrl + `/manage/app/category`,
		method: 'get',
		params: data,
	});
}
//新增技能分类
export function addCategory(data: object, type: string) {
	return request({
		url: baseUrl + `/manage/app/category`,
		method: type,
		data,
	});
}
//删除技能分类
export function deleteCategory(id: string) {
	return request({
		url: baseUrl + `/manage/app/category/${id}`,
		method: 'delete',
	});
}
//技能分类上架
export function categoryUp(id: string) {
	return request({
		url: baseUrl + `/manage/app/category/up/${id}`,
		method: 'post',
	});
}
//技能分类下架
export function categoryDown(id: string) {
	return request({
		url: baseUrl + `/manage/app/category/down/${id}`,
		method: 'post',
	});
}
//知识库分页列表查询
export function knowledgePage(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/adminPage',
		method: 'post',
		data,
	});
}

//新增知识库
export function createKnowledge(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/createKnowledge',
		method: 'post',
		data,
	});
}
//编辑知识库
export function editKnowledge(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/editKnowledge',
		method: 'post',
		data,
	});
}
//删除知识库
export function deleteKnowledge(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/deleteKnowledge',
		method: 'post',
		data,
	});
}

//根据技能名称模糊匹配技能列表
export function knowledgeFuzzyMatching() {
	return request({
		url: baseUrl + '/knowledge-base/fuzzyMatching',
		method: 'get',
	});
}
//用户角色字典
export function userRoleDict() {
	return request({
		url: baseUrl + '/knowledge-base/userRoleDict',
		method: 'get',
	});
}
