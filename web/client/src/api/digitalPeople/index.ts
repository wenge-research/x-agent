import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;

// 获取数字人token
export function apiGetAuthTokenBySubscriptionNew() {
	return request({
		url: baseUrl + '/virtualHumanComponentInfo/getAuthTokenBySubscriptionNew',
		method: 'get'
	});
}
