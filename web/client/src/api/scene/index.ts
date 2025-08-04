import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;


// 获取表单数据-新场景
export function apiGetMatterGuideForm(data: object) {
	return request({
		url: baseUrl + '/matterGuideFiled/getMatterGuideForm',
		method: 'post',
		data,
	});
}
