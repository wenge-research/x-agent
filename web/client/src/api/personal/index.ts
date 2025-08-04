import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;


//认证
export function authorizationApi(data: object) {
	return request({
		url: baseUrl + '/wx/jsapi/authorization/url',
		method: 'get',
		params: data,
	});
}
//获取用户名
export function userInfo(data: object) {
	return request({
		url: baseUrl + '/login',
		method: 'post',
		params: data,
	});
}
//生成收款二维码
export function preCreat(data: object) {
	return request({
		url: baseUrl + '/pay/preCreat',
		method: 'get',
		params: data,
	});
}
//查询支付状态
export function isPayComplete(data: object) {
	return request({
		url: baseUrl + `/pay/isPayComplete/${data.payType}/${data.tradeNo}`,
		method: 'get',
	});
}
//查询充值明细
export function pagePayRecord(data: object) {
	return request({
		url: baseUrl + `/pay/pagePayRecord`,
		method: 'post',
		data
	});
}
//用户更新资料-电话、Emil、名称
export function editUser(data: object) {
	return request({
		url: baseUrl + '/user/editUser',
		method: 'post',
		data,
	});
}

//绑定企微
export function bindWxUser(data: object) {
	return request({
		url: baseUrl + '/user/bindWxUser',
		method: 'post',
		data,
	});
}

//时间范围内统计某个技能类别下的某个技能的调用次数和token
export function getSkillDetailCountAndToken(data: object) {
	return request({
		url: baseUrl + '/overview/getSkillDetailCountAndToken',
		method: 'post',
		data,
	});
}
//某个技能类别下的某个技能的调用次数和token
export function getSkillCategoryCount() {
	return request({
		url: baseUrl + '/app/listAppCategory',
		method: 'get',
	});
}
//时间范围内统计某个技能类别下的某个技能的调用次数和token
export function getSkillCategoryCountTime(data: object) {
	return request({
		url: baseUrl + '/overview/v3/getSkillCategoryCount',
		method: 'post',
		data,
	});
}
//时间范围内统计某个技能类别下的某个技能的调用次数和token
export function getSkillCount(data: object) {
	return request({
		url: baseUrl + '/overview/getSkillCount',
		method: 'post',
		data,
	});
}
//获取余额以及充值情况
export function getPersonagePrice(isFirstPay: boolean) {
	return request({
		url: baseUrl + `/overview/getPersonagePrice/${isFirstPay}`,
		method: 'get',
	});
}
//最近消费
export function recentConsumptionApi(data: object) {
	return request({
		url: baseUrl + '/overview/recentConsumption',
		method: 'post',
		data
	});
}

//消费明细
export function pageConsume(data: object) {
	return request({
		url: baseUrl + '/consume/pageConsume',
		method: 'post',
		data
	});
}

//余额警告
export function notice() {
	return request({
		url: baseUrl + '/overview/notice',
		method: 'get',
	});
}

//技能类别列表
export function listAppCategory() {
	return request({
		url: baseUrl + '/app/listAppCategory',
		method: 'get',
	});
}
//技能类别列表
export function payExport(data: object) {
	return request({
		url: baseUrl + '/pay/export',
		method: 'post',
		responseType:'blob',
		data
	});
}
//支付类型列表
export function listPayType() {
	return request({
		url: baseUrl + '/pay/listPayType',
		method: 'get',
	});
}
//支付类型列表
export function listPayStatus() {
	return request({
		url: baseUrl + '/pay/listPayStatus',
		method: 'get',
	});
}
//用户类型列表
export function listPayUserType() {
	return request({
		url: baseUrl + '/pay/listPayUserType',
		method: 'get',
	});
}
//服务收费策略
export function serviceChargePolicy() {
	return request({
		url: baseUrl + '/auth/serviceChargePolicy',
		method: 'get',
	});
}

//更新余额警告状态
export function updateStatusByNoticeId(noticeId: string) {
	return request({
		url: baseUrl + `/overview/updateStatusByNoticeId/${noticeId}`,
		method: 'get',
	});
}
